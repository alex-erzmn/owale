package fr.ai.game.programming.client;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Presenter for the start scene of the game.
 * This scene is shown when the game is started.
 */
public class StartPresenter {

    private final SceneManager sceneManager;
    public Button playerVsAiButton;
    public Button aiVsAiButton;

    public StartPresenter(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    public Scene createStartScene() {
        AnchorPane rootPane = new AnchorPane();
        rootPane.setPrefSize(800, 500);

        Rectangle background = new Rectangle(800, 500);
        background.setFill(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#4a90e2")),
                new Stop(1, Color.web("#50e3c2"))
        ));
        rootPane.getChildren().add(background);

        StackPane stackPane = new StackPane();

        VBox layout = new VBox(20);
        layout.setAlignment(javafx.geometry.Pos.CENTER);
        layout.setPadding(new javafx.geometry.Insets(40));

        Label welcomeLabel = new Label("Welcome to Awalé Game!");
        welcomeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        welcomeLabel.setTextFill(Color.WHITE);

        playerVsAiButton = createStyledButton("Player vs AI");
        playerVsAiButton.setOnAction(e -> {
            GamePresenter gameScenePresenter = new GamePresenter(sceneManager, true);
            sceneManager.setGameScene(gameScenePresenter.createGameScene());
            sceneManager.showGameScene();
        });

        aiVsAiButton = createStyledButton("AI vs AI");
        aiVsAiButton.setOnAction(e -> {
            GamePresenter gameScenePresenter = new GamePresenter(sceneManager, false);
            sceneManager.setGameScene(gameScenePresenter.createGameScene());
            sceneManager.showSpectatorScene();
        });

        layout.getChildren().addAll(welcomeLabel, playerVsAiButton, aiVsAiButton);

        stackPane.getChildren().add(layout);

        AnchorPane.setTopAnchor(stackPane, 0.0);
        AnchorPane.setBottomAnchor(stackPane, 0.0);
        AnchorPane.setLeftAnchor(stackPane, 0.0);
        AnchorPane.setRightAnchor(stackPane, 0.0);

        rootPane.getChildren().add(stackPane);

        return new Scene(rootPane, 800, 500);
    }


    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: #50e3c2; " + // Base color
                "-fx-text-fill: white; " +
                "-fx-font-size: 18px; " +
                "-fx-padding: 10 20; " +
                "-fx-background-radius: 30; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0.0, 0, 1);");

        // Add hover effect
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
}
