package edu.cmu.cs.cs214.hw4.core;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

public class TestPlayer {
    Player p1;
    Player p2;

    @Before
    public void setUp() {
        p1 = new Player(1);
        p2 = new Player(2);
    }

    @Test
    public void testScore() {
        p1.addScore(2);
        assertEquals(2, p1.getScore());
    }

    @Test
    public void testMeepleOperations() {
        assertNotNull(p1.getMeeple());
        p1.returnMeeple(1);
        assertEquals(7, p1.meeplesLeft());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMeepleOperations2() {
        for (int i = 0; i < 8; i++)
            p1.getMeeple();
    }

    @Test
    public void testToStringDEqualsHash() {
        assertEquals(new Player(1), p1);
        assertNotEquals(p2, p1);
        assertNotEquals(p2.hashCode(), p1.hashCode());
        assertEquals(new Player(1).hashCode(), p1.hashCode());
        assertEquals("Player{index=1, score=0}", p1.toString());
    }

    @Test
    public void testUserOperation() {
        ByteArrayInputStream in = new ByteArrayInputStream("1 3 2".getBytes(StandardCharsets.UTF_8));
        System.setIn(in);
        Tile t = new Tile("1", "City", "Road", "Road", "Field", "Road", false, false);
        int[] array = new int[]{3,2};
        assertArrayEquals(array,p1.chooseLocation(t));
        assertEquals(Terrain.City,t.getUp().type());
    }
}
