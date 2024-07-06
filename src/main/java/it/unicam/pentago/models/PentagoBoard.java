package it.unicam.pentago.models;

public class PentagoBoard {
    public static final int BOARD_SIZE = 6;
    public static final int QUADRANT_SIZE = 3;
    private int[][] board;

    public PentagoBoard() {
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

    public int countNInARow(int playerIndex, int n) {
        int count = 0;
        int playerValue = playerIndex + 1;

        // Controlla orizzontalmente
        for (int row = 0; row < BOARD_SIZE; row++) {
            count += countInDirection(row, 0, 0, 1, playerValue, n);
        }

        // Controlla verticalmente
        for (int col = 0; col < BOARD_SIZE; col++) {
            count += countInDirection(0, col, 1, 0, playerValue, n);
        }

        // Controlla diagonali (da sinistra a destra)
        for (int i = 0; i <= BOARD_SIZE - n; i++) {
            count += countInDirection(i, 0, 1, 1, playerValue, n);
            if (i > 0) count += countInDirection(0, i, 1, 1, playerValue, n);
        }

        // Controlla diagonali (da destra a sinistra)
        for (int i = 0; i <= BOARD_SIZE - n; i++) {
            count += countInDirection(i, BOARD_SIZE - 1, 1, -1, playerValue, n);
            if (i > 0) count += countInDirection(0, BOARD_SIZE - 1 - i, 1, -1, playerValue, n);
        }

        return count;
    }

    private int countInDirection(int startRow, int startCol, int dRow, int dCol, int playerValue, int n) {
        int count = 0;
        int sequence = 0;

        for (int i = 0; i < BOARD_SIZE; i++) {
            int row = startRow + i * dRow;
            int col = startCol + i * dCol;

            if (row < 0 || row >= BOARD_SIZE || col < 0 || col >= BOARD_SIZE) {
                break;
            }

            if (board[row][col] == playerValue) {
                sequence++;
                if (sequence == n) {
                    count++;
                    sequence--;  // Per contare sovrapposizioni
                }
            } else {
                sequence = 0;
            }
        }

        return count;
    }

    public int countBlockingMoves(int playerIndex, int opponentIndex) {
        int count = 0;
        int playerValue = playerIndex + 1;
        int opponentValue = opponentIndex + 1;

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (board[row][col] == 0) {  // Cella vuota
                    // Controlla se questa mossa blocca una potenziale vittoria dell'avversario
                    if (isBlockingMove(row, col, playerValue, opponentValue)) {
                        count++;
                    }
                }
            }
        }

        return count;
    }

    private boolean isBlockingMove(int row, int col, int playerValue, int opponentValue) {
        // Direzioni: orizzontale, verticale, diagonale dx, diagonale sx
        int[][] directions = {{0, 1}, {1, 0}, {1, 1}, {1, -1}};

        for (int[] dir : directions) {
            if (wouldBlock(row, col, dir[0], dir[1], playerValue, opponentValue) ||
                    wouldBlock(row, col, -dir[0], -dir[1], playerValue, opponentValue)) {
                return true;
            }
        }

        return false;
    }

    private boolean wouldBlock(int row, int col, int dRow, int dCol, int playerValue, int opponentValue) {
        int opponentCount = 0;
        int emptyCount = 0;

        for (int i = 1; i <= 4; i++) {
            int newRow = row + i * dRow;
            int newCol = col + i * dCol;

            if (newRow < 0 || newRow >= BOARD_SIZE || newCol < 0 || newCol >= BOARD_SIZE) {
                break;
            }

            if (board[newRow][newCol] == opponentValue) {
                opponentCount++;
            } else if (board[newRow][newCol] == 0) {
                emptyCount++;
            } else {
                break;
            }
        }

        // Considera una mossa di blocco se previene 4 in fila dell'avversario
        return opponentCount == 3 && emptyCount == 1;
    }
}
