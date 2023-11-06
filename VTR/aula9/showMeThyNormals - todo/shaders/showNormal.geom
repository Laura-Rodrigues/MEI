#version 420
 
layout(triangles) in;
layout (line_strip, max_vertices=6) out;

uniform mat4 m_pvm;

in Data {
	vec3 normal;
} DataIn[3];

out vec4 color;

void main() {

	color=vec4(1,0,0,0);
	for(int i=0; i<3; ++i){
        gl_Position = m_pvm * gl_in[i].gl_Position;
    	EmitVertex();

    	gl_Position = m_pvm * (gl_in[i].gl_Position + vec4(DataIn[i].normal, 0.0));
    	EmitVertex();
    	EndPrimitive();
    }
	
}

