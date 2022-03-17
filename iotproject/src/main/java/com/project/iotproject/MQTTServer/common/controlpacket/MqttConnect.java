package com.project.iotproject.MQTTServer.common.controlpacket;

import com.project.iotproject.MQTTServer.common.MqttConnectFlags;
import com.project.iotproject.MQTTServer.common.MqttPersistableControlPacket;
import com.project.iotproject.MQTTServer.common.MqttControlPacketType;
import com.project.iotproject.MQTTServer.common.MqttQoS;
import com.project.iotproject.Util.Util;

/**
 * After a Network Connection is established by a Client to a Server, the first Packet sent from the Client to
 * the Server MUST be a CONNECT Packet. A Client can only send the CONNECT Packet once over a Network Connection. 
 * The Server MUST process a second CONNECT Packet sent from a Client as a protocol violation and disconnect the Client. 
 * 
 * The payload contains one or more encoded fields. They specify a unique Client id for the Client, 
 * a Will topic, Will Message, User Name and Password. All but the Client id are optional and 
 * presence is determined based on flags in the variable header.
 * 
 * Remaining Length is the length of the variable header (10 bytes) plus the length of the Payload. 
 * 
 * The variable header for the CONNECT Packet consists of four fields in the following order: Protocol Name, Protocol Level, Connect Flags, and Keep Alive.
 * 
 * The Protocol Name is a UTF-8 encoded string that represents the protocol name “MQTT”, capitalized as shown. 
 * The string, its offset and length will not be changed by future versions of the MQTT specification.
 * 
 * If the protocol name is incorrect the Server MAY disconnect the Client, or it MAY continue processing the
 * CONNECT packet in accordance with some other specification. In the latter case, the Server MUST NOT
 * continue to process the CONNECT packet in line with this specification [
 * 
 * The 8 bit unsigned value that represents the revision level of the protocol used by the Client. The value of
 * the Protocol Level field for the version 3.1.1 of the protocol is 4 (0x04). The Server MUST respond to the
 * CONNECT Packet with a CONNACK return code 0x01 (unacceptable protocol level) and then disconnect 
 * the Client if the Protocol Level is not supported by the Server
 * 
 * The Connect Flags byte contains a number of parameters specifying the behavior of the MQTT connection. 
 * It also indicates the presence or absence of fields in the payload.
 * 
 */

public class MqttConnect extends MqttPersistableControlPacket {

    /** Fixed Header */
    private MqttControlPacketType _MQTTControlPacketType = MqttControlPacketType.CONNECT; //1

    /** Varibale Header */
    private String _ProtocolName;
    private Integer _ProtocolLevel;
    private MqttConnectFlags _ConnectFlag;   //KEY
    private Integer _KeepAlive;
    
    /** Payload */
    private String _ClientId;
    private String _WillTopic;
    private String _WillMessage; //private MqttMessage _WillMessage;
    private String _UserName;
    private String _Password;

    public MqttConnect(){
        _ProtocolName = DEFAULT_PROTOCOL_NAME;
        _ProtocolLevel = DEFAULT_PROTOCOL_VERSION;   //Protocol Level field for the version 3.1.1 of the protocol is 4 (0x04).
        _ConnectFlag = new MqttConnectFlags();
        _KeepAlive = 60;    //in sec
    }

    @Override
    public MqttControlPacketType getType() {
        return _MQTTControlPacketType;
    }

    @Override
    public int getVariableHeader() {
        return 0;
    }

    @Override
    public int getFixedHeader() {
        return ((getType().getKey() << 4) | 0); //GET = 1. 0001
    }


    public MqttConnect setProtocolName(String _ProtocolName) throws Exception {
        if(_ProtocolName.length() < 0x0 || _ProtocolName.length() > 0x6) {
            throw new Exception("Protocol Name can only be between 0-6 bytes");
        }
        this._ProtocolName = _ProtocolName;
        return this;
    }
    public String getProtocolName(){
        return _ProtocolName;
    }

    public MqttConnect setProtocolLevel(Integer _ProtocolLevel) throws Exception {
        if(_ProtocolLevel < 0x0 || _ProtocolLevel > 0xff) {
            throw new Exception("Keep Alive Value can only be between 0 and 1 byte");
        }
        this._ProtocolLevel = _ProtocolLevel;
        return this;
    }
    public Integer getProtocolLevel(){
        return _ProtocolLevel;
    }

    public MqttConnect setConnectFlag(MqttConnectFlags _ConnectFlag){
        this._ConnectFlag = _ConnectFlag;
        return this;
    }
    public MqttConnectFlags getConnectFlag() {
        if (this._ConnectFlag == null) {
            System.out.println("No connectflag found!");
            this._ConnectFlag = new MqttConnectFlags()
                .setUserNameFlag(true).setPasswordFlag(true)
                .setWillRetain(false).setWillQoS(MqttQoS.ATLEASTONCE)
                .setWillFlag(true).setCleanSession(true).setReserved(false);
        }
        return this._ConnectFlag;
    }

    //max time in sec 65535.
    public MqttConnect setKeepAlive(Integer _KeepAlive) throws Exception {
        if(_KeepAlive < 0x0 || _KeepAlive > 0xffff) {
            throw new Exception("Keep Alive Value can only be between 0-2 bytes");
        }
        this._KeepAlive = _KeepAlive;
        return this;
    }
    public Integer getKeepAlive(){
        return _KeepAlive;
    }

    public MqttConnect setClientId(String _ClientId) throws Exception {
        if(Util.containsIllegalCharacter(_ClientId)){
            throw new Exception("Illegal Characters in ClientID");
        }
        else if(_ClientId.length() == 0){
            System.out.println("ClientID Length is 0!");
            _ConnectFlag.setCleanSessionTrue();
            this._ClientId = _ClientId;
            return this;
        }
        //else if(_ClientId.length() == 0 && _ConnectFlag.isCleanSession() == false){
            //_MQTTControlPacketType = MqttControlPacketType.CONNACK;
            //MqttConnAck.setMqttReturnCode(MqttConnAck.MqttConnAckReturnCode.IDENTIFIER_REJECTED);
        //}

        else{
            this._ClientId = _ClientId;
            return this;
        }
    }
    public String getClientId(){
        return _ClientId;
    }

    public MqttConnect setWillTopic(String _WillTopic) {
        this._WillTopic = _WillTopic;
        return this;
    }
    public String getWillTopic(){
        return _WillTopic;
    }

    public MqttConnect setWillMessage(String _WillMessage) {     //void, MqttMessage
        this._WillMessage = _WillMessage;
        return this;
    }
    public String getWillMessage(){     //type = MqttMessage
        return _WillMessage;
    }

    public MqttConnect setUserName(String _UserName) {   //void. Byte[]
        this._UserName = _UserName;
        return this;
    }
    public String getUserName(){
        return _UserName;
    }

    public MqttConnect setPassword(String _Password) {   //type = void. byte[]
        this._Password = _Password;
        return this;
    }
    public String getPassword(){    //type = byte[]
        return _Password;
    }

    public boolean isMessageIdRequired() {
		return false;
	}
    
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n---Mqtt Connect Control Packet---\n");
        stringBuilder.append("Protocol Name Length: " + getProtocolName().length() + "\n");
        stringBuilder.append("Protocol Name: " + getProtocolName() + "\n");
        stringBuilder.append("Protocol Level: " + getProtocolLevel() + "\n");
        stringBuilder.append("Keep Alive: " + getKeepAlive() + "\n");
        stringBuilder.append("Client ID: " + getClientId() + "\n");
        stringBuilder.append("Will Topic: " + getWillTopic() + "\n");
        stringBuilder.append("Will Message: " + getWillMessage() + "\n");
        stringBuilder.append("Username: " + getUserName() + "\n");
        stringBuilder.append("Password: " + getPassword() + "\n");
        stringBuilder.append(getConnectFlag());
        return stringBuilder.toString();
    }

	/*
	public void setWillDestination(String willDestination) {
		this.willDestination = willDestination;
	}

	public byte getInfo() {
		return info;
	}

	public String getWillDestination() {
		return willDestination;
	}
    */

}
