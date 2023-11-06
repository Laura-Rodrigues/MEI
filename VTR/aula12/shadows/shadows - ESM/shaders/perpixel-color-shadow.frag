#version 330

uniform sampler2D blurredExp;
uniform sampler2D texUnit;
uniform vec4 diffuse;
uniform vec4 lightDirection;
uniform int texCount;
uniform float k;
uniform float near, far;

in vec2 texCoordV;
in vec4 projShadowCoordV;
in vec3 normalV, lightDirV;
in vec4 posV, lightPosV;

out vec4 outColor;

void main()
{
	float shadowTerm=1;
	vec4 color, diff;

	if (texCount  != 2)
		diff = texture(texUnit, texCoordV) ;
	else 
		diff = diffuse;
		
	// ambient term = shadowed	
	color = diff * 0.25;
	
	vec3 n = normalize (normalV);
	
	float NdotL = max(0.0,dot (n, lightDirV));
	float expz = 0.0;

	vec2 exp_ = vec2(0.0, 0.0);
	if (NdotL > 0.0) {
		float lightDist = length(vec3(lightPosV - posV))/(far - near);
		vec4 proj = projShadowCoordV / projShadowCoordV.w; 
		exp_ = texture(blurredExp, proj.xy).rg;
		expz = texture(blurredExp, proj.xy).x;
		shadowTerm = exp(-k * (expz-lightDist) )  ;
		shadowTerm = clamp(shadowTerm, 0 , 1) ;
		color += diff  * NdotL * shadowTerm ;
	}
	
	outColor = vec4(exp_,0, 1);
}
