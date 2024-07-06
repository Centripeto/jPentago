package it.unicam.pentago.dataanalysis;

import com.lostrucos.jabtbg.dataanalysis.GameTreeVisualizer;
import it.unicam.pentago.models.PentagoAction;
import it.unicam.pentago.models.PentagoGameState;

import java.util.List;
import java.util.Map;

public class PentagoGameTreeVisualizer implements GameTreeVisualizer<PentagoGameState, PentagoAction> {
    @Override
    public String visualize(Map<String, Object> data) {
        StringBuilder visualization = new StringBuilder();
        List<Map<String, Object>> turns = (List<Map<String, Object>>) data.get("turns");
        for (Map<String, Object> turn : turns) {
            visualization.append("Turn ").append(turns.indexOf(turn) + 1).append(":\n");
            visualization.append("Player: ").append(turn.get("player")).append("\n");
            visualization.append("Action: ").append(turn.get("action")).append("\n");
            visualization.append("Decision Time: ").append(turn.get("decisionTime")).append(" ms\n\n");
        }
        return visualization.toString();
    }
}