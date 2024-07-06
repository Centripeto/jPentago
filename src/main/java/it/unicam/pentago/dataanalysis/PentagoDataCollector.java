package it.unicam.pentago.dataanalysis;

import com.lostrucos.jabtbg.dataanalysis.*;
import it.unicam.pentago.models.PentagoAction;
import it.unicam.pentago.models.PentagoGameState;

import java.util.*;

public class PentagoDataCollector implements DataCollector<PentagoGameState, PentagoAction> {
    private List<Map<String, Object>> turns = new ArrayList<>();

    @Override
    public void collectData(PentagoGameState state, PentagoAction action, int player, long decisionTime) {
        Map<String, Object> turnData = new HashMap<>();
        turnData.put("state", state.toString());
        turnData.put("action", action.toString());
        turnData.put("player", player);
        turnData.put("decisionTime", decisionTime);
        turns.add(turnData);
    }

    @Override
    public Map<String, Object> getCollectedData() {
        Map<String, Object> data = new HashMap<>();
        data.put("turns", turns);
        return data;
    }
}
