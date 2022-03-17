package com.project.iotproject.Util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import com.project.iotproject.MQTTServer.common.listener.callback.Callback;

public class MessageTransmissionUtil {

    public static void sendCoAPMessage(byte[] packetBuffer, String hostname, int port, Callback<byte[]> callback) {
		try (DatagramSocket datagramsocket = new DatagramSocket()) {
			InetAddress address = InetAddress.getByName(hostname);
			DatagramPacket request = new DatagramPacket(packetBuffer, packetBuffer.length, address, port);
			datagramsocket.send(request);
			datagramsocket.close();

			callback.callback(packetBuffer);
        } catch(Exception e) {
            e.printStackTrace();
		}
	}


    public static void receiveCoAPMessage(DatagramSocket datagramsocket, int receiveSize, Callback<DatagramPacket> callback){
		byte[] receivingBuffer = new byte[receiveSize];
		DatagramPacket responseMessage = new DatagramPacket(receivingBuffer, receivingBuffer.length); //receivingbuffersize
		try {
			datagramsocket.receive(responseMessage);
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			datagramsocket.close();
			//callback.callback(responseMessage.getData()); //byte[] return
			callback.callback(responseMessage);
		}
    }

	public static void sendAndReceiveCoAPMessage(byte[] packet, String hostname, int port, int receiveSize, Callback<DatagramPacket> callback) throws IOException {
		DatagramSocket datagramsocket = new DatagramSocket();
		//Send a CoAPMessage
		InetAddress address = InetAddress.getByName(hostname);
		DatagramPacket request = new DatagramPacket(packet, packet.length, address, port);
		datagramsocket.send(request);
		
		// Receive a CoAPMessage
		// An Async task always executes in new thread Can take time to receive message. Therefore perform other tasks in the meantime.
		new Thread(new Runnable() {
			public void run(){
				receiveCoAPMessage(datagramsocket, receiveSize, responseBuffer ->{
					callback.callback(responseBuffer);
				});
			}
		}).start();
	}


	/*
	public static void sendAndReceiveMQTTMessage(byte[] packet, String hostname, int port, Callback<byte[]> callback) {
		try {
			var socket = new Socket(hostname, port);
			socket.getOutputStream().write(packet);
			new Thread(new Runnable() {
				public void run(){
					try {
						callback.callback(socket.getInputStream().readAllBytes());
						socket.close();					
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
			

		} catch (Exception e) {
			e.printStackTrace();
		} finally{}
	}
	*/


    public static String getHttpRequest(String uri) throws IOException, InterruptedException  {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
			.GET()
        	.uri(URI.create(uri))
            .build();
		
        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        return response.body();
    }






}
