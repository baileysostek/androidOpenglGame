package com.example.bhsostek.fraudtek.engine.renderer;

public enum EnumGLDatatype {

    //Different data types that we can use in our shader.
    FLOAT(Float.BYTES, 1 ),
    VEC2 (Float.BYTES, 2 ),
    VEC3 (Float.BYTES, 3 ),
    VEC4 (Float.BYTES, 4 ),

    //Matrix
    MAT3(Float.BYTES, 9 ),
    MAT4(Float.BYTES, 16),
    ;

    //Size in bytes of one pice of data
    protected int instanceSize; //Size of a single piece of data in bytes. IE a float
    protected int sizePerVertex;

    EnumGLDatatype(int instanceSize, int sizePerVertex){
        this.instanceSize  = instanceSize;
        this.sizePerVertex = sizePerVertex;
    }
}
