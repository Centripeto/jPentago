package it.unicam.pentago;

import com.lostrucos.jabtbg.core.*;

public class HumanAgent implements Agent<PentagoGameState, PentagoAction> {
    private int playerIndex;
    private PentagoAction lastAction;

    public HumanAgent(int playerIndex) {
        this.playerIndex = playerIndex;
    }

    @Override
    public int getPlayerIndex() {
        return playerIndex;
    }

    @Override
    public PentagoAction getAction(PentagoGameState state) {
        return lastAction;
    }

    public void setAction(PentagoAction action) {
        this.lastAction = action;
    }

    @Override
    public void updateAfterAction(PentagoGameState state, PentagoAction action) {
        // Clear the last action after it's been used
        lastAction = null;
    }

    @Override
    public void reset() {
        lastAction = null;
    }

    @Override
    public String toString() {
        return "Human Agent (Player " + playerIndex + ")";
    }
}