#version 430

uniform int texCount;
uniform sampler2D texUnit;
uniform vec4 diffuse;

in vec3 Normal;
in vec2 TexCoord;

layout (location = 0) out vec4 outNormal;
layout (location = 1) out vec4 outColor;


void main()
{
	outNormal = vec4(normalize(Normal)*0.5+0.5, 1.0);
	
	vec4 auxColor;
	if (texCount != 0)
		auxColor = texture(texUnit, TexCoord);
	else
		auxColor = vec4(fract(TexCoord.s), fract(TexCoord.t), 0, 1);
	outColor = vec4(auxColor);
}
