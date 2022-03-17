package com.project.iotproject.MQTTServer.server;

import java.util.HashMap;

import com.project.iotproject.MQTTServer.common.MqttClient;
import com.project.iotproject.MQTTServer.common.MqttMessage;

/** Container with all the connected clients */
public class MqttClientHolder {
                                                        // Key, value.
    private HashMap<Integer, String> mqttClientIdMap;   // 1,2,3... , ClientId
    private HashMap<String, MqttClient> mqttClientMap;  // ClientId , MqttClient

    public MqttClientHolder() {
        this.mqttClientIdMap = new HashMap<>();
        this.mqttClientMap = new HashMap<>();
    }

    public void sendMessage(String _clientId, MqttMessage _Message) {
        mqttClientMap.forEach((clientId, mqttClient) -> {
            if(mqttClient.getClientId().equals(_clientId)) {
                mqttClient.sendMessage(_Message);
            }
            else{
                try {
                    throw new Exception("Cant find Client to send Message!");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void addClient(Integer clientIndex, MqttClient client) { // Key, value.
        this.mqttClientIdMap.put(clientIndex, client.getClientId());// 1,2,3... , ClientiD
        this.mqttClientMap.put(client.getClientId(), client);       // ClientID , MqttClient
    }

    public void removeClient(int clientIndex) throws Exception {
        try {
            String mqttClientId = getClientId(clientIndex);
            MqttClient mqttClient = mqttClientMap.get(mqttClientId);
            mqttClient.clientConnectionClose();
            mqttClientIdMap.remove(clientIndex);
            mqttClientMap.remove(mqttClientId); 
        } catch (Exception e) {
            throw new Exception("Unable to Remove Client!");
        }
    }

    public HashMap<Integer, String> getClientIdMap(){
        return this.mqttClientIdMap;
    }

    public HashMap<String, MqttClient> getClientsMap(){
        return this.mqttClientMap;
    }

    public String getClientId(int clientIndex){
        return this.mqttClientIdMap.get(clientIndex);
    }
    
    public MqttClient getMqttClient(int clientIndex) {
        //String clientId = String.valueOf(clientIndex);
        return this.mqttClientMap.get(getClientId(clientIndex));
    }


}
