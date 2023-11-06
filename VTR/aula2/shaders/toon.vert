#version 460

// uniforms
uniform mat4 m_pvm;
uniform mat3 m_normal;
uniform mat4 m_view;
uniform	vec4 l_dir;	   // world space
//uniform vec4 diffuse;


// input streams
in vec4 position;	// local space
in vec3 normal;		// local space


// output
//out vec4 c;
//out float i;
out vec3 n; 

void main () {

    n = normalize (m_normal * normal);
    //vec3 l = normalize (vec3(m_view * -l_dir));


    //i = max(0.0, dot(l,n));


    gl_Position = m_pvm * position;
}