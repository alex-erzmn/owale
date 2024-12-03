package fr.ai.game.programming.mqtt.service;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Service;

import fr.phenix333.logger.MyLogger;

import java.util.function.Consumer;

@Service
public class MqttSubscribe implements MqttCallback {

    private static final MyLogger L = MyLogger.create(MqttSubscribe.class);

    private Consumer<String> messageCallback;

    private String mqttHost = "test.mosquitto.org";

    private String mqttPort = "1883";

    private String mqttUser = "Alex";

    private String mqttOpponent = "Nolan";

    /**
     *  Initialise variables for receiving messages and subscribing to the
     *  topic
     */
    public void subscribeMqtt() {
        L.function("Initialise variables for receiving messages and subscribing to the topic");

        L.info("New part : {} VS {}", this.mqttUser, this.mqttOpponent);

        String broker = String.format("tcp://%s:%s", this.mqttHost, this.mqttPort);

        L.info("Broker mqtt : {}", broker);

        try {
            MqttClient client = new MqttClient(broker, MqttAsyncClient.generateClientId());

            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            client.connect(connOpts);
            client.setCallback(this);
            client.subscribe(String.format("awale/%s", this.mqttUser));
        } catch (MqttException e) {
            L.error("An MQTT error has been logged", e);

            // I assume that since the project is based on MQTT, if the
            // function to receive a message no longer works, the project cannot
            // work
            System.exit(-1);
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        L.error("Lost connection");

        System.exit(-1);
    }

    public void setCallback(Consumer<String> callback) {
        this.messageCallback = callback;
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        L.info("New message arriving: {}", message.toString());

        // Invoke the callback if set
        if (messageCallback != null) {
            messageCallback.accept(message.toString());
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        L.warn("Delivery complete");
    }

}