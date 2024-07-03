package com.lostrucos.jabtbg.dataanalysis;

import com.lostrucos.jabtbg.core.Action;
import com.lostrucos.jabtbg.core.GameState;

import java.util.List;

public class GameTreeVisualizer<T extends GameState<E>, E extends Action> {
    public String visualize(SimulationResult<T, E> result) {
        StringBuilder sb = new StringBuilder();
        sb.append("Game Tree:\n");
        visualizeNode(sb, result.getActions(), 0, 0);
        return sb.toString();
    }

    private void visualizeNode(StringBuilder sb, List<E> actions, int depth, int index) {
        if (index >= actions.size()) return;

        sb.append("  ".repeat(depth))
                .append("+-- Player ")
                .append(actions.get(index).getPlayer())
                .append(": ")
                .append(actions.get(index))
                .append("\n");

        visualizeNode(sb, actions, depth + 1, index + 1);
    }
}
