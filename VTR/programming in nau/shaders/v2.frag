#version 460

// adding a uniform with the diffuse color
uniform vec4 diffuse;

out vec4 color;

void main() {

    color = diffuse;
}