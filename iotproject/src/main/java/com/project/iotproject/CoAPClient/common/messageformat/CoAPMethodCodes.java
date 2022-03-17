package com.project.iotproject.CoAPClient.common.messageformat;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public enum CoAPMethodCodes implements CoAPCode{
    EMPTY(0),
    GET(1),
    POST(2),
    PUT(3),
    DELETE(4);

    private Integer _CoAPCode;
    private static final Map<Integer, CoAPMethodCodes> hashMap = new HashMap<>(); //<K,V>, e.g. <1, GET>

    private CoAPMethodCodes(Integer _CoAPCode) {      
        this._CoAPCode = _CoAPCode;  
    }  
    
    static{
        for(CoAPMethodCodes e : CoAPMethodCodes.values()){
            hashMap.put(e.getKey(), e);
            //hashMap.put(e._CoAPCode, e);
        }
    }

    //Returns Key, Integer
    public Integer getKey(){
        return this._CoAPCode;
    }

    //Returns Value, CoAPMethodCodes
    public static CoAPMethodCodes getValue(Integer _CoAPCode){
        //return (CoAPMethodCodes)hashMap.get(_CoAPCode);
        return hashMap.get(_CoAPCode);
    }

    public static Collection<CoAPMethodCodes> getValues() {
        return hashMap.values();
    }
}
