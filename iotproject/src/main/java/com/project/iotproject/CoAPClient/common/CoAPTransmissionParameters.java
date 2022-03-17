package com.project.iotproject.CoAPClient.common;

public class CoAPTransmissionParameters {
    private CoAPTransmissionParameters() {}

/**
 * +-------------------+---------------+
 * | name              | default value |
 * +-------------------+---------------+
 * | ACK_TIMEOUT       | 2 seconds     |
 * | ACK_RANDOM_FACTOR | 1.5           |
 * | MAX_RETRANSMIT    | 4             |
 * | NSTART            | 1             |
 * | DEFAULT_LEISURE   | 5 seconds     |
 * | PROBING_RATE      | 1 Byte/second |
 * +-------------------+---------------+
 * 
 * @return
 */
    
    /**
     * Transmission Parameters
     * @return
     */
    public int getAckTimeout(){
        return 2000;
    }

    public float getAckRandomFactor(){
        return 1.5f;
    }

    public int getMaxRetransmit(){
        return 4;
    }

    public int getNstart(){
        return 1;
    }

    public int getDefualtLeisure(){
        return 5000;
    }

    public int getProbingRate(){
        return 1;
    }

    /**
     * Time Values Derived from Transmission Parameters
     *  +-------------------+---------------+
     *  | name              | default value |
     *  +-------------------+---------------+
     *  | MAX_TRANSMIT_SPAN | 45 s          |
     *  | MAX_TRANSMIT_WAIT | 93 s          |
     *  | MAX_LATENCY       | 100 s         |
     *  | PROCESSING_DELAY  | 2 s           |
     *  | MAX_RTT           | 202 s         |
     *  | EXCHANGE_LIFETIME | 247 s         |
     *  | NON_LIFETIME      | 145 s         |
     *  +-------------------+---------------+
     * @return
     */

     /**
      * MAX_TRANSMIT_SPAN is the maximum time from the first transmission
      of a Confirmable message to its last retransmission. For the
      default transmission parameters, the value is (2+4+8+16)*1.5 = 45
      seconds, or more generally:
      ACK_TIMEOUT * ((2 ** MAX_RETRANSMIT) - 1) * ACK_RANDOM_FACTOR
      * @return
      */
     public int getMaxTransmitSpan(){
        return (int)(getAckTimeout() * ((2 * getMaxRetransmit()) - 1) * getAckRandomFactor());
     }

    /**
     * MAX_TRANSMIT_WAIT is the maximum time from the first transmission
     * of a Confirmable message to the time when the sender gives up on
     * receiving an acknowledgement or reset. For the default
     * transmission parameters, the value is (2+4+8+16+32)*1.5 = 93
     * seconds, or more generally:
     * ACK_TIMEOUT * ((2 ** (MAX_RETRANSMIT + 1)) - 1) * ACK_RANDOM_FACTOR
     * @return
     */
    public int getMaxTransmitWait(){
        return (int)(getAckTimeout() * ((2 * (getMaxRetransmit() + 1)) - 1) * getAckRandomFactor());
    }

    /**
     * MAX_LATENCY is the maximum time a datagram is expected to take
     * from the start of its transmission to the completion of its
     * reception.
     * MAX_LATENCY to be 100 seconds. 
     * in ms
     * @return
     */
    public int getMaxLatency(){
        return 100000;
    }

    /**
     * PROCESSING_DELAY is the time a node takes to turn around a
     * Confirmable message into an acknowledgement. We assume the node
     * will attempt to send an ACK before having the sender time out, so
     * as a conservative assumption we set it equal to ACK_TIMEOUT.
     * @return
     */
    public int getProcessingDelay(){
        return getAckTimeout();
    }

    /**
     * MAX_RTT is the maximum round-trip time, or:
     * (2 * MAX_LATENCY) + PROCESSING_DELAY
     */
    public int getMaxRtt(){
        return (2 * getMaxLatency()) * getProcessingDelay();
    }

    /**
     * EXCHANGE_LIFETIME is the time from starting to send a Confirmable
     * message to the time when an acknowledgement is no longer expected,
     * i.e., message-layer information about the message exchange can be purged.
     * MAX_TRANSMIT_SPAN + (2 * MAX_LATENCY) + PROCESSING_DELAY
     * or 247 seconds with the default transmission parameters.
     */
    public int getExchangeLifetime(){
        return getMaxTransmitSpan() + (2 * getMaxLatency()) + getProcessingDelay();
    }

    /**
     * NON_LIFETIME is the time from sending a Non-confirmable message to
     * the time its Message ID can be safely reused. If multiple
     * transmission of a NON message is not used, its value is
     * MAX_LATENCY, or 100 seconds. 
     * MAX_TRANSMIT_SPAN + MAX_LATENCY
     */
    public int getNonLifetime(){
        return getMaxTransmitSpan() + getMaxLatency();
    }

    //1152 bytes
    public int getMaxMessageSize(){
        return 1152;
    }

    //1024 bytes;
    public int getMaxPayloadSize(){
        return 1024;
    }
}