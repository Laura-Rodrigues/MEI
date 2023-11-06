#version 330

uniform sampler2D blurredMoments;
uniform sampler2D texUnit;
uniform vec4 diffuse;
uniform int texCount;
uniform float minVariance = 0.0002;


in vec2 texCoordV;
in vec4 projShadowCoord;
in vec3 normalV, lightDirV;
in vec4 pos, lightPosV;

out vec4 outColor;

void main()
{
	float shadowTerm=0;
	vec4 color, diff;

	if (texCount  != 2)
		diff = texture(texUnit, texCoordV) ;
	else 
		diff = diffuse;
		
	// ambient term = shadowed	
	color = diff * 0.25;
	
	vec3 n = normalize (normalV);
	
	float NdotL = max(0.0,dot (n, lightDirV));
	

	vec2 moments;
	float p=0;
	if (NdotL > 0.0) {
		float lightDist = length(vec3(lightPosV - pos));
		vec4 proj = projShadowCoord / projShadowCoord.w; 
		moments = texture(blurredMoments, proj.xy).rg;
		if (lightDist <= moments.x) 
			shadowTerm = 1.0;
		else {
			float var = max(minVariance, moments.y - (moments.x * moments.x));
			float diff = lightDist - moments.x;
			shadowTerm = var / (var + diff*diff);
			shadowTerm = smoothstep(0.001, 1, shadowTerm);
		}
		color += diff  * NdotL * (1-shadowTerm) ;
	}
	
	outColor = vec4(moments, 0, 1.0);
}
