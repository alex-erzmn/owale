package fr.ai.game.programming.game.player;

import fr.ai.game.programming.game.elements.Board;
import fr.ai.game.programming.game.elements.Seed;
import fr.ai.game.programming.mqtt.MQTTService;
import lombok.Setter;

import java.util.function.Consumer;

public class MQTTOpponent implements Player {
    private final MQTTService mqttService;
    @Setter
    private Consumer<Void> onMoveProcessed;

    public MQTTOpponent(MQTTService mqttService) {
        this.mqttService = mqttService;
    }

    @Override
    public void makeMove(Board board) {
        processMoveFromServer("4,B", board);
    }

    public void processMoveFromServer(String moveData, Board board) {
        // Process the received move
        // Example: parse moveData, make the move
        int holeIndex = parseHoleIndex(moveData);
        Seed.Color chosenColor = parseColor(moveData);
        board.sowSeeds(holeIndex, chosenColor);

        // Notify game that the move is complete
        if (onMoveProcessed != null) {
            onMoveProcessed.accept(null);
        }
    }

    private int parseHoleIndex(String moveData) {
        // Parse hole index from move data
        return Integer.parseInt(moveData.split(",")[0]);
    }

    private Seed.Color parseColor(String moveData) {
        // Parse seed color from move data
        return Seed.Color.valueOf(moveData.split(",")[1]);
    }
}
