package fr.ai.game.programming.common;

/**
 * Interface for a player in the Awale game.
 */
public interface Player {

    /**
     * Makes a move on the given board.
     *
     * @param board the board on which to make the move
     */
    void makeMove(AwaleBoard board);
}