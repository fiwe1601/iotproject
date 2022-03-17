package com.project.iotproject.Util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class timeCalculations {

    long timeStart = 0;
    long timeStop = 0;
    ArrayList<Long> measurmentsList; 
    String fileName;
    int averageSum;
    int n = 0;

    public timeCalculations(String fileName, int averageSum, boolean deleteFilesBefore){
        measurmentsList = new ArrayList<>();
        this.fileName = fileName;
        this.averageSum = averageSum;
        if(deleteFilesBefore){
            deleteFile(fileName);
            deleteFile("average_"+fileName);
        }
    }

    public void globalStartTimer(){
        timeStart = System.currentTimeMillis();
        writeToFile(timeStart, "start_"+fileName);
    }

    public void globalStopTimer() throws IOException{
        timeStop = System.currentTimeMillis(); //returns long
        try (BufferedReader reader = new BufferedReader(new FileReader("start_"+fileName))) {
            for (int i = 0; i < n; i++) {
                reader.readLine();
            }
            n = n + 1;
            timeStart = Long.parseLong(reader.readLine());
            reader.close();
        }
        catch(IOException e){
            System.out.println(e);
        }
        measurmentsList.add(elapsedTime(timeStop, timeStart));
        System.out.println("Total time elapsed: " + elapsedTime(timeStop, timeStart));
        timeStart = 0;
        timeStop = 0;
        if(measurmentsList.size() == averageSum){
            writeListtoFile(fileName);
            writeToFile(calculateAverageTime(), "average_"+fileName);
            measurmentsList.clear();
        }
    }

    public void startTimer(){
        timeStart = System.currentTimeMillis();
    }

    public void stopTimer(){
        timeStop = System.currentTimeMillis(); //returns long
        measurmentsList.add(elapsedTime(timeStop, timeStart));
        System.out.println("TimeElapsed: " + elapsedTime(timeStop, timeStart));
        timeStart = 0;
        timeStop = 0;
        if(measurmentsList.size() == averageSum){
            writeListtoFile(fileName);
            writeToFile(calculateAverageTime(), "average_"+fileName);
            measurmentsList.clear();
        }
    }

    public void writeListtoFile(String fileName){
        measurmentsList.forEach(value -> writeToFile(value, fileName));
    }

    public long elapsedTime(long timeEnd, long timeStart){
        return timeEnd - timeStart;
    }

    public Long calculateAverageTime(){
        long average = (long) measurmentsList.stream()
            .mapToLong(value -> value)
            .average()
            .getAsDouble();
        return average;
    }

    public String convertLongTimetoDataTimeString(Long time){
        SimpleDateFormat formatter= new SimpleDateFormat("HH:mm:ss:SSSSS");
        Date date = new Date(time);
        String dateString = formatter.format(date);
        System.out.println(dateString);
        return dateString;
    }

    public <T> void writeToFile(T content, String fileName){
        try {
            File file = new File(fileName);
            if(!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWritter = new FileWriter(file.getName(), true);
            BufferedWriter output = new BufferedWriter(fileWritter);
            output.write(content + "\n");
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteFile(String fileName){
        (new File(fileName)).delete();
    }
}
