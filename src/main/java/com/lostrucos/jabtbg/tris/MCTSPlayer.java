package com.lostrucos.jabtbg.tris;

import com.lostrucos.jabtbg.core.Agent;
import com.lostrucos.jabtbg.core.Algorithm;
import com.lostrucos.jabtbg.core.UtilityStrategy;

public class MCTSPlayer implements Agent<TrisGameState, TrisAction> {
    private final int id;
    private final Algorithm<TrisGameState, TrisAction> algorithm;

    public MCTSPlayer(int id, Algorithm<TrisGameState, TrisAction> algorithm, UtilityStrategy<TrisGameState, TrisAction> utilityStrategy) {
        this.id = id;
        this.algorithm = algorithm;
        this.algorithm.setUtilityStrategy(utilityStrategy);
    }

    @Override
    public int getPlayerIndex() {
        return id;
    }

    @Override
    public TrisAction getAction(TrisGameState state) {
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
