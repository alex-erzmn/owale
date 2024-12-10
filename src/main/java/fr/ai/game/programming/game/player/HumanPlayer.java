package fr.ai.game.programming.game.player;

import fr.ai.game.programming.game.elements.Board;

/**
 * Human player for the Awale game.
 * @implNote This class is an empty placeholder for a human player.
 */
public class HumanPlayer implements Player {

    @Override
    public void makeMove(Board board) {
        System.out.print("Enter your move (e.g., '3B'): ");
    }
}