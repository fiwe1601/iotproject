package com.project.iotproject.CoAPClient.common.messageformat.option;

public class CoAPOptionsUnknown extends CoAPOptions<Object> {

    public CoAPOptionsUnknown(CoAPOptionsFormat _name, Object _value)
    { super(_name, _value, Object.class); }

    
    @Override
    public void setValueOfBytes(byte[] bytes) {}

    @Override
    public byte[] getValueAsBytes() { return null; }
    
}
