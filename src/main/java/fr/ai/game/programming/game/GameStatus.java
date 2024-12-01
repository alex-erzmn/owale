package fr.ai.game.programming.game;

public class GameStatus {
    private final boolean isGameOver;
    private final String winner; // "Player 1", "Player 2", or "Draw"
    private final String reason; // E.g., "More than 32 seeds", "No valid moves", etc.

    public GameStatus(boolean isGameOver, String winner, String reason) {
        this.isGameOver = isGameOver;
        this.winner = winner;
        this.reason = reason;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public String getWinner() {
        return winner;
    }

    public String getReason() {
        return reason;
    }
}
