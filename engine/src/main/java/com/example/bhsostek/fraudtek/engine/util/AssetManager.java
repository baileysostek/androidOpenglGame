package com.example.bhsostek.fraudtek.engine.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.example.bhsostek.fraudtek.engine.R;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Scanner;

public class AssetManager {

    private static AssetManager assetManager;

    private HashMap<String, String> loadedFiles = new HashMap<>();

    //fields on the singleton (NOTE these variables cannot be accessed from a static context)
    private Context context;
    private android.content.res.AssetManager manager;

    //TODO cache option here.
    public String readFile(String name){
        if(loadedFiles.containsKey(name)){
            return loadedFiles.get(name);
        }
        try {
            InputStream fileBytes = this.manager.open(name);
            Scanner scanner = new Scanner(fileBytes).useDelimiter("\\A");
            if(scanner.hasNext()){
                String body = scanner.next();
                loadedFiles.put(name, body);
                return body;
            }else{
                return "";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private void writeFile(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("config.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            System.out.println("File write failed: " + e.toString());
        }
    }

    public Bitmap readImage(String imageName){
        try {
            Bitmap out = BitmapFactory.decodeStream(this.manager.open("textures/"+imageName));
            return out;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Initialize code
    public static void initialize(Context context){
        if(assetManager == null){
            assetManager = new AssetManager();
            AssetManager.getInstance().setContext(context);
        }
    }

    //Cleanup our memory. We don't want to have old programs lying around.
    public void shutdown(){

    }

    //Singleton Design Pattern
    public static AssetManager getInstance(){
        return assetManager;
    }

    public void setContext(Context context) {
        this.context = context;
        this.manager = context.getAssets();
    }

}
