package com.project.iotproject.CoAPClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.project.iotproject.common.MessageParser;
import com.project.iotproject.MQTTServer.common.listener.ClientConnectionListener;
import com.project.iotproject.MQTTServer.common.listener.callback.Callback;
import com.project.iotproject.MQTTServer.server.ClientMessageReceiverHandler;

public class CoapClientMessageReceiverHandler<Message> extends ClientMessageReceiverHandler<CoapClientMessageReceiverHandler<Message>, Message>{
    Socket clientSocket; // Client Socket
    
    public CoapClientMessageReceiverHandler(MessageParser<Message> messageParser) {
        super(messageParser);
    }

    @Override
    protected void messageReceiverLoop() {
        try {
            InputStream inputStream = clientSocket.getInputStream(); // Read data sent from the client
            OutputStream outputStream = clientSocket.getOutputStream(); // Send data to the client
            System.out.println("clientConnectListener: " + clientConnectListener);

            //Anonymous Inner Class instance object
            ClientConnectionListener<Message> connectionReceiver = new ClientConnectionListener<>() {
                
                @Override
                public void receivePacket(Callback<Message> callback) {
                    new Thread(() -> {
                        while(!clientSocket.isClosed() && clientSocket.isConnected()) {
                            try {
                                inputStream.read(bufferArray); //Reads some number of bytes from the input stream and stores them into the buffer array b.
                                //Message mqttMessage = (Message) messageParser.decodeMessage(bufferArray);
                                //callback.callback(bufferArray);
                                callback.callback(messageParser.decodeMessage(bufferArray));
                            } catch (IOException e) {
                                try { clientSocket.close(); } catch (IOException e1) {
                                    e1.printStackTrace(); }
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }

                // Close client connection
                @Override
                public void clientConnectionClose() {
                    try {
                        clientSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void sendMessage(Message message) {
                    byte[] buffer = messageParser.encodeMessage(message);
                    try {
                        outputStream.write(buffer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public boolean isConnected() {
                    return clientSocket.isConnected();
                }

                @Override
                public void disconnect() {
                    //try {
                    //    clientSocket.close();
                    //} catch (IOException e) {
                    //    e.printStackTrace();
                    //}
                }

            };
            
            if(clientConnectListener != null) {
                //Method for every client is being run. Hence MqttServer, runs onClientConnect
                clientConnectListener.onClientMessageConnect(connectionReceiver);
            }

            while(clientSocket.isConnected()) {
                /** TODO */
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

    @Override
    protected void OpenSocketConnection() {
        try {
            //Creates a server clientSocket and binds it to the port number.
            clientSocket = new Socket(host, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void terminateSocketConnection() {
        try {
            clientSocket.close(); //Terminate the server
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
