package fr.ai.game.programming.common;

/**
 * AI player for the Awale game.
 */
public class AIPlayer implements Player {
    private final AIManager aiManager;

    public AIPlayer(AIManager aiManager) {
        this.aiManager = aiManager;
    }

    @Override
    public void makeMove(AwaleBoard awaleBoard) {
        System.out.println("AI is making a move...");

        int currentPlayerId = awaleBoard.getCurrentPlayer();
        int aiMove;

        if (currentPlayerId == 1) {
            aiMove = aiManager.findRandomMove(1);
        } else {
            aiMove = aiManager.findBestMove(2);
        }
        System.out.println("AI move: " + aiMove);
        awaleBoard.sowSeeds(aiMove);
    }
}
