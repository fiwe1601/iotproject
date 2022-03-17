package com.project.iotproject.CoAPClient.common;

import java.util.HashMap;
import java.util.Map;

public enum CoAPProgramOptions {
    VERSION(0),
    TYPE(1),
    CODE(2),
    MESSAGEID(3),
    TOKEN(4),
    OPTIONS(5),
    PAYLOAD(6),
    RETURN(7);

    private Integer coAPProgramOptions;
    private static final Map<Integer, CoAPProgramOptions> hashMap = new HashMap<>();

    private CoAPProgramOptions(Integer coAPProgramOptions) {
        this.coAPProgramOptions = coAPProgramOptions;
    }
    
    public Integer getKey(){
        return this.coAPProgramOptions;
    }

    //return a value String based key
    public static CoAPProgramOptions getValue(Integer key) {
        return hashMap.get(key);
    }

    /*static initializer
    It gets run once this class has been loaded into the JVM, 
    whether or not an instance is being created. However, 
    since we're dealing with an enum, all its instances have to be created as part of loading the class. 
    */
    static {
        for (CoAPProgramOptions e : CoAPProgramOptions.values()) {
            hashMap.put(e.getKey(), e);
        }
    }
}
