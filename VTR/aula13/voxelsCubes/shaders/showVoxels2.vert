#version 440

in vec4 position;
in vec3 normal;

layout(std430, binding = 1) buffer PosBuffer {
	vec4 cubePosition[];
};

uniform mat4 PVM;
uniform int GridSize;
uniform int level = 0;

out vec3 normalV;
flat out ivec3 posW;

void main()
{
	ivec4 pos;
	vec4 posF;
	int inst = gl_InstanceID;
	pos = ivec4(cubePosition[inst]);
	posF = pos + position * 0.5;
	posF /= GridSize/(pow(2,level));
	posF = 2 * posF - vec4(1.0, 1.0, 1.0, 0);
	posF.w = 1;
    gl_Position = PVM * posF;
	posW = pos.xyz;
	normalV = normal;
}
