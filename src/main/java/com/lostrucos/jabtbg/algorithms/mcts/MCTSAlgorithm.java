package com.lostrucos.jabtbg.algorithms.mcts;

import com.lostrucos.jabtbg.core.*;

import java.util.*;

/**
 * Implements the Monte Carlo Tree Search (MCTS) algorithm for games with perfect information.
 */
public class MCTSAlgorithm<E extends Action, T extends GameState<E>> implements Algorithm<T, E> {

    private final int numIterations;
    private final double explorationConstant;
    private Map<T, MCTSNode<E, T>> gameTree;
    private MCTSNode<E, T> rootNode;

    /**
     * Constructs a new MCTSAlgorithm.
     *
     * @param numIterations       the number of simulations to run.
     * @param explorationConstant the exploration constant used in UCB.
     */
    public MCTSAlgorithm(int numIterations, double explorationConstant) {
        this.numIterations = numIterations;
        this.explorationConstant = explorationConstant;
        //this.gameTree = new HashMap<>();
    }

    /**
     * Initializes the algorithm with the given game and agent.
     * Initializes also the game tree and its root node.
     *
     * @param game  the game to be played.
     * @param agent the agent playing the game.
     */
    @Override
    public void initialize(Game<T, E> game, Agent<T, E> agent) { //Deve essere richiamato dopo ogni turno del mcts player
        //qui va modificato, perché come specificato nell'interfaccia Game, il metodo deve restituire lo stato corrente e non lo stato iniziale
        //Bisogna sempre capire se all'algoritmo serve game o gamestate
        gameTree.put(game.getCurrentState(), new MCTSNode<>(game.getCurrentState(), null));
        Optional<T> firstKey = gameTree.keySet().stream().findFirst();
        rootNode = firstKey.map(informationSet -> gameTree.get(informationSet)).orElse(null);
    }

    @Override
    public void initialize(T state) {
        gameTree.put(state, new MCTSNode<>(state, null));
        rootNode = gameTree.get(state);
    }

    @Override
    public void reset() {
        gameTree = new HashMap<>();
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
        MCTSNode<E, T> node = gameTree.get(state);
        for (int i = 0; i < numIterations; i++) {
            runIteration(node);
        }
        return node.selectBestAction();
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
     * Runs one complete iteration starting from the given starting node (selection, expansion, simulation and back-propagation).
     *
     * @param startingNode the starting node for the iteration.
     */
    void runIteration(MCTSNode<E, T> startingNode) {
        MCTSNode<E, T> selectedNode = selectLeafNode(startingNode);
        if (selectedNode.isTerminal()) {
            List<Double> rewards = this.getReward(selectedNode);
            backpropagation(selectedNode, rewards);
            return;
        }
        MCTSNode<E, T> leafNode = expandGameTree(selectedNode);
        simulation(leafNode);
    }

    /**
     * Selects a leaf node from the given starting node.
     *
     * @param startingNode the starting node.
     * @return the selected leaf node.
     */
    MCTSNode<E, T> selectLeafNode(MCTSNode<E, T> startingNode) {
        MCTSNode<E, T> selectedNode = startingNode;

        //Finché il nodo è pienamente espanso, faccio due azioni:
        //1)Verifico se è terminale
        //2)Sposto il puntatore al miglior figlio
        while (selectedNode.isFullyExpanded()) {
            if (selectedNode.isTerminal())
                return selectedNode;
            selectedNode = selectedNode.selectChild(explorationConstant);
        }
        return selectedNode;
    }

    /**
     * If the leaf node isn't a terminal node, expands the game tree one time from the given leaf node.
     *
     * @param leafNode the leaf node to expand.
     * @return the expanded node.
     */
    MCTSNode<E, T> expandGameTree(MCTSNode<E, T> leafNode) {
        List<E> actionList = leafNode.getState().getAvailableActions(leafNode.getState().getCurrentPlayer());
        E selectedAction = null;
        //Una volta ottenuta la lista delle azioni disponibili scelgo un'azione che non è mai stata scelta
        //Ovvero un'azione che mi porta ad uno stato non ancora presente nel gametree
        for (E action : actionList) {
            if (!leafNode.getChildNodes().containsKey(action)) {
                selectedAction = action;
                break;
            }
        }
        MCTSNode<E, T> expandedNode = this.getOrCreateChild(leafNode, selectedAction);
        this.applyPseudoAction(expandedNode.getState(), selectedAction);
        gameTree.put(expandedNode.getState(), expandedNode);
        return expandedNode;
    }

    /**
     * Performs a simulation from the given starting node then calls a back-propagation for every simulation node created.
     *
     * @param startingNode the startingNode to start the playout from.
     */
    void simulation(MCTSNode<E, T> startingNode) {
        MCTSNode<E, T> terminalNode = new MCTSNode<>((T) startingNode.getState().deepCopy(), startingNode.getParentNode());
        while (!terminalNode.isTerminal()) {
            List<E> actionList = new ArrayList<>(terminalNode.getState().getAvailableActions(terminalNode.getState().getCurrentPlayer()));
            Random random = new Random();
            int randomIndex = random.nextInt(actionList.size());
            E randomAction = actionList.get(randomIndex);
            //terminalNode = this.getOrCreateChild(terminalNode,randomAction);
            this.applyPseudoAction(terminalNode.getState(), randomAction);
        }
        //Devo salvarmi l'informazione sul vincitore
        List<Double> rewards = this.getReward(terminalNode);
        backpropagation(startingNode, rewards);
    }

    /**
     * Performs back-propagation to update the visits and score of all nodes with the results obtained from the simulation until it reaches the root node.
     *
     * @param startingNode the starting node to perform the back-propagation step.
     */
    void backpropagation(MCTSNode<E, T> startingNode, List<Double> rewards) {
        do {
            startingNode.updateNodeStats(rewards.get(startingNode.getState().getCurrentPlayer()));
            startingNode = startingNode.getParentNode();
        } while (startingNode != null);
    }

    /**
     * The algorithm evalutes the given terminal node and creates an array of rewards
     *
     * @param terminalNode the terminal node that refers to a terminal game state
     * @return an array of rewards
     */

    private List<Double> getReward(MCTSNode<E, T> terminalNode) {
        List<Double> reward = new ArrayList<>();
        if (terminalNode.getState().isTie()) {
            reward.add(0.5);
            reward.add(0.5);
        } else {
            int loserPlayer = terminalNode.getState().getCurrentPlayer();
            if (loserPlayer == 0) {
                reward.add(0, 1.0);
                reward.add(1, -1.0);
            } else {
                reward.add(0, -1.0);
                reward.add(1, 1.0);
            }
        }
        return reward;
    }

    /**
     * Gets the game tree.
     *
     * @return the game tree.
     */
    public Map<T, MCTSNode<E, T>> getGameTree() {
        return gameTree;
    }

    /**
     * Gets the root node of the game tree.
     *
     * @return the root node.
     */
    public MCTSNode<E, T> getRootNode() {
        return rootNode;
    }

    public MCTSNode<E, T> getOrCreateChild(MCTSNode<E, T> node, E action) {
        MCTSNode<E, T> child = node.getChildNodes().computeIfAbsent(action, a -> new MCTSNode<E, T>((T) node.getState().deepCopy(), node));
        node.setLeaf(false);
        return child;
    }
}