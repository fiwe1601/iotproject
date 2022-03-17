package com.project.iotproject.CoAPClient.common.messageformat;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public enum CoAPResponseCodes implements CoAPCode{
    SUCCESS(2),
    CLIENT_ERROR(4),
    SERVER_ERROR(5),

    //Success Response Code
    CREATED(65),
    DELETED(66),
    VALID(67),
    CHANGED(68),
    CONTENT(69),
    CONTINUE(95),
    
    //Client Error Response Code
    BAD_REQUEST(128),
    UNAUTHORIZED(129),
    BAD_OPTION(130),
    FORBIDDEN(131),
    NOT_FOUND(132),
    METHOD_NOT_ALLOWED(133),
    NOT_ACCEPTABLE(134),
    REQUEST_ENTITY_INCOMPLETE(136),
    PRECONDITION_FAILED(140),
    REQUEST_ENTITY_TOO_LARGE(141),
    UNSUPPORTED_CONTENT_FORMAT(143),

    //Server Error Response Code
    INTERNAL_SERVER_ERROR(160),
    NOT_IMPLEMENTED(161),
    BAD_GATEWAY(162),
    SERVICE_UNAVAILABLE(163),
    GATEWAY_TIMEOUT(164),
    PROXYING_NOT_SUPPORTED(165);

    private Integer _CoAPCodeClass;
    private Integer _CoAPCodeDetail;

    private Integer _CoAPCode;
    private static final Map<Integer, CoAPResponseCodes> hashMap = new HashMap<>(); //<K,V>, e.g. <1, GET>

    private CoAPResponseCodes(Integer _CoAPCode) {      
        this._CoAPCode = _CoAPCode;  
    }

    public CoAPResponseCodes setCodeClass(Integer _CoAPCodeClass){
        this._CoAPCodeClass = _CoAPCodeClass;
        return this;
    }

    public CoAPResponseCodes setCodeDetail(Integer _CoAPCodeDetail){
        this._CoAPCodeDetail = _CoAPCodeDetail;
        return this;
    }

    public Integer getCodeClass(){
        return this._CoAPCodeClass;
    }

    public Integer getCodeDetail(){
        return this._CoAPCodeDetail;
    }
    
    static{
        for(CoAPResponseCodes e : CoAPResponseCodes.values()){
            hashMap.put(e.getKey(), e);
            //hashMap.put(e._CoAPCode, e);
        }
    }

    //Returns Key, Integer
    public Integer getKey(){
        return this._CoAPCode;
    }

    //Returns Value, CoAPResponseCodes
    public static CoAPResponseCodes getValue(Integer _CoAPCode) {
        //return (CoAPResponseCodes)hashMap.get(_CoAPCode);
        return hashMap.get(_CoAPCode);
    }

    public static Collection<CoAPResponseCodes> getValues() {
        return hashMap.values();
    }
}
