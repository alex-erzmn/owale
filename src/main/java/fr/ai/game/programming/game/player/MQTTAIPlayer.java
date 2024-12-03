package fr.ai.game.programming.game.player;

import fr.ai.game.programming.game.elements.Board;
import fr.ai.game.programming.mqtt.service.MqttPublish;
import org.eclipse.paho.client.mqttv3.MqttException;

public class MQTTAIPlayer extends AIPlayer {
    private final MqttPublish mqttPublish;

    public MQTTAIPlayer(AIManager aiManager, MqttPublish mqttPublish) {
        super(aiManager);
        this.mqttPublish = mqttPublish;
    }

    @Override
    public void makeMove(Board board) throws MqttException {
        System.out.println("AI is making a move...");

        int currentPlayerId = board.getCurrentPlayer();

        Move aiMove;

        if (currentPlayerId == 1) {
            aiMove = aiManager.findRandomMove(1);
        } else {
            aiMove = aiManager.findRandomMove(2);
        }
        System.out.println("AI chose to sow " + aiMove.hole() + " " + aiMove.color() + " seeds.");
        board.sowSeeds(aiMove.hole(), aiMove.color());

        System.out.println("Sending my move: " + aiMove);
        String message = aiMove.toString();
        mqttPublish.publish(message);
    }
}
