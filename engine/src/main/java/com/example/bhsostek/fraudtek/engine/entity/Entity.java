package com.example.bhsostek.fraudtek.engine.entity;

import android.opengl.Matrix;

import com.example.bhsostek.fraudtek.engine.math.Vector3f;
import com.example.bhsostek.fraudtek.engine.models.Model;
import com.example.bhsostek.fraudtek.engine.math.MatrixUtils;

import org.json.JSONObject;

public class Entity{

    //Transform properties of an entity
    private Vector3f position = new Vector3f(0f);
    private Vector3f rotation = new Vector3f(0f);
    private float scale = 1.0f;

    private float[] transform = MatrixUtils.getIdentityMatrix();

    //Model data for this entity
    private Model model;

    private Entity parent = null;

    private EnumEntityType type = EnumEntityType.UNKNOWN;

    //Needed for Guac
    private boolean isSeethrough = false;

    public Entity(){}

    //This constructor should be overridden by child classes.
    public void initialize(JSONObject object){}

    public Entity setPosition(float x, float y, float z){
        position.setX(x);
        position.setY(y);
        position.setZ(z);
        return this;
    }

    public Entity setType(EnumEntityType type) {
        this.type = type;
        return this;
    }

    public Entity setPosition(Vector3f vector3f) {
        position = new Vector3f(vector3f);
        return this;
    }

    public Entity setRotation(float x, float y, float z){
        rotation.setX(x);
        rotation.setY(y);
        rotation.setZ(z);
        return this;
    }

    public Entity setRotation(Vector3f vector3f) {
        rotation = new Vector3f(vector3f);
        return this;
    }

    public Entity setScale(float w) {
        this.scale = w;
        return this;
    }

    public Entity setModel(Model m) {
        this.model = m;
        return this;
    }

    public Vector3f getPosition(){
        return this.position;
    }

    public Model getModel(){
        return model;
    }

    public Entity getParent(){
        return this.parent;
    }

    public void setParent(Entity parent){
        this.parent = parent;
    }

    public float[] getTransform(){
        transform = MatrixUtils.getIdentityMatrix();
        Matrix.scaleM(transform, 0, scale, scale, scale);
        Matrix.rotateM(transform, 0,  rotation.z(), 0f,0f, 1f);
        Matrix.rotateM(transform, 0,  rotation.y(), 0f,1f, 0f);
        Matrix.rotateM(transform, 0,  rotation.x(), 1f,0f, 0f);
        MatrixUtils.translate(transform, position);

        if(this.parent != null){
            float[] tmp = MatrixUtils.getIdentityMatrix();
            Matrix.multiplyMM(tmp, 0, this.parent.getTransform(), 0, transform, 0);
            transform = tmp;
        }

        return this.transform;
    }

    //Update Method
    public void update(double delta){
        return;
    }

    public void rotate(float x, float y, float z) {
        this.rotation.setX(this.rotation.x() + x);
        this.rotation.setY(this.rotation.y() + y);
        this.rotation.setZ(this.rotation.z() + z);
    }

    //Gets aabb in worldspace, I guess at this point its just a bb? because there is no more aa
    public Vector3f[] getAABB(){
        //Get the model transform
        float[] transform = MatrixUtils.getIdentityMatrix();
//        Matrix.rotateM(transform, 0, (float) Math.toRadians(rotation.z()), 0f,0f, 1f);
//        Matrix.rotateM(transform, 0, (float) Math.toRadians(rotation.y()), 0f,1f, 0f);
//        Matrix.rotateM(transform, 0, (float) Math.toRadians(rotation.x()), 1f,0f, 0f);

        //Raw unNormaized and unTranslated AABB
        Vector3f[] out = this.model.getAABB();

        float[] min = out[0].toVec4();
        float[] min_out = new float[]{0, 0, 0, 1};
        Matrix.multiplyMV(min_out, 0, transform, 0, min, 0);

        float[] max = out[1].toVec4();
        float[] max_out = new float[]{0, 0, 0, 1};
        Matrix.multiplyMV(max_out, 0, transform, 0, max, 0);

        return new Vector3f[]{new Vector3f(min_out).mul(scale).add(this.position), new Vector3f(max_out).mul(scale).add(this.position)};
    }

    //Override me
    public void onAdd(){
        return;
    }

    @Override
    public String toString(){
        return this.getType().toString();
    }

    public EnumEntityType getType() {
        return this.type;
    }

    protected float getScale() {
        return this.scale;
    }

    protected Vector3f getRotation() {
        return new Vector3f(this.rotation);
    }

    protected Entity rotate(Vector3f rotation) {
        this.rotate(rotation.x(), rotation.y(), rotation.z());
        return this;
    }
}
