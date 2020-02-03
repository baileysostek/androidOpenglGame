package com.example.bhsostek.fraudtek.engine.renderer;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class BufferUtils {

    public static Buffer bufferData(float[] raw, EnumGLDatatype datatype){
        ByteBuffer byteBuf = ByteBuffer.allocateDirect(raw.length * datatype.instanceSize); //4 bytes per float
        byteBuf.order(ByteOrder.nativeOrder());
        FloatBuffer buffer = byteBuf.asFloatBuffer();
        buffer.put(raw);
        buffer.position(0);

        return buffer;
    }

    public static Buffer bufferData(int[] raw){
        ByteBuffer byteBuf = ByteBuffer.allocateDirect(raw.length * 4); //4 bytes per float
        byteBuf.order(ByteOrder.nativeOrder());
        IntBuffer buffer = byteBuf.asIntBuffer();
        buffer.put(raw);
        buffer.position(0);

        return buffer;
    }
}
