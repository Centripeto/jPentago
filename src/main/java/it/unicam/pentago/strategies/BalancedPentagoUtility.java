package it.unicam.pentago.strategies;

import it.unicam.pentago.models.PentagoAction;
import it.unicam.pentago.models.PentagoGameState;
import com.lostrucos.jabtbg.core.*;

import java.util.*;
import java.util.stream.Collectors;

public class BalancedPentagoUtility implements UtilityStrategy<PentagoGameState, PentagoAction> {
    private List<PentagoStrategy> strategies;

    public BalancedPentagoUtility() {
        strategies = Arrays.asList(
                new MonicasFive(),
                new MiddleFive(),
                new StraightFive(),
                new TriplePowerPlay()
        );
    }

    @Override
    public double calculateUtility(PentagoGameState state, int playerIndex) {
        // Check for immediate win or loss
        int winner = state.checkForWinner();
        if (winner == playerIndex) return 1.0;
        if (winner != -1) return 0.0;
        if (state.isTie()) return 0.5;

        // Riduci l'impatto della strategia
        double strategicValue = evaluateStrategicValue(state, playerIndex);
        return 0.7 + (0.3 * strategicValue); // Valore base + contributo strategico ridotto
    }

    private double evaluateStrategicValue(PentagoGameState state, int playerIndex) {
        double value = 0.0;
        value += state.countAlignedPieces(playerIndex) * 0.1;
        value += state.countCenterPieces(playerIndex) * 0.05;
        value -= state.countAlignedPieces(1 - playerIndex) * 0.08;
        value -= state.countCenterPieces(1 - playerIndex) * 0.04;
        return Math.max(0, Math.min(1, value)); // Normalizza tra 0 e 1
    }

    @Override
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