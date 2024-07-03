package com.lostrucos.jabtbg.tris;

import com.lostrucos.jabtbg.core.Agent;
import com.lostrucos.jabtbg.core.Algorithm;

public class MCTSPlayer implements Agent<TrisGameState, TrisAction> {
    private final int id;
    private final Algorithm<TrisGameState, TrisAction> algorithm;

    public MCTSPlayer(int id, Algorithm<TrisGameState, TrisAction> algorithm) {
        this.id = id;
        this.algorithm = algorithm;
    }

    @Override
    public int getPlayerIndex() {
        return id;
    }

    @Override
    public TrisAction getAction(TrisGameState state) {
        algorithm.reset();
        algorithm.initialize(state);
        return algorithm.chooseAction(state);
    }

    //Non serve ora
    @Override
    public void updateAfterAction(TrisGameState state, TrisAction action) {
    }

    //Non serve ora
    @Override
    public void reset() {
    }
}
