#version 450

// uniforms
uniform sampler2D tex;
uniform sampler2D tex2;
uniform mat4 m_view;
uniform vec4 l_dir;
//uniform vec4 c1 = vec4(0.3,0.3,1.0,1);
//uniform vec4 c2 = vec4(0.9,0.9,0.9,1);

// interpolated inputs
in vec2 tc;
in vec3 n;

// output
out vec4 color;

void main() {

 /*   float k = 0.5;
    float gap = 0.01;
    int div = 10;
    vec2 ft = fract(div * tc);
    vec2 deriv = vec2(dFdx(ft.s), dFdy(ft.s));
    gap = 2.5 * length(deriv);
    float f;
    f = smoothstep(0.5-gap,0.5, ft.s) - smoothstep(1.0-gap,1.0,ft.s);
    color = mix(c1,c2,f);


    if (ft.s < 0.4)
        color = c1;
    else if (ft.s < 0.5) {
        f = (ft.s - 0.4) * div;
        color = mix(c1,c2,f);
        //color = (1-f) * c1 + f * c2;
    }
    else if (ft.s < 0.9)
        color = c2;
    else {
        f = (ft.s - 0.9) * div;
        color = mix(c2,c1,f);
        //color = (1-f) * c2 + f * c1;                    // para representar o teapot
    }
*/

    vec3 nn = normalize(n);
    vec3 l = normalize(vec3(m_view * l_dir));

    float f = max(dot(l,nn),0);

    vec4 t1 = texture(tex,tc);
    vec4 t2 = texture(tex2,tc);
    
    //color = mix(t2,t1,f);
    color = max(0.3,f) *t1 + pow((1-f), 5) * t2;
    
}