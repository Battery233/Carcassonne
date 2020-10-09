package edu.cmu.cs.cs214.hw4.gui;

import edu.cmu.cs.cs214.hw4.core.Game;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.HashMap;

/**
 * The main game frame.
 */
public class Carcassonne extends JFrame {
    private static final String CARCASSONNE = "Carcassonne";

    //a map of color to represent players
    private final HashMap<Integer, Color> colorMap;

    public Carcassonne(int playerNumber, String config) {
        Game game = new Game(playerNumber, config);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        //set user colors
        colorMap = new HashMap<>();
        setUserColors();

        //set the layout of the main window
        setTitle(CARCASSONNE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        //add the panel of the game board
        JPanel boardPanel = new BoardPanel(game, colorMap);
        add(boardPanel, BorderLayout.CENTER);

        //add the control panel of the user
        JPanel controlPanel = new ControlPanel(game, colorMap);
        controlPanel.setPreferredSize(new Dimension((int) (screenSize.getWidth() * 0.3), (int) (screenSize.getHeight() * 0.9)));
        add(controlPanel, BorderLayout.EAST);

        //init the game to make it ready
        game.init();
        pack();
        setLocationRelativeTo(null);
    }

    private void setUserColors() {
        //colors for at most 5 players
        colorMap.put(0, Color.BLUE);
        colorMap.put(1, Color.RED);
        colorMap.put(2, Color.ORANGE);
        colorMap.put(3, Color.BLACK);
        colorMap.put(4, Color.PINK);
    }
}
