package com.lostrucos.jabtbg.core;

import java.util.List;

public abstract class AbstractGame<T extends GameState<E>, E extends Action> implements Game<T, E> {
    private final int playerCount;

    protected AbstractGame(int playerCount) {
        this.playerCount = playerCount;
    }

    @Override
    public int getNumberOfPlayers() {
        return playerCount;
    }

    @Override
    public abstract T getInitialState();


    @Override
    public abstract T getNextState(T state, List<E> actions);

    //TODO: devo aggiungere i metodi di game? Boh da vedere
}
