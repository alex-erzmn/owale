package fr.ai.game.programming;

import fr.ai.game.programming.common.AwaleBoard;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

import static fr.ai.game.programming.common.AwaleBoard.NUM_HOLES;
import static fr.ai.game.programming.common.AwaleBoard.TOTAL_HOLES;

public class AwaleFX extends Application {

    private AwaleBoard awaleBoard;
    private AIManager aiManager;
    private SceneManager sceneManager;
    private Button[] player1Buttons = new Button[NUM_HOLES];
    private Button[] player2Buttons = new Button[NUM_HOLES];
    private Button restartButton;
    private Label player1Label = new Label("Player 1 Seeds: 0");
    private Label player2Label = new Label("Player 2 Seeds: 0");
    private Label statusLabel = new Label("Player 1's Turn");

    @Override
    public void start(Stage primaryStage) {
        awaleBoard = new AwaleBoard();
        aiManager = new AIManager(awaleBoard);

        primaryStage.getIcons().add(new Image("/images/seeds.png"));
        primaryStage.setTitle("Awalé Game");
        sceneManager = new SceneManager(primaryStage);
        sceneManager.setStartScene(createStartScene());
        sceneManager.setGameScene(createGameScene());
        sceneManager.showStartScene();

        updateBoard();
    }

    // Create the start scene with a welcome message and a start button
    private Scene createStartScene() {
        Label welcomeLabel = new Label("Welcome to Awalé Game!");
        welcomeLabel.setFont(Font.font("Arial", 24));

        Button startButton = new Button("Start Game");
        startButton.setFont(Font.font("Arial", 20));
        startButton.setOnAction(e -> sceneManager.showGameScene());

        VBox layout = new VBox(20, welcomeLabel, startButton);
        layout.setAlignment(Pos.CENTER);  // Ensure alignment is centered
        layout.setPadding(new Insets(20));

        Scene startScene = new Scene(layout, 800, 500);  // Set consistent scene size

        return startScene;
    }


    // Create the game scene with the board and status bar
    private Scene createGameScene() {
        GridPane grid = setupBoard();

        player1Label.setFont(Font.font("Arial", 20));
        player2Label.setFont(Font.font("Arial", 20));
        statusLabel.setFont(Font.font("Arial", 24));
        statusLabel.setTextFill(Color.DARKBLUE);
        restartButton = createRestartButton();

        HBox statusBar = new HBox(40, player1Label, statusLabel, player2Label);
        statusBar.setAlignment(Pos.CENTER);
        statusBar.setPadding(new Insets(20));
        statusBar.setStyle("-fx-background-color: #D2B48C; -fx-padding: 15; -fx-background-radius: 10;");
        HBox restartBox = new HBox(restartButton);
        restartBox.setAlignment(Pos.CENTER);
        restartBox.setPadding(new Insets(20));
        restartBox.setStyle("-fx-background-color: #D2B48C; -fx-padding: 15; -fx-background-radius: 10;");

        VBox layout = new VBox(20, grid, statusBar, restartBox);
        layout.setAlignment(Pos.CENTER);  // Maintain centered alignment
        layout.setPadding(new Insets(20));

        Scene gameScene = new Scene(layout, 800, 500);  // Keep consistent scene size

        return gameScene;
    }

    private Button createRestartButton() {
        Button button = new Button("Restart Game");
        button.setFont(Font.font("Arial", 20));
        button.setOnAction(e -> resetGame()); // Set the action to reset the game
        button.setDisable(true); // Disable it initially until the game is over
        return button;
    }

    // Setup the board grid and buttons
    private GridPane setupBoard() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(15);  // Space between holes
        grid.setVgap(15);
        grid.setPadding(new Insets(20));

        // Setting up the background color and style for a wooden look
        grid.setStyle("-fx-background-color: #8B4513; -fx-padding: 20; -fx-border-radius: 15; -fx-background-radius: 15;");

        // Player 2 buttons (holes 1 to 6)
        for (int i = NUM_HOLES - 1; i >= 0; i--) {
            player2Buttons[i] = createButton(i);
            player2Buttons[i].setDisable(true);  // Disable player 2 buttons initially
            grid.add(player2Buttons[i], NUM_HOLES - i - 1, 0);  // Add to the top row
        }

        // Player 1 buttons (holes 7 to 12)
        for (int i = NUM_HOLES; i < TOTAL_HOLES; i++) {
            player1Buttons[i - NUM_HOLES] = createButton(i);
            grid.add(player1Buttons[i - NUM_HOLES], i - NUM_HOLES, 1);  // Add to the bottom row
        }

        return grid;
    }

    // Create buttons and define actions for player moves
    private Button createButton(int index) {
        Button button = new Button("4");

        button.setFont(Font.font("Arial", 22));  // Large font size for seeds
        button.setPrefSize(80, 80);  // Larger buttons for a better look
        button.setStyle("-fx-background-color: #F4A460; -fx-border-color: black; -fx-background-radius: 40; -fx-border-radius: 40;"); // Rounded shape for buttons

        // Add shadow effect to give a more 3D look
        DropShadow shadow = new DropShadow();
        shadow.setOffsetX(3);
        shadow.setOffsetY(3);
        button.setEffect(shadow);

        // On button click: handle the game logic
        button.setOnAction(e -> {
            if (awaleBoard.getBoard()[index] <= 1 || awaleBoard.getCurrentPlayer() == 2) {
                sceneManager.showError("Invalid move!");
                return;
            }

            if (awaleBoard.checkGameOver()) {
                endGame();
            }

            awaleBoard.sowSeeds(index);
            updateBoard();

            awaleBoard.setCurrentPlayer(2);
            statusLabel.setText("AI's Turn");

            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(event -> {
                int aiMove = aiManager.findBestMove();
                awaleBoard.sowSeeds(aiMove);
                awaleBoard.setCurrentPlayer(1);
                statusLabel.setText("Player 1's Turn");

                updateBoard();
                if (awaleBoard.checkGameOver()) {
                    endGame();
                }
            });
            pause.play();
        });

        return button;
    }

    // Update board display and labels
    private void updateBoard() {
        int[] board = awaleBoard.getBoard();

        for (int i = 0; i < NUM_HOLES; i++) {
            player2Buttons[i].setText(String.valueOf(board[i]));
        }
        for (int i = NUM_HOLES; i < TOTAL_HOLES; i++) {
            player1Buttons[i - NUM_HOLES].setText(String.valueOf(board[i]));
        }

        player1Label.setText("Player 1 Seeds: " + awaleBoard.getPlayer1Seeds());
        player2Label.setText("Player 2 Seeds: " + awaleBoard.getPlayer2Seeds());
    }

    // End game logic
    private void endGame() {
        statusLabel.setText("Game Over!");
        for (Button button : player1Buttons) {
            button.setDisable(true);
        }
        for (Button button : player2Buttons) {
            button.setDisable(true);
        }
        restartButton.setDisable(false);
    }

    private void resetGame() {
        awaleBoard.resetBoard(); // Assuming you have a resetBoard method in your AwaleBoard class
        updateBoard(); // Update the UI to reflect the reset state
        statusLabel.setText("Player 1's Turn"); // Reset the status label
        restartButton.setDisable(true); // Disable the restart button again
        for (Button button : player1Buttons) {
            button.setDisable(false); // Re-enable player 1 buttons
        }
        for (Button button : player2Buttons) {
            button.setDisable(false); // Re-enable player 2 buttons
        }
    }
}
