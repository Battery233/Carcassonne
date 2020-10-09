package edu.cmu.cs.cs214.hw4.core;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Scanner;

/**
 * The class to represent a single player of the game.
 */
public class Player {
    // the number of meeples the player has, according to the rule of the game.
    private static final int MEEPLE_NUMBER = 7;
    // the number tag of the player
    private final int index;
    // the number of meeples remains
    private int meeples;
    // current score of the player
    private int score;

    Player(int index) {
        this.index = index;
        meeples = MEEPLE_NUMBER;
        score = 0;
    }

    /**
     * Get the current score.
     *
     * @return player's score
     */
    public int getScore() {
        return score;
    }

    /**
     * Add additional scores to the player.
     *
     * @param i the score to be added.
     */
    public void addScore(int i) {
        score += i;
    }

    /**
     * The function which let the user to rotate tile and choose the location to set the tile.
     *
     * @param t the tile to be rotated and placed
     * @return the coordinate choice from the user
     */
    public int[] chooseLocation(Tile t) {
        Scanner sc = new Scanner(System.in, StandardCharsets.UTF_8);
        int rotationCount = Integer.parseInt(sc.next());
        for (int i = 0; i < rotationCount; i++) {
            t.rotateClockwiseOnce();
        }
        //print out the info after rotation.
        System.out.println(t);

        System.out.println("input x and y coordinates!");
        int[] result = new int[2];
        result[0] = Integer.parseInt(sc.next());
        result[1] = Integer.parseInt(sc.next());
        return result;
    }

    /**
     * Getter function for meeple number.
     *
     * @return the number of meeples the user has.
     */
    public int meeplesLeft() {
        return meeples;
    }

    /**
     * Get a meeple if has meeples left.
     *
     * @return an meeple object initialize with the player id.
     */
    public Meeple getMeeple() {
        if (meeples > 0) {
            meeples--;
            return new Meeple(index);
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Take back meeples when returned.
     *
     * @param meepleNumber the number of meeples returned.
     */
    public void returnMeeple(int meepleNumber) {
        meeples += meepleNumber;
    }

    @Override
    public String toString() {
        return "Player{" +
                "index=" + index +
                ", score=" + score +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return index == player.index;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index);
    }
}
