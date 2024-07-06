package com.lostrucos.jabtbg.tris;

import com.lostrucos.jabtbg.core.UtilityStrategy;
import it.unicam.pentago.models.PentagoGameState;

import java.util.List;

// Strategia di base: considera solo la vittoria/sconfitta
public class BasicUtilityStrategy implements UtilityStrategy<TrisGameState, TrisAction> {
    @Override
    public double calculateUtility(TrisGameState state, int playerIndex) {
        int winner = state.checkForWinner();
        if (state.isTie()) return 0.5;
        if(state.getCurrentPlayer() == winner) {
            if (playerIndex == winner) {
                return 1.0;
            } else {
                return -1.0;
            }
        } else {
            if (playerIndex == winner) {
                return -1.0;
            } else {
                return 1.0;
            }
        }
    }

    @Override
    public List<TrisAction> suggestStrategicMoves(PentagoGameState state, int currentPlayer) {
        return List.of();
    }
}
