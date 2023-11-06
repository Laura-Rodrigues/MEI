#version 440

uniform sampler3D grid;
uniform int level = 0;

in vec3 normalV;
flat in ivec3 posW;

out vec4 outColor;

void main()
{
	outColor = vec4(1,0,0,0);
	// vec3(1,2,3) is just an example of a light direction
	float intensity = max(0.0, dot(normalV, normalize(vec3(1,2,3))));	
	outColor = vec4(texelFetch(grid, ivec3(posW), level) * (intensity + 0.5));
}