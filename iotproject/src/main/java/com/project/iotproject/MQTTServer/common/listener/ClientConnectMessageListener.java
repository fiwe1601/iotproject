package com.project.iotproject.MQTTServer.common.listener;

public interface ClientConnectMessageListener<Message> {
    public void onClientMessageConnect(ClientConnectionListener<Message> connection);    
}
