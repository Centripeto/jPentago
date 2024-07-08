package it.unicam.pentago.models;

import com.lostrucos.jabtbg.core.*;

import java.util.*;

public class PentagoGameState implements GameState<PentagoAction> {
    private PentagoBoard board;
    private int currentPlayer;
    private UtilityStrategy<PentagoGameState, PentagoAction> utilityStrategy;

    public PentagoGameState(PentagoBoard board, int currentPlayer) {
        this.board = board;
        this.currentPlayer = currentPlayer;
    }

    @Override
    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(int player) {
        this.currentPlayer = player;
    }

    @Override
    public boolean isTerminalNode() {
        return checkForWinner() != -1 || isBoardFull();
    }

    @Override
    public boolean isTie() {
        return isTerminalNode() && checkForWinner() == -1;
    }

    @Override
    public PentagoGameState applyAction(PentagoAction action) {
        board.setCell(action.getRow(), action.getCol(), currentPlayer + 1);
        board.rotateQuadrant(action.getQuadrant(), action.isClockwise());
        return new PentagoGameState(board, 1 - currentPlayer);
    }

    @Override
    public PentagoGameState deepCopy() {
        return new PentagoGameState(board.deepCopy(), currentPlayer);
    }

    @Override
    public List<PentagoAction> getAvailableActions(int playerIndex) {
        List<PentagoAction> actions = new ArrayList<>();
        for (int i = 0; i < PentagoBoard.BOARD_SIZE; i++) {
            for (int j = 0; j < PentagoBoard.BOARD_SIZE; j++) {
                if (board.getCell(i, j) == 0) {
                    for (int q = 0; q < 4; q++) {
                        actions.add(new PentagoAction(i, j, q, true, playerIndex));
                        actions.add(new PentagoAction(i, j, q, false, playerIndex));
                    }
                }
            }
        }
        return actions;
    }

    public int checkForWinner() {
        // Check horizontal, vertical, and diagonal lines
        for (int i = 0; i < PentagoBoard.BOARD_SIZE; i++) {
            for (int j = 0; j < PentagoBoard.BOARD_SIZE; j++) {
                int player = board.getCell(i, j);
                if (player != 0) {
                    if (checkLine(i, j, 0, 1, player) || // horizontal
                            checkLine(i, j, 1, 0, player) || // vertical
                            checkLine(i, j, 1, 1, player) || // diagonal
                            checkLine(i, j, 1, -1, player))  // other diagonal
                    {
                        return player - 1;
                    }
                }
            }
        }
        return -1; // No winner
    }

    private boolean checkLine(int startRow, int startCol, int dRow, int dCol, int player) {
        int count = 0;
        for (int i = 0; i < 5; i++) {
            int row = startRow + i * dRow;
            int col = startCol + i * dCol;
            if (row < 0 || row >= PentagoBoard.BOARD_SIZE || col < 0 || col >= PentagoBoard.BOARD_SIZE) {
                return false;
            }
            if (board.getCell(row, col) == player) {
                count++;
            } else {
                break;
            }
        }
        return count == 5;
    }

    private boolean isBoardFull() {
        for (int i = 0; i < PentagoBoard.BOARD_SIZE; i++) {
            for (int j = 0; j < PentagoBoard.BOARD_SIZE; j++) {
                if (board.getCell(i, j) == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public PentagoBoard getBoard() {
        return board;
    }

    public int countAlignedPieces(int playerIndex) {
        int count = 0;
        int playerValue = playerIndex + 1;

        // Controlla orizzontalmente e verticalmente
        for (int i = 0; i < PentagoBoard.BOARD_SIZE; i++) {
            count += countAlignedInLine(i, 0, 0, 1, playerValue); // Orizzontale
            count += countAlignedInLine(0, i, 1, 0, playerValue); // Verticale
        }

        // Controlla le diagonali
        count += countAlignedInLine(0, 0, 1, 1, playerValue); // Diagonale principale
        count += countAlignedInLine(0, PentagoBoard.BOARD_SIZE - 1, 1, -1, playerValue); // Diagonale secondaria

        return count;
    }

    private int countAlignedInLine(int startRow, int startCol, int dRow, int dCol, int playerValue) {
        int count = 0;
        int alignedCount = 0;

        for (int i = 0; i < PentagoBoard.BOARD_SIZE; i++) {
            int row = startRow + i * dRow;
            int col = startCol + i * dCol;

            if (row < 0 || row >= PentagoBoard.BOARD_SIZE || col < 0 || col >= PentagoBoard.BOARD_SIZE) {
                break;
            }

            if (board.getCell(row, col) == playerValue) {
                alignedCount++;
                if (alignedCount >= 3) {
                    count++;
                }
            } else {
                alignedCount = 0;
            }
        }

        return count;
    }

    public int countCenterPieces(int playerIndex) {
        // Conta i pezzi del giocatore nelle 4 posizioni centrali della plancia
        int count = 0;
        for (int i = 2; i <= 3; i++) {
            for (int j = 2; j <= 3; j++) {
                if (board.getCell(i, j) == playerIndex + 1) {
                    count++;
                }
            }
        }
        return count;
    }

    int countLegalMoves(int playerIndex) {
        // Conta il numero di mosse legali disponibili per il giocatore
        return getAvailableActions(playerIndex).size();
    }

    public int countNInARow(int playerIndex, int n) {
        return board.countNInARow(playerIndex, n);
    }

    int countBlockingMoves(int playerIndex, int opponentIndex) {
        return board.countBlockingMoves(playerIndex, opponentIndex);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public double countThreats(int playerIndex, int lineLength) {
        int threats = 0;
        int playerValue = playerIndex + 1;

        // Check all directions
        for (int row = 0; row < PentagoBoard.BOARD_SIZE; row++) {
            for (int col = 0; col < PentagoBoard.BOARD_SIZE; col++) {
                threats += checkThreatInDirection(row, col, 1, 0, playerValue, lineLength);
                threats += checkThreatInDirection(row, col, 0, 1, playerValue, lineLength);
                threats += checkThreatInDirection(row, col, 1, 1, playerValue, lineLength);
                threats += checkThreatInDirection(row, col, 1, -1, playerValue, lineLength);
            }
        }

        return threats;
    }

    private int checkThreatInDirection(int startRow, int startCol, int dRow, int dCol, int playerValue, int lineLength) {
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

    public double evaluatePositionalPotential(int playerIndex) {
        double potential = 0;
        int playerValue = playerIndex + 1;

        // Evaluate center control
        for (int row = 1; row <= 4; row++) {
            for (int col = 1; col <= 4; col++) {
                if (board.getCell(row, col) == playerValue) {
                    potential += 1.0;
                }
            }
        }

        // Evaluate corner control
        if (board.getCell(0, 0) == playerValue) potential += 0.5;
        if (board.getCell(0, 5) == playerValue) potential += 0.5;
        if (board.getCell(5, 0) == playerValue) potential += 0.5;
        if (board.getCell(5, 5) == playerValue) potential += 0.5;

        // Evaluate sequences of 2 and 3 pieces
        potential += countNInARow(playerIndex, 2) * 0.2;
        potential += countNInARow(playerIndex, 3) * 0.5;

        return potential;
    }

    public int countPotentialWinPaths(int playerIndex) {
        int winPaths = 0;
        int playerValue = playerIndex + 1;

        // Check all possible lines
        for (int row = 0; row < PentagoBoard.BOARD_SIZE; row++) {
            for (int col = 0; col < PentagoBoard.BOARD_SIZE; col++) {
                winPaths += checkWinPathInDirection(row, col, 1, 0, playerValue);
                winPaths += checkWinPathInDirection(row, col, 0, 1, playerValue);
                winPaths += checkWinPathInDirection(row, col, 1, 1, playerValue);
                winPaths += checkWinPathInDirection(row, col, 1, -1, playerValue);
            }
        }

        return winPaths;
    }

    private int checkWinPathInDirection(int startRow, int startCol, int dRow, int dCol, int playerValue) {
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

        // Consider a potential win path if there are at least 3 player pieces and the rest are empty spaces
        return (count >= 3 && count + emptySpaces == 5) ? 1 : 0;
    }

    public int countCriticalBlocks(int playerIndex, int opponentIndex) {
        int criticalBlocks = 0;
        int opponentValue = opponentIndex + 1;

        for (int row = 0; row < PentagoBoard.BOARD_SIZE; row++) {
            for (int col = 0; col < PentagoBoard.BOARD_SIZE; col++) {
                if (board.getCell(row, col) == 0) {
                    if (wouldBlockWin(row, col, opponentValue)) {
                        criticalBlocks++;
                    }
                }
            }
        }

        return criticalBlocks;
    }

    private boolean wouldBlockWin(int row, int col, int opponentValue) {
        int[][] directions = {{0, 1}, {1, 0}, {1, 1}, {1, -1}};
        for (int[] dir : directions) {
            if (checkLineForWin(row, col, dir[0], dir[1], opponentValue) ||
                    checkLineForWin(row, col, -dir[0], -dir[1], opponentValue)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkLineForWin(int row, int col, int dRow, int dCol, int opponentValue) {
        int count = 0;
        for (int i = 1; i <= 4; i++) {
            int newRow = row + i * dRow;
            int newCol = col + i * dCol;
            if (newRow < 0 || newRow >= PentagoBoard.BOARD_SIZE || newCol < 0 || newCol >= PentagoBoard.BOARD_SIZE) {
                break;
            }
            if (board.getCell(newRow, newCol) == opponentValue) {
                count++;
            } else {
                break;
            }
        }
        return count == 4;
    }

    public int countPotentialBlocks(int playerIndex, int opponentIndex) {
        int potentialBlocks = 0;
        int opponentValue = opponentIndex + 1;

        for (int row = 0; row < PentagoBoard.BOARD_SIZE; row++) {
            for (int col = 0; col < PentagoBoard.BOARD_SIZE; col++) {
                if (board.getCell(row, col) == 0) {
                    if (wouldBlockPotentialWin(row, col, opponentValue)) {
                        potentialBlocks++;
                    }
                }
            }
        }

        return potentialBlocks;
    }

    private boolean wouldBlockPotentialWin(int row, int col, int opponentValue) {
        int[][] directions = {{0, 1}, {1, 0}, {1, 1}, {1, -1}};
        for (int[] dir : directions) {
            if (checkLineForPotentialWin(row, col, dir[0], dir[1], opponentValue) ||
                    checkLineForPotentialWin(row, col, -dir[0], -dir[1], opponentValue)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkLineForPotentialWin(int row, int col, int dRow, int dCol, int opponentValue) {
        int count = 0;
        int emptySpaces = 0;
        for (int i = 1; i <= 4; i++) {
            int newRow = row + i * dRow;
            int newCol = col + i * dCol;
            if (newRow < 0 || newRow >= PentagoBoard.BOARD_SIZE || newCol < 0 || newCol >= PentagoBoard.BOARD_SIZE) {
                break;
            }
            if (board.getCell(newRow, newCol) == opponentValue) {
                count++;
            } else if (board.getCell(newRow, newCol) == 0) {
                emptySpaces++;
            } else {
                break;
            }
        }
        return count == 3 && emptySpaces == 1;
    }

    public PentagoGameState simulateRotation(int quadrant, boolean clockwise) {
        PentagoBoard newBoard = board.deepCopy();
        newBoard.rotateQuadrant(quadrant, clockwise);
        return new PentagoGameState(newBoard, currentPlayer);
    }
}
