package fr.ai.game.programming.common;

import static fr.ai.game.programming.common.AwaleBoard.PLAYER_HOLES;
import static fr.ai.game.programming.common.AwaleBoard.TOTAL_HOLES;

/**
 * AI manager for the Awale game. Implements the Minimax algorithm with Alpha-Beta pruning and a random move selection.
 */
public class AIManager {

    private final AwaleBoard awaleBoard;
    private static final int MAX_DEPTH = 20; // Maximum depth for Minimax algorithm

    public AIManager(AwaleBoard awaleBoard) {
        this.awaleBoard = awaleBoard;
    }

    /**
     * Find a random move for the player.
     * @param player the player for which to find a move
     * @return the index of the hole to sow seeds
     */
    public int findRandomMove(int player) {
        int move = -1;
        int[] board = awaleBoard.getBoard();

        // Set bounds for random selection based on the player
        int startHole = (player == 2) ? 0 : PLAYER_HOLES; // Player 1's holes are 6-11, Player 2's holes are 0-5
        int endHole = (player == 2) ? PLAYER_HOLES : TOTAL_HOLES; // Player 1 ends at 12, Player 2 ends at 6

        while (move == -1) {
            int randomHole = startHole + (int) (Math.random() * (endHole - startHole));
            if (board[randomHole] > 1) {
                move = randomHole;
            }
        }
        return move;
    }

    /**
     * Find the best move for the player using the Minimax algorithm with Alpha-Beta pruning.
     * @param player the player for which to find the best move
     * @return the index of the hole to sow seeds
     */
    public int findBestMove(int player) {
        int bestMove = -1;
        int bestValue = (player == 2) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int[] board = awaleBoard.getBoard();

        int startHole = (player == 1) ? PLAYER_HOLES : 0; // Player 1 checks holes 6-11
        int endHole = (player == 1) ? TOTAL_HOLES : PLAYER_HOLES; // Player 2 checks holes 0-5

        for (int i = startHole; i < endHole; i++) {
            if (board[i] > 1) {
                int[] newBoard = makeMoveForSimulation(board.clone(), i);
                int moveValue = minimax(newBoard, MAX_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE, player == 1);

                if (player == 2) {
                    if (moveValue > bestValue) {
                        bestValue = moveValue;
                        bestMove = i;
                    }
                } else {
                    if (moveValue < bestValue) {
                        bestValue = moveValue;
                        bestMove = i;
                    }
                }
            }
        }
        if (bestMove == -1) {
            bestMove = findRandomMove(player); //TODO: WORKAROUND!
        }
        return bestMove;
    }


    /**
     * Minimax algorithm with Alpha-Beta pruning.
     * @param board the current board state
     * @param depth the depth of the search tree
     * @param alpha the alpha value for pruning
     * @param beta the beta value for pruning
     * @param isMaximizingPlayer whether the player is maximizing or minimizing
     * @return the evaluation value of the board state
     */
    private int minimax(int[] board, int depth, int alpha, int beta, boolean isMaximizingPlayer) {
        // Check for game over or depth limit
        if (depth == 0 ) {
            return awaleBoard.evaluateBoard();
        }

        if (isMaximizingPlayer) {
            // AI is maximizing
            int maxEval = Integer.MIN_VALUE;
            for (int i = 0; i < PLAYER_HOLES; i++) {
                if (board[i] > 1) {
                    int[] newBoard = makeMoveForSimulation(board.clone(), i);
                    int eval = minimax(newBoard, depth - 1, alpha, beta, false);
                    maxEval = Math.max(maxEval, eval);
                    alpha = Math.max(alpha, eval);
                    if (beta <= alpha) {
                        break; // Beta cut-off
                    }
                }
            }
            return maxEval;
        } else {
            // Opponent (Player 1) is minimizing
            int minEval = Integer.MAX_VALUE;
            for (int i = PLAYER_HOLES; i < TOTAL_HOLES; i++) {
                if (board[i] > 1) {
                    int[] newBoard = makeMoveForSimulation(board.clone(), i);
                    int eval = minimax(newBoard, depth - 1, alpha, beta, true);
                    minEval = Math.min(minEval, eval);
                    beta = Math.min(beta, eval);
                    if (beta <= alpha) {
                        break; // Alpha cut-off
                    }
                }
            }
            return minEval;
        }
    }

    /**
     * Simulate making a move on the board.
     * @param board the current board state
     * @param hole the hole to sow seeds
     * @return the new board state after the move
     */
    private int[] makeMoveForSimulation(int[] board, int hole) {
        int seeds = board[hole];
        board[hole] = 0;
        int pos = hole;

        while (seeds > 0) {
            pos = (pos + 1) % TOTAL_HOLES;
            board[pos]++;
            seeds--;
        }

        captureSeedsForSimulation(board, pos);

        return board;
    }

    /**
     * Capture seeds based on the game rules for the simulation.
     * @param board the current board state
     * @param lastHole the last hole where a seed was sown
     */
    private void captureSeedsForSimulation(int[] board, int lastHole) {
        int capturedSeeds = 0;
        int player = (lastHole < PLAYER_HOLES) ? 2 : 1;

        while (lastHole >= 0 && lastHole < TOTAL_HOLES &&
                ((player == 1 && lastHole >= PLAYER_HOLES) ||
                        (player == 2 && lastHole < PLAYER_HOLES))) {
            if (board[lastHole] == 2 || board[lastHole] == 3) {
                capturedSeeds += board[lastHole];
                board[lastHole] = 0;
            } else {
                break;
            }
            lastHole--;
        }
    }
}
