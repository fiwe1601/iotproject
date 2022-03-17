package com.project.iotproject.MQTTServer.common;

import com.project.iotproject.Util.DataTypesUtil;

/**
 * The Connect Flags byte contains a number of parameters specifying the
 * behavior of the MQTT connection. It also indicates the presence or absence of
 * fields in the payload.
 * 
 */
public class MqttConnectFlags {
    boolean _UserNameFlag = false;
    boolean _PasswordFlag = false;
    boolean _WillRetain = false;
    MqttQoS _WillQoS = MqttQoS.ATMOSTONCE;   //00
    boolean _WillFlag = false;
    boolean _CleanSession = false;
    boolean _Reserved = false;  

    public MqttConnectFlags setMqttConnectFlag(Integer bits){
        this._UserNameFlag = (((bits & 128) >> 7) == 1) ? true : false;  //X0000000>>7 => 0000000X
        this._PasswordFlag = (((bits & 64) >> 6) == 1) ? true : false;
        this._WillRetain = (((bits & 32) >> 5) == 1) ? true : false;
        this._WillQoS = MqttQoS.getValue((bits & 24) >> 3);              //000XX000>>3 => 000000XX
        this._WillFlag = (((bits & 4) >> 2) == 1) ? true : false;
        this._CleanSession = (((bits & 2) >> 1) == 1) ? true : false;
        this._Reserved = (((bits & 1)) == 1) ? true : false;
        return this;
    }

    public Integer getMqttConnectFlag(){
        Integer boolInteger = DataTypesUtil.booleanArrayToInteger(
            _UserNameFlag, _PasswordFlag, _WillRetain, ((_WillQoS.getKey() & 0x02) == 2), ((_WillQoS.getKey() & 0x01) == 1), _WillFlag, _CleanSession, _Reserved);
        return boolInteger;
    }

    public boolean[] getMqttConnectFlagAsBooleanArray(){
        return new boolean[] {_UserNameFlag, _PasswordFlag, _WillRetain, ((_WillQoS.getKey() & 0x02) == 2), ((_WillQoS.getKey() & 0x01) == 1), _WillFlag, _CleanSession, _Reserved};
    }

    public MqttQoS getWillQoS(){
        return _WillQoS;
    }

    public MqttConnectFlags setUserNameFlagTrue(){
        _UserNameFlag = true;
        return this;
    }
    
    public MqttConnectFlags setPasswordFlagTrue(){
        _PasswordFlag = true;
        return this;
    }

    public MqttConnectFlags setWillRetainTrue(){
        _WillRetain = true;
        return this;
    }

    public MqttConnectFlags setWillFlagTrue(){
        _WillFlag = true;
        return this;
    }

    public MqttConnectFlags setCleanSessionTrue(){
        _CleanSession = true;
        return this;
    }

    public MqttConnectFlags setReservedTrue(){
        _Reserved = true;
        return this;
    }

    public MqttConnectFlags setUserNameFlag(boolean statement){
        this._UserNameFlag = statement;
        return this;
    }
    
    public MqttConnectFlags setPasswordFlag(boolean statement){
        this._PasswordFlag = statement;
        return this;
    }

    public MqttConnectFlags setWillRetain(boolean statement){
        this._WillRetain = statement;
        return this;
    }

    public MqttConnectFlags setWillQoS(MqttQoS statement){
        this._WillQoS = statement;
        return this;
    }

    public MqttConnectFlags setWillFlag(boolean statement){
        this._WillFlag = statement;
        return this;
    }

    public MqttConnectFlags setCleanSession(boolean statement){
        this._CleanSession = statement;
        return this;
    }

    public MqttConnectFlags setReserved(boolean statement){
        this._Reserved = statement;
        return this;
    }

    public boolean isUserNameFlag(){
        return _UserNameFlag;
    }
    
    public boolean isPasswordFlag(){
        return _PasswordFlag;
    }

    public boolean isWillRetain(){
        return _WillRetain;
    }

    public boolean isWillFlag(){
        return _WillFlag;
    }

    public boolean isCleanSession(){
        return _CleanSession;
    }

    public boolean isReserved(){
        return _Reserved;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("---MQTT Connect Flags---");
        builder.append("\nUser Name Flag: " + isUserNameFlag() + "\n");
        builder.append("Password Flag: " + isPasswordFlag() + "\n");
        builder.append("Will Retain: " + isWillRetain() + "\n");
        builder.append("Will QoS: " + getWillQoS() + "\n");
        builder.append("Will Flag: " + isWillFlag() + "\n");
        builder.append("Clean Session: " + isCleanSession() + "\n");
        builder.append("Reserved: " + isReserved());
        return builder.toString();
    }
    
}
