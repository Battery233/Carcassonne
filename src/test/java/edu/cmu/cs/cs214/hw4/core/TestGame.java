package edu.cmu.cs.cs214.hw4.core;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TestGame {
    Game game;

    @Test
    public void testGameInitializationNotHavingExceptions() {
        game = new Game(3, "src/main/resources/initTile.json");
        game.commandLinePlay();
    }

    @Test
    public void testGameRound() {
        //a test which will go through a whole game process with 4 tiles in total with asserts to test internal states of the game
        game = new Game(3, "src/main/resources/gameRoundTests.json");
        game.init();
        assertEquals(3, game.getPlayers().length);
        assertEquals("Board with a square grid, Size=9*9", game.getBoard().toString());
        assertEquals(1, game.getCityFeatures().size());
        assertEquals(1, game.getRoadFeatures().size());
        int currentPlayer = 0;
        Tile currentTile = game.getCurrentTile();
        //round counter
        int counter = -1;
        while (currentTile != null) {
            counter++;
            switch (counter) { //set input manually
                case 0:
                    System.setIn(new ByteArrayInputStream("2 4 5".getBytes(StandardCharsets.UTF_8)));
                    break;
                case 1:
                    System.setIn(new ByteArrayInputStream("2 5 4".getBytes(StandardCharsets.UTF_8)));
                    break;
                case 2:
                    System.setIn(new ByteArrayInputStream("0 5 3".getBytes(StandardCharsets.UTF_8)));
                    break;
                default:
                    return;
            }

            game.printScores();
            List<int[]> availableLocations = game.getBoard().getPossibleLocations(currentTile);

            //test if the number of available locations are correct
            switch (counter) {
                case 0:
                    assertEquals(4, availableLocations.size());
                    break;
                case 1:
                    assertEquals(6, availableLocations.size());
                    break;
                case 2:
                    assertEquals(7, availableLocations.size());
                    break;
                default:
                    return;
            }

            if (availableLocations.size() != 0) {
                int[] choiceOfPlayer = game.askNextUserPlaceTile(currentPlayer, availableLocations);
                game.getBoard().setTile(choiceOfPlayer, currentTile);
                game.addToFeature(currentTile);
                if (game.getPlayers()[currentPlayer].meeplesLeft() > 0) {
                    switch (counter) {
                        case 0:
                            System.setIn(new ByteArrayInputStream("l".getBytes(StandardCharsets.UTF_8)));
                            break;
                        case 1:
                            System.setIn(new ByteArrayInputStream("c".getBytes(StandardCharsets.UTF_8)));
                            break;
                        case 2:
                            System.setIn(new ByteArrayInputStream("r".getBytes(StandardCharsets.UTF_8)));
                            break;
                        default:
                            return;
                    }
                    game.askUserPlaceMeeple(currentPlayer, currentTile);
                }
                game.updateScoreInGame();
                currentPlayer = (currentPlayer + 1) % game.getPlayers().length;
            }
            currentTile = game.getDeck().getTile();
            game.setCurrentTile(currentTile);
        }
        game.calculateFinalScore();
        //test the scores afterwards
        assertEquals(4, game.getPlayers()[0].getScore());
        assertEquals(2, game.getPlayers()[1].getScore());
        assertEquals(4, game.getPlayers()[2].getScore());
        game.gameResult();
    }

    @Test
    public void testPrintingAndOverriddenFunctions() {
        game = new Game(3, "src/main/resources/initTile.json");
        game.printScores();
        assertEquals("A Carcassonne game with 3 players, have fun!", game.toString());
        Game game2 = new Game(3, "src/main/resources/initTile.json");
        assertNotEquals(game, game2);
        assertEquals(game.hashCode(), game.hashCode());
        assertNotEquals(game.hashCode(), game2.hashCode());
    }
}
