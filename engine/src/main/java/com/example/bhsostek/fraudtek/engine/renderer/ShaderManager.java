package com.example.bhsostek.fraudtek.engine.renderer;

import android.opengl.GLES20;

import com.example.bhsostek.fraudtek.engine.util.AssetManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Scanner;

public class ShaderManager {

    //Singleton instance
    public static ShaderManager shaderManager;

    //Store references to all shader programs compiled via this class, so we can delete them on shutdown.
    private HashMap<String, Integer>    shaderInstances       = new HashMap<>();
    private HashMap<String, Shader>     shaders               = new HashMap<>();
    private HashMap<Integer, String>    shaderInstances_prime = new HashMap<>();

    private ShaderManager(){

    }

    public int loadShader(String name){
        //Look at the assets we have available to us, and load a shaders source files
        String info     = AssetManager.getInstance().readFile("shaders/" + name + "_properties.json");
        String vertex   = AssetManager.getInstance().readFile("shaders/" + name + "_vertex.glsl");
        String fragment = AssetManager.getInstance().readFile("shaders/" + name + "_fragment.glsl");

        //Buffer for reading compile status
        int[] compileBuffer = new int[]{ 0 };

        //Now that we have a source, we  need to compile the shaders into GPU code
        int vertexShader   = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        GLES20.glShaderSource(vertexShader, vertex);
        GLES20.glCompileShader(vertexShader);
        GLES20.glGetShaderiv(vertexShader, GLES20.GL_COMPILE_STATUS, compileBuffer, 0);
        if (compileBuffer[0] == GLES20.GL_FALSE) {
            GLES20.glGetShaderiv(vertexShader, GLES20.GL_INFO_LOG_LENGTH, compileBuffer, 0);
            //Check that log exists
            if (compileBuffer[0] > 0) {
                //Cleanup our broken shader
                GLES20.glDeleteShader(vertexShader);
                System.out.println("Vertex Status:" + GLES20.glGetShaderInfoLog(vertexShader));
                return -1;
            }
        }else{
            if(compileBuffer[0] == GLES20.GL_TRUE){
                System.out.println("Vertex Shader compiled Successfully.");
            }else{
                System.out.println("Vertex Shader compiled in an unknown state. This may cause strange behavior.");
            }
        }

        int fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(fragmentShader, fragment);
        GLES20.glCompileShader(fragmentShader);
        GLES20.glGetShaderiv(fragmentShader, GLES20.GL_COMPILE_STATUS, compileBuffer, 0);
        if (compileBuffer[0] == GLES20.GL_FALSE) {
            GLES20.glGetShaderiv(fragmentShader, GLES20.GL_INFO_LOG_LENGTH, compileBuffer, 0);
            //Check that log exists
            if (compileBuffer[0] > 0) {
                //Cleanup our broken shader
                GLES20.glDeleteShader(vertexShader);
                System.out.println("Fragment Status:" + GLES20.glGetShaderInfoLog(fragmentShader));
                return -1;
            }
        }else{
            if(compileBuffer[0] == GLES20.GL_TRUE){
                System.out.println("Fragment Shader compiled Successfully.");
            }else{
                System.out.println("Fragment Shader compiled in an unknown state. This may cause strange behavior.");
            }
        }

        //Now that we have our shaders compiled, we link them to a shader program.
        int programID = GLES20.glCreateProgram();

        //Combine vertex and fragment shaders into one program
        GLES20.glAttachShader(programID, vertexShader);
        GLES20.glAttachShader(programID, fragmentShader);

        //Link
        GLES20.glLinkProgram(programID);

        //Check link status
        GLES20.glGetShaderiv(vertexShader, GLES20.GL_LINK_STATUS, compileBuffer, 0);
        if (compileBuffer[0] == GLES20.GL_TRUE) {
            System.out.println("Shader Link was successful.");
            //Add this shader to our shader cache
            shaderInstances.put(name, programID);
            shaderInstances_prime.put(programID, name);
        }else{
            System.err.println("Shader Link failed.");
            GLES20.glDeleteProgram(programID);
            return -1;
        }

        //These programs are no longer needed, so we can simply clean them up.
        GLES20.glDeleteShader(vertexShader);
        GLES20.glDeleteShader(fragmentShader);

        //Add the parsed meta data into this
        try {
            JSONObject shaderMeta = new JSONObject(info);
            String version = shaderMeta.getString("Version");

            //IN's are attributes that need to be bound to the current context.
            JSONObject vertexData = shaderMeta.getJSONObject("Vertex");
            JSONArray attributesJSON = vertexData.getJSONObject("Attributes").names();
            String[] attributes = new String[attributesJSON.length()];
            for(int i = 0; i < attributesJSON.length(); i++){
                String attributeName = attributesJSON.getString(i);
                attributes[i] = attributeName;
                System.out.println("Adding attribute: " + attributes[i]);
            }

            //Create a shader data object which will be used to hold data about this shader.
            Shader shader = new Shader(name, programID, version).setAttributes(attributes);

            //get all uniforms
            JSONArray uniformsJSON = vertexData.getJSONObject("Uniforms").names();
            for(int i = 0; i < uniformsJSON.length(); i++){
                String uniformName = uniformsJSON.getString(i);
                String uniformType = vertexData.getJSONObject("Uniforms").getString(uniformName);
                shader.addUniform(uniformName, uniformType);
                System.out.println("Adding uniform named: " + uniformName + " type:" + uniformType);
            }


            shaders.put(name, shader);
        } catch (JSONException e) {
            System.out.println("Error resolving metaData for this program, deleting this program.");
            e.printStackTrace();
            GLES20.glDeleteProgram(programID);
            return -2;
        }

        //Return the program ID, and store this shader's name hashed to its program id. That way we can skip loading in the future
        return programID;
    }

    public boolean hasShader(String name){
        return shaderInstances.containsKey(name);
    }

    //Use shader takes a shader context
    public void useShader(int programID){
        if(shaderInstances_prime.containsKey(programID)){
            GLES20.glUseProgram(programID);
        }else{
            System.err.println("Tried to use a shader programID out of the range of currently available programs.");
        }
    }

    public void loadHandshakeIntoShader(int programID, Handshake handshake){
        if(shaderInstances_prime.containsKey(programID)) {
            Shader shader = shaders.get(shaderInstances_prime.get(programID));
            for(String attribute : shader.getAttributes()){
                //If handshake contains this buffered data.
                int attribPointer = GLES20.glGetAttribLocation(programID, attribute);

                //Enable vertex attribute
                GLES20.glEnableVertexAttribArray(attribPointer);

                //Check to see if this handshake has this attribute.
                if(!handshake.hasAttribute(attribute)){
                    System.err.println("This handshake does not contain the attribute: " + attribute);
                }

                GLES20.glVertexAttribPointer(attribPointer, 3, GLES20.GL_FLOAT, true, 0, handshake.getAttribute(attribute));
            }
            //Cleanup stuff | This redirects the pointer for the active attribute array to null.
            GLES20.glEnableVertexAttribArray(0);
        }
    }

    //Initialize code
    public static void initialize(){
        if(shaderManager == null){
            shaderManager = new ShaderManager();
        }
    }

    //Cleanup our memory. We don't want to have old programs lying around.
    public void shutdown(){
        for(int id : shaderInstances.values()){
            GLES20.glDeleteProgram(id);
            System.out.println("Deleted program:" + id);
        }
    }

    //Singleton Design Pattern
    public static ShaderManager getInstance(){
        return shaderManager;
    }
}
