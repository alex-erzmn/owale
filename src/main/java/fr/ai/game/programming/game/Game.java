package fr.ai.game.programming.game;

import fr.ai.game.programming.client.Observer;
import fr.ai.game.programming.game.elements.Board;
import fr.ai.game.programming.game.elements.Seed;
import fr.ai.game.programming.game.player.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.util.Duration;
import lombok.Getter;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private final List<Observer> observers;

    public Game(Board board, Player player1, Player player2) {
        this.board = board;
        this.player1 = player1;
        this.player2 = player2;
        this.isRunning = false;
        observers = new ArrayList<>();
    }

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    private void notifyObservers(GameEventType eventType) {
        for (Observer observer : observers) {
            switch (eventType) {
                case UPDATE_BOARD:
                    observer.onBoardUpdated();
                    break;
                case ENABLE_BUTTONS:
                    observer.onEnablePlayerButtons();
                    break;
                case GAME_OVER:
                    observer.onGameOver();
                    break;
            }
        }
    }

    public void start() {
        notifyObservers(GameEventType.UPDATE_BOARD);
        this.isRunning = true;
        performNextMove();
    }

    public void stop() {
        this.isRunning = false;
    }

    public void performNextMove() {
        if(!isRunning) return;
        if (checkGameOver()) return;

        Player currentPlayer = getCurrentPlayer();
        if (isCurrentPlayerAI()) {

            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.1), event -> {
                try {
                    currentPlayer.makeMove(board);
                } catch (MqttException e) {
                    throw new RuntimeException(e);
                }
                board.switchPlayer();
                notifyObservers(GameEventType.UPDATE_BOARD);

                if (checkGameOver()) return;

                performNextMove();
            }));

            timeline.setCycleCount(1);
            timeline.play();
        } else if (isCurrentPlayerMQTT()) {
            ((MQTTOpponent) currentPlayer).setOnMoveProcessed(v -> {

                board.switchPlayer();
                notifyObservers(GameEventType.UPDATE_BOARD);

                if (!checkGameOver()) {
                    performNextMove(); // Continue loop after move is received
                }
            });
        } else if (isCurrentPlayerHuman()) {
            notifyObservers(GameEventType.ENABLE_BUTTONS); // TODO: Remove if only via console allowed
            ((HumanPlayer) currentPlayer).makeMove(board);

            // Start a new thread to listen for console input
            new Thread(() -> {
                // This loop will keep running until a valid move is entered
                while (true) { // Continue prompting for valid input until correct
                    try {
                        System.out.println("Enter your move (e.g., '3B') or use the buttons:");
                        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                        String input = reader.readLine();

                        if (input != null && !input.isEmpty()) {
                            if (processConsoleMove(input, board)) {
                                break;  // Exit the loop if the move is valid
                            } else {
                                System.out.println("Invalid move, please try again."); // Inform the user to try again
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
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
            int holeIndex = Integer.parseInt(matcher.group(1)) - 1; // Convert to 0-based index
            Seed.Color chosenColor = parseColor(matcher.group(2));

            // Validate hole index
            if (holeIndex < 0 || holeIndex >= board.getHoles().length) {
                System.out.println("Invalid hole number. Please choose a number between 1 and " + board.getHoles().length + ".");
                return false;
            }

            if (holeIndex % 2 == 1) {
                System.out.println("Invalid hole number. This hole is not yours.");
                return false;
            }

            // Apply the move
            board.sowSeeds(holeIndex, chosenColor);

            // Update the game state on the JavaFX Application Thread
            Platform.runLater(() -> {
                board.switchPlayer();
                notifyObservers(GameEventType.UPDATE_BOARD);

                // Continue the game loop
                if (!checkGameOver()) {
                    performNextMove();
                }
            });

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
    
    public boolean isCurrentPlayerMQTT() {
        return getCurrentPlayer() instanceof MQTTOpponent;
    }

    public boolean isCurrentPlayerAI() {
        return getCurrentPlayer() instanceof AIPlayer;
    }

    public boolean isCurrentPlayerHuman() {
        return getCurrentPlayer() instanceof HumanPlayer;
    }

    public boolean checkGameOver() {
        GameStatus status = board.checkGameStatus();
        if (status.isGameOver()) {
            notifyObservers(GameEventType.GAME_OVER); // Pass the status object
            System.out.println("Game over! Winner: " + status.getWinner() + " Reason: " + status.getReason());
            System.out.println("------------------------------------------------------------------------");
        }
        return status.isGameOver();
    }
}

