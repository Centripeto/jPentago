package it.unicam.pentago.strategies;

import it.unicam.pentago.models.PentagoAction;
import it.unicam.pentago.models.PentagoBoard;
import it.unicam.pentago.models.PentagoGameState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MonicasFive extends PentagoStrategy {
    private final int[][] keyPositions = {{0,2}, {0,3}, {2,0}, {3,0}, {2,5}};

    @Override
    public double evaluateState(PentagoGameState state, int playerIndex) {
        PentagoBoard board = state.getBoard();
        int playerValue = playerIndex + 1;
        int score = 0;

        for (int[] pos : keyPositions) {
            if (board.getCell(pos[0], pos[1]) == playerValue) {
                score++;
            }
        }

        return score / 5.0; // Normalize score
    }

    @Override
    public List<PentagoAction> suggestMoves(PentagoGameState state, int playerIndex) {
        List<PentagoAction> moves = new ArrayList<>();
        PentagoBoard board = state.getBoard();

        for (int[] pos : keyPositions) {
            if (isPositionEmpty(board, pos[0], pos[1])) {
                moves.add(new PentagoAction(pos[0], pos[1], getNearestQuadrant(pos[0], pos[1]), true, playerIndex));
                moves.add(new PentagoAction(pos[0], pos[1], getNearestQuadrant(pos[0], pos[1]), false, playerIndex));
            }
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