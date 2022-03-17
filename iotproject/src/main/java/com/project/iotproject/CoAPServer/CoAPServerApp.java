package com.project.iotproject.CoAPServer;

import java.io.IOException;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.util.concurrent.ThreadLocalRandom;

//http://netsec.unipr.it/project/mjcoap/
public class CoAPServerApp extends Thread{

    private static boolean doStop = false;

    public synchronized static void doStop() {
        doStop = true;
    }

    private synchronized static boolean keepRunning() {
        return doStop == false;
    }

    public static void main( String[] args ) throws IllegalStateException, IOException, URISyntaxException, InterruptedException {
        try {
			CoAPServer _CoAPServer = new CoAPServer();
        
            Thread thread = new Thread(){
                @Override
                public void run(){
                    while(keepRunning()){
                        //int boundedRandomValue = ThreadLocalRandom.current().nextInt(0, 100);
                        //int boundedRandomValue1 = ThreadLocalRandom.current().nextInt(0, 100);
                        //System.out.println(boundedRandomValue);
                        //System.out.println(boundedRandomValue1);
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            thread.start();

            new Thread(() -> {
                while(keepRunning()){
                    //int boundedRandomValue = ThreadLocalRandom.current().nextInt(0, 100);
                    //int boundedRandomValue1 = ThreadLocalRandom.current().nextInt(0, 100);
                    //System.out.println(boundedRandomValue);
                    //System.out.println(boundedRandomValue1);
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();


            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            doStop();

        } catch (SocketException e) {
            e.printStackTrace();
        }
    }



}


