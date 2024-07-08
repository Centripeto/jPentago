package com.lostrucos.jabtbg.core;

/**
 * Represents an agent (player) in the game.
 */
public interface Player<T extends GameState<E>, E extends Action> {
    /**
     * Returns the index of the player this agent is controlling.
     *
     * @return the index of the player.
     */
    int getPlayerIndex();

    /**
     * Returns a representation of the agent.
     *
     * @return a string representation of the agent.
     */
    String toString();

    // Restituisce l'azione che l'agente sceglierà di eseguire nello stato di gioco specificato.
    E getAction(T state);
}
