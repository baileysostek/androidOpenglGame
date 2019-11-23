package com.example.bhsostek.fraudtek.engine.renderer;

//A Handshake is a collection of data to be rendered by a shader.
//When a shader context is bound, a Handshake can be passed to it
//If the Handshake contains references to the attributes needed by
//the shader. The handshake is successful and the object renders.

//This class was specifically designed to operate similar to a VAO since OpenGL ES 2.0 does not have vaos

import java.nio.Buffer;
import java.util.HashMap;

public class Handshake {

    //Stored attributes
    private HashMap<String, Buffer> bufferedAttributes = new HashMap<>();

    public Handshake(){
    }

    public void addAttributeList(String name, float[] data, EnumGLDatatype datatype){
        bufferedAttributes.put(name, BufferUtils.bufferData(data, datatype));
    }

    public Buffer getAttribute(String attribute) {
        return bufferedAttributes.get(attribute);
    }


    public boolean hasAttribute(String attribute) {
        return this.bufferedAttributes.containsKey(attribute);
    }
}