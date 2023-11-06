#version 330

in vec4 position;

uniform mat4 PVM;

void main () {
    gl_Position = PVM * position;
}