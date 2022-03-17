package com.project.iotproject.MQTTServer;

public class MQTTServerProgram extends Thread{
    
    MqttServer<?> _MQTTServer;

    public static void main(String [] args){
        new MQTTServerProgram();
    }

    public MQTTServerProgram(){
        _MQTTServer = new MqttServer<>();
    }
}
