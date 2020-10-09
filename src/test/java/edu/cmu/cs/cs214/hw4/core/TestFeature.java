package edu.cmu.cs.cs214.hw4.core;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestFeature {
    Feature city;
    Feature monastery;
    Feature road;
    Tile tile;
    Meeple m1 = new Meeple(1);
    Meeple m2 = new Meeple(2);

    @Before
    public void setUp() {
        city = new CityFeature(0);
        monastery = new MonasteryFeature(0);
        road = new RoadFeature(0);
        tile = new Tile("1", "City", "Road", "Road", "Field", "Road", false, false);
    }

    @Test
    public void testType() {
        assertEquals(Terrain.City, city.type());
        assertEquals(Terrain.Monastery, monastery.type());
        assertEquals(Terrain.Road, road.type());
    }

    @Test
    public void testGetterAndSetters() {
        city.addTile(tile);
        city.addSegment(tile.getCenter());
        city.addSegment(tile.edges());
        assertEquals(1, city.getTiles().size());
        assertEquals(5, city.getSegments().size());
        ArrayList<Tile> listT = new ArrayList<>();
        listT.add(tile);
        road.addTile(listT);
        assertEquals(city.getTiles(), road.getTiles());
        monastery.addMeeple(m1);
        ArrayList<Meeple> listM = new ArrayList<>();
        listM.add(m2);
        monastery.addMeeple(listM);
        assertEquals(2, monastery.getMeeples().size());
        assertTrue(monastery.hasMeeples());
    }

    @Test
    public void testToStringHashEquals() {
        city.addTile(tile);
        city.addSegment(tile.getCenter());
        Feature city2 = new CityFeature(0);
        city2.addTile(tile);
        city2.addSegment(tile.getCenter());
        assertEquals(city, city2);
        assertEquals(city.hashCode(), city2.hashCode());
        assertEquals("Feature{" + "type=Road}", city2.toString());
    }
}
