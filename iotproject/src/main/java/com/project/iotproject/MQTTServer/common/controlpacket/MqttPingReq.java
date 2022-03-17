package com.project.iotproject.MQTTServer.common.controlpacket;

import com.project.iotproject.MQTTServer.common.MqttPersistableControlPacket;
import com.project.iotproject.MQTTServer.common.MqttControlPacketType;

public class MqttPingReq extends MqttPersistableControlPacket {
    /**
     * The PINGREQ Packet is sent from a Client to the Server. It can be used to:
     * 1. Indicate to the Server that the Client is alive in the absence of any other Control Packets being sent from the Client to the Server.
     * 2. Request that the Server responds to confirm that it is alive.
     * 3. Exercise the network to indicate that the Network Connection is active.
     * 
     * The PINGREQ Packet has no variable header.
     * The PINGREQ Packet has no payload.
     * The Server MUST send a PINGRESP Packet in response to a PINGREQ Packet.
     */

    private MqttControlPacketType _MQTTControlPacketType = MqttControlPacketType.PINGREQ;
    
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
