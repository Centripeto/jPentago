package com.lostrucos.jabtbg.dataanalysis;

import com.lostrucos.jabtbg.core.Action;
import com.lostrucos.jabtbg.core.Game;
import com.lostrucos.jabtbg.core.GameState;

class DefaultSimulationReconstructor<T extends GameState<E>, E extends Action> implements SimulationReconstructor<T, E> {
    @Override
    public void reconstruct(Game<T, E> game, SimulationResult<T, E> result) {
        T currentState = game.getInitialState();
        System.out.println("Reconstructing simulation:");
        for (int i = 0; i < result.getActions().size(); i++) {
            E action = result.getActions().get(i);
            System.out.println("Turn " + i + ":");
            System.out.println("Player " + action.getPlayer() + " takes action: " + action);
            System.out.println("Decision time: " + result.getDecisionTimes().get(i) + " ms");
            currentState = game.getNextState(currentState, action);
            System.out.println("Resulting state: " + currentState);
            System.out.println();
        }
        System.out.println("Final state: " + result.getFinalState());
    }
}
