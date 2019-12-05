package com.example.bhsostek.fraudtek.engine.models;

import android.util.Log;

import com.example.bhsostek.fraudtek.engine.math.Vector2f;
import com.example.bhsostek.fraudtek.engine.math.Vector3f;
import com.example.bhsostek.fraudtek.engine.renderer.EnumGLDatatype;
import com.example.bhsostek.fraudtek.engine.renderer.Handshake;
import com.example.bhsostek.fraudtek.engine.util.AssetManager;

import java.util.HashMap;
import java.util.LinkedList;

//Singleton design pattern
public class ModelManager {

    //TODO create a scene structure with scenes and scene transitions. Scenes can register a need for specific models, and on transition to scene the entity manager will unload all uneded data, and load new data in

    private static ModelManager modelManager;
    private HashMap<String, Model> cachedModels = new HashMap<>();

    private ModelManager(){

    }

    //For now just obj files
    public Model loadModel(String modelName){
        //Check to see if model is cached
        if(cachedModels.containsKey(modelName)){
            return cachedModels.get(modelName);
        }

        //Check to see that a file extension has been specified
        if(!modelName.contains(".")){
            System.err.println("Tried to load model: " + modelName + " however no file extension is specified. This information is needed to correctly parse the file.");
        }

        String[] lines = AssetManager.getInstance().readFile("models/" + modelName).split("\n");
        String fileExtension = modelName.split("\\.")[1];

        //TODO replace with log manager call
        Log.w("ModelManager", "Successfully loaded file: " + modelName + " File extension: " + fileExtension + " : Lines:" + lines.length);

        switch(fileExtension){
            case "obj":
                Model model = parseOBJ(cachedModels.size(), lines);
                cachedModels.put(modelName, model);
                return model;
            default:
                System.err.println("No model parsing function defined for file extension type: " + fileExtension);
                return null;
        }
    }


    private Model parseOBJ(int id, String[] lines){
        //Buffer lists
        LinkedList<Vector3f>  verteciesList = new LinkedList<>();
        LinkedList<Vector3f>  nomrmalsList  = new LinkedList<>();
        LinkedList<Vector3f>  facesList     = new LinkedList<>();
        LinkedList<Vector2f>  textureVectors  = new LinkedList<>();
        LinkedList<Vector3f>  facesList_normal   = new LinkedList<>();
        LinkedList<Vector2f>  facesList_texture  = new LinkedList<>();

        //Arrays of data
        Vector3f[] vertecies = null;
        Vector3f[] normals   = null;
        Vector2f[] textures  = null;

        //Lists of raw floats to buffer into shader (VAO / Handshake)
        float[] vPositions = null;
        float[] vNormals   = null;
        float[] vTextures  = null;

        int faceCount = 0;

        //Parse data from file
        for(String line : lines){
            //Loading vertices
            if(line.startsWith("v ") || line.startsWith("vn ") || line.startsWith("vt ")){
                if(line.startsWith("v ")){
                    //vertex
                    line = line.replace("v ", "");
                    String[] components = line.split(" ");
                    Vector3f vector = new Vector3f(Float.parseFloat(components[0]), Float.parseFloat(components[1]), Float.parseFloat(components[2])).mul(1.0f);
                    verteciesList.addFirst(vector); // More memory efficient because we do not need to traverse the whole list to add a new element. Although, this LL interface may hold pointer to end of list.
                }
                if(line.startsWith("vn ")){
                    //vertex
                    line = line.replace("vn ", "");
                    String[] components = line.split(" ");
                    Vector3f vector = new Vector3f(Float.parseFloat(components[0]), Float.parseFloat(components[1]), Float.parseFloat(components[2])).mul(1.0f);
                    nomrmalsList.addFirst(vector); // More memory efficient because we do not need to traverse the whole list to add a new element. Although, this LL interface may hold pointer to end of list.
                }
                if(line.startsWith("vt ")){
                    //vertex
                    line = line.replace("vt ", "");
                    String[] components = line.split(" ");
                    Vector2f vector = new Vector2f(Float.parseFloat(components[0]), Float.parseFloat(components[1])).mul(1.0f);
                    textureVectors.addFirst(vector); // More memory efficient because we do not need to traverse the whole list to add a new element. Although, this LL interface may hold pointer to end of list.
                }
            }else{
                if(line.startsWith("f ")) {
                    faceCount++;
                    if (vertecies == null) {
                        Log.w("ModelManager", "Vertecies have been loaded, Buffering to array:" + verteciesList.size());
                        vertecies = new Vector3f[verteciesList.size()];
                        int index = verteciesList.size() - 1;
                        for (Vector3f vec : verteciesList) {
                            vertecies[index] = vec;
                            index--;
                        }
                        Log.w("ModelManager", "Normals have been loaded, Buffering to array:" + nomrmalsList.size());
                        normals = new Vector3f[nomrmalsList.size()];
                        index = nomrmalsList.size() - 1;
                        for (Vector3f vec : nomrmalsList) {
                            normals[index] = vec;
                            index--;
                        }
                        Log.w("ModelManager", "Textures have been loaded, Buffering to array:" + textureVectors.size());
                        textures = new Vector2f[textureVectors.size()];
                        index = textureVectors.size() - 1;
                        for (Vector2f vec : textureVectors) {
                            textures[index] = vec;
                            index--;
                        }
                    }
                }
            }

            //Loading vertices
            if(line.startsWith("f ")){
                //vertex
                line = line.replace("f ", "");
                String[] components = line.split(" ");
                for(String component : components){
                    String[] componentParts = component.split("/");
                    int index        = Integer.parseInt(componentParts[0].trim()); //Index   //Always
                    int textureIndex = 1;
                    if(!componentParts[1].trim().isEmpty()){
                        textureIndex = Integer.parseInt(componentParts[1].trim()); //texture //Sometimes
                    }
                    int normalVector = Integer.parseInt(componentParts[2].trim()); //Normal  //Always
                    facesList.addLast(vertecies[index-1]);
                    facesList_normal.addLast(normals[normalVector-1]);

                    //Textures
                    if((textureIndex - 1) < textures.length) {
                        Log.w("ModelManager", textures[textureIndex - 1].toString());
                        facesList_texture.addLast(textures[textureIndex - 1]);
                    }else{
                        facesList_texture.addLast(new Vector2f(0));
                    }
                }
            }
        }

        //Put our lists into buffers for this Model.
        vPositions = new float[facesList.size() * 3];
        vNormals   = new float[facesList.size() * 3];
        vTextures  = new float[facesList.size() * 2];
        for(int i = 0; i < facesList.size(); i++){
            vPositions[(i * 3) + 0] = facesList.get(i).x();
            vPositions[(i * 3) + 1] = facesList.get(i).y();
            vPositions[(i * 3) + 2] = facesList.get(i).z();

            vTextures[(i * 2) + 0] = facesList_texture.get(i).x();
            vTextures[(i * 2) + 1] = facesList_texture.get(i).y();
//            vTextures[(i * 2) + 0] = (float)Math.random();
//            vTextures[(i * 2) + 1] = (float)Math.random();

            vNormals[(i * 3) + 0] = facesList_normal.get(i).x();
            vNormals[(i * 3) + 1] = facesList_normal.get(i).y();
            vNormals[(i * 3) + 2] = facesList_normal.get(i).z();
        }

        Handshake modelHandshake = new Handshake();
        modelHandshake.addAttributeList("vPosition", vPositions, EnumGLDatatype.VEC3);
        modelHandshake.addAttributeList("vColor", vNormals, EnumGLDatatype.VEC3);
        modelHandshake.addAttributeList("vNormal", vNormals, EnumGLDatatype.VEC3);
        modelHandshake.addAttributeList("vTexture", vTextures, EnumGLDatatype.VEC2);

        System.out.println("AABB");
        for(Vector3f vec: getAABB(verteciesList)){
            System.out.println(vec);
        }

        return new Model(id, modelHandshake, faceCount * 3, getAABB(verteciesList));
    }

    public Vector3f[] getAABB(LinkedList<Vector3f>  verteciesList){
        Vector3f min = new Vector3f(Float.MAX_VALUE,Float.MAX_VALUE,Float.MAX_VALUE);
        Vector3f max = new Vector3f(Float.MIN_VALUE,Float.MIN_VALUE,Float.MIN_VALUE);

        for(Vector3f vertex : verteciesList){

            if(vertex.x()>max.x()){
                max.setX(vertex.x());
            }
            if(vertex.y()>max.y()){
                max.setY(vertex.y());
            }
            if(vertex.z()>max.z()){
                max.setZ(vertex.z());
            }
            if(vertex.x()<min.x()){
                min.setX(vertex.x());
            }
            if(vertex.y()<min.y()){
                min.setY(vertex.y());
            }
            if(vertex.z()<min.z()){
                min.setZ(vertex.z());
            }
        }

        Vector3f[] out = new Vector3f[]{min, max};

        return out;
    }


    public static void initialize(){
        if(modelManager == null){
            modelManager = new ModelManager();
        }
    }

    public static ModelManager getInstance(){
        return modelManager;
    }
}
