#version 330

uniform sampler2D moments;

in vec2 texCoordV;

out float blurred;

void main() {

	float delta = 1.0 / textureSize(moments,0).y;
	
	const float weights[7] = float[7](0.00598, 0.060626, 0.241843, 0.383103, 0.241843, 0.060626, 0.00598);
	
	float blur = 0.0;
	
	for (int i=0; i<7; ++i) {
		blur += texture(moments, texCoordV + vec2(0, (i-3)*delta)).x * weights[i];
	}
	
	blurred = blur;
}
	