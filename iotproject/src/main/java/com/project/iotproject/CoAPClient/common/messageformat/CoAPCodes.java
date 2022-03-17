package com.project.iotproject.CoAPClient.common.messageformat;

import java.util.HashMap;
import java.util.Map;

/**
 * 0 = Method (Request)
 * 2 = Success (Response)
 * 4 = Client Error (Response)
 * 5 = Server Error (Response)
 * 
 * Method codes:
 * +------+--------+
 * | Code | Name   |
 * +------+--------+
 * | 0.00 | EMPTY  |
 * +------+--------+
 * | 0.01 | GET    |
 * | 0.02 | POST   |
 * | 0.03 | PUT    |
 * | 0.04 | DELETE |
 * +------+--------+
 *
 *
 * Response codes:
 * 0
 * 0 1 2 3 4 5 6 7
 * +-+-+-+-+-+-+-+-+
 * |class| detail  |
 * +-+-+-+-+-+-+-+-+
 * 
 * +------------------+------------------------------+--------------+
 * | Code             | Description                  |              |
 * +------------------+------------------------------+--------------+
 * | 2.01 (65, 0x41)  | Created                      | Success      |
 * | 2.02 (66, 0x42)  | Deleted                      |              |
 * | 2.03 (67, 0x43)  | Valid                        |              |
 * | 2.04 (68, 0x44)  | Changed                      |              |
 * | 2.05 (69, 0x45)  | Content                      |              |
 * | 2.31 (95, 0x5F)  | Continue                     |              |
 * +------------------+------------------------------+--------------+
 * | 4.00 (128, 0x80) | Bad Request                  | Client Error |
 * | 4.01 (129, 0x81) | Unauthorized                 |              |
 * | 4.02 (130, 0x82) | Bad Option                   |              |
 * | 4.03 (131, 0x83) | Forbidden                    |              |
 * | 4.04 (132, 0x84) | Not Found                    |              |
 * | 4.05 (133, 0x85) | Method Not Allowed           |              |
 * | 4.06 (134, 0x86) | Not Acceptable               |              |
 * | 4.08 (136, 0x88) | Request Entity Incomplete    |              |
 * | 4.12 (140, 0x8C) | Precondition Failed          |              |
 * | 4.13 (141, 0x8D) | Request Entity Too Large     |              |
 * | 4.15 (143, 0x8F) | Unsupported Content-Format   |              |
 * +------------------+------------------------------+--------------+
 * | 5.00 (160, 0xA0) | Internal Server Error        | Server Error |
 * | 5.01 (161, 0xA1) | Not Implemented              |              |
 * | 5.02 (162, 0xA2) | Bad Gateway                  |              |
 * | 5.03 (163, 0xA3) | Service Unavailable          |              |
 * | 5.04 (164, 0xA4) | Gateway Timeout              |              |
 * | 5.05 (165, 0xA5) | Proxying Not Supported       |              |
 * +------------------+------------------------------+--------------+
 */


public enum CoAPCodes{
    EMPTY(0),
    GET(1),
    POST(2),
    PUT(3),
    DELETE(4),

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

    private Integer _CoAPCode;
    private static final Map<Integer, CoAPCodes> hashMap = new HashMap<>(); //<K,V>, e.g. <1, GET>

    private CoAPCodes(Integer _CoAPCode) {      
        this._CoAPCode = _CoAPCode;  
    }  
    
    //Returns Key, Integer
    public Integer getKey(){
        return this._CoAPCode;
    }

    //Returns Value, CoAPCodes
    public static CoAPCodes getValue(Integer _CoAPCode) {
        return hashMap.get(_CoAPCode);
    }

    /*static initializer
    It gets run once this class has been loaded into the JVM, whether or not an instance is being created. Hence populating the lookup table on loading time.
    However, since we're dealing with an enum, all its instances have to be created as part of loading the class. */
    static{
        for(CoAPCodes e : CoAPCodes.values())
            hashMap.put(e.getKey(), e);
    }
}

 


/*     
    //input string value get key
    public static Integer getKey(Map<Integer, CoAPCode> map, String value){
        Integer key = null;
        for (Map.Entry<Integer, CoAPCode> entry : hashMap.entrySet()) {
            if(entry.getValue().equals(value)) {
                key = entry.getKey();
                break;
            }
        }
        return key;
    }
*/

/*
public class Code {
    
    private Code() {}

    public final class Class{
        private Class() {}
        //X.00
        public static final int METHOD = 0;
        public static final int SUCCESS = 2;
        public static final int CLIENT_ERROR = 4;
        public static final int SERVER_ERROR = 5;
    }

    //Method Request/Codes, 0.XX
    public static final int EMPTY = 0;
    public static final int GET = 1;
    public static final int POST = 2;
    public static final int PUT = 3;
    public static final int DELETE = 4;

    //Success Response Code, 2.XX
    public static final int CREATED = (Class.SUCCESS << 5) + 1; //(65, 0x41)
    public static final int DELETED = (Class.SUCCESS << 5) + 2; //(66, 0x42)
    public static final int VALID = (Class.SUCCESS << 5) + 3; //(67, 0x43)
    public static final int CHANGED = (Class.SUCCESS << 5) + 4; //(68, 0x44)
    public static final int CONTENT = (Class.SUCCESS << 5) + 5; //(69, 0x45)
    public static final int CONTINUE = (Class.SUCCESS << 5) + 31; //(95, 0x5F)

    //Client Error Response Code, 4.XX
    public static final int BAD_REQUEST = (Class.CLIENT_ERROR << 5); //4.00 (128, 0x80) 
    public static final int UNAUTHORIZED = (Class.CLIENT_ERROR << 5) + 1; //4.01 (129, 0x81)
    public static final int BAD_OPTION = (Class.CLIENT_ERROR << 5) + 2; //4.02 (130, 0x82)
    public static final int FORBIDDEN = (Class.CLIENT_ERROR << 5) + 3; //4.03 (131, 0x83)
    public static final int NOT_FOUND = (Class.CLIENT_ERROR << 5) + 4; //4.04 (132, 0x84)
    public static final int METHOD_NOT_ALLOWED = (Class.CLIENT_ERROR << 5) + 5; //4.05 (133, 0x85) 
    public static final int NOT_ACCEPTABLE = (Class.CLIENT_ERROR << 5) + 6; //4.06 (134, 0x86)
    public static final int REQUEST_ENTITY_INCOMPLETE = (Class.CLIENT_ERROR << 5) + 8; //4.08 (136, 0x88)
    public static final int PRECONDITION_FAILED = (Class.CLIENT_ERROR << 5) + 12; // 4.12 (140, 0x8C)
    public static final int REQUEST_ENTITY_TOO_LARGE  = (Class.CLIENT_ERROR << 5) + 3; //4.13 (141, 0x8D) 
    public static final int UNSUPPORTED_CONTENT_FORMAT = (Class.CLIENT_ERROR << 5) + 15; //4.15 (143, 0x8F)

    //Server Error Response Code, 5.XX
    public static final int INTERNAL_SERVER_ERROR = (Class.SERVER_ERROR << 5);//5.00 (160, 0xA0)
    public static final int NOT_IMPLEMENTED = (Class.SERVER_ERROR << 5) + 1;//5.01 (161, 0xA1)
    public static final int BAD_GATEWAY = (Class.SERVER_ERROR << 5) + 2;//5.02 (162, 0xA2)
    public static final int SERVICE_UNAVAILABLE = (Class.SERVER_ERROR << 5) + 3;//5.03 (163, 0xA3)
    public static final int GATEWAY_TIMEOUT = (Class.SERVER_ERROR << 5) + 4;//5.04 (164, 0xA4)
    public static final int PROXYING_NOT_SUPPORTED = (Class.SERVER_ERROR << 5) + 5;//5.05 (165, 0xA5)

    public static String toString(int code){
        switch(code){
            case EMPTY:
                return "EMPTY";
            case GET:
                return "GET";
            case POST:
                return "POST";
            case PUT:
                return "PUT";
            case DELETE:
                return "DELETE";
            default:
                return getCompleteCodeValue(code);
        }
    }

    public static int getClassValue(int code) {
        if (code >= 0 && code < 5)
            return 0;
        if(code > 64 && code < 96)
            return 2;
        else if(code > 127 && code < 144)
            return 4;
        else if(code > 159 && code < 166)
            return 5;
        return 0;
        //return (code >> 5) & 0x7;    
    }

    public static int getDetailValue(int code) {
        return code & 0x1F;
    }

    public static boolean isRequest(int code) {
        return (code != 0) && (getClassValue(code) == Class.METHOD);
        //code =! 0 && 0/2/4/5 == 0
    }

    public static boolean isResponse(int code) {
        int classValue = getClassValue(code);
        return (classValue >= Class.SUCCESS);
        // 0/2/4/5 >= 2
    }

    public static String getCompleteCodeValue(int code) {
        return String.format("%d.%02d", getClassValue(code), getDetailValue(code));
        //02 = X.00 and replaced by Detail"
    }
    
}
*/