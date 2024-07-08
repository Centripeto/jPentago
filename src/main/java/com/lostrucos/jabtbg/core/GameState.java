package com.lostrucos.jabtbg.core;

import java.util.List;

/**
 * Represents the state of the game and has all the game informations at a particular point in the game.
 * A GameState is derived from the current composition of the game information.
 */
public interface GameState<E extends Action>{
    /**
     * Returns the index of the current player.
     *
     * @return the index of the current player.
     */
    int getCurrentPlayer();

    /**
     * Checks if the current state is a terminal node.
     *
     * @return true if this state is a terminal node, false otherwise.
     */
    boolean isTerminalNode();

    /**
     * Tells if the current game state is a tie.
     *
     * @return true if the game is a tie
     *         false otherwise
     */
    boolean isTie();

    /**
     * Modifies the current game state by applying an action
     *
     * @param action the action to be applied to the current game state
     * @return the new game state after applying the action
     */
    GameState<E> applyAction(E action);

    /**
     * Creates a deep copy of the current game state
     *
     * @return the cloned game state
     */
    GameState<E> deepCopy();

    /**
     * Returns a representation of the state of the game.
     *
     * @return a string representation of the game state.
     */
    String toString();

    List<E> getAvailableActions(int playerIndex);
}
