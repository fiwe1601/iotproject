package com.project.iotproject.Util;

public class MqttRemainingLengthVariable {

    private int value;
    private int length;

    public MqttRemainingLengthVariable(int value) {
        this(value, -1);
    }

    public MqttRemainingLengthVariable(int value, int length) {
        this.value = value;
        this.length = length;
    }

    public MqttRemainingLengthVariable setLength(int length) {
        this.length = length;
        return this;
    }

    public MqttRemainingLengthVariable setValue(int value) {
        this.value = value;
        return this;
    }

    public int getLength() {
        return length;
    }

    public int getValue() {
        return value;
    }
}
