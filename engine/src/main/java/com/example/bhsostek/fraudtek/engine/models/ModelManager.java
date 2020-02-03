package com.example.bhsostek.fraudtek.engine.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.example.bhsostek.fraudtek.engine.math.Vector2f;
import com.example.bhsostek.fraudtek.engine.math.Vector3f;
import com.example.bhsostek.fraudtek.engine.renderer.EnumGLDatatype;
import com.example.bhsostek.fraudtek.engine.renderer.Handshake;
import com.example.bhsostek.fraudtek.engine.renderer.SpriteManager;
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
        System.out.println("Successfully loaded file: " + modelName + " File extension: " + fileExtension + " : Lines:" + lines.length);

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

        // for .mtl files
        boolean hasTexture = false;
        Vector2f overrideCoords = new Vector2f(0);
        String mtllib = "";
        int matScale = 0;
        String faceMaterial = "";

        HashMap<String, Vector3f> materials = new HashMap<>();
        HashMap<String, Vector2f> materialLocations = new HashMap<>();

        //Parse data from file
        for(String line : lines){
            //Loading material library
            if(line.startsWith("mtllib ")){
                mtllib = line.replace("mtllib ", "");
                //Now that we have found a material file, lets look up that file and read in all of the materials
                System.out.println("Looking for lib:"+"models/"+mtllib.trim());
                String[] matFile = AssetManager.getInstance().readFile("models/" + mtllib.trim()).split("\n");
                String currentMaterial = "";
                for(String matLine : matFile){
                    if(matLine.startsWith("newmtl ")){
                        currentMaterial = matLine.replace("newmtl ", "");
                    }
                    //Get diffuse color
                    if(currentMaterial != null && matLine.startsWith("Kd ")){
                        String[] colorComponents = matLine.replace("Kd ", "").split(" ");
                        Vector3f color = new Vector3f(Float.parseFloat(colorComponents[0]), Float.parseFloat(colorComponents[1]), Float.parseFloat(colorComponents[2])).mul(255.0f);
                        materials.put(currentMaterial, color);
                    }
                }
                matScale = (int) Math.ceil(Math.sqrt(materials.size()));
                {
                    int i = 0;
                    for (String mat : materials.keySet()) {
                        float xPos = (i % (float) matScale) / (float) matScale;
                        float yPos = (float) Math.floor(i / (float) matScale) / (float) matScale;
                        materialLocations.put(mat, new Vector2f(xPos, yPos));
                        System.out.println("Location:" + new Vector2f(xPos, yPos));
                        i++;
                    }
                }
                //Check to see if this models image exists, if not create it here
//                String bitmapFileName = mtllib.trim().replace(".mtl", ".png");
//                Bitmap bmp = AssetManager.getInstance().readImage(bitmapFileName);
//                if(bmp == null){
//                    int[] colors = new int[matScale * matScale];
//                    for(int i = 0; i < colors.length; i++){
//                        colors[i] = 0;
//                    }
//                    bmp = Bitmap.createBitmap(colors, matScale, matScale, Bitmap.Config.ARGB_8888);
//                    int i = 0;
//                    for (String mat : materials.keySet()) {
//                        int xPos = (int) Math.floor(i % (float) matScale);
//                        int yPos = (int) Math.floor(i / (float) matScale);
//                        Vector3f color = materials.get(mat);
//                        bmp.setPixel(xPos, yPos, Color.argb(1, color.x(), color.y(), color.z()));
//                        i++;
//                    }
//                    SpriteManager.getInstance().putTexture(bitmapFileName, bmp);
//                }
            }
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
                    hasTexture = true;
                    //vertex
                    line = line.replace("vt ", "");
                    String[] components = line.split(" ");
                    Vector2f vector = new Vector2f(Float.parseFloat(components[0]), Float.parseFloat(components[1])).mul(new Vector2f(1, -1));
                    textureVectors.addFirst(vector); // More memory efficient because we do not need to traverse the whole list to add a new element. Although, this LL interface may hold pointer to end of list.
                }
            }else{
                if(line.startsWith("usemtl ") && !hasTexture){
                    //Force our Texture coords to be different
                    String materialName = line.replace("usemtl ", "");
                    faceMaterial = materialName;
                    System.out.println("Setting color to:" + materials.get(materialName));

                }
                if(line.startsWith("f ")) {
                    faceCount++;
                    if (vertecies == null) {
                        System.out.println("Vertecies have been loaded, Buffering to array:" + verteciesList.size());
                        vertecies = new Vector3f[verteciesList.size()];
                        int index = verteciesList.size() - 1;
                        for (Vector3f vec : verteciesList) {
                            vertecies[index] = vec;
                            index--;
                        }
                        System.out.println("Normals have been loaded, Buffering to array:" + nomrmalsList.size());
                        normals = new Vector3f[nomrmalsList.size()];
                        index = nomrmalsList.size() - 1;
                        for (Vector3f vec : nomrmalsList) {
                            normals[index] = vec;
                            index--;
                        }
                        System.out.println("Textures have been loaded, Buffering to array:" + textureVectors.size());
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
                    if(componentParts.length == 3) {
                        int index = Integer.parseInt(componentParts[0].trim()); //Index   //Always
                        int textureIndex = 1;
                        if (!componentParts[1].trim().isEmpty()) {
                            textureIndex = Integer.parseInt(componentParts[1].trim()); //texture //Sometimes
                        }
                        int normalVector = Integer.parseInt(componentParts[2].trim()); //Normal  //Always
                        facesList.addLast(vertecies[index - 1]);
                        facesList_normal.addLast(normals[normalVector - 1]);

                        //Textures
                        if ((textureIndex - 1) < textures.length) {
                            facesList_texture.addLast(textures[textureIndex - 1]);
                        } else {
                            //We dont have a texture but we do have material channels
                            facesList_texture.addLast(materialLocations.get(faceMaterial));
                            System.out.println("Using override texture index:" + materialLocations.get(faceMaterial));
                        }
                    }else if(componentParts.length == 2){
                        component = component.replaceAll("/", "//");
                        componentParts = component.split("/");
                        int index = Integer.parseInt(componentParts[0].trim()); //Index   //Always
                        int textureIndex = 1;
                        if (!componentParts[1].trim().isEmpty()) {
                            textureIndex = Integer.parseInt(componentParts[1].trim()); //texture //Sometimes
                        }
                        int normalVector = Integer.parseInt(componentParts[2].trim()); //Normal  //Always
                        facesList.addLast(vertecies[index - 1]);
                        facesList_normal.addLast(normals[normalVector - 1]);

                        //Textures
                        if ((textureIndex - 1) < textures.length) {
                            facesList_texture.addLast(textures[textureIndex - 1]);
                        } else {
                            //We dont have a texture but we do have material channels
                            facesList_texture.addLast(materialLocations.get(faceMaterial));
                            System.out.println("Using override texture index:" + materialLocations.get(faceMaterial));
                        }
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
