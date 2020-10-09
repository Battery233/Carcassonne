package edu.cmu.cs.cs214.hw4.core;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Test the functions in the board class.
 */
public class TestBoard {
    Board board;
    Tile init = new Tile("1", "City", "Road", "Road", "Field", "Road", false, false);
    Tile t = new Tile("1", "City", "Road", "Road", "Field", "Road", false, false);

    @Before
    public void setUp() {
        board = new Board(72);
    }

    @Test
    public void tesGettingAllPossibleLocations() {
        board.setFirstTile(init);
        assertEquals(4, board.getPossibleLocations(new Tile("1", "City", "Road", "Road", "Field", "Road", true, false)).size());
        assertEquals(3, board.getPossibleLocations(new Tile("2", "Field", "Road", "Road", "Field", "Road", false, false)).size());
        assertEquals(2, board.getPossibleLocations(new Tile("3", "Field", "City", "City", "City", "City", true, false)).size());
        assertEquals(1, board.getPossibleLocations(new Tile("4", "City", "City", "City", "City", "City", true, false)).size());
    }

    @Test
    public void testTileValidation() {
        board.setFirstTile(init);
        assertFalse(board.tileValidation(new int[]{72, 72}, t));
        assertFalse(board.tileValidation(new int[]{72, 82}, t));
        t.rotateClockwiseOnce();
        t.rotateClockwiseOnce();
        assertTrue(board.tileValidation(new int[]{72, 71}, t));
    }

    @Test
    public void testGetNeighbourSegment() {
        board.setFirstTile(init);
        t.rotateClockwiseOnce();
        t.rotateClockwiseOnce();
        board.setTile(new int[]{72, 71}, t);
        assertEquals(t.getRight(), board.getNeighbourSegment(init, init.getLeft()));
        assertNull(board.getNeighbourSegment(init, init.getRight()));
        assertNull(board.getNeighbourSegment(init, init.getUp()));
        assertNull(board.getNeighbourSegment(init, init.getDown()));
        assertEquals(init.getCenter(), board.getNeighbourSegment(init, init.getCenter()));
    }

    @Test
    public void testNumberOfTilesAround() {
        board.setFirstTile(init);
        t.rotateClockwiseOnce();
        t.rotateClockwiseOnce();
        board.setTile(new int[]{72, 71}, t);
        assertEquals(2, board.numberOfTilesAround(init));
    }

    @Test
    public void testToStringHashEquals() {
        assertEquals("Board with a square grid, Size=145*145", board.toString());
        Board b2 = new Board(72);
        assertEquals(board, b2);
        assertEquals(board.hashCode(), b2.hashCode());
    }

}
