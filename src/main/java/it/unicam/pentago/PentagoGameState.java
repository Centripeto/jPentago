package it.unicam.pentago;

import com.lostrucos.jabtbg.core.*;
import java.util.*;

public class PentagoGameState implements GameState<PentagoAction> {
    private PentagoBoard board;
    private int currentPlayer;

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

    @Override
    public List<Integer> getPlayersInGame() {
        return Arrays.asList(0, 1);
    }

    @Override
    public boolean isPlayerStillInGame(int player) {
        return true; // Both players are always in the game in Pentago
    }

    @Override
    public double getUtility(int playerIndex) {
        int winner = checkForWinner();
        if (winner == -1) return 0; // Tie
        return winner == playerIndex ? 1 : -1;
    }
}
