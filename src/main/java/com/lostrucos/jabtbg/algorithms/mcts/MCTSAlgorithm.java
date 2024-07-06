package com.lostrucos.jabtbg.algorithms.mcts;

import com.lostrucos.jabtbg.core.*;
import it.unicam.pentago.models.PentagoGameState;

import java.util.*;

/**
 * Implements the Monte Carlo Tree Search (MCTS) algorithm for games with perfect information.
 */
public class MCTSAlgorithm<E extends Action, T extends GameState<E>> implements Algorithm<T, E> {

    private final int numIterations;
    private final double explorationConstant;
    private Map<T, MCTSNode<T, E>> gameTree;
    private MCTSNode<T, E> rootNode;
    private UtilityStrategy<T, E> utilityStrategy;

    private static final long TIME_LIMIT_MS = 10000; // 10 secondi

    /**
     * Constructs a new MCTSAlgorithm.
     *
     * @param numIterations       the number of simulations to run.
     * @param explorationConstant the exploration constant used in UCB.
     */
    public MCTSAlgorithm(int numIterations, double explorationConstant) {
        this.numIterations = numIterations;
        this.explorationConstant = explorationConstant;
        this.gameTree = new HashMap<>();
    }

    @Override
    public void initialize(Game<T, E> game, Agent<T, E> agent) {}

    /**
     * Initializes the algorithm with the given game and agent.
     * Initializes also the game tree and its root node.
     *
     * @param state the current game state.
     */
    @Override
    public void initialize(T state) {
        gameTree.clear();
        rootNode = new MCTSNode<>(state, null);
        gameTree.put(state, rootNode);
    }

    @Override
    public void setUtilityStrategy(UtilityStrategy<T, E> strategy) {
        this.utilityStrategy = strategy;
    }

    @Override
    public void reset() {
        gameTree.clear();
        rootNode = null;
    }

    /**
     * It chooses the best action to take from the considered state using the MCTS algorithm
     * iterated as many times as was indicated during the instantiation of the MCTS algorithm.
     *
     * @param state the current game state.
     * @return the selected action.
     */
    @Override
    public E chooseAction(T state) {
        if (rootNode == null || !rootNode.getState().equals(state)) {
            initialize(state);
        }

        long startTime = System.currentTimeMillis();
        int iterations = 0;

        for (int i = 0; i < numIterations; i++) {
            //while(System.currentTimeMillis() - startTime < TIME_LIMIT_MS && iterations < numIterations) {
            //iterations++;
            MCTSNode<T, E> selectedNode = select(rootNode);
            MCTSNode<T, E> expandedNode = expand(selectedNode);
            double reward = simulate(expandedNode);
            backpropagate(expandedNode, reward);
        }

        //System.out.println("MCTS completed " + iterations + " iterations in " + (System.currentTimeMillis() - startTime) + "ms");

        return getBestAction(rootNode);
    }

    @Override
    public GameState<E> applyPseudoAction(T state, E action) {
        return state.applyAction(action);
    }

    /**
     * Updates the tree after an action is taken.
     *
     * @param state  the new game state.
     * @param action the action taken.
     */
    @Override
    public void updateAfterAction(GameState state, Action action) {
    }

    /**
     * Selects a leaf node from the given starting node.
     *
     * @param node the starting node.
     * @return the selected leaf node.
     */
    private MCTSNode<T, E> select(MCTSNode<T, E> node) {
        while (!node.isTerminal() && node.isFullyExpanded()) {
            node = node.selectChild(explorationConstant);
        }
        return node;
    }

    /**
     * If the leaf node isn't a terminal node, expands the game tree one time from the given leaf node.
     *
     * @param node the leaf node to expand.
     * @return the expanded node.
     */
    private MCTSNode<T, E> expand(MCTSNode<T, E> node) {
        if (node.isTerminal()) return node;

        //List<E> untriedActions = utilityStrategy.suggestStrategicMoves((PentagoGameState)node.getState(), node.getState().getCurrentPlayer());
        List<E> untriedActions = node.getUntriedActions();
        if (untriedActions.isEmpty()) return node;

        E availableRandomAction = untriedActions.get(new Random().nextInt(untriedActions.size()));
        MCTSNode<T, E> expandedNode = getOrCreateChild(node, availableRandomAction);
        this.applyPseudoAction(expandedNode.getState(), availableRandomAction);
        gameTree.put(expandedNode.getState(), expandedNode);

        return expandedNode;
    }

    public MCTSNode<T, E> getOrCreateChild(MCTSNode<T, E> node, E action) {
        MCTSNode<T, E> child = node.getChildNodes().computeIfAbsent(action, a -> new MCTSNode<T, E>((T) node.getState().deepCopy(), node));
        return child;
    }

    /**
     * Performs a simulation from the given starting node then calls a back-propagation for every simulation node created.
     *
     * @param node the startingNode to start the playout from.
     */
    private double simulate(MCTSNode<T, E> node) {
        MCTSNode<T, E> terminalNode = new MCTSNode<>((T) node.getState().deepCopy(), node.getParentNode());
        while (!terminalNode.getState().isTerminalNode()) {
            //List<E> actions = utilityStrategy.suggestStrategicMoves((PentagoGameState) terminalNode.getState(), terminalNode.getState().getCurrentPlayer());
            List<E> actions = terminalNode.getState().getAvailableActions(terminalNode.getState().getCurrentPlayer());
            E randomAction = actions.get(new Random().nextInt(actions.size()));
            this.applyPseudoAction(terminalNode.getState(), randomAction);
        }
        return utilityStrategy.calculateUtility(terminalNode.getState(), node.getState().getCurrentPlayer());
    }

    /**
     * Performs back-propagation to update the visits and score of all nodes with the results obtained from the simulation until it reaches the root node.
     *
     * @param node   the starting node to perform the back-propagation step.
     * @param reward the reward for the visited node.
     */
    private void backpropagate(MCTSNode<T, E> node, double reward) {
        while (node != null) {
            node.updateNodeStats(reward);
            node = node.getParentNode();
            reward = 1 - reward; // Invert reward for opponent
        }
    }

    /**
     * Select the best action of the given node based on the ratio of reward to visits.
     *
     * @param node the node from which to find the actions.
     * @return the best action.
     */
    private E getBestAction(MCTSNode<T, E> node) {
        if (node.getChildNodes().isEmpty()) {
            // Se non ci sono figli, restituisci un'azione casuale dallo stato corrente
            List<E> availableActions = node.getState().getAvailableActions(node.getState().getCurrentPlayer());
            return availableActions.get(new Random().nextInt(availableActions.size()));
        }
        return node.getChildNodes().entrySet().stream()
                .max(Comparator.comparingDouble(e -> e.getValue().getTotalReward() / e.getValue().getVisitCount()))
                .map(Map.Entry::getKey)
                .orElseThrow(() -> new IllegalStateException("No children found"));
    }

    /**
     * Gets the game tree.
     *
     * @return the game tree.
     */
    public Map<T, MCTSNode<T, E>> getGameTree() {
        return gameTree;
    }

    /**
     * Gets the root node of the game tree.
     *
     * @return the root node.
     */
    public MCTSNode<T, E> getRootNode() {
        return rootNode;
    }
}