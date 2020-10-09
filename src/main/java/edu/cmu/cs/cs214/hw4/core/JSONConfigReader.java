package edu.cmu.cs.cs214.hw4.core;

/**
 * A helper class which helps to read the tile type json config files.
 */
public class JSONConfigReader {
    //CHECKSTYLE:OFF
    public static class JSONItem {
        // the type of the tile (A, B, ...)
        public String type;
        // the type of the terrains at the left, right, upper, down and center of the tile
        public String left;
        public String right;
        public String up;
        public String down;
        public String center;
        // if the tile contains coatOfArms
        public boolean coatOfArms;
        // number of this type of tile
        public int quantity;
        // if it is the designed first tile
        public boolean init;
        // if the cities in the tile are separated instead of connected
        public boolean separatedCities;
    }

    public static class JSONTile {
        public String name;
        public JSONItem[] tiles;
    }
    //CHECKSTYLE:ON
}