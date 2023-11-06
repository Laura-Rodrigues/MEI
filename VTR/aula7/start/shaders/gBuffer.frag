#version 460

in vec4 p;
in vec3 n,t;
in vec2 tc; 

// same order as in the render target definition
layout (location = 0) out vec4 normal;
layout (location = 1) out vec4 tangent;
layout (location = 2) out vec4 texCoord;
layout (location = 3) out vec4 pos;

void main(){
    normal = normalize(n) * 0.5 + 0.5;
    tangent = normalize(t) * 0.5 + 0.5;

    pos = position;
    texCoord = vec4(tc, 0,0);
}
