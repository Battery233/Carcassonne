package edu.cmu.cs.cs214.hw4.core;

import edu.cmu.cs.cs214.hw4.core.segments.City;
import edu.cmu.cs.cs214.hw4.core.segments.Field;
import edu.cmu.cs.cs214.hw4.core.segments.Monastery;
import edu.cmu.cs.cs214.hw4.core.segments.Road;
import edu.cmu.cs.cs214.hw4.core.segments.Segment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The class to represent a tile in the game
 */
public class Tile {
    // The id of the tile, which should be a unique string
    private final String id;
    private final Segment center;
    // If the tile contains coat of Arms
    private final boolean coatOfArms;
    // If the cities on the tile are connected
    private final boolean separatedCities;
    // Five segment objects on the tile
    private Segment left;
    private Segment right;
    private Segment up;
    private Segment down;
    // The coordinate of the tile on the deck. The value will be set when the tile is placed on the board
    private int xCoordinate;
    private int yCoordinate;
    private int rotation;
    private int featureMeepleBelongsTo;

    Tile(String id, String left, String right, String up, String down, String center, boolean coatOfArms, boolean separatedCities) {
        this.id = id;
        this.left = parseSegment(left);
        this.right = parseSegment(right);
        this.up = parseSegment(up);
        this.down = parseSegment(down);
        this.center = parseSegment(center);
        this.coatOfArms = coatOfArms;
        this.separatedCities = separatedCities;
        rotation = 0;
        featureMeepleBelongsTo = -1;
    }

    public int getFeatureMeepleBelongsTo() {
        return featureMeepleBelongsTo;
    }

    public void setFeatureMeepleBelongsTo(int i) {
        this.featureMeepleBelongsTo = i;
    }

    public String getId() {
        return id;
    }

    /**
     * Get the left segment object itself.
     * The function is made package private to hide information.
     *
     * @return get the left segment
     */
    public Segment getLeft() {
        return left;
    }

    /**
     * Get the right segment object itself.
     * The function is made package private to hide information.
     *
     * @return the right segment
     */
    public Segment getRight() {
        return right;
    }

    /**
     * Get the upper segment object itself.
     * The function is made package private to hide information.
     *
     * @return the upper segment
     */
    public Segment getUp() {
        return up;
    }

    /**
     * Get the down segment object itself.
     * The function is made package private to hide information.
     *
     * @return the down segment
     */
    public Segment getDown() {
        return down;
    }

    /**
     * Get the center segment object itself.
     * The function is made package private to hide information.
     *
     * @return the center segment
     */
    public Segment getCenter() {
        return center;
    }

    /**
     * If the tile has coat of arms.
     *
     * @return whether tile has coat of arms.
     */
    public boolean hasCoatOfArms() {
        return coatOfArms;
    }

    /**
     * If the tile has separated city segments.
     *
     * @return whether tile has separated city segments.
     */
    public boolean isseparatedCities() {
        return separatedCities;
    }

    public int getRotationTimes() {
        return rotation;
    }

    /**
     * Rotate the tile clockwise for 90 degrees once.
     */
    public void rotateClockwiseOnce() {
        Segment temp = up;
        up = left;
        left = down;
        down = right;
        right = temp;
        rotation = (rotation + 1) % 4;
    }

    /**
     * Test if the tile has an intersection in the middle.
     *
     * @return if an intersection exists.
     */
    public boolean isIntersection() {
        Segment road = new Road();
        int counter = 0;
        if (center.sameType(road)) {
            if (left.sameType(road)) {
                counter++;
            }
            if (right.sameType(road)) {
                counter++;
            }
            if (up.sameType(road)) {
                counter++;
            }
            if (down.sameType(road)) {
                counter++;
            }
        }
        return counter > 2;
    }

    /**
     * Test if the tile has a monastery.
     *
     * @return Whether a monastery exists.
     */
    public boolean isMonastery() {
        return center.type() == Terrain.Monastery;
    }

    /**
     * Return list of four edge segment objects of the tile.
     * Made package private for information hiding
     *
     * @return the list.
     */
    List<Segment> edges() {
        List<Segment> result = new ArrayList<>();
        result.add(left);
        result.add(right);
        result.add(up);
        result.add(down);
        return result;
    }

    /**
     * Get the Y coordinate of the tile in the deck
     *
     * @return the row id
     */
    public int getXCoordinate() {
        return xCoordinate;
    }

    /**
     * Record the X coordinate of the tile on the deck.
     *
     * @param xCoordinate the row id
     */
    public void setXCoordinate(int xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    /**
     * Get the Y coordinate of the tile in the deck
     *
     * @return the column id
     */
    public int getYCoordinate() {
        return yCoordinate;
    }

    /**
     * Record the Y coordinate of the tile on the deck.
     *
     * @param yCoordinate the row id
     */
    public void setYCoordinate(int yCoordinate) {
        this.yCoordinate = yCoordinate;
    }

    /**
     * Parse a string of segment to a segment object.
     *
     * @param s the type string of the segment
     * @return a new segment object according to the string.
     */
    private Segment parseSegment(String s) {
        switch (s) {
            case "Road":
                return new Road();
            case "Field":
                return new Field();
            case "Monastery":
                return new Monastery();
            case "City":
                return new City();
            default:
                throw new IllegalArgumentException("Invalid Segment " + s);
        }
    }

    @Override
    public String toString() {
        return "Tile{" + id +
                ": left=" + left.getClass().getSimpleName() +
                ", right=" + right.getClass().getSimpleName() +
                ", up=" + up.getClass().getSimpleName() +
                ", down=" + down.getClass().getSimpleName() +
                ", center=" + center.getClass().getSimpleName() +
                ", coatOfArms=" + coatOfArms +
                ", separatedCities=" + separatedCities +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        // according to the game design, the id will be unique for two different tiles.
        return id.equals(((Tile) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
