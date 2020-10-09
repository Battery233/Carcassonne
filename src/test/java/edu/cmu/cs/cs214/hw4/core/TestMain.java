package edu.cmu.cs.cs214.hw4.core;

import org.junit.Test;

public class TestMain {

    @Test(expected = IllegalArgumentException.class)
    public void testMainException() {
        String[] args = new String[1];
        Main.main(args);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMainException2() {
        String[] args = new String[2];
        args[0] = "6";
        Main.main(args);
    }

    @Test
    public void testMainOnSingleTile() {
        String[] args = new String[2];
        args[0] = "3";
        args[1] = "src/main/resources/initTile.json";
        Main.main(args);
    }
}
