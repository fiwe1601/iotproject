package com.project.iotproject.CoAPClient.common.messageformat.option;

import com.project.iotproject.Util.DataTypesUtil;

public class CoAPOptionsString extends CoAPOptions<String> {

    public CoAPOptionsString(CoAPOptionsFormat _name, String _value)
    { super(_name, _value, String.class); }

    
    @Override
    public void setValueOfBytes(byte[] bytes) {
        this.setValue(DataTypesUtil.byteArrayToString(bytes));
    }

    @Override
    public byte[] getValueAsBytes() {
        return getValue().getBytes();
    }
    
}
