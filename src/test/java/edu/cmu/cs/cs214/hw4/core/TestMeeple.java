package edu.cmu.cs.cs214.hw4.core;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestMeeple {
    Meeple meeple1;
    Meeple meeple2;
    Meeple meeple3;

    @Before
    public void setUp() {
        meeple1 = new Meeple(1);
        meeple2 = new Meeple(1);
        meeple3 = new Meeple(2);
    }

    @Test
    public void testMeepleOwner() {
        assertEquals(2, meeple3.meepleOwner());
    }

    @Test
    public void testToStringDEqualsHash() {
        assertEquals("Meeple belongs to player 1", meeple2.toString());
        assertEquals(meeple1, meeple2);
        assertEquals(meeple1.hashCode(), meeple2.hashCode());
    }

}
