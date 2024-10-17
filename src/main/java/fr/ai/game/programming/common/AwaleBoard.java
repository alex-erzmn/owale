package fr.ai.game.programming.common;

import lombok.Getter;

import java.util.Random;

/**
 * Represents the Awale board and its state.
 */
public class AwaleBoard {

    public static final int PLAYER_HOLES = 6;
    private static final int SEEDS_PER_HOLE = 4;
    public static final int TOTAL_HOLES = 12;
    public boolean isGameOver = false;
    /**
     * The board state array. Each element represents the number of seeds in a hole. The first 6 elements represent
     * Player 2's holes, and the last 6 elements represent Player 1's holes.
     */
    @Getter
    private final int[] board; // Board state array
    @Getter
    private int player1Seeds; // Human seeds
    @Getter
    private int player2Seeds; // AI seeds
    @Getter
    private int currentPlayer; // Track current player: 1 (Human), 2 (AI)

    public AwaleBoard() {
        this.board = new int[TOTAL_HOLES];
        for (int i = 0; i < TOTAL_HOLES; i++) {
            board[i] = SEEDS_PER_HOLE; // Each hole starts with 4 seeds
        }
        this.player1Seeds = 0;
        this.player2Seeds = 0;

        Random random = new Random();
        this.currentPlayer = random.nextBoolean() ? 1 : 2;
    }

    /**
     * Makes a move by sowing seeds from the selected hole. The seeds are sown in a counter-clockwise direction.
     * The method also captures seeds based on the game rules.
     * @param hole the hole from which to sow seeds
     */
    public void sowSeeds(int hole) {
        if (hole < 0 || hole >= TOTAL_HOLES || board[hole] <= 0) {
            throw new IllegalArgumentException("Invalid move!");
        }

        int seedsToSow = board[hole];
        board[hole] = 0;

        int pos = hole;
        while (seedsToSow > 0) {
            pos = (pos + 1) % TOTAL_HOLES;
            if (pos == hole) {
                continue; // Skip the hole where the last seed was sown
            }
            board[pos]++;
            seedsToSow--;
        }

        captureSeeds(currentPlayer, pos);
    }

    /**
     * Captures seeds based on the game rules. The method captures seeds in the opponent's row if the last seed is sown
     * in a hole with 2 or 3 seeds. The captured seeds are added to the player's score.
     * @param player the current player
     * @param lastHole the last hole where a seed was sown
     */
    public void captureSeeds(int player, int lastHole) {
        int capturedSeeds = 0;

        // Capture seeds in the opponent's row
        while ((player == 1 && lastHole >= 0 && lastHole < PLAYER_HOLES) || (player == 2 && lastHole >= PLAYER_HOLES)) {
            if (board[lastHole] == 2 || board[lastHole] == 3) {
                capturedSeeds += board[lastHole];
                board[lastHole] = 0;
            } else {
                break;
            }
            lastHole--;
        }

        // Update seeds accordingly
        if (player == 1) {
            player1Seeds += capturedSeeds;
        } else {
            player2Seeds += capturedSeeds;
        }
    }

    /**
     * Switches the current player.
     */
    public void switchPlayer() {
        currentPlayer = (currentPlayer == 1) ? 2 : 1;
    }

    /**
     * Checks if the game is over. The game is over if the current player has 0 or 1 seeds in all holes or if the board
     * state is such that no player cannot make a move.
     * @return true if the game is over, false otherwise
     */
    public boolean checkGameOver() {
        boolean isCurrentPlayerEmpty = true; // Checks if the current player has 0 or 1 seeds in all holes

        // Determine the indices for the current player
        int startIndex = (currentPlayer == 1) ? PLAYER_HOLES : 0; // Player 1's holes start from NUM_HOLES
        int endIndex = (currentPlayer == 1) ? TOTAL_HOLES : PLAYER_HOLES; // Player 1's holes end at TOTAL_HOLES

        // Check the current player's holes
        for (int i = startIndex; i < endIndex; i++) {
            if (board[i] > 1) { // If the current player has more than 1 seed in any hole
                isCurrentPlayerEmpty = false;
                break; // No need to check further, the current player can still play
            }
        }

        // Determine if the game is over
        isGameOver = isCurrentPlayerEmpty;
        return isGameOver;
    }

    /**
     * Evaluates the board state based on the heuristic. The heuristic evaluates the difference between Player 2's and
     * Player 1's seeds.
     * @return the heuristic value
     * TODO: This method is incorrect.
     */
    public int evaluateBoard() {
        int player1FinalSeeds = 0;
        int player2FinalSeeds = 0;

        // Count seeds for both players
        for (int i = 0; i < AwaleBoard.PLAYER_HOLES; i++) {
            player2FinalSeeds += board[i];
        }
        for (int i = AwaleBoard.PLAYER_HOLES; i < AwaleBoard.TOTAL_HOLES; i++) {
            player1FinalSeeds += board[i];
        }

        // The heuristic evaluates the difference between Player 2's and Player 1's seeds
        return player2FinalSeeds - player1FinalSeeds;
    }
}
