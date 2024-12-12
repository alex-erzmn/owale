package fr.ai.game.programming.game.player;

import fr.ai.game.programming.game.elements.Board;
import fr.ai.game.programming.game.elements.Seed;

import java.util.ArrayList;
import java.util.List;

/**
 * AI manager for the Awale game. Implements the Minimax algorithm with Alpha-Beta pruning and random move selection.
 */
public class AIManagerNolan implements AIManager {
    private final Board board;
    private final int initialDepth = 5; // Initial depth for Minimax algorithm
    private int currentDepth = initialDepth; // Initial depth for Minimax algorithm
    private static final int MAX_DEPTH = 20; // Maximum depth for Minimax algorithm

    public AIManagerNolan(Board board) {
        this.board = board;
    }

    public Move findMove() {
        return findBestMoveOld();
    }

    /**
     * Find a random move for the player.
     * @return a tuple with the color of seeds and the number of seeds
     */
    private Move findRandomMove() {
        int player = board.getCurrentPlayer();
        Seed.Color seedColor = null;
        int hole = 0;

        // Define the range of holes based on the player and the new board layout
        int[] playerHoles = board.getPlayerHoles(player);

        // Continue searching until a valid hole and color are selected
        while (seedColor == null) {
            int randomIndex = (int) (Math.random() * playerHoles.length); // Pick a random hole index for the player
            int randomHole = playerHoles[randomIndex]; // Get the actual hole number
            List<Seed> seedsInHole = board.getSeedsInHole(randomHole); // Get all seeds from the hole

            if (!seedsInHole.isEmpty()) {
                // Select a random seed from the available seeds in the hole to determine the color
                int randomSeedIndex = (int) (Math.random() * seedsInHole.size());
                seedColor = seedsInHole.get(randomSeedIndex).getColor(); // Get the color of the random seed
                hole = randomHole; // Set the hole to the selected random hole
            }
        }

        // Return the chosen hole and color of seeds
        return new Move(hole, seedColor);
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
        List<Move> possibleMoves = getAllPossibleMoves(player);

        // for (Move move : possibleMoves) {
        //    System.out.println(move);
        //}

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
            for (int hole : board.getPlayerHoles(2)) {
                for (Seed.Color color : Seed.Color.values()) {
                    if (simulatedBoard.hasSeeds(hole, color)) {
                        // Simulate the move
                        Board childBoard = simulatedBoard.copy();
                        childBoard.sowSeeds(hole, color);
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
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (int hole : board.getPlayerHoles(1)) {
                for (Seed.Color color : Seed.Color.values()) {
                    if (simulatedBoard.hasSeeds(hole, color)) {
                        // Simulate the move
                        Board childBoard = simulatedBoard.copy();
                        childBoard.sowSeeds(hole, color);
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
            }
            return minEval;
        }
    }

    /**
     * Evaluate the utility of the board for the AI.
     * @param board the board to evaluate
     * @return an integer representing the utility of the board
     */
    private int evaluateBoard(Board board) {
        return board.getPlayer1Seeds() - board.getPlayer2Seeds(); // Example: Favor the player with more seeds
    }

    /**
     * Optimize the depth of the Minimax algorithm based on the current game state.
     * The depth is increased every 6 turns to improve the AI performance.
     */
    private void optimizeDepth(int amountPossibleMoves) {
        if( amountPossibleMoves > 8) {
            currentDepth = initialDepth ;
        } else if (amountPossibleMoves > 4) {
            currentDepth = initialDepth + 1;
        } else if (amountPossibleMoves > 2) {
            currentDepth = initialDepth + 3;
        } else {
            currentDepth = initialDepth + 4;
        }
    }

    // Generate a list of all possible moves for a player
    private List<Move> getAllPossibleMoves(int player) {
        // Get the player's holes
        int[] playerHoles = board.getPlayerHoles(player);

        // Initialize an empty list to store possible moves
        List<Move> possibleMoves = new ArrayList<>();

        // Iterate over all player holes
        for (int hole : playerHoles) {
            // Check for each color
            for (Seed.Color color : Seed.Color.values()) {
                if (board.hasSeeds(hole, color)) { // Check if the hole has seeds of this color
                    possibleMoves.add(new Move(hole, color)); // Add the move to the list
                }
            }
        }

        // Sort the moves based on the number of seeds in the specified color
        possibleMoves.sort((move1, move2) -> {
            // Count seeds of the specific color for move1
            long seeds1 = board.getSeedsInHole(move1.hole())
                    .stream()
                    .filter(seed -> seed.getColor() == move1.color())
                    .count();

            // Count seeds of the specific color for move2
            long seeds2 = board.getSeedsInHole(move2.hole())
                    .stream()
                    .filter(seed -> seed.getColor() == move2.color())
                    .count();

            // Sort in descending order of the seed count
            return Long.compare(seeds2, seeds1);
        });

        return possibleMoves;
    }






    /* ------------------------------------- DELETE ------------------------------------ */

    /**
     * Find the best move for the player using the Minimax algorithm with Alpha-Beta pruning.
     * @return the best move which includes seed color and number of seeds
     */
    private Move findBestMoveOld() {
        int player = board.getCurrentPlayer();
        // Define initial alpha and beta values
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        // Define a high-level utility variable to track the best move
        Move bestMove = findRandomMove(); // This is only workaround because there is an error which causes sometimes to not find a solution with minimax
        int bestValue = (player == 1) ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        System.out.println(currentDepth);

        // Define the player's holes based on the game rules
        int[] playerHoles = board.getPlayerHoles(player);

        // Iterate through all holes to find the best move
        for (int hole : playerHoles) {

            for (Seed.Color color : Seed.Color.values()) {
                if (board.hasSeeds(hole, color)) { // Check if seeds of this color exist in the hole
                    // Create a temporary board state for simulation
                    Board simulatedBoard = board.copy();
                    simulatedBoard.sowSeeds(hole, color); // Perform the move on the simulated board



                    // Calculate the utility of the move using the minimax algorithm
                    int moveValue = minimax(simulatedBoard, 5, alpha, beta, player == 2);

                    // Update the best move if the current move has a better value
                    if ((player == 1 && moveValue > bestValue) || (player == 2 && moveValue < bestValue)) {
                        bestValue = moveValue;
                        bestMove = new Move(hole, color);
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
        }

        return bestMove;
    }

}
