package it.unicam.pentago.strategies;

import it.unicam.pentago.models.*;
import java.util.*;

public abstract class PentagoStrategy {
    protected static final int BOARD_SIZE = 6;

    public abstract double evaluateState(PentagoGameState state, int playerIndex);
    public abstract List<PentagoAction> suggestMoves(PentagoGameState state, int playerIndex);

    protected boolean isPositionEmpty(PentagoBoard board, int row, int col) {
        return board.getCell(row, col) == 0;
    }

    protected int getNearestQuadrant(int row, int col) {
        return (row / 3) * 2 + (col / 3);
    }
}