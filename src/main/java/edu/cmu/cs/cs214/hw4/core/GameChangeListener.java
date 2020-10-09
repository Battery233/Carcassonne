package edu.cmu.cs.cs214.hw4.core;

public interface GameChangeListener {
    /**
     * Called when new round started.
     */
    void newRound();

    /**
     * Called when a tile is placed successfully.
     */
    void tilePlaced();

    /**
     * Called when meeple is placed.
     */
    void meeplePlaced();

    /**
     * Called when the game found a feature is finished.
     * @param feature the feature finished
     */
    void featureFinished(Feature feature);

    /**
     * Called when the game ends.
     */
    void gameEnds();
}
