package com.lostrucos.jabtbg.dataanalysis;

import com.lostrucos.jabtbg.core.Action;
import com.lostrucos.jabtbg.core.GameState;

import java.util.Map;

public interface DataCollector<T extends GameState<E>, E extends Action> {
    void collectData(T state, E action, int player, long decisionTime);
    Map<String, Object> getCollectedData();
}