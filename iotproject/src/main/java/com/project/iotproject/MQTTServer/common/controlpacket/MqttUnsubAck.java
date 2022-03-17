package com.project.iotproject.MQTTServer.common.controlpacket;

import com.project.iotproject.MQTTServer.common.MqttControlPacketType;

public class MqttUnsubAck extends MqttAck {
    /**
     * The UNSUBACK Packet is sent by the Server to the Client to confirm receipt of an UNSUBSCRIBE Packet.
     * 
     * Remaining Length field
     *      This is the length of the variable header. For the UNSUBACK Packet this has the value 2.
     * 
     * The variable header contains the Packet Identifier of the UNSUBSCRIBE Packet that is being acknowledged.
     * The UNSUBACK Packet has no payload.
     */

    /** Fixed Header */
    private MqttControlPacketType _MQTTControlPacketType = MqttControlPacketType.UNSUBACK;

    /** Variable Header */
    private int _PacketIdentifier;

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

    public MqttUnsubAck setPacketIdentifier(int _PacketIdentifier){
        this._PacketIdentifier = _PacketIdentifier;
        return this;
    }

    public int getPacketIdentifier(){
        return _PacketIdentifier;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n---MQTT UnSubAck Control Packet---" + "\n");
        stringBuilder.append("Packet Identifier: " + getPacketIdentifier());
        return stringBuilder.toString();
    }
}
