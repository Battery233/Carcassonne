package edu.cmu.cs.cs214.hw4.gui;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.Component;

/**
 * The initializing window to ask user to input the player number.
 */
public class InitWindow extends JPanel {
    private static final String START_GAME_TEXT = "Start Game!";
    private static final String PROMPT = "Input player number (2-5):";
    private static final int INPUT_FIELD_WIDTH = 30;
    private JFrame parentFrame;

    InitWindow(JFrame frame, String config) {
        parentFrame = frame;
        JLabel hintText = new JLabel(PROMPT);
        JTextField input = new JTextField(INPUT_FIELD_WIDTH);
        JButton startButton = new JButton(START_GAME_TEXT);

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        add(hintText);
        add(input);
        add(startButton);
        hintText.setAlignmentX(Component.CENTER_ALIGNMENT);
        input.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        //add listener to test if the input is legit
        startButton.addActionListener(e -> {
            int playerNumber;
            String userInput = input.getText();
            try {
                playerNumber = Integer.parseInt(userInput);
                if (playerNumber > 1 && playerNumber < 6) {
                    startGame(playerNumber, config);
                } else {
                    throw new IllegalArgumentException();
                }
            } catch (IllegalArgumentException e1) {
                input.setText("Illegal input!");
            }
        });
    }

    /**
     * Start the main game using the input and config file.
     * @param playerNumber number of players
     * @param config the tile config file
     */
    private void startGame(int playerNumber, String config) {
        parentFrame.dispose();
        parentFrame = null;
        SwingUtilities.invokeLater(() -> {
            TileImages images = new TileImages();
            if (images.imageLoadSuccessful()) {
                Carcassonne game = new Carcassonne(playerNumber, config);
                JOptionPane.showMessageDialog(null, "Have fun!");
                game.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null, "Image loading error!");
            }
        });
    }
}
