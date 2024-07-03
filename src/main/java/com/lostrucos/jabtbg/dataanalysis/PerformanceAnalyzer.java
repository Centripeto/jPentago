package com.lostrucos.jabtbg.dataanalysis;

import com.lostrucos.jabtbg.core.Action;
import com.lostrucos.jabtbg.core.GameState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PerformanceAnalyzer<T extends GameState<E>, E extends Action> {
    public Map<String, Object> compareAlgorithms(List<SimulationResult<T, E>> results) {
        Map<String, Object> comparison = new HashMap<>();
        for (int i = 0; i < results.size(); i++) {
            SimulationResult<T, E> result = results.get(i);
            String algorithmName = "Algorithm " + (i + 1);

            long totalDecisionTime = result.getDecisionTimes().values().stream().mapToLong(Long::longValue).sum();
            double avgDecisionTime = (double) totalDecisionTime / result.getDecisionTimes().size();

            Map<String, Object> algorithmStats = new HashMap<>();
            algorithmStats.put("totalDecisionTime", totalDecisionTime);
            algorithmStats.put("averageDecisionTime", avgDecisionTime);
            algorithmStats.put("numberOfMoves", result.getActions().size());

            comparison.put(algorithmName, algorithmStats);
        }
        return comparison;
    }
}
