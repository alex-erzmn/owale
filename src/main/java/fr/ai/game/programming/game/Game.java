package fr.ai.game.programming.game;

import fr.ai.game.programming.game.elements.Board;
import fr.ai.game.programming.game.elements.Seed;
import fr.ai.game.programming.game.player.*;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static fr.ai.game.programming.game.elements.Board.TOTAL_HOLES;

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

        Player currentPlayer = getCurrentPlayer();
        if (isCurrentPlayerAI()) {
            currentPlayer.makeMove(board);
            board.switchPlayer();

            // Print the board layout in the console
            board.printBoardLayout();

            if (!checkGameOver()) {
                performNextMove();
            }
        } else if (isCurrentPlayerHuman()) {
            currentPlayer.makeMove(board);

            // Read the player's move from the console
            while (true) {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                    String input = reader.readLine();

                    if (input != null && !input.isEmpty()) {
                        if (processConsoleMove(input, board)) {
                            break;
                        } else {
                            System.out.print("Enter your move (e.g., '3B'): ");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("An error occurred while reading input. Please try again.");
                }
            }
        }
    }

    private boolean processConsoleMove(String input, Board board) {
        try {
            // Validate and parse input using regex
            String pattern = "^(\\d{1,2})([RB])$"; // Match 1-2 digits followed by 'R' or 'B'
            Pattern regex = Pattern.compile(pattern);
            Matcher matcher = regex.matcher(input.toUpperCase());

            if (!matcher.matches()) {
                System.out.println("Invalid input format. Example: '11R' or '3B'.");
                return false;
            }

            // Extract hole index and color
            int holeIndex = Integer.parseInt(matcher.group(1));
            Seed.Color chosenColor = parseColor(matcher.group(2));

            // Validate hole index
            if (holeIndex < 1 || holeIndex > TOTAL_HOLES) {
                System.out.println("Invalid hole number. Please choose a number between 1 and " + board.getHoles().length + ".");
                return false;
            }

            if (holeIndex % 2 != board.getCurrentPlayer() % 2) {
                System.out.println("Invalid hole number " + holeIndex +". This hole is not yours " + "player" + board.getCurrentPlayer() + ".");
                return false;
            }

            // Apply the move
            try {
                int zeroBasedHoleIndex = holeIndex - 1; // Convert to 0-based index
                board.sowSeeds(zeroBasedHoleIndex, chosenColor);
                System.out.println("Player " + board.getCurrentPlayer() + " chose to sow " + chosenColor + " seeds from hole " + holeIndex + ".");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                return false;
            }

            // Update the game state on the JavaFX Application Thread
            board.switchPlayer();

            // Print the board layout in the console
            board.printBoardLayout();


            // Continue the game loop
            if (!checkGameOver()) {
                performNextMove();
            }

            return true; // Move successfully processed

        } catch (Exception e) {
            System.out.println("An error occurred while processing your move. Please try again.");
            e.printStackTrace();
            return false;
        }
    }

    private Seed.Color parseColor(String colorCode) {
        switch (colorCode.toUpperCase()) {
            case "B":
                return Seed.Color.BLUE;
            case "R":
                return Seed.Color.RED;
            default:
                throw new IllegalArgumentException("Invalid color code: " + colorCode);
        }
    }

    public Player getCurrentPlayer() {
        return board.getCurrentPlayer() == 1 ? player1 : player2;
    }

    public boolean isCurrentPlayerAI() {
        return getCurrentPlayer() instanceof AIPlayer;
    }

    public boolean isCurrentPlayerHuman() {
        return getCurrentPlayer() instanceof HumanPlayer;
    }

    public boolean checkGameOver() {
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

