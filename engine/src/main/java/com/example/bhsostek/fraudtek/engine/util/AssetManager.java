package com.example.bhsostek.fraudtek.engine.util;

import android.content.Context;
import android.opengl.GLES20;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class AssetManager {

    private static AssetManager assetManager;

    //fields on the singleton (NOTE these variables cannot be accessed from a static context)
    private Context context;
    private android.content.res.AssetManager manager;

    //TODO cache option here.
    public String readFile(String name){
        try {
            InputStream fileBytes = this.manager.open(name);
            Scanner scanner = new Scanner(fileBytes).useDelimiter("\\A");
            if(scanner.hasNext()){
                return scanner.next();
            }else{
                return "";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
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
