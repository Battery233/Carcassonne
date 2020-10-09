package edu.cmu.cs.cs214.hw4.core;

import edu.cmu.cs.cs214.hw4.core.segments.City;
import edu.cmu.cs.cs214.hw4.core.segments.Field;
import edu.cmu.cs.cs214.hw4.core.segments.Road;
import edu.cmu.cs.cs214.hw4.core.segments.Monastery;
import edu.cmu.cs.cs214.hw4.core.segments.Segment;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestSegment {

    @Test
    public void testCitySegment(){
        Segment s1 = new City();
        Segment s2 = new City();
        assertTrue(s1.sameType(s2));
        assertNotEquals(s1,s2);
        assertNotEquals(s1.hashCode(),s2.hashCode());
        assertEquals("Segment: City", s1.toString());
        assertEquals(s1.type(),Terrain.City);
        assertFalse(new Road().sameType(s1));
    }

    @Test
    public void testFieldSegment(){
        Segment s1 = new Field();
        Segment s2 = new Field();
        assertTrue(s1.sameType(s2));
        assertNotEquals(s1,s2);
        assertNotEquals(s1.hashCode(),s2.hashCode());
        assertEquals("Segment: Field", s1.toString());
        assertEquals(s1.type(),Terrain.Field);
        assertFalse(new Road().sameType(s1));
    }

    @Test
    public void testRoadSegment(){
        Segment s1 = new Road();
        Segment s2 = new Road();
        assertTrue(s1.sameType(s2));
        assertNotEquals(s1,s2);
        assertNotEquals(s1.hashCode(),s2.hashCode());
        assertEquals("Segment: Road", s1.toString());
        assertEquals(s1.type(),Terrain.Road);
        assertTrue(new Road().sameType(s1));
    }

    @Test
    public void testMonastery(){
        Segment s1 = new Monastery();
        Segment s2 = new Monastery();
        assertTrue(s1.sameType(s2));
        assertNotEquals(s1,s2);
        assertNotEquals(s1.hashCode(),s2.hashCode());
        assertEquals("Segment: Monastery", s1.toString());
        assertEquals(s1.type(),Terrain.Monastery);
        assertFalse(new Road().sameType(s1));
    }

}
