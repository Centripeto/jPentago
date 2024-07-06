package com.lostrucos.jabtbg.core;

import it.unicam.pentago.models.PentagoGameState;

import java.util.List;

public interface UtilityStrategy<T extends GameState<E>, E extends Action> {
    double calculateUtility(T state, int playerIndex);
    List<E> suggestStrategicMoves(PentagoGameState state, int currentPlayer);
}
