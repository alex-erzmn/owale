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

        Move aiMove = aiManager.findMove(board);

        int oneBasedHole = aiMove.hole() + 1;
        int currentPlayerId = board.getCurrentPlayer();
        System.out.println("Player " + currentPlayerId + " chose to sow " + aiMove.color() + " seeds from hole " + oneBasedHole + ". (" + oneBasedHole + aiMove.color().toString().charAt(0) + ")");

        board.sowSeeds(aiMove.hole(), aiMove.color());
    }

}
