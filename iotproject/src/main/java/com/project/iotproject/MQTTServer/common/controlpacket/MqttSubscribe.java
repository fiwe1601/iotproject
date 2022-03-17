package com.project.iotproject.MQTTServer.common.controlpacket;

import java.util.ArrayList;

import com.project.iotproject.MQTTServer.common.MqttPersistableControlPacket;
import com.project.iotproject.MQTTServer.common.MqttControlPacketType;
import com.project.iotproject.MQTTServer.common.MqttSubscription;

public class MqttSubscribe extends MqttPersistableControlPacket {

    /** Fixed Header */
    private MqttControlPacketType _MQTTControlPacketType = MqttControlPacketType.SUBCRIBE;

    /** Variable Header */
    private int _PacketIdentifier;
    private ArrayList<MqttSubscription> _SubscriptionList; //Payload

    public MqttSubscribe(){
        this._SubscriptionList = new ArrayList<>();
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

    public MqttSubscribe setPacketIdentifier(int _PacketIdentifier){
        this._PacketIdentifier = _PacketIdentifier;
        return this;
    }
    public int getPacketIdentifier(){
        return _PacketIdentifier;
    }

    public MqttSubscribe setSubscriptionList(ArrayList<MqttSubscription> _SubscriptionList){
        this._SubscriptionList = _SubscriptionList;
        return this;
    }
    public ArrayList<MqttSubscription> getSubscriptionList(){
        return _SubscriptionList;
    }
    public MqttSubscribe addSubscription(MqttSubscription subscription){
        if(this._SubscriptionList == null) { this._SubscriptionList = new ArrayList<>(); }
        this._SubscriptionList.add(subscription);
        return this;
    }

	public boolean isRetryable() {
		return true;
    }
    
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n---MQTT Subscribe Control Packet---" + "\n");
        stringBuilder.append("Packet Identifier: " + getPacketIdentifier() + "\n");
        this._SubscriptionList.forEach(subscriptionListTopic -> {
            stringBuilder.append("Topic: " + subscriptionListTopic.getTopicName() + ", QoS: " + subscriptionListTopic.getMqttQoS() + "\n");
        });
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }    
}
