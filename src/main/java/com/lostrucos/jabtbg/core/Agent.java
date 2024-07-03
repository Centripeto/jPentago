package com.lostrucos.jabtbg.core;

/**
 * Represents an agent (player) in the game.
 */
public interface Agent<T extends GameState<E>, E extends Action> {

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

    // Consente all'agente di aggiornare il suo stato interno dopo che un'azione è stata eseguita nello stato di gioco specificato.
    void updateAfterAction(T state, E action); //In pratica l'agente aggiorna il suo information set, questo è il succo

    // Reimposta lo stato interno dell'agente prima di iniziare una nuova partita.
    void reset();

}
