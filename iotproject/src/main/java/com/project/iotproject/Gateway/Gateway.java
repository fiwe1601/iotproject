package com.project.iotproject.Gateway;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import com.project.iotproject.CoAPClient.common.CoAPMessageHandler;
import com.project.iotproject.CoAPClient.common.messageformat.CoAPCodes;
import com.project.iotproject.CoAPClient.common.messageformat.CoAPMessage;
import com.project.iotproject.CoAPClient.common.messageformat.CoAPMessageTypes;
import com.project.iotproject.CoAPClient.common.messageformat.option.CoAPOptions;
import com.project.iotproject.CoAPClient.common.messageformat.option.CoAPOptionsFormat;
import com.project.iotproject.CoAPClient.common.messageformat.option.CoAPOptionsTypeResolver;
import com.project.iotproject.Util.MessageTransmissionUtil;
import com.project.iotproject.Util.ServerProperties;
import com.project.iotproject.Util.timeCalculations;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class Gateway extends Thread implements MqttCallback {

	protected MqttClient mqttClient;
    protected String brokerUrl = "tcp://"+ServerProperties.LOCAL_HOST+":"+ServerProperties.MQTT_SERVER_PORT;
	protected String clientIdString = "Sensor";
    protected boolean cleanSessionBoolean = true;
	protected int keepAlive = 60;
    ArrayList<String> stringOptionlist = new ArrayList<>(Arrays.asList("temp", "time", "rand", "message", "time"));
    ArrayList<String> stringPayloadlist = new ArrayList<>(Arrays.asList("Sent Payload 1", "Sent Payload 2", "Sent Payload 3", "Sent Payload 4", "Sent Payload 5", "Sent Payload 6"));

    CoAPOptions<?> coAPOption;

    CoAPMessageHandler coAPMessageHandler;
    CoAPMessage coAPMessage;

    timeCalculations coAPClientTransmissonTime = new timeCalculations("coAPClientTransmissonTime.txt", 50, true);
    timeCalculations mQTTClientTransmissonTime = new timeCalculations("mQTTClientTransmissonTime.txt", 50, true);

    public Gateway() throws Exception{
        /** MQTTClient */
        mqttClient = new MqttClient(brokerUrl, clientIdString, new MemoryPersistence());
        mqttClient.setCallback(this);
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setKeepAliveInterval(keepAlive);
        connOpts.setCleanSession(cleanSessionBoolean);

        try {
            mqttClient.connect(connOpts);
            System.out.println("MQTTClient Connected!");
        } 
        catch (MqttSecurityException e) { e.printStackTrace(); } 
        catch (MqttException e) { e.printStackTrace(); }

        for(String topic : stringOptionlist){
            mqttClient.subscribe(topic);
        }
        

        /** CoAPServer */
        coAPMessageHandler = new CoAPMessageHandler();
        setupStandardCoAPMessage();

        Runnable runnable = new Runnable() {
            public void run() {
                System.out.println("RUN");
                sendAndReceiveCoAPMessageGateway();
            }
        };
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(runnable, 0, 5, TimeUnit.SECONDS);

        this.start();
    }


    public void sendAndReceiveCoAPMessageGateway(){
        String optionValue = getOptionValue();
        System.out.println("optionValue: " + optionValue);
        setupStandardCoAPMessage();
        coAPMessage.setCoAPPayload(stringPayloadlist.get(ThreadLocalRandom.current().nextInt(0, 5)));

        try {
            coAPMessage.addCoAPOptions(CoAPOptionsTypeResolver.getCoAPOption(CoAPOptionsFormat.URI_PATH, CoAPOptionsTypeResolver.getType(CoAPOptionsFormat.URI_PATH), optionValue.getBytes()));

            String time = new Timestamp(System.currentTimeMillis()).toString();
            coAPClientTransmissonTime.startTimer();
            MessageTransmissionUtil.sendAndReceiveCoAPMessage(coAPMessageHandler.encodeMessage(coAPMessage), ServerProperties.LOCAL_HOST, ServerProperties.COAP_SERVER_PORT, ServerProperties.PACKAGE_LENGTH, _CoAPResponseBuffer -> {
                coAPClientTransmissonTime.stopTimer();
                CoAPMessage coAPResponse = coAPMessageHandler.decodeMessage(_CoAPResponseBuffer.getData());
                
                MqttMessage mqttMessage = new MqttMessage();
                mqttMessage.setQos(0);
                mqttMessage.setPayload((coAPResponse.getCoAPPayload() + "\n" + "Time: " + time).getBytes());
                try {
                    mQTTClientTransmissonTime.startTimer();
                    mqttClient.publish(optionValue, mqttMessage);
                    mQTTClientTransmissonTime.stopTimer();
                } catch (MqttException e) { e.printStackTrace(); }
            });
        } catch (Exception e1) { e1.printStackTrace(); }

    }
    
    public void setupStandardCoAPMessage(){
        try {
            coAPMessage = new CoAPMessage()
                .setCoAPVersion(1)
                .setCoAPType(CoAPMessageTypes.NON)
                .setCoAPCode(CoAPCodes.GET)
                .setCoAPMessageId(0xaa55);
        } catch (Exception e) { e.printStackTrace(); }
    }

    String getOptionValue(){
        return stringOptionlist.get(ThreadLocalRandom.current().nextInt(0, 5));
    }

    @Override
    public void run(){
        super.run();
    }

    @Override
    public void connectionLost(Throwable cause) {
        System.out.println("Connection to " + brokerUrl + " lost!" + cause);
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        // String time = new Timestamp(System.currentTimeMillis()).toString();
		// System.out.println("Time: " + time +
		// 	"    Topic:" + topic +
		// 	"    Message: " + new String(message.getPayload()) +
		// 	"    QoS: " + message.getQos() + 
		// 	"    Retained: " + message.isRetained() + 
		// 	"    id: " + message.getId());
        
        // CoAPMessage coAPMessageReq = new CoAPMessage()
        //     .setCoAPVersion(1)
        //     .setCoAPType(CoAPMessageTypes.NON)
        //     .setCoAPCode(CoAPCodes.GET)
        //     .setCoAPMessageId(0xaa55);

        // Thread thread = new Thread() {
        //     public void run() {
                
        //         String reqTime = new Timestamp(System.currentTimeMillis()).toString();
        //         try {
        //             coAPMessageReq.addCoAPOptions(CoAPOptionsTypeResolver.getCoAPOption(CoAPOptionsFormat.URI_PATH, CoAPOptionsTypeResolver.getType(CoAPOptionsFormat.URI_PATH), topic.getBytes()));
                    
        //             MessageTransmissionUtil.sendAndReceiveCoAPMessage(coAPMessageHandler.encodeMessage(coAPMessageReq), ServerProperties.LOCAL_HOST, ServerProperties.COAP_SERVER_PORT, ServerProperties.PACKAGE_LENGTH, _CoAPResponseBuffer -> {
        //                 CoAPMessage coAPResponse = coAPMessageHandler.decodeMessage(_CoAPResponseBuffer.getData());
      
        //                 MqttMessage reqMqttMessage = new MqttMessage();
        //                 reqMqttMessage.setQos(0);
        //                 reqMqttMessage.setPayload((coAPResponse.getCoAPPayload() + "\nTime: "  + reqTime).getBytes());
        //                 System.out.println(coAPResponse.getCoAPPayload() + "\nTime: " + reqTime + "\nTopic: " + topic);
      
        //                 try {
        //                     mqttClient.publish(topic, reqMqttMessage);              
        //                 } catch (MqttException e) { e.printStackTrace(); }
        //             });
        //         } catch (IOException e) {
        //             e.printStackTrace();
        //         } catch (Exception e1) {
        //             e1.printStackTrace();
        //         }

        //     }  
        // };
        // thread.start(); 

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        System.out.println("Delivery complete callback: Publish Completed "+ Arrays.toString(token.getTopics()));
    
    }
}
