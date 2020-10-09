package edu.cmu.cs.cs214.hw4.gui;

import edu.cmu.cs.cs214.hw4.core.Feature;
import edu.cmu.cs.cs214.hw4.core.Game;
import edu.cmu.cs.cs214.hw4.core.GameChangeListener;
import edu.cmu.cs.cs214.hw4.core.Player;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The main control panel contains the buttons for game operation and the score board.
 */
public class ControlPanel extends JPanel implements GameChangeListener {
    private final Game game;

    // the map of user id and color for the user
    private final HashMap<Integer, Color> colorMap;

    private final JLabel currentPlayerLabel;

    private JPanel rotationPanel;
    // the image for the tile to be placed
    private JLabel nextTile;
    private JButton rotateButton;

    private JPanel meeplePanel;
    private JButton skipPlaceMeeple;
    private JButton placeMeepleLeft;
    private JButton placeMeepleright;
    private JButton placeMeepleUp;
    private JButton placeMeepleDown;
    private JButton placeMeepleMiddle;

    private JPanel scorePanel;
    //an array of user score labels
    private JLabel[] scores;

    // data structure for finding out winners at the end game
    private int highestScore;
    private final List<Integer> winners;

    public ControlPanel(Game game, HashMap<Integer, Color> colorMap) {
        this.game = game;
        this.colorMap = colorMap;

        //set highest score to -1 since game not finished
        highestScore = -1;
        winners = new ArrayList<>();

        game.addGameChangeListener(this);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(Box.createRigidArea(new Dimension(0, 20)));
        currentPlayerLabel = new JLabel();
        add(currentPlayerLabel);

        add(Box.createRigidArea(new Dimension(0, 20)));
        initRotationPanel();
        add(rotationPanel);

        add(Box.createRigidArea(new Dimension(0, 20)));
        initMeeplePanel();
        add(meeplePanel);

        add(Box.createRigidArea(new Dimension(0, 20)));
        initScores();
        add(scorePanel);
    }

    /**
     * Initialize the tile rotation panel
     */
    private void initRotationPanel() {
        rotationPanel = new JPanel();
        rotationPanel.setMaximumSize(new Dimension(100, 160));
        rotationPanel.setLayout(new BoxLayout(rotationPanel, BoxLayout.Y_AXIS));

        nextTile = new JLabel();
        nextTile.setSize(new Dimension(90, 90));

        rotateButton = new JButton("Rotate Tile");
        rotateButton.setSize(new Dimension(90, 50));

        rotationPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        rotationPanel.add(rotateButton);
    }

    /**
     * Initialize the meeple placing panel
     */
    private void initMeeplePanel() {
        meeplePanel = new JPanel();
        meeplePanel.setLayout(new BoxLayout(meeplePanel, BoxLayout.Y_AXIS));
        meeplePanel.add(new JLabel("Meeple options:"));

        // add listeners for all 6 meeple options: left, right. upper, down, center, skip placing
        skipPlaceMeeple = new JButton("Skip meeple");
        meeplePanel.add(skipPlaceMeeple);
        skipPlaceMeeple.addActionListener(e -> doAfterMeeplePlaced());

        placeMeepleLeft = new JButton(" place Left ");
        placeMeepleLeft.addActionListener(e -> {
            // ask the game to test if the meeple can be placed here
            boolean result = game.placeMeepleUsingCommand(game.getCurrentPlayer(), "l");
            if (!result) {
                JOptionPane.showMessageDialog(null, "Cannot place it at left!");
                placeMeepleLeft.setEnabled(false);
            } else {
                //meeple placed successfully
                doAfterMeeplePlaced();
            }
        });
        meeplePanel.add(placeMeepleLeft);

        placeMeepleright = new JButton("place right ");
        placeMeepleright.addActionListener(e -> {
            boolean result = game.placeMeepleUsingCommand(game.getCurrentPlayer(), "r");
            if (!result) {
                JOptionPane.showMessageDialog(null, "Cannot place it at right!");
                placeMeepleright.setEnabled(false);
            } else {
                doAfterMeeplePlaced();
            }
        });
        meeplePanel.add(placeMeepleright);

        placeMeepleUp = new JButton("  place Up  ");
        placeMeepleUp.addActionListener(e -> {
            boolean result = game.placeMeepleUsingCommand(game.getCurrentPlayer(), "u");
            if (!result) {
                JOptionPane.showMessageDialog(null, "Cannot place it at upper!");
                placeMeepleUp.setEnabled(false);
            } else {
                doAfterMeeplePlaced();
            }
        });
        meeplePanel.add(placeMeepleUp);

        placeMeepleDown = new JButton(" place Down ");
        placeMeepleDown.addActionListener(e -> {
            boolean result = game.placeMeepleUsingCommand(game.getCurrentPlayer(), "d");
            if (!result) {
                JOptionPane.showMessageDialog(null, "Cannot place it at lower position!");
                placeMeepleDown.setEnabled(false);
            } else {
                doAfterMeeplePlaced();
            }
        });
        meeplePanel.add(placeMeepleDown);

        placeMeepleMiddle = new JButton("place Middle");
        placeMeepleMiddle.addActionListener(e -> {
            boolean result = game.placeMeepleUsingCommand(game.getCurrentPlayer(), "c");
            if (!result) {
                JOptionPane.showMessageDialog(null, "Cannot place it at center!");
                placeMeepleMiddle.setEnabled(false);
            } else {
                doAfterMeeplePlaced();
            }
        });
        meeplePanel.add(placeMeepleMiddle);
    }

    /**
     * Things needs to be done when a meeple if placed
     */
    private void doAfterMeeplePlaced() {
        //update the game status
        game.updateScoreInGame();
        game.nextPlayer();
        game.getNewTile();

        //goto next round or end of the game depending on if any tile is left in deck
        if (game.getCurrentTile() != null) {
            game.notifyNewRound();
        } else {
            game.notifyGameEnds();
        }
    }

    /**
     * Initialize the score board panel.
     */
    private void initScores() {
        scorePanel = new JPanel();
        scorePanel.setLayout(new BoxLayout(scorePanel, BoxLayout.Y_AXIS));
        JLabel scoreboardText = new JLabel("~~~ScoreBoard~~~");
        scoreboardText.setFont(new Font("Serif", Font.BOLD, 20));
        scorePanel.add(scoreboardText);

        //add one JLabel for each player
        scores = new JLabel[game.getPlayerNumber()];
        for (int i = 0; i < scores.length; i++) {
            scorePanel.add(Box.createRigidArea(new Dimension(0, 20)));
            scores[i] = new JLabel();
            scorePanel.add(scores[i]);
        }
        //load the score
        updateScores();
    }

    /**
     * Update the score text.
     * Show current play score and meeple number.
     * Indicate the winner(s) if the game finishes
     */
    public void updateScores() {
        for (int i = 0; i < game.getPlayerNumber(); i++) {
            int score = game.getPlayerById(i).getScore();
            int meeplesLeft = game.getPlayerById(i).meeplesLeft();
            String scoreText = "Player " + (i + 1) + ": Score = " + score + "!";
            String meepleText = " , " + meeplesLeft + " Meeple(s) left!";
            if (highestScore > -1) {
                if (highestScore == score) {
                    scores[i].setText(scoreText + " WINNER!");
                    winners.add(i + 1);
                } else {
                    scores[i].setText(scoreText);
                }
            } else {
                scores[i].setText(scoreText + meepleText);
            }
            scores[i].setForeground(colorMap.get(i));
        }
    }

    @Override
    public void newRound() {
        updateScores();
        //remove the old rotation button listeners
        for (ActionListener al : rotateButton.getActionListeners()) {
            rotateButton.removeActionListener(al);
        }
        currentPlayerLabel.setText("Tiles to be placed: " + (game.getDeck().tilesRemain() + 1) + ", Current player: player " + (game.getCurrentPlayer() + 1));

        if (nextTile != null) {
            //remove the tile image from the last round
            rotationPanel.remove(nextTile);
        }

        //disable meeple operations when the tile is not placed
        skipPlaceMeeple.setEnabled(false);
        setMeeplePlacingButtons(false);
        rotateButton.setEnabled(true);

        //load tile image for this round
        nextTile = new JLabel();
        BufferedImage imgBuffer = TileImages.getImageById(game.getCurrentTile().getId());
        assert imgBuffer != null;
        ImageIcon img = new ImageIcon(imgBuffer);
        nextTile.setIcon(img);
        rotationPanel.add(nextTile);

        //add listener for tile rotation
        rotateButton.addActionListener(e -> {
            game.getCurrentTile().rotateClockwiseOnce();
            nextTile.setIcon(new ImageIcon(TileImages.rotateClockwise(imgBuffer, game.getCurrentTile().getRotationTimes())));
        });
    }

    @Override
    public void tilePlaced() {
        //change the text
        currentPlayerLabel.setText("Waiting player " + (game.getCurrentPlayer() + 1) + " to choose meeple option!");

        //enable the meeple operations
        rotateButton.setEnabled(false);
        skipPlaceMeeple.setEnabled(true);

        //let the player to choose if has meeple left
        int playerId = game.getCurrentPlayer();
        Player currentPlayer = game.getPlayerById(playerId);
        if (currentPlayer.meeplesLeft() > 0) {
            setMeeplePlacingButtons(true);
        }
    }

    @Override
    public void meeplePlaced() {
        updateScores();
    }

    @Override
    public void featureFinished(Feature feature) {
        updateScores();
    }

    @Override
    public void gameEnds() {
        //calculate the highest score
        highestScore = game.calculateFinalScore();
        updateScores();

        //disable all operations
        setMeeplePlacingButtons(false);
        skipPlaceMeeple.setEnabled(false);
        rotateButton.setEnabled(false);
        nextTile.setVisible(false);
        currentPlayerLabel.setText("Game ended!");

        //make a pop up windows to indicate the winners
        StringBuilder sb = new StringBuilder();
        sb.append("Winner(s):\n");
        for (int i : winners) {
            sb.append("Player ").append(i).append(" -> Score: ").append(highestScore).append('\n');
        }
        JOptionPane.showMessageDialog(null, sb.toString());
    }

    /**
     * Set all five meeple location buttons
     *
     * @param enabled the status to set
     */
    private void setMeeplePlacingButtons(boolean enabled) {
        placeMeepleLeft.setEnabled(enabled);
        placeMeepleright.setEnabled(enabled);
        placeMeepleUp.setEnabled(enabled);
        placeMeepleDown.setEnabled(enabled);
        placeMeepleMiddle.setEnabled(enabled);
    }
}
