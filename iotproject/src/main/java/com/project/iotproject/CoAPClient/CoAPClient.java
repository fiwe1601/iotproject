package com.project.iotproject.CoAPClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import com.project.iotproject.CoAPClient.common.CoAPMessageHandler;
import com.project.iotproject.CoAPClient.common.CoAPProgramOptions;
import com.project.iotproject.CoAPClient.common.messageformat.CoAPCodes;
import com.project.iotproject.CoAPClient.common.messageformat.CoAPMessage;
import com.project.iotproject.CoAPClient.common.messageformat.CoAPMessageTypes;
import com.project.iotproject.CoAPClient.common.messageformat.CoAPMethodCodes;
import com.project.iotproject.CoAPClient.common.messageformat.CoAPResponseCodes;
import com.project.iotproject.CoAPClient.common.messageformat.option.CoAPOptionsFormat;
import com.project.iotproject.CoAPClient.common.messageformat.option.CoAPOptions;
import com.project.iotproject.CoAPClient.common.messageformat.option.CoAPOptionsTypeResolver;
import com.project.iotproject.Util.MessageTransmissionUtil;
import com.project.iotproject.Util.ServerProperties;
import com.project.iotproject.Util.Util;

public class CoAPClient{
	String host;
	Integer port;
	int receiveSize;
	
	boolean runProgram = true;
	BufferedReader bufferedReader;
	CoAPMessage coAPMessage;

	//public static void main(String[] args) {
	//	final CoAPClientProgram _CoAPClientProgram = new CoAPClientProgram(ServerProperties.LOCAL_HOST, ServerProperties.LOCAL_PORT, ServerProperties.PACKAGE_LENGTH);
	//}

	public CoAPClient() {
		new CoAPClient(ServerProperties.LOCAL_HOST, ServerProperties.COAP_SERVER_PORT, ServerProperties.PACKAGE_LENGTH);
	}

	public CoAPClient(String host, Integer port, int receiveSize) {
		this.host = host;
		this.port = port;
		this.receiveSize = receiveSize;
		bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		defaultCoAPMessageParameters();
		runProgram();
	}

	protected void runProgram() {
		try {
			//coAPMessage = new CoAPMessage()
			defaultCoAPMessageParameters();
			terminalMainMenu();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void terminalMainMenu() {
		CoAPMessageHandler coAPMessageHandler = new CoAPMessageHandler();
		//clearTerminalWindow();
		while (runProgram) {
			System.out.println("\n"+"---------------------------------");
			System.out.println("|	MENU SELECTION		|");
			System.out.println("|	Selecion:		|");
			System.out.println("|	1. Manage Message	|");
			System.out.println("|	2. Preview Message	|");
			System.out.println("|	3. Send Message		|");
			System.out.println("|	4. Send and Receive	|");
			System.out.println("|	5. Reset Message	|");
			System.out.println("|	6. Exit			|");
			System.out.println("---------------------------------");
			try {
				Integer inputCode = Integer.parseInt(bufferedReader.readLine());
				switch (inputCode) {
					case 1:{
						//clearTerminalWindow();
						manageMessage();
						break;
					}
					case 2:{
						//clearTerminalWindow();
						Util.printCoAPMessage(coAPMessage);
						break;
					}
					case 3:{
						//clearTerminalWindow();
						byte [] coAPMessageBuffer = coAPMessageHandler.encodeMessage(coAPMessage);
						MessageTransmissionUtil.sendCoAPMessage(coAPMessageBuffer, host, port, _packetBuffer -> {
							Util.printTransmissionCoAPMessage(_packetBuffer, coAPMessage);
						});
						
						break;
					}
					case 4:{
						//clearTerminalWindow();
						byte [] coAPMessageBuffer = coAPMessageHandler.encodeMessage(coAPMessage);
						MessageTransmissionUtil.sendAndReceiveCoAPMessage(coAPMessageBuffer, host, port, receiveSize, _CoAPResponseBuffer -> {
							byte[] response = Arrays.copyOfRange(_CoAPResponseBuffer.getData(), 0, _CoAPResponseBuffer.getLength()); //Not optimal, just for visual purpose. _CoAPResponseBuffer shuold be byte[] type.
							CoAPMessage coAPResponse = coAPMessageHandler.decodeMessage(response);

							Util.printTransmissionCoAPMessage(coAPMessageBuffer, coAPMessage);
							Util.printTransmissionCoAPMessage(response, coAPResponse);
							//terminalMainMenu();
						});
						break;
					}
					case 5:{
						//clearTerminalWindow();
						resetCoAPMessage(coAPMessage);
						break;
					}
					case 6:{
						//clearTerminalWindow();
						runProgram = false;
					}
					default:{
						terminalMainMenu();
						//clearTerminalWindow();
					}
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void manageMessage() {
		//clearTerminalWindow();
		while (true) {
			try {
				System.out.print("CoAP Message Options:" + "\n");
				int i = 0;
				for(CoAPProgramOptions e : CoAPProgramOptions.values()) {
					System.out.println(i++ + ":" + e);
				}
				Integer inputCode = Integer.parseInt(bufferedReader.readLine());
				//clearTerminalWindow();
				switch (inputCode) {
					case 0:{
						//clearTerminalWindow();
						setCoAPVersion();
						break;
					}
					case 1:{
						//clearTerminalWindow();
						setCoAPType();
						break;
					}
					case 2:{
						//clearTerminalWindow();
						setCoAPCode();
						break;
					}
					case 3:{
						//clearTerminalWindow();
						setCoAPMessageID();
						break;
					}
					case 4:{
						//clearTerminalWindow();
						setCoAPToken();
						break;
					}
					case 5:{
						//clearTerminalWindow();
						CoAPOptionsFormat optionNumber = getOptionNumber();
						setCoAPOptionValue(optionNumber);
						break;
					}
					case 6:{
						//clearTerminalWindow();
						setCoAPPayload();
						break;
					}
					case 7:{
						terminalMainMenu();
					}
					default:
						//clearTerminalWindow();
				}
				//break;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private CoAPOptionsFormat getOptionNumber() {
		while (true) {
			try {
				CoAPOptionsFormat[] options = CoAPOptionsFormat.values();
				System.out.print("Set a Option, previous value: " + coAPMessage.getCoAPOptionsMap() + "\n");
				int i = 0;
				for(CoAPOptionsFormat e : CoAPOptionsFormat.values()) {
					System.out.println(i++ + ":" + e);
				}
				int index = Integer.parseInt(bufferedReader.readLine());
				return options[index];
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void resetCoAPMessage(CoAPMessage coapMessage) {
		while(true){
			try {
				//clearTerminalWindow();
				Util.printCoAPMessage(coapMessage);
				System.out.println("\n"+"Reset the all Messageparameters to Defualt? Y/N");
				String input = bufferedReader.readLine();
				if (input.equalsIgnoreCase("Y")) {
					/*
					Byte 1: (0101 0000) Coap version 1, NON, no token
					Byte 2: (0000 0001) GET
					Byte 3:	(1010 1010) Random msg id number part 1
					Byte 4: (0101 0101) Random msg id number part 2
					*/
					coAPMessage.setCoAPVersion(1)
							   .setCoAPType(CoAPMessageTypes.NON)
							   .setCoAPCode(CoAPCodes.GET)
							   .setCoAPMessageId(0xaa55); //10101010 01010101 == 0xAA55
					/*
					coAPMessage = new CoAPMessage()
								.setCoAPVersion(1)
								.setCoAPType(CoAPType.NON)
								.setCoAPCode(CoAPCodes.GET)
								.setCoAPMessageId(0xaa55); //10101010 01010101 == 0xAA55
					*/
					//clearTerminalWindow();
					System.out.println("Message Resetted!");
					pressAnyKeyToContinue();
					//clearTerminalWindow();
					break;
				}
				else if(input.equalsIgnoreCase("N")) {
					//clearTerminalWindow();
					System.out.println("Oki, returning to Main Menu...");
					pressAnyKeyToContinue();
					//clearTerminalWindow();
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	void defaultCoAPMessageParameters(){
		try {
			coAPMessage = new CoAPMessage()
				.setCoAPVersion(1)
				.setCoAPType(CoAPMessageTypes.NON)
				.setCoAPCode(CoAPCodes.GET)
				.setCoAPMessageId(0xaa55);//10101010 01010101 == 0xAA55
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	private void setCoAPVersion() {
		while (true) {
			try {
				System.out.print("\n" + "Set a Version Value, previous value: " + coAPMessage.getCoAPVersion() + "\n");
				coAPMessage.setCoAPVersion(Integer.parseInt(bufferedReader.readLine()));
				break;	//Break from while loop
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void setCoAPType() {
		while (true) {
			try {
				System.out.print("\n" + "Set a Type Value, previous value: " + coAPMessage.getCoAPType() + "\n");
				int i = 0;
				for(CoAPMessageTypes e : CoAPMessageTypes.values()) {
					System.out.println(i++ +":"+e);
				}
				coAPMessage.setCoAPType(CoAPMessageTypes.getValue(Integer.parseInt(bufferedReader.readLine()))); //sets a type value based on input key
				break;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void setCoAPCode() {
		while (true) {
			try {
				System.out.print("\n" + "CoAP Code Metod/Response:");
				System.out.print("\n" + "1: Method" + "\n" + "2: Response" + "\n");
				Integer inputCode = Integer.parseInt(bufferedReader.readLine());
				switch (inputCode) {
					case 1:{
						System.out.print("Set a Method Code Value, previous value: " + coAPMessage.getCoAPCode() + "\n");
						int i = 0;
						for(CoAPMethodCodes e : CoAPMethodCodes.values()) {
							System.out.println(i++ +":"+e);
						}
						coAPMessage.setCoAPCode(CoAPCodes.getValue(Integer.parseInt(bufferedReader.readLine()))); //sets a code value based on input key, ex sets empty
						//coAPMessage.setCoAPCode(CoAPCode.getValue(CoAPMethodCodes.class, Integer.parseInt(bufferedReader.readLine())));
						terminalMainMenu();
					}
					case 2:{
						System.out.print("Set a Response Code Value, previous value: " + coAPMessage.getCoAPCode() + "\n");
						int i = 0;
						for(CoAPResponseCodes e : CoAPResponseCodes.values()) {
							System.out.println(i++ +":"+e);
						}
						coAPMessage.setCoAPCode(CoAPCodes.getValue(Integer.parseInt(bufferedReader.readLine()))); //sets a code value based on input key, ex sets empty
						//coAPMessage.setCoAPCode(CoAPCode.getValue(CoAPResponseCodes.class, Integer.parseInt(bufferedReader.readLine())));
						terminalMainMenu();
					}
					default:
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void setCoAPMessageID(){
		while (true) {
			try {
				System.out.print("\n" + "Set a Message ID, previous value: " + coAPMessage.getCoAPMessageId() + "\n");
				coAPMessage.setCoAPMessageId(Integer.parseInt(bufferedReader.readLine()));
				break;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void setCoAPToken() {
		while (true) {
			try {
				System.out.print("\n" + "Set a Token Value, previous value: " + coAPMessage.getCoAPToken() + "\n");
				String input = bufferedReader.readLine();
				if (input != "null") {
					coAPMessage.setCoAPToken(input);
				}
				else{
					coAPMessage.setCoAPToken(null);
				}
				break;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	//TO-DO, fix if value exists.
	private void setCoAPOptionValue(CoAPOptionsFormat _CoAPOptionFormatName){
		while (true) {
			try {
				System.out.println("\n" + "Select Option Value for " + _CoAPOptionFormatName + ":");//_CoAPOptionFormatName==Uri_Path, Value
				byte[] optionValueInBytes = bufferedReader.readLine().getBytes(); //sets a code value based on input key, ex sets empty

				CoAPOptions<?> coAPOption = CoAPOptionsTypeResolver.getCoAPOption(_CoAPOptionFormatName, CoAPOptionsTypeResolver.getType(_CoAPOptionFormatName), optionValueInBytes);
				
				//Value is not repeatable and already exists and and map with values is not empty -> cannot be added
				if (!coAPOption.isRepeatable() && coAPMessage.getCoAPOptionsMap().containsKey(_CoAPOptionFormatName) && !coAPMessage.getCoAPOptionsMap().get(_CoAPOptionFormatName).isEmpty()) {
					System.out.println(_CoAPOptionFormatName + "is not repeatable, cannot be added more than once");
					//TODO Other options
				}
				else{
					coAPMessage.addCoAPOptions(coAPOption);
				}
				break;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void setCoAPPayload() {
		while (true) {
			try {
				System.out.print("\n" + "Set a Payload, previous value: " + coAPMessage.getCoAPPayload() + "\n");
				coAPMessage.setCoAPPayload(bufferedReader.readLine());
				break;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

    void pressAnyKeyToContinue() {
        System.out.println("Enter any Key to continue...");
        try{ bufferedReader.readLine(); }  
        catch(Exception e) {
			e.printStackTrace();
		}
	}
	
    void clearTerminalWindow() {
		System.out.print("\033[H\033[2J");
		//Runtime.getRuntime().exec("cls");
        System.out.flush();  
    }

}



