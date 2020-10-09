package edu.cmu.cs.cs214.hw4.core;

import edu.cmu.cs.cs214.hw4.core.segments.Segment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * The abstract class to represent a feature in the game.
 * <p>
 * This class implements common functions for a City/Monastery/Road feature.
 * <p>
 * Template pattern is used here for code reuse since the most functions for features are same.
 * <p>
 * In my game design, as in the real world, a feature should include these information:
 * The tiles and the specific segments which consist this feature and
 * the meeple(s) which are placed on this feature (meeple belongs to a feature when it is placed).
 * The exact location of which segment the meeple is on does not matter since one feature can have at most one
 * meeple (unless it is merged)
 * <p>
 * Note: All the methods in this class are made packet private to maximize information hiding.
 * Defensive copying is used when returning collections.
 */
public abstract class Feature {
    private final int uniqueId;
    private final List<Meeple> meeples = new ArrayList<>();
    private final List<Tile> tiles = new ArrayList<>();
    private final List<Segment> segments = new ArrayList<>();

    Feature(int id) {
        uniqueId = id;
    }

    abstract Terrain type();

    public List<Tile> getTiles() {
        return new ArrayList<>(tiles);
    }

    List<Segment> getSegments() {
        return new ArrayList<>(segments);
    }

    List<Meeple> getMeeples() {
        return new ArrayList<>(meeples);
    }

    public int getUniqueId() {
        return uniqueId;
    }

    Feature addSegment(Segment s) {
        segments.add(s);
        return this;
    }

    Feature addSegment(Collection<Segment> s) {
        segments.addAll(s);
        return this;
    }

    Feature addTile(Tile t) {
        tiles.add(t);
        return this;
    }

    Feature addTile(Collection<Tile> t) {
        tiles.addAll(t);
        return this;
    }

    Feature addMeeple(Meeple m) {
        meeples.add(m);
        return this;
    }

    Feature addMeeple(Collection<Meeple> m) {
        meeples.addAll(m);
        return this;
    }

    boolean hasMeeples() {
        return meeples.size() > 0;
    }

    @Override
    public String toString() {
        return "Feature{" +
                "type=" + segments.get(0).type() + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Feature feature = (Feature) o;
        // Array equals are suitable here. Two same features should consist of exact the same
        // tiles, segments and meeples. Since all three types of objects in the list are unique in this game,
        // this return value will work.
        return meeples.equals(feature.meeples) &&
                tiles.equals(feature.tiles) &&
                segments.equals(feature.segments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(meeples, tiles, segments);
    }
}
