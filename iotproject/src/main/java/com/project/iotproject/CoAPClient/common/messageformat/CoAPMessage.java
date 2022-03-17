package com.project.iotproject.CoAPClient.common.messageformat;

import java.util.ArrayList;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.project.iotproject.CoAPClient.common.messageformat.option.CoAPOptions;
import com.project.iotproject.CoAPClient.common.messageformat.option.CoAPOptionsFormat;

public class CoAPMessage {
    private int _CoAPVersion;
    private CoAPMessageTypes _CoAPType;
    private CoAPCodes _CoAPCode;
    private int _CoAPMessageId;
    private String _CoAPToken;
    private SortedMap<CoAPOptionsFormat, ArrayList<CoAPOptions<?>>> options_Map_List;
    private String _CoAPPayload;

    private CoAPCode _CoAPCodeClass;
    private CoAPCode _CoAPCodeDetail;

    //Constructor, sets default parameters
    public CoAPMessage(){
        this._CoAPVersion = 0x0;
        this._CoAPType = CoAPMessageTypes.NON;
        //this._CoAPCode = CoAPCodes.GET;
        this._CoAPCode = CoAPCodes.GET;
        this._CoAPCodeClass = CoAPResponseCodes.SUCCESS;
        this._CoAPCodeDetail = CoAPResponseCodes.CREATED;
        this._CoAPMessageId = 0;
        this._CoAPToken = null;
        this.options_Map_List = new TreeMap<CoAPOptionsFormat, ArrayList<CoAPOptions<?>> >(); //Empty TreeMap, <IF_MATCH(1), ArrayList<CoAPOptions<T>>>
        this._CoAPPayload = null;
    }
    /**SortedMap is an interface while TreeMap is a class. TreeMap is child to SortedMap while SortedMap is parent to TreeMap.
     * CoAPOptions<?>
     * private Boolean _isCrucial;
     * private Boolean _isUnsafe;
     * private Boolean _isNoCacheKey;
     * private Boolean _isRepeatable;
     * private CoAPOptionsFormat _name; IF_MATCH(1)
     * protected T _value;
     * private Type _type;
     */

    /**
     * EX:
     * <Uri_Path, List(new instance(CoAPOption(C,U,N,R,Format, Length)))>
     * Wildcard Type
     * @return
     */


    /**
    Version (Ver): 2-bit unsigned integer. Indicates the CoAP version number. 
    Implementations of this specification MUST set this field to 1 (01 binary). 
    Other values are reserved for future versions.
    Messages with unknown version numbers MUST be silently ignored.
    Between 0 and 2 bits, 00 -> 11, 0x0 -> 0x3.
    */
    public CoAPMessage setCoAPVersion(int _CoAPVersion) throws Exception{
        if(_CoAPVersion < 0x0 || _CoAPVersion > 0x3)
            throw new Exception("_CoAPVersion have a Max Size of 2-bits, _CoAPVersion: " + _CoAPVersion);
        this._CoAPVersion = _CoAPVersion;
        return this;
    }
    public int getCoAPVersion(){
        return this._CoAPVersion;
    }


    /*
    Type (T): 2-bit unsigned integer. Indicates if this message is of
    type Confirmable (0), Non-confirmable (1), Acknowledgement (2), or Reset (3).
    */
    public CoAPMessage setCoAPType(CoAPMessageTypes _CoAPType){
        this._CoAPType = _CoAPType;
        return this;
    }
    public CoAPMessageTypes getCoAPType(){
        return this._CoAPType;   //remove "this"
    }


    public CoAPMessage setCoAPCode(CoAPCodes _CoAPCode){
        this._CoAPCode = _CoAPCode;
        return this;
    }
    public CoAPCodes getCoAPCode(){
        return this._CoAPCode;   //Remove "this"
    }

    public CoAPMessage setCodeClass(CoAPCode _CoAPCodeClass){
        this._CoAPCodeClass = _CoAPCodeClass;
        return this;
    }

    public CoAPMessage setCodeDetail(CoAPCode _CoAPCodeDetail){
        this._CoAPCodeDetail = _CoAPCodeDetail;
        return this;
    }

    public CoAPCode getCodeClass(){
        return this._CoAPCodeClass;
    }

    public CoAPCode getCodeDetail(){
        return this._CoAPCodeDetail;
    }



    /*
    16-bit unsigned integer in network byte order. Used to
    detect message duplication and to match messages of type
    Acknowledgement/Reset to messages of type Confirmable/Non-
    confirmable. The rules for generating a Message ID and matching
    messages are defined in Section 4.
    */
    public CoAPMessage setCoAPMessageId(int _CoAPMessageId) throws Exception {
        //Between 0 bits and 16 bits
        if(_CoAPMessageId < 0x00 || _CoAPMessageId > 0xffff)
            throw new Exception("MessageID have a Max Size of: 16-bit unsigned integer, MessageID: " + _CoAPMessageId);
        this._CoAPMessageId = _CoAPMessageId;
        return this;
    }
    public int getCoAPMessageId(){
        return this._CoAPMessageId;  //remove "this"
    }


    /*
    Token Length (TKL): 4-bit unsigned integer. Indicates the length of
    the variable-length Token field (0-8 bytes). Lengths 9-15 are
    reserved, MUST NOT be sent, and MUST be processed as a message format error.
    The Token is used to match a response with a request. The token
    value is a sequence of 0 to 8 bytes.
    */
    public CoAPMessage setCoAPToken(String _CoAPToken) throws Exception {
        if(_CoAPToken.getBytes().length < 0x00 || _CoAPToken.getBytes().length > 0xff)
            throw new Exception("_CoAPToken have a Max Size of: 8-bytes, _CoAPToken: " + _CoAPToken);
        this._CoAPToken = _CoAPToken;
        return this;
    }
    public String getCoAPToken(){
        return this._CoAPToken;  //remove "this"
    }


    @SuppressWarnings("serial")
    public CoAPMessage addCoAPOptions(CoAPOptions<?> _CoAPOption) throws Exception {
        /*If ArrayList is uninitialized, Empty, array dont contain key e.g URI_PATH
        = Create new ArrayList and add _CoAPOption
        options_Map_List = new TreeMap<CoAPOptionsFormat, ArrayList<CoAPOptions<?>>>(); //Empty TreeMap, <IF_MATCH(1), ArrayList<CoAPOptions<T>>>*/
        if(!options_Map_List.containsKey(_CoAPOption.getName())) {
            options_Map_List.put(_CoAPOption.getName(), new ArrayList<CoAPOptions<?>>() {{ add(_CoAPOption); }});
        }
        /*If option is not Repeatable and ArrayList is empty
        = Add: Get value from sortedmap from key(CoAPOptionName)*/
        else if(!_CoAPOption.isRepeatable() && options_Map_List.get(_CoAPOption.getName()).isEmpty())
            options_Map_List.get(_CoAPOption.getName()).add(_CoAPOption);
        else if(_CoAPOption.isRepeatable()) 
            options_Map_List.get(_CoAPOption.getName()).add(_CoAPOption);
        else
            throw new Exception("Cannot add option: " + _CoAPOption.getName());
        return this;
    }
    public CoAPMessage setCoAPOptions(SortedMap<CoAPOptionsFormat, ArrayList<CoAPOptions<?>>> options_Map_List){
        this.options_Map_List = options_Map_List;
        return this;
    }
    public CoAPMessage resetCoAPOptions() {
        this.options_Map_List = new TreeMap<CoAPOptionsFormat, ArrayList<CoAPOptions<?>>>();
        return this;
    }
    public SortedMap<CoAPOptionsFormat, ArrayList<CoAPOptions<?>>> getCoAPOptionsMap(){
        return this.options_Map_List;
    }
    public void printMap() { 
        System.out.println("\nTraversing TreeMap:"); 
        for (Map.Entry<CoAPOptionsFormat, ArrayList<CoAPOptions<?>>> e : options_Map_List.entrySet()) 
            System.out.println(e.getKey() 
                               + " "
                               + e.getValue()); 
    } 
    public ArrayList<CoAPOptions<?>> getCoAPOptionsMapAsArray() {
        ArrayList<CoAPOptions<?>> coAPOptions = new ArrayList<>();
        this.options_Map_List.values().forEach(arrayList -> arrayList.forEach(coAPOption -> coAPOptions.add(coAPOption)));
        return coAPOptions;
    }


    public CoAPMessage setCoAPPayload(String _CoAPPayload){
        this._CoAPPayload = _CoAPPayload;
        return this;
    }

    public String getCoAPPayload(){
        //if(_CoAPPayload == null)
        //    return "";
        //return _CoAPPayload.replaceAll("\u0000.*", "");    //remove "this"
        return _CoAPPayload;    //remove "this"
    }
    

    @Override
    public String toString() {
        StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append("CoAP Message: " + "\n");
        stringbuilder.append("CoAP Version: " + getCoAPVersion() + "\n");
        stringbuilder.append("CoAP Type: " + getCoAPType() + "\n");
        stringbuilder.append("CoAP Code: " + getCoAPCode() + "\n");
        stringbuilder.append("CoAP MessageId: " + getCoAPMessageId() + "\n");
        stringbuilder.append("CoAP Token: " + getCoAPToken() + "\n");
        var optionsArray = getCoAPOptionsMapAsArray();
        optionsArray.forEach(_CoAPOption -> {stringbuilder.append("Coap option number: " + _CoAPOption.getName() +  " = " + _CoAPOption.getValue() + "\n");});
        stringbuilder.append("CoAP Payload: " + getCoAPPayload() + "\n\n");
        return stringbuilder.toString();
    }
    
}
