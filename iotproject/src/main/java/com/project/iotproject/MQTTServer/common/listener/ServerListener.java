package com.project.iotproject.MQTTServer.common.listener;

public interface ServerListener <Message>{
    public void onMessageReceived(Message message);
    public void onMessageSent(Message message);
}
