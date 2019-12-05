#version 100
// Fragment shader which takes interpolated colours and uses them to set the final fragment colour.
// Floating point values in fragment shaders must have a precision set.
// This can be done globally (as done here) or per variable.
precision mediump float;
// Inputs
varying vec3 passNormal;
varying vec3 cameraDir;
varying vec3 interpolated_color;



float dotProduct(vec3 posI, vec3 posJ){
    return (posI.x * posJ.x) + (posI.y * posJ.y) + (posI.z * posJ.z);
}

void main()
{
//    vec2 normal = gl_FragCoord.xy / gl_FragCoord.w;
//    gl_FragColor = vec4((sin(gl_FragCoord.x / 10.0) + 1.0) / 2.0, (sin(gl_FragCoord.y / 10.0) + 1.0) / 2.0, 1.0, 1.0);

//    gl_FragColor = vec4(dotProduct(passNormal, cameraDir) *passNormal, 1.0);

      gl_FragColor = vec4(interpolated_color, 1.0);
//    gl_FragColor = vec4(cameraDir, 1.0);

}