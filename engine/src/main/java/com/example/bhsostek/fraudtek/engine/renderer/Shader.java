package com.example.bhsostek.fraudtek.engine.renderer;

import java.util.HashMap;

//Metadata container for the metadata in shaders
public class Shader {
    private final String version;
    private final String name;
    private final int    programID;

    private String[] attributes;
    private HashMap<String, EnumGLDatatype> uniforms = new HashMap<>();

    public Shader(String name, int programID, String version){
        this.name = name;
        this.programID = programID;
        this.version = version;
    }

    public Shader setAttributes(String[] attributes){
        this.attributes = attributes;
        return this;
    }

    public String[] getAttributes(){
        return this.attributes;
    }

    public void addUniform(String name, String datatype){
        //convert string datatype to datatype
        EnumGLDatatype type = EnumGLDatatype.valueOf(datatype.toUpperCase());
        if(type != null){//if there is a type it means that this uniform was bound correctly
            uniforms.put(name, type);
        }else{
            System.err.println("No datatype like " + datatype + " for uniform " + name + " could be found.");
        }
    }
}
