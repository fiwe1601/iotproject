package com.project.iotproject.MQTTServer.common.controlpacket;

import com.project.iotproject.MQTTServer.common.MqttControlPacketType;

public class MqttPingResp extends MqttAck {
    /**
     * A PINGRESP Packet is sent by the Server to the Client in response to a PINGREQ Packet. It indicates that the Server is alive.
     * 
     * The PINGRESP Packet has no variable header.
     * The PINGRESP Packet has no payload.
     */
    private MqttControlPacketType _MQTTControlPacketType = MqttControlPacketType.PINGRESP;
    
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
    
}
