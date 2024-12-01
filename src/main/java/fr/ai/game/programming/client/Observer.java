package fr.ai.game.programming.client;

/**
 * Interface to observe a game
 * @implNote Observer pattern
 */
public interface Observer {

    /**
     * Update the game
     */
    void onBoardUpdated();

    /**
     * Enable player buttons
     */
    void onEnablePlayerButtons();

    /**
     * Game over
     */
    void onGameOver();
}
