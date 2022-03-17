package com.project.iotproject.MQTTServer.common.MqttSubscriptionHandler;

import java.util.ArrayList;
import java.util.HashMap;

import com.project.iotproject.MQTTServer.common.MqttClient;
import com.project.iotproject.MQTTServer.common.MqttSubscription;
import com.project.iotproject.MQTTServer.server.MqttClientHolder;

/** Handles Clients and there subscribed topics */
public class MqttSubscriptionHandler {
    HashMap<String, ArrayList<MqttClient>> topicSubscriptionMap;    //<Topic, List<Clients>>. <Key, Value>.
    MqttClientHolder mqttClientHolder;                              //Container with all Connected Clients

    public MqttSubscriptionHandler(MqttClientHolder mqttClientHolder) {
        this.mqttClientHolder = mqttClientHolder;
        this.topicSubscriptionMap = new HashMap<>();
    }

    public void addTopic(ArrayList<MqttSubscription> topicSubscriptionMap, Integer clientIndex) {
        /** add list of topics to topicSubscriptionMap with correspoding clientIndex */
        topicSubscriptionMap.forEach(topic -> { addTopic(topic, clientIndex); });
    }
    
    public void addTopic(MqttSubscription topic, Integer clientIndex) {
        //If Topic dosent exist in map -> add it to map with new arraylist
        if(!topicSubscriptionMap.containsKey(topic.getTopicName())) {     
            topicSubscriptionMap.put(topic.getTopicName(), new ArrayList<>());
        }
        //Get Arraylist with correspoding Topic. Add MqttClient with corresponding clientIndex to arraylist.
        topicSubscriptionMap.get(topic.getTopicName()).add(mqttClientHolder.getMqttClient(clientIndex));
    }

    /** List of topics to unsub to with the corresponding clientIndex that what to unsub. Mutiple topics to unsub to. */
    public void removeTopic(ArrayList<String> topicSubscriptionMap, Integer clientIndex) {
        topicSubscriptionMap.forEach(topic -> { removeTopic(topic, clientIndex); });
    }

    /** One topic to unsub to */
    public void removeTopic(String topic, Integer clientIndex) {
        /** if Map dosent contain cetrain topic, return. */
        if(!topicSubscriptionMap.containsKey(topic)) {
            return;
        }

        ArrayList<MqttClient> subscribedClientList = getSubscribedClients(topic);
        for(var i = 0; i < subscribedClientList.size(); i++) {
            /** Iterate over all items in list of clients subscribed to a certain topic. 
             * Check ClientId if it match with the ClientId in mqttClientIdMap. 
             * Then remove that client from subscribedClientList. */
            if(subscribedClientList.get(i).getClientId().equals(mqttClientHolder.getClientId(clientIndex))) {
                subscribedClientList.remove(i);
                return;
            }
        }
    }

    /** Return List of Clients subscribed to a certain topic, if empty return new empty arraylist */
    public ArrayList<MqttClient> getSubscribedClients(String topic) {
        if(topicSubscriptionMap.containsKey(topic)) {
            return topicSubscriptionMap.get(topic);
        }
        return new ArrayList<>();
    }

    public void removeClient(Integer clientIndex) {
        /** Remove a certain client from topicSubscriptionMap 
         * Remove only if ClientId match with the ClientId in mqttClientIdMap. */
        topicSubscriptionMap.forEach((topic, clientsList) -> {
            for(var i = 0; i < clientsList.size(); i++) {
                if(clientsList.get(i).getClientId().equals(mqttClientHolder.getClientId(clientIndex))) {
                    clientsList.remove(i);
                    return;
                }
            }
        });
    }
}
