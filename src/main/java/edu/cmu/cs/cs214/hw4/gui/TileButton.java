package edu.cmu.cs.cs214.hw4.gui;

import javax.swing.JButton;

/**
 * The tile button extends from JButton, which can record the x and y coordinates.
 */
public class TileButton extends JButton {
    private int x;
    private int y;

    public void setXY(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getXValue() {
        return x;
    }

    public int getYValue() {
        return y;
    }
}
