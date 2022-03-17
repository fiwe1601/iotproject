package com.project.iotproject.CoAPClient.common.messageformat.option;

import java.lang.reflect.Type;
/** 
* "Opaque" = "fill a gap".
* 
*   0   1   2   3   4   5   6   7
* +---------------+---------------+
* | Option Delta  | Option Length | 1 byte
* +---------------+---------------+
* / Option Delta                  / 0-2 bytes
* \ (extended)                    \
* +-------------------------------+
* / Option Length                 / 0-2 bytes
* \ (extended)                    \
* +-------------------------------+
* / Option Value                  / 0 or more bytes
* +-------------------------------+
* 
* 
* +-----+----+---+---+---+----------------+--------+--------+-------------+
* | No. | C  | U | N | R | Name           | Format | Length | Default     |
* +-----+----+---+---+---+----------------+--------+--------+-------------+
* | 1   | x  |   |   | x | If-Match       | opaque | 0-8    | (none)      |
* | 3   | x  | x | - |   | Uri-Host       | string | 1-255  | (see note 1)|
* | 4   |    |   |   | x | ETag           | opaque | 1-8    | (none)      |
* | 5   | x  |   |   |   | If-None-Match  | empty  | 0      | (none)      |
* | 7   | x  | x | - |   | Uri-Port       | uint   | 0-2    | (see note 1)|
* | 8   |    |   |   | x | Location-Path  | string | 0-255  | (none)      |
* | 11  | x  | x | - | x | Uri-Path       | string | 0-255  | (none)      |
* | 12  |    |   |   |   | Content-Format | uint   | 0-2    | (none)      |
* | 14  |    | x | - |   | Max-Age        | uint   | 0-4    | 60          |
* | 15  | x  | x | - | x | Uri-Query      | string | 0-255  | (none)      |
* | 17  | x  |   |   |   | Accept         | uint   | 0-2    | (none)      |
* | 20  |    |   |   | x | Location-Query | string | 0-255  | (none)      |
* | 28  |    |   | x |   | Size2          | uint   | 0-4    | (none)      |
* | 35  | x  | x | - |   | Proxy-Uri      | string | 1-1034 | (none)      |
* | 39  | x  | x | - |   | Proxy-Scheme   | string | 1-255  | (none)      |
* | 60  |    |   | x |   | Size1          | uint   | 0-4    | (none)      |
* +-----+----+---+---+---+----------------+--------+--------+-------------+
*
* C=Critical, U=Unsafe, N=No-Cache-Key, R=Repeatable
* Note 1: taken from destination address/port of request message
**/ 


public abstract class CoAPOptions<T> {
    private Boolean _isCrucial;
    private Boolean _isUnsafe;
    private Boolean _isNoCacheKey;
    private Boolean _isRepeatable;
    private CoAPOptionsFormat _name;
    protected T _value;
    private Type _type;

    public CoAPOptions(CoAPOptionsFormat _name, T _value, Type _Type){
        this._isCrucial = isCritical(_name.getKey());
        this._isUnsafe = isUnSafe(_name.getKey());
        this._isNoCacheKey = isNoCacheKey(_name.getKey());
        this._isRepeatable = isRepeatable(_name.getKey());
        this._name = _name;
        this._value = _value;
        this._type = _type;
    }

    public CoAPOptions(
        Boolean _isCrucial, 
        Boolean _isUnsafe, 
        Boolean _isNoCacheKey, 
        Boolean _isRepetable,
        CoAPOptionsFormat _name,
        T _value,
        Type _Type
    ) {
        this._isCrucial = _isCrucial;
        this._isUnsafe = _isUnsafe;
        this._isNoCacheKey = _isNoCacheKey;
        this._isRepeatable = _isRepetable;
        this._name = _name;
        this._value = _value;
        this._type = _type;
    }


    public CoAPOptionsFormat getName(){
        return this._name;
    }

    public Boolean isCrucial() {
        return this._isCrucial;
    }

    public Boolean isUnsafe() {
        return this._isUnsafe;
    }

    public Boolean isNoCacheKey() {
        return this._isNoCacheKey;
    }

    public Boolean isRepeatable() {
        return this._isRepeatable;
    }

    public T getValue() {
        return this._value;
    }

    public void setValue(T _value){
        this._value = _value;
    }

    public void setFormat(Type T){
        this._type = T;
    }

    public Type getFormat(){
        return this._type;
    }

    public class Option_Numbers{
        private Option_Numbers() {}
        public static final int CRITICAL = 1; // 0000 0001
        public static final int UN_SAFE = 2; // 0000 0010
        public static final int NO_CACHE_KEY = 0x1E;// 0001 1110
        public static final int NO_CACHE_KEY_1 = 0x1C;// 0001 1110
        public static final int REPEATABLE = 0x1F; // 0001 1100
    }

    // When bit 7 (the least significant bit) is 1, an option is Critical (and
    // likewise Elective when 0)
    public boolean isCritical(int onum) {
        return (onum & Option_Numbers.CRITICAL) != 0;
    }

    //When bit 6 is 1, an option is Unsafe (and likewise Safe when 0).
    public boolean isUnSafe(int onum) {
        return (onum & Option_Numbers.UN_SAFE) != 0;
    }

    //When bit 6 is 0, i.e., the option is not Unsafe, it is not a Cache-Key (NoCacheKey) if and only if bits 3-5 are all set to 1
    public boolean isNoCacheKey(int onum) {
        return (onum & Option_Numbers.NO_CACHE_KEY) == Option_Numbers.NO_CACHE_KEY_1;
    }

    public boolean isRepeatable(int onum){
        if(onum == 1 || onum == 4 || onum == 8 || onum == 11 || onum == 15 || onum == 20)
            return true;
        else
            return false;
    }
    public abstract void setValueOfBytes(byte [] bytes);
    public abstract byte [] getValueAsBytes();
}
