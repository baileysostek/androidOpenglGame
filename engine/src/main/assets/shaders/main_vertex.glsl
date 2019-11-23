#version 100
// Inputs
attribute vec4 vPosition;
attribute vec3 vNormal;

//Uniform variables
uniform mat4 transformation; // objects transform in space
uniform mat4 view;           // Cameras position in space
uniform vec3 inverseCamera;  // Cameras position in space
uniform mat4 perspective;    //Perspective of this world

// Outputs
varying vec3 passNormal;
varying vec3 cameraDir;
varying vec3 interpolated_color;

//Main function to run
void main(){
    //Transdform the normnal vectors of this model by its transform.
    vec4 offsetNormal = transformation *  vec4(vNormal.xyz, 1.0);
    vec4 worldOffset = transformation * vec4(0, 0, 0, 1);
    passNormal = (vec3(offsetNormal) / offsetNormal.w) - (worldOffset.xyz)/worldOffset.w;

    //Output color
    interpolated_color = vec3(1, 0, 0);

    vec4 worldPosition = transformation * vec4(vPosition.x, vPosition.y, vPosition.z, 1.0);
//    worldPosition.x += 1;

    //Camera Direction
//    vec4 offsetCameraPos = view * vec4(0, 0, 0, 1.0);
//    vec3 delta = ((offsetCameraPos.xyz / offsetCameraPos.w) * vec3(-1, -1, -1)) - (worldPosition.xyz / worldPosition.w);
//    vec3 normalCamera = delta;
    cameraDir = inverseCamera;

    gl_Position = perspective * view * worldPosition;
}