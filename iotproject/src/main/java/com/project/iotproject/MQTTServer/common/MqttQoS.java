package com.project.iotproject.MQTTServer.common;

import java.util.HashMap;
import java.util.Map;

public enum MqttQoS {

    ATMOSTONCE(0),  //00
    ATLEASTONCE(1), //01
    EXACTLYONCE(2), //10
    
    FALIURE(80);

    private Integer _MQTTQoS;
    private static final Map<Integer, MqttQoS> hashMap = new HashMap<>();//<Key, Value>, <1, ATLEASTONCE>

    private MqttQoS(Integer _MQTTQoS) {      
        this._MQTTQoS = _MQTTQoS;  
    }  
    
    //Returns Key
    public Integer getKey(){
        return this._MQTTQoS;
    }
    
    //Return Value
    public static MqttQoS getValue(Integer _MQTTQoS) {
        return hashMap.get(_MQTTQoS);
    }

    /*  
        static initializer
        It gets run once this class has been loaded into the JVM, 
        whether or not an instance is being created. However, 
        since we're dealing with an enum, all its instances have to be created as part of loading the class. 
    */
    static{ for(MqttQoS e : MqttQoS.values()) { hashMap.put(e.getKey(), e); }
    }
}
