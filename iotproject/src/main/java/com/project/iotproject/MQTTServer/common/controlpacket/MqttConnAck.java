package com.project.iotproject.MQTTServer.common.controlpacket;

import java.util.HashMap;
import java.util.Map;

import com.project.iotproject.MQTTServer.common.MqttControlPacketType;

public class MqttConnAck extends MqttAck {

    /** Fixed Header */
    private MqttControlPacketType _MQTTControlPacketType = MqttControlPacketType.CONNACK; //2

    /** Varibale Header */
    private Boolean _MQTTSessionPresent;   //Connect Acknowledge Flag. Bit 7-1 must be set to 0, bit 0 is the SP. Byte 1.
    private MqttConnAckReturnCode _MQTTConnAckReturnCode; //Connect Return code. Byte 2.
    
    @Override
    public MqttControlPacketType getType() {
        return _MQTTControlPacketType;
    }

    @Override
    public int getFixedHeader() {
        return ((getType().getKey() << 4) | 0); //0010 0000 | 0000 0000 => 0010 0000 Byte 1
    }

    @Override
    public int getVariableHeader() {
        return 0;
    }

	public boolean isMessageIdRequired() {
		return false;
	}

    public MqttConnAck(){
        _MQTTSessionPresent = false; //0
        _MQTTConnAckReturnCode = MqttConnAckReturnCode.CONNECTION_ACCEPTED;  //0
    }

    public MqttConnAck setMqttReturnCode(MqttConnAckReturnCode _MQTTConnAckReturnCode){
        this._MQTTConnAckReturnCode = _MQTTConnAckReturnCode;
        return this;
    }
    public MqttConnAckReturnCode getMqttConnAckReturnCode(){
        return _MQTTConnAckReturnCode;
    }

    public MqttConnAck setMqttSessionPresent(Boolean _MQTTSessionPresent){
        this._MQTTSessionPresent = _MQTTSessionPresent;
        return this;
    }
    public Boolean getMqttSessionPresent(){
        return _MQTTSessionPresent;
    }

    public MqttConnAckReturnCode[] getAllMqttReturnCodes(){
        return MqttConnAckReturnCode.values();
    }
    
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n---MQTT ConnAck Control Packet---\n");
        stringBuilder.append("Connect Return Code: " + getMqttConnAckReturnCode() + "\n");
        stringBuilder.append("Session Present: " + getMqttSessionPresent());
        return stringBuilder.toString();
    }

    public enum MqttConnAckReturnCode {
        CONNECTION_ACCEPTED(0),             //Connection accepted.
        UNACCEPTABLE_PROTOCOL_VERSION(1),   //The Server does not support the level of the Mqtt protocol requested by the Client.
        IDENTIFIER_REJECTED(2),             //The Client identifier is correct UTF-8 but not allowed by the Server.
        SERVER_UNAVALIABLE(3),              //The Network Connection has been made but the Mqtt service is unavailable.
        BAD_USER_NAME_OR_PASSWORD(4),       //The data in the user name or password is malformed.
        NOT_AUTHORIZED(5);                  //The Client is not authorized to connect.
    
        private Integer returnCodeValue;
        private static final Map<Integer, MqttConnAckReturnCode> hashMap = new HashMap<>();//<Key, Value>, <1, NON>
    
        private MqttConnAckReturnCode(Integer returnCodeValue) {      
            this.returnCodeValue = returnCodeValue;  
        }  
        
        //Returns Key
        public Integer getKey(){
            return this.returnCodeValue;
        }
        
        //Return Value
        public static MqttConnAckReturnCode getValue(Integer returnCodeValue) {
            return hashMap.get(returnCodeValue);
        }
    
        /*static initializer
        It gets run once this class has been loaded into the JVM, 
        whether or not an instance is being created. However, 
        since we're dealing with an enum, all its instances have to be created as part of loading the class. 
        */
        static{ for(MqttConnAckReturnCode e : MqttConnAckReturnCode.values()){ hashMap.put(e.getKey(), e); } }
    }
    
}
