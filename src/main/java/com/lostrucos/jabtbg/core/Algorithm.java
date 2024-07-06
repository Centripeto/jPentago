package com.lostrucos.jabtbg.core;

/**
 * Represents an algorithm used by an agent to decide actions in the game.
 */
public interface Algorithm<T extends GameState<E>, E extends Action> {
    /**
     * Initializes the algorithm with the given game and agent.
     *
     * @param game  the game to be played.
     * @param agent the agent using this algorithm.
     */
    void initialize(Game<T, E> game, Agent<T, E> agent); //come specificato, è l'agent che utilizza l'algoritmo e non viceversa
    //altro punto, all'algoritmo serve game o gamestate?

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
     * Updates the algorithm's internal state after an action has been taken.
     *
     * @param gameState the new state of the game.
     * @param action    the action that was taken.
     */
    void updateAfterAction(T gameState, E action);

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
