#version 330

uniform mat4 lightSpaceMat;
uniform vec4 lightPos, lightDirection;
uniform mat4 PVM;
uniform mat4 V, M;
uniform mat3 NormalMatrix;

in vec4 position;
in vec4 normal;
in vec4 texCoord0;

out vec4 projShadowCoord;
out vec3 normalV, lightDirV;
out vec2 texCoordV;
out vec4 lightPosV;
out vec4 pos;



void main() 
{
	lightPosV = V*M*lightPos ;
	lightDirV = normalize(- vec3(V * lightDirection));
	normalV = normalize (NormalMatrix * vec3(normal));
	texCoordV = vec2(texCoord0);
			
	projShadowCoord = lightSpaceMat * M * position;
	pos = V*M * position;
	gl_Position = PVM * position;
} 
