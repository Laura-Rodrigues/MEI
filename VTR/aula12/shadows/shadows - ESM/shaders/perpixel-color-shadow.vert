#version 330

uniform mat4 lightSpaceMat;
uniform vec4 lightPos, lightDirection;
uniform mat4 PVM;
uniform mat4 V, M;
uniform mat3 NormalMatrix;

in vec4 position;
in vec4 normal;
in vec4 texCoord0;

out vec4 projShadowCoordV;
out vec3 normalV;
out vec2 texCoordV;
out vec3 lightDirV;
out vec4 lightPosV;
out vec4 posV;


void main() 
{
	posV = position;
	lightPosV = lightPos ;
	lightDirV = normalize (vec3(V * -lightDirection)) ;
	normalV = normalize (NormalMatrix * vec3(normal));
	texCoordV = vec2(texCoord0);
			
	projShadowCoordV = lightSpaceMat * M * position;
	gl_Position = PVM * position;
} 
