package com.project.iotproject.MQTTClient;

import java.util.HashMap;
import java.util.Map;

public enum ManageMessageEnum {
    Topic(0),
    Payload(1),
    QoS(2),
    BrokerURL(3),
    ClientID(4),
    KeepAlive(5),
    Retained(6),
    CleanSession(7),
    Return(8);

    private Integer manageMessageEnum;
    private static final Map<Integer, ManageMessageEnum> hashMap = new HashMap<>();

    private ManageMessageEnum(Integer manageMessageEnum) {
        this.manageMessageEnum = manageMessageEnum;
    }
    
    public Integer getKey(){
        return this.manageMessageEnum;
    }

    //return a value String based key
    public static ManageMessageEnum getValue(Integer key) {
        return hashMap.get(key);
    }

    /*static initializer
    It gets run once this class has been loaded into the JVM, 
    whether or not an instance is being created. However, 
    since we're dealing with an enum, all its instances have to be created as part of loading the class. 
    */
    static {
        for (ManageMessageEnum e : ManageMessageEnum.values()) {
            hashMap.put(e.getKey(), e);
        }
    }
}
