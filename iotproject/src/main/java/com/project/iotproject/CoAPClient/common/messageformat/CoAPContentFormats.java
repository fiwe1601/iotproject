package com.project.iotproject.CoAPClient.common.messageformat;

public abstract class CoAPContentFormats {
    
    /**
     * Content-Formats:
     * +--------------------------+-----+
     * | Media type               | Id. |
     * +--------------------------+-----+
     * | text/plain;charset=utf-8 | 0   |
     * | application/link-format  | 40  |
     * | application/xml          | 41  |
     * | application/octet-stream | 42  |
     * | application/exi          | 47  |
     * | application/json         | 50  |
     * | application/cbor         | 60  |
     * +--------------------------+-----+
     */
    private CoAPContentFormats(){}
    //Media Type = Id
    public static final int TEXT_PLAIN_UTF8 = 0;
    public static final int APPLICATION_LINK_FORMAT = 40;
    public static final int APPLICATION_XML = 41;
    public static final int APPLICATION_OCTET_STREAM = 42;
    public static final int APPLICATION_EXI = 47;
    public static final int APPLICATION_JSON = 50;
    public static final int APPLICATION_CBOR = 60;

    public static String idtoMediaType(int _COAPContentFormat){
        switch (_COAPContentFormat) {
            case TEXT_PLAIN_UTF8:
                return "text/plain;charset=utf-8";
            case APPLICATION_LINK_FORMAT:
                return "application/link-format";
            case APPLICATION_XML:
                return "application/xml";
            case APPLICATION_OCTET_STREAM:
                return "application/octet-stream";
            case APPLICATION_EXI:
                return " application/exi";
            case APPLICATION_JSON:
                return "application/json";
            case APPLICATION_CBOR:
                return "application/cbor";
    
            default:
                return "No_Content_Format" + _COAPContentFormat;
        }
    }

}

