package com.lostrucos.jabtbg.core;

/**
 * Represents an algorithm used by an agent to decide actions in the game.
 */
public interface Algorithm<T extends GameState<E>, E extends Action> {
    /**
     * Initializes the algorithm with the given state of the game
     *
     * @param state the state of the game
     */
    void initialize(T state);

    /**
     * Sets the strategy.
     */
    void setUtilityStrategy(UtilityStrategy<T, E> strategy);

    /**
     * Resets the algorithm internal information
     */
    void reset();

    /**
     * Returns the action chosen by the algorithm for the given game state.
     *
     * @param gameState the current state of the game.
     * @return the chosen action.
     */
    E chooseAction(T gameState);

    /**
     * Returns a representation of the algorithm.
     *
     * @return a string representation of the algorithm.
     */
    String toString();

    /**
     * Applies a pseudo action to a game state during the simulation. The original game state is not modified
     *
     * @param state  the current state of the simulation, not the actual game state
     * @param action the action to apply
     * @return the new state after applying the action
     */
    GameState<E> applyPseudoAction(T state, E action);
}
