package com.project.iotproject.MQTTServer.common;

public class MqttMessage {

	private int remainingLength;
    private boolean mutable = true;
	private byte[] payload;
	private int qos = 1;
	private boolean retained = false;
	private boolean dup = false;
	private int messageId;

	private MqttPersistableControlPacket mqttPacket;

	public MqttMessage(){}
	
	public MqttMessage setRemainingLength(Integer remainingLength) throws Exception{
		if(remainingLength < 0x0 || remainingLength > 0xffff) {
            throw new Exception("RemainingLength can only be between 1 and 4 bytes");
        }
		this.remainingLength = remainingLength;
        return this;
	}

	public int getRemainingLength() {
		return remainingLength;
	}

	public MqttMessage setMqttPacket(MqttPersistableControlPacket mqttPacket){
		this.mqttPacket = mqttPacket;
		return this;
	}

	public MqttPersistableControlPacket getMqttPacket(){
		return mqttPacket;
	}

    public MqttControlPacketType getMqttControlPacketType() {
        return getMqttPacket() == null ? null : getMqttPacket().getType();
    }


	public static void validateQoS(int qos) {
		if ((qos < 0) || (qos > 2)) {
			throw new IllegalArgumentException();
		}
    }

	public MqttMessage(byte[] payload) {
		setPayload(payload);
	}

	public MqttMessage(byte[] payload, int qos, boolean retained) {
		setPayload(payload);
		setQos(qos);
		setRetained(retained);
	}

	public byte[] getPayload() {
		return payload;
	}

	public void clearPayload() {
		checkMutable();
		this.payload = new byte[] {};
	}

	public void setPayload(byte[] payload) {
		checkMutable();
		if (payload == null) {
			throw new NullPointerException();
		}
		this.payload = payload;
	}

	public boolean isRetained() {
		return retained;
	}

	public void setRetained(boolean retained) {
		checkMutable();
		this.retained = retained;
	}

	public int getQos() {
		return qos;
	}

	public void setQos(int qos) {
		checkMutable();
		validateQoS(qos);
		this.qos = qos;
	}

	public void setMutable(boolean mutable) {
		this.mutable = mutable;
	}

	protected void checkMutable() throws IllegalStateException {
		if (!mutable) {
			throw new IllegalStateException();
		}
	}

	public void setDuplicate(boolean dup) {
		this.dup = dup;
	}

	public boolean isDuplicate() {
		return this.dup;
	}

	public void setId(int messageId) {
		this.messageId = messageId;
	}

	public int getId() {
		return this.messageId;
	}

	
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        //stringBuilder.append("\n---MqttMessage---" + "\n");
        stringBuilder.append("ControlPacket Type: " + getMqttControlPacketType() + "\n");
        stringBuilder.append("Remaining Length: " + getRemainingLength() + "\n");
        if(getMqttPacket() != null) {
            stringBuilder.append(getMqttPacket() + "\n");
        }
		stringBuilder.append("\n");
        return stringBuilder.toString();
    }
    
}
