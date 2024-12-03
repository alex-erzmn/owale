package fr.ai.game.programming.client;

import fr.ai.game.programming.game.Game;
import fr.ai.game.programming.game.GameStatus;
import fr.ai.game.programming.game.elements.Seed;
import fr.ai.game.programming.game.player.HumanPlayer;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import javax.swing.*;
import java.util.List;
import java.util.Optional;

import static fr.ai.game.programming.game.elements.Board.PLAYER_HOLES;

/**
 * Presenter for the game scene of the Awale game.
 * This scene is shown when the game is started.
 */
public class GamePresenter implements Observer {
    private static final String FONT = "Arial";

    private final Game game;
    private final SceneManager sceneManager;
    private final Button[] player1Buttons = new Button[PLAYER_HOLES];
    private final Button[] player2Buttons = new Button[PLAYER_HOLES];
    private final Label player1Label = new Label("Player 1 Seeds: 0");
    private final Label player2Label = new Label("Player 2 Seeds: 0");
    private final Label statusLabel = new Label("Player 1's Turn");

    public GamePresenter(SceneManager sceneManager, Game game) {
        this.game = game;
        this.sceneManager = sceneManager;
    }

    @Override
    public void onBoardUpdated() {
        updateBoard();
        updateTurnDisplay();
    }

    @Override
    public void onGameOver() {
        GameStatus status = game.getBoard().checkGameStatus();
        showGameOver(status);
    }

    @Override
    public void onEnablePlayerButtons() {
        enablePlayerButtons(1);
    }

    public Scene createGameScene() {
        StackPane rootPane = new StackPane();

        rootPane.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #8B4513, #D2B48C);");

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

        // Start the game loop
        game.start();

        return new Scene(rootPane, 800, 500);
    }

    private HBox createStatusBar() {
        HBox statusBar = new HBox(40, player1Label, statusLabel, player2Label);
        statusBar.setAlignment(Pos.CENTER);
        statusBar.setPadding(new Insets(20));
        statusBar.setStyle("-fx-background-color: rgba(255,255,255,0.8); -fx-padding: 15; -fx-background-radius: 10;");

        player1Label.setTextFill(Color.web("#4a90e2"));
        player2Label.setTextFill(Color.web("#4a90e2"));
        statusLabel.setTextFill(Color.web("#F4A460"));
        statusLabel.setFont(Font.font(FONT, FontWeight.BOLD, 24));

        return statusBar;
    }

    private HBox createRestartBox() {
        Button restartButton = createStyledButton("Back to Start");
        restartButton.setOnAction(e -> {
            game.stop();
            sceneManager.showStartScene();
        });
        HBox restartBox = new HBox(restartButton);
        restartBox.setAlignment(Pos.CENTER);
        restartBox.setPadding(new Insets(20));
        return restartBox;
    }

    private void setupLabelsAndRestartButton() {
        player1Label.setFont(Font.font(FONT, FontWeight.BOLD, 20));
        player2Label.setFont(Font.font(FONT, FontWeight.BOLD, 20));
        statusLabel.setFont(Font.font(FONT, FontWeight.BOLD, 24));
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
                player1Buttons[i / 2] = createButton(holeIndex - 1, game.getPlayer1() instanceof HumanPlayer);
                grid.add(player1Buttons[i / 2], i, 0); // Row 0
            } else {
                // Even index belongs to Player 2
                player2Buttons[i / 2] = createButton(holeIndex - 1, game.getPlayer2() instanceof HumanPlayer);
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
                player1Buttons[4 + (i / 2)] = createButton(holeIndex - 1, game.getPlayer1() instanceof HumanPlayer);
                grid.add(player1Buttons[4 + (i / 2)], i, 1); // Row 1
            } else {
                // Even index belongs to Player 2
                player2Buttons[4 + (i / 2)] = createButton(holeIndex - 1, game.getPlayer2() instanceof HumanPlayer);
                player2Buttons[4 + (i / 2)].setDisable(true);
                grid.add(player2Buttons[4 + (i / 2)], i, 1); // Row 1
            }
        }
    }

    private Button createButton(int index, boolean isHumanPlayer) {
        Button button = new Button();
        button.setPrefSize(200, 200);
        button.setFont(Font.font(FONT, FontWeight.BOLD, 26));
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
        button.setStyle("-fx-background-color: #F4A460; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 18px; " +
                "-fx-padding: 10 20; " +
                "-fx-background-radius: 30; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0.0, 0, 1);");

        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #FF8C00; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 18px; " +
                "-fx-padding: 10 20; " +
                "-fx-background-radius: 30; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 5, 0.0, 0, 1);"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #F4A460; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 18px; " +
                "-fx-padding: 10 20; " +
                "-fx-background-radius: 30; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0.0, 0, 1);"));

        return button;
    }

    // Handle human player move
    private void makeHumansMove(int index) {
        if (game.getBoard().getCurrentPlayer() != 1) {
            sceneManager.showError("It's not your turn!");
            return;
        }

        boolean hasBlueSeeds = game.getBoard().hasSeeds(index, Seed.Color.BLUE);
        boolean hasRedSeeds = game.getBoard().hasSeeds(index, Seed.Color.RED);
        Optional<String> result = ColorChoiceDialog.showColorChoiceDialog(hasBlueSeeds, hasRedSeeds);

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

            if (game.getBoard().getHoles()[index].isEmpty() || !game.getBoard().hasSeeds(index, chosenColor)) {
                sceneManager.showError("Invalid move! Not enough seeds of the selected color.");
                return;
            }

            System.out.println("Player 1 chose to sow " + chosenColor + " seeds from hole " + index);
            game.getBoard().sowSeeds(index, chosenColor);

            disableAllButtons();
            game.getBoard().switchPlayer();
            updateBoard();
            game.performNextMove();
        }
    }

    private void updateTurnDisplay() {
        if (game.getBoard().getCurrentPlayer() == 1) {
            statusLabel.setText("Player 1's Turn");
        } else {
            statusLabel.setText("Player 2's Turn");
        }
    }

    private void updateBoard() {
        List<Seed>[] board = game.getBoard().getHoles();

        // Update the top row (holes 1 to 8)
        for (int i = 0; i < 8; i++) {
            int holeIndex = i + 1; // Holes 1 to 8
            if (holeIndex % 2 != 0) {
                // Odd hole index belongs to Player 1
                player1Buttons[i / 2].setGraphic(createColoredText(board[holeIndex - 1]));
            } else {
                // Even hole index belongs to Player 2
                player2Buttons[i / 2].setGraphic(createColoredText(board[holeIndex - 1]));
            }
        }

        // Update the bottom row (holes 16 to 9 in reverse)
        for (int i = 0; i < 8; i++) {
            int holeIndex = 16 - i; // Holes 16 to 9
            if (holeIndex % 2 != 0) {
                // Odd hole index belongs to Player 1
                player1Buttons[4 + (i / 2)].setGraphic(createColoredText(board[holeIndex - 1]));
            } else {
                // Even hole index belongs to Player 2
                player2Buttons[4 + (i / 2)].setGraphic(createColoredText(board[holeIndex - 1]));
            }
        }

        // Update player labels with the seed counts
        player1Label.setText("Player 1 Seeds: " + game.getBoard().getPlayer1Seeds());
        player2Label.setText("Player 2 Seeds: " + game.getBoard().getPlayer2Seeds());

        // Print the board layout in the console
        printBoardLayout(board);
    }

    private void printBoardLayout(List<Seed>[] board) {
        final String RESET = "\u001B[0m";
        final String EVEN_COLOR = "\u001B[32m"; // Green for even numbers
        final String ODD_COLOR = "\u001B[33m"; // Yellow for odd numbers
        System.out.println(EVEN_COLOR + "Player 1 Seeds: " + RESET + game.getBoard().getPlayer1Seeds() + " | " + ODD_COLOR + "Player 2 Seeds: " + RESET + game.getBoard().getPlayer2Seeds());
        // Top row (holes 0 to 7)
        for (int i = 0; i < 8; i++) {
            String formattedHole = formatHoleWithColor(i, holeSummary(board[i]));
            System.out.print(formattedHole + " ");
        }
        System.out.println();

        // Bottom row (holes 15 to 8 in reverse)
        for (int i = 15; i >= 8; i--) {
            String formattedHole = formatHoleWithColor(i, holeSummary(board[i]));
            System.out.print(formattedHole + " ");
        }
        System.out.println();
    }

    private String holeSummary(List<Seed> seeds) {
        int blueCount = (int) seeds.stream().filter(seed -> seed.getColor() == Seed.Color.BLUE).count();
        int redCount = (int) seeds.stream().filter(seed -> seed.getColor() == Seed.Color.RED).count();

        // Define ANSI colors
        final String RESET = "\u001B[0m";
        final String BLUE_SEED_COLOR = "\u001B[34m"; // Blue for B seeds
        final String RED_SEED_COLOR = "\u001B[31m";  // Red for R seeds
        final String GREY_COLOR = "\u001B[90m";      // Grey for 0 counts

        // Apply grey color if the count is 0
        String blueText = (blueCount > 0 ? BLUE_SEED_COLOR : GREY_COLOR) + blueCount + "B" + RESET;
        String redText = (redCount > 0 ? RED_SEED_COLOR : GREY_COLOR) + redCount + "R" + RESET;

        return blueText + " " + redText;
    }


    private String formatHoleWithColor(int holeIndex, String holeSummary) {
        // Define ANSI escape codes for colors
        final String RESET = "\u001B[0m"; // Reset color
        final String EVEN_COLOR = "\u001B[32m"; // Green for even numbers
        final String ODD_COLOR = "\u001B[33m"; // Yellow for odd numbers

        // Apply color to parentheses based on hole index (even or odd)
        return (holeIndex % 2 == 0) ? EVEN_COLOR + "(" + RESET + holeSummary + EVEN_COLOR + ")" + RESET
                : ODD_COLOR + "(" + RESET + holeSummary + ODD_COLOR + ")" + RESET;
    }

    /**
     * Create a TextFlow containing the colored seed counts.
     *
     * @param seeds The list of seeds in the hole.
     * @return TextFlow with blue and red colored numbers.
     */
    private TextFlow createColoredText(List<Seed> seeds) {
        long blueSeeds = seeds.stream().filter(seed -> seed.getColor() == Seed.Color.BLUE).count();
        long redSeeds = seeds.stream().filter(seed -> seed.getColor() == Seed.Color.RED).count();

        Text blueText = new Text(blueSeeds > 0 ? String.valueOf(blueSeeds) : "");
        blueText.setFill(Color.BLUE);
        blueText.setFont(Font.font(FONT, FontWeight.BOLD, 26));

        Text redText = new Text(redSeeds > 0 ? " " + redSeeds : "");
        redText.setFill(Color.RED);
        redText.setFont(Font.font(FONT, FontWeight.BOLD, 26));

        return new TextFlow(blueText, redText);
    }

    private void showGameOver(GameStatus status) {
        statusLabel.setText("Game Over!");

        // Disable all buttons
        for (Button button : player1Buttons) {
            button.setDisable(true);
        }
        for (Button button : player2Buttons) {
            button.setDisable(true);
        }

        // Schedule the dialog to be shown later
        Platform.runLater(() -> {
            String message = "Reason: " + status.getReason();
            if (!status.getWinner().equals("Draw")) {
                message += "\nWinner: " + status.getWinner();
            } else {
                message += "\nIt's a draw!";
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
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

class ColorChoiceDialog {

    private ColorChoiceDialog() {
        // Private constructor to prevent instantiation
    }

    public static Optional<String> showColorChoiceDialog(boolean hasBlueSeeds, boolean hasRedSeeds) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Choose Seed Color");

        // Dialog Header
        Text headerText = new Text("Select the color of the seeds you want to sow:");
        headerText.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Create Buttons
        Button blueButton = createColorButton(Color.BLUE, hasBlueSeeds);
        Button redButton = createColorButton(Color.RED, hasRedSeeds);

        // Add action handlers
        blueButton.setOnAction(e -> {
            dialog.setResult("BLUE");
            dialog.close();
        });

        redButton.setOnAction(e -> {
            dialog.setResult("RED");
            dialog.close();
        });

        // Layout buttons
        HBox buttonBox = new HBox(20, blueButton, redButton);
        buttonBox.setAlignment(Pos.CENTER);

        // Add content to dialog
        VBox contentBox = new VBox(20, headerText, buttonBox);
        contentBox.setPadding(new Insets(20));
        contentBox.setAlignment(Pos.CENTER);

        dialog.getDialogPane().setContent(contentBox);
        dialog.getDialogPane().getButtonTypes().clear(); // Remove default buttons

        // Show dialog and wait for user input
        return dialog.showAndWait();
    }

    private static Button createColorButton(Color color, boolean isEnabled) {
        Button button = new Button();
        button.setDisable(!isEnabled); // Disable button if seeds for this color are unavailable
        button.setStyle("-fx-background-color: transparent; -fx-padding: 0;");

        // Rectangle as button graphic
        Rectangle colorIndicator = new Rectangle(150, 100, color);
        colorIndicator.setArcWidth(20);
        colorIndicator.setArcHeight(20);
        colorIndicator.setStroke(Color.BLACK);

        button.setGraphic(colorIndicator);
        button.setPrefSize(150, 100);
        return button;
    }
}