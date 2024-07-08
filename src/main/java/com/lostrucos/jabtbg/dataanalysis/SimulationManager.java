package com.lostrucos.jabtbg.dataanalysis;

import com.lostrucos.jabtbg.core.*;

import java.util.List;
import java.util.Map;

public interface SimulationManager<T extends GameState<E>, E extends Action> {
    void runSimulation(Game<T, E> game, List<Player<T, E>> players);
    Map<String, Object> getSimulationResult();
}