package edu.cmu.cs.cs214.hw4.core;

import java.util.Objects;

/**
 * A class represent a meeple.
 * It has an int which will be set to the owner id when the meeple is dispatched.
 */
public class Meeple {
    private final int player;

    Meeple(int player) {
        this.player = player;
    }

    /**
     * Get the meepleOwner id.
     *
     * @return the owner id of this meeple.
     */
    public int meepleOwner() {
        return player;
    }

    @Override
    public String toString() {
        return "Meeple belongs to player " + player;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Meeple meeple = (Meeple) o;
        return player == meeple.player;
    }

    @Override
    public int hashCode() {
        return Objects.hash(player);
    }
}
