package com.project.iotproject.MQTTServer.common;

/** Topic */
public class MqttSubscription {

	private String _TopicName;
	private MqttQoS _MQTTQoS;

    private boolean _Mutable = true;
	private boolean _NoLocal = false;

	public MqttSubscription() {}

	public MqttSubscription(String _TopicName, MqttQoS _MQTTQoS) {
		setTopicName(_TopicName);
		setMqttQoS(_MQTTQoS);
	}

	public MqttSubscription setTopicName(String _TopicName) {
		//checkMutable();
		if(_TopicName == null){
			throw new NullPointerException();
		}
        this._TopicName = _TopicName;
        return this;
	}

	public String getTopicName() {
		return _TopicName;
	}

	public MqttSubscription setMqttQoS(MqttQoS _MQTTQoS) {
		//checkMutable();
        this._MQTTQoS = _MQTTQoS;
        return this;
	}

	public MqttQoS getMqttQoS() {
		return _MQTTQoS;
	}
	

	public void setNoLocal(boolean _NoLocal) {
		checkMutable();
		this._NoLocal = _NoLocal;
	}

	public boolean isNoLocal() {
		return _NoLocal;
	}

	protected void setMutable(boolean _Mutable) {
		this._Mutable = _Mutable;
	}

	protected void checkMutable() throws IllegalStateException {
		if (!_Mutable) {
			throw new IllegalStateException();
		}
	}

}
