package fr.ai.game.programming.game.elements;

import fr.ai.game.programming.game.GameStatus;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Board {

    public static final int PLAYER_HOLES = 8;
    public static final int INITIAL_SEEDS_PER_COLOR = 2;
    public static final int TOTAL_HOLES = 16;

    @Getter
    private final List<Seed>[] holes;
    @Getter
    private int player1Seeds;
    @Getter
    private int player2Seeds;
    @Getter
    private int currentPlayer;

    public Board() {
        holes = new List[TOTAL_HOLES];
        for (int i = 0; i < TOTAL_HOLES; i++) {
            holes[i] = new ArrayList<>();
            for (int j = 0; j < INITIAL_SEEDS_PER_COLOR; j++) {
                holes[i].add(new Seed(Seed.Color.BLUE));
                holes[i].add(new Seed(Seed.Color.RED));
            }
        }
        this.player1Seeds = 0;
        this.player2Seeds = 0;

        Random random = new Random();
        this.currentPlayer = random.nextBoolean() ? 1 : 2;
    }

    public boolean hasSeeds(int hole, Seed.Color seedColor) {
        return holes[hole].stream().anyMatch(seed -> seed.getColor() == seedColor);
    }

    public List<Seed> getSeedsInHole(int index) {
        return holes[index];
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
    public GameStatus checkGameStatus() {
        int totalSeedsOnBoard = 0;

        for (List<Seed> hole : holes) {
            totalSeedsOnBoard += hole.size();
        }

        // Check if Player 1 has won
        if (player1Seeds >= 33) {
            return new GameStatus(true, "Player 1", "More than 32 seeds collected");
        }

        // Check if Player 2 has won
        if (player2Seeds >= 33) {
            return new GameStatus(true, "Player 2", "More than 32 seeds collected");
        }

        // Check for a draw
        if (player1Seeds == 32 && player2Seeds == 32) {
            return new GameStatus(true, "Draw", "Both players collected 32 seeds");
        }

        // Check for insufficient seeds on the board
        if (totalSeedsOnBoard < 8) {
            String winner = player1Seeds > player2Seeds ? "Player 1" :
                    player1Seeds < player2Seeds ? "Player 2" : "Draw";
            return new GameStatus(true, winner, "Less than 8 seeds remaining");
        }

        // Check for valid moves for current player
        boolean hasValidMoveForPlayer1 = false;
        boolean hasValidMoveForPlayer2 = false;

        // Get the correct holes for each player
        int[] player1Holes = getPlayerHoles(1);
        int[] player2Holes = getPlayerHoles(2);

        // Check if player 1 has a valid move
        for (int i : player1Holes) {
            if (!holes[i].isEmpty()) {
                hasValidMoveForPlayer1 = true;
                break;  // No need to check further if one valid move is found
            }
        }

        // Check if player 2 has a valid move
        for (int i : player2Holes) {
            if (!holes[i].isEmpty()) {
                hasValidMoveForPlayer2 = true;
                break;  // No need to check further if one valid move is found
            }
        }

        // If the current player has no valid moves
        if ((currentPlayer == 1 && !hasValidMoveForPlayer1) || (currentPlayer == 2 && !hasValidMoveForPlayer2)) {
            captureRemainingSeeds(currentPlayer == 1 ? 2 : 1);  // Capture the remaining seeds of the opponent
            String winner = player1Seeds > player2Seeds ? "Player 1" :
                    player1Seeds < player2Seeds ? "Player 2" : "Draw";
            return new GameStatus(true, winner, "No valid moves left");
        }

        return new GameStatus(false, null, null); // Game is not over
    }

    public boolean isWinningState(int player) {
        int opponent = (player == 1) ? 2 : 1;

        // Check if the opponent has no legal moves left
        for (int hole : getPlayerHoles(opponent)) {
            for (Seed.Color color : Seed.Color.values()) {
                if (hasSeeds(hole, color)) {
                    return false; // Opponent still has a valid move
                }
            }
        }

        // Opponent cannot make a move, so the player wins
        return true;
    }

    /**
     * Sows seeds from the given hole in the specified color.
     */
    public void sowSeeds(int hole, Seed.Color seedColor) {
        if (hole < 0 || hole >= TOTAL_HOLES || holes[hole].isEmpty() || !hasSeeds(hole, seedColor)) {
            System.out.println(hole + " " + holes[hole] + " " + !hasSeeds(hole, seedColor));
            throw new IllegalArgumentException("Invalid move!");
        }

        if (hole % 2 == 1 && currentPlayer == 1) {
            throw new IllegalArgumentException("Player 1 cannot sow seeds from Player 2's holes!");
        } else if (hole % 2 == 0 && currentPlayer == 2) {
            throw new IllegalArgumentException("Player 2 cannot sow seeds from Player 1's holes!");
        }

        List<Seed> seedsToSow = takeSeedsFromHole(hole, seedColor);

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

        Iterator<Seed> iterator = holes[index].iterator();

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
            holes[pos].add(seeds.remove(0));
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
            holes[oppositeHole].add(seeds.remove(0));
            oppositeHole = (oppositeHole + 2) % TOTAL_HOLES;
        }
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
            List<Seed> seedsInHole = holes[lastHole];
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
                player1Seeds += holes[i].size();
                holes[i].clear();
            }
        } else {
            for (int i = PLAYER_HOLES; i < TOTAL_HOLES; i++) {
                player2Seeds += holes[i].size();
                holes[i].clear();
            }
        }
    }

    /**
     * Evaluates the board state based on the heuristic. The heuristic evaluates the difference between Player 2's and
     * Player 1's seeds.
     * @return the heuristic value
     */
    public int evaluateBoard() {
        if (isWinningState(1)) {
            return Integer.MAX_VALUE; // Winning state for Player 1
        }
        if (isWinningState(2)) {
            return Integer.MIN_VALUE; // Winning state for Player 2
        }

        // Neutral evaluation: Score based on seed counts
        return getPlayer1Seeds() - getPlayer2Seeds();
    }

    public Board copy() {
        Board copy = new Board();
        for (int i = 0; i < TOTAL_HOLES; i++) {
            copy.holes[i].clear();
            copy.holes[i].addAll(holes[i]);
        }
        copy.player1Seeds = player1Seeds;
        copy.player2Seeds = player2Seeds;
        copy.currentPlayer = currentPlayer;
        return copy;
    }

    /**
     * Get the holes belonging to a player.
     * @param player the player (1 or 2)
     * @return an array of hole indices belonging to the player
     */
    private int[] getPlayerHoles(int player) {
        return (player == 1)
                ? new int[]{0, 2, 4, 6, 8, 10, 12, 14}
                : new int[]{1, 3, 5, 7, 9, 11, 13, 15};
    }
}
