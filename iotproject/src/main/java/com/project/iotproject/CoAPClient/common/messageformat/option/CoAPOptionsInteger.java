package com.project.iotproject.CoAPClient.common.messageformat.option;

import com.project.iotproject.Util.DataTypesUtil;

public class CoAPOptionsInteger extends CoAPOptions<Integer> {

    public CoAPOptionsInteger(CoAPOptionsFormat _name, Integer _value)
    { super(_name, _value, Integer.class); }

    
    @Override
    public void setValueOfBytes(byte[] bytes) {
        this.setValue(DataTypesUtil.byteArrayToInteger(bytes));
    }

    @Override
    public byte[] getValueAsBytes() {
        return DataTypesUtil.integerToByteArray(getValue());
    }
    
}
