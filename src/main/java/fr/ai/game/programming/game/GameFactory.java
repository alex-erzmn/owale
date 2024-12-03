package fr.ai.game.programming.game;

import fr.ai.game.programming.game.elements.Board;
import fr.ai.game.programming.game.player.*;
import fr.ai.game.programming.mqtt.service.MqttPublish;
import fr.ai.game.programming.mqtt.service.MqttSubscribe;



/**
 * Factory class for creating Awale games.
 */
public class GameFactory {

    private GameFactory() {
        // Prevent instantiation
    }

    public static Game createAwaleGame(GameMode gameMode) {
        switch (gameMode) {
            case PLAYER_VS_AI_LOCAL -> {
                Board board = new Board();
                HumanPlayer player1 = new HumanPlayer();
                AIPlayer player2 = new AIPlayer(new AIManager(board));
                return new Game(board, player1, player2);
            }
            case AI_VS_AI_LOCAL -> {
                Board board = new Board();
                AIPlayer player1 = new AIPlayer(new AIManager(board));
                AIPlayer player2 = new AIPlayer(new AIManager(board));
                return new Game(board, player1, player2);
            }
            case AI_VS_AI_MQTT -> {
                Board board = new Board();
                MqttSubscribe mqttSubscribe = new MqttSubscribe();
                MQTTOpponent player1 = new MQTTOpponent(mqttSubscribe);
                MqttPublish mqttPublish = new MqttPublish();
                MQTTAIPlayer player2 = new MQTTAIPlayer(new AIManager(board), mqttPublish);
                return new Game(board, player1, player2);
            }
            default -> throw new IllegalArgumentException("Unsupported game mode: " + gameMode);
        }
    }
}


