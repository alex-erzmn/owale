package fr.ai.game.programming.game.elements;

import fr.ai.game.programming.game.GameStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Getter
public class Board {

    public static final int INITIAL_SEEDS_PER_COLOR = 2;
    public static final int TOTAL_HOLES = 16;
    @Setter
    private int turns = 0;

    private final List<Seed>[] holes;
    private int player1Seeds;
    private int player2Seeds;
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

        this.currentPlayer = 1; // Player 1 starts the game
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
            return new GameStatus(true, 1, "More than 32 seeds collected");
        }

        // Check if Player 2 has won
        if (player2Seeds >= 33) {
            return new GameStatus(true, 2, "More than 32 seeds collected");
        }

        // Check for a draw
        if (player1Seeds == 32 && player2Seeds == 32) {
            return new GameStatus(true, 0, "Both players collected 32 seeds");
        }

        // Check for insufficient seeds on the board
        if (totalSeedsOnBoard < 8) {
            int winner = player1Seeds > player2Seeds ? 1 :
                    player1Seeds < player2Seeds ? 2 : 0;
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
            int winner = currentPlayer == 1 ? 2 : 1;
            captureRemainingSeeds(winner);
            return new GameStatus(true, winner, "No valid moves left");
        }

        return new GameStatus(false, -1, null); // Game is not over
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
            throw new IllegalArgumentException("Invalid move! No " + seedColor + " seeds in hole " + (hole + 1) + ". Choose another color or hole.");
        }

        if (hole % 2 == 1 && currentPlayer == 1) {
            throw new IllegalArgumentException("Player 1 cannot sow seeds from Player 2's holes! Choose another hole.");
        } else if (hole % 2 == 0 && currentPlayer == 2) {
            throw new IllegalArgumentException("Player 2 cannot sow seeds from Player 1's holes! Choose another hole.");
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
            for (int i = 0; i < TOTAL_HOLES; i++) {
                player1Seeds += holes[i].size();
                holes[i].clear();
            }
        } else {
            for (int i = 0; i < TOTAL_HOLES; i++) {
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
    public int[] getPlayerHoles(int player) {
        return (player == 1)
                ? new int[]{0, 2, 4, 6, 8, 10, 12, 14}  // 1, 3, 5, 7, 9, 11, 13, 15
                : new int[]{1, 3, 5, 7, 9, 11, 13, 15}; // 2, 4, 6, 8, 10, 12, 14, 16
    }

    /**
     * Print the board layout in the console with a table-like grid and better spacing.
     */
    public void printBoardLayout() {
        System.out.println();
        final String RESET = "\u001B[0m";
        final String EVEN_COLOR = "\u001B[32m"; // Green for even numbers
        final String ODD_COLOR = "\u001B[33m"; // Yellow for odd numbers

        // Display seed counts for both players
        System.out.println(EVEN_COLOR + "Player 1 Seeds: " + RESET + this.getPlayer1Seeds() + " | "
                + ODD_COLOR + "Player 2 Seeds: " + RESET + this.getPlayer2Seeds());
        System.out.println();
        System.out.println("TURN: " + this.getTurns());
        System.out.println();

        // Print header for grid
        printRowSeparator();
        System.out.print("|");
        for (int i = 0; i < 8; i++) {
            String formattedHole = formatHoleWithColor(i, holeSummary(holes[i]));
            System.out.print("    " + centerText(formattedHole, 15) + "    |");
        }
        System.out.println();

        // Print separator between rows
        printRowSeparator();

        // Print bottom row (holes 15 to 8 in reverse)
        System.out.print("|");
        for (int i = 15; i >= 8; i--) {
            String formattedHole = formatHoleWithColor(i, holeSummary(holes[i]));
            System.out.print("    " + centerText(formattedHole, 15) + "    |");
        }
        System.out.println();
        printRowSeparator();
        System.out.println();
    }

    /**
     * Generates a summary of the seeds in a hole with colored formatting.
     */
    private String holeSummary(List<Seed> seeds) {
        int blueCount = (int) seeds.stream().filter(seed -> seed.getColor() == Seed.Color.BLUE).count();
        int redCount = (int) seeds.stream().filter(seed -> seed.getColor() == Seed.Color.RED).count();

        // Define ANSI colors
        final String RESET = "\u001B[0m";
        final String BLUE_SEED_COLOR = "\u001B[34m"; // Blue for B seeds
        final String RED_SEED_COLOR = "\u001B[31m";  // Red for R seeds
        final String GREY_COLOR = "\u001B[90m";      // Grey for 0 counts

        // Apply grey color if the count is 0
        String blueText = (blueCount > 0 ? BLUE_SEED_COLOR : GREY_COLOR) + blueCount + "B" + RESET;
        String redText = (redCount > 0 ? RED_SEED_COLOR : GREY_COLOR) + redCount + "R" + RESET;

        return blueText + " " + redText;
    }

    /**
     * Formats the hole summary with color based on the index (even or odd).
     */
    private String formatHoleWithColor(int holeIndex, String holeSummary) {
        // Define ANSI escape codes for colors
        final String RESET = "\u001B[0m"; // Reset color
        final String EVEN_COLOR = "\u001B[32m"; // Green for even numbers
        final String ODD_COLOR = "\u001B[33m"; // Yellow for odd numbers

        // Apply color to parentheses based on hole index (even or odd)
        return (holeIndex % 2 == 0)
                ? EVEN_COLOR + "(" + RESET + holeSummary + EVEN_COLOR + ")" + RESET
                : ODD_COLOR + "(" + RESET + holeSummary + ODD_COLOR + ")" + RESET;
    }

    /**
     * Prints a horizontal row separator for the grid.
     */
    private void printRowSeparator() {
        System.out.println("+---------------+---------------+---------------+---------------+---------------+---------------+---------------+---------------+");
    }

    /**
     * Centers the given text within a fixed width, padding with spaces as needed.
     */
    private String centerText(String text, int width) {
        if (width <= text.length()) {
            return text; // If the text is too long, return it as is
        }
        int padding = (width - text.length()) / 2;
        String paddingSpaces = " ".repeat(padding);
        return paddingSpaces + text + " ".repeat(width - text.length() - padding);
    }
}
