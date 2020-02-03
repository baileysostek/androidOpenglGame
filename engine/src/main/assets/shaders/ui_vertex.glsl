#version 100
// Inputs
attribute vec4 vPosition;
attribute vec2 vTexture;

//Uniform variables
uniform mat4 transformation; // objects transform in space
uniform vec2 scale;
uniform vec2 position;
uniform float ar;

// Outputs
varying vec2 pass_texture_coords;

//Main function to run
void main(){
    //Output color
    pass_texture_coords = vTexture.xy;

    vec4 worldPosition = transformation * vec4(vPosition.xyz, 1.0);
    //Skew by ar (to preserve squareness)
    worldPosition.x /= ar;
    //Skew by aspect ratio
    worldPosition.xy *= scale;
    worldPosition.xy += position;

    gl_Position = worldPosition;
}