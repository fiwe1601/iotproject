package com.project.iotproject.CoAPClient.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;


import com.project.iotproject.CoAPClient.common.messageformat.CoAPCodes;
import com.project.iotproject.CoAPClient.common.messageformat.CoAPMessage;
import com.project.iotproject.CoAPClient.common.messageformat.CoAPMessageTypes;
import com.project.iotproject.CoAPClient.common.messageformat.option.CoAPOptions;
import com.project.iotproject.CoAPClient.common.messageformat.option.CoAPOptionsExtended;
import com.project.iotproject.CoAPClient.common.messageformat.option.CoAPOptionsFormat;
import com.project.iotproject.CoAPClient.common.messageformat.option.CoAPOptionsTypeResolver;
import com.project.iotproject.common.MessageParser;
import com.project.iotproject.Util.DataTypesUtil;

public class CoAPMessageHandler implements MessageParser<CoAPMessage> {

    public CoAPMessageHandler(){}

    //Message endoced ready to be sent.
    @Override
    public byte[] encodeMessage(CoAPMessage coapMessage) {
        byte[] messageBuffer = null;
        try (var byteArrayOutputStream = new ByteArrayOutputStream()){
            //Ver, T, TKL
            //var messageFirstByte = coapMessage.getCoAPVersion() << 6;           //2 bit, XX00 0000 Version
            //messageFirstByte |= (coapMessage.getCoAPType().getKey() << 4);         //2 bit, XXXX 0000 Type
            //                        xx00 | 00xx << 4 -> xxxx 0000                       
            int messageFirstByte = (((coapMessage.getCoAPVersion() << 2) | coapMessage.getCoAPType().getKey()) << 4);

            if (coapMessage.getCoAPToken() != null){
                messageFirstByte |= (coapMessage.getCoAPToken().getBytes().length); //4 bit, XXXX XXXX Token Lenght
            }
            byteArrayOutputStream.write(messageFirstByte);  //Write first Byte to Outputstreamarray

            //Code, Second Byte
            if (coapMessage.getCoAPCode() != null) {
                int messageSecondByte = coapMessage.getCoAPCode().getKey();
                byteArrayOutputStream.write(messageSecondByte);
            }

            //Message ID, Third to Fourth Byte, XXXX XXXX XXXX XXXX
            //int messageThirdandFourthByte = ((coapMessage.getCoAPMessageId() & 0xff00) >> 8); //XXXX XXXX XXXX XXXX & 1111 1111 0000 0000 => XXXX XXXX 0000 0000, >> 8 => 0000 0000 XXXX XXXX
            byteArrayOutputStream.write(((coapMessage.getCoAPMessageId() & 0xff00) >> 8));     //Only writes one byte at the time, Third Byte
            //messageThirdandFourthByte = (coapMessage.getCoAPMessageId() & 0xff);     ////XXXX XXXX XXXX XXXX & 0000 0000 1111 1111 => 0000 0000 XXXX XXXX
            byteArrayOutputStream.write((coapMessage.getCoAPMessageId() & 0xff));     //Fourth Byte
            //byte [] messageIdTwoBytes = DataTypesUtil.bigIntToByteArray(coapMessage.getCoAPMessageId());
            //byteArrayOutputStream.write(messageIdTwoBytes, 0, 2);-------------------------------------------------------------

            if(coapMessage.getCoAPToken() != null){
                byte[] tokenBytes = coapMessage.getCoAPToken().getBytes();
                byteArrayOutputStream.write(tokenBytes, 0, tokenBytes.length);
            }

            //OPTIONS, SortedMap<CoAPOptionFormat, ArrayList<CoAPOption<?>>>
            //         e.g      <URI_PATH        , ArrayList<CoAPOption.class>>
            //var coAPOptionsEntrySet = coapMessage.getCoAPOptionsMap().entrySet().iterator();
            var coAPOptionsIterator = coapMessage.getCoAPOptionsMap().values().iterator();
            encodeCoAPOption(0, coAPOptionsIterator, byteArrayOutputStream);


            /*
            If present and of non-zero length, it is prefixed by a
            fixed, one-byte Payload Marker (0xFF), which indicates the end of
            options and the start of the payload. 
            */
            if (coapMessage.getCoAPPayload() != null) {
                byteArrayOutputStream.write(0xff); //Payload Marker
                //byte [] payloadBytes = coapMessage.getCoAPPayload().getBytes();
                byteArrayOutputStream.write(coapMessage.getCoAPPayload().getBytes());
                //byteArrayOutputStream.write(payloadBytes, 0, payloadBytes.length); 
            }

            messageBuffer = byteArrayOutputStream.toByteArray();

            //return messageBuffer; //remove
        } catch (Exception e) {
            e.printStackTrace();
        }
        return messageBuffer;
    }

    //Decode a recived message
    @Override
    public CoAPMessage decodeMessage(byte[] _CoAPResponse) {
        CoAPMessage coAPMessage = new CoAPMessage();
        /*
        Bytewise reader
        A ByteArrayInputStream contains an internal buffer 
        that contains bytes that may be read from the stream. 
        An internal counter keeps track of the next byte to 
        be supplied by the read method.
        */
        try (var byteArrayInputStream = new ByteArrayInputStream(_CoAPResponse)) {
            var streamFirstByte = byteArrayInputStream.read();

            coAPMessage.setCoAPVersion((streamFirstByte & 0xc0) >> 6);    //1100 0000(0xc0), 0000 0011(>>6)
            coAPMessage.setCoAPType(CoAPMessageTypes.getValue((streamFirstByte & 0x30) >> 4));        //0011 0000(0x30), 0000 0011(>>4)
            int tokenLenght = (streamFirstByte & 0xf); //& 0000 1111

            var streamSecondByte = byteArrayInputStream.read();
            //int codeClass = ((streamSecondByte & 0xE0) >> 5);
            //int codeDetail = ((streamSecondByte & 0x1F));
            //coAPMessage.setCodeClass(CoAPCode.getValue(CoAPResponseCodes.class, codeClass));
            //coAPMessage.setCodeClass(CoAPCode.getValue(CoAPResponseCodes.class, codeDetail));
            
            coAPMessage.setCoAPCode(CoAPCodes.getValue(streamSecondByte));
            //coAPMessage.setCoAPCode(CoAPCode.getValue(CoAPResponseCodes.class, streamSecondByte));
            //coAPMessage.setCoAPCode(CoAPCodes.getValue(CoAPResponseCodes.class, byteArrayInputStream.read()));

            coAPMessage.setCoAPMessageId(DataTypesUtil.byteArrayToInteger(byteArrayInputStream.readNBytes(2)) & 0xffff);  //convert byte[] to Integer

            //coAPMessage.setCoAPToken(String.valueOf(DataTypesUtil.byteArrayToIntegerNew(byteArrayInputStream.readNBytes(tokenLenght)))); //convert byte[] to string
            coAPMessage.setCoAPToken(new String(byteArrayInputStream.readNBytes(tokenLenght))); //convert byte[] to string

            boolean payloadMarker = decodeCoAPOptions(coAPMessage, byteArrayInputStream, 0);

            if(payloadMarker) {
                //coAPMessage.setCoAPPayload(new String(byteArrayInputStream.readAllBytes())); //Reads rest of inputstream. 
                coAPMessage.setCoAPPayload(new String(byteArrayInputStream.readAllBytes()).replaceAll("\u0000.*", "")); //Reads rest of inputstream. \0 matches the NUL character (\u0000). removes NUL chars.
                //character value called the null character with value \u0000. This is the default value for type char.
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return coAPMessage;
    }

    //Iterator<ArrayList<AbstractCoapOption<?>>>
    public void encodeCoAPOption(Integer previousOptionNumber, Iterator<ArrayList<CoAPOptions<?>>> coAPIterator, 
        ByteArrayOutputStream byteArrayOutputStream) throws Exception {
        
        if (!coAPIterator.hasNext())
            return;
        //if (!coAPEntrySetIterator.next().getValue().isEmpty()) //ArrayList<CoAPOption<?>>>> coAPEntrySetIterator
        //    return;

        Iterator<CoAPOptions<?>> coAPOptionsIterator = coAPIterator.next().iterator();

        CoAPOptions<?> coAPOption = coAPOptionsIterator.next();

        Integer optionNumber = coAPOption.getName().getKey();   //Get value 1,3,4,5,7...

        var coAPOptionDelta = optionNumber - previousOptionNumber;  //First time previousOptionNumber=0
        byte[] coAPOptionValueinBytes = DataTypesUtil.coAPOptionValuetoBytes(coAPOption);   //Option Value
        
        Integer coAPOptionLength = coAPOptionValueinBytes.length;

        // +---------------+---------------+
        // | Option Delta  | Option Length | 1 byte
        // +---------------+---------------+
        CoAPOptionsExtended coAPOptionDeltaExtended = CoAPOptionsExtended.getOptionExtended(coAPOptionDelta); //Option Delta
        CoAPOptionsExtended coAPOptionLengthExtended = CoAPOptionsExtended.getOptionExtended(coAPOptionLength); //Option Length

        Integer optionDeltaExtended = CoAPOptionsExtended.getCoAPExtendedDeltaValue(coAPOptionDeltaExtended, coAPOptionDelta);
        Integer optionLengthExtended = CoAPOptionsExtended.getCoAPExtendedDeltaValue(coAPOptionLengthExtended, coAPOptionLength);

        byteArrayOutputStream.write(optionDeltaExtended << 4 | optionLengthExtended);

        // +---------------+---------------+
        // /         Option Delta          / 0-2 bytes
        // \          (extended)           \
        // +-------------------------------+
        // /         Option Length         / 0-2 bytes
        // \          (extended)           \
        // +-------------------------------+
        // coAPOptionDeltaExtended != CoAPOptionsExtended.ZEROBITS
        if ((coAPOptionDeltaExtended == CoAPOptionsExtended.EIGHTBITS) || (coAPOptionDeltaExtended == CoAPOptionsExtended.SIXTEENBITS))
            byteArrayOutputStream.write(DataTypesUtil.integerToByteArray(coAPOptionDelta));
        if ((coAPOptionLengthExtended == CoAPOptionsExtended.EIGHTBITS) || (coAPOptionLengthExtended == CoAPOptionsExtended.SIXTEENBITS))
            byteArrayOutputStream.write(DataTypesUtil.integerToByteArray(coAPOptionLength));

        // +-------------------------------+
        // /          Option Value         / 0 or more bytes
        // +-------------------------------+
        byteArrayOutputStream.write(coAPOptionValueinBytes);
        
        /**
         * Options with Option Delta=0
         * Byte 1: deltaValue/13/14
         * Byte 2: if (EIGHTBITS or SIXTEENBITS) -> Byte[], coAPOptionLength
         * Byte 3: coAPOptionValueinBytes
         
        while(coAPOptionsIterator.hasNext()){
            var coAPOptionMultiple = coAPOptionsIterator.next(); //CoAPOption<?>
            coAPOptionValueinBytes = coAPOptionValuetoBytes(coAPOptionMultiple);
            coAPOptionLength = coAPOptionValueinBytes.length;
            coAPOptionLengthExtended = CoAPOptionExtended.getOptionExtended(coAPOptionLength);
            byteArrayOutputStream.write(CoAPOptionExtended.getCoAPExtendedDeltaValue(coAPOptionLengthExtended, coAPOptionLength));
            if ((coAPOptionLengthExtended == CoAPOptionExtended.EIGHTBITS) || (coAPOptionLengthExtended == CoAPOptionExtended.SIXTEENBITS))
                byteArrayOutputStream.write(DataTypesUtil.integerToByteArray(coAPOptionLength));
            byteArrayOutputStream.write(coAPOptionValueinBytes); 
        }
        */
        encodeCoAPOption(optionNumber, coAPIterator, byteArrayOutputStream);
    }

    public boolean decodeCoAPOptions(CoAPMessage coAPMessage, ByteArrayInputStream byteArrayInputStream, int previousCoAPOptionNumber) throws Exception {

        var byteRead = byteArrayInputStream.read();

        //No Option nor No Payload, 0, -1
        if(byteRead == 0) {
            return false;
        }

        //Extended or No Extended Delta Option Number
        var regularOptionNumber = false;
        var coAPOptionDelta = (byteRead & 0xf0) >> 4; // (xxxxxxxx & 11110000) >> 4 => xxxx
        var coAPOptionLength = (byteRead & 0xf); // (xxxxxxxx & 00001111) => xxxx
        Integer coAPOptionNumber;
        
        //no more CoAP Option or Payload
        if (coAPOptionDelta == 0) {
            coAPOptionNumber = previousCoAPOptionNumber;
            //return false;
        }
        //Option Delta Extended
        else if(coAPOptionDelta == 13) {
            coAPOptionNumber = DataTypesUtil.byteArrayToInteger(byteArrayInputStream.readNBytes(1)) + previousCoAPOptionNumber;
        } else if(coAPOptionDelta == 14) {
            coAPOptionNumber = DataTypesUtil.byteArrayToInteger(byteArrayInputStream.readNBytes(2)) + previousCoAPOptionNumber;
        }
        //Payload Marker
        else if(coAPOptionDelta == 15 && coAPOptionLength == 15) { //Payload found
            return true;
        }
        //value between 0 and 12
        else {
            regularOptionNumber = true;
            coAPOptionNumber = coAPOptionDelta + previousCoAPOptionNumber;
        }
        //if(coAPOptionDelta >= 1 || coAPOptionDelta <= 12)
        //    coAPOptionNumber = coAPOptionDelta + previousCoAPOptionNumber;
        

        //Option Length Extended
        if(coAPOptionLength == 13) {
            coAPOptionLength = DataTypesUtil.byteArrayToInteger(byteArrayInputStream.readNBytes(1));
        } else if(coAPOptionLength == 14) {
            coAPOptionLength = DataTypesUtil.byteArrayToInteger(byteArrayInputStream.readNBytes(2));
        }
        //Reserved for future use only
        else if(coAPOptionLength == 15) { 
            throw new Exception("Option Value 15 is reserved for future use only!");
        }

        byte [] coAPOptionValue = byteArrayInputStream.readNBytes(coAPOptionLength);

        CoAPOptionsFormat coapOptionFormatNumber = CoAPOptionsFormat.getValue(coAPOptionNumber); //e.g IF_MATCH, ETAG

        if(coapOptionFormatNumber == null){
            return false;
        }

        CoAPOptions<?> option;
        option = CoAPOptionsTypeResolver.getCoAPOption(coapOptionFormatNumber, CoAPOptionsTypeResolver.getType(coapOptionFormatNumber), coAPOptionValue);
        coAPMessage.addCoAPOptions(option);
        
        return decodeCoAPOptions(coAPMessage, byteArrayInputStream, regularOptionNumber ? coAPOptionDelta : coAPOptionNumber);
    }   
}



/**
Bits 0-3 will tell you which option it is. This nibble only gives you the delta compared 
to the previous option encoded in this message. For the first option in the message, 
there is no previous option so the bits 0-3 give you the Option number.

Lets consider an example where you need to encode 2 options Uri-Port with value 7000 and 
Uri-Path with value /temp in a CoAP message. Options are always encoded in increasing order 
of the Option numbers. So you first encode Uri-Port which has Option number 7 and then 
Uri-Path with Option number 11.

Uri-Port As this is the first option in the message, the Option delta will be same as the 
Option number so Option delta = 0x7. Port value 7000 will take 2 bytes (0x1B58) so Option length = 0x2. 
So this Option will be encoded get encoded as 72 1b 58.

Uri-Path This is not the first Option in this message. Option delta for this option will be this 
option number - prev option number i.e. 11 - 7 = 4. Encoding temp will take 4 bytes so Option length = 4. 
So this option would get encoded as 44 74 65 6d 70

Note that this was for a simplified case where the Option number and length are not more than 12 bytes. 
When either of these is more than 12 bytes, you encode using the extended option delta/length as specified 
in the RFC.
 */