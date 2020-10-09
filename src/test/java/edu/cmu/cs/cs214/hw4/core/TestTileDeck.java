package edu.cmu.cs.cs214.hw4.core;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestTileDeck {
    TileDeck deck;

    @Before
    public void setUp() {
        deck = new TileDeck("src/main/resources/generalTest.json");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testException() {
        deck = new TileDeck("src/main/resources/fake.json");
    }

    @Test
    public void testInitTile() {
        assertEquals("Tile{D-Init: left=Field, right=City, up=Road, down=Road, center=Road, coatOfArms=false, separatedCities=false}", deck.getInitTile().toString());
    }

    @Test
    public void testTileNumber() {
        assertEquals(18, deck.tilesRemain());
    }

    @Test
    public void testGetTile() {
        deck = new TileDeck("src/main/resources/singleTile.json");
        assertEquals("Tile{D-1: left=Field, right=City, up=Road, down=Road, center=Road, coatOfArms=false, separatedCities=false}", deck.getTile().toString());
        assertNull(deck.getTile());
    }

    @Test
    public void testToStringHashAndEquals() {
        System.out.println(deck.toString());
        assertEquals("TileDeck with tile number =18", deck.toString());
        TileDeck d2 = new TileDeck("src/main/resources/generalTest.json");
        assertNotEquals(deck,d2);
        assertNotEquals(deck.hashCode(),d2.hashCode());
    }
}
