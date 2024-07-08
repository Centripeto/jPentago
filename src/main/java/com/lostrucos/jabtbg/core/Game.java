package com.lostrucos.jabtbg.core;

/**
 * Represents a game with complete but imperfect information.
 */
public interface Game<T extends GameState<E>, E extends Action> {
    /**
     * Returns the initial state of the game.
     *
     * @return the initial game state.
     */
    T getInitialState();

    /**
     * Return the current state of the game
     *
     * @return the current game state
     */
    T getCurrentState();

    /**
     * Updates the given game state by applying an action
     *
     * @param state  the state to be updated
     * @param action the action to be applied
     * @return the new state after having applied the action
     */
    T getNextState(T state, E action);
}
