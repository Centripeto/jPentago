package com.lostrucos.jabtbg.dataanalysis;

import com.lostrucos.jabtbg.core.Action;
import com.lostrucos.jabtbg.core.GameState;

import java.util.Map;

public interface GameTreeVisualizer<T extends GameState<E>, E extends Action> {
    String visualize(Map<String, Object> data);
}
