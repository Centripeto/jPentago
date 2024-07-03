package com.lostrucos.jabtbg.dataanalysis;

import com.lostrucos.jabtbg.core.*;
import java.util.*;

public class SimulationManager<T extends GameState<E>, E extends Action> {
    private Game<T, E> game;
    private List<Agent<T, E>> agents;
    private DataCollector<T, E> dataCollector;
    private SimulationReconstructor<T, E> reconstructor;

    public SimulationManager(Game<T, E> game, List<Agent<T, E>> agents) {
        this.game = game;
        this.agents = agents;
        this.dataCollector = new DefaultDataCollector<>();
        this.reconstructor = new DefaultSimulationReconstructor<>();
    }

    public SimulationResult<T, E> runSimulation() {
        T currentState = game.getInitialState();
        List<E> actions = new ArrayList<>();
        Map<Integer, Long> decisionTimes = new HashMap<>();

        while (!currentState.isTerminalNode()) {
            int currentPlayer = currentState.getCurrentPlayer();
            Agent<T, E> currentAgent = agents.get(currentPlayer);

            long startTime = System.nanoTime();
            E action = currentAgent.getAction(currentState);
            long endTime = System.nanoTime();

            long decisionTime = (endTime - startTime) / 1_000_000; // Convert to milliseconds
            decisionTimes.put(actions.size(), decisionTime);

            actions.add(action);
            currentState = game.getNextState(currentState, action);
            dataCollector.collectData(currentState, action, currentPlayer, decisionTime);
        }

        return new SimulationResult<>(actions, decisionTimes, dataCollector.getCollectedData(), currentState);
    }

    public void reconstructSimulation(SimulationResult<T, E> result) {
        reconstructor.reconstruct(game, result);
    }
}