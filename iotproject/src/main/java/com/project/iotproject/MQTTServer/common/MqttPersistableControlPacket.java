package com.project.iotproject.MQTTServer.common;

public abstract class MqttPersistableControlPacket implements MqttPersistable {

	protected static final String STRING_ENCODING = "UTF-8";
	protected static final String DEFAULT_PROTOCOL_NAME = "MQTT";
	protected static final int DEFAULT_PROTOCOL_VERSION = 0x04;

	protected int msgId; // The MQTT Message ID
	protected int[] returnCodes = null; // Multiple Reason Codes (SUBACK, UNSUBACK)
	protected int returnCode = -1; // Single Reason Code, init with -1 as that's an invalid RC
	protected boolean duplicate = false;

    MqttControlPacketType mqttControlPacketType;
   
    public abstract MqttControlPacketType getType();
    public abstract int getVariableHeader();
	public abstract int getFixedHeader();

    @Override
    public String toString() { return ""; }

	
	/*
	public int getHeaderLength() {
		return getHeaderBytes().length;
	}

	public int getHeaderOffset() {
		return 0;
	}

	public byte[] getPayloadBytes() {
		return getPayload();
	}
	
	public int getPayloadLength() {
		return 0;
	}
	*/
}
