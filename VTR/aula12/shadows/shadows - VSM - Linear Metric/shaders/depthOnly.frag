#version 330

uniform float derivWeight;

in vec4 pos;

out vec2 moments;

void main(void) {
	float depth = length(vec3(pos));
	
	float dx = dFdx(depth);
	float dy = dFdy(depth);

	float d2 = depth*depth + derivWeight * (dx*dx + dy*dy); 

	moments = vec2(depth, d2);
}
