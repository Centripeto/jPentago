package com.lostrucos.jabtbg.tris;

import com.lostrucos.jabtbg.core.Agent;

import java.util.Scanner;

public class Player implements Agent<TrisGameState, TrisAction> {
    private final int id;

    public Player(int id) {
        this.id = id;
    }

    @Override
    public int getPlayerIndex() {
        return id;
    }

    //Allora,nel caso di ia che usa mcts, la action verrà data dall'algoritmo.
    //Nel caso generico di giocatore, supponiamo che la action sia casuale.
    //Il processo prevede che l'agent richieda al gamestate le azioni disponibili e ne scelga una casualmente.
    @Override
    public TrisAction getAction(TrisGameState state) {
        Scanner scanner = new Scanner(System.in);
        int row, col;
        TrisAction result;
        while (true) {
            System.out.println("Inserire le coordinate della mossa desiderata (riga e colonna)");
            row = scanner.nextInt();
            col = scanner.nextInt();
            TrisAction temp = new TrisAction(row, col, this.id);
            if (state.isActionLegal(temp)) {
                result = temp;
                break;
            }
            System.out.println("Mossa non valida, riprovare");
        }
        return result;
    }

    //non serve ora
    @Override
    public void updateAfterAction(TrisGameState state, TrisAction action) {}

    //non serve ora
    @Override
    public void reset() {}

}
