package it.unicam.pentago.dataanalysis;

import com.lostrucos.jabtbg.dataanalysis.PerformanceAnalyzer;
import it.unicam.pentago.models.PentagoAction;
import it.unicam.pentago.models.PentagoGameState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PentagoPerformanceAnalyzer implements PerformanceAnalyzer<PentagoGameState, PentagoAction> {
    @Override
    public Map<String, Object> compareAlgorithms(List<Map<String, Object>> data) {
        Map<String, Object> analysis = new HashMap<>();
        for (int i = 0; i < data.size(); i++) {
            Map<String, Object> algorithmData = data.get(i);
            List<Map<String, Object>> turns = (List<Map<String, Object>>) algorithmData.get("turns");

            long totalDecisionTime = turns.stream()
                    .mapToLong(turn -> (Long) turn.get("decisionTime"))
                    .sum();
            double avgDecisionTime = totalDecisionTime / (double) turns.size();

            Map<String, Object> algorithmAnalysis = new HashMap<>();
            algorithmAnalysis.put("totalDecisionTime", totalDecisionTime);
            algorithmAnalysis.put("averageDecisionTime", avgDecisionTime);
            algorithmAnalysis.put("numberOfMoves", turns.size());

            analysis.put("Algorithm " + (i + 1), algorithmAnalysis);
        }
        return analysis;
    }
}