package it.unicam.pentago;

import com.lostrucos.jabtbg.algorithms.mcts.MCTSAlgorithm;
import com.lostrucos.jabtbg.core.*;
import com.lostrucos.jabtbg.core.UtilityStrategy;
import it.unicam.pentago.models.*;
import it.unicam.pentago.strategies.BalancedPentagoUtility;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.List;

public class PentagoController implements Game<PentagoGameState, PentagoAction> {
    private PentagoGameState currentState;
    private PentagoGameState previousState;
    private Agent<PentagoGameState, PentagoAction> player1;
    private Agent<PentagoGameState, PentagoAction> player2;
    private PentagoLogger logger;
    private PentagoGame gameView;
    private PentagoDataManager dataManager;
    private boolean isAIvsAI;

    public PentagoController(PentagoGame gameView) {
        this.gameView = gameView;
        this.logger = new PentagoLogger(gameView.getGameLog());
        this.dataManager = new PentagoDataManager();
    }

    public void setupNewGame(String player1Type, String player2Type, String difficulty1, String difficulty2, String startingPlayer) {
        PentagoBoard initialBoard = new PentagoBoard();
        int initialPlayer = "Giocatore 1".equals(startingPlayer) ? 0 :
                "Giocatore 2".equals(startingPlayer) ? 1 :
                        (int) (Math.random() * 2);
        currentState = new PentagoGameState(initialBoard, initialPlayer);
        previousState = null;

        player1 = createAgent(player1Type, 0, difficulty1);
        player2 = createAgent(player2Type, 1, difficulty2);

        isAIvsAI = player1 instanceof AIAgent && player2 instanceof AIAgent;

        gameView.resetGame();
        updateGameView();
        if (currentState.getCurrentPlayer() == 1) {
            makeMove(player2);
        } else if (isAIvsAI) {
            makeMove(player1);
        }
    }

    private Agent<PentagoGameState, PentagoAction> createAgent(String type, int playerIndex, String difficulty) {
        if ("Umano".equals(type)) {
            return new HumanAgent(playerIndex);
        } else {
            Algorithm<PentagoGameState, PentagoAction> algorithm = createAlgorithm(type, difficulty);
            UtilityStrategy utilityStrategy = createUtilityStrategy(difficulty);
            return new AIAgent(playerIndex, algorithm, utilityStrategy);
        }
    }

    private Algorithm<PentagoGameState, PentagoAction> createAlgorithm(String type, String difficulty) {
        int iterations = getIterationsForDifficulty(difficulty);
        switch (type) {
            case "Monte Carlo Tree Search":
                return new MCTSAlgorithm<>(iterations, Math.sqrt(2));
            /*case "Minimax":
                return new MinimaxAlgorithm<>(iterations);
            case "Minimax con Alfa-Beta pruning":
                return new AlphaBetaAlgorithm<>(iterations);*/
            default:
                throw new IllegalArgumentException("Algoritmo non riconosciuto: " + type);
        }
    }

    private UtilityStrategy<PentagoGameState, PentagoAction> createUtilityStrategy(String difficulty) {
        switch (difficulty) {
            case "Facile":
                return new BalancedPentagoUtility();
            case "Normale":
                return new BalancedPentagoUtility();
            case "Difficile":
                return new BalancedPentagoUtility();
            default:
                return new BalancedPentagoUtility();
        }
    }

    private int getIterationsForDifficulty(String difficulty) {
        switch (difficulty) {
            case "Facile": return 500;
            case "Normale": return 1000;
            case "Difficile": return 2000;
            default: return 100000;
        }
    }

    public void handleCellClick(int row, int col) {
        if (currentState.getBoard().getCell(row, col) == 0) {
            if (currentState.getCurrentPlayer() == 0 && player1 instanceof HumanAgent) {
                gameView.showRotationOptions();
            } else if (currentState.getCurrentPlayer() == 1 && player2 instanceof HumanAgent) {
                gameView.showRotationOptions();
            }
        } else {
            gameView.setInstructionText("Questa cella è già occupata. Scegli un'altra cella.");
            gameView.setRedTextColor();
        }
    }

    public void handleRotation(int quadrant, boolean clockwise) {
        Agent<PentagoGameState, PentagoAction> currentAgent = currentState.getCurrentPlayer() == 0 ? player1 : player2;
        if (currentAgent instanceof HumanAgent) {
            PentagoAction action = new PentagoAction(gameView.getSelectedRow(), gameView.getSelectedColumn(), quadrant, clockwise, currentState.getCurrentPlayer());
            ((HumanAgent) currentAgent).setAction(action);
            applyAction(action);
            if (!currentState.isTerminalNode()) {
                makeMove(currentState.getCurrentPlayer() == 0 ? player1 : player2);
            } else {
                handleGameEnd();
            }
        }
    }

    private void makeMove(Agent<PentagoGameState, PentagoAction> agent) {
        gameView.setInstructionText((agent == player1 ? "Giocatore 1" : "Giocatore 2") + " sta pensando...");

        if (agent instanceof AIAgent) {
            new Thread(() -> {
                try {
                    long startTime = System.nanoTime();
                    PentagoAction aiAction = agent.getAction(currentState);
                    long endTime = System.nanoTime();
                    long decisionTime = (endTime - startTime) / 1_000_000; // In millisecondi

                    System.out.println("AI ha scelto l'azione: " + aiAction + " in " + decisionTime + "ms");

                    if (aiAction != null) {
                        Platform.runLater(() -> {
                            applyAction(aiAction);
                            dataManager.logAction(currentState, aiAction, currentState.getCurrentPlayer(), decisionTime);
                            if (!currentState.isTerminalNode()) {
                                if (isAIvsAI) {
                                    makeMove(currentState.getCurrentPlayer() == 0 ? player1 : player2);
                                } else {
                                    updateGameView();
                                }
                            } else {
                                handleGameEnd();
                            }
                        });
                    } else {
                        System.err.println("L'IA non è riuscita a scegliere un'azione valida.");
                        Platform.runLater(this::handleGameEnd);
                    }
                } catch (Exception e) {
                    System.err.println("Errore durante la scelta dell'azione dell'IA: " + e.getMessage());
                    e.printStackTrace();
                    Platform.runLater(this::handleGameEnd);
                }
            }).start();
        } else {
            updateGameView();
        }
    }

    private void applyAction(PentagoAction action) {
        previousState = currentState;
        PentagoBoard newBoard = currentState.getBoard().deepCopy();
        newBoard.setCell(action.getRow(), action.getCol(), currentState.getCurrentPlayer() + 1);
        newBoard.rotateQuadrant(action.getQuadrant(), action.isClockwise());
        currentState = new PentagoGameState(newBoard, 1 - currentState.getCurrentPlayer());

        logger.logAction(action, action.getPlayer() == 0 ? "Giocatore 1" : "Giocatore 2");
        updateGameView();
        //gameView.rotateQuadrant(action.getQuadrant(), action.isClockwise());

        if (previousState != null) {
            gameView.updateGameHistory(previousState, currentState);
            previousState = null;
        }
    }

    private void updateGameView() {
        gameView.updateBoard(currentState.getBoard());
        String currentPlayerName = currentState.getCurrentPlayer() == 0 ? "Giocatore 1" : "Giocatore 2";
        gameView.setInstructionText("Turno di " + currentPlayerName + ". Posiziona un segnalino.");
        gameView.hideRotationOptions();
    }

    private void handleGameEnd() {
        int winner = currentState.checkForWinner();
        if (winner == -1) {
            gameView.showGameEndDialog("La partita è terminata in pareggio!");
        } else {
            String winnerName = winner == 0 ? "Giocatore 1" : "Giocatore 2";
            List<int[]> winningCells = findWinningCells(winner + 1);
            gameView.highlightWinningCells(winningCells);
            gameView.showGameEndDialog(winnerName + " ha vinto la partita!");
        }
        logger.saveLogToFile();
        gameView.showSaveLogDialog(format -> dataManager.saveLogToFile(format));
    }

    private List<int[]> findWinningCells(int player) {
        List<int[]> winningCells = new ArrayList<>();
        PentagoBoard board = currentState.getBoard();

        // Check horizontal, vertical, and diagonal lines
        for (int i = 0; i < PentagoBoard.BOARD_SIZE; i++) {
            for (int j = 0; j < PentagoBoard.BOARD_SIZE; j++) {
                if (board.getCell(i, j) == player) {
                    if (checkLineAndCollectCells(i, j, 0, 1, player, winningCells) || // horizontal
                            checkLineAndCollectCells(i, j, 1, 0, player, winningCells) || // vertical
                            checkLineAndCollectCells(i, j, 1, 1, player, winningCells) || // diagonal
                            checkLineAndCollectCells(i, j, 1, -1, player, winningCells))  // other diagonal
                    {
                        return winningCells;
                    }
                }
            }
        }
        return winningCells;
    }

    private boolean checkLineAndCollectCells(int startRow, int startCol, int dRow, int dCol, int player, List<int[]> cells) {
        cells.clear();
        for (int i = 0; i < 5; i++) {
            int row = startRow + i * dRow;
            int col = startCol + i * dCol;
            if (row < 0 || row >= PentagoBoard.BOARD_SIZE || col < 0 || col >= PentagoBoard.BOARD_SIZE ||
                    currentState.getBoard().getCell(row, col) != player) {
                return false;
            }
            cells.add(new int[]{row, col});
        }
        return true;
    }

    public PentagoDataManager getDataManager() {
        return dataManager;
    }

    @Override
    public InformationSet getInformationSet(int playerIndex, PentagoGameState gameState) {
        return null;
    }

    @Override
    public PentagoGameState getInitialState() {
        return null;
    }

    @Override
    public PentagoGameState getCurrentState() {
        return currentState;
    }

    @Override
    public int getNumberOfPlayers() {
        return 0;
    }

    @Override
    public PentagoGameState getNextState(PentagoGameState state, List<PentagoAction> actions) {
        return null;
    }

    @Override
    public PentagoGameState getNextState(PentagoGameState state, PentagoAction action) {
        return (PentagoGameState) state.applyAction(action);
    }

    @Override
    public List<PentagoAction> getPlayerActions(int playerIndex, PentagoGameState gameState) {
        return List.of();
    }

    @Override
    public int getCurrentPlayer() {
        return currentState.getCurrentPlayer();
    }

    @Override
    public boolean isTerminal(GameState state) {
        return false;
    }

    @Override
    public double getUtility(GameState state, int playerIndex) {
        return 0;
    }
}