package fr.ai.game.programming.game.player;

import fr.ai.game.programming.game.elements.Board;

/**
 * AI player for the Awale game.
 */
public class AIPlayer implements Player {
    protected final AIManager aiManager;

    public AIPlayer(AIManager aiManager) {
        this.aiManager = aiManager;
    }

    @Override
    public void makeMove(Board board) {
        System.out.println("AI is making a move...");

        // Start timing
        long startTime = System.nanoTime();

        Move aiMove = aiManager.findMove();

        // End timing
        long endTime = System.nanoTime();

        // Calculate elapsed time in milliseconds
        long elapsedTime = (endTime - startTime) / 1_000_000; // Convert nanoseconds to milliseconds

        int oneBasedHole = aiMove.hole() + 1;
        int currentPlayerId = board.getCurrentPlayer();
        System.out.println("Player " + currentPlayerId + " chose to sow " + aiMove.color() + " seeds from hole " + oneBasedHole + ". (" + oneBasedHole + aiMove.color().toString().charAt(0) + ")");
        System.out.println("AI move computation time: " + elapsedTime + " ms");

        board.sowSeeds(aiMove.hole(), aiMove.color());
    }

}
