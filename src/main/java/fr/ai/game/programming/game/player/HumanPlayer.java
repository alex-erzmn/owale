package fr.ai.game.programming.game.player;

import fr.ai.game.programming.game.elements.Board;
import fr.ai.game.programming.game.elements.SeedColor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static fr.ai.game.programming.game.elements.Board.TOTAL_HOLES;

/**
 * Human player for the Awale game.
 * @implNote This class is an empty placeholder for a human player.
 */
public class HumanPlayer implements Player {

    @Override
    public void makeMove(Board board) {
        System.out.print("Enter your move (e.g., '3B'): ");

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
            SeedColor chosenColor = parseColor(matcher.group(2));

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

            return true; // Move successfully processed

        } catch (Exception e) {
            System.out.println("An error occurred while processing your move. Please try again.");
            e.printStackTrace();
            return false;
        }
    }

    private SeedColor parseColor(String colorCode) {
        switch (colorCode.toUpperCase()) {
            case "B":
                return SeedColor.BLUE;
            case "R":
                return SeedColor.RED;
            default:
                throw new IllegalArgumentException("Invalid color code: " + colorCode);
        }
    }

}