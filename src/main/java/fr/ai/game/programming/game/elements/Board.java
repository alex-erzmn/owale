package fr.ai.game.programming.game.elements;

import fr.ai.game.programming.game.GameStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Board {

    public static final int INITIAL_SEEDS_PER_COLOR = 2;
    public static final int TOTAL_HOLES = 16;
    @Setter
    private int turns = 0;

    private final int[][] holes = new int[TOTAL_HOLES][2]; // 16 holes, 2 seed types (blue, red)
    private int player1Seeds;
    private int player2Seeds;
    private int currentPlayer;

    public Board() {
        // Initialize each hole with the initial number of seeds per color
        for (int i = 0; i < TOTAL_HOLES; i++) {
            holes[i][0] = INITIAL_SEEDS_PER_COLOR; // Blue seeds
            holes[i][1] = INITIAL_SEEDS_PER_COLOR; // Red seeds
        }

        this.player1Seeds = 0;
        this.player2Seeds = 0;
        this.currentPlayer = 1; // Player 1 starts the game
    }

    /**
     * Check if a hole has seeds of a specific color.
     * @param holeIndex the index of the hole (0 to 15)
     * @param seedColor the color of the seeds (BLUE or RED)
     * @return
     */
    public boolean hasSeeds(int holeIndex, SeedColor seedColor) {
        if (seedColor == SeedColor.BLUE) {
            return holes[holeIndex][0] > 0;
        } else {
            return holes[holeIndex][1] > 0;
        }
    }

    /**
     * Get the total number of seeds in a hole.
     * @param holeIndex the index of the hole (0 to 15)
     * @return
     */
    public int getSeedsInHole(int holeIndex) {
        return holes[holeIndex][0] + holes[holeIndex][1];
    }

    /**
     * Get the number of seeds in a hole only of a specific color.
     * @param holeIndex the index of the hole (0 to 15)
     * @param seedColor the color of the seeds (BLUE or RED)
     * @return
     */
    public int getSeedsInHole(int holeIndex, SeedColor seedColor) {
        if (seedColor == SeedColor.BLUE) {
            return holes[holeIndex][0];
        } else {
            return holes[holeIndex][1];
        }
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

        for (int[] hole : holes) {
            for (int seeds : hole) {
                totalSeedsOnBoard += seeds;
            }
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
            if (getSeedsInHole(i) != 0) {
                hasValidMoveForPlayer1 = true;
                break;  // No need to check further if one valid move is found
            }
        }

        // Check if player 2 has a valid move
        for (int i : player2Holes) {
            if (getSeedsInHole(i) != 0) {
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

    /**
     * Sows seeds from the given hole in the specified color.
     */
    public void sowSeeds(int hole, SeedColor seedColor) {
        /*System.out.println("this is the sowSeeds call [DEBUG Board.sowSeeds] hole=" + hole 
        + ", currentPlayer=" + currentPlayer 
        + ", color=" + seedColor);*/
        if (hole < 0 || hole >= TOTAL_HOLES || getSeedsInHole(hole) ==0 || !hasSeeds(hole, seedColor)) {
            throw new IllegalArgumentException("Invalid move! No " + seedColor + " seeds in hole " + (hole + 1) + ". Choose another color or hole.");
        }
        if (hole % 2 == 1 && currentPlayer == 1) {
            System.out.println("DEBUG: hole=" + hole + ", currentPlayer=1 => throwing exception");
            throw new IllegalArgumentException("Player 1 cannot sow seeds...");
        } else if (hole % 2 == 0 && currentPlayer == 2) {
            System.out.println("DEBUG: hole=" + hole + ", currentPlayer=2 => throwing exception");
            throw new IllegalArgumentException("Player 2 cannot sow seeds...");
        }
        

        int seedsToSow = takeSeedsFromHole(hole, seedColor);

        int lastHole = hole;
        if (seedColor == SeedColor.BLUE) {
            lastHole = sowBlueSeeds(hole, seedsToSow);
        } else if (seedColor == SeedColor.RED) {
            lastHole = sowRedSeeds(hole, seedsToSow);
        }

        captureSeeds(currentPlayer, lastHole);
    }

    private int takeSeedsFromHole(int index, SeedColor seedColor) {
        int seedsToTake = 0;

        if (seedColor == SeedColor.BLUE) {
            seedsToTake = holes[index][0];
            holes[index][0] = 0;
        } else {
            seedsToTake = holes[index][1];
            holes[index][1] = 0;
        }
        return seedsToTake;
    }

    /**
     * Sows blue seeds in the holes starting from the given hole.
     * @param startingHole
     * @param seeds
     * @return the last hole where a seed was placed
     */
    private int sowBlueSeeds(int startingHole, int seeds) {
        int pos = startingHole;
        while (seeds != 0) {
            pos = (pos + 1) % TOTAL_HOLES;
            if (pos == startingHole) {
                continue;  // Skip the hole from which the seeds were taken
            }
            // Add one blue seed to the next hole
            holes[pos][0]++;
            seeds--;
        }
        return pos; // Return the last hole where a seed was placed
    }

    /**
     * Sows red seeds in the opposite holes starting from the given hole.
     * @param startingHole
     * @param seeds
     * @return the last hole where a seed was placed
     */
    private int sowRedSeeds(int startingHole, int seeds) {
        int oppositeHole = (startingHole + 1) % TOTAL_HOLES; // Track the current hole
        while (seeds != 0) {
            holes[oppositeHole][1]++;  // Add one red seed to the opposite hole
            seeds--;
            oppositeHole = (oppositeHole + 2) % TOTAL_HOLES;
        }
        return (oppositeHole - 2 + TOTAL_HOLES) % TOTAL_HOLES;
    }

    public void forceCurrentPlayer(int player) {
        if (player != 1 && player != 2) {
            throw new IllegalArgumentException("Invalid player: " + player);
        }
        this.currentPlayer = player;
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
            int seedsInHole = holes[lastHole][0] + holes[lastHole][1];

            // Capture if the hole has 2 or 3 seeds
            if (seedsInHole == 2 || seedsInHole == 3) {
                capturedSeeds += seedsInHole;
                holes[lastHole][0] = 0;
                holes[lastHole][1] = 0;
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
                player1Seeds += holes[i][0] + holes[i][1];
                holes[i][0] = 0;
                holes[i][1] = 0;
            }
        } else {
            for (int i = 0; i < TOTAL_HOLES; i++) {
                player2Seeds += holes[i][0] + holes[i][1];
                holes[i][0] = 0;
                holes[i][1] = 0;
            }
        }
    }

    /**
     * Evaluates the board state based on the heuristic. The heuristic evaluates the difference between Player 2's and
     * Player 1's seeds.
     * @return the heuristic value
     */
    public int evaluateBoard() {
        if (checkGameStatus().getWinner() == 1) {
            return Integer.MAX_VALUE; // Winning state for Player 1
        }
        if (checkGameStatus().getWinner() == 2) {
            return Integer.MIN_VALUE; // Winning state for Player 2
        }

        // Neutral evaluation: Score based on seed counts
        return getPlayer1Seeds() - getPlayer2Seeds();
    }



    /* ------------------------------------------ Methods for Simulation ------------------------------------------ */

    /**
     * Create a deep copy of the current board state.
     * @return a deep copy of the board
     */
    public Board copy() {
        Board copy = new Board();
        for (int i = 0; i < TOTAL_HOLES; i++) {
            copy.holes[i][0] = holes[i][0];
            copy.holes[i][1] = holes[i][1];
        }
        copy.player1Seeds = player1Seeds;
        copy.player2Seeds = player2Seeds;
        copy.currentPlayer = currentPlayer;
        return copy;
    }

    /**
     * Sow seeds for simulation purposes only. This method does not capture seeds but return the seeds theoretically captured.
     * @param hole          the hole index
     * @param seedColor     the seed color
     * @return              the number of seeds theoretically captured
     */
    public int sowSeedsForSimulation(int hole, SeedColor seedColor) {
        if (hole < 0 || hole >= TOTAL_HOLES || getSeedsInHole(hole) == 0 || !hasSeeds(hole, seedColor)) {
            System.out.println(hole);
            System.out.println(getSeedsInHole(hole));
            System.out.println(hasSeeds(hole, seedColor));
            throw new IllegalArgumentException("SIMULATION_ERROR: Invalid move! No " + seedColor + " seeds in hole " + (hole + 1) + ". Choose another color or hole.");
        }

        if (hole % 2 == 1 && currentPlayer == 1) {
            throw new IllegalArgumentException("SIMULATION_ERROR: Player 1 cannot sow seeds from Player 2's holes! Choose another hole.");
        } else if (hole % 2 == 0 && currentPlayer == 2) {
            throw new IllegalArgumentException("SIMULATION_ERROR: Player 2 cannot sow seeds from Player 1's holes! Choose another hole.");
        }

        int seedsToSow = takeSeedsFromHole(hole, seedColor);

        int lastHole = hole;
        if (seedColor == SeedColor.BLUE) {
            lastHole = sowBlueSeedsForSimulation(hole, seedsToSow);
        } else if (seedColor == SeedColor.RED) {
            lastHole = sowRedSeedsForSimulation(hole, seedsToSow);
        }

        return captureSeedsForSimulation(lastHole);
    }

    /**
     * Sows blue seeds in the holes starting from the given hole for simulation purposes only.
     * @param startingHole the starting hole index
     * @param seeds the number of seeds to sow
     * @return the last hole where a seed was placed
     */
    private int sowBlueSeedsForSimulation(int startingHole, int seeds) {
        int pos = startingHole;
        while (seeds != 0) {
            pos = (pos + 1) % TOTAL_HOLES;
            if (pos == startingHole) {
                continue;  // Skip the hole from which the seeds were taken
            }
            seeds--;
        }
        return pos; // Return the last hole where a seed was placed
    }

    /**
     * Sows red seeds in the opposite holes starting from the given hole for simulation purposes only.
     * @param startingHole the starting hole index
     * @param seeds the number of seeds to sow
     * @return the last hole where a seed was placed
     */
    private int sowRedSeedsForSimulation(int startingHole, int seeds) {
        int oppositeHole = (startingHole + 1) % TOTAL_HOLES; // Track the current hole
        while (seeds != 0) {
            seeds--;
            oppositeHole = (oppositeHole + 2) % TOTAL_HOLES;
        }
        return (oppositeHole - 2 + TOTAL_HOLES) % TOTAL_HOLES;
    }

    /**
     * Capture seeds for simulation purposes only. This method does not modify the board state but returns the seeds
     * @param lastHole the last hole where a seed was placed
     * @return the number of seeds theoretically captured
     */
    private int captureSeedsForSimulation(int lastHole) {
        int capturedSeeds = 0;

        // Move counter-clockwise to capture seeds
        while (true) {
            int seedsInHole = holes[lastHole][0] + holes[lastHole][1];

            // Capture if the hole has 2 or 3 seeds
            if (seedsInHole == 2 || seedsInHole == 3) {
                capturedSeeds += seedsInHole;
            } else {
                break;  // Stop capturing if the current hole doesn't have 2 or 3 seeds
            }

            // Move to the previous hole counter-clockwise
            lastHole = (lastHole - 1 + TOTAL_HOLES) % TOTAL_HOLES;
        }

        return capturedSeeds;
    }



    /* --------------------------------------- Methods for Console Printing --------------------------------------- */

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
            String formattedHole = formatHoleWithColor(i, holeSummary(i));
            System.out.print("    " + centerText(formattedHole, 15) + "    |");
        }
        System.out.println();

        // Print separator between rows
        printRowSeparator();

        // Print bottom row (holes 15 to 8 in reverse)
        System.out.print("|");
        for (int i = 15; i >= 8; i--) {
            String formattedHole = formatHoleWithColor(i, holeSummary(i));
            System.out.print("    " + centerText(formattedHole, 15) + "    |");
        }
        System.out.println();
        printRowSeparator();
        System.out.println();
    }

    /**
     * Generates a summary of the seeds in a hole with colored formatting.
     */
    private String holeSummary(int holeIndex) {
        int blueCount = holes[holeIndex][0];
        int redCount = holes[holeIndex][1];

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
    
    //this is a test by Yassin, AI logic shouldn't be here
    public int evaluateBoardHeuristic() {
        // 1) Check if game is over
        GameStatus status = checkGameStatus();
        if (status.isGameOver()) {
            // If P1 has won, big positive
            if (status.getWinner() == 1) {
                return +100000;
            }
            // If P2 has won, big negative
            else if (status.getWinner() == 2) {
                return -100000;
            }
            // Draw
            return 0;
        }

        // Basic difference from P1's perspective:
        int seedDiff = player1Seeds - player2Seeds;   // + => P1 leads, - => P2 leads
    
        // If you want to weigh seeds on board (physically in holes 1,3,5.. for P1 vs holes 2,4,6.. for P2):
        int seedsP1OnBoard = countSeedsOnBoard(1);
        int seedsP2OnBoard = countSeedsOnBoard(2);
        int boardDiff = seedsP1OnBoard - seedsP2OnBoard;

        int score = 10 * seedDiff + 2 * boardDiff;
    
        // 5) Hole-by-hole nuance
        int holeAdjust = holeByHoleEvaluation();
        score += holeAdjust;
    
        // 6) Debug prints

    
        return score;
    }
    
    
    private int holeByHoleEvaluation() {
        int adjustment = 0;
        int[] myHoles = getPlayerHoles(currentPlayer);
        for (int holeIndex : myHoles) {
            int totalSeeds = holes[holeIndex][0] + holes[holeIndex][1];
            if (totalSeeds == 1) {
                adjustment -= 2; 
            } else if (totalSeeds == 2 || totalSeeds == 3) {
                adjustment -= 3;
            }
            // Possibly more logic for 4+ seeds if relevant to your strategy
        }
        return adjustment;
    }
    
    private int countSeedsOnBoard(int player) {
        int[] playerHoles = getPlayerHoles(player);
        int total = 0;
        for (int holeIndex : playerHoles) {
            total += (holes[holeIndex][0] + holes[holeIndex][1]);
        }
        return total;
    }
    
}

