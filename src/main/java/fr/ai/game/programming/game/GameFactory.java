package fr.ai.game.programming.game;

import fr.ai.game.programming.game.elements.Board;
import fr.ai.game.programming.game.player.*;
import fr.ai.game.programming.mqtt.MQTTService;
import org.eclipse.paho.client.mqttv3.MqttException;


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
                MQTTService mqttService = initializeMQTTService();
                MQTTOpponent player1 = new MQTTOpponent(mqttService);
                MQTTAIPlayer player2 = new MQTTAIPlayer(new AIManager(board), mqttService);
                return new Game(board, player1, player2);
            }
            default -> throw new IllegalArgumentException("Unsupported game mode: " + gameMode);
        }
    }

    /**
     * Initializes the MQTTService, handling any exceptions and providing proper logging.
     *
     * @return An instance of MQTTService if initialization succeeds, or null if it fails.
     */
    private static MQTTService initializeMQTTService() {
        String brokerUrl = "tcp://test.mosquitto.org:1883"; // MQTT broker URL
        String clientId = "Alex"; // Unique client ID
        String topic = "game/input"; // MQTT topic

        try {
            MQTTService mqttService = new MQTTService(brokerUrl, clientId, topic);
            System.out.println("Successfully initialized MQTTService with topic: " + topic);
            return mqttService;
        } catch (MqttException e) {
            System.err.println("Failed to initialize MQTTService: " + e.getMessage());
            e.printStackTrace();
            return null; // Return null if initialization fails
        }
    }
}


