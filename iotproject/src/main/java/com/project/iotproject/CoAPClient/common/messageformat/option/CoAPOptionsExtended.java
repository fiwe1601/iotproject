package com.project.iotproject.CoAPClient.common.messageformat.option;

import com.project.iotproject.Util.Util;

//Option Delta:
// 0 to 12: For delta between 0 to 12: Represents the exact delta value between the last option ID and the desired option ID, with no Option Delta Extended value
// 13: For delta from 13 to 268: Option Delta Extended is an 8-bit value that represents the Option Delta value minus 13
// 14: For delta from 269 to 65,804: Option Delta Extended is a 16-bit value that represents the Option Delta value minus 269
// 15: Reserved for Payload Marker, where the Option Delta and Option Length are set together as 0xFF.

// Option Length:
// 0 to 12: For Option Length between 0 to 12: Represents the exact length value, with no Option Length Extended value
// 13: For Option Length from 13 to 268: Option Length Extended is an 8-bit value that represents the Option Length value minus 13
// 14: For Option Length from 269 to 65,804: Option Length Extended is a 16-bit value that represents the Option Length value minus 269
// 15: Reserved for future use. It is an error if Option Length field is set to 0xFF.

// Option Value:
// Size of Option Value field is defined by Option Length value in bytes.
// Semantic and format this field depends on the respective option.


public enum CoAPOptionsExtended {
    ZEROBITS(0), //0 Bytes 0x0
    EIGHTBITS(8),   //1 Byte 1111 1111 255 0xff
    SIXTEENBITS(16); //2 Byte 1111 1111 1111 1111 0xffff
    private Integer coAPOptionExtended;
    private CoAPOptionsExtended(Integer coAPOptionExtended) { this.coAPOptionExtended = coAPOptionExtended; }

    public static CoAPOptionsExtended getOptionExtended(Integer optionDelta) throws Exception{
        if(optionDelta >= 65805){
            throw new Exception("Option Delta to Large: " + optionDelta);
        }
        return (optionDelta >= 0 && optionDelta <= 12) ? CoAPOptionsExtended.ZEROBITS : 
            (optionDelta >= 13 && optionDelta <= 268) ? CoAPOptionsExtended.EIGHTBITS : 
            (optionDelta >= 269 && optionDelta <= 65804) ? CoAPOptionsExtended.SIXTEENBITS : Util.<CoAPOptionsExtended> throwException("Unable to retrive CoAPOptionsExtended from Integer: " + optionDelta);
    }
    //public static CoAPOptionsExtended getOptionExtended(Integer optionDelta) throws Exception{
    //    return (optionDelta > 0xFF) ? ("Unable to retrive CoAPOptionsExtended from Integer: " + optionDelta) : (optionDelta <= 12) ? CoAPOptionsExtended.ZEROBITS : optionDelta <= 0xFF ? CoAPOptionsExtended.EIGHTBITS : CoAPOptionsExtended.SIXTEENBITS;
    //}

    public static Integer getCoAPExtendedDeltaValue(CoAPOptionsExtended optionDelta, Integer deltaValue) throws Exception {
        switch (optionDelta) {
            case ZEROBITS: return deltaValue;
            case EIGHTBITS: return 13;
            case SIXTEENBITS: return 14;
            default: throw new Exception("Could not return getCoAPExtendedDeltaValue by: " + optionDelta);
        }
    }

    public Integer getcoAPOptionExtended(){
        return this.coAPOptionExtended;
    }

}
