package com.example.bhsostek.fraudtek.engine.util;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


/**
 * Created by Bailey on 1/21/2018.
 */
public class LogManager{

    private LinkedList<String> logData = new LinkedList<>();
    private String fileName = "log.txt";
    private boolean doOutput = true;

    private boolean lastWasPrintLn = false;
    private long startTime;

    //Frame Timing
    private HashMap<String, Long> frameTime = new HashMap<String, Long>();
    private long frameStartTime;


    public void println(){
        String line = "------------------------------------------------------------------------------------------";
        logData.add(line);
        if(doOutput) {
            System.out.println(line);
        }
    }

    public void println(String line){
        if(lastWasPrintLn) {
            String current = logData.getLast();
            logData.removeLast();
            logData.addLast(current + line);
            lastWasPrintLn = false;
        }else{
            logData.add(line);
        }
        if(doOutput) {
            System.out.println(line);
        }
    }

    public void println(String line, EnumErrorLevel errorLevel){
        if(lastWasPrintLn) {
            String current = logData.getLast();
            logData.removeLast();
            logData.addLast(current + "["+errorLevel.name()+"]" +line);
            lastWasPrintLn = false;
        }else{
            logData.add(line);
        }
        if(doOutput || errorLevel == EnumErrorLevel.STDIN) {
            System.out.println(line);
        }
    }


    public void println(String[] lines){
        if(lastWasPrintLn) {
            String current = logData.getLast();
            logData.removeLast();
            logData.addLast(current + lines[0]);
            for(int i = 1; i < lines.length; i++) {
                String line = lines[i];
                logData.add(line);
            }
            lastWasPrintLn = false;
        }else{
            for(int i = 0; i < lines.length; i++) {
                String line = lines[i];
                logData.add(line);
            }
        }
        if(doOutput) {
            for(int i = 0; i < lines.length; i++) {
                String line = lines[i];
                System.out.println(line);
            }
        }
    }

    public void print(String line){
        if(lastWasPrintLn) {
            String current = logData.getLast();
            logData.removeLast();
            logData.addLast(current + line);
        }else{
            logData.add(line);
            lastWasPrintLn = true;
        }
        if(doOutput) {
            System.out.print(line);
        }
    }

    public void flush(){
        String[] lines = new String[logData.size()];
        int index = 0;
        for(String s: logData){
            lines[index] = s;
            index++;
        }
    }

    public void setFileName(String name){
        if(!name.endsWith(".txt")){
            name = name+".txt";
        }
        this.fileName = name;
    }

    public void printStackTrace(Exception e){
        StackTraceElement[] errorMessage = e.getStackTrace();
        println();
        println("Error:"+e.getLocalizedMessage(), EnumErrorLevel.SEVERE);
        for(int i = 0; i < errorMessage.length; i++){
            println(errorMessage[i].getClassName()+"["+errorMessage[i].getLineNumber()+"]"+errorMessage[i].toString());
        }
        println();
    }

    public void printArray(float[] object){
        String line = "";
        for(int i = 0; i < object.length; i++){
            line += object[i]+",";
        }
        println(line);
    }

    public void printArray(int[] object){
        String line = "";
        for(int i = 0; i < object.length; i++){
            line += object[i]+",";
        }
        println(line);
    }

    public void printSquareArray(float[] object){
        int square = (int) Math.floor(Math.sqrt(object.length));
        println();
        for(int j = 0; j < square; j++){
            String line = "";
            for(int i = 0; i < square; i++){
                line += object[ i + (j * square)];
                if(i < square-1){
                    line+=",";
                }
            }
            println(line);
        }
        println();
    }

    public void startTimer(){
        this.startTime = System.nanoTime();
        this.println();
        this.println("Timer Started");
        this.println();
    }

    public void startFrameTimer(){
        this.frameStartTime = System.nanoTime();
    }


    public long stopTimer(){
        this.println();
        long delta = (System.nanoTime() - this.startTime);
        this.println("Stopped Timer:"+(delta));
        this.startTime = System.nanoTime();
        this.println();
        return delta;
    }

    public float stopTimerSeconds(){
        this.println();
        long delta = (System.nanoTime() - this.startTime);
        this.println("Stopped Timer:"+(delta/1000000000.0f));
        this.startTime = System.nanoTime();
        this.println();
        return (delta/1000000000.0f);
    }

    public float stopTimerSeconds(String info){
        this.println();
        long delta = (System.nanoTime() - this.startTime);
        this.println(info+":"+(delta/1000000000.0f));
        this.startTime = System.nanoTime();
        this.println();
        return (delta/1000000000.0f);
    }

    public long stopFrameTimerSilent(){
        this.frameStartTime = System.nanoTime();
        return 0;
    }

    public long splitTimer(){
        this.println();
        long delta = (System.nanoTime() - this.startTime);
        this.println("Split:"+(delta));
        this.println();
        return delta;
    }

    public long splitTimer(String callingClass){
        long delta = (System.nanoTime() - this.frameStartTime);
        if(frameTime.containsKey(callingClass)){
            frameTime.put(callingClass, delta + frameTime.get(callingClass));
        }else{
            frameTime.put(callingClass, delta);
        }
        this.frameStartTime = System.nanoTime();
        return delta;
    }

}
