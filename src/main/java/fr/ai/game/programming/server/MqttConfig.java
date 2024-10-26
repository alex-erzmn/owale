package fr.ai.game.programming.server;
/*
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.MessageHandler;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Payload;

@Configuration
@IntegrationComponentScan
public class MqttConfig {

    private static final String MQTT_BROKER_URL = "tcp://test.mosquitto.org:1883";
    private static final String CLIENT_ID = "gameClientId";

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        factory.setConnectionOptions(options);
        return factory;
    }

    @Bean
    public MqttPahoMessageDrivenChannelAdapter mqttInbound() {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(mqttClientFactory(), CLIENT_ID, "awale/game/player2");
        adapter.setOutputChannelName("mqttInputChannel");
        return adapter;
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler mqttMessageHandler() {
        return message -> {
            String payload = (String) message.getPayload();
            System.out.println("Received message: " + payload);
            // Handle the received message (e.g., update game state)
        };
    }
}
*/