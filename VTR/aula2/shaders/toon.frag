#version 460

//uniform
uniform	vec4 diffuse;
uniform mat4 m_view;
uniform	vec4 l_dir;

//in vec4 c;
//in float i;
in vec3 n;

// output
out vec4 color;


void main() {
    vec3 l = normalize (vec3(m_view * -l_dir));
    vec3 nn = normalize(n);
    float i = max(0.0, dot(l,nn));

    float it;
    if(i>0.9) it = 0.9;
    else if(i>0.75) it = 0.75;
    else if(i>0.5) it = 0.5;
    else it = 0.25;



    color = it * diffuse;

}