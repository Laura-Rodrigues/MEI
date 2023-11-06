#version 430

uniform sampler2D texCoord;
uniform sampler2D texNormal;

uniform sampler2D texColor;

uniform vec3 lightDirection;

uniform mat4 V;

in vec2 texCoordV;
out vec4 colorOut;

void main() {
	
	vec2 texCoord = texture(texCoord, texCoordV).st;
	vec4 color = texture(texColor, texCoord);
	vec4 texN = texture(texNormal, texCoordV);
	vec3 n = texN.xyz * 2.0 - 1.0;
	vec3 ld = vec3(V * vec4(-lightDirection, 0.0));
	
	if (texN.w == 1) {
		float intensity = max(0.0, dot(n, normalize(ld)));
		colorOut = vec4(color.rgb*intensity+0.2*color.rgb, color.a);
	}
	else 
		colorOut = vec4(0);
}
