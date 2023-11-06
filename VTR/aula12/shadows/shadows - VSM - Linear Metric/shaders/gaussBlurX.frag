#version 330

uniform sampler2D moments;

in vec2 texCoordV;

out vec2 blurred;

void main() {

	float delta = 1.0 / textureSize(moments,0).x;
	
	const float weights[7] = float[7](0.00598, 0.060626, 0.241843, 0.383103, 0.241843, 0.060626, 0.00598);
	
	vec2 blur = vec2(0.0);
	
	for (int i=0; i<7; ++i) {
		blur += texture(moments, texCoordV + vec2((i-3)*delta, 0)).xy * weights[i];
	}
	
	blurred = blur;
}
	