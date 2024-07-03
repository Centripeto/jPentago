package com.lostrucos.jabtbg.dataanalysis;

import com.lostrucos.jabtbg.core.Action;
import com.lostrucos.jabtbg.core.GameState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class DefaultDataCollector<T extends GameState<E>, E extends Action> implements DataCollector<T, E> {
    private List<Map<String, Object>> collectedData = new ArrayList<>();

    @Override
    public void collectData(T state, E action, int player, long decisionTime) {
        Map<String, Object> turnData = new HashMap<>();
        turnData.put("state", state.toString());
        turnData.put("action", action.toString());
        turnData.put("player", player);
        turnData.put("decisionTime", decisionTime);
        collectedData.add(turnData);
    }

    @Override
    public Map<String, Object> getCollectedData() {
        Map<String, Object> data = new HashMap<>();
        data.put("turns", collectedData);
        return data;
    }
}
