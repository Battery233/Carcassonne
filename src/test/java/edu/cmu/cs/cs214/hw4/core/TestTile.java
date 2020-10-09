package edu.cmu.cs.cs214.hw4.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class TestTile {
    @Test
    public void testTileEquals() {
        Tile t1 = new Tile("1", "City", "Road", "Road", "Field", "Road", false, false);
        Tile t2 = new Tile("1", "City", "Road", "Road", "Field", "Road", false, false);
        Tile t3 = new Tile("2", "Road", "City", "Road", "Field", "Road", false, false);
        assertEquals(t1, t2);
        assertEquals(t1.hashCode(), t2.hashCode());
        assertNotEquals(t1, t3);
        assertNotEquals(t1.hashCode(), t3.hashCode());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testException() {
        new Tile("1", "ERROR", "Road", "Road", "Field", "Road", false, false);
    }

    @Test
    public void testTileSettingValidationAndLocations() {
        Board board = new Board(72);
        Tile t = new Tile("1", "City", "Road", "Road", "Field", "Road", false, false);
        board.setFirstTile(t);
        //testing the tile location
        assertEquals(72, t.getXCoordinate());
        assertEquals(72, t.getYCoordinate());

        //test location validation
        int[] location = {72, 71};
        assertFalse(board.tileValidation(location, new Tile("1", "City", "Road", "Road", "Field", "Road", false, false)));
        assertTrue(board.tileValidation(location, new Tile("2", "City", "City", "Road", "Field", "Road", false, false)));
    }

    @Test
    public void testTileTypeFunctions() {
        Tile t = new Tile("1", "City", "Road", "Road", "Field", "Road", false, false);
        assertNotEquals(t.getRight(), t.getUp());
        assertEquals(Terrain.Road, t.getCenter().type());
        assertFalse(t.hasCoatOfArms());
        assertFalse(t.isseparatedCities());
        assertFalse(t.isIntersection());
        assertFalse(t.isMonastery());
        assertEquals(4, t.edges().size());
        assertEquals("Tile{1: left=City, right=Road, up=Road, down=Field, center=Road, coatOfArms=false, separatedCities=false}", t.toString());
    }
}
