#version 460

uniform mat4 m_pvm;     // Local -> Clip Space
uniform mat3 m_normal;  // Local -> Camera Space
uniform mat4 m_view;    // World -> Camera Space
uniform vec4 l_dir;     // World Space
uniform vec4 diffuse;

in vec4 position;       // Local Space
in vec3 normal;         // Local Space

out vec4 cor;

void main() {

    vec3 n = normalize(m_normal * normal);     // Camera Space
    vec3 ld = normalize(vec3(m_view * -l_dir)); // Camera Space

    float intensity = max(0.0, dot(n,ld));     // dot operacao comutativa (cosseno se os vetores estiverem normalizados)
    
    cor = diffuse * max(0.3, intensity);

    gl_Position = m_pvm * position;
}