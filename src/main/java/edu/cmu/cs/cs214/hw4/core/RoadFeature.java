package edu.cmu.cs.cs214.hw4.core;

/**
 * The class to represent a Road feature which extends the feature class.
 * The type methods is customized.
 * Template pattern is used here.
 */
public class RoadFeature extends Feature {
    RoadFeature(int uniqueId) {
        super(uniqueId);
    }

    @Override
    public Terrain type() {
        return Terrain.Road;
    }
}
