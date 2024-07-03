package com.lostrucos.jabtbg.dataanalysis;

import com.lostrucos.jabtbg.core.Action;
import com.lostrucos.jabtbg.core.GameState;

import java.util.List;
import java.util.Map;

public class SimulationResult<T extends GameState<E>, E extends Action> {
    private List<E> actions;
    private Map<Integer, Long> decisionTimes;
    private Map<String, Object> collectedData;
    private T finalState;

    public SimulationResult(List<E> actions, Map<Integer, Long> decisionTimes, Map<String, Object> collectedData, T finalState) {
        this.actions = actions;
        this.decisionTimes = decisionTimes;
        this.collectedData = collectedData;
        this.finalState = finalState;
    }

    // Getters
    public List<E> getActions() { return actions; }
    public Map<Integer, Long> getDecisionTimes() { return decisionTimes; }
    public Map<String, Object> getCollectedData() { return collectedData; }
    public T getFinalState() { return finalState; }
}
