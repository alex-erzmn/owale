package fr.ai.game.programming.client;

import fr.ai.game.programming.common.AIManager;
import fr.ai.game.programming.common.AIPlayer;
import fr.ai.game.programming.common.AwaleBoard;
import fr.ai.game.programming.common.HumanPlayer;
import fr.ai.game.programming.common.Player;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import static fr.ai.game.programming.common.AwaleBoard.PLAYER_HOLES;
import static fr.ai.game.programming.common.AwaleBoard.TOTAL_HOLES;

/**
 * Presenter for the game scene of the Awale game.
 * This scene is shown when the game is started.
 */
public class GamePresenter {

    private final AwaleBoard awaleBoard;
    private final SceneManager sceneManager;
    private Button[] player1Buttons = new Button[PLAYER_HOLES];
    private Button[] player2Buttons = new Button[PLAYER_HOLES];
    private Button restartButton;
    private Label player1Label = new Label("Player 1 Seeds: 0");
    private Label player2Label = new Label("Player 2 Seeds: 0");
    private Label statusLabel = new Label("Player 1's Turn");
    private Player player1;  // Human or AI player
    private Player player2;  // AI player

    public GamePresenter(SceneManager sceneManager, boolean isPlayerVsAI) {
        this.awaleBoard = new AwaleBoard();
        this.sceneManager = sceneManager;

        if (isPlayerVsAI) {
            this.player1 = new HumanPlayer();
            this.player2 = new AIPlayer(new AIManager(awaleBoard));
        } else {
            this.player1 = new AIPlayer(new AIManager(awaleBoard));
            this.player2 = new AIPlayer(new AIManager(awaleBoard));
        }
    }

    public Scene createGameScene() {
        // Full background covering the entire scene
        StackPane rootPane = new StackPane();

        // Create a gradient background for the entire scene
        rootPane.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #4a90e2, #50e3c2);");

        // Board and controls container
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        // Awale gameboard with transparent background and rounded corners
        GridPane grid = setupBoard();
        grid.setStyle("-fx-background-color: rgba(255, 255, 255, 0.7); -fx-background-radius: 20; -fx-padding: 20; "
                + "-fx-border-radius: 20; -fx-border-color: rgba(0, 0, 0, 0.2); -fx-border-width: 2px;");
        grid.setEffect(new DropShadow(10, Color.gray(0.3)));

        setupLabelsAndRestartButton();

        layout.getChildren().addAll(grid, createStatusBar(), createRestartBox());

        // Add everything to root pane
        rootPane.getChildren().add(layout);

        updateBoard();
        performNextMove(); // Automatically check and proceed with the next move (whether AI or human)

        return new Scene(rootPane, 800, 500);
    }

    private HBox createStatusBar() {
        HBox statusBar = new HBox(40, player1Label, statusLabel, player2Label);
        statusBar.setAlignment(Pos.CENTER);
        statusBar.setPadding(new Insets(20));
        statusBar.setStyle("-fx-background-color: rgba(255,255,255,0.8); -fx-padding: 15; -fx-background-radius: 10;");

        player1Label.setTextFill(Color.web("#4a90e2")); // Similar color scheme
        player2Label.setTextFill(Color.web("#4a90e2"));
        statusLabel.setTextFill(Color.web("#50e3c2"));
        statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        return statusBar;
    }

    private HBox createRestartBox() {
        restartButton = createStyledButton("Back to Start"); // Styled button similar to StartPresenter
        restartButton.setOnAction(e -> sceneManager.showStartScene());
        HBox restartBox = new HBox(restartButton);
        restartBox.setAlignment(Pos.CENTER);
        restartBox.setPadding(new Insets(20));
        return restartBox;
    }

    private void setupLabelsAndRestartButton() {
        player1Label.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        player2Label.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
    }

    private GridPane setupBoard() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(15);
        grid.setVgap(15);

        // Setup player buttons in grid
        setupPlayerButtons(grid);
        return grid;
    }

    private void setupPlayerButtons(GridPane grid) {
        // Player 2 buttons
        for (int i = PLAYER_HOLES - 1; i >= 0; i--) {
            player2Buttons[i] = createButton(i, false); // Initially disabled
            grid.add(player2Buttons[i], PLAYER_HOLES - i - 1, 0);
        }

        // Player 1 buttons
        for (int i = PLAYER_HOLES; i < TOTAL_HOLES; i++) {
            player1Buttons[i - PLAYER_HOLES] = createButton(i, player1 instanceof HumanPlayer); // Enabled for human player
            grid.add(player1Buttons[i - PLAYER_HOLES], i - PLAYER_HOLES, 1);
        }
    }

    private Button createButton(int index, boolean isHumanPlayer) {
        Button button = new Button("4"); // Initial seed count
        button.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        button.setPrefSize(80, 80);
        button.setStyle("-fx-background-color: #F4A460; -fx-border-color: black; -fx-border-radius: 40; -fx-background-radius: 40;");
        button.setTextFill(Color.WHITE);

        DropShadow shadow = new DropShadow();
        shadow.setOffsetX(3);
        shadow.setOffsetY(3);
        button.setEffect(shadow);

        // Hover effect similar to StartPresenter
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #FF8C00; -fx-border-color: black; -fx-border-radius: 40; -fx-background-radius: 40;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #F4A460; -fx-border-color: black; -fx-border-radius: 40; -fx-background-radius: 40;"));

        if (isHumanPlayer) {
            button.setOnAction(e -> makeHumansMove(index));
        } else {
            button.setOnAction(null);
        }

        return button;
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: #50e3c2; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 18px; " +
                "-fx-padding: 10 20; " +
                "-fx-background-radius: 30; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0.0, 0, 1);");

        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #4a90e2; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 18px; " +
                "-fx-padding: 10 20; " +
                "-fx-background-radius: 30; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 5, 0.0, 0, 1);"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #50e3c2; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 18px; " +
                "-fx-padding: 10 20; " +
                "-fx-background-radius: 30; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0.0, 0, 1);"));

        return button;
    }

    // Handle human player move
    private void makeHumansMove(int index) {

        if (awaleBoard.getCurrentPlayer() != 1) {
            sceneManager.showError("It's not your turn!");
            return;
        }

        if (awaleBoard.getBoard()[index] <= 1) {
            sceneManager.showError("Invalid move! Not enough seeds.");
            return;
        }

        awaleBoard.sowSeeds(index);

        updateBoard();
        disableAllButtons();
        awaleBoard.switchPlayer();
        if (checkGameOver()) return;

        performNextMove();
    }

    private void performNextMove() {
        int currentPlayerId = awaleBoard.getCurrentPlayer();
        Player currentPlayer = (currentPlayerId == 1) ? player1 : player2;

        if (currentPlayer instanceof AIPlayer) {

            // Delay AI move for 2 seconds to make it visible
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), event -> {
                ((AIPlayer) currentPlayer).makeMove(awaleBoard);
                updateBoard();
                awaleBoard.switchPlayer();
                if (checkGameOver()) return;

                performNextMove();
            }));

            timeline.setCycleCount(1);
            timeline.play();
        } else if (currentPlayer instanceof HumanPlayer) {
            enablePlayerButtons(1);
            ((HumanPlayer) currentPlayer).makeMove(awaleBoard);
        }

        updateTurnDisplay();
    }

    private void updateTurnDisplay() {
        if (awaleBoard.getCurrentPlayer() == 1) {
            statusLabel.setText("Player 1's Turn");
        } else {
            statusLabel.setText("Player 2's Turn");
        }
    }

    private void updateBoard() {
        int[] board = awaleBoard.getBoard();

        for (int i = 0; i < PLAYER_HOLES; i++) {
            player2Buttons[i].setText(String.valueOf(board[i]));
        }
        for (int i = PLAYER_HOLES; i < TOTAL_HOLES; i++) {
            player1Buttons[i - PLAYER_HOLES].setText(String.valueOf(board[i]));
        }

        player1Label.setText("Player 1 Seeds: " + awaleBoard.getPlayer1Seeds());
        player2Label.setText("Player 2 Seeds: " + awaleBoard.getPlayer2Seeds());
    }

    private boolean checkGameOver() {
        if (awaleBoard.checkGameOver()) {
            endGame();
            return true;
        }
        return false;
    }

    private void endGame() {
        statusLabel.setText("Game Over!");
        for (Button button : player1Buttons) {
            button.setDisable(true);
        }
        for (Button button : player2Buttons) {
            button.setDisable(true);
        }
    }

    private void enablePlayerButtons(int playerId) {
        if (playerId == 1) {
            for (int i = 0; i < player1Buttons.length; i++) {
                player1Buttons[i].setDisable(awaleBoard.getBoard()[PLAYER_HOLES + i] <= 1);
            }
        } else {
            for (Button button : player2Buttons) {
                button.setDisable(true);
            }
        }
    }

    private void disableAllButtons() {
        for (Button button : player1Buttons) {
            button.setDisable(true);
        }
        for (Button button : player2Buttons) {
            button.setDisable(true);
        }
    }
}
