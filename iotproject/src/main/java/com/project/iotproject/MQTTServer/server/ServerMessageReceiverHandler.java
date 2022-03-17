package com.project.iotproject.MQTTServer.server;

import com.project.iotproject.common.MessageParser;
import com.project.iotproject.MQTTServer.common.listener.ClientConnectMessageListener;
import com.project.iotproject.MQTTServer.common.listener.ServerListener;
import com.project.iotproject.Util.ServerProperties;

/** Connection Runs on Individual Threads */
public abstract class ServerMessageReceiverHandler<T, Message> extends Thread {
    private boolean runServer;
    protected int packetSize;
    protected Integer port;
    protected String host;

    ServerListener<Message> serverListener;
    protected MessageParser<Message> messageParser;
    protected byte[] bufferArray;

    protected ClientConnectMessageListener<Message> clientConnectListener; //Listener

    protected abstract void messageReceiverLoop();
    protected abstract void OpenSocketConnection();
    protected abstract void terminateSocketConnection();

    protected ServerMessageReceiverHandler(MessageParser<Message> messageParser) {
        this.messageParser = messageParser;
        try {
            bufferArray = new byte[ServerProperties.PACKAGE_LENGTH];
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    @Override
    public void run() {
        OpenSocketConnection();
        runServer = true;
        while(runServer) {
            System.out.println("Listening on port: " + port);
            messageReceiverLoop();
        }
        terminateSocketConnection();
    }
    
    public void stopServer() {
        this.runServer = false;
        terminateSocketConnection();
    }

    //Sets Clients from Listening
    @SuppressWarnings("unchecked")
    public T setClientConnectListener(ClientConnectMessageListener<Message> clientConnectListener) {
        this.clientConnectListener = clientConnectListener;
        return (T)this;
    }

    @SuppressWarnings("unchecked")
    public T setServerListener(ServerListener<Message> serverListener) {
        this.serverListener = serverListener;
        return (T)this;
    }

    @SuppressWarnings("unchecked")
    public T setPacketLength(int packetSize) {
        this.packetSize = packetSize;
        return (T)this;
    }

    @SuppressWarnings("unchecked")
    public T setPort(int port) {
        this.port = port;
        return (T)this;
    }

    @SuppressWarnings("unchecked")
    public T setHost(String hostname) {
        this.host = hostname;
        return (T) this;
    }



    public ServerMessageReceiverHandler<T, Message> setClientConnectListener1(ClientConnectMessageListener<Message> clientConnectListener) {
        this.clientConnectListener = clientConnectListener;
        return this;
    }

    public ServerMessageReceiverHandler<T, Message> setPacketLength1(int packetSize) {
        this.packetSize = packetSize;
        return this;
    }

    public ServerMessageReceiverHandler<T, Message> setPort1(int port) {
        this.port = port;
        return this;
    }

}
