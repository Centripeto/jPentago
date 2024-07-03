package com.lostrucos.jabtbg.tris;

import com.lostrucos.jabtbg.core.Action;
import com.lostrucos.jabtbg.core.GameState;

import java.util.Objects;

public class TrisAction implements Action {
    private int x;
    private int y;
    private int player;

    public TrisAction(int x, int y, int player) {
        this.x = x;
        this.y = y;
        this.player = player;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public int getPlayer() {
        return player;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TrisAction that)) return false;
        return x == that.x && y == that.y && player == that.player;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, player);
    }

}
