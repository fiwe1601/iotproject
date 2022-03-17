package com.project.iotproject.MQTTServer.common.listener;

public interface ClientActionListener<Message> {
    void sendMessage(Message message);
}
