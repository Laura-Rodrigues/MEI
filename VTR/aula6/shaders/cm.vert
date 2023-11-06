#version 450


// uniform variables
uniform mat4 m_pvm, m_view, m_model, m_viewModel;
uniform mat3 m_normal;
uniform vec4 cam_pos; // world space


// input variables
in vec4 position; // local space
in vec4 normal; // local space


// output variables
out vec3 incident, n;

void main() {

    mat4 mn = inverse(transpose(m_model));
    n = normalize(vec3(mn * normal)); // world space

    vec3 p = vec3(m_model * position); // world
    incident = p - vec3(cam_pos);

    gl_Position = m_pvm * position;
}