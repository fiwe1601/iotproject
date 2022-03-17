package com.project.iotproject.Util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import com.project.iotproject.CoAPClient.common.messageformat.option.CoAPOptions;

//https://javadeveloperzone.com/java-basic/java-convert-int-to-byte-array/
public class DataTypesUtil {

    public DataTypesUtil() throws IllegalAccessException {
		throw new IllegalAccessException("Utility Class");
	}

    private static final int TWO_BYTE_INT_MAX = 65535;
	private static final long FOUR_BYTE_INT_MAX = 4294967295L;
    public static final int VARIABLE_BYTE_INT_MAX = 268435455;
    protected static final Charset STRING_ENCODING = StandardCharsets.UTF_8;

    public static byte [] integerToByteArray(final Integer value) {
        BigInteger bigInt = BigInteger.valueOf(value);
        return bigInt.toByteArray();
    }

    public static byte[] bigIntToByteArray(final int i) {
        BigInteger bigInt = BigInteger.valueOf(i);      
        return bigInt.toByteArray();
    }

    public static byte[] intToByteArray ( final int i ) throws IOException {      
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        dos.writeInt(i);
        dos.flush();
        return bos.toByteArray();
    }

    public static byte[] intToBytes(final int i ) {
        ByteBuffer bb = ByteBuffer.allocate(4); 
        bb.putInt(i); 
        return bb.array();
    }
    
    public static byte [] stringToByteArray(String value) {
        return value.getBytes();
    }

    public static byte[] intToBytesShift(final int data) {
        return new byte[] {
            (byte)((data >> 24) & 0xff),
            (byte)((data >> 16) & 0xff),
            (byte)((data >> 8) & 0xff),
            (byte)((data >> 0) & 0xff),
        };
    }

    public static int ByteArrayToInt(byte[] intBytes){
        ByteBuffer byteBuffer = ByteBuffer.wrap(intBytes);
        return byteBuffer.getInt();
    }

    public static int convertByteArrayToIntShift(byte[] data) {
        if (data == null || data.length != 4) return 0x0;
        // ----------
        return (int)( // NOTE: type cast not necessary for int
                (0xff & data[0]) << 24  |
                (0xff & data[1]) << 16  |
                (0xff & data[2]) << 8   |
                (0xff & data[3]) << 0
                );
    }

   /*public byte[] convertIntArrayToByteArray(int[] data) {
        if (data == null) return null;
        // ----------
        byte[] byts = new byte[data.length * 4];
        for (int i = 0; i < data.length; i++)
            System.arraycopy(convertIntToByteArray(data[i]), 0, byts, i * 4, 4);
        return byts;
    }*/

    public int[] byteArrayToIntArray(byte[] data) {
        if (data == null || data.length % 4 != 0) return null;
        // ----------
        int[] ints = new int[data.length / 4];
        for (int i = 0; i < ints.length; i++)
            ints[i] = (ByteArrayToInt(new byte[] {
                    data[(i*4)],
                    data[(i*4)+1],
                    data[(i*4)+2],
                    data[(i*4)+3],
            } ));
        return ints;
    }

    //----------------------------------------
    public static Integer byteArrayToInteger(byte [] bytes) {
        return new BigInteger(bytes).intValue();
    }

    public static int byteArrayToIntegerNew(byte [] bytes) {
        return ByteBuffer.wrap(bytes).getShort();
    }


    public static long bytestoLong(byte[] bytes){
        long res = 0;
        for (byte b : bytes) {
            res <<= 8;   // make room for next byte
            res |= b;    // append next byte
        }
        return res;
    }

    public static int twoBytesToInt(byte[] bytes){
        return ((bytes[0] & 0xff) << 8) | (bytes[1] & 0xff);
    }

    public static Integer byteArrayToInteger1(byte[] bytes){
        Integer value = 0;
        for (byte b : bytes) {
            value = (value << 8) + (b & 0xFF);
        }
        return value;
    }

    public static int byteArrayToInt(byte[] intBytes){
        int intBuffer = ByteBuffer.wrap(intBytes).getInt();
        return intBuffer;
    }

    public static String byteToBinaryString(byte n) {
		StringBuilder sb = new StringBuilder("00000000");
		for (int bit = 0; bit < 8; bit++) {
			if (((n >> bit) & 1) > 0)
				sb.setCharAt(7 - bit, '1');
        }
		return sb.toString();
    }
    
    public static String byteToString(byte b) {
        byte[] masks = { -128, 64, 32, 16, 8, 4, 2, 1 };
        StringBuilder builder = new StringBuilder();
        for (byte m : masks) {
            if ((b & m) == m) {
                builder.append('1');
            } else {
                builder.append('0');
            }
        }
        return builder.toString();
    }

    public static void printBytesAsString(byte[] bytes) {
        Integer i = 0;
        System.out.println("Bytes:");
        for(byte _byte : bytes){
            System.out.println("Byte "+ i++ + ": " + byteToBinaryString(_byte));
        }
        System.out.println("Total Bytes: "+ i + "\n");
    }

    public static void printBytesAsBinaryString(byte[] bytes, Integer appendedMessageSize) {
        System.out.println("\n" + "Total Message Length(Bytes): " + appendedMessageSize);
        for (int i= 0; i < appendedMessageSize; i++) {
            System.out.println("Byte "+ i + ": " + byteToBinaryString(bytes[i]));
        }
    }

    public static String byteArrayToString(byte [] bytes) {
        return new String(bytes);
    }

    public static byte[] coAPOptionValuetoBytes(CoAPOptions<?> coAPOption) throws Exception {
        byte[] coAPOptionValueinBytes;
        if(coAPOption.getValue() instanceof String)
            coAPOptionValueinBytes = ((String)coAPOption.getValue()).getBytes();
        else if(coAPOption.getValue() instanceof Integer){
            coAPOptionValueinBytes = DataTypesUtil.integerToByteArray((Integer)coAPOption.getValue());
        }
        else
            throw new Exception("Not able to generate Byte[] from CoAPOption Value: " + coAPOption.getValue());
        return coAPOptionValueinBytes;
    }

    public static Integer booleanArrayToInteger(Boolean... boolArray){
        int n = 0, l = boolArray.length;
        for (int i = 0; i < l; ++i) {
            n = (n << 1) + (boolArray[i] ? 1 : 0);
        }
        return n;
    }

    public static int boolArrayToInteger(boolean ... bools) {
        byte val = 0;
        for(int i = 0; i < bools.length; i++) {
            val <<= 1;
            if (bools[i]) val |= 1;
        }

        return val & ((int)Math.pow(2, bools.length) - 1);
    }
    

	public static void validateVariableByteInt(long value) throws IllegalArgumentException {
		if (value >= 0 && value <= VARIABLE_BYTE_INT_MAX) {
			return;
		} else {
			throw new IllegalArgumentException("Value must be between 0 and " + VARIABLE_BYTE_INT_MAX);
		}
	}

	public static MqttRemainingLengthVariable decodeRemainingLengthVariableByteInteger(DataInputStream inputStream)
            throws Exception {
        int multiplier = 1;
        int value = 0;
        byte encodedByte;
		int numBytes = 0;
		do {
			encodedByte = inputStream.readByte(); //'next byte from stream'
			numBytes++;
			value += ((encodedByte & 0x7F) * multiplier);
            multiplier *= 128;
            if (multiplier > Math.pow(128, 3)) {
                throw new Exception("Malformed Remaining Length");
            }
		} while ((encodedByte & 0x80) != 0);
		if (value < 0 || value > VARIABLE_BYTE_INT_MAX) {
			throw new Exception("Must be  a number between 0 and " + VARIABLE_BYTE_INT_MAX + ". Value Read: " + value);
		}
		return new MqttRemainingLengthVariable(value, numBytes);
	}

    public static void encodeRemainingLengthVariableByteInteger(ByteArrayOutputStream byteArrayOutputStream, long x, int maxByte){
        validateVariableByteInt(x);
        int numBytes = 0;
        byte encodedByte;
        do {
            encodedByte = (byte) (x % 128);
            x = (x / 128);
            // if there are more data to encode, set the top bit of this byte
            if(x > 0){
                encodedByte |= 0x80;
            }
            byteArrayOutputStream.write(encodedByte);
            numBytes++;
        } while ((x > 0) && (numBytes < maxByte));
    }

	public static void encodeString(ByteArrayOutputStream byteArrayOutputStream, String stringToEncode) throws Exception {
		validateUTF8String(stringToEncode);
		try {
			byte[] encodedString = stringToEncode.getBytes(STRING_ENCODING); //StandardCharsets.UTF_8
            int stringLength = encodedString.length;
			byte byte1 = (byte) ((stringLength >>> 8) & 0xFF); //0000 0000 0000 0100 >>> 8 -> 0000 0100 0000 0000 & 1111 1111 => 0000 0000
            byte byte2 = (byte) ((stringLength >>> 0) & 0xFF); //0000 0000 0000 0100 >>> 0 -> 0000 0000 0000 0100 & 1111 1111 => 0000 0100
            //byte byte1 = (byte) ((stringLength & 0xFF00) >>> 0x08);
			//byte byte2 = (byte) ((stringLength & 0xFF));

			byteArrayOutputStream.write(byte1); //Length MSB
			byteArrayOutputStream.write(byte2); //Length LSB
            
            //byteArrayOutputStream.write(encodedString, 0, stringLength == 0 ? (stringLength+1) : stringLength); //Data

            if(byte1 != 0 || byte2 != 0 || stringLength > 0){
                byteArrayOutputStream.write(encodedString, 0, stringLength); //Data
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


    public static void writeTwoBytes(ByteArrayOutputStream byteArrayOutputStream, int number) {
        byteArrayOutputStream.write((number & 0xFF00) >> 8);
        byteArrayOutputStream.write(number & 0xFF);
    }

















    public static int getTwoBytesToInt(ByteArrayInputStream byteArrayInputStream) throws IOException {
        return ((byteArrayInputStream.read() << 8) | byteArrayInputStream.read());
    }






	public static void validateTwoByteInt(Integer value) throws IllegalArgumentException {
		if (value == null) {
			return;
		}
		if (value >= 0 && value <= TWO_BYTE_INT_MAX) {
			return;
		} else {
			throw new IllegalArgumentException("This property must be a number between 0 and " + TWO_BYTE_INT_MAX);
		}
	}

	public static void validateFourByteInt(Long value) throws IllegalArgumentException {
		if (value == null) {
			return;
		}
		if (value >= 0 && value <= FOUR_BYTE_INT_MAX) {
			return;
		} else {
			throw new IllegalArgumentException("This property must be a number between 0 and " + FOUR_BYTE_INT_MAX);
		}
	}

	public static void writeUnsignedFourByteInt(long value, DataOutputStream stream) throws IOException {
		stream.writeByte((byte) (value >>> 24));
		stream.writeByte((byte) (value >>> 16));
		stream.writeByte((byte) (value >>> 8));
		stream.writeByte((byte) (value >>> 0));
	}

	public static Long readUnsignedFourByteInt(DataInputStream inputStream) throws Exception {
		byte[] readBuffer = {0, 0, 0, 0, 0, 0, 0, 0};
		inputStream.readFully(readBuffer, 4, 4);
		return (((long) readBuffer[0] << 56) + ((long) (readBuffer[1] & 255) << 48)
				+ ((long) (readBuffer[2] & 255) << 40) + ((long) (readBuffer[3] & 255) << 32)
				+ ((long) (readBuffer[4] & 255) << 24) + ((readBuffer[5] & 255) << 16) + ((readBuffer[6] & 255) << 8)
				+ ((readBuffer[7] & 255) << 0));
	}

	public static int readUnsignedTwoByteInt(DataInputStream inputStream) throws Exception {
		// byte readBuffer[] = {0,0}
		int ch1 = inputStream.read();
		int ch2 = inputStream.read();
		if ((ch1 | ch2) < 0)
			throw new Exception();
		return (int) ((ch1 << 8) + (ch2 << 0));
	}

	public static String decodeUTF8(DataInputStream input) throws Exception {
		int encodedLength;
		try {
			encodedLength = input.readUnsignedShort();

			byte[] encodedString = new byte[encodedLength];
			input.readFully(encodedString);
			String output = new String(encodedString, STRING_ENCODING);
			validateUTF8String(output);

			return output;
		} catch (IOException ioe) {
			throw new Exception(ioe);
		}
	}

	private static void validateUTF8String(String input) throws IllegalArgumentException {
		for (int i = 0; i < input.length(); i++) {
			boolean isBad = false;
			char c = input.charAt(i);
			/* Check for mismatched surrogates */
			if (Character.isHighSurrogate(c)) {
				if (++i == input.length()) {
					isBad = true; /* Trailing high surrogate */
				} else {
					char c2 = input.charAt(i);
					if (!Character.isLowSurrogate(c2)) {
						isBad = true; /* No low surrogate */
					} else {
						int ch = ((((int) c) & 0x3ff) << 10) | (c2 & 0x3ff);
						if ((ch & 0xffff) == 0xffff || (ch & 0xffff) == 0xfffe) {
							isBad = true; /* Noncharacter in base plane */
						}
					}
				}
			} else {
				if (Character.isISOControl(c) || Character.isLowSurrogate(c)) {
					isBad = true; /* Control character or no high surrogate */
				} else if (c >= 0xfdd0 && (c <= 0xfddf || c >= 0xfffe)) {
					isBad = true; /* Noncharacter in other nonbase plane */
				}
			}
			if (isBad) {
				throw new IllegalArgumentException(String.format("Invalid UTF-8 char: [%04x]", (int) c));
			}
		}
	}





}
