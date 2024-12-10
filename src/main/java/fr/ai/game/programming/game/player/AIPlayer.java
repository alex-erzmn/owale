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

        int currentPlayerId = board.getCurrentPlayer();
        Move aiMove;

        if (currentPlayerId == 1) {
            aiMove = aiManager.findBestMove(1);
        } else {
            aiMove = aiManager.findBestMove(2);
        }
        int oneBasedHole = aiMove.hole() + 1;
        System.out.println("Player " + currentPlayerId + " chose to sow " + aiMove.color() + " seeds from hole " + oneBasedHole + ".");
        board.sowSeeds(aiMove.hole(), aiMove.color());
    }
}
