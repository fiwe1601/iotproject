package com.project.iotproject.MQTTServer.common.controlpacket;

import com.project.iotproject.MQTTServer.common.MqttControlPacketType;

public class MqttPubRec extends MqttAck {

    /** Fixed Header */
    private MqttControlPacketType _MQTTControlPacketType = MqttControlPacketType.PUBACK;

    /** Varibale Header */
    private int _PacketIdentifier;

    public MqttPubRec(){ }
    
    public MqttPubRec setPacketIdentifier(int _PacketIdentifier){
        this._PacketIdentifier = _PacketIdentifier;
        return this;
    }

    public int getPacketIdentifier(){
        return _PacketIdentifier;
    }

    @Override
    public MqttControlPacketType getType() {
        return _MQTTControlPacketType;
    }

    @Override
    public int getFixedHeader() {
        return ((getType().getKey() << 4) | 0);
    }

    @Override
    public int getVariableHeader() {
        return 0;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n---MQTT PubRec Control Packet---" + "\n");
        stringBuilder.append("Packet Identifier: " + getPacketIdentifier());
        return stringBuilder.toString();
    } 
    
}
