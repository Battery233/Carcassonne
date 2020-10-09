package edu.cmu.cs.cs214.hw4.core.segments;

import edu.cmu.cs.cs214.hw4.core.Terrain;

/**
 * An interface to define a piece of terrain on a tile.
 * It can be an edge or the center area of the tile.
 * Each tile should has 5 segments
 */
public interface Segment {
    /**
     * The function returns whether an object is the same type of segment as obj.
     *
     * @param obj a second object to compare with.
     * @return if the two object are the same type of object.
     */
    boolean sameType(Object obj);

    /**
     * A function returns the type of the Segment.
     *
     * @return The type of Segment, which is defined in an Enum class (the Terrain class).
     */
    Terrain type();
}
