package com.project.iotproject.MQTTServer.common.listener;

import com.project.iotproject.MQTTServer.common.MqttMessage;

public interface MQTTClientMessageListener {
    void onMqttClientMessagePublish(String topic, String payload, MqttMessage message);
    void onMqttClientMessageReceived(MqttMessage message);
    void onMqttClientMessageConnect();
    void onMqttClientMessageSent(MqttMessage message);
}
