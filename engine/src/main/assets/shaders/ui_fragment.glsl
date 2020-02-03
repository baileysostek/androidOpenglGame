#version 100
// Fragment shader which takes interpolated colours and uses them to set the final fragment colour.
// Floating point values in fragment shaders must have a precision set.
// This can be done globally (as done here) or per variable.
precision mediump float;
// Inputs
varying vec2 pass_texture_coords;

uniform sampler2D texureID;
uniform float alpha;

void main(){
    gl_FragColor = texture2D(texureID, pass_texture_coords);
    if(gl_FragColor.a < 0.5){
        discard;
    }
    gl_FragColor.a = clamp(alpha, 0.0, 1.0);
//    gl_FragColor = vec4(1, 0, 0, 1);
}