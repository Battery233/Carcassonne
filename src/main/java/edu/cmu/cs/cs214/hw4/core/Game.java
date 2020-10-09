package edu.cmu.cs.cs214.hw4.core;

import edu.cmu.cs.cs214.hw4.core.segments.Segment;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

/**
 * The major class to represent the game controller.
 * The game class will control the rounds of the game and process all logic tasks
 * It will process the operations between other classes so that others class will not
 * depend on each other but this class only.
 * <p>
 * A game should have these fields: players, a tile deck for unused tiles, a board to place tiles
 * (the board of the game should only be responsible for containing the tiles and calculating available locations.),
 * and lists of different features produced (features are concepts for represent game states
 * so it will be better to put features and feature processing methods here instead of the board class,
 * which can reduce the representational gap of the code).
 * <p>
 * Most of the functions are made package private for the purpose of testing.
 */
public class Game {
    //the list of the players
    private final Player[] players;
    private final TileDeck deck;
    private final Board board;
    private final List<Feature> monasteryFeatures;
    private final List<Feature> roadFeatures;
    private final List<Feature> cityFeatures;
    private final List<GameChangeListener> gameChangeListeners = new ArrayList<>();
    private Tile currentTile;
    private String currentMeepleLocation;
    private int currentPlayer = 0;
    private int featureId = 0;

    public Game(int playerNumber, String config) {
        //initialize players
        players = new Player[playerNumber];
        for (int i = 0; i < playerNumber; i++) {
            players[i] = new Player(i);
        }
        deck = new TileDeck(config);
        // set up the board. 1 is for the initial tile which is not included in the tile deck list.
        board = new Board(deck.tilesRemain() + 1);
        monasteryFeatures = new ArrayList<>();
        roadFeatures = new ArrayList<>();
        cityFeatures = new ArrayList<>();
    }

    /**
     * Initialize the game and place the first tile when the game starts
     */
    public void init() {
        //initialize the game board by setting the first tile
        currentTile = deck.getInitTile();
        board.setFirstTile(currentTile);
        addToFeature(currentTile);   // add the feature of first tiles in the feature lists.
        System.out.println("Init tile" + currentTile + " placed at {" + (deck.tilesRemain() + 1) + "," + (deck.tilesRemain() + 1) + "}");
        // while there are tiles left in the deck, play a game round
        getNewTile();
        notifyNewRound();
    }

    public void notifyNewRound() {
        for (GameChangeListener gcl : gameChangeListeners) {
            gcl.newRound();
        }
    }

    public void notifyTilePlaced() {
        for (GameChangeListener gcl : gameChangeListeners) {
            gcl.tilePlaced();
        }
    }

    public void notifyMeeplePlaced() {
        for (GameChangeListener gcl : gameChangeListeners) {
            gcl.meeplePlaced();
        }
    }

    public void notifyFeatureFinished(Feature feature) {
        for (GameChangeListener gcl : gameChangeListeners) {
            gcl.featureFinished(feature);
        }
    }

    public void notifyGameEnds() {
        for (GameChangeListener gcl : gameChangeListeners) {
            gcl.gameEnds();
        }
    }

    public void addGameChangeListener(GameChangeListener listener) {
        gameChangeListeners.add(listener);
    }

    public void nextPlayer() {
        currentPlayer = (currentPlayer + 1) % players.length; //go the the next player
    }

    public void getNewTile() {
        currentTile = deck.getTile();
    }

    public void discardTile() {
        if (deck.tilesRemain() > 0) {
            getNewTile();
            notifyNewRound();
        } else {
            notifyGameEnds();
        }
    }

    /**
     * The function to print the current scores for the players.
     */
    void printScores() {
        System.out.println("---Current score:---");
        for (int i = 0; i < players.length; i++) {
            System.out.println("Player " + (i + 1) + " score: " +
                    players[i].getScore() + ", " + players[i].meeplesLeft() + " meeples left!");
        }
    }

    /**
     * The function for updating the score of the game after one round.
     */
    public void updateScoreInGame() {
        updateMonasteryScoreInGame();
        updateRoadOrCityScoreInGame(cityFeatures, Terrain.City);
        updateRoadOrCityScoreInGame(roadFeatures, Terrain.Road);
    }

    /**
     * Go through the current monastery feature list and update the score if a feature is finished.
     */
    void updateMonasteryScoreInGame() {
        Feature feature;
        Iterator<Feature> iterator = monasteryFeatures.iterator();
        while (iterator.hasNext()) {
            feature = iterator.next();
            if (feature.hasMeeples()) {
                //at most 1 meeple in one Monastery feature
                //and there will be one and only one tile in the Tile list of Monastery feature
                Tile monasteryTile = feature.getTiles().get(0);
                if (board.numberOfTilesAround(monasteryTile) == 9) {
                    notifyFeatureFinished(feature);
                    int playerIndex = feature.getMeeples().get(0).meepleOwner();
                    players[playerIndex].returnMeeple(1);
                    players[playerIndex].addScore(9);
                    iterator.remove();
                }
            }
        }
    }

    /**
     * Go through the current feature list for city or roads and update the score if feature is finished.
     * This function can be reused for both city and road features
     *
     * @param features the feature list need to be updated
     * @param type     the type of the features in that list
     */
    void updateRoadOrCityScoreInGame(List<Feature> features, Terrain type) {
        Feature feature;
        Iterator<Feature> iterator = features.iterator();
        while (iterator.hasNext()) {
            feature = iterator.next();
            //only process the feature if the feature has a meeple
            if (feature.hasMeeples()) {
                boolean featureFinished = isRoadOrCityFinished(feature, type);
                if (featureFinished) {
                    notifyFeatureFinished(feature);
                    int score = 0;
                    if (type == Terrain.City) {
                        // if the feature is a city feature, calculate the number of coat of arms
                        for (Tile t : feature.getTiles()) {
                            if (t.hasCoatOfArms()) {
                                score += 2;
                            }
                        }
                        score += feature.getTiles().size() * 2;
                    } else if (type == Terrain.Road) {
                        score += feature.getTiles().size();
                    }
                    // find out who has the most meeples to get the score
                    List<Integer> playerIdAndMeepleNumber = playIdWithMostMeeplesAndMeepleNumber(feature.getMeeples());
                    addScoreAndReturnMeeple(playerIdAndMeepleNumber, score, feature);
                    iterator.remove();
                }
            }
        }
    }

    /**
     * The helper function to find out if a road or city feature is completed.
     *
     * @param feature the feature objected need to be tested
     * @param type    the type of the feature
     * @return the flag of whether the feature is finished.
     */
    boolean isRoadOrCityFinished(Feature feature, Terrain type) {
        for (Tile t : feature.getTiles()) {
            for (Segment s : t.edges()) {
                //A road or city feature is finished if and only if, all edge segments belong to this feature have
                // a neighbouring segment.
                if (feature.getSegments().contains(s) && s.type() == type && board.getNeighbourSegment(t, s) == null) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Add score to a list of players and return the meeple to them
     *
     * @param featureWinnerInformation A list of players to return meeples. The last element of the list
     *                                 should be the number of meeples to return.
     * @param score                    the score which should be added to the players.
     * @param feature                  the feature to operate
     */
    void addScoreAndReturnMeeple(List<Integer> featureWinnerInformation, int score, Feature feature) {
        int meepleNumber = featureWinnerInformation.get(featureWinnerInformation.size() - 1);
        featureWinnerInformation.remove(featureWinnerInformation.size() - 1);
        for (int i : featureWinnerInformation) {
            players[i].addScore(score);
            players[i].returnMeeple(meepleNumber);
        }
        //Return the meeples to the player who did not win the feature
        for (Meeple m : feature.getMeeples()) {
            if (!featureWinnerInformation.contains(m.meepleOwner()))
                players[m.meepleOwner()].returnMeeple(1);
        }
    }

    /**
     * Update the final score at the end of the game.
     *
     * @return return the highest score
     */
    public int calculateFinalScore() {
        updateMonasteryScoreAtEnd();
        updateRoadOrCityScoreAtEnd(cityFeatures, Terrain.City);
        updateRoadOrCityScoreAtEnd(roadFeatures, Terrain.Road);
        return gameResult(); // calculate and print the result of the game.
    }

    /**
     * Go through the current monastery feature list and update the score for the player.
     */
    void updateMonasteryScoreAtEnd() {
        for (Feature feature : monasteryFeatures) {
            if (feature.hasMeeples()) {
                Tile monasteryTile = feature.getTiles().get(0);
                int playerIndex = feature.getMeeples().get(0).meepleOwner();
                players[playerIndex].addScore(board.numberOfTilesAround(monasteryTile));
            }
        }
    }

    /**
     * Go through the feature list and update the score.
     *
     * @param features the list of features
     * @param type     the type of the feature list
     */
    void updateRoadOrCityScoreAtEnd(List<Feature> features, Terrain type) {
        for (Feature feature : features) {
            if (feature.hasMeeples()) {
                int score = 0;
                if (type == Terrain.City) {
                    for (Tile t : feature.getTiles()) {
                        if (t.hasCoatOfArms()) {
                            score++;
                        }
                    }
                }
                score += feature.getTiles().size();
                List<Integer> playerIdAndMeepleNumber = playIdWithMostMeeplesAndMeepleNumber(feature.getMeeples());
                addScoreAndReturnMeeple(playerIdAndMeepleNumber, score, feature);
            }
        }
    }

    /**
     * Calculate the user id who has the most meeples in a meeple list and the number of meeples
     *
     * @param meeples the list of meeples (which are placed on a single feature)
     * @return A list contains of user ids who has the most meeples and the number of meeples
     */
    List<Integer> playIdWithMostMeeplesAndMeepleNumber(List<Meeple> meeples) {
        List<Integer> result = new ArrayList<>();
        Map<Integer, Integer> meepleCounter = new HashMap<>();
        int max = 0;
        for (Meeple m : meeples) {
            int meepleNumber = meepleCounter.getOrDefault(m.meepleOwner(), 0) + 1;
            meepleCounter.put(m.meepleOwner(), meepleNumber);
            if (meepleNumber > max) {
                max = meepleNumber;
            }
        }
        for (Map.Entry<Integer, Integer> entry : meepleCounter.entrySet()) {
            if (entry.getValue() == max) {
                result.add(entry.getKey());
            }
        }
        result.add(max);
        return result;
    }

    /**
     * Calculate the result of the game, print the scores and print the winner(s).
     */
    int gameResult() {
        System.out.println("-----Game Ends!-----");
        int maxScore = 0;
        for (int i = 0; i < players.length; i++) {
            System.out.println("Player " + (i + 1) + " final score = " + players[i].getScore());
            if (maxScore < players[i].getScore()) {
                maxScore = players[i].getScore();
            }
        }
        System.out.println("Winner(s):");
        for (int i = 0; i < players.length; i++) {
            if (players[i].getScore() == maxScore) {
                System.out.println("Player " + (i + 1));
            }
        }
        return maxScore;
    }

    /**
     * Update the feature lists, according the tile which is just placed on the board.
     *
     * @param t The new tile
     */
    public void addToFeature(Tile t) {
        if (t.isMonastery() || t.isIntersection() ||
                (t.getCenter().type() == Terrain.Field && t.isseparatedCities())) {
            if (t.isMonastery()) {
                //Tile A, B
                Feature f = new MonasteryFeature(featureId++);
                f.addTile(t);
                f.addSegment(t.getCenter());
                monasteryFeatures.add(f);
            }
            //Tile H,I,E,L,W,X
            for (Segment s : t.edges()) {
                // the edge segment are not connected so it can call addSingleEdgeToFeatures() here
                addSingleEdgeToFeatures(t, s);
            }
        } else if (t.getCenter().type() == Terrain.Road) { // the center is a road but not an intersection
            //Tile D, J, K, O, P, U, V
            Feature localRoadFeature = new RoadFeature(featureId++); // the road feature of this single tiles
            List<Feature> roadFeatureList = new ArrayList<>(); //the list of the features which connect to this tile
            localRoadFeature.addTile(t);
            List<Segment> roadSegments = new ArrayList<>(); //segments related to this feature.
            roadSegments.add(t.getCenter());

            int citySegmentCounter = 0;
            List<Segment> citySegments = new ArrayList<>(); // list for possible city edges

            // add the edge segments.
            for (Segment s : t.edges()) {
                if (s.type() == Terrain.Road) {
                    roadSegments.add(s);
                    //test if this is connected to existing features
                    Feature existingFeature = getNeighbourFeatureUsingEdge(t, s);
                    if (existingFeature != null) {
                        roadFeatureList.add(existingFeature);
                        roadFeatures.remove(existingFeature);
                    }
                } else if (s.type() == Terrain.City) {
                    citySegmentCounter++;
                    citySegments.add(s);
                }
            }
            localRoadFeature.addSegment(roadSegments);
            roadFeatureList.add(localRoadFeature);
            // merge the feature with existing features.
            roadFeatures.add(mergeFeatures(roadFeatureList));

            if (citySegmentCounter == 1) { // if there is only one city segment among all edges
                addSingleEdgeToFeatures(t, citySegments.get(0));
            } else if (citySegmentCounter > 1) {
                //according to the rules of the game and the flag of "separatedCities", count must equals to two and
                //two city segment should be connected to each other!
                Feature localCityFeature = new CityFeature(featureId++);
                localCityFeature.addTile(t).addSegment(citySegments);
                List<Feature> cityFeatureList = new ArrayList<>();
                cityFeatureList.add(localCityFeature);
                for (Segment s : citySegments) {
                    Feature existingFeature = getNeighbourFeatureUsingEdge(t, s);
                    if (existingFeature != null) {
                        cityFeatureList.add(existingFeature);
                        cityFeatures.remove(existingFeature);
                    }
                }
                //merge the city feature, if exist.
                cityFeatures.add(mergeFeatures(cityFeatureList));
            }
        } else if (!t.isseparatedCities()) {
            Feature localFeature = new CityFeature(featureId++);
            localFeature.addTile(t);
            List<Feature> cityFeatureList = new ArrayList<>();
            if (t.getCenter().type() == Terrain.City) {
                //Tiles C F G Q R S T. If center==Field do not add (Tile M,N)
                localFeature.addSegment(t.getCenter());
            }
            for (Segment s : t.edges()) {
                if (s.type() == Terrain.City) {
                    localFeature.addSegment(s);
                    Feature existingFeature = getNeighbourFeatureUsingEdge(t, s);
                    if (existingFeature != null) {
                        cityFeatures.remove(existingFeature);
                        cityFeatureList.add(existingFeature);
                    }
                } else if (s.type() == Terrain.Road) {
                    addSingleEdgeToFeatures(t, s);
                }
            }
            cityFeatureList.add(localFeature);
            //add the connected city feature to the list.
            cityFeatures.add(mergeFeatures(cityFeatureList));
        } else {
            System.out.println("Illegal tiles found!");
        }
    }

    /**
     * Merge a list of features to a new feature.
     *
     * @param features the list contains features to be merged
     * @return the new feature after merging.
     */
    Feature mergeFeatures(List<Feature> features) {
        Feature newFeature;
        int fId;
        HashSet<Integer> oldFID = new HashSet<>();
        if (features.get(0).type() == Terrain.City) { //only city and road features can or need to be merged
            newFeature = new CityFeature(featureId++);
        } else {
            newFeature = new RoadFeature(featureId++);
        }
        fId = newFeature.getUniqueId();
        HashSet<Tile> tileSet = new HashSet<>();
        for (Feature f : features) {
            oldFID.add(f.getUniqueId());
            newFeature
                    .addSegment(f.getSegments())
                    .addMeeple(f.getMeeples());
            tileSet.addAll(f.getTiles());
        }
        newFeature.addTile(tileSet);
        for (Tile t : newFeature.getTiles()) {
            if (oldFID.contains(t.getFeatureMeepleBelongsTo())) {
                t.setFeatureMeepleBelongsTo(fId);
            }
        }
        return newFeature;
    }

    /**
     * Giving a segment object, find out if it belongs to a specific feature in a feature list.
     *
     * @param segment  the segment
     * @param features the list to retrieve
     * @return the feature object if the feature exist, null if the segment does not belongs to any feature (e.g. field)
     */
    public Feature getFeatureUsingSegment(Segment segment, List<Feature> features) {
        for (Feature f : features) {
            for (Segment s : f.getSegments()) {
                if (s == segment) {
                    return f;
                }
            }
        }
        return null;
    }

    /**
     * Find out the feature adjacent to a specific segment of a tile.
     *
     * @param t the tile
     * @param s the segment
     * @return the feature if exist, null if it does not exist.
     */
    Feature getNeighbourFeatureUsingEdge(Tile t, Segment s) {
        Segment neighbourS = board.getNeighbourSegment(t, s);
        List<Feature> featureList;
        if (neighbourS == null)
            return null;
        // the list to search for
        if (neighbourS.type() == Terrain.Road) {
            featureList = roadFeatures;
        } else {
            featureList = cityFeatures;
        }
        // if the neighbour edge belongs to an feature, it must be in the segment list of the feature
        for (Feature f : featureList) {
            if (f.getSegments().contains(neighbourS))
                return f;
        }
        return null;
    }

    /**
     * Add a single edge segment to existing feature or a new feature.
     *
     * @param tile    the tile which the segment belongs to
     * @param segment the segment itself
     */
    void addSingleEdgeToFeatures(Tile tile, Segment segment) {
        List<Feature> features;
        switch (segment.type()) {
            case Field:
                return;
            case City:
                features = cityFeatures;
                break;
            case Road:
                features = roadFeatures;
                break;
            default:
                //this should never happen
                features = new ArrayList<>();
        }
        Segment neighbour = board.getNeighbourSegment(tile, segment);
        if (neighbour == null) { // if no neighbour, create a new feature
            if (segment.type() == Terrain.Road) {
                features.add(new RoadFeature(featureId++).addTile(tile).addSegment(segment));
            } else if (segment.type() == Terrain.City) {
                features.add(new CityFeature(featureId++).addTile(tile).addSegment(segment));
            }
        } else { //find the existing feature and add this tile and segment to the feature.
            for (Feature f : features) {
                for (Segment s : f.getSegments()) {
                    if (s == neighbour) {//same object
                        f.addTile(tile);
                        f.addSegment(segment);
                        return;
                    }
                }
            }
        }
    }

    /**
     * Ask the user to give a location to set the current tile
     *
     * @param currentPlayer      the player id to place the tile
     * @param availableLocations a list of all possible locations the tile can be placed (for printing purpose only)
     */
    int[] askNextUserPlaceTile(int currentPlayer, List<int[]> availableLocations) {
        boolean validationResult = false;
        int[] playerChoice = null;
        while (!validationResult) { // ask the user to place the valid tile until the location is legit
            try {
                System.out.println("current tile: " + currentTile.toString());
                System.out.print("All possible locations: ");
                for (int[] i : availableLocations) {
                    System.out.print("{" + i[0] + ", " + i[1] + "} ");
                }

                System.out.println("\nInput how many times you want to rotate clockwise 90 degrees:");
                playerChoice = players[currentPlayer].chooseLocation(currentTile);

                //validate if the player's choice is legit
                validationResult = board.tileValidation(playerChoice, currentTile);

                if (!validationResult) {
                    System.out.println("Incorrect, retry!");
                }
            } catch (NumberFormatException e) {
                System.out.println("illegal input!");
            }
        }
        return playerChoice;
    }

    /**
     * Ask the user to give a valid segment to set the meeple at and add the meeple to the feature.
     *
     * @param currentPlayer the user id of the current player
     * @param t             the tile which the meeple can be placed on
     */
    void askUserPlaceMeeple(int currentPlayer, Tile t) {
        Segment s; // the segment where the meeple will be placed on. This segment will be used to find the feature.
        boolean validInput = false;
        while (!validInput) {
            System.out.println("Place meeple? n = dont, l = left, r = right, u = upper, d = down, c = center");
            Scanner scan = new Scanner(System.in, StandardCharsets.UTF_8);
            String command = scan.nextLine();
            boolean placedInCenter = false;
            switch (command) { //get user decision
                case "n":
                    System.out.println("No meeple placed!");
                    return;
                case "l":
                    s = t.getLeft();
                    break;
                case "r":
                    s = t.getRight();
                    break;
                case "u":
                    s = t.getUp();
                    break;
                case "d":
                    s = t.getDown();
                    break;
                case "c":
                    s = t.getCenter();
                    placedInCenter = true;
                    break;
                default:
                    continue;
            }

            Feature feature;
            switch (s.type()) { //test if the user can place meeple here
                case City:
                    feature = getFeatureUsingSegment(s, cityFeatures);
                    break;
                case Road:
                    if (t.isIntersection() && placedInCenter) {
                        System.out.println("Cannot place meeple at intersection!");
                        continue;
                    }
                    feature = getFeatureUsingSegment(s, roadFeatures);
                    break;
                case Monastery:
                    feature = getFeatureUsingSegment(s, monasteryFeatures);
                    break;
                case Field:
                    System.out.println("Cannot place meeple on the Field!");
                    continue;
                default:
                    System.out.println("This won't happen unless new rules are added to the game!");
                    return;
            }
            if (feature != null && feature.hasMeeples()) { // one feature can have at most one meeple except merged
                System.out.println("Cannot place meeple on a feature which already has meeple(s)! ");
            } else {
                Meeple m = players[currentPlayer].getMeeple();
                if (feature != null) {
                    feature.addMeeple(m);
                } else {
                    return;
                }
                validInput = true;
            }
        }
    }

    public boolean placeMeepleUsingCommand(int currentPlayer, String command) {
        Tile t = currentTile;
        Segment s; // the segment where the meeple will be placed on. This segment will be used to find the feature.
        boolean placedInCenter = false;
        switch (command) { //get user decision
            case "l":
                s = t.getLeft();
                break;
            case "r":
                s = t.getRight();
                break;
            case "u":
                s = t.getUp();
                break;
            case "d":
                s = t.getDown();
                break;
            case "c":
                s = t.getCenter();
                placedInCenter = true;
                break;
            default:
                s = null;
        }

        Feature feature;
        assert s != null;
        switch (s.type()) { //test if the user can place meeple here
            case City:
                feature = getFeatureUsingSegment(s, cityFeatures);
                break;
            case Road:
                if (t.isIntersection() && placedInCenter) {
                    return false;
                }
                feature = getFeatureUsingSegment(s, roadFeatures);
                break;
            case Monastery:
                feature = getFeatureUsingSegment(s, monasteryFeatures);
                break;
            default:
                return false;
        }
        if (feature != null && feature.hasMeeples()) { // one feature can have at most one meeple except merged
            return false;
        } else {
            Meeple m = players[currentPlayer].getMeeple();
            if (feature != null) {
                feature.addMeeple(m);
                currentTile.setFeatureMeepleBelongsTo(feature.getUniqueId());
                currentMeepleLocation = command;
                notifyMeeplePlaced();
                return true;
            } else {
                return false;
            }
        }
    }


    @Override
    public String toString() {
        return "A Carcassonne game with " + players.length + " players, have fun!";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return Arrays.equals(players, game.players) &&
                Objects.equals(deck, game.deck) &&
                Objects.equals(board, game.board) &&
                Objects.equals(monasteryFeatures, game.monasteryFeatures) &&
                Objects.equals(roadFeatures, game.roadFeatures) &&
                Objects.equals(cityFeatures, game.cityFeatures);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(deck, board, monasteryFeatures, roadFeatures, cityFeatures);
        result = 31 * result + Arrays.hashCode(players);
        return result;
    }

    /**
     * Package private getter for testing
     *
     * @return players
     */
    Player[] getPlayers() {
        return players;
    }

    public Player getPlayerById(int id) {
        if (id > players.length - 1) {
            return null;
        } else {
            return players[id];
        }
    }

    /**
     * Package private getter for testing
     *
     * @return deck
     */
    public TileDeck getDeck() {
        return deck;
    }

    /**
     * Package private getter for testing
     *
     * @return board
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Package private getter for testing
     *
     * @return roadFeatures
     */
    public List<Feature> getRoadFeatures() {
        return roadFeatures;
    }

    /**
     * Package private getter for testing
     *
     * @return cityFeatures
     */
    public List<Feature> getCityFeatures() {
        return cityFeatures;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public Tile getCurrentTile() {
        return currentTile;
    }

    public void setCurrentTile(Tile currentTile) {
        this.currentTile = currentTile;
    }

    public int getPlayerNumber() {
        return players.length;
    }

    public String getCurrentMeepleLocation() {
        return currentMeepleLocation;
    }

    /**
     * The main play methods which will start the game and control the rounds until the game ends.
     */
    public void commandLinePlay() {
        while (currentTile != null) {
            printScores(); //print current score information
            System.out.println("\n--- Next player: player " + (currentPlayer + 1) + " ---");
            // ask the board to give all possible locations where the tile can be placed
            // this is just for core testing purpose.
            // For the future Gui design, the user does not need to know the exactly locations.
            // Instead, the board just need to tell the game whether this tile should be discarded
            List<int[]> availableLocations = board.getPossibleLocations(currentTile);
            if (availableLocations.size() != 0) {
                // ask the user to operate on this tile and get a valid location for putting this tile
                int[] choiceOfPlayer = askNextUserPlaceTile(currentPlayer, availableLocations);
                board.setTile(choiceOfPlayer, currentTile);
                //calculate any new features created by the event of tile placing and update the feature list.
                addToFeature(currentTile);
                // ask the user to place a meeple if the user has meeples left.
                if (players[currentPlayer].meeplesLeft() > 0) {
                    askUserPlaceMeeple(currentPlayer, currentTile);
                }
                updateScoreInGame(); //update the score of the game
                nextPlayer();
            }
            getNewTile(); //get the next tile.
        }
        calculateFinalScore(); //update the score at the end of the game using the rules.
        notifyGameEnds();
    }
}