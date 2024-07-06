package com.lostrucos.jabtbg.dataanalysis;

import com.lostrucos.jabtbg.core.Action;
import com.lostrucos.jabtbg.core.GameState;

import java.util.List;
import java.util.Map;

public interface PerformanceAnalyzer<T extends GameState<E>, E extends Action> {
    Map<String, Object> compareAlgorithms(List<Map<String, Object>> data);
}
