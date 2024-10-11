package fr.ai.game.programming.common;

import lombok.Getter;
import lombok.Setter;

public class AwaleBoard {

    public static final int NUM_HOLES = 6;
    private static final int SEEDS_PER_HOLE = 4;
    public static final int TOTAL_HOLES = 12;
    public boolean isGameOver = false;

    @Getter
    private final int[] board; // Board state array
    @Getter
    @Setter
    private int player1Seeds; // Human seeds
    @Getter
    @Setter
    private int player2Seeds; // AI seeds
    @Getter
    @Setter
    private int currentPlayer; // Track current player: 1 (Human), 2 (AI)

    public AwaleBoard() {
        this.board = new int[TOTAL_HOLES];
        for (int i = 0; i < TOTAL_HOLES; i++) {
            board[i] = SEEDS_PER_HOLE; // Each hole starts with 4 seeds
        }
        this.player1Seeds = 0;
        this.player2Seeds = 0;
        this.currentPlayer = 1; // Human starts first
    }

    // Sow seeds logic
    public void sowSeeds(int hole) {
        if (hole < 0 || hole >= TOTAL_HOLES || board[hole] <= 0) {
            System.out.println(hole);
            System.out.println(board[hole]);
            throw new IllegalArgumentException("Invalid move!");
        }

        int seedsToSow = board[hole];

        board[hole] = 0;

        int pos = hole;
        while (seedsToSow > 0) {
            pos = (pos + 1) % TOTAL_HOLES;
            if (pos == hole) {
                break; // Skip the hole where the last seed was sown
            }
            board[pos]++;
            seedsToSow--;
        }

        captureSeeds(currentPlayer, pos);
    }

    // Capture seeds based on the rules
    public void captureSeeds(int player, int lastHole) {
        int capturedSeeds = 0;

        // Capture seeds in the opponent's row
        while ((player == 1 && lastHole >= 0 && lastHole < NUM_HOLES) || (player == 2 && lastHole >= NUM_HOLES)) {
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

    public boolean checkGameOver() {
        boolean isCurrentPlayerEmpty = true; // Checks if the current player has 0 or 1 seeds in all holes

        // Determine the indices for the current player
        int startIndex = (currentPlayer == 1) ? NUM_HOLES : 0; // Player 1's holes start from NUM_HOLES
        int endIndex = (currentPlayer == 1) ? TOTAL_HOLES : NUM_HOLES; // Player 1's holes end at TOTAL_HOLES

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

    public void resetBoard() {
        for (int i = 0; i < TOTAL_HOLES; i++) {
            board[i] = SEEDS_PER_HOLE;
        }
        player1Seeds = 0;
        player2Seeds = 0;
        currentPlayer = 1;
        isGameOver = false;
    }
}
