package edu.cmu.cs.cs214.hw4.core;

/**
 * The class to represent a Monastery feature which extends the feature class.
 * The type methods is customized.
 * Template pattern is used here.
 */
public class MonasteryFeature extends Feature {
    MonasteryFeature(int uniqueId) {
        super(uniqueId);
    }

    @Override
    public Terrain type() {
        return Terrain.Monastery;
    }
}
