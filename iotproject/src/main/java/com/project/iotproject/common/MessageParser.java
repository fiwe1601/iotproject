package com.project.iotproject.common;

public interface MessageParser<Message> {
    byte[] encodeMessage(Message message);
    Message decodeMessage(byte[] responseBuffer);
}