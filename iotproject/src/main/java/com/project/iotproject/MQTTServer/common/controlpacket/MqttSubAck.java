package com.project.iotproject.MQTTServer.common.controlpacket;

import com.project.iotproject.MQTTServer.common.MqttQoS;

import java.util.ArrayList;

import com.project.iotproject.MQTTServer.common.MqttControlPacketType;
import com.project.iotproject.MQTTServer.common.MqttPacketId;

public class MqttSubAck extends MqttAck implements MqttPacketId {

    /**
     * A SUBACK Packet is sent by the Server to the Client to confirm receipt and processing of a SUBSCRIBE Packet.
     * A SUBACK Packet contains a list of return codes, that specify the maximum QoS level that was granted
     * in each Subscription that was requested by the SUBSCRIBE.
     * 
     * This is the length of variable header (2 bytes) plus the length of the payload.
     * 
     * The variable header contains the Packet Identifier from the SUBSCRIBE Packet that is being acknowledged.
     * 
     * The payload contains a list of return codes. Each return code corresponds to a Topic Filter in the
     * SUBSCRIBE Packet being acknowledged. The order of return codes in the SUBACK Packet MUST
     * match the order of Topic Filters in the SUBSCRIBE Packet.
     * 
     * SUBACK return codes other than 0x00, 0x01, 0x02 and 0x80 are reserved and MUST NOT be used.
     */

     /** Fixed Header */
    private MqttControlPacketType _MQTTControlPacketType = MqttControlPacketType.SUBACK;

    /** Variable Header */
    private Integer _PacketIdentifier;
    private ArrayList<MqttQoS> _QoS;
    private String _Payload;
    private MqttQoS _PayloadQoS;

    public MqttSubAck(){
        this._QoS = new ArrayList<>();
    }

    //Mqtt Control Packet type
    @Override
    public MqttControlPacketType getType() {
        return _MQTTControlPacketType;
    }

    //Flags specific to each MQTT Control Packet type
    @Override
    public int getFixedHeader() {
        return ((getType().getKey() << 4) | 0);
    }
    
    @Override
    public int getVariableHeader() {
        return 0;
    }

    public MqttSubAck setPacketIdentifier(int _PacketIdentifier) {
        this._PacketIdentifier = _PacketIdentifier;
        return this;
    }

    public int getPacketIdentifier() {
        return _PacketIdentifier;
    }

    public MqttSubAck setQoSList(ArrayList<MqttQoS> _QoS) {
        this._QoS = _QoS;
        return this;
    }

    public MqttSubAck addQoS(MqttQoS _MqttQoS) {
        if(this._QoS == null) { this._QoS = new ArrayList<>(); }
        this._QoS.add(_MqttQoS);
        return this;
    }

    public ArrayList<MqttQoS> getQoSList() {
        return _QoS;
    }

    public MqttSubAck setPayload(String _Payload){
        this._Payload = _Payload;
        return this;
    }
    public String getPayload(){
        return _Payload;
    }

    public MqttSubAck setPayloadQoSReturnCode(MqttQoS _MqttQoS) throws Exception{
        if(_MqttQoS.getKey() == 0x80){
            throw new Exception("Faliure");
        }

        this._PayloadQoS = _MqttQoS;
        return this;
    }
    public MqttQoS getPayloadQoSReturnCode(){
        return _PayloadQoS;
    }
    
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n---Mqtt SubAck Packet---" + "\n");
        stringBuilder.append("Packet Identifier: " + getPacketIdentifier() + "\n");
        stringBuilder.append("Packet QoS: " + getQoSList() + "\n\n");
        return stringBuilder.toString();
    }
}
