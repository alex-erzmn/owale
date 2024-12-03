package fr.ai.game.programming.game.player;

import fr.ai.game.programming.game.elements.Board;
import fr.ai.game.programming.game.elements.Seed;
import fr.ai.game.programming.mqtt.service.MqttSubscribe;
import lombok.Setter;

import java.util.function.Consumer;

public class MQTTOpponent implements Player {
    private final MqttSubscribe mqttSubscribe; // Use MqttSubscribe to listen for moves
    @Setter
    private Consumer<Void> onMoveProcessed;

    public MQTTOpponent(MqttSubscribe mqttSubscribe) {
        this.mqttSubscribe = mqttSubscribe;


    }

    @Override
    public void makeMove(Board board) {
        // Set up MQTT message callback
        this.mqttSubscribe.setCallback(moveData -> processMoveFromServer(moveData, board));
        // Wait for the opponent's move to arrive over MQTT
        mqttSubscribe.subscribeMqtt();
    }

    private void processMoveFromServer(String moveData, Board board) {
        // Parse and process the move data
        int holeIndex = parseHoleIndex(moveData);
        Seed.Color chosenColor = parseColor(moveData);

        // Apply the move to the board
        board.sowSeeds(holeIndex, chosenColor);

        // Notify the game loop that the move is done
        if (onMoveProcessed != null) {
            onMoveProcessed.accept(null);
        }
    }

    private int parseHoleIndex(String moveData) {
        return Integer.parseInt(moveData.split(",")[0]);
    }

    private Seed.Color parseColor(String moveData) {
        return Seed.Color.valueOf(moveData.split(",")[1]);
    }
}
