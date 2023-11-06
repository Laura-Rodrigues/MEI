#version 460

// uniforms
uniform mat4 m_pvm;
uniform mat3 m_normal;

// input streams
in vec4 position;
in vec2 texCoord0;
in vec3 normal;

// output
out vec2 tc;
out vec3 n;


void main() {
    tc = texCoord0;
    gl_Position = m_pvm * position;
    n = normalize(m_normal*normal);
}