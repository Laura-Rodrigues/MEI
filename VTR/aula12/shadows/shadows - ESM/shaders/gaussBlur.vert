#version 330

uniform mat4 PVM;

in vec4 position;
in vec2 texCoord0;

out vec2 texCoordV;

void main() {
	
	texCoordV = texCoord0;
	gl_Position = PVM * position;
}