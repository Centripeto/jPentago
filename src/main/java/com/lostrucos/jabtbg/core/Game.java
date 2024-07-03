package com.lostrucos.jabtbg.core;

import java.util.List;

/**
 * Represents a game with complete but imperfect information.
 */
public interface Game<T extends GameState<E>, E extends Action> {

    /**
     * Returns the information set for a given player in a given game state.
     *
     * @param playerIndex the index of the player.
     * @param gameState   the current state of the game.
     * @return the information set for the player.
     */
    //Non so se ha senso, cioè, se io mi trovo in uno stato, allora non sto nell'information set(?) boh, da rivedere
    InformationSet getInformationSet(int playerIndex, T gameState);

    /**
     * Returns the initial state of the game.
     *
     * @return the initial game state.
     */
    T getInitialState(); //Non serve, basta lo stato corrente

    /**
     * Return the current state of the game
     *
     * @return the current game state
     */
    T getCurrentState(); //Metodo fondamentale per capire in che stato si trova il gioco

    /**
     * Returns the number of players in the game.
     *
     * @return the number of players.
     */
    int getNumberOfPlayers();

    // Restituisce il nuovo stato di gioco risultante dall'applicazione delle azioni specificate allo stato di gioco corrente.
    T getNextState(T state, List<E> actions); //non utilizzato poiché viene applicata sempre un'azione alla volta

    /**
     * Updates the given game state by applying an action
     *
     * @param state  the state to be updated
     * @param action the action to be applied
     * @return the new state after having applied the action
     */
    T getNextState(T state, E action);


    /**
     * Returns the list of possible actions for a given player in a given game state.
     *
     * @param playerIndex the index of the player.
     * @param gameState   the current state of the game.
     * @return the list of possible actions.
     */
    //Abbiamo deciso che la responsabilità è di GameState, per ora lo lascio qui
    List<E> getPlayerActions(int playerIndex, T gameState);

    /**
     * Method that returns the index of the current player
     *
     * @return the index of the current player
     */
    int getCurrentPlayer(); //restituisce l'indice del giocatore di turno

    //TODO: da levare, ma da errore cfrm
    boolean isTerminal(GameState state);

    //TODO: da levare, ma da errore su altri algoritmi
    double getUtility(GameState state, int playerIndex);
}
