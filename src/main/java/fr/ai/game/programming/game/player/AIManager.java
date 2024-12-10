package fr.ai.game.programming.game.player;

import fr.ai.game.programming.game.elements.Board;
import fr.ai.game.programming.game.elements.Seed;
import java.util.List;

/**
 * AI manager for the Awale game. Implements the Minimax algorithm with Alpha-Beta pruning and random move selection.
 */
public class AIManager {
    private final Board board;
    private static final int MAX_DEPTH = 5; // Maximum depth for Minimax algorithm

    public AIManager(Board board) {
        this.board = board;
    }

    /**
     * Find a random move for the player.
     * @param player the player for which to find a move
     * @return a tuple with the color of seeds and the number of seeds
     */
    public Move findRandomMove(int player) {
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
     * @param player the player for which to find the best move
     * @return the best move which includes seed color and number of seeds
     */
    public Move findBestMove(int player) {
        // Define initial alpha and beta values
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        // Define a high-level utility variable to track the best move
        Move bestMove = findRandomMove(player); // This is only workaround because there is an error which causes sometimes to not find a solution with minimax
        int bestValue = (player == 1) ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        // Define the player's holes based on the game rules
        int[] playerHoles = board.getPlayerHoles(player);

        // TODO: Sort the Holes based on any heuristic to improve the performance of the algorithm

        // Iterate through all holes to find the best move
        for (int hole : playerHoles) {

            for (Seed.Color color : Seed.Color.values()) {
                if (board.hasSeeds(hole, color)) { // Check if seeds of this color exist in the hole
                    // Create a temporary board state for simulation
                    Board simulatedBoard = board.copy();
                    simulatedBoard.sowSeeds(hole, color); // Perform the move on the simulated board

                    // Calculate the utility of the move using the minimax algorithm
                    int moveValue = minimax(simulatedBoard, MAX_DEPTH, alpha, beta, player == 2);

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
            return evaluateBoard(simulatedBoard); // Evaluate the utility of the board
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
}
