#version 330

uniform float near, far;
in vec4 pos;

out float depth;

void main(void) {

	// 1000.0 is an approximation to the max distance
	//depth = exp(k * length(vec3(pos))/1000.0);
	depth = length(vec3(pos)) / (far-near);
}