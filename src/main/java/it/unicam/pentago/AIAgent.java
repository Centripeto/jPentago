package it.unicam.pentago;

import com.lostrucos.jabtbg.core.*;

public class AIAgent implements Agent<PentagoGameState, PentagoAction> {
    private int playerIndex;
    private Algorithm<PentagoGameState, PentagoAction> algorithm;

    public AIAgent(Algorithm<PentagoGameState, PentagoAction> algorithm, int playerIndex) {
        this.algorithm = algorithm;
        this.playerIndex = playerIndex;
    }

    public void setAlgorithm(Algorithm<PentagoGameState, PentagoAction> algorithm) {
        this.algorithm = algorithm;
    }

    @Override
    public int getPlayerIndex() {
        return playerIndex;
    }

    @Override
    public PentagoAction getAction(PentagoGameState state) {
        try {
            System.out.println("AIAgent: Inizializzazione dell'algoritmo..."); // Debug
            algorithm.reset();
            algorithm.initialize(state);
            System.out.println("AIAgent: Scelta dell'azione..."); // Debug
            PentagoAction chosenAction = algorithm.chooseAction(state);
            System.out.println("AIAgent: Azione scelta: " + chosenAction); // Debug
            return chosenAction;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("AIAgent: Errore durante la scelta dell'azione: " + e.getMessage()); // Debug
            return null;
        }
    }

    @Override
    public void updateAfterAction(PentagoGameState state, PentagoAction action) {
        // AI doesn't need to update internal state
    }

    @Override
    public void reset() {
        algorithm.reset();
    }

    @Override
    public String toString() {
        return "AI Agent (Player " + playerIndex + ")";
    }

    public Algorithm<PentagoGameState, PentagoAction> getAlgorithm() {
        return algorithm;
    }
}