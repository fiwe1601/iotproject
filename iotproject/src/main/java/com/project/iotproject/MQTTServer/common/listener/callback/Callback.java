package com.project.iotproject.MQTTServer.common.listener.callback;

public interface Callback<T> {
    void callback(T t);
}
