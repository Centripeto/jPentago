package it.unicam.pentago.strategies;

import it.unicam.pentago.models.PentagoAction;
import it.unicam.pentago.models.PentagoBoard;
import it.unicam.pentago.models.PentagoGameState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StraightFive extends PentagoStrategy {
    private final int targetRow = 2;

    @Override
    public double evaluateState(PentagoGameState state, int playerIndex) {
        PentagoBoard board = state.getBoard();
        int playerValue = playerIndex + 1;
        int score = 0;

        for (int col = 0; col < BOARD_SIZE; col++) {
            if (board.getCell(targetRow, col) == playerValue) {
                score++;
            }
        }

        return score / 5.0; // Normalize score (5 is needed for a win)
    }

    @Override
    public List<PentagoAction> suggestMoves(PentagoGameState state, int playerIndex) {
        List<PentagoAction> moves = new ArrayList<>();
        PentagoBoard board = state.getBoard();

        for (int col = 0; col < BOARD_SIZE; col++) {
            if (isPositionEmpty(board, targetRow, col)) {
                moves.add(new PentagoAction(targetRow, col, getNearestQuadrant(targetRow, col), true, playerIndex));
                moves.add(new PentagoAction(targetRow, col, getNearestQuadrant(targetRow, col), false, playerIndex));
            }
        }

        // Suggest rotating the quadrants to align pieces
        for (int q = 0; q < 4; q++) {
            moves.add(new PentagoAction(0, 0, q, true, playerIndex));
            moves.add(new PentagoAction(0, 0, q, false, playerIndex));
        }

        if (moves.isEmpty()) {
            List<PentagoAction> allMoves = state.getAvailableActions(playerIndex);
            int movesToAdd = Math.min(5, allMoves.size());
            Collections.shuffle(allMoves);
            moves.addAll(allMoves.subList(0, movesToAdd));
        }

        return moves;
    }
}