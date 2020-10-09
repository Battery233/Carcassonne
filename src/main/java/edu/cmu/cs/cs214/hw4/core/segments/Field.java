package edu.cmu.cs.cs214.hw4.core.segments;

import edu.cmu.cs.cs214.hw4.core.Terrain;

/**
 * A segment class to represent a Field type of segment.
 * Two segment object will be the same if and only if they are exact the same object so that
 * the equals methods will be using the super.equals() method. SameType() will be used to find out
 * if two segment are the same type of segment.
 */
public class Field implements Segment {

    @Override
    public boolean sameType(Object obj) {
        return obj instanceof Field;
    }

    @Override
    public Terrain type() {
        return Terrain.Field;
    }

    @Override
    public String toString() {
        return "Segment: " + Terrain.Field;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
