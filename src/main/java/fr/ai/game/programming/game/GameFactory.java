package fr.ai.game.programming.game;

import fr.ai.game.programming.game.elements.Board;
import fr.ai.game.programming.game.player.*;

/**
 * Factory class for creating Awale games.
 */
public class GameFactory {

    private GameFactory() {
        // Prevent instantiation
    }

    public static Game createAwaleGame(GameMode gameMode) {
        switch (gameMode) {
            case PLAYER_VS_AI_LOCAL -> {
                Board board = new Board();
                HumanPlayer player1 = new HumanPlayer();
                AIPlayer player2 = new AIPlayer(new AIManagerTest());
                return new Game(board, player1, player2);
            }
            case AI_VS_PLAYER_LOCAL -> {
                Board board = new Board();
                AIPlayer player1 = new AIPlayer(new AIManagerTest());
                HumanPlayer player2 = new HumanPlayer();
                return new Game(board, player1, player2);
            }
            case AI_VS_AI_LOCAL -> {
                Board board = new Board();
                AIPlayer player1 = new AIPlayer(new AIManagerNew());
                AIPlayer player2 = new AIPlayer(new AIManagerOld());
                return new Game(board, player1, player2);
            }
            default -> throw new IllegalArgumentException("Unsupported game mode: " + gameMode);
        }
    }
}




