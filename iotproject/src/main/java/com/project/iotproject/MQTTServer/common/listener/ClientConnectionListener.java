package com.project.iotproject.MQTTServer.common.listener;

import com.project.iotproject.MQTTServer.common.listener.callback.Callback;

public interface ClientConnectionListener<Message> {
//public interface ClientConnectionListener<Message> extends ClientActionListener<Message> {
    void receivePacket(Callback<Message> callback);
    void clientConnectionClose();
    boolean isConnected();
    void disconnect();
    void sendMessage(Message message);
}
