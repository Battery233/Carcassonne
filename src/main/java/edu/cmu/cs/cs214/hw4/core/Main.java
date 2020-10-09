package edu.cmu.cs.cs214.hw4.core;

/**
 * The main entrance of the game.
 */
public class Main {
    /**
     * The main method.
     *
     * @param args args[0]: player number, args[1]: config file location.
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            throw new IllegalArgumentException("Not enough args!");
        } else {
            System.out.println("Start game! Player number = " + args[0]);
            int player = args[0].charAt(0) - '0';
            if (player > 5 || player < 2) {
                throw new IllegalArgumentException("Player number error!");
            } else {
                Game game = new Game(player, args[1]);
                game.init();
                game.commandLinePlay();
                System.out.println("The game finished!");
            }
        }
    }
}
