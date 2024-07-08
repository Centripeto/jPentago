package it.unicam.pentago.models;

import com.lostrucos.jabtbg.core.*;

public class AIPlayer implements Player<PentagoGameState, PentagoAction> {
    private int playerIndex;
    private Algorithm<PentagoGameState, PentagoAction> algorithm;

    public AIPlayer(int playerIndex, Algorithm<PentagoGameState, PentagoAction> algorithm, UtilityStrategy<PentagoGameState, PentagoAction> utilityStrategy) {
        this.playerIndex = playerIndex;
        this.algorithm = algorithm;
        algorithm.setUtilityStrategy(utilityStrategy);
    }

    @Override
    public int getPlayerIndex() {
        return playerIndex;
    }

    @Override
    public PentagoAction getAction(PentagoGameState state) {
        try {
            System.out.println("AIPlayer: Inizializzazione dell'algoritmo..."); // Debug
            algorithm.initialize(state);
            System.out.println("AIPlayer: Scelta dell'azione..."); // Debug
            PentagoAction chosenAction = algorithm.chooseAction(state);
            System.out.println("AIPlayer: Azione scelta: " + chosenAction); // Debug
            return chosenAction;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("AIPlayer: Errore durante la scelta dell'azione: " + e.getMessage()); // Debug
            return null;
        }
    }

    @Override
    public String toString() {
        return "AI Player (Giocatore " + playerIndex + ")";
    }

    public Algorithm<PentagoGameState, PentagoAction> getAlgorithm() {
        return algorithm;
    }
}