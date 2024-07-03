package com.lostrucos.jabtbg.tris;

/**
 * This class represent the board in the game of tic-tac-toe
 */
public class Board {
    private Symbol[][] board;

    public Board() {
        board = new Symbol[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = Symbol.FREE;
            }
        }
    }

    /**
     * Method that display on console the current configuration of the board
     */
    public void display() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                switch (board[i][j]) {
                    case FREE -> System.out.print(' ');
                    case CIRCLE -> System.out.print('O');
                    case CROSS -> System.out.print('X');
                }
                if (j < 2) System.out.print("|");
            }
            System.out.println();
            if (i < 2) System.out.println("-----");
        }
    }

    public Symbol[][] getBoard() {
        return board;
    }

    /**
     * Modifies the symbol on the cell specified by the coordinates passed as parameters
     *
     * @param x      the x coordinate
     * @param y      the y coordinate
     * @param symbol the new symbol
     */
    public void setSymbol(int x, int y, Symbol symbol) {
        this.board[x][y] = symbol;
    }


}
