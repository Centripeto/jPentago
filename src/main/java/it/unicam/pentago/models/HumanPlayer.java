package it.unicam.pentago.models;

import com.lostrucos.jabtbg.core.*;

public class HumanPlayer implements Player<PentagoGameState, PentagoAction> {
    private int playerIndex;
    private PentagoAction lastAction;

    public HumanPlayer(int playerIndex) {
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
    public String toString() {
        return "Giocatore umano (Giocatore " + playerIndex + ")";
    }
}