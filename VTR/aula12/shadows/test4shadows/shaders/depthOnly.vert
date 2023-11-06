#version 330

uniform mat4 PVM;
uniform mat4 VM;

in vec4 position;

out vec4 pos;

void main(void) {
	gl_Position = PVM * position;
	pos = VM * position;
}
