package fr.ai.game.programming.game.player;

import fr.ai.game.programming.game.elements.Board;

/**
 * Interface for AI managers that can find moves for the AI player using a specific algorithm.
 */
public interface AIManager {

    /**
     * Find the move for the AI player using the AIManagers current strategy.
     *
     * @return The move for the AI player.
     */
    Move findMove(Board board);
}
