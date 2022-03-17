package com.project.iotproject.MQTTServer.common;

import java.util.HashMap;
import java.util.Map;

public enum MqttControlPacketType {
    CONNECT(1),         //Client to Server. Client request to connect to Server.
    CONNACK(2),         //Server to Client. Connect acknowledgment.
    PUBLISH(3),         //Client to Server or Server to Client. Publish message.
    PUBACK(4),          //Client to Server or Server to Client. Publish acknowledgment.
    PUBREC(5),          //Client to Server or Server to Client. Publish received (assured delivery part 1).
    PUBREL(6),          //Client to Server or Server to Client. Publish release (assured delivery part 2).
    PUBCOMP(7),         //Client to Server or Server to Client. Publish complete (assured delivery part 3).
    SUBCRIBE(8),        //Client subscribe request.
    SUBACK(9),          //Server to Client. Subscribe acknowledgment.
    UNSUBSCRIBE(10),    //Client to Server. Unsubscribe request.
    UNSUBACK(11),       //Server to Client. Unsubscribe acknowledgment.
    PINGREQ(12),        //Client to Server. PING request.
    PINGRESP(13),       //Server to Client. PING response.
    DISCONNECT(14);     //Client to Server. Client is disconnecting.

    private Integer _MQTTType;
    private static final Map<Integer, MqttControlPacketType> hashMap = new HashMap<>();//<Key, Value>, <1, NON>

    private MqttControlPacketType(Integer _MQTTType) {      
        this._MQTTType = _MQTTType;  
    }  
    
    //Returns Key
    public Integer getKey(){
        return this._MQTTType;
    }
    
    //Return Value
    public static MqttControlPacketType getValue(Integer _MQTTType) {
        return hashMap.get(_MQTTType);
    }

    /*static initializer
    It gets run once this class has been loaded into the JVM, 
    whether or not an instance is being created. However, 
    since we're dealing with an enum, all its instances have to be created as part of loading the class. 
    */
    static{
        for(MqttControlPacketType e : MqttControlPacketType.values()) { hashMap.put(e.getKey(), e); }
    }
}
