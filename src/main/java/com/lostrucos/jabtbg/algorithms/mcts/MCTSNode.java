package com.lostrucos.jabtbg.algorithms.mcts;

import com.lostrucos.jabtbg.core.Action;
import com.lostrucos.jabtbg.core.GameState;

import java.util.*;

/**
 * Represents a node in the Monte Carlo Tree Search (MCTS) algorithm.
 */
public class MCTSNode<T extends GameState<E>, E extends Action> {
    private T state;
    private MCTSNode<T, E> parentNode;
    private Map<E, MCTSNode<T, E>> childNodes;
    private double totalReward;
    private int visitCount;

    /**
     * Constructs a new MCTSNode.
     *
     * @param state  the game state represented by this node.
     * @param parentNode the parent node.
     */
    public MCTSNode(T state, MCTSNode<T, E> parentNode) {
        this.state = state;
        this.parentNode = parentNode;
        this.childNodes = new HashMap<>();
        this.totalReward = 0.0;
        this.visitCount = 0;
    }

    /**
     * Checks if this node is a terminal node.
     *
     * @return true if this node is terminal, false otherwise.
     */
    public boolean isTerminal() {
        return state.isTerminalNode();
    }

    public boolean isFullyExpanded() {
        return childNodes.size() == state.getAvailableActions(state.getCurrentPlayer()).size();
    }

    /**
     * Selects the best action from this node using UCB.
     *
     * @param explorationConstant the exploration constant.
     * @return the selected action.
     */
    public MCTSNode<T, E> selectChild(double explorationConstant) {
        return childNodes.values().stream()
                .max(Comparator.comparingDouble(child -> calculateUCB(child, explorationConstant)))
                .orElseThrow(() -> new IllegalStateException("No children to select"));
    }

    /**
     * Gets the UCB value for this node.
     *
     * @param explorationConstant the exploration constant.
     * @return the UCB value.
     */
    private double calculateUCB(MCTSNode<T, E> child, double explorationConstant) {
        if (child.visitCount == 0) {
            return Double.POSITIVE_INFINITY;
        }
        double exploitation = child.totalReward / child.visitCount;
        double exploration = Math.sqrt(Math.log(this.visitCount) / child.visitCount);
        return exploitation + explorationConstant * exploration;
    }

    public List<E> getUntriedActions() {
        List<E> availableActions = state.getAvailableActions(state.getCurrentPlayer());
        availableActions.removeAll(childNodes.keySet());
        return availableActions;
    }

    /**
     * Updates the statistics of this node.
     *
     * @param reward the value to update with.
     */
    public void updateNodeStats(double reward) {
        this.visitCount++;
        this.totalReward += reward;
    }

    /**
     * Gets the game state represented by this node.
     *
     * @return the game state.
     */
    public T getState() {
        return state;
    }

    /**
     * Gets the parent of this node.
     *
     * @return the parent node of this node.
     */
    public MCTSNode<T, E> getParentNode() {
        return parentNode;
    }

    /**
     * Gets the child nodes.
     *
     * @return a map of actions to child nodes.
     */
    public Map<E, MCTSNode<T, E>> getChildNodes() {
        return childNodes;
    }

    /**
     * Gets the total reward of the node
     *
     * @return the reward score of the node
     */
    public double getTotalReward() {
        return totalReward;
    }

    /**
     * Gets the number of times the node has been visited
     *
     * @return the number of times the node has been visited
     */
    public int getVisitCount() {
        return visitCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MCTSNode<?, ?> mctsNode = (MCTSNode<?, ?>) o;
        return Objects.equals(state, mctsNode.state);
    }

    @Override
    public int hashCode() {
        return Objects.hash(state);
    }

    @Override
    public String toString() {
        return "MCTSNode{" +
                "state=" + state +
                ", visitCount=" + visitCount +
                ", totalReward=" + totalReward +
                ", childNodes=" + childNodes.size() +
                '}';
    }

    public int numOfLegalActions(){
        return this.getState().getAvailableActions(getState().getCurrentPlayer()).size();
    }
}