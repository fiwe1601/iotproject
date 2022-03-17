package com.project.iotproject.CoAPServer;

import java.util.concurrent.ThreadLocalRandom;

public class RandomNumberGenerator extends Thread{

    public RandomNumberGenerator(){}

    @Override
    public void run() {
        while(true){
            int boundedRandomValue = ThreadLocalRandom.current().nextInt(0, 100);
            System.out.println(boundedRandomValue);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void getRandomNumber(){
        this.start();
    }
        
}

