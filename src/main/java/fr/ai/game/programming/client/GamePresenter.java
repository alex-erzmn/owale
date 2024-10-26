package fr.ai.game.programming.client;

import fr.ai.game.programming.common.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

import java.util.List;
import java.util.Optional;

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
    private Player player1;
    private Player player2;

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
        StackPane rootPane = new StackPane();

        rootPane.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #4a90e2, #50e3c2);");

        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        GridPane grid = setupBoard();
        grid.setStyle("-fx-background-color: rgba(255, 255, 255, 0.7); -fx-background-radius: 20; -fx-padding: 20; "
                + "-fx-border-radius: 20; -fx-border-color: rgba(0, 0, 0, 0.2); -fx-border-width: 2px;");
        grid.setEffect(new DropShadow(10, Color.gray(0.3)));

        setupLabelsAndRestartButton();

        layout.getChildren().addAll(grid, createStatusBar(), createRestartBox());

        rootPane.getChildren().add(layout);

        updateBoard();
        performNextMove();

        return new Scene(rootPane, 800, 500);
    }

    private HBox createStatusBar() {
        HBox statusBar = new HBox(40, player1Label, statusLabel, player2Label);
        statusBar.setAlignment(Pos.CENTER);
        statusBar.setPadding(new Insets(20));
        statusBar.setStyle("-fx-background-color: rgba(255,255,255,0.8); -fx-padding: 15; -fx-background-radius: 10;");

        player1Label.setTextFill(Color.web("#4a90e2"));
        player2Label.setTextFill(Color.web("#4a90e2"));
        statusLabel.setTextFill(Color.web("#50e3c2"));
        statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        return statusBar;
    }

    private HBox createRestartBox() {
        restartButton = createStyledButton("Back to Start");
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

        setupPlayerButtons(grid);
        return grid;
    }

    private void setupPlayerButtons(GridPane grid) {
        // Top row (holes 1 to 8)
        for (int i = 0; i < 8; i++) {
            // Determine the correct hole index for the top row
            int holeIndex = i + 1; // 1, 2, 3, ..., 8

            // Player 1 owns odd holes, Player 2 owns even holes
            if (holeIndex % 2 != 0) {
                // Odd index belongs to Player 1
                player1Buttons[i / 2] = createButton(holeIndex - 1, player1 instanceof HumanPlayer);
                grid.add(player1Buttons[i / 2], i, 0); // Row 0
            } else {
                // Even index belongs to Player 2
                player2Buttons[i / 2] = createButton(holeIndex - 1, player2 instanceof HumanPlayer);
                player2Buttons[i / 2].setDisable(true);
                grid.add(player2Buttons[i / 2], i, 0); // Row 0
            }
        }

        // Bottom row (holes 16 to 9, in reverse)
        for (int i = 0; i < 8; i++) {
            // Determine the correct hole index for the bottom row
            int holeIndex = 16 - i; // 16, 15, 14, ..., 9

            // Player 1 owns odd holes, Player 2 owns even holes
            if (holeIndex % 2 != 0) {
                // Odd index belongs to Player 1
                player1Buttons[4 + (i / 2)] = createButton(holeIndex - 1, player1 instanceof HumanPlayer);
                grid.add(player1Buttons[4 + (i / 2)], i, 1); // Row 1
            } else {
                // Even index belongs to Player 2
                player2Buttons[4 + (i / 2)] = createButton(holeIndex - 1, player2 instanceof HumanPlayer);
                player2Buttons[4 + (i / 2)].setDisable(true);
                grid.add(player2Buttons[4 + (i / 2)], i, 1); // Row 1
            }
        }
    }



    private Button createButton(int index, boolean isHumanPlayer) {
        Button button = new Button();
        button.setPrefSize(200, 200);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        button.setStyle("-fx-background-color: #F4A460; -fx-border-color: black; -fx-border-radius: 40; -fx-background-radius: 40;");
        button.setTextFill(Color.WHITE);

        DropShadow shadow = new DropShadow();
        shadow.setOffsetX(3);
        shadow.setOffsetY(3);
        button.setEffect(shadow);

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

        String[] colors = {"BLUE", "RED"};
        ChoiceDialog<String> dialog = new ChoiceDialog<>(colors[0], colors);
        dialog.setTitle("Choose Seed Color");
        dialog.setHeaderText("Select the color of the seeds you want to sow:");
        dialog.setContentText("Color:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String selectedColor = result.get();

            Seed.Color chosenColor;
            if (selectedColor.equals("BLUE")) {
                chosenColor = Seed.Color.BLUE;
            } else if (selectedColor.equals("RED")) {
                chosenColor = Seed.Color.RED;
            } else {
                sceneManager.showError("Invalid color selection!");
                return;
            }

            if (awaleBoard.getBoard()[index].isEmpty() || !awaleBoard.hasSeeds(index, chosenColor)) {
                sceneManager.showError("Invalid move! Not enough seeds of the selected color.");
                return;
            }

            System.out.println("Player 1 chose to sow seeds from hole " + index);
            awaleBoard.sowSeeds(index, chosenColor);

            updateBoard();
            disableAllButtons();
            awaleBoard.switchPlayer();
            if (checkGameOver()) return;

            performNextMove();
        }
    }

    private void performNextMove() {
        int currentPlayerId = awaleBoard.getCurrentPlayer();
        Player currentPlayer = (currentPlayerId == 1) ? player1 : player2;

        if (currentPlayer instanceof AIPlayer) {

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
        List<Seed>[] board = awaleBoard.getBoard();

        // Update the top row (holes 1 to 8)
        for (int i = 0; i < 8; i++) {
            int holeIndex = i + 1; // Holes 1 to 8
            if (holeIndex % 2 != 0) {
                // Odd hole index belongs to Player 1
                player1Buttons[i / 2].setGraphic(createColoredText(board[holeIndex-1]));
            } else {
                // Even hole index belongs to Player 2
                player2Buttons[i / 2].setGraphic(createColoredText(board[holeIndex-1]));
            }
        }

        // Update the bottom row (holes 16 to 9 in reverse)
        for (int i = 0; i < 8; i++) {
            int holeIndex = 16 - i; // Holes 16 to 9
            if (holeIndex % 2 != 0) {
                // Odd hole index belongs to Player 1
                player1Buttons[4 + (i / 2)].setGraphic(createColoredText(board[holeIndex-1]));
            } else {
                // Even hole index belongs to Player 2
                player2Buttons[4 + (i / 2)].setGraphic(createColoredText(board[holeIndex-1]));
            }
        }

        // Update player labels with the seed counts
        player1Label.setText("Player 1 Seeds: " + awaleBoard.getPlayer1Seeds());
        player2Label.setText("Player 2 Seeds: " + awaleBoard.getPlayer2Seeds());

        // Debugging information: print seed counts for each hole
        for (int i = 0; i < TOTAL_HOLES; i++) {
            int blueCount = (int) board[i].stream().filter(seed -> seed.getColor() == Seed.Color.BLUE).count();
            int redCount = (int) board[i].stream().filter(seed -> seed.getColor() == Seed.Color.RED).count();
            System.out.println("Hole " + i + ": Blue Seeds: " + blueCount + ", Red Seeds: " + redCount);
        }
    }



    /**
         * Create a TextFlow containing the colored seed counts.
         * @param seeds The list of seeds in the hole.
         * @return TextFlow with blue and red colored numbers.
         */
    private TextFlow createColoredText(List<Seed> seeds) {
        long blueSeeds = seeds.stream().filter(seed -> seed.getColor() == Seed.Color.BLUE).count();
        long redSeeds = seeds.stream().filter(seed -> seed.getColor() == Seed.Color.RED).count();

        Text blueText = new Text(blueSeeds > 0 ? String.valueOf(blueSeeds) : "");
        blueText.setFill(Color.BLUE);
        blueText.setFont(Font.font("Arial", FontWeight.BOLD, 26));

        Text redText = new Text(redSeeds > 0 ? " " + redSeeds : "");
        redText.setFill(Color.RED);
        redText.setFont(Font.font("Arial", FontWeight.BOLD, 26));

        return new TextFlow(blueText, redText);
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
        // Enable buttons for Player 1
        if (playerId == 1) {
            // Enable all Player 1's buttons
            for (Button button : player1Buttons) {
                button.setDisable(false);
            }

            // Disable all Player 2's buttons
            for (Button button : player2Buttons) {
                button.setDisable(true);
            }
        }
        // Enable buttons for Player 2
        else {
            // Enable all Player 2's buttons
            for (Button button : player2Buttons) {
                button.setDisable(false);
            }

            // Disable all Player 1's buttons
            for (Button button : player1Buttons) {
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
