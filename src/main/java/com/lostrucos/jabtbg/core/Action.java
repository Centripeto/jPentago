package com.lostrucos.jabtbg.core;

/**
 * Represents an action that can be taken in the game.
 */
public interface Action {


    /**
     * Returns a representation of the action.
     *
     * @return a string representation of the action.
     */
    String toString();

    /**
     * Returns the index of the player performing this action.
     *
     * @return the index of the player.
     */
    int getPlayer();

}
