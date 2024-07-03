package it.unicam.pentago;

public class PentagoBoard {
    public static final int BOARD_SIZE = 6;
    public static final int QUADRANT_SIZE = 3;
    private int[][] board;

    PentagoBoard() {
        this.board = new int[BOARD_SIZE][BOARD_SIZE]; // 0: empty, 1: player 1, 2: player 2
    }

    public int[][] getBoard() {
        return board;
    }

    // Metodi aggiuntivi per manipolare la board
    public void setCell(int row, int col, int player) {
        board[row][col] = player;
    }

    public int getCell(int row, int col) {
        return board[row][col];
    }

    public PentagoBoard deepCopy() {
        PentagoBoard copy = new PentagoBoard();
        for (int i = 0; i < BOARD_SIZE; i++) {
            System.arraycopy(this.board[i], 0, copy.board[i], 0, BOARD_SIZE);
        }
        return copy;
    }

    public void rotateQuadrant(int quadrant, boolean clockwise) {
        int startRow = (quadrant / 2) * QUADRANT_SIZE;
        int startCol = (quadrant % 2) * QUADRANT_SIZE;
        int[][] temp = new int[QUADRANT_SIZE][QUADRANT_SIZE];

        for (int i = 0; i < QUADRANT_SIZE; i++) {
            for (int j = 0; j < QUADRANT_SIZE; j++) {
                if (clockwise) {
                    temp[j][QUADRANT_SIZE-1-i] = board[startRow+i][startCol+j];
                } else {
                    temp[QUADRANT_SIZE-1-j][i] = board[startRow+i][startCol+j];
                }
            }
        }

        for (int i = 0; i < QUADRANT_SIZE; i++) {
            for (int j = 0; j < QUADRANT_SIZE; j++) {
                board[startRow+i][startCol+j] = temp[i][j];
            }
        }
    }
}
