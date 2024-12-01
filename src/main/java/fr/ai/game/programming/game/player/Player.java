package fr.ai.game.programming.game.player;

import fr.ai.game.programming.game.elements.Board;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 * Interface for a player in the Awale game.
 */
public interface Player {

    /**
     * Makes a move on the given board.
     *
     * @param board the board on which to make the move
     */
    void makeMove(Board board) throws MqttException;
}