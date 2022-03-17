package com.project.iotproject.MQTTServer.common.controlpacket;

import com.project.iotproject.MQTTServer.common.MqttPersistableControlPacket;

import java.util.ArrayList;

import com.project.iotproject.MQTTServer.common.MqttControlPacketType;

public class MqttUnsubscribe extends MqttPersistableControlPacket {
    /**
     * An UNSUBSCRIBE Packet is sent by the Client to the Server, to unsubscribe from topics.
     * 
     * Bits 3,2,1 and 0 of the fixed header of the UNSUBSCRIBE Control Packet are reserved and MUST be set
     * to 0,0,1 and 0 respectively. The Server MUST treat any other value as malformed and close the Network Connection 
     * 
     * Remaining Length field
     * This is the length of variable header (2 bytes) plus the length of the payload.
     * 
     * The variable header contains a Packet Identifier.
     * 
     * The payload for the UNSUBSCRIBE Packet contains the list of Topic Filters that the Client wishes to unsubscribe from. 
     * The Topic Filters in an UNSUBSCRIBE packet MUST be UTF-8 encoded strings, packed contiguously. 
     * The Payload of an UNSUBSCRIBE packet MUST contain at least one Topic Filter. 
     * An UNSUBSCRIBE packet with no payload is a protocol violation.
     * 
     * The Topic Filters (whether they contain wildcards or not) supplied in an UNSUBSCRIBE packet MUST be
     * compared character-by-character with the current set of Topic Filters held by the Server for the Client. 
     * If any filter matches exactly then its owning Subscription is deleted, otherwise no additional processing occurs.
     * 
     * If a Server deletes a Subscription:
     *      It MUST stop adding any new messages for delivery to the Client [MQTT-3.10.4-2].
     *      It MUST complete the delivery of any QoS 1 or QoS 2 messages which it has started to send to the Client.
     *      It MAY continue to deliver any existing messages buffered for delivery to the Client.
     * The Server MUST respond to an UNSUBSUBCRIBE request by sending an UNSUBACK packet. 
     * The UNSUBACK Packet MUST have the same Packet Identifier as the UNSUBSCRIBE Packet. 
     * Even where no Topic Subscriptions are deleted, the Server MUST respond with an UNSUBACK.
     * If a Server receives an UNSUBSCRIBE packet that contains multiple Topic Filters it MUST handle thatpacket as 
     * if it had received a sequence of multiple UNSUBSCRIBE packets, except that it sends just one UNSUBACK response.
     * 
     */

    /** Fixed Header */
    private MqttControlPacketType _MQTTControlPacketType = MqttControlPacketType.UNSUBSCRIBE;

    /** Variable Header */
    private Integer _PacketIdentifier;
    private ArrayList<String> _TopicsList; //Payload

    public MqttUnsubscribe(){
        this._TopicsList = new ArrayList<>();
        //this._Payload = "";
    }

    @Override
    public MqttControlPacketType getType() {
        return _MQTTControlPacketType;
    }

    @Override
    public int getFixedHeader() {
        return ((getType().getKey() << 4) | 0x02);
    }
    
    @Override
    public int getVariableHeader() {
        return 0;
    }

    public MqttUnsubscribe setPacketIdentifier(int _PacketIdentifier) {
        this._PacketIdentifier = _PacketIdentifier;
        return this;
    }

    public int getPacketIdentifier() {
        return _PacketIdentifier;
    }

    //Topicslist = Payload
    public MqttUnsubscribe setTopicsList(ArrayList<String> _TopicsList) {
        this._TopicsList = _TopicsList;
        return this;
    }
    public MqttUnsubscribe addTopicsList(String topic) {
        if(this._TopicsList == null) { this._TopicsList = new ArrayList<>(); }
        this._TopicsList.add(topic);
        return this;
    }
    public ArrayList<String> getTopicsList() {
        return _TopicsList;
    }

    @Override
    public String toString() {
        StringBuilder StringBuilder = new StringBuilder();
        StringBuilder.append("\n---MQTT Unsubscribe Control Packet" + "\n");
        StringBuilder.append("Packet identifier: " + getPacketIdentifier() + "\n");
        this._TopicsList.forEach(topic -> {
            StringBuilder.append("Topic: " + topic + "\n");
        });
        StringBuilder.append("\n");
        return StringBuilder.toString();
    }  
}
