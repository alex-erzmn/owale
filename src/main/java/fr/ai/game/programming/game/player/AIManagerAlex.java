package fr.ai.game.programming.game.player;

import fr.ai.game.programming.game.elements.Board;
import fr.ai.game.programming.game.elements.SeedColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static fr.ai.game.programming.game.elements.Board.TOTAL_HOLES;

/**
 * AI manager for the Awale game. Implements the Minimax algorithm with Alpha-Beta pruning and random move selection.
 */
public class AIManagerAlex implements AIManager {
    private final Board board;
    private static final int INITIAL_DEPTH = 5; // Initial depth for Minimax algorithm
    private int currentDepth = INITIAL_DEPTH; // Initial depth for Minimax algorithm
    private static final int MAX_DEPTH = 20; // Maximum depth for Minimax algorithm

    public AIManagerAlex(Board board) {
        this.board = board;
    }

    public Move findMove() {
        return findBestMove();
    }

    /**
     * Find the best move for the player using the Minimax algorithm with Alpha-Beta pruning.
     * @return the best move which includes seed color and number of seeds
     */
    private Move findBestMove() {
        int player = board.getCurrentPlayer();
        // Define initial alpha and beta values
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        // Define a high-level utility variable to track the best move
        Move bestMove = findRandomMove(); // This is only workaround because there is an error which causes sometimes to not find a solution with minimax
        int bestValue = (player == 1) ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        // Generate a sorted list of all possible moves for the player
        List<Move> possibleMoves = getAllPossibleMoves(player, board);

        for (Move move : possibleMoves) {
            System.out.println(move);
        }

        optimizeDepth(possibleMoves.size());
        System.out.println(currentDepth);

        // Iterate through all holes to find the best move
        for (Move move : possibleMoves) {
                if (board.hasSeeds(move.hole(), move.color())) { // Check if seeds of this color exist in the hole
                    // Create a temporary board state for simulation
                    Board simulatedBoard = board.copy();
                    simulatedBoard.sowSeeds(move.hole(), move.color()); // Perform the move on the simulated board

                    // Calculate the utility of the move using the minimax algorithm
                    int moveValue = minimax(simulatedBoard, currentDepth, alpha, beta, player == 2);

                    // Update the best move if the current move has a better value
                    if ((player == 1 && moveValue > bestValue) || (player == 2 && moveValue < bestValue)) {
                        bestValue = moveValue;
                        bestMove = new Move(move.hole(), move.color());
                    }

                    // Update alpha or beta for pruning
                    if (player == 1) {
                        alpha = Math.max(alpha, bestValue);
                    } else {
                        beta = Math.min(beta, bestValue);
                    }

                    // Prune the search tree if the move cannot improve the current state
                    if (alpha >= beta) {
                        break;
                    }
                }
        }

        return bestMove;
    }

    /**
     * Minimax algorithm with Alpha-Beta pruning to evaluate the best move.
     * @param simulatedBoard the current state of the board
     * @param depth the depth of the recursion
     * @param alpha the alpha value for pruning
     * @param beta the beta value for pruning
     * @param isMaximizing true if the current player is maximizing their score
     * @return the utility value of the current board state
     */
    private int minimax(Board simulatedBoard, int depth, int alpha, int beta, boolean isMaximizing) {
        // Base case: Check if the game is over or if the search depth is reached
        if (depth == 0 || simulatedBoard.checkGameStatus().isGameOver()) {
            return simulatedBoard.evaluateBoard(); // Evaluate the utility of the board
        }



        if (isMaximizing) {
            int maxEval = Integer.MIN_VALUE;
            List<Move> possibleMoves = getAllPossibleMoves(2, simulatedBoard);
            for (Move move : possibleMoves) {
                    if (simulatedBoard.hasSeeds(move.hole(), move.color())) {
                        // Simulate the move
                        Board childBoard = simulatedBoard.copy();
                        childBoard.sowSeeds(move.hole(), move.color());
                        childBoard.switchPlayer();

                        // Check if this move is a winning move
                        if (childBoard.isWinningState(1)) {
                            return Integer.MAX_VALUE; // Immediate win for player 1
                        }

                        // Recur with the next player (minimizing)
                        int eval = minimax(childBoard, depth - 1, alpha, beta, false);
                        maxEval = Math.max(maxEval, eval);

                        // Update alpha and prune if necessary
                        alpha = Math.max(alpha, eval);
                        if (alpha >= beta) {
                            break;
                        }
                    }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            List<Move> possibleMoves = getAllPossibleMoves(1, simulatedBoard);
                for (Move move : possibleMoves) {
                    if (simulatedBoard.hasSeeds(move.hole(), move.color())) {
                        // Simulate the move
                        Board childBoard = simulatedBoard.copy();
                        childBoard.sowSeeds(move.hole(), move.color());
                        childBoard.switchPlayer();

                        // Check if this move is a winning move
                        if (childBoard.isWinningState(2)) {
                            return Integer.MIN_VALUE; // Immediate win for player 2
                        }

                        // Recur with the next player (maximizing)
                        int eval = minimax(childBoard, depth - 1, alpha, beta, true);
                        minEval = Math.min(minEval, eval);

                        // Update beta and prune if necessary
                        beta = Math.min(beta, eval);
                        if (alpha >= beta) {
                            break;
                        }
                    }
            }
            return minEval;
        }
    }

    /**
     * Find a random move for the player.
     * @return a Move with the chosen hole and seed color
     */
    private Move findRandomMove() {
        int player = board.getCurrentPlayer();

        // Get the range of holes for the current player
        int[] playerHoles = board.getPlayerHoles(player);

        // Continue searching until a valid hole and color are selected
        while (true) {
            // Pick a random hole index for the player
            int randomIndex = (int) (Math.random() * playerHoles.length);
            int randomHole = playerHoles[randomIndex];

            // Check the seed counts for each color in the selected hole
            int blueCount = board.getSeedsInHole(randomHole, SeedColor.BLUE);
            int redCount = board.getSeedsInHole(randomHole, SeedColor.RED);

            // Randomly decide which color to choose if both are available
            if (blueCount > 0 && redCount > 0) {
                SeedColor seedColor = Math.random() < 0.5 ? SeedColor.BLUE : SeedColor.RED;
                return new Move(randomHole, seedColor);
            } else if (blueCount > 0) {
                return new Move(randomHole, SeedColor.BLUE);
            } else if (redCount > 0) {
                return new Move(randomHole, SeedColor.RED);
            }
            // If no seeds are available in this hole, continue to another random selection
        }
    }


    /**
     * Optimize the depth of the Minimax algorithm based on the current game state.
     * The depth is increased every 6 turns to improve the AI performance.
     */
    private void optimizeDepth(int amountPossibleMoves) {
        if( amountPossibleMoves > 8) {
            currentDepth = INITIAL_DEPTH;
        } else if (amountPossibleMoves > 4) {
            currentDepth = INITIAL_DEPTH + 5;
        } else if (amountPossibleMoves > 3) {
            currentDepth = INITIAL_DEPTH + 10;
        } else if (amountPossibleMoves > 2) {
            currentDepth = INITIAL_DEPTH + 15;
        } else {
            currentDepth = INITIAL_DEPTH + 20;
        }
    }

    // Generate a list of all possible moves for a player
    private List<Move> getAllPossibleMoves(int player, Board board) {
        // Get the player's holes
        int[] playerHoles = board.getPlayerHoles(player);

        // Initialize an empty list to store possible moves
        List<Move> possibleMoves = new ArrayList<>();

        // Iterate over all player holes
        for (int hole : playerHoles) {
            // Check for each color
            for (SeedColor color : SeedColor.values()) {
                if (board.hasSeeds(hole, color)) { // Check if the hole has seeds of this color
                    possibleMoves.add(new Move(hole, color)); // Add the move to the list
                }
            }
        }

        // Sort the moves based on the specified criteria
        possibleMoves.sort((move1, move2) -> {
            // Evaluate the "quality" of the moves
            int quality1 = evaluateMoveQuality(move1, player);
            int quality2 = evaluateMoveQuality(move2, player);

            // First sort by move quality (higher is better)
            int qualityComparison = Integer.compare(quality2, quality1);
            if (qualityComparison != 0) {
                return qualityComparison;
            }

            // If quality is equal, sort by seed count in the starting hole (descending)
            int seeds1 = board.getSeedsInHole(move1.hole(), move1.color());

            int seeds2 = board.getSeedsInHole(move2.hole(), move2.color());

            return Long.compare(seeds1, seeds2);
        });

        return possibleMoves;
    }

    // Evaluate the quality of a move
    private int evaluateMoveQuality(Move move, int player) {
        // Get the number of seeds in the starting hole
        long seedCount = board.getSeedsInHole(move.hole(), move.color());

        // Determine the end position of the sowing
        int endHole = calculateEndHole(move.hole(), (int) seedCount, move.color(), player);

        // Check the seed count in the end hole
        int endHoleSeedCount = board.getSeedsInHole(endHole);

        // Assign a score: prioritize moves that end in a hole with 1 or 2 seeds
        if (endHoleSeedCount == 1 || endHoleSeedCount == 2) {
            return 1000; // High priority for these moves
        }

        return 0; // Default priority for other moves
    }

    // Calculate the end hole of a sowing move
    private int calculateEndHole(int startHole, int seedCount, SeedColor color, int player) {
        int currentHole = startHole;

        for (int i = 0; i < seedCount; i++) {
            if (currentHole == startHole) {
                // Skip the starting hole
                currentHole++;
            }

            currentHole++;

            // Skip opponent's store if sowing the opponent's seeds
            int finalCurrentHole = currentHole;
            if (color == SeedColor.RED && Arrays.stream(board.getPlayerHoles(player)).anyMatch(hole -> hole == finalCurrentHole)) {
                currentHole++;
            }

            // Wrap around the board
            currentHole %= TOTAL_HOLES;
        }

        return currentHole;
    }
}
