package com.project.iotproject.MQTTServer.common.controlpacket;

import com.project.iotproject.MQTTServer.common.MqttControlPacketType;

public class MqttPubComp extends MqttAck {

    /** Fixed Header */
    private MqttControlPacketType mQTTControlPacketType = MqttControlPacketType.PUBACK;

    /** Varibale Header */
    private int _PacketIdentifier;

    public MqttPubComp(){ }
    
    public MqttPubComp setPacketIdentifier(int _PacketIdentifier){
        this._PacketIdentifier = _PacketIdentifier;
        return this;
    }

    public int getPacketIdentifier(){
        return _PacketIdentifier;
    }

    @Override
    public MqttControlPacketType getType() {
        return mQTTControlPacketType;
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
        stringBuilder.append("\n---MQTT PubComp Control Packet---" + "\n");
        stringBuilder.append("Packet Identifier: " + getPacketIdentifier());
        return stringBuilder.toString();
    } 
    
}
