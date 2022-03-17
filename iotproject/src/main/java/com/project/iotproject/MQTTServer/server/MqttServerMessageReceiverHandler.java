package com.project.iotproject.MQTTServer.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import com.project.iotproject.common.MessageParser;
import com.project.iotproject.MQTTServer.common.listener.ClientConnectionListener;
import com.project.iotproject.MQTTServer.common.listener.callback.Callback;

public class MqttServerMessageReceiverHandler<Message> extends ServerMessageReceiverHandler<MqttServerMessageReceiverHandler<Message>, Message> {
    ServerSocket serverSocket;

    public MqttServerMessageReceiverHandler(MessageParser<Message> messageParser) {
        super(messageParser);  //Calling this class parent, superconstructor, MessageReceiverhandler(MessageParser<Message> parser)
    }
    
    @Override
    protected void messageReceiverLoop() {
        try {
            Socket socket = serverSocket.accept(); // Start listening for incoming client requests. Blocks below Methods until a connection is made.
            InputStream inputStream = socket.getInputStream(); // Read data sent from the client
            OutputStream outputStream = socket.getOutputStream(); // Send data to the client
            System.out.println("clientConnectListener: " + clientConnectListener);

            //Anonymous Inner Class instance object
            ClientConnectionListener<Message> connectionReceiver = new ClientConnectionListener<>() {
                
                @Override
                public void receivePacket(Callback<Message> callback) {
                    new Thread(() -> {
                        while(!socket.isClosed() && socket.isConnected()) {
                            try {
                                inputStream.read(bufferArray); //Reads some number of bytes from the input stream and stores them into the buffer array b.
                                //Message mqttMessage = (Message) messageParser.decodeMessage(bufferArray);
                                //callback.callback(bufferArray);
                                callback.callback(messageParser.decodeMessage(bufferArray));
                            } catch (IOException e) {
                                try { socket.close(); } catch (IOException e1) {
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
                        socket.close();
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
                    return socket.isConnected();
                }

                @Override
                public void disconnect() {
                    //try {
                    //    socket.close();
                    //} catch (IOException e) {
                    //    e.printStackTrace();
                    //}
                }

  
            };
            
            if(clientConnectListener != null) {
                //Method for every client is being run. Hence MqttServer, runs onClientConnect
                clientConnectListener.onClientMessageConnect(connectionReceiver);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void OpenSocketConnection() {
        try {
            //Creates a server socket and binds it to the port number.
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void terminateSocketConnection() {
        try {
            serverSocket.close(); //Terminate the server
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
