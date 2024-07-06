package it.unicam.pentago.strategies;

import it.unicam.pentago.models.AdvancedPentagoStrategy;
import it.unicam.pentago.models.PentagoAction;
import it.unicam.pentago.models.PentagoGameState;
import com.lostrucos.jabtbg.core.*;

import java.util.*;
import java.util.stream.Collectors;

public class StrategicPentagoUtility implements UtilityStrategy<PentagoGameState, PentagoAction> {
    private List<PentagoStrategy> strategies;
    private AdvancedPentagoStrategy baseStrategy;

    public StrategicPentagoUtility() {
        strategies = Arrays.asList(
                new MonicasFive(),
                new MiddleFive(),
                new StraightFive(),
                new TriplePowerPlay()
        );
        baseStrategy = new AdvancedPentagoStrategy();
    }

    @Override
    public double calculateUtility(PentagoGameState state, int playerIndex) {
        // Check for immediate win or loss
        int winner = state.checkForWinner();
        if (winner == playerIndex) return 1.0;
        if (winner != -1) return 0.0;
        //if (state.isTie()) return 0.5;

        double immediateThreats = evaluateImmediateThreats(state, playerIndex);
        if (immediateThreats > 0) return 0.9 + (immediateThreats * 0.1);

        double baseUtility = baseStrategy.calculateUtility(state, playerIndex);
        double strategicUtility = evaluateStrategies(state, playerIndex);

        return 0.7 * baseUtility + 0.3 * strategicUtility;
    }

    private double evaluateImmediateThreats(PentagoGameState state, int playerIndex) {
        int opponentIndex = 1 - playerIndex;
        double playerThreats = countImmediateThreats(state, playerIndex);
        double opponentThreats = countImmediateThreats(state, opponentIndex);

        return playerThreats - opponentThreats;
    }

    private double countImmediateThreats(PentagoGameState state, int playerIndex) {
        double threats = 0;
        for (PentagoAction action : state.getAvailableActions(playerIndex)) {
            PentagoGameState nextState = state.applyAction(action);
            if (nextState.checkForWinner() == playerIndex) {
                threats += 1;
            }
        }
        return threats / state.getAvailableActions(playerIndex).size();
    }

    private double evaluateStrategies(PentagoGameState state, int playerIndex) {
        return strategies.stream()
                .mapToDouble(strategy -> strategy.evaluateState(state, playerIndex))
                .max()
                .orElse(0.0);
    }

    public List<PentagoAction> suggestStrategicMoves(PentagoGameState state, int playerIndex) {
        List<PentagoAction> suggestedMoves = new ArrayList<>();

        // Cerca mosse vincenti
        List<PentagoAction> winningMoves = findWinningMoves(state, playerIndex);
        suggestedMoves.addAll(winningMoves);

        // Se non ci sono mosse vincenti, cerca mosse di blocco
        if (suggestedMoves.isEmpty()) {
            List<PentagoAction> blockingMoves = findBlockingMoves(state, playerIndex);
            suggestedMoves.addAll(blockingMoves);
        }

        // Se non ci sono mosse vincenti o di blocco, aggiungi mosse strategiche
        if (suggestedMoves.isEmpty()) {
            for (PentagoStrategy strategy : strategies) {
                suggestedMoves.addAll(strategy.suggestMoves(state, playerIndex));
            }
        }

        // Se ancora non abbiamo mosse, aggiungi tutte le mosse disponibili
        if (suggestedMoves.isEmpty()) {
            suggestedMoves.addAll(state.getAvailableActions(playerIndex));
        }

        // Se per qualche motivo la lista è ancora vuota, aggiungi una mossa casuale
        if (suggestedMoves.isEmpty()) {
            System.err.println("Nessuna mossa suggerita trovata. Aggiunta mossa casuale.");
            List<PentagoAction> allMoves = state.getAvailableActions(playerIndex);
            if (!allMoves.isEmpty()) {
                suggestedMoves.add(allMoves.get(new Random().nextInt(allMoves.size())));
            } else {
                throw new IllegalStateException("Nessuna mossa disponibile.");
            }
        }

        return suggestedMoves;
    }

    private List<PentagoAction> findWinningMoves(PentagoGameState state, int playerIndex) {
        return state.getAvailableActions(playerIndex).stream()
                .filter(action -> {
                    PentagoGameState stateCopy = state.deepCopy();
                    PentagoGameState nextState = stateCopy.applyAction(action);
                    return nextState.checkForWinner() == playerIndex;
                })
                .collect(Collectors.toList());
    }

    private List<PentagoAction> findBlockingMoves(PentagoGameState state, int playerIndex) {
        int opponentIndex = 1 - playerIndex;
        List<PentagoAction> blockingMoves = new ArrayList<>();

        for (PentagoAction action : state.getAvailableActions(playerIndex)) {
            PentagoGameState stateCopy = state.deepCopy();
            PentagoGameState nextState = stateCopy.applyAction(action);
            boolean blocks = nextState.getAvailableActions(opponentIndex).stream()
                    .noneMatch(opponentAction -> {
                        PentagoGameState opponentStateCopy = nextState.deepCopy();
                        PentagoGameState opponentNextState = opponentStateCopy.applyAction(opponentAction);
                        return opponentNextState.checkForWinner() == opponentIndex;
                    });

            if (blocks) {
                blockingMoves.add(action);
            }
        }

        return blockingMoves;
    }
}