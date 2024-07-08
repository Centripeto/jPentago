package it.unicam.pentago.strategies;

import com.lostrucos.jabtbg.core.*;
import it.unicam.pentago.models.PentagoAction;
import it.unicam.pentago.models.PentagoBoard;
import it.unicam.pentago.models.PentagoGameState;

import java.util.*;

public class AdvancedPentagoStrategy implements UtilityStrategy<PentagoGameState, PentagoAction> {

    private static final double IMMEDIATE_THREAT_WEIGHT = 0.3;
    private static final double LONG_TERM_STRATEGY_WEIGHT = 0.3;
    private static final double BLOCKING_WEIGHT = 0.2;
    private static final double ROTATION_EFFECT_WEIGHT = 0.2;

    @Override
    public double calculateUtility(PentagoGameState state, int playerIndex) {
        int winner = state.checkForWinner();
        if (winner != -1) {
            return winner == playerIndex ? 1.0 : 0.0;
        }

        int opponentIndex = 1 - playerIndex;

        double immediateThreatsScore = evaluateImmediateThreats(state, playerIndex, opponentIndex);
        double longTermStrategyScore = evaluateLongTermStrategy(state, playerIndex, opponentIndex);
        double blockingScore = evaluateBlockingMoves(state, playerIndex, opponentIndex);
        double rotationEffectScore = evaluateRotationEffect(state, playerIndex, opponentIndex);

        return IMMEDIATE_THREAT_WEIGHT * immediateThreatsScore +
                LONG_TERM_STRATEGY_WEIGHT * longTermStrategyScore +
                BLOCKING_WEIGHT * blockingScore +
                ROTATION_EFFECT_WEIGHT * rotationEffectScore;
    }

    @Override
    public List<PentagoAction> suggestStrategicMoves(PentagoGameState state, int currentPlayer) {
        return List.of();
    }

    private double evaluateImmediateThreats(PentagoGameState state, int playerIndex, int opponentIndex) {
        double playerThreats = state.countThreats(playerIndex, 4);
        double opponentThreats = state.countThreats(opponentIndex, 4);

        // Normalize and invert opponent threats
        return (playerThreats - opponentThreats + 10) / 20.0;
    }

    private double evaluateLongTermStrategy(PentagoGameState state, int playerIndex, int opponentIndex) {
        double playerPotential = state.evaluatePositionalPotential(playerIndex);
        double opponentPotential = state.evaluatePositionalPotential(opponentIndex);

        int playerWinPaths = state.countPotentialWinPaths(playerIndex);
        int opponentWinPaths = state.countPotentialWinPaths(opponentIndex);

        double potentialScore = (playerPotential - opponentPotential + 10) / 20.0;
        double winPathsScore = (playerWinPaths - opponentWinPaths + 10.0) / 20.0;

        return (potentialScore + winPathsScore) / 2.0;
    }

    private double evaluateBlockingMoves(PentagoGameState state, int playerIndex, int opponentIndex) {
        int criticalBlocks = state.countCriticalBlocks(playerIndex, opponentIndex);
        int potentialBlocks = state.countPotentialBlocks(playerIndex, opponentIndex);

        // Normalize the blocking score
        return (criticalBlocks * 2 + potentialBlocks) / 20.0;
    }

    private double evaluateRotationEffect(PentagoGameState state, int playerIndex, int opponentIndex) {
        double playerRotationBenefit = calculateRotationBenefit(state, playerIndex);
        double opponentRotationBenefit = calculateRotationBenefit(state, opponentIndex);

        // Normalize and compare rotation benefits
        return (playerRotationBenefit - opponentRotationBenefit + 10) / 20.0;
    }

    private double calculateRotationBenefit(PentagoGameState state, int playerIndex) {
        double benefit = 0;
        for (int quadrant = 0; quadrant < 4; quadrant++) {
            for (boolean clockwise : new boolean[]{true, false}) {
                PentagoGameState rotatedState = state.simulateRotation(quadrant, clockwise);
                benefit += Math.max(0, rotatedState.countThreats(playerIndex, 4) - state.countThreats(playerIndex, 4));
                benefit += Math.max(0, rotatedState.countPotentialWinPaths(playerIndex) - state.countPotentialWinPaths(playerIndex));
            }
        }
        return benefit;
    }

    private double countThreats(PentagoBoard board, int playerIndex, int lineLength) {
        int threats = 0;
        int playerValue = playerIndex + 1;

        // Controlla in tutte le direzioni
        for (int row = 0; row < PentagoBoard.BOARD_SIZE; row++) {
            for (int col = 0; col < PentagoBoard.BOARD_SIZE; col++) {
                threats += checkThreatInDirection(board, row, col, 1, 0, playerValue, lineLength);
                threats += checkThreatInDirection(board, row, col, 0, 1, playerValue, lineLength);
                threats += checkThreatInDirection(board, row, col, 1, 1, playerValue, lineLength);
                threats += checkThreatInDirection(board, row, col, 1, -1, playerValue, lineLength);
            }
        }

        return threats;
    }

    private int checkThreatInDirection(PentagoBoard board, int startRow, int startCol, int dRow, int dCol, int playerValue, int lineLength) {
        int count = 0;
        int emptySpaces = 0;

        for (int i = 0; i < lineLength; i++) {
            int row = startRow + i * dRow;
            int col = startCol + i * dCol;

            if (row < 0 || row >= PentagoBoard.BOARD_SIZE || col < 0 || col >= PentagoBoard.BOARD_SIZE) {
                return 0;
            }

            if (board.getCell(row, col) == playerValue) {
                count++;
            } else if (board.getCell(row, col) == 0) {
                emptySpaces++;
            } else {
                return 0;
            }
        }

        return (count == lineLength - 1 && emptySpaces == 1) ? 1 : 0;
    }

    private double evaluatePositionalPotential(PentagoBoard board, int playerIndex) {
        double potential = 0;
        int playerValue = playerIndex + 1;

        // Valuta il controllo del centro
        for (int row = 1; row <= 4; row++) {
            for (int col = 1; col <= 4; col++) {
                if (board.getCell(row, col) == playerValue) {
                    potential += 1.0;
                }
            }
        }

        // Valuta il controllo degli angoli
        if (board.getCell(0, 0) == playerValue) potential += 0.5;
        if (board.getCell(0, 5) == playerValue) potential += 0.5;
        if (board.getCell(5, 0) == playerValue) potential += 0.5;
        if (board.getCell(5, 5) == playerValue) potential += 0.5;

        // Valuta le sequenze di 2 e 3 pezzi
        //potential += countNInARow(board, playerIndex, 2) * 0.2;
        //potential += countNInARow(board, playerIndex, 3) * 0.5;

        return potential;
    }

    private int countNInARow(PentagoGameState state, int playerIndex, int n) {
        return state.countNInARow(playerIndex, n);
    }

    private int countPotentialWinPaths(PentagoBoard board, int playerIndex) {
        int winPaths = 0;
        int playerValue = playerIndex + 1;

        // Controlla tutte le linee possibili
        for (int row = 0; row < PentagoBoard.BOARD_SIZE; row++) {
            for (int col = 0; col < PentagoBoard.BOARD_SIZE; col++) {
                winPaths += checkWinPathInDirection(board, row, col, 1, 0, playerValue);
                winPaths += checkWinPathInDirection(board, row, col, 0, 1, playerValue);
                winPaths += checkWinPathInDirection(board, row, col, 1, 1, playerValue);
                winPaths += checkWinPathInDirection(board, row, col, 1, -1, playerValue);
            }
        }

        return winPaths;
    }

    private int checkWinPathInDirection(PentagoBoard board, int startRow, int startCol, int dRow, int dCol, int playerValue) {
        int count = 0;
        int emptySpaces = 0;

        for (int i = 0; i < 5; i++) {
            int row = startRow + i * dRow;
            int col = startCol + i * dCol;

            if (row < 0 || row >= PentagoBoard.BOARD_SIZE || col < 0 || col >= PentagoBoard.BOARD_SIZE) {
                return 0;
            }

            if (board.getCell(row, col) == playerValue) {
                count++;
            } else if (board.getCell(row, col) == 0) {
                emptySpaces++;
            } else {
                return 0;
            }
        }

        // Considera un percorso di vittoria potenziale se ci sono almeno 3 pezzi del giocatore e il resto sono spazi vuoti
        return (count >= 3 && count + emptySpaces == 5) ? 1 : 0;
    }

    private int countCriticalBlocks(PentagoBoard board, int playerIndex, int opponentIndex) {
        // Count the number of moves that would directly block an opponent's win
        // This is critical for immediate defense
        // ... Implement the counting logic ...
        return 0; // Placeholder
    }

    private int countPotentialBlocks(PentagoBoard board, int playerIndex, int opponentIndex) {
        // Count the number of moves that could block potential future threats
        // This is important for long-term defense
        // ... Implement the counting logic ...
        return 0; // Placeholder
    }

    // Additional helper methods as needed
}