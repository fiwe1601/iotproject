package com.project.iotproject.CoAPClient.common.messageformat;

import java.lang.reflect.Field;

public interface CoAPCode {

    default Integer getKey() {
        //default <E extends Enum<E> & ABAB> E getKey(final Class<E> EnumClass, final ABAB _CoAPCode){
        try{
            Class<?> clazz = getClass();
            Field field = clazz.getDeclaredField("_CoAPCode");
            return field.getInt(this);
        }catch(RuntimeException e){
            throw e;
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    //Input ENUM URI-PATH -> Get Integer
    public static <E extends Enum<E> & CoAPCode> Integer getKey(final Class<E> EnumClass, final E _CoAPCode){
        if (_CoAPCode != null) {
            for ( final E pc : EnumClass.getEnumConstants() ) {
                if ( _CoAPCode == pc ) {
                    return pc.getKey();
                }
            }
        }
        return null;
    }

    //Input Integer -> Get ENUM URI-PATH
    public static <E extends Enum<E> & CoAPCode> E getValue(final Class<E> EnumClass, final Integer _CoAPCode){
        if (_CoAPCode != null) {
            for ( final E pc : EnumClass.getEnumConstants() ) {
                if ( _CoAPCode == pc.getKey() ) {
                    return pc;
                }
            }
        }
        return null;
    }
   
}
