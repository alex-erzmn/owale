package fr.ai.game.programming.game;

import fr.ai.game.programming.game.elements.Board;
import fr.ai.game.programming.game.player.*;
import lombok.Getter;


/**
 * Represents an Awale game.
 *
 */
public class Game {

    @Getter
    private final Board board;
    @Getter
    private final Player player1;
    @Getter
    private final Player player2;
    private boolean isRunning;

    public Game(Board board, Player player1, Player player2) {
        this.board = board;
        this.player1 = player1;
        this.player2 = player2;
        this.isRunning = false;
    }

    public void start() {
        this.isRunning = true;
        // Print the board layout in the console
        board.printBoardLayout();
        performNextMove();
    }

    public void stop() {
        this.isRunning = false;
    }

    public void performNextMove() {
        if(!isRunning) return;

        Player currentPlayer = this.getCurrentPlayer();
        currentPlayer.makeMove(board);
        board.switchPlayer();
        board.setTurns(board.getTurns() + 1);

        // Print the board layout in the console
        board.printBoardLayout();

        if (!checkGameOver()) {
            performNextMove();
        }
    }

    private Player getCurrentPlayer() {
        return board.getCurrentPlayer() == 1 ? player1 : player2;
    }

    private boolean checkGameOver() {
        GameStatus status = board.checkGameStatus();
        if (status.isGameOver() && isRunning) {
            stop();
            showGameOver(status);
        }
        return status.isGameOver();
    }

    private void showGameOver(GameStatus status) {
        System.out.println("Game Over!");

        // Print reason for game over
        System.out.println("Reason: " + status.reason());

        // Print the winner or draw message
        if (status.winner() != 0) {
            System.out.println("Winner: " + "Player " + status.winner());
        } else {
            System.out.println("It's a draw!");
        }

        System.out.println("Thank you for playing Awalé!");
        System.out.println();

        System.out.println(" -#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#- FINAL BOARD -#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#- ");
        board.printBoardLayout();
        System.out.println(" -#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#- ");
    }
}

