package com.lostrucos.jabtbg.dataanalysis;

import com.lostrucos.jabtbg.core.Action;
import com.lostrucos.jabtbg.core.Game;
import com.lostrucos.jabtbg.core.GameState;

public interface SimulationReconstructor<T extends GameState<E>, E extends Action> {
    void reconstruct(Game<T, E> game, SimulationResult<T, E> result);
}
