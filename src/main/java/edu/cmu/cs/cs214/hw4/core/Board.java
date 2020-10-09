package edu.cmu.cs.cs214.hw4.core;

import edu.cmu.cs.cs214.hw4.core.segments.Segment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The class to represent the board of the game which the players place the tile on.
 * It has three attributes: the size of the grid (will be different according to the number of the tiles, but will make
 * sure the space for the tiles will be enough for all possible combinations). Although most of the space is not used,
 * it will be easier to implement and the total memory consumption is rather tiny considering the memory size for
 * modern computers.
 * the grid for putting tiles on (a 2-D array) and a list of coordinates which stores all the neighbouring locations
 * which are adjacent to existing tiles.
 */
public class Board {
    private final int gridSize;
    private final Tile[][] grid;
    private final List<int[]> allNeighbours;

    Board(int tileNumber) {
        //initialize the grid and the list
        gridSize = 2 * tileNumber + 1;
        grid = new Tile[gridSize][gridSize];
        allNeighbours = new ArrayList<>();
    }

    /**
     * Return all legit locations where a tile can be placed in the grid.
     *
     * @param t the tile need to be placed on the grid
     * @return a list of coordinated where the tile can be place legally.
     */
    public List<int[]> getPossibleLocations(Tile t) {
        List<int[]> results = new ArrayList<>();
        int rotateCounter = 0;
        for (int[] location : allNeighbours) {
            //for all available adjacent locations on the grid
            if (grid[location[0]][location[1]] == null) {
                for (int i = 0; i < 4; i++) {
                    if (tileValidation(location, t)) {
                        //add to the list if all the edges satisfies requirements
                        boolean duplicate = false;
                        for (int[] result : results) {
                            if (result[0] == location[0] && result[1] == location[1]) {
                                duplicate = true;
                                break;
                            }
                        }
                        if (!duplicate) {
                            results.add(location);
                        }
                        break;
                    } else {
                        t.rotateClockwiseOnce();
                        rotateCounter++;
                    }
                }
            }
        }
        rotateCounter = 4 - rotateCounter % 4;
        for (int i = 0; i < rotateCounter; i++) {
            t.rotateClockwiseOnce();
        }
        return results;
    }

    /**
     * Validate whether a tile can be placed in a specific location.
     *
     * @param location an int array whose first two elements represent the x and y coordinates.
     * @param t        the tile needs to be validated
     * @return a boolean value which indicates if the tile can be placed at the location
     */
    public boolean tileValidation(int[] location, Tile t) {
        int x = location[0];
        int y = location[1];
        if (grid[x][y] != null) {
            System.out.println("Location " + x + ", " + y + " already occupied!");
            return false;
        } else {
            Tile leftTile = grid[x][y - 1];
            Tile rightTile = grid[x][y + 1];
            Tile upperTile = grid[x - 1][y];
            Tile lowerTile = grid[x + 1][y];
            if (leftTile == null & rightTile == null && upperTile == null && lowerTile == null) {
                System.out.println("Location must be adjacent!");
                return false;
            }
            // test if the edges matches with the neighbours
            boolean leftNotPass = leftTile != null && !leftTile.getRight().sameType(t.getLeft());
            boolean rightNotPass = rightTile != null && !rightTile.getLeft().sameType(t.getRight());
            boolean upNotPass = upperTile != null && !upperTile.getDown().sameType(t.getUp());
            boolean downNotPass = lowerTile != null && !lowerTile.getUp().sameType(t.getDown());
            return !(leftNotPass || rightNotPass || upNotPass || downNotPass);
        }
    }

    /**
     * Set the initial tile of the game at the middle of the grid.
     *
     * @param t The first tile of the game
     */
    public void setFirstTile(Tile t) {
        t.setXCoordinate(gridSize / 2);
        t.setYCoordinate(gridSize / 2);
        setTile(new int[]{gridSize / 2, gridSize / 2}, t);
    }

    /**
     * Set a tile to a specific location.
     * Must valid it using tileValidation() function before calling this function.
     *
     * @param location the coordinates of the desired location
     * @param t        the tile to be placed
     */
    public void setTile(int[] location, Tile t) {
        grid[location[0]][location[1]] = t;
        t.setXCoordinate(location[0]);
        t.setYCoordinate(location[1]);
        System.out.println("Tile placed!");
        // add all the adjacent locations to the list
        allNeighbours.add(new int[]{location[0] - 1, location[1]});
        allNeighbours.add(new int[]{location[0] + 1, location[1]});
        allNeighbours.add(new int[]{location[0], location[1] - 1});
        allNeighbours.add(new int[]{location[0], location[1] + 1});
    }

    /**
     * Get the adjacent segment of a specific segment.
     *
     * @param t The tile which the segment belongs to.
     * @param s The segment
     * @return the neighbouring segment if exist, null if does not exist, the segment it self if it is at the
     * center of the tile.
     */
    public Segment getNeighbourSegment(Tile t, Segment s) {
        if (s == t.getLeft() && grid[t.getXCoordinate()][t.getYCoordinate() - 1] != null) {
            return grid[t.getXCoordinate()][t.getYCoordinate() - 1].getRight();
        }
        if (s == t.getRight() && grid[t.getXCoordinate()][t.getYCoordinate() + 1] != null) {
            return grid[t.getXCoordinate()][t.getYCoordinate() + 1].getLeft();
        }
        if (s == t.getUp() && grid[t.getXCoordinate() - 1][t.getYCoordinate()] != null) {
            return grid[t.getXCoordinate() - 1][t.getYCoordinate()].getDown();
        }
        if (s == t.getDown() && grid[t.getXCoordinate() + 1][t.getYCoordinate()] != null) {
            return grid[t.getXCoordinate() + 1][t.getYCoordinate()].getUp();
        }
        if (s == t.getCenter()) {
            return s;
        }
        return null;
    }

    /**
     * Get the number of the tiles around the current tile.
     * Usually it will be used for calculating the score of monastery.
     *
     * @param tile the tile in the center which needs to be calculated.
     * @return number of the tiles around the current tile in a 3*3 grid (including the tile itself)
     */
    public int numberOfTilesAround(Tile tile) {
        int result = 0;
        int x = tile.getXCoordinate();
        int y = tile.getYCoordinate();
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if (grid[i][j] != null) {
                    result++;
                }
            }
        }
        return result;
    }

    public int getGridSize() {
        return gridSize;
    }

    @Override
    public String toString() {
        return "Board with " + "a square grid, Size=" + gridSize + "*" + gridSize;
    }

    // two board are the same when the size of them are the same
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Board board = (Board) o;
        return gridSize == board.gridSize;
    }

    @Override
    public int hashCode() {
        return Objects.hash(gridSize);
    }
}
