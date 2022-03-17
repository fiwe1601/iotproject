package com.project.iotproject.MQTTServer.common.controlpacket;

import com.project.iotproject.MQTTServer.common.MqttPersistableControlPacket;
import com.project.iotproject.MQTTServer.common.MqttControlPacketType;

public class MqttDisconnect extends MqttPersistableControlPacket {
    /**
     * The DISCONNECT Packet is the final Control Packet sent from the Client to the Server. 
     * It indicates that the Client is disconnecting cleanly.
     * 
     * The Server MUST validate that reserved bits are set to zero and disconnect the Client if they are not zero.
     * 
     * The DISCONNECT Packet has no variable header.
     * The DISCONNECT Packet has no payload.
     * 
     * Response:
     * After sending a DISCONNECT Packet the Client:
     *      MUST close the Network Connection.
     *      MUST NOT send any more Control Packets on that Network Connection.
     * On receipt of DISCONNECT the Server:
     *      MUST discard any Will Message associated with the current connection without publishing it.
     *      SHOULD close the Network Connection if the Client has not already done so.
     * 
     */
    
    private MqttControlPacketType _MQTTControlPacketType = MqttControlPacketType.DISCONNECT;
    
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
