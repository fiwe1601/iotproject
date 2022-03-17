package com.project.iotproject.CoAPClient.common.messageformat;

import java.util.HashMap;
import java.util.Map;
/**
 *  +----------+-----+-----+-----+-----+
 *  |          | CON | NON | ACK | RST |
 *  +----------+-----+-----+-----+-----+
 *  | Request  | X   | X   | -   | -   |
 *  | Response | X   | X   | X   | -   |
 *  | Empty    | *   | -   | X   | X   |
 *  +----------+-----+-----+-----+-----+
 *  Usage of Message Types
 */

public enum CoAPMessageTypes {
    CON(0), //CONfirmable (0)
    NON(1), //NON-confirmable (1)
    ACK(2), //ACKnowledgement (2)
    RST(3); //ReSeT (3)

    private Integer coAPType;
    private static final Map<Integer, CoAPMessageTypes> hashMap = new HashMap<>();//<Key, Value>, <1, NON>

    private CoAPMessageTypes(Integer coAPType) {      
        this.coAPType = coAPType;  
    }  
    
    //Returns Key, Integer(0,1,2,3)
    public Integer getKey(){
        return this.coAPType;
    }
    
    //Returns Value, CoAPMessageTypes(CON, NON, ACK, RST)
    public static CoAPMessageTypes getValue(Integer coAPType) {
        return hashMap.get(coAPType);
    }

    /*static initializer
    It gets run once this class has been loaded into the JVM, whether or not an instance is being created. Hence populating the hashMap on loading time.
    However, since we're dealing with an enum, all its instances have to be created as part of loading the class. */
    static{
        for(CoAPMessageTypes e : CoAPMessageTypes.values()){
            hashMap.put(e.getKey(), e); //0,1,2,3,  CON, NON...
        }
    }
}
