#version 440

layout (binding = 1, rgba8) uniform coherent image3D imageUnit;

uniform vec2 WindowSize;
uniform vec4 diffuse;
uniform int texCount;
uniform sampler2D texUnit;


in vec4 worldPos;
in vec4 bBox;
in vec4 colorG;
in vec2 texCoordG;

out vec4 colorOut;


void main() {

	vec4 color;
	if (texCount == 0) 
		color = vec4(diffuse.xyz, 1.0);
	else 
		color = vec4(texture(texUnit, texCoordG).xyz, 1.0);

	// convert from [-1,1] (model is unitized) to [0,imagesize] (texture coordinates)
	ivec3 pos = ivec3((worldPos.xyz + 1) * 0.5 * imageSize(imageUnit));
	
	if (all(greaterThanEqual(-1+2*gl_FragCoord.xy/WindowSize, bBox.xy)) && all(lessThanEqual(-1+2*gl_FragCoord.xy/WindowSize, bBox.zw))) {
		// use color for real color or colorG to identify the camera used to voxelize the triangle		
		imageStore(imageUnit, pos, color);
	 }
	 else 
		 discard;

	colorOut=color;
}