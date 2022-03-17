package com.project.iotproject.MQTTServer.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.project.iotproject.common.MessageParser;
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

public class MqttMessageHandler implements MessageParser<MqttMessage> {
    
    //Message endoced ready to be sent.
    @Override
    public byte[] encodeMessage(MqttMessage mqttMessage) {
        byte[] messageBuffer = null;
        try (var byteArrayOutputStream = new ByteArrayOutputStream()){

            int messageFixedHeader = mqttMessage.getMqttPacket().getFixedHeader();

            byteArrayOutputStream.write(messageFixedHeader & 0xff);  //Write first Byte to Outputstreamarray
            
            byte[] remainingMessageBuffer = getRemainingMessage(mqttMessage); //Dont write anything

            if(remainingMessageBuffer != null){
                mqttMessage.setRemainingLength(remainingMessageBuffer.length);
                byteArrayOutputStream.write(mqttMessage.getRemainingLength());
                byteArrayOutputStream.write(remainingMessageBuffer);
            }
            else{
                byteArrayOutputStream.write(0);
            }
            messageBuffer = byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            System.out.println("Not able to encode message with Type: " + mqttMessage.getMqttControlPacketType());
            e.printStackTrace();
        }
        return messageBuffer;
    }

    private byte[] getRemainingMessage(MqttMessage mqttMessage) {
        MqttEncodeMessageControlPacket mqttEncodeMessageControlPacket = new MqttEncodeMessageControlPacket();
        switch(mqttMessage.getMqttControlPacketType()) {
            case CONNECT: { return mqttEncodeMessageControlPacket.encodeMessagePacket((MqttConnect) mqttMessage.getMqttPacket()); }
            case CONNACK: { return mqttEncodeMessageControlPacket.encodeMessagePacket((MqttConnAck) mqttMessage.getMqttPacket()); }
            case PUBLISH: { return mqttEncodeMessageControlPacket.encodeMessagePacket((MqttPublish) mqttMessage.getMqttPacket()); }
            case PUBACK: { return mqttEncodeMessageControlPacket.encodeMessagePacket((MqttPubAck) mqttMessage.getMqttPacket()); }
            case PUBREC: { return mqttEncodeMessageControlPacket.encodeMessagePacket((MqttPubRec) mqttMessage.getMqttPacket()); }
            case PUBREL: { return mqttEncodeMessageControlPacket.encodeMessagePacket((MqttPubRel) mqttMessage.getMqttPacket()); }
            case PUBCOMP: { return mqttEncodeMessageControlPacket.encodeMessagePacket((MqttPubComp) mqttMessage.getMqttPacket()); }
            case SUBCRIBE: { return mqttEncodeMessageControlPacket.encodeMessagePacket((MqttSubscribe) mqttMessage.getMqttPacket()); }
            case SUBACK: { return mqttEncodeMessageControlPacket.encodeMessagePacket((MqttSubAck) mqttMessage.getMqttPacket()); }
            case UNSUBSCRIBE: { return mqttEncodeMessageControlPacket.encodeMessagePacket((MqttUnsubscribe) mqttMessage.getMqttPacket()); }
            case UNSUBACK: { return mqttEncodeMessageControlPacket.encodeMessagePacket((MqttUnsubAck) mqttMessage.getMqttPacket()); }

            //No variable header
            case PINGREQ:
            case PINGRESP: 
            case DISCONNECT: { return null; }
            default: {
                System.out.println("Encoder doesnt support packet: " + mqttMessage.getMqttControlPacketType() + " yet.");
                break;
            }
        }
        return null;
    }

    @Override
    public MqttMessage decodeMessage(byte[] messageBuffer) {
        MqttMessage mqttMessage = new MqttMessage();
 
        try (var byteArrayInputStream = new ByteArrayInputStream(messageBuffer)){

            //Fixed Header
            var streamFirstByte = byteArrayInputStream.read();
            MqttControlPacketType mqttControlPacketType = MqttControlPacketType.getValue((streamFirstByte & 0xF0) >> 0x04);

            // Set Remaining Length
            Integer remainingLength = byteArrayInputStream.read(); // starts at byte 2. 1-4 bytes.
            mqttMessage.setRemainingLength(remainingLength);

            MqttPersistableControlPacket mqttPacket = decodeMqttPacket(byteArrayInputStream, mqttControlPacketType, remainingLength, streamFirstByte);

            mqttMessage.setMqttPacket(mqttPacket);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return mqttMessage;
    }

    private MqttPersistableControlPacket decodeMqttPacket(
        ByteArrayInputStream byteArrayInputStream, 
        MqttControlPacketType mqttControlPacketType,
        Integer remainingLength,
        int streamFirstByte
    ) throws Exception {
        MqttDecodeMessageControlPacket mqttDecodeMessageControlPacket = new MqttDecodeMessageControlPacket();
        switch(mqttControlPacketType) {
            case CONNECT: { return mqttDecodeMessageControlPacket.decodeConnectMessagePacket(byteArrayInputStream, remainingLength); }
            case CONNACK: { return (new MqttConnAck()); }
            case PUBLISH: { return mqttDecodeMessageControlPacket.decodePublishMessagePacket(byteArrayInputStream, remainingLength, streamFirstByte); }
            case PUBACK: { return (new MqttPubAck()); }
            case PUBREC: { return (new MqttPubRec()); }
            case PUBREL: { return (new MqttPubRel()); }
            case PUBCOMP: { return (new MqttPubComp()); }
            case SUBCRIBE: { return mqttDecodeMessageControlPacket.decodeSubscribeMessagePacket(byteArrayInputStream, remainingLength); }
            case SUBACK: { return (new MqttSubAck()); }
            case UNSUBSCRIBE: { return mqttDecodeMessageControlPacket.decodeUnsubscribeMessagePacket(byteArrayInputStream, remainingLength); }
            case UNSUBACK: { return (new MqttUnsubAck()); }
            case PINGREQ:{ return (new MqttPingReq()); }
            case PINGRESP: { return (new MqttPingResp()); }
            case DISCONNECT: { return (new MqttDisconnect()); }
            default: {
                return null;
            }
        }
    }


    /**
    private MqttPersistableControlPacket decodeMqttPacket1(
        ByteArrayInputStream byteArrayInputStream, 
        MqttControlPacketType mqttControlPacketType,
        Integer remainingLength,
        int streamFirstByte
    ) throws Exception {
        MqttDecodeMessageControlPacket mqttDecodeMessageControlPacket = new MqttDecodeMessageControlPacket();
        switch(mqttControlPacketType) {
            case CONNECT: { return mqttDecodeMessageControlPacket.decodeConnectMessagePacket(byteArrayInputStream, remainingLength); }
            case CONNACK: { return (mqttDecodeMessageControlPacket.decodeConnAckMessagePacket(byteArrayInputStream, remainingLength)); }
            case PUBLISH: { return mqttDecodeMessageControlPacket.decodePublishMessagePacket(byteArrayInputStream, remainingLength, streamFirstByte); }
            case PUBACK: { return (mqttDecodeMessageControlPacket.decodePubAckMessagePacket(byteArrayInputStream, remainingLength)); }
            case PUBREC: { return (mqttDecodeMessageControlPacket.decodePubRecMessagePacket(byteArrayInputStream, remainingLength)); }
            case PUBREL: { return (mqttDecodeMessageControlPacket.decodePubRelMessagePacket(byteArrayInputStream, remainingLength)); }
            case PUBCOMP: { return (mqttDecodeMessageControlPacket.decodePubCompMessagePacket(byteArrayInputStream, remainingLength)); }
            case SUBCRIBE: { return mqttDecodeMessageControlPacket.decodeSubscribeMessagePacket(byteArrayInputStream, remainingLength); }
            case SUBACK: { return (mqttDecodeMessageControlPacket.decodeSubAckMessagePacket(byteArrayInputStream, remainingLength)); }
            case UNSUBSCRIBE: { return mqttDecodeMessageControlPacket.decodeUnsubscribeMessagePacket(byteArrayInputStream, remainingLength); }
            case UNSUBACK: { return (mqttDecodeMessageControlPacket.decodeUnsubAckMessagePacket(byteArrayInputStream, remainingLength)); }
            case PINGREQ:{ return (mqttDecodeMessageControlPacket.decodePingReqMessagePacket(byteArrayInputStream, remainingLength)); }
            case PINGRESP: { return (mqttDecodeMessageControlPacket.decodePingRespMessagePacket(byteArrayInputStream, remainingLength)); }
            case DISCONNECT: { return (mqttDecodeMessageControlPacket.decodeDisconnectMessagePacket(byteArrayInputStream, remainingLength)); }
            default: {
                return null;
            }
        }
    }
 */

}
