package fr.ai.game.programming.game;

import lombok.Getter;

/**
 * @param winner "Player 1", "Player 2", or "Draw"
 * @param reason E.g., "More than 32 seeds", "No valid moves", etc.
 */
public record GameStatus(boolean isGameOver, @Getter int winner, @Getter String reason) {
}
