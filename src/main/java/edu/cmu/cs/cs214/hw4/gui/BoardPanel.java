package edu.cmu.cs.cs214.hw4.gui;

import edu.cmu.cs.cs214.hw4.core.Board;
import edu.cmu.cs.cs214.hw4.core.Feature;
import edu.cmu.cs.cs214.hw4.core.Game;
import edu.cmu.cs.cs214.hw4.core.GameChangeListener;
import edu.cmu.cs.cs214.hw4.core.Tile;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;

/**
 * The panel for placing tiles.
 */
public class BoardPanel extends JPanel implements GameChangeListener {
    private static final int TILE_SIZE = 90; //90 pixels for a tile
    private final Game game; //the game instance
    private final Board board; // the board instance in the game
    private final HashMap<Integer, Color> colorMap; //a map match users and the colors to represent the user
    private final TileButton[][] grid; //a grid to represent the tiles
    private final ImageIcon[][] tileWithoutMeeple; //a buffer to store images where
    private final int gridSize; //the size of the grid

    public BoardPanel(Game game, HashMap<Integer, Color> colorMap) {
        this.game = game;
        game.addGameChangeListener(this);
        this.board = game.getBoard();
        gridSize = board.getGridSize();
        grid = new TileButton[gridSize][gridSize];
        tileWithoutMeeple = new ImageIcon[gridSize][gridSize];
        this.colorMap = colorMap;
        add(createBoardPanel(), BorderLayout.CENTER);
    }

    /**
     * Create the panel of the board.
     *
     * @return a JScrollPane just created
     */
    public JScrollPane createBoardPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(gridSize, gridSize));

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                //use a grid of jbutton to represent possible locations. This is more accurate when clicking
                grid[row][col] = new TileButton();
                grid[row][col].setXY(row, col);

                if (row != gridSize / 2 || col != gridSize / 2) {
                    //if not the middle button, make it invisible
                    grid[row][col].setVisible(false);
                } else {
                    //set the center to the initial tile
                    BufferedImage buttonIcon = TileImages.getImageById(game.getDeck().getInitTileId());
                    ImageIcon img = new ImageIcon(buttonIcon);
                    grid[row][col].setIcon(img);
                    tileWithoutMeeple[row][col] = img;
                }

                grid[row][col].setPreferredSize(new Dimension(TILE_SIZE, TILE_SIZE));
                panel.add(grid[row][col]);
            }
        }

        //set the board size according to screen resolution and direct the scroll bar to middle
        panel.setPreferredSize(new Dimension(TILE_SIZE * gridSize, TILE_SIZE * gridSize));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        JScrollPane jsp = new JScrollPane(panel);
        jsp.setViewportView(panel);
        jsp.setPreferredSize(new Dimension((int) (screenSize.getWidth() * 0.7), (int) (screenSize.getHeight() * 0.9)));
        jsp.getVerticalScrollBar().setValue(TILE_SIZE * gridSize / 2 - (int) (screenSize.getHeight() * 0.45));
        jsp.getHorizontalScrollBar().setValue(TILE_SIZE * gridSize / 2 - (int) (screenSize.getWidth() * 0.35));
        jsp.getVerticalScrollBar().setUnitIncrement(15);
        jsp.getHorizontalScrollBar().setUnitIncrement(15);

        return jsp;
    }

    @Override
    public void newRound() {
        Tile t = game.getCurrentTile();
        List<int[]> availableLocations = board.getPossibleLocations(t);

        if (availableLocations.size() == 0) {
            //discard tile if the tile cannot be placed anywhere
            game.discardTile();
        } else {
            for (int[] location : availableLocations) {
                // Show all possible locations to place on the board
                TileButton button = grid[location[0]][location[1]];
                button.setEnabled(true);
                button.setVisible(true);

                //an image of a check mark indicates the tile can be placed here
                ImageIcon availableIcon = new ImageIcon(TileImages.availableIcon());
                button.setIcon(availableIcon);
                button.setBorderPainted(false);

                button.addActionListener(e -> {
                    // get the button location on the grid
                    int[] userChoice = new int[]{button.getXValue(), button.getYValue()};

                    // test if this choice is legit
                    if (!board.tileValidation(userChoice, t)) {
                        JOptionPane.showMessageDialog(null, "Not legit. Consider rotate tile first!");
                    } else {
                        //set tile in the core and add the tile to the feature list
                        board.setTile(userChoice, t);
                        game.addToFeature(t);

                        //set the image of the tile and cache another copy of the image for future use
                        BufferedImage bi = TileImages.getImageById(t.getId());
                        assert bi != null;
                        ImageIcon ii = new ImageIcon(TileImages.rotateClockwise(bi, t.getRotationTimes()));
                        button.setIcon(ii);
                        tileWithoutMeeple[userChoice[0]][userChoice[1]] = ii;

                        //for those locations not chosen, remove the listener and make the button invisible
                        for (int[] al : availableLocations) {
                            if (al[0] != userChoice[0] || al[1] != userChoice[1]) {
                                grid[al[0]][al[1]].setVisible(false);
                            }
                            for (ActionListener listener : grid[al[0]][al[1]].getActionListeners()) {
                                grid[al[0]][al[1]].removeActionListener(listener);
                            }
                        }
                        JOptionPane.showMessageDialog(null, "Tile placed! Place Meeple now!");

                        //use observe pattern here to notify
                        game.notifyTilePlaced();
                    }
                });
            }
        }

    }

    @Override
    public void meeplePlaced() {
        // get the info about the tile and the location of the meeple
        Tile t = game.getCurrentTile();
        String meepleLocation = game.getCurrentMeepleLocation();

        // get the color represents the player and draw the meeple
        Color color = colorMap.get(game.getCurrentPlayer());
        BufferedImage image = TileImages.getImageById(t.getId());
        image = TileImages.rotateClockwise(image, t.getRotationTimes());
        switch (meepleLocation) {
            case "l":
                image = TileImages.withCircle(image, color, 12, 45, 8);
                break;
            case "r":
                image = TileImages.withCircle(image, color, 78, 45, 8);
                break;
            case "u":
                image = TileImages.withCircle(image, color, 45, 12, 8);
                break;
            case "d":
                image = TileImages.withCircle(image, color, 45, 78, 8);
                break;
            case "c":
                image = TileImages.withCircle(image, color, 45, 45, 8);
                break;
            default:
                image = null;
        }
        if (image != null) {
            grid[t.getXCoordinate()][t.getYCoordinate()].setIcon(new ImageIcon(image));
        }
    }

    @Override
    public void featureFinished(Feature feature) {
        // get the list of tiles which consist the feature
        List<Tile> tiles = feature.getTiles();
        for (Tile t : tiles) {
            // remove the meeple, if the tile belongs to the feature and the meeple on the tile is a part of the feature
            if (t.getFeatureMeepleBelongsTo() == feature.getUniqueId()) {
                int x = t.getXCoordinate();
                int y = t.getYCoordinate();
                grid[x][y].setIcon(tileWithoutMeeple[x][y]);
            }
        }
    }

    @Override
    public void gameEnds() {
        //do nothing
    }

    @Override
    public void tilePlaced() {
        //do nothing
    }
}
