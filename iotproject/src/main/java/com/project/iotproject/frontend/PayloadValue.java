package com.project.iotproject.frontend;

import org.eclipse.paho.client.mqttv3.MqttMessage;

public class PayloadValue {
    String time;
    String topic;
    MqttMessage mqttMessage;
    public PayloadValue(String time, String topic, MqttMessage mqttMessage) {
        this.time = time;
        this.topic = topic;
        this.mqttMessage = mqttMessage;
    }
    String getTime(){
        return this.time;
    }
    String getTopic(){
        return this.topic;
    }
    MqttMessage getMqttMessage(){
        return this.mqttMessage;
    } 
}
