package fr.ai.game.programming;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import lombok.Setter;

public class SceneManager {
    private final Stage stage;
    @Setter
    private Scene startScene;
    @Setter
    private Scene gameScene;

    public SceneManager(Stage stage) {
        this.stage = stage;
    }

    public void showStartScene() {
        if (startScene != null) {
            stage.setScene(startScene);
            stage.show();
        }
    }

    public void showGameScene() {
        if (gameScene != null) {
            stage.setScene(gameScene);
            stage.show();
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
