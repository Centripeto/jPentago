package com.lostrucos.jabtbg.algorithms.mcts;

import com.lostrucos.jabtbg.core.Action;
import com.lostrucos.jabtbg.core.GameState;

import java.util.*;

/**
 * Represents a node in the Monte Carlo Tree Search (MCTS) algorithm.
 */
public class MCTSNode<E extends Action,T extends GameState<E>> {
    private T state;
    private MCTSNode<E,T> parentNode;
    private Map<E, MCTSNode<E,T>> childNodes;
    private double totalReward;
    private int visitCount;
    private boolean isLeaf;

    /**
     * Constructs a new MCTSNode.
     *
     * @param state  the game state represented by this node.
     * @param parentNode the parent node.
     */
    public MCTSNode(T state, MCTSNode<E,T> parentNode) {
        this.state = state;
        this.parentNode = parentNode;
        this.childNodes = new HashMap<>();
        this.totalReward = 0.0;
        this.visitCount = 0;
        this.isLeaf = true;
    }

    /**
     * Checks if this node is a terminal node.
     *
     * @return true if this node is terminal, false otherwise.
     */
    public boolean isTerminal() {
        return isLeaf && state.isTerminalNode();
    }

    /**
     * Selects the best action from this node using UCB.
     *
     * @param explorationConstant the exploration constant.
     * @return the selected action.
     */
    public MCTSNode<E,T> selectChild(double explorationConstant) {
        return childNodes.values().stream()
                .max(Comparator.comparing(node -> node.getUCBValue(explorationConstant)))
                .orElseThrow(IllegalStateException::new);
    }

    /**
     * Gets the UCB value for this node.
     *
     * @param explorationConstant the exploration constant.
     * @return the UCB value.
     */
    public double getUCBValue(double explorationConstant) {
        if (visitCount == 0) {
            return Double.POSITIVE_INFINITY;
        }
        return totalReward / visitCount + explorationConstant * Math.sqrt(Math.log(parentNode.visitCount) / visitCount);
    }

    /**
     * Select the best action of this node based on the ratio of reward to visits.
     *
     * @return the best action.
     */
    public E selectBestAction() {
        return childNodes.entrySet().stream()
                .max(Comparator.comparingDouble(entry -> entry.getValue().totalReward / entry.getValue().visitCount))
                .map(Map.Entry::getKey)
                .orElseThrow(IllegalStateException::new);
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
    public MCTSNode<E,T> getParentNode() {
        return parentNode;
    }

    /**
     * Gets the child nodes.
     *
     * @return a map of actions to child nodes.
     */
    public Map<E, MCTSNode<E,T>> getChildNodes() {
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

    /**
     * Checks if this node is a leaf node.
     *
     * @return true if this node is a leaf, false otherwise.
     */
    public boolean isLeaf() {
        return isLeaf;
    }

    /**
     * Sets the new value for the variable isLeaf
     *
     * @param leaf the new boolean value
     */
    public void setLeaf(boolean leaf) {
        isLeaf = leaf;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MCTSNode mctsNode)) return false;
        return Objects.equals(state, mctsNode.state) && Objects.equals(parentNode, mctsNode.parentNode) && Objects.equals(childNodes, mctsNode.childNodes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(state, parentNode, childNodes);
    }

    //Ci dice se un nodo è pienamente espanso
    public boolean isFullyExpanded() {
        int numOfAvailableActions = this.getState().getAvailableActions(getState().getCurrentPlayer()).size();
        return numOfAvailableActions == this.getChildNodes().size();
    }

    public int numOfLegalActions(){
        return this.getState().getAvailableActions(getState().getCurrentPlayer()).size();
    }
}