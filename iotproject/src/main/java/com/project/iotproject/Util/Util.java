package com.project.iotproject.Util;

import java.util.regex.*;

import com.project.iotproject.CoAPClient.common.messageformat.CoAPMessage;

public class Util {
    /** Always throws {@link RuntimeException} with the given message */
    public static <T> T throwException(String msg)
    {
        throw new RuntimeException(msg);
    }

    public static Boolean containsIllegalCharacter(String str){
        String regex = "[^a-zA-Z0-9]";
        Pattern p = Pattern.compile(regex);
        if (str == null) {
            return true;
        }
        return p.matcher(str).find();
    }


    public static void printCoAPMessage(CoAPMessage coAPMessage){
        System.out.println("\nCoAPMessage:");
		System.out.println(""+"Coap Version (Ver): " + coAPMessage.getCoAPVersion());
		System.out.println("Coap Type (T): " + coAPMessage.getCoAPType());
		if (coAPMessage.getCoAPToken() == null) {
			System.out.println("Coap Token Lenght (TKL): " + 0);
		}
		else{
			System.out.println("Coap Token Lenght (TKL): " + coAPMessage.getCoAPToken().getBytes().length);
		}
		System.out.println("CoAP Code (Code): " + coAPMessage.getCoAPCode());
		System.out.println("CoAP Message ID: " + coAPMessage.getCoAPMessageId());
		System.out.println("CoAP Token: " + coAPMessage.getCoAPToken());

		var optionsArray = coAPMessage.getCoAPOptionsMapAsArray();
		if (!optionsArray.isEmpty()) {
			optionsArray.forEach(option -> {
				System.out.println("CoAP Option Number: " + option.getName() +  ", Value= " + option.getValue());
			});
		}
        System.out.println("CoAP Payload: " + coAPMessage.getCoAPPayload() + "\n\n");
	}


	public static void printTransmissionCoAPMessage(byte[] packet, CoAPMessage coAPMessage){
		System.out.println("\n" +"----Message Sent/Received:----");
		DataTypesUtil.printBytesAsBinaryString(packet, packet.length);
		//DataTypesUtil.printBytesAsString(packetBuffer);
		Util.printCoAPMessage(coAPMessage);
	}
}
