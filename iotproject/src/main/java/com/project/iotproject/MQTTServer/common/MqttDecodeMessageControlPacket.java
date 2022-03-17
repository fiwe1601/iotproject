package com.project.iotproject.MQTTServer.common;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import com.project.iotproject.MQTTServer.common.controlpacket.MqttConnAck;
import com.project.iotproject.MQTTServer.common.controlpacket.MqttConnect;
import com.project.iotproject.MQTTServer.common.controlpacket.MqttDisconnect;
import com.project.iotproject.MQTTServer.common.controlpacket.MqttPingReq;
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
import com.project.iotproject.MQTTServer.common.controlpacket.MqttConnAck.MqttConnAckReturnCode;
import com.project.iotproject.Util.DataTypesUtil;

public class MqttDecodeMessageControlPacket {

    public MqttConnect decodeConnectMessagePacket(ByteArrayInputStream byteArrayInputStream, Integer remainingLength) throws Exception {
        
        MqttConnect connectPacket = new MqttConnect();

        //Length of Protocol Name = Read first 2 bytes
        var protocolNameLength = DataTypesUtil.getTwoBytesToInt(byteArrayInputStream);
        byte[] variableHeader = byteArrayInputStream.readNBytes(protocolNameLength);
        connectPacket.setProtocolName(new String(variableHeader));
        remainingLength -= (2 + protocolNameLength);

        connectPacket.setProtocolLevel(byteArrayInputStream.read());

        int connectflag = byteArrayInputStream.read();
        connectPacket.setConnectFlag(new MqttConnectFlags().setMqttConnectFlag(connectflag));

        Integer keepAlive = DataTypesUtil.byteArrayToInteger1(byteArrayInputStream.readNBytes(2));
        //Integer keepAlive1 = DataTypesUtil.getTwoBytesToInt(byteArrayInputStream);
        connectPacket.setKeepAlive(keepAlive);
        remainingLength -= (4);

        // If CleanSession is set to 0, the Server MUST resume communications with the Client based on state from 
        // the current Session (as identified by the Client identifier). If there is no Session associated with the Client
        // identifier the Server MUST create a new Session. 
        // If CleanSession is set to 1, the Client and Server MUST discard any previous Session and start a new one. 
        if (connectPacket.getConnectFlag().isCleanSession()) {
            var clientIdLength = DataTypesUtil.getTwoBytesToInt(byteArrayInputStream);
            connectPacket.setClientId(DataTypesUtil.byteArrayToString(byteArrayInputStream.readNBytes(clientIdLength)));
            remainingLength -= (2 + clientIdLength);
        }

        // If the Will Flag is set to 1, the Will Topic is the next field in the payload.
        // If the Will Flag is set to 1 the Will Message is the next field in the payload. 
        // The Will Message defines the Application Message that is to be published to the Will Topic.
        if (connectPacket.getConnectFlag().isWillFlag()) {
            var willTopicLength = DataTypesUtil.getTwoBytesToInt(byteArrayInputStream);
            connectPacket.setWillTopic(DataTypesUtil.byteArrayToString(byteArrayInputStream.readNBytes(willTopicLength)));

            var willMessageLength = DataTypesUtil.getTwoBytesToInt(byteArrayInputStream);
            connectPacket.setWillMessage(DataTypesUtil.byteArrayToString(byteArrayInputStream.readNBytes(willMessageLength)));
            remainingLength -= (4 + willTopicLength + willMessageLength);
        }

        //If the User Name Flag is set to 1, this is the next field in the payload
        if (connectPacket.getConnectFlag().isUserNameFlag()) {
            var userNameLength = DataTypesUtil.getTwoBytesToInt(byteArrayInputStream);
            connectPacket.setUserName(DataTypesUtil.byteArrayToString(byteArrayInputStream.readNBytes(userNameLength)));
            remainingLength -= (2 + userNameLength);
        }
        
        //If the Password Flag is set to 1, this is the next field in the payload.
        if (connectPacket.getConnectFlag().isPasswordFlag()) {
            var passwordLength = DataTypesUtil.getTwoBytesToInt(byteArrayInputStream);
            connectPacket.setPassword(DataTypesUtil.byteArrayToString(byteArrayInputStream.readNBytes(passwordLength)));
            remainingLength -= (2 + passwordLength);
        }
        if(remainingLength > 0){
            throw new Exception("More Bytes to read from stream! RemainingLength: " + remainingLength);
        }

        return connectPacket;
    }

    public MqttPublish decodePublishMessagePacket(ByteArrayInputStream byteArrayInputStream, Integer remainingLength, int streamFirstByte) throws Exception {

        //Set Fixed Header Methods
        MqttPublish publishPacket = new MqttPublish()
                .setDUPFlag(((streamFirstByte & 0x08) >> 3) == 1)
                .setQoSLevel(MqttQoS.getValue((streamFirstByte & 0x06) >> 1))
                .setRetain((streamFirstByte & 1) == 1);

        System.out.println("RemainingLength: " + remainingLength);

        var topicLength = DataTypesUtil.getTwoBytesToInt(byteArrayInputStream);
        publishPacket.setTopicName(DataTypesUtil.byteArrayToString(byteArrayInputStream.readNBytes(topicLength)));
        remainingLength  -= (topicLength + 2); //2 bytes for length

        //The Packet Identifier field is only present in PUBLISH Packets where the QoS level is 1 or 2.
        if(publishPacket.getQoSLevel() == MqttQoS.ATLEASTONCE || publishPacket.getQoSLevel() == MqttQoS.EXACTLYONCE) {
            publishPacket.setPacketIdentifier(DataTypesUtil.getTwoBytesToInt(byteArrayInputStream));
            remainingLength -= (2);
        }
        
        if(remainingLength > 0) {
            publishPacket.setPayload(DataTypesUtil.byteArrayToString(byteArrayInputStream.readNBytes(remainingLength)));
        }
        
        return publishPacket;
    }

    public MqttSubscribe decodeSubscribeMessagePacket(ByteArrayInputStream byteArrayInputStream, Integer remainingLength) throws Exception {

        MqttSubscribe subscribePacket = new MqttSubscribe();

        // The variable header contains a Packet Identifier.
        subscribePacket.setPacketIdentifier(DataTypesUtil.getTwoBytesToInt(byteArrayInputStream));
        remainingLength -= (2);

        // Can be multiple Topic Subscriptions
        
        while ( remainingLength > 0 ) {

            // The payload of a SUBSCRIBE packet MUST contain at least one Topic Filter / QoS pair.
            // A SUBSCRIBE packet with no payload is a protocol violation.
            if(remainingLength == 2){
                throw new Exception("Protocol Violation! Only Two Bytes Left!");
            }

            //The payload of a SUBSCRIBE Packet contains a list of Topic Filters indicating the Topics to which the Client wants to subscribe.
            var topicFilterLength = DataTypesUtil.getTwoBytesToInt(byteArrayInputStream);//Length MSB, LSB
            var topicFilter = DataTypesUtil.byteArrayToString(byteArrayInputStream.readNBytes(topicFilterLength));

            MqttQoS requestedQoS = MqttQoS.getValue(byteArrayInputStream.read()); //2 First bits
            remainingLength -= (topicFilterLength + 3);    //3 = QOS + MSB + LSB
            subscribePacket.addSubscription(new MqttSubscription(topicFilter, requestedQoS));
        }

        return subscribePacket;
    }

    public MqttUnsubscribe decodeUnsubscribeMessagePacket(ByteArrayInputStream byteArrayInputStream, Integer remainingLength) throws Exception {

        MqttUnsubscribe unsubscribePacket = new MqttUnsubscribe();

        unsubscribePacket.setPacketIdentifier(DataTypesUtil.getTwoBytesToInt(byteArrayInputStream));
        remainingLength -= (2);

        while ( remainingLength > 0 ) {

            // The Payload of an UNSUBSCRIBE packet MUST contain at least one Topic Filter.
            // An UNSUBSCRIBE packet with no payload is a protocol violation.
            if(remainingLength == 2){
                throw new Exception("Protocol Violation! Only Two Bytes Left!");
            }

            var topicFilterLength = DataTypesUtil.getTwoBytesToInt(byteArrayInputStream);//Length MSB, LSB
            var topicFilter = DataTypesUtil.byteArrayToString(byteArrayInputStream.readNBytes(topicFilterLength));

            remainingLength -= (topicFilterLength + 2);    //2 = MSB + LSB
            unsubscribePacket.addTopicsList(topicFilter);
        }

        return unsubscribePacket;
    }

    public MqttConnAck decodeConnAckMessagePacket(ByteArrayInputStream byteArrayInputStream, Integer remainingLength) throws Exception {

        MqttConnAck connAckPacket = new MqttConnAck();

        connAckPacket.setMqttSessionPresent(byteArrayInputStream.read() == 1 ? true : false);
        connAckPacket.setMqttReturnCode(MqttConnAckReturnCode.getValue(byteArrayInputStream.read()));
        remainingLength -= (2);

        if ( !(remainingLength == 0) ) {
            throw new Exception("Protocol Violation! More Bytes left");
        }

        return connAckPacket;
    }

    public MqttPubAck decodePubAckMessagePacket(ByteArrayInputStream byteArrayInputStream, Integer remainingLength) throws Exception {

        MqttPubAck pubAckPacket = new MqttPubAck();

        pubAckPacket.setPacketIdentifier(DataTypesUtil.getTwoBytesToInt(byteArrayInputStream));
        remainingLength -= (2);

        if ( !(remainingLength == 0) ) {
            throw new Exception("Protocol Violation! More Bytes left");
        }

        return pubAckPacket;
    }

    public MqttPubRec decodePubRecMessagePacket(ByteArrayInputStream byteArrayInputStream, Integer remainingLength) throws Exception {

        MqttPubRec pubRecPacket = new MqttPubRec();

        pubRecPacket.setPacketIdentifier(DataTypesUtil.getTwoBytesToInt(byteArrayInputStream));
        remainingLength -= (2);

        if ( !(remainingLength == 0) ) {
            throw new Exception("Protocol Violation! More Bytes left");
        }

        return pubRecPacket;
    }

    public MqttPubRel decodePubRelMessagePacket(ByteArrayInputStream byteArrayInputStream, Integer remainingLength) throws Exception {

        MqttPubRel pubRelPacket = new MqttPubRel();

        pubRelPacket.setPacketIdentifier(DataTypesUtil.getTwoBytesToInt(byteArrayInputStream));
        remainingLength -= (2);

        if ( !(remainingLength == 0) ) {
            throw new Exception("Protocol Violation! More Bytes left");
        }

        return pubRelPacket;
    }

    public MqttPubComp decodePubCompMessagePacket(ByteArrayInputStream byteArrayInputStream, Integer remainingLength) throws Exception {

        MqttPubComp pubCompPacket = new MqttPubComp();

        pubCompPacket.setPacketIdentifier(DataTypesUtil.getTwoBytesToInt(byteArrayInputStream));
        remainingLength -= (2);

        if ( !(remainingLength == 0) ) {
            throw new Exception("Protocol Violation! More Bytes left");
        }

        return pubCompPacket;
    }

    public MqttSubAck decodeSubAckMessagePacket(ByteArrayInputStream byteArrayInputStream, Integer remainingLength) throws Exception {

        MqttSubAck subAckPacket = new MqttSubAck();

        subAckPacket.setPacketIdentifier(DataTypesUtil.getTwoBytesToInt(byteArrayInputStream));
        remainingLength -= (2);
        ArrayList<MqttQoS> arrList = new ArrayList<>();

        while ( !(remainingLength == 0) ) {
            arrList.add(MqttQoS.getValue(byteArrayInputStream.read()));
        }
        subAckPacket.setQoSList(arrList);

        return subAckPacket;
    }

    public MqttUnsubAck decodeUnsubAckMessagePacket(ByteArrayInputStream byteArrayInputStream, Integer remainingLength) throws Exception {

        MqttUnsubAck UnsubAckPacket = new MqttUnsubAck();

        UnsubAckPacket.setPacketIdentifier(DataTypesUtil.getTwoBytesToInt(byteArrayInputStream));
        remainingLength -= (2);

        if ( !(remainingLength == 0) ) {
            throw new Exception("Protocol Violation! More Bytes left");
        }

        return UnsubAckPacket;
    }

    public MqttPingReq decodePingReqMessagePacket(ByteArrayInputStream byteArrayInputStream, Integer remainingLength) throws Exception {

        MqttPingReq UnPingReqPacket = new MqttPingReq();

        return UnPingReqPacket;
    }

    public MqttPingResp decodePingRespMessagePacket(ByteArrayInputStream byteArrayInputStream, Integer remainingLength) throws Exception {

        MqttPingResp UnPingReqPacket = new MqttPingResp();

        return UnPingReqPacket;
    }

    public MqttDisconnect decodeDisconnectMessagePacket(ByteArrayInputStream byteArrayInputStream, Integer remainingLength) throws Exception {

        MqttDisconnect UnPingReqPacket = new MqttDisconnect();

        return UnPingReqPacket;
    }

}
