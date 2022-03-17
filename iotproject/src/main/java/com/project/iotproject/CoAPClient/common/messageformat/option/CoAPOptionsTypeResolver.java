package com.project.iotproject.CoAPClient.common.messageformat.option;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import com.project.iotproject.Util.DataTypesUtil;

public class CoAPOptionsTypeResolver {

    @SuppressWarnings({ "serial", "unchecked" })
    private static Map<CoAPOptionsFormat, Class<CoAPOptions<?>>> CoAPOptionTable = new HashMap() {
        {
            put(CoAPOptionsFormat.IF_MATCH, CoAPOptionsUnknown.class);
            put(CoAPOptionsFormat.URI_HOST, CoAPOptionsString.class);
            put(CoAPOptionsFormat.ETAG, CoAPOptionsUnknown.class);
            put(CoAPOptionsFormat.IF_NONE_MATCH, CoAPOptionsUnknown.class);
            put(CoAPOptionsFormat.URI_PORT, CoAPOptionsInteger.class);
            put(CoAPOptionsFormat.LOCATION_PATH, CoAPOptionsString.class);
            put(CoAPOptionsFormat.URI_PATH, CoAPOptionsString.class);
            put(CoAPOptionsFormat.CONTENT_FORMAT, CoAPOptionsInteger.class);
            put(CoAPOptionsFormat.MAX_AGE, CoAPOptionsInteger.class);
            put(CoAPOptionsFormat.URI_QUERY, CoAPOptionsString.class);
            put(CoAPOptionsFormat.ACCEPT, CoAPOptionsInteger.class);
            put(CoAPOptionsFormat.LOCATION_QUERY, CoAPOptionsString.class);
            put(CoAPOptionsFormat.SIZE2, CoAPOptionsInteger.class);
            put(CoAPOptionsFormat.PROXY_URI, CoAPOptionsString.class);
            put(CoAPOptionsFormat.PROXY_SCHEME, CoAPOptionsString.class);
            put(CoAPOptionsFormat.SIZE1, CoAPOptionsInteger.class);
        }
    };



    public static CoAPOptions<?> getCoAPOption(CoAPOptionsFormat coAPOptionName, Type type, byte[] coAPValueByteArray) throws Exception {
        CoAPOptions<?> coAPOption;
        if (!CoAPOptionTable.containsKey(coAPOptionName)) {
            System.out.println("CoapOptionResolver doesnt containt optionNumber, giving it a unknown option: " + coAPOptionName);
            coAPOption = new CoAPOptionsUnknown(coAPOptionName, coAPValueByteArray);
        }
        else{
            Object coAPValue;
            if (type == Integer.class){
                coAPValue = DataTypesUtil.byteArrayToInteger1(coAPValueByteArray);
            }
            else if(type == String.class){
                coAPValue = DataTypesUtil.byteArrayToString(coAPValueByteArray);
            }
            //else if(type == Object.class){
            //    System.out.println("asdasdsa: " + coAPValueByteArray);
            //    ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(coAPValueByteArray));
            //    coAPValue = in.readObject();
            //}
            else{
                coAPValue = DataTypesUtil.byteArrayToInteger1(coAPValueByteArray);
                //coAPValue = DataTypesUtil.byteArrayToString(coAPValueByteArray);
                //throw new Exception("Could not parse CoAPOption " + coAPOptionName + " and Type: " + type); 
            }
               
            //if(coAPValue == null){
            //    coAPOption = null;
            //} else {
                //(Class)gets coAPValue of hashmap, contructor object, class type, new instance of class object.
                coAPOption = CoAPOptionTable.get(coAPOptionName).getDeclaredConstructor(CoAPOptionsFormat.class, (Class<?>)type).newInstance(coAPOptionName, coAPValue);
            //}
        }
        return coAPOption;
    }

    //coAPOptionFormatName = URI_PATH, get(coAPOptionFormatName) = CoAPOptionXXXXX.class
    public static Type getType(CoAPOptionsFormat coAPOptionFormatName) throws Exception{
        return ((ParameterizedType)CoAPOptionTable.get(coAPOptionFormatName).getGenericSuperclass()).getActualTypeArguments()[0];
    }

}
