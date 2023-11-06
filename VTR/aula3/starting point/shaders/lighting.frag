#version 460

//uniform
uniform vec4 diffuse;
uniform vec4 specular;

uniform vec4 l_dir; // world space
uniform mat4 m_view;
uniform float shininess = 128.0;
uniform vec3 l_spotDir;
uniform float l_spotCutOff;

// input
in vec3 n; // normal
in vec3 e; // eye vector
in vec3 l; // light direction

// output
out vec4 color;

void main() {

   // compute light direction in camera space
    //vec3 l = normalize(vec3(m_view * -l_dir));
    vec3 sd = normalize(vec3(m_view* -vec4(l_spotDir,0)));

    vec3 nn = normalize(n);
    vec3 ll = normalize(l);
    float i = max(0.0, dot(ll,nn)); //intensity

    if (dot(sd,ll) > l_spotCutOff){  // dor cutoff é o cos
        vec3 h = normalize(ll + normalize(e));

        float is = pow(max(0.0, dot(h,nn)), shininess); // intensidade especular
        //specular = is * vec4(1.0);

        //float is = max(dot(h,nn), 0.0);
        color = (i + max(0.25, i)) * diffuse + specular * is;

    }
    else 
        color = max(0.25, 0) * diffuse;
        //color = vec4(e,1.0);
}