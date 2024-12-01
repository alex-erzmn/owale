package fr.ai.game.programming.game;

import fr.ai.game.programming.client.Observer;
import fr.ai.game.programming.game.elements.Board;
import fr.ai.game.programming.game.player.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import lombok.Getter;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.ArrayList;
import java.util.List;

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
        } else if (currentPlayer instanceof MQTTOpponent) {
            ((MQTTOpponent) currentPlayer).setOnMoveProcessed(v -> {
                board.switchPlayer();
                notifyObservers(GameEventType.UPDATE_BOARD);

                if (!checkGameOver()) {
                    performNextMove(); // Continue loop after move is received
                }
            });
        } else if (isCurrentPlayerHuman()) {
            notifyObservers(GameEventType.ENABLE_BUTTONS);
            ((HumanPlayer) currentPlayer).makeMove(board);
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
        boolean gameOver = board.checkGameOver();
        if (gameOver) {
            notifyObservers(GameEventType.GAME_OVER); // Game is over, update the observers
        }
        return gameOver;
    }


}
