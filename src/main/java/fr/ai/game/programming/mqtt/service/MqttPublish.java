package fr.ai.game.programming.mqtt.service;

import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.stereotype.Service;

import fr.phenix333.logger.MyLogger;

@Service
public class MqttPublish {

    private static final MyLogger L = MyLogger.create(MqttSubscribe.class);

    private String mqttHost = "test.mosquitto.org";

    private String mqttPort = "1883";

    private String mqttUser = "Alex";

    private String mqttOpponent = "Nolan";

    private MqttClient client;

    /**
     * Initialise mqttPublish so that it can be used again later
     */
    public MqttPublish() {
        L.function("Initialise mqttPublish so you can use it again later");

        String broker = String.format("tcp://%s:%s", this.mqttHost, this.mqttPort);

        MemoryPersistence persistence = new MemoryPersistence();

        try {
            this.client = new MqttClient(broker, MqttAsyncClient.generateClientId(), persistence);

            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            client.connect(connOpts);
        } catch (MqttException e) {
            L.error("An MQTT error has been logged", e);

            // I assume that since the project is based on MQTT, if the
            // function to send a message no longer works, the project cannot
            // work
            System.exit(-1);
        }
    }

    /**
     * Send a message on a specific topic
     *
     * @param theMessage -> String : the message to send
     */
    public void publish(String theMessage) {
        L.function("Send a message on a specific topic | message : {}", theMessage);

        MqttMessage message = new MqttMessage(theMessage.getBytes());

        try {
            this.client.publish(String.format("awale/%s", this.mqttOpponent), message);
        } catch (MqttException e) {
            L.error("An MQTT error has been logged", e);

            //  I assume that since the project is based on MQTT,
            //  if the function for sending a message no longer works,
            //  the project cannot function.
            System.exit(-1);
        }

        L.info("New message sent : {}", theMessage);
    }

}
