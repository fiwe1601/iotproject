package com.project.iotproject.MQTTServer.common;

import com.project.iotproject.MQTTServer.common.listener.ClientConnectionListener;

public class MqttClient {
    private String clientId;
    private ClientConnectionListener<MqttMessage> mqttClientConnection;

    public MqttClient(String clientId, ClientConnectionListener<MqttMessage> mqttClientConnection){
        setClientId(clientId);
        setClientConnection(mqttClientConnection);
    }

    public MqttClient setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }
    public String getClientId() {
        return clientId;
    }

    public MqttClient setClientConnection(ClientConnectionListener<MqttMessage> mqttClientConnection) {
        this.mqttClientConnection = mqttClientConnection;
        return this;
    }
    public ClientConnectionListener<MqttMessage> getClientConnection() {
        return mqttClientConnection;
    }

    public void sendMessage(MqttMessage mqttMessage) {
        mqttClientConnection.sendMessage(mqttMessage);
    }

    public void clientConnectionClose() {
        mqttClientConnection.clientConnectionClose();
    }

    public boolean isConnected() {
        return mqttClientConnection.isConnected();
    }

    public void disconnect(){
        mqttClientConnection.disconnect();
    }
    
}
