package it.unicam.pentago;

import com.lostrucos.jabtbg.core.Action;

public class PentagoAction implements Action {
    private final int row;
    private final int col;
    private final int quadrant;
    private final boolean clockwise;
    private final int player;

    public PentagoAction(int row, int col, int quadrant, boolean clockwise, int player) {
        this.row = row;
        this.col = col;
        this.quadrant = quadrant;
        this.clockwise = clockwise;
        this.player = player;
    }

    public int getRow() { return row; }
    public int getCol() { return col; }
    public int getQuadrant() { return quadrant; }
    public boolean isClockwise() { return clockwise; }

    @Override
    public int getPlayer() {
        return player;
    }

    @Override
    public String toString() {
        return String.format("Player %d: Place at (%d,%d), rotate quadrant %d %s",
                player, row, col, quadrant, clockwise ? "clockwise" : "counterclockwise");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PentagoAction that = (PentagoAction) o;
        return row == that.row &&
                col == that.col &&
                quadrant == that.quadrant &&
                clockwise == that.clockwise &&
                player == that.player;
    }
}