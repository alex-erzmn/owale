package fr.ai.game.programming.common;

import static fr.ai.game.programming.common.AwaleBoard.TOTAL_HOLES;

import java.util.ArrayList;
import java.util.List;

/**
 * AI manager for the Awale game. Implements the Minimax algorithm with Alpha-Beta pruning and random move selection.
 */
public class AIManager {

    private final AwaleBoard awaleBoard;
    private static final int MAX_DEPTH = 5; // Maximum depth for Minimax algorithm

    public AIManager(AwaleBoard awaleBoard) {
        this.awaleBoard = awaleBoard;
    }

    /**
     * Find a random move for the player.
     * @param player the player for which to find a move
     * @return a tuple with the color of seeds and the number of seeds
     */
    public Move findRandomMove(int player) {
        if(awaleBoard.checkGameOver()) {
            return null;
        }
        Seed.Color seedColor = null;
        int hole = 0;

        // Define the range of holes based on the player and the new board layout
        int[] playerHoles = (player == 1)
                ? new int[]{0, 2, 4, 6, 8, 10, 12, 14}  // Player 1's holes (even indices)
                : new int[]{1, 3, 5, 7, 9, 11, 13, 15}; // Player 2's holes (odd indices)

        // Continue searching until a valid hole and color are selected
        while (seedColor == null) {
            int randomIndex = (int) (Math.random() * playerHoles.length); // Pick a random hole index for the player
            int randomHole = playerHoles[randomIndex]; // Get the actual hole number
            List<Seed> seedsInHole = awaleBoard.getSeedsInHole(randomHole); // Get all seeds from the hole

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
    // Changes in the findBestMove method
    public Move findBestMove(int player) {
        Move bestMove = null;
        int bestValue = (player == 1) ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        // Ensure correct hole range for each player
        int[] playerHoles = (player == 1)
                ? new int[]{0, 2, 4, 6, 8, 10, 12, 14}  // Player 1's holes (even indices)
                : new int[]{1, 3, 5, 7, 9, 11, 13, 15}; // Player 2's holes (odd indices)

        for (int holeIndex : playerHoles) {
            List<Seed> seedsInHole = awaleBoard.getSeedsInHole(holeIndex);

            if (!seedsInHole.isEmpty()) {
                for (Seed seed : seedsInHole) {
                    // Ensure to consider seeds of the current player's color
                    if ((player == 1 && seed.getColor() == Seed.Color.RED) ||
                            (player == 2 && seed.getColor() == Seed.Color.BLUE)) {

                        int moveValue = minimax(awaleBoard.getBoard(), holeIndex, seed.getColor(), 1, MAX_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE, player == 1);

                        if (player == 1) {
                            if (moveValue > bestValue) {
                                bestValue = moveValue;
                                bestMove = new Move(holeIndex, seed.getColor());
                            }
                        } else {
                            if (moveValue < bestValue) {
                                bestValue = moveValue;
                                bestMove = new Move(holeIndex, seed.getColor());
                            }
                        }
                    }
                }
            }
        }

        return (bestMove != null) ? bestMove : findRandomMove(player);
    }

    // Changes in the makeMoveForSimulation method
    private List<Seed>[] makeMoveForSimulation(List<Seed>[] board, int hole, Seed.Color seedColor, int seedsToSow) {
        List<Seed> seedsInHole = new ArrayList<>(board[hole]);
        List<Seed> seedsToSowList = new ArrayList<>();

        for (Seed seed : seedsInHole) {
            if (seed.getColor() == seedColor && seedsToSowList.size() < seedsToSow) {
                seedsToSowList.add(seed);
            }
        }

        seedsInHole.removeAll(seedsToSowList);
        board[hole] = seedsInHole;

        int pos = hole;
        for (Seed seed : seedsToSowList) {
            do {
                pos = ((pos % TOTAL_HOLES) + 1) % TOTAL_HOLES; // Move to the next hole, looping around
            } while (pos == hole); // Skip the original hole

            // Ensure that the seeds are placed in the correct holes
            if (seedColor == Seed.Color.BLUE || pos % 2 == 0) { // Correctly identify holes for each color
                board[pos].add(seed);
            }
        }

        captureSeedsForSimulation(board, pos);

        return board;
    }

    // Adjustments in the minimax method
    private int minimax(List<Seed>[] board, int hole, Seed.Color seedColor, int seedsToSow, int depth, int alpha, int beta, boolean isMaximizingPlayer) {
        if (depth == 0 || awaleBoard.checkGameOver()) {
            // Differentiate evaluation based on the current player
            if (isMaximizingPlayer) {
                return awaleBoard.evaluateBoard(); // Evaluating for the maximizing player
            } else {
                return -awaleBoard.evaluateBoard(); // Evaluating for the minimizing player
            }
        }

        if (isMaximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (int i = 0; i < TOTAL_HOLES; i++) {
                if (!board[i].isEmpty() && (i % 2 == 0)) { // Only consider Player 1's holes
                    List<Seed>[] newBoard = makeMoveForSimulation(board.clone(), i, seedColor, seedsToSow);
                    int eval = minimax(newBoard, i, seedColor, seedsToSow, depth - 1, alpha, beta, false);
                    maxEval = Math.max(maxEval, eval);
                    alpha = Math.max(alpha, eval);
                    if (beta <= alpha) {
                        break;  // Beta cut-off
                    }
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (int i = 0; i < TOTAL_HOLES; i++) {
                if (!board[i].isEmpty() && (i % 2 == 1)) { // Only consider Player 2's holes
                    List<Seed>[] newBoard = makeMoveForSimulation(board.clone(), i, seedColor, seedsToSow);
                    int eval = minimax(newBoard, i, seedColor, seedsToSow, depth - 1, alpha, beta, true);
                    minEval = Math.min(minEval, eval);
                    beta = Math.min(beta, eval);
                    if (beta <= alpha) {
                        break;  // Alpha cut-off
                    }
                }
            }
            return minEval;
        }
    }


    private void captureSeedsForSimulation(List<Seed>[] board, int lastHole) {
        int player = (lastHole % 2 == 1) ? 2 : 1; // Determine the player based on hole number

        while (lastHole >= 1 && lastHole <= TOTAL_HOLES &&
                ((player == 1 && lastHole % 2 == 0) || (player == 2 && lastHole % 2 == 1))) {
            int seedCount = board[lastHole].size();
            if (seedCount == 2 || seedCount == 3) {
                board[lastHole].clear(); // Capture the seeds by clearing the hole
            } else {
                break;
            }
            lastHole--;
        }
    }

    public record Move(int hole, Seed.Color color) {
    }
}
