package com.project.iotproject.MQTTServer.common;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import com.project.iotproject.MQTTServer.common.controlpacket.MqttConnAck;
import com.project.iotproject.MQTTServer.common.controlpacket.MqttConnect;
import com.project.iotproject.MQTTServer.common.controlpacket.MqttPubAck;
import com.project.iotproject.MQTTServer.common.controlpacket.MqttPubComp;
import com.project.iotproject.MQTTServer.common.controlpacket.MqttPubRec;
import com.project.iotproject.MQTTServer.common.controlpacket.MqttPubRel;
import com.project.iotproject.MQTTServer.common.controlpacket.MqttPublish;
import com.project.iotproject.MQTTServer.common.controlpacket.MqttSubAck;
import com.project.iotproject.MQTTServer.common.controlpacket.MqttSubscribe;
import com.project.iotproject.MQTTServer.common.controlpacket.MqttUnsubAck;
import com.project.iotproject.MQTTServer.common.controlpacket.MqttUnsubscribe;
import com.project.iotproject.Util.DataTypesUtil;

/** VARIABLE HEADER ENCODING */
public class MqttEncodeMessageControlPacket {
    
    public byte[] encodeMessagePacket(MqttConnect mqttMessagePacket) {
        byte[] messageBuffer = null;
        try (var byteArrayOutputStream = new ByteArrayOutputStream()){
            //Protocol name
            DataTypesUtil.encodeString(byteArrayOutputStream, mqttMessagePacket.getProtocolName());

            //Protocol Level
            byteArrayOutputStream.write(MqttPersistableControlPacket.DEFAULT_PROTOCOL_VERSION); //Level(4)

            //Connect Flags TODO
            byteArrayOutputStream.write(mqttMessagePacket.getConnectFlag().setCleanSessionTrue().getMqttConnectFlag());
            //MqttConnectFlags mqttConnectFlags = new MqttConnectFlags().setCleanSession(true); //Remove
            //byteArrayOutputStream.write(mqttConnectFlags.getMqttConnectFlag()); //Remove
            
            //Keep Alive
            DataTypesUtil.writeTwoBytes(byteArrayOutputStream, mqttMessagePacket.getKeepAlive());

            /* PAYLOAD **/
            //Client Identifier
            if (mqttMessagePacket.getClientId() != null) {
                DataTypesUtil.encodeString(byteArrayOutputStream, mqttMessagePacket.getClientId());
            }

            //Will Topic & Will Message
            if (mqttMessagePacket.getWillTopic() != null && mqttMessagePacket.getConnectFlag().isWillFlag() == true && mqttMessagePacket.getClientId() != null) {
                DataTypesUtil.encodeString(byteArrayOutputStream, mqttMessagePacket.getWillTopic());
                if (mqttMessagePacket.getWillMessage() != null){
                    DataTypesUtil.encodeString(byteArrayOutputStream, mqttMessagePacket.getWillMessage());
                }
            }

            //User Name
            if (mqttMessagePacket.getUserName() != null && mqttMessagePacket.getConnectFlag().isUserNameFlag() == true && mqttMessagePacket.getClientId() != null) {
                DataTypesUtil.encodeString(byteArrayOutputStream, mqttMessagePacket.getUserName());
            }

            //Password
            if (mqttMessagePacket.getPassword() != null && mqttMessagePacket.getConnectFlag().isPasswordFlag() == true && mqttMessagePacket.getClientId() != null) {
                DataTypesUtil.encodeString(byteArrayOutputStream, mqttMessagePacket.getPassword());
            }

            messageBuffer = byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return messageBuffer;
    }

    public byte[] encodeMessagePacket(MqttConnAck mqttMessagePacket) {
        byte[] messageBuffer = null;
        try (var byteArrayOutputStream = new ByteArrayOutputStream()){

            //Connect Acknowledge Flags, true or false.
            byteArrayOutputStream.write(mqttMessagePacket.getMqttSessionPresent() ? 0x01 : 0x00);

            //Connect Return code
            byteArrayOutputStream.write(mqttMessagePacket.getMqttConnAckReturnCode().getKey());

            messageBuffer = byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return messageBuffer;
    }

    public byte[] encodeMessagePacket(MqttPublish mqttMessagePacket) {
        byte[] messageBuffer = null;
        try (var byteArrayOutputStream = new ByteArrayOutputStream()){

            var messageTopicName = mqttMessagePacket.getTopicName();

            // Topic name, Length & Data.
            DataTypesUtil.encodeString(byteArrayOutputStream, messageTopicName == null ? "" : messageTopicName);

            // Packet Identifier, QoS > 0 Control Packets MUST contain a non-zero 16-bit Packet Identifier
            if (mqttMessagePacket.getQoSLevel() == MqttQoS.ATLEASTONCE || mqttMessagePacket.getQoSLevel() == MqttQoS.EXACTLYONCE) {
                DataTypesUtil.writeTwoBytes(byteArrayOutputStream, mqttMessagePacket.getPacketIdentifier());
            }

            //TODO
            //byteArrayOutputStream.write(mqttMessagePacket.getPayload() != null ? mqttMessagePacket.getPayload().getBytes(StandardCharsets.UTF_8) : "".getBytes());
            if (mqttMessagePacket.getPayload() != null) {
                var stringLength = mqttMessagePacket.getPayload().getBytes(StandardCharsets.UTF_8).length;
                byteArrayOutputStream.write(mqttMessagePacket.getPayload().getBytes(StandardCharsets.UTF_8), 0, stringLength == 0 ? (stringLength+1) : stringLength); //Data
            }
            else{
                byteArrayOutputStream.write("".getBytes());
            }

            messageBuffer = byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return messageBuffer;
    }

    public byte[] encodeMessagePacket(MqttPubAck mqttMessagePacket) {
        byte[] messageBuffer = null;
        try (var byteArrayOutputStream = new ByteArrayOutputStream()){

            DataTypesUtil.writeTwoBytes(byteArrayOutputStream, mqttMessagePacket.getPacketIdentifier());

            messageBuffer = byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return messageBuffer;
    }

    public byte[] encodeMessagePacket(MqttPubRec mqttMessagePacket) {
        byte[] messageBuffer = null;
        try (var byteArrayOutputStream = new ByteArrayOutputStream()){

            DataTypesUtil.writeTwoBytes(byteArrayOutputStream, mqttMessagePacket.getPacketIdentifier());

            messageBuffer = byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return messageBuffer;
    }

    public byte[] encodeMessagePacket(MqttPubRel mqttMessagePacket) {
        byte[] messageBuffer = null;
        try (var byteArrayOutputStream = new ByteArrayOutputStream()){

            DataTypesUtil.writeTwoBytes(byteArrayOutputStream, mqttMessagePacket.getPacketIdentifier());

            messageBuffer = byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return messageBuffer;
    }

    public byte[] encodeMessagePacket(MqttPubComp mqttMessagePacket) {
        byte[] messageBuffer = null;
        try (var byteArrayOutputStream = new ByteArrayOutputStream()){

            DataTypesUtil.writeTwoBytes(byteArrayOutputStream, mqttMessagePacket.getPacketIdentifier());

            messageBuffer = byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return messageBuffer;
    }

    public byte[] encodeMessagePacket(MqttSubscribe mqttMessagePacket) {
        byte[] messageBuffer = null;
        try (var byteArrayOutputStream = new ByteArrayOutputStream()){

            DataTypesUtil.writeTwoBytes(byteArrayOutputStream, mqttMessagePacket.getPacketIdentifier());

            mqttMessagePacket.getSubscriptionList().forEach(topic -> {
                try {
                    DataTypesUtil.encodeString(byteArrayOutputStream, topic.getTopicName());
                    byteArrayOutputStream.write(topic.getMqttQoS().getKey());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            messageBuffer = byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return messageBuffer;
    }

    public byte[] encodeMessagePacket(MqttSubAck mqttMessagePacket) {
        byte[] messageBuffer = null;
        try (var byteArrayOutputStream = new ByteArrayOutputStream()){

            DataTypesUtil.writeTwoBytes(byteArrayOutputStream, mqttMessagePacket.getPacketIdentifier());

            mqttMessagePacket.getQoSList().forEach(_QoS -> {
                byteArrayOutputStream.write(_QoS.getKey());
            });

            messageBuffer = byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return messageBuffer;
    }

    public byte[] encodeMessagePacket(MqttUnsubscribe mqttMessagePacket) {
        byte[] messageBuffer = null;
        try (var byteArrayOutputStream = new ByteArrayOutputStream()){

            DataTypesUtil.writeTwoBytes(byteArrayOutputStream, mqttMessagePacket.getPacketIdentifier());


            mqttMessagePacket.getTopicsList().forEach(topic -> {
                try {
                    DataTypesUtil.encodeString(byteArrayOutputStream, topic);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            messageBuffer = byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return messageBuffer;
    }

    public byte[] encodeMessagePacket(MqttUnsubAck mqttMessagePacket) {
        byte[] messageBuffer = null;
        try (var byteArrayOutputStream = new ByteArrayOutputStream()){

            DataTypesUtil.writeTwoBytes(byteArrayOutputStream, mqttMessagePacket.getPacketIdentifier());

            messageBuffer = byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return messageBuffer;
    }

}
