package fr.ai.game.programming;

import fr.ai.game.programming.common.AwaleBoard;

public class AIManager {

    private final AwaleBoard awaleBoard;
    private static final int MAX_DEPTH = 8; // Maximum depth for Minimax search

    public AIManager(AwaleBoard awaleBoard) {
        this.awaleBoard = awaleBoard;
    }

    // Find the best move for the AI using Minimax with Alpha-Beta pruning
    public int findBestMove() {
        int bestMove = -1;
        int bestValue = Integer.MIN_VALUE;
        int[] board = awaleBoard.getBoard();

        // Simulate all possible moves for the AI (player 2)
        for (int i = 0; i < AwaleBoard.NUM_HOLES; i++) {
            if (board[i] > 1) { // If the hole has seeds, it's a valid move
                int[] newBoard = makeMove(board.clone(), i);
                int moveValue = minimax(newBoard, MAX_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE, false);

                // Choose the move with the highest evaluation
                if (moveValue > bestValue) {
                    bestValue = moveValue;
                    bestMove = i;
                }
            }
        }
        return bestMove;
    }

    // Minimax algorithm with Alpha-Beta pruning
    private int minimax(int[] board, int depth, int alpha, int beta, boolean isMaximizingPlayer) {
        // Check for game over or depth limit
        if (depth == 0 || isGameOver(board)) {
            return evaluateBoard(board);
        }

        if (isMaximizingPlayer) {
            // AI is maximizing
            int maxEval = Integer.MIN_VALUE;
            for (int i = 0; i < AwaleBoard.NUM_HOLES; i++) {
                if (board[i] > 1) {
                    int[] newBoard = makeMove(board.clone(), i);
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
            for (int i = AwaleBoard.NUM_HOLES; i < AwaleBoard.TOTAL_HOLES; i++) {
                if (board[i] > 1) {
                    int[] newBoard = makeMove(board.clone(), i);
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

    // Simulate making a move (sowing seeds)
    private int[] makeMove(int[] board, int hole) {
        int seeds = board[hole];
        board[hole] = 0;
        int pos = hole;

        // Sow the seeds
        while (seeds > 0) {
            pos = (pos + 1) % AwaleBoard.TOTAL_HOLES;
            board[pos]++;
            seeds--;
        }

        // Simulate seed capture
        captureSeedsForSimulation(board, pos);

        return board;
    }

    // Simple evaluation function (you can enhance it to suit more complex strategies)
    private int evaluateBoard(int[] board) {
        int player1Seeds = 0;
        int player2Seeds = 0;

        // Count seeds for both players
        for (int i = 0; i < AwaleBoard.NUM_HOLES; i++) {
            player2Seeds += board[i];
        }
        for (int i = AwaleBoard.NUM_HOLES; i < AwaleBoard.TOTAL_HOLES; i++) {
            player1Seeds += board[i];
        }

        // The heuristic evaluates the difference between Player 2's and Player 1's seeds
        return player2Seeds - player1Seeds;
    }

    // Capture seeds for simulation
    private void captureSeedsForSimulation(int[] board, int lastHole) {
        int capturedSeeds = 0;
        int player = (lastHole < AwaleBoard.NUM_HOLES) ? 2 : 1;

        // Check the bounds and adjust the conditions to prevent negative indexing
        while (lastHole >= 0 && lastHole < AwaleBoard.TOTAL_HOLES &&
                ((player == 1 && lastHole >= AwaleBoard.NUM_HOLES) ||
                        (player == 2 && lastHole < AwaleBoard.NUM_HOLES))) {
            if (board[lastHole] == 2 || board[lastHole] == 3) {
                capturedSeeds += board[lastHole];
                board[lastHole] = 0;
            } else {
                break;
            }
            lastHole--; // Decrement lastHole
        }
    }

    // Check if the game is over
    private boolean isGameOver(int[] board) {
        boolean player1HasSeeds = false;
        boolean player2HasSeeds = false;

        for (int i = 0; i < AwaleBoard.NUM_HOLES; i++) {
            if (board[i] > 0) {
                player2HasSeeds = true;
                break;
            }
        }
        for (int i = AwaleBoard.NUM_HOLES; i < AwaleBoard.TOTAL_HOLES; i++) {
            if (board[i] > 0) {
                player1HasSeeds = true;
                break;
            }
        }
        return !(player1HasSeeds && player2HasSeeds); // Game over if either player has no seeds
    }
}
