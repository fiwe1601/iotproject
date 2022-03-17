package com.project.iotproject.MQTTServer.common.controlpacket;

import com.project.iotproject.MQTTServer.common.MqttPersistableControlPacket;
import com.project.iotproject.MQTTServer.common.MqttControlPacketType;
import com.project.iotproject.MQTTServer.common.MqttQoS;

public class MqttPublish extends MqttPersistableControlPacket {

    /** Fixed Header */
    private MqttControlPacketType _MQTTControlPacketType = MqttControlPacketType.PUBLISH;
    private int _FixedHeader = 0;

    /** Varibale Header */
    private String _TopicName;
    private int _PacketIdentifier;
    private String _Payload;

    //0011 0000
    public MqttPublish(){
        setFixedHeader((getType().getKey() << 4) | 0);
        setDUPFlag(false);
        setQoSLevel(MqttQoS.ATMOSTONCE);
        setRetain(false);
        
        //setTopicName("");
        //setPacketIdentifier(0);
        //setPayload("");
    }

    @Override
    public MqttControlPacketType getType() {
        return _MQTTControlPacketType;
    }

    public MqttPublish setFixedHeader(int _FixedHeader){
        this._FixedHeader = _FixedHeader;
        return this;
    }

    @Override
    public int getFixedHeader() {
        return _FixedHeader;
    }

    @Override
    public int getVariableHeader() {
        return 0;
    }

    public MqttPublish setDUPFlag(boolean _DUPFlag){
        this._FixedHeader = ((_DUPFlag ? 0x8 : 0x0) | this._FixedHeader);    //true = 00001000 | XXXXXXXX
        return this;
    }
    public boolean getDUPFlag(){
        return ((((this._FixedHeader & 0x8) >> 3) == 1) ? true : false);
    }


    public MqttPublish setQoSLevel(MqttQoS _MQTTQoS){
        this._FixedHeader = ((_MQTTQoS.getKey() << 1) | this._FixedHeader);       //(00 or 01 or 10) << 1 => XX0 
        return this;
    }
    public MqttQoS getQoSLevel(){
        return MqttQoS.getValue((this._FixedHeader & 0x06) >> 1);
    }


    public MqttPublish setRetain(boolean _Retain){
        this._FixedHeader = ((_Retain ? 1 : 0) | this._FixedHeader);          // (xxxx xxx1 or xxxx xxx0) | xxxx xxxx
        return this;
    }
    public boolean getRetain(){
        return (((this._FixedHeader & 0x1) == 1) ? true : false);
    }


    public MqttPublish setTopicName(String _TopicName){
        this._TopicName = _TopicName;
        return this;
    }
    public String getTopicName(){
        return _TopicName;
    }


    public MqttPublish setPacketIdentifier(int _PacketIdentifier){
        this._PacketIdentifier = _PacketIdentifier;
        return this;
    }
    public int getPacketIdentifier(){
        return _PacketIdentifier;
    }


    public MqttPublish setPayload(String _Payload){
        this._Payload = _Payload;
        return this;
    }
    public String getPayload(){
        return _Payload;
    }
    
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("\n---Mqtt Publish Control Packet---" + "\n");
        builder.append("DUPFlag: " + getDUPFlag() + "\n");
        builder.append("Retain: " + getRetain() + "\n");
        builder.append("QoS Level: " + getQoSLevel() + "\n");
        builder.append("Packet Identifier: " + getPacketIdentifier() + "\n" );
        builder.append("Payload: " + getPayload() + "\n" );
        builder.append("Topic Name: " + getTopicName());
        return builder.toString();
    }
}
