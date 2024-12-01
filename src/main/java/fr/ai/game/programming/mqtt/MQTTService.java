package fr.ai.game.programming.mqtt;

import org.eclipse.paho.client.mqttv3.*;

public class MQTTService {

    private final String brokerUrl;
    private final String clientId;
    private final String topic;
    private final MqttClient mqttClient;

    public MQTTService(String brokerUrl, String clientId, String topic) throws MqttException {
        this.brokerUrl = brokerUrl;
        this.clientId = clientId;
        this.topic = topic;
        this.mqttClient = new MqttClient(brokerUrl, clientId);
    }

    public void connect() throws MqttException {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        mqttClient.connect(options);
        System.out.println("Connected to MQTT broker at " + brokerUrl);
    }

    public void subscribe(MqttCallback callback) throws MqttException {
        mqttClient.setCallback(callback);
        mqttClient.subscribe(topic);
        System.out.println("Subscribed to topic: " + topic);
    }

    public void disconnect() throws MqttException {
        mqttClient.disconnect();
        System.out.println("Disconnected from MQTT broker.");
    }

    public void publish(String message) throws MqttException {
        mqttClient.publish(topic, new MqttMessage(message.getBytes()));
    }
}