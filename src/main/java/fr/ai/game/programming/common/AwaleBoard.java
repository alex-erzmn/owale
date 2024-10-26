package fr.ai.game.programming.common;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class AwaleBoard {

    public static final int PLAYER_HOLES = 8;
    public static final int INITIAL_SEEDS_PER_COLOR = 2;
    public static final int TOTAL_HOLES = 16;
    public boolean isGameOver = false;

    @Getter
    private final List<Seed>[] board;
    @Getter
    private int player1Seeds;
    @Getter
    private int player2Seeds;
    @Getter
    private int currentPlayer;

    public AwaleBoard() {
        board = new List[TOTAL_HOLES];
        for (int i = 0; i < TOTAL_HOLES; i++) {
            board[i] = new ArrayList<>();
            for (int j = 0; j < INITIAL_SEEDS_PER_COLOR; j++) {
                board[i].add(new Seed(Seed.Color.BLUE));
                board[i].add(new Seed(Seed.Color.RED));
            }
        }
        this.player1Seeds = 0;
        this.player2Seeds = 0;

        Random random = new Random();
        this.currentPlayer = random.nextBoolean() ? 1 : 2;
    }

    public boolean hasSeeds(int hole, Seed.Color seedColor) {
        return board[hole].stream().anyMatch(seed -> seed.getColor() == seedColor);
    }

    public List<Seed> getSeedsInHole(int index) {
        return board[index];
    }

    /**
     * Switches the current player.
     */
    public void switchPlayer() {
        currentPlayer = (currentPlayer == 1) ? 2 : 1;
    }

    /**
     * Checks if the game is over. The game is over when:
     * - One player has captured 33 or more seeds,
     * - Both players have captured 32 seeds (draw),
     * - There are fewer than 8 seeds remaining on the board, or
     * - One player has no valid move anymore (0 seeds in each player's hole).
     * @return true if the game is over, false otherwise
     */
    public boolean checkGameOver() {
        int totalSeedsOnBoard = 0;

        // Calculate total seeds remaining on the board
        for (List<Seed> hole : board) {
            totalSeedsOnBoard += hole.size();
        }

        // Check for winning conditions
        if (player1Seeds >= 33) {
            isGameOver = true; // Player 1 wins
            return true;
        }
        if (player2Seeds >= 33) {
            isGameOver = true; // Player 2 wins
            return true;
        }

        // Check for draw condition
        if (player1Seeds == 32 && player2Seeds == 32) {
            isGameOver = true; // Draw
            return true;
        }

        // Check for fewer than 8 seeds remaining
        if (totalSeedsOnBoard < 8) {
            isGameOver = true; // Game over due to few seeds
            return true;
        }

        // Check for valid moves for current player
        boolean hasValidMoveForPlayer1 = false;
        boolean hasValidMoveForPlayer2 = false;

        // Check each hole for valid moves
        for (int i = 0; i < PLAYER_HOLES; i++) {
            if (!board[i].isEmpty()) { // Check if there's at least one seed in the hole
                hasValidMoveForPlayer1 = true;
            }
            if (!board[i + PLAYER_HOLES].isEmpty()) { // Check Player 2's holes
                hasValidMoveForPlayer2 = true;
            }
        }

        // If current player has no valid moves
        if ((currentPlayer == 1 && !hasValidMoveForPlayer1) || (currentPlayer == 2 && !hasValidMoveForPlayer2)) {
            isGameOver = true; // Current player has no valid moves
            captureRemainingSeeds(currentPlayer == 1 ? 2 : 1);
            return true;
        }

        // If none of the end conditions are met, return false
        isGameOver = false;
        return false;
    }

    /**
     * Sows seeds from the given hole in the specified color.
     */
    public void sowSeeds(int hole, Seed.Color seedColor) {
        if (hole < 0 || hole >= TOTAL_HOLES || board[hole].isEmpty() || !hasSeeds(hole, seedColor)) {
            System.out.println(hole + " " + board[hole] + " " + !hasSeeds(hole, seedColor));
            throw new IllegalArgumentException("Invalid move!");
        }

        if (hole % 2 == 1 && currentPlayer == 1) {
            throw new IllegalArgumentException("Player 1 cannot sow seeds from Player 2's holes!");
        } else if (hole % 2 == 0 && currentPlayer == 2) {
            throw new IllegalArgumentException("Player 2 cannot sow seeds from Player 1's holes!");
        }

        List<Seed> seedsToSow = takeSeedsFromHole(hole, seedColor);
        System.out.println("Seeds to sow: " + seedsToSow.size());

        int lastHole = hole;
        if (seedColor == Seed.Color.BLUE) {
            lastHole = sowBlueSeeds(hole, seedsToSow);
        } else if (seedColor == Seed.Color.RED) {
            lastHole = sowRedSeeds(hole, seedsToSow);
        }

        captureSeeds(currentPlayer, lastHole);
    }

    private List<Seed> takeSeedsFromHole(int index, Seed.Color seedColor) {
        List<Seed> seedsToTake = new ArrayList<>();

        Iterator<Seed> iterator = board[index].iterator();

        while (iterator.hasNext()) {
            Seed seed = iterator.next();
            if (seed.getColor() == seedColor) {
                seedsToTake.add(seed);
                iterator.remove();
            }
        }

        return seedsToTake;
    }

    /**
     * Sows blue seeds in the holes starting from the given hole.
     * @param startingHole
     * @param seeds
     * @return the last hole where a seed was placed
     */
    private int sowBlueSeeds(int startingHole, List<Seed> seeds) {
        int pos = startingHole;
        while (!seeds.isEmpty()) {
            pos = (pos + 1) % TOTAL_HOLES;
            if (pos == startingHole) {
                continue;  // Skip the hole from which the seeds were taken
            }
            // Add one blue seed to the next hole
            System.out.println(pos);
            board[pos].add(seeds.remove(0));
        }
        return pos; // Return the last hole where a seed was placed
    }

    /**
     * Sows red seeds in the opposite holes starting from the given hole.
     * @param startingHole
     * @param seeds
     * @return the last hole where a seed was placed
     */
    private int sowRedSeeds(int startingHole, List<Seed> seeds) {
        int oppositeHole = (startingHole + 1) % TOTAL_HOLES; // Track the current hole
        while (!seeds.isEmpty()) {
            System.out.println(oppositeHole);
            board[oppositeHole].add(seeds.remove(0));
            oppositeHole = (oppositeHole + 2) % TOTAL_HOLES;
        }
        System.out.println((oppositeHole - 2) % TOTAL_HOLES);
        return (oppositeHole - 2 + TOTAL_HOLES) % TOTAL_HOLES;
    }

    /**
     * Captures seeds based on the game rules. The method captures seeds in the opponent's row if the last seed is sown
     * in a hole with 2 or 3 seeds. The captured seeds are added to the player's score.
     * @param player the current player
     * @param lastHole the last hole where a seed was sown
     */
    private void captureSeeds(int player, int lastHole) {
        int capturedSeeds = 0;

        // Move counter-clockwise to capture seeds
        while (true) {
            List<Seed> seedsInHole = board[lastHole];
            int totalSeeds = seedsInHole.size();  // Count total seeds in the hole (both colors)

            // Capture if the hole has 2 or 3 seeds
            if (totalSeeds == 2 || totalSeeds == 3) {
                capturedSeeds += totalSeeds;
                seedsInHole.clear();  // Remove seeds after capturing
            } else {
                break;  // Stop capturing if the current hole doesn't have 2 or 3 seeds
            }

            // Move to the previous hole counter-clockwise
            lastHole = (lastHole - 1 + TOTAL_HOLES) % TOTAL_HOLES;
        }

        // Update seeds accordingly
        if (player == 1) {
            player1Seeds += capturedSeeds;
        } else {
            player2Seeds += capturedSeeds;
        }
    }

    private void captureRemainingSeeds(int player) {
        if (player == 1) {
            for (int i = 0; i < PLAYER_HOLES; i++) {
                player1Seeds += board[i].size();
                board[i].clear();
            }
        } else {
            for (int i = PLAYER_HOLES; i < TOTAL_HOLES; i++) {
                player2Seeds += board[i].size();
                board[i].clear();
            }
        }
    }

    /**
     * Evaluates the board state based on the heuristic. The heuristic evaluates the difference between Player 2's and
     * Player 1's seeds.
     * @return the heuristic value
     */
    public int evaluateBoard() {
        return player2Seeds - player1Seeds;
    }
}
