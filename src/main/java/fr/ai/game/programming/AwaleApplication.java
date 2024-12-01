package fr.ai.game.programming;

import fr.ai.game.programming.client.SceneManager;
import fr.ai.game.programming.client.StartPresenter;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Main class of the Awalé game.
 */
public class AwaleApplication extends javafx.application.Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.getIcons().add(new Image("/images/seeds.png"));
        primaryStage.setTitle("Awalé Game");
        SceneManager sceneManager = new SceneManager(primaryStage);

        StartPresenter startScenePresenter = new StartPresenter(sceneManager);

        sceneManager.setStartScene(startScenePresenter.createStartScene());

        sceneManager.showStartScene();
    }

}
