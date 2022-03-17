package com.project.iotproject.MQTTClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.Arrays;

import com.project.iotproject.CoAPClient.CoAPClient;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MQTTClientApp implements MqttCallback {
	
	protected boolean runProgram;
	protected BufferedReader bufferedReader;
	protected MqttClient mqttClient;
	protected MqttConnectOptions connOpts;
	protected String brokerUrl;
	protected String clientId;
	protected String topic;
	protected String payload;
	protected boolean cleanSession;
	protected boolean retained;
	protected int keepAlive;
	protected int qos;

	//https://github.com/eclipse/paho.mqtt.java
	//https://www.eclipse.org/paho/index.php?page=clients/java/index.php
	public static void main(String[] args) {
		new MQTTClientApp();
	}

	public MQTTClientApp(){
        System.out.println("MQTTClient Start!");
		this.topic        	= "temp";
		this.payload      	= "MQTT Payload Message";
		this.qos            = 1;
		this.brokerUrl    	= "tcp://localhost:1883";
		this.clientId    	= "MQTTClientID";
		this.keepAlive 		= 60;
		this.retained 		= false;
		this.cleanSession	= true;
		this.runProgram = true;
		this.bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		try {
			terminalMainMenu();
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}

	public void setDefaultParameters(){
		this.topic        	= "temp";
		this.payload      	= "MQTT Payload Message";
		this.qos            = 1;
		this.brokerUrl    	= "tcp://localhost:1883";
		this.clientId    	= "MQTTClientID";
		this.keepAlive 		= 60;
		this.retained 		= false;
		this.cleanSession	= true;
	}

	private void manageMessage(){
		while (true) {
			try {
				int i = 1;
				System.out.print("MQTT Message Options:" + "\n");
				for(ManageMessageEnum e : ManageMessageEnum.values()) {
					System.out.println(i++ + ":" + e);
				}
				Integer inputCode = Integer.parseInt(bufferedReader.readLine());
				switch (inputCode) {
					case 1:
						System.out.print("\n" + "Set Topic, previous: " + this.topic + "\n");
						this.topic = bufferedReader.readLine();
						break;
					case 2:
						System.out.print("\n" + "Set Payload, previous: " + this.payload + "\n");
						this.payload = bufferedReader.readLine();
						break;
					case 3:
						System.out.print("\n" + "Set QoS(0-2), previous: " + this.qos + "\n");
						this.qos = Integer.parseInt(bufferedReader.readLine());
						break;
					case 4:
						System.out.print("\n" + "Set BrokerURL, previous: " + this.brokerUrl + "\n");
						this.brokerUrl = bufferedReader.readLine();
						break;
					case 5:
						System.out.print("\n" + "Set ClientID, previous: " + this.clientId + "\n");
						this.clientId = bufferedReader.readLine();
						break;
					case 6:
						System.out.print("\n" + "Set KeepAlive, previous: " + this.keepAlive + "\n");
						this.keepAlive = Integer.parseInt(bufferedReader.readLine());
						break;
					case 7:
						System.out.print("\n" + "Set Retained, previous: " + this.retained + "\n");
						this.retained = bufferedReader.readLine().equalsIgnoreCase("true") ? true : false;
						break;
					case 8:
						System.out.print("\n" + "Set Cleansession, previous: " + this.cleanSession + "\n");
						this.cleanSession = bufferedReader.readLine().equalsIgnoreCase("true") ? true : false;
						break;
					case 9:
						terminalMainMenu();
				
					default:
						break;
				}


			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	void view(){
		System.out.println("\nTopic:\t\t" + this.topic +
		"\nPayload:\t" + this.payload +
		"\nQoS:\t\t" + this.qos + 
		"\nBrokerURL:\t" + this.brokerUrl + 
		"\nClientID:\t" + this.clientId + 
		"\nKeepAlive:\t" + this.keepAlive + 
		"\nRetained:\t" + this.retained + 
		"\nCleanSession:\t" + this.cleanSession);
	}


	protected void terminalMainMenu() throws MqttException {
		while (runProgram){
			System.out.println("\n"+"---------------------------------");
			System.out.println("|    MENU SELECTION		|");
			System.out.println("|    Selecion:			|");
			System.out.println("|  1. Manage Message  		|");
			System.out.println("|  2. View 		 	|");
			System.out.println("|  3. Create/Connect to Broker  |");
			System.out.println("|  4. Disconnect from Broker	|");
			System.out.println("|  5. Subscribe to Broker	|");
			System.out.println("|  6. Publish to Broker		|");
			System.out.println("|  7. AUTO			|");
			System.out.println("|  8. Exit			|");
			System.out.println("---------------------------------");
			try {
				Integer inputCode = Integer.parseInt(bufferedReader.readLine());
				switch (inputCode) {
					case 1:
						clearTerminalWindow();				
						manageMessage();
						break;
					case 2:
						clearTerminalWindow();
						view();
						break;
					case 3:
						clearTerminalWindow();
						createAndConnectClient();
						break;
					case 4:
						clearTerminalWindow();
						clientDisconnect();
						break;
					case 5:
						clearTerminalWindow();
						subscribe(topic, qos);
						break;
					case 6:
						clearTerminalWindow();
						publish(topic, qos, payload, retained);
						break;
					case 7:
						clearTerminalWindow();
						auto();
						break;
					case 8:
						clearTerminalWindow();
						System.out.println("-EXITING-");
						waitForExit();
						runProgram = false;
						System.exit(1);
						break;
					default:
						terminalMainMenu();
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private void createAndConnectClient() {
		try {
			mqttClient = new MqttClient(this.brokerUrl, this.clientId, new MemoryPersistence());
			mqttClient.setCallback(this);

			MqttConnectOptions connOpts = new MqttConnectOptions();
			connOpts.setCleanSession(cleanSession);
			connOpts.setKeepAliveInterval(keepAlive);

			mqttClient.connect(connOpts);
			System.out.println("Connected to broker "+ brokerUrl + " with clientID " + mqttClient.getClientId());

		} catch (MqttException e) {
			e.printStackTrace();
			System.out.println("Unable to setup client: " + e.toString());
		}
	}

	public void clientDisconnect(){
		try {
			if(mqttClient.isConnected()){
				mqttClient.disconnect();
				mqttClient.close();
				System.out.println("Disconnected and Closed MQTTClient from MQTT Broker");
			}
			else {
				System.out.println("Cant disconnect, No Client found!");
			}
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}

	public void publish(String topic, int qos, String payload, boolean retained) throws MqttException {
    	String time = new Timestamp(System.currentTimeMillis()).toString();
    	System.out.println("Publishing at: " + time + " to topic \"" + topic + "\" qos " + qos);

   		MqttMessage message = new MqttMessage();
    	message.setQos(qos);
		message.setPayload(payload.getBytes());
		message.setRetained(retained);

    	mqttClient.publish(topic, message);
		System.out.println("Published with message: " + payload);
    }

    public void subscribe(String topicName, int qos) throws MqttException {
    	// Subscribe to the requested topic
    	// The QoS specified is the maximum level that messages will be sent to the client at.
    	// For instance if QoS 1 is specified, any messages originally published at QoS 2 will
    	// be downgraded to 1 when delivering to the client but messages published at 1 and 0
    	// will be received at the same level they were published at.
		System.out.println("Subscribing to topic \"" + topicName + "\" qos " + qos);
		mqttClient.subscribe(topicName, qos);
		System.out.println("Subscribed with message: " + payload);
    }

	public void auto(){
		clearTerminalWindow();
		System.out.println("AUTO");
		waitForExit();
	}

	void clearTerminalWindow() {
		System.out.print("\033[H\033[2J");
		//Runtime.getRuntime().exec("cls");
        System.out.flush();  
    }
	
	public void waitForExit(){
    	// Continue waiting for messages until the Enter is pressed
    	System.out.println("Press <Enter> to exit");
		try {
			System.in.read();
		} catch (IOException e) {
			//If we can't read we'll just exit
		}
	}

	void pressAnyKeyToContinue() {
        System.out.println("Enter any Key to continue...");
        try{ bufferedReader.readLine(); }  
        catch(Exception e) {
			e.printStackTrace();
		}
	}









	@Override
	public void connectionLost(Throwable cause) {
		// Called when the connection to the server has been lost.
		// An application may choose to implement reconnection
		// logic at this point. This sample simply exits.
		System.out.println("Connection to " + brokerUrl + " lost!" + cause);
	}


	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		// Called when a message arrives from the server that matches any
		// subscription made by the client
		String time = new Timestamp(System.currentTimeMillis()).toString();
		System.out.println("Time: " + time +
			"    Topic:" + topic +
			"    Message: " + new String(message.getPayload()) +
			"    QoS: " + message.getQos() + 
			"    Retained: " + message.isRetained() + 
			"    id: " + message.getId());



		//CoAPClient asd = new CoAPClient();
		//asd.sendCoAPMessage();
		
		new Thread() {
			@Override
			public void run(){
				try {
					publish(topic, qos, payload, retained);
				} catch (MqttException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}


	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		// Called when a message has been delivered to the
		// server. The token passed in here is the same one
		// that was returned from the original call to publish.
		// This allows applications to perform asynchronous
		// delivery without blocking until delivery completes.
		//
		// This sample demonstrates asynchronous deliver, registering
		// a callback to be notified on each call to publish.
		//
		// The deliveryComplete method will also be called if
		// the callback is set on the client
		//
		// note that token.getTopics() returns an array so we convert to a string
		// before printing it on the console
		System.out.println("Delivery complete callback: Publish Completed "+ Arrays.toString(token.getTopics()));
	}

}
