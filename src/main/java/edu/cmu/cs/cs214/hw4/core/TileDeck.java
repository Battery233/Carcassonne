package edu.cmu.cs.cs214.hw4.core;

import com.google.gson.Gson;
import edu.cmu.cs.cs214.hw4.core.JSONConfigReader.JSONItem;
import edu.cmu.cs.cs214.hw4.core.JSONConfigReader.JSONTile;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The class to represent a tile deck which holds all unused decks.
 */
public class TileDeck {
    private final List<Tile> tiles;
    private JSONItem initItem;

    TileDeck(String config) {
        tiles = new ArrayList<>();
        JSONTile configuration = parse(config);
        boolean flagOfInitItem = false;
        for (int i = 0; i < configuration.tiles.length; i++) {
            JSONItem item = configuration.tiles[i];
            if (item.init) {
                // mark the first tile to be placed.
                initItem = item;
                flagOfInitItem = true;
            }
            for (int j = 0; j < item.quantity; j++) {
                if (!flagOfInitItem) {
                    // create the tile and add to the deck
                    tiles.add(new Tile(item.type + "-" + j, item.left, item.right, item.up, item.down, item.center,
                            item.coatOfArms, item.separatedCities));
                } else {
                    flagOfInitItem = false;
                }
            }
        }
        System.out.println("Tile Deck init finished! Total tile number = " + (tiles.size() + 1) +
                ", Init item exist = " + (initItem != null));
    }

    /**
     * Parse the config json file.
     *
     * @param configFile the json file location
     * @return the JSONTile object which contains the configuration list.
     */
    private static JSONTile parse(String configFile) {
        Gson gson = new Gson();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8))) {
            return gson.fromJson(reader, JSONTile.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("Error when reading file: " + configFile, e);
        }
    }

    /**
     * get the first tile to be placed for the game.
     *
     * @return A tile object which should be the first tile on the board
     */
    public Tile getInitTile() {
        return new Tile(initItem.type + "-Init", initItem.left, initItem.right,
                initItem.up, initItem.down, initItem.center, initItem.coatOfArms, initItem.separatedCities);
    }

    public String getInitTileId() {
        return initItem.type;
    }

    /**
     * Get a random tile from the tile deck.
     *
     * @return A random tile object in the list. Null if the tile deck is empty.
     */
    Tile getTile() {
        if (tiles.size() == 0) {
            return null;
        } else {
            int randomIndex = ThreadLocalRandom.current().nextInt(0, tiles.size());
            Tile result = tiles.get(randomIndex);
            tiles.remove(randomIndex);
            return result;
        }
    }

    /**
     * Find out the number of unused tiles in the deck.
     *
     * @return the number of tiles remain
     */
    public int tilesRemain() {
        return tiles.size();
    }

    @Override
    public String toString() {
        return "TileDeck with tile number =" + tiles.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TileDeck tileDeck = (TileDeck) o;
        return Objects.equals(tiles, tileDeck.tiles) &&
                Objects.equals(initItem, tileDeck.initItem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tiles, initItem);
    }
}
