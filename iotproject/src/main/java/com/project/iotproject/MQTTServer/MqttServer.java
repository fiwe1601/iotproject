package com.project.iotproject.MQTTServer;

import com.project.iotproject.MQTTServer.common.MqttClient;
import com.project.iotproject.MQTTServer.common.MqttMessage;
import com.project.iotproject.MQTTServer.common.MqttMessageHandler;
import com.project.iotproject.MQTTServer.common.MqttQoS;
import com.project.iotproject.MQTTServer.common.MqttSubscriptionHandler.MqttSubscriptionHandler;
import com.project.iotproject.MQTTServer.common.controlpacket.MqttConnAck;
import com.project.iotproject.MQTTServer.common.controlpacket.MqttConnect;
import com.project.iotproject.MQTTServer.common.controlpacket.MqttPingResp;
import com.project.iotproject.MQTTServer.common.controlpacket.MqttPubAck;
import com.project.iotproject.MQTTServer.common.controlpacket.MqttPubComp;
import com.project.iotproject.MQTTServer.common.controlpacket.MqttPubRec;
import com.project.iotproject.MQTTServer.common.controlpacket.MqttPubRel;
import com.project.iotproject.MQTTServer.common.controlpacket.MqttPublish;
import com.project.iotproject.MQTTServer.common.controlpacket.MqttSubAck;
import com.project.iotproject.MQTTServer.common.controlpacket.MqttSubscribe;
import com.project.iotproject.MQTTServer.common.controlpacket.MqttUnsubAck;
import com.project.iotproject.MQTTServer.common.controlpacket.MqttUnsubscribe;
import com.project.iotproject.MQTTServer.common.listener.ClientConnectionListener;
import com.project.iotproject.MQTTServer.common.listener.ClientConnectMessageListener;
import com.project.iotproject.MQTTServer.common.listener.ServerListener;
import com.project.iotproject.MQTTServer.server.MqttClientHolder;
import com.project.iotproject.MQTTServer.server.MqttServerMessageReceiverHandler;
import com.project.iotproject.MQTTServer.server.ServerMessageReceiverHandler;
import com.project.iotproject.Util.DataTypesUtil;
import com.project.iotproject.Util.ServerProperties;
import com.project.iotproject.Util.timeCalculations;

public class MqttServer<T extends ServerMessageReceiverHandler<T, MqttMessage>> implements ClientConnectMessageListener<MqttMessage> {
    MqttClientHolder mqttClientHolder; /** Container with all the connected clients */
    MqttSubscriptionHandler mqttSubscriptionHandler; /** Handles Clients and there subscribed topics */

    MqttServerMessageReceiverHandler<MqttMessage> mqttMessageReceiverHandler;
    ServerListener<MqttMessage> serverListener;
    int totalConnectedClients = 0;
    timeCalculations mQTTBrokerTransmissonTime = new timeCalculations("mQTTBrokerTransmissonTime.txt", 50, true);

    T serverMessageReceiverHandler;

    public MqttServer(){
        mqttClientHolder = new MqttClientHolder(); // Holds Connected Clients To Server
        mqttSubscriptionHandler = new MqttSubscriptionHandler(mqttClientHolder); // Holds Connected Clients and there Different Subscriptions to Topics
        MqttMessageHandler _MQTTMessageHandler = new MqttMessageHandler(); // Encodes and decodes MQTT Messages'
        mqttMessageReceiverHandler = new MqttServerMessageReceiverHandler<>(_MQTTMessageHandler); // Listener for both CoAP and MQTT Protocols
        mqttMessageReceiverHandler.setClientConnectListener(this).setPacketLength(ServerProperties.PACKAGE_LENGTH).setPort(ServerProperties.MQTT_SERVER_PORT); // Listener for both CoAP and MQTT Protocols
        startServer();
    }

    public MqttServer(T serverMessageReceiverHandler){
        try {
            this.serverMessageReceiverHandler = serverMessageReceiverHandler;

            mqttClientHolder = new MqttClientHolder(); // Holds Connected Clients To Server
            mqttSubscriptionHandler = new MqttSubscriptionHandler(mqttClientHolder); // Holds Connected Clients and there Different Subscriptions to Topics
            //MqttMessageHandler _MQTTMessageHandler = new MqttMessageHandler(); // Encodes and decodes MQTT Messages
            //mqttMessageReceiverHandler = new MqttServerMessageReceiverHandler<>(_MQTTMessageHandler); // Listener for both CoAP and MQTT Protocols
            serverMessageReceiverHandler.setClientConnectListener(this).setPacketLength(ServerProperties.PACKAGE_LENGTH).setPort(ServerProperties.MQTT_SERVER_PORT); // Listener for both CoAP and MQTT Protocols
            startServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void startServer(){
        this.mqttMessageReceiverHandler.start(); // Starting in own Thread 
    }

    public void stopServer(){
        this.mqttMessageReceiverHandler.stopServer(); // Starting in own Thread 
    }

    @Override
    public void onClientMessageConnect(ClientConnectionListener<MqttMessage> _Connection) {
        MqttMessageHandler _MQTTMessageHandler = new MqttMessageHandler();
        int clientIndex = incrementandGetConnectedClients();    //start at 1, next client ++prevIndex

        _Connection.receivePacket(request -> {

            //MqttMessage request = _MQTTMessageHandler.decodeMessage(requestByteArray);

            if (this.serverListener != null) {
                this.serverListener.onMessageReceived(request);
            }

            try { 
                System.out.println("\nPacket Received: \n" + request);
                DataTypesUtil.printBytesAsString(_MQTTMessageHandler.encodeMessage(request));
        
                MqttMessage responseMessage = new MqttMessage();
                switch (request.getMqttControlPacketType()) {
                    
                    case CONNECT: {
                        MqttConnect mqttConnectRequestPacket = ((MqttConnect) request.getMqttPacket());
    
                        boolean isCleanSession = mqttConnectRequestPacket.getConnectFlag().isCleanSession();
                        MqttConnAck mqttResponsePacket;

                        mqttResponsePacket = isCleanSession 
                        ? new MqttConnAck().setMqttReturnCode(MqttConnAck.MqttConnAckReturnCode.CONNECTION_ACCEPTED).setMqttSessionPresent(false)
                        : ((mqttClientHolder.getClientIdMap().containsValue((mqttConnectRequestPacket.getClientId()))) 
                            ? new MqttConnAck().setMqttReturnCode(MqttConnAck.MqttConnAckReturnCode.CONNECTION_ACCEPTED).setMqttSessionPresent(true) 
                            : new MqttConnAck().setMqttReturnCode(MqttConnAck.MqttConnAckReturnCode.CONNECTION_ACCEPTED).setMqttSessionPresent(false));

                        //CONNACK packet containing a non-zero return code it MUST set Session Present to 0
                        if (mqttResponsePacket.getMqttConnAckReturnCode().getKey() != 0) {
                            mqttResponsePacket.setMqttSessionPresent(false);
                        }

                        mqttClientHolder.addClient(clientIndex, new MqttClient(mqttConnectRequestPacket.getClientId(), _Connection));
                        responseMessage.setMqttPacket(mqttResponsePacket); //MqttMessage Packet
                        //responseMessage.setMqttPacket(getConnAckPacket(request));
                        break;
                    }
                    
                    case CONNACK:{
                        //No response Message
                        break;
                    }

                    case PUBLISH: {
                        // Send PUBLISH to subscribers
                        MqttPublish mqttPublishRequestPacket = ((MqttPublish) request.getMqttPacket());
                        if(mqttPublishRequestPacket.getPayload().equals("sensor")){
                            request.setPayload("sensor".getBytes());
                        }
                        
                        // Response - None, QOS = 0
                        if (mqttPublishRequestPacket.getQoSLevel() == MqttQoS.ATMOSTONCE) {
                            //No Response Message
                        }
                        // Response - PUBACK Packet, QOS = 1
                        else if (mqttPublishRequestPacket.getQoSLevel() == MqttQoS.ATLEASTONCE) {
                            MqttPubAck mqttResponsePacket = new MqttPubAck().setPacketIdentifier(mqttPublishRequestPacket.getPacketIdentifier());
                            responseMessage.setMqttPacket(mqttResponsePacket);
                        } 
                        // Response - PUBREC Packet, QOS = 2
                        else if (mqttPublishRequestPacket.getQoSLevel() == MqttQoS.EXACTLYONCE) {
                            MqttPubRec mqttResponsePacket = new MqttPubRec().setPacketIdentifier(mqttPublishRequestPacket.getPacketIdentifier());
                            responseMessage.setMqttPacket(mqttResponsePacket);
                        }

                        // Get Clients subscribed to a topic, Send message to all subscribers with the corresponding Topic, publish. Arraylist of subscribed clients.
                        var topicName = mqttPublishRequestPacket.getTopicName();
                        mQTTBrokerTransmissonTime.startTimer();
                        mqttSubscriptionHandler.getSubscribedClients(topicName).forEach(subscribedClient -> { 
                            System.out.println("subscribedClient: " + subscribedClient);
                            subscribedClient.sendMessage(request); 
                        });
                        mQTTBrokerTransmissonTime.stopTimer();
                        break;
                    }

                    case PUBACK:{
                        //No response Message
                        break;
                    } 
                    
                    case PUBREC:{
                        MqttPubRec mqttPublishRecRequestPacket = ((MqttPubRec) request.getMqttPacket());
                        MqttPubRel mqttResponsePacket = new MqttPubRel().setPacketIdentifier(mqttPublishRecRequestPacket.getPacketIdentifier());
                        responseMessage.setMqttPacket(mqttResponsePacket);
                        break;
                    } 

                    case PUBREL:{
                        MqttPubRel mqttPublishRelRequestPacket = ((MqttPubRel) request.getMqttPacket());
                        MqttPubComp mqttResponsePacket = new MqttPubComp().setPacketIdentifier(mqttPublishRelRequestPacket.getPacketIdentifier());
                        responseMessage.setMqttPacket(mqttResponsePacket);
                        break;
                    } 

                    case PUBCOMP:{
                        //No response Message
                        break;
                    } 

                    case SUBCRIBE: {
                        MqttSubscribe mqttSubscribeRequestPacket = ((MqttSubscribe) request.getMqttPacket());
                        MqttSubAck mqttResponsePacket = new MqttSubAck().setPacketIdentifier(mqttSubscribeRequestPacket.getPacketIdentifier());

                        responseMessage.setMqttPacket(mqttResponsePacket);   //MqttMessage Packet
                        mqttSubscriptionHandler.addTopic(mqttSubscribeRequestPacket.getSubscriptionList(), clientIndex);

                        break;
                    }

                    case SUBACK:{
                        //No response Message
                        break;
                    }

                    case UNSUBSCRIBE: {
                        MqttUnsubscribe mqttUnsubscribeRequestPacket = ((MqttUnsubscribe) request.getMqttPacket());
                        MqttUnsubAck mqttResponsePacket = new MqttUnsubAck().setPacketIdentifier(mqttUnsubscribeRequestPacket.getPacketIdentifier());
                        
                        responseMessage.setMqttPacket(mqttResponsePacket);
                        mqttSubscriptionHandler.removeTopic(mqttUnsubscribeRequestPacket.getTopicsList(), clientIndex);

                        break;
                    }

                    case UNSUBACK: {
                        //No response Message
                        break;
                    }

                    case PINGREQ: {
                        MqttPingResp mqttPintReqRequestPacket = new MqttPingResp();
                        responseMessage.setMqttPacket(mqttPintReqRequestPacket);
                        break;
                    }

                    case PINGRESP:{
                        //No response Message
                        break;
                    }

                    case DISCONNECT: {
                        mqttSubscriptionHandler.removeClient(clientIndex);
                        mqttClientHolder.removeClient(clientIndex);
                        break;
                    }
                    
                    default: {
                        throw new Exception(request.getMqttControlPacketType() + "is not a supported ControlPacketType.");
                    }
                }
				
                // Send the responseMessage packet to client
                if(responseMessage.getMqttPacket() != null) { 
                    
                    System.out.println("\nSending Response Message: \n" + responseMessage);
                    if (this.serverListener != null) {
                        this.serverListener.onMessageSent(responseMessage);
                    }
                    try {
                        DataTypesUtil.printBytesAsString(_MQTTMessageHandler.encodeMessage(responseMessage));
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
					
                    _Connection.sendMessage(responseMessage);
                }

            } catch (Exception e) {
                _Connection.clientConnectionClose();
            }
        });
    }


    private int incrementandGetConnectedClients() {
        return ++this.totalConnectedClients;
    }

    private int getTotalConnectedClients(){
        return this.totalConnectedClients;
    }


    public MqttConnAck getConnAckPacket(MqttMessage request){
        MqttConnect mqttConnectRequestPacket = ((MqttConnect) request.getMqttPacket());
    
        boolean isCleanSession = mqttConnectRequestPacket.getConnectFlag().isCleanSession();
        MqttConnAck mqttResponsePacket;

        mqttResponsePacket = isCleanSession 
        ? new MqttConnAck().setMqttReturnCode(MqttConnAck.MqttConnAckReturnCode.CONNECTION_ACCEPTED).setMqttSessionPresent(false)
        : ((mqttClientHolder.getClientIdMap().containsValue((mqttConnectRequestPacket.getClientId()))) 
            ? new MqttConnAck().setMqttReturnCode(MqttConnAck.MqttConnAckReturnCode.CONNECTION_ACCEPTED).setMqttSessionPresent(true) 
            : new MqttConnAck().setMqttReturnCode(MqttConnAck.MqttConnAckReturnCode.CONNECTION_ACCEPTED).setMqttSessionPresent(false));

        //CONNACK packet containing a non-zero return code it MUST set Session Present to 0
        if (mqttResponsePacket.getMqttConnAckReturnCode().getKey() != 0) {
            mqttResponsePacket.setMqttSessionPresent(false);
        }

        return mqttResponsePacket;
    }





    /*
    HashMap<String, ArrayList<MqttClient>> subscriptionTopicList = new HashMap<String, ArrayList<MqttClient>>();

    public MqttServer(){

    }

    public void addSubscriptionTopic(ArrayList<MqttClient> subscriptionTopicList, Integer clientId){
        subscriptionTopicList.forEach(subscriptionTopicLists -> { addSubscriptionTopic(subscriptionTopicLists, clientId); });
    }

    public void addSubscriptionTopic(MqttSubscription subscription, Integer ClientId){

    }
    
    public void removeSubscriptionTopic(){
        
    }

    public void getClients(){
        
    }

    public void removeUser(){
        
    }
    */
}
