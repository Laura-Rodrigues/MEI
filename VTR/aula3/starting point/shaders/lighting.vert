#version 460

// uniforms
uniform mat4 m_pvm;
uniform mat3 m_normal;
uniform mat4 m_viewModel;
uniform mat4 m_view;
uniform vec4 l_pos;

// input streams - local space
in vec4 position;
in vec3 normal;

//output
out vec3 n; // normal in camera space
out vec3 e; // eye vector in camera space
out vec3 l; // light direction in camera space

void main() {

    // normalize to ensure correct direction when interpolating
    n = normalize(m_normal * normal);
    // create eye vector in camera space. not normalized
    e = - vec3(m_viewModel * position); // e = -p
    l = vec3(m_view * l_pos - (m_viewModel * position));

    
    gl_Position = m_pvm * position;
}