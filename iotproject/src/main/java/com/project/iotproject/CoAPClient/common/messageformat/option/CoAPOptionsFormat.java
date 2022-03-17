package com.project.iotproject.CoAPClient.common.messageformat.option;

import java.util.HashMap;
import java.util.Map;

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
 */

//Critical = (onum & 1);
//UnSafe = (onum & 2);
//NoCacheKey = ((onum & 0x1e) == 0x1c);

//When bit 7 (the least significant bit) is 1, an option is Critical (likewise Elective when 0)
//When bit 6 is 1, an option is Unsafe (and likewise Safe when 0).
//it is not a Cache-Key (NoCacheKey) if and only if bits 3-5 are all set to 1

public enum CoAPOptionsFormat {
                           //0123 4567
    IF_MATCH(1),           //0000 0001 C     R
    URI_HOST(3),           //0000 0011 C U
    ETAG(4),               //0000 0100       R
    IF_NONE_MATCH(5),      //0000 0101 C
    URI_PORT(7),           //0000 0111 C U
    LOCATION_PATH(8),      //0000 1000       R
    URI_PATH(11),          //0000 1011 C U   R
    CONTENT_FORMAT(12),    //0000 1100
    MAX_AGE(14),           //0000 1110   U
    URI_QUERY(15),         //0000 1111 C U   R
    ACCEPT(17),            //0001 0001 C
    LOCATION_QUERY(20),    //0001 0100       R
    SIZE2(28),             //0001 1100     N
    PROXY_URI(35),         //0010 0011 C U 
    PROXY_SCHEME(39),      //0010 0111 C U 
    SIZE1(60);             //0011 1100     N
                           //0001 1111 

    Integer coAPOptionFormatNumber;
    private static final Map<Integer, CoAPOptionsFormat> hashMap = new HashMap<>();

    private CoAPOptionsFormat(Integer coAPOptionFormatNumber) {      
        this.coAPOptionFormatNumber = coAPOptionFormatNumber;  
    }

    //Returns Key, Integer
    public Integer getKey(){
        return this.coAPOptionFormatNumber;
    }
    
    //Return value based on key, CoAPOptionsFormat
    public static CoAPOptionsFormat getValue(Integer coAPOptionFormatNumber) {
        return hashMap.get(coAPOptionFormatNumber);
    }

    /*static initializer
    It gets run once this class has been loaded into the JVM, whether or not an instance is being created. Hence populating the hashMap. on loading time.
    However, since we're dealing with an enum, all its instances have to be created as part of loading the class. */
    static{
        for(CoAPOptionsFormat e : CoAPOptionsFormat.values()){
            hashMap.put(e.getKey(), e);
        }
  
    }
    
}
