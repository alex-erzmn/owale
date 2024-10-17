package fr.ai.game.programming.client;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import lombok.Setter;

/**
 * Manages the scenes of the application.
 */
public class SceneManager {
    private final Stage primaryStage;
    @Setter
    private Scene startScene;
    @Setter
    private Scene gameScene;

    public SceneManager(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void showStartScene() {
        if (startScene != null) {
            primaryStage.setScene(startScene);
            primaryStage.show();
        }
    }

    public void showGameScene() {
        if (gameScene != null) {
            primaryStage.setScene(gameScene);
            primaryStage.show();
        }
    }

    public void showSpectatorScene() {
        if (gameScene != null) {
            primaryStage.setScene(gameScene);
            primaryStage.show();
        }
    }

    public void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.showAndWait();
    }

    public void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message, ButtonType.OK);
        alert.showAndWait();
    }

    public void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.showAndWait();
    }
}
