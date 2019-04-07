
#version 400 core

in vec3 position;
in vec2 textureCoords;
in vec3 normal;

out vec2 texCoord;
out vec3 surfaceNormal;
out vec3 toLightVector;
out vec3 toCameraVector;

uniform mat4 worldTransform;
uniform mat4 viewTransform;
uniform mat4 projectionTransform;
uniform mat4 combinedTransform;

uniform vec3 lightPossition;

uniform vec3 cameraPossition;

uniform int useFakeLighting;

uniform int drawingShadow;

void main( void )
{
	vec4 worldPossition = worldTransform*vec4(position,1);
	gl_Position = combinedTransform * vec4(position,1);
	texCoord = textureCoords;
	
	if( drawingShadow == 0 )
	{
		vec3 currentNormal;
		if( useFakeLighting == 1 )
			currentNormal = vec3(0,1,0);
		else
			currentNormal = normal;
		
		surfaceNormal = (worldTransform * vec4(currentNormal,1)).xyz - (worldTransform * vec4(0,0,0,1)).xyz;
		toLightVector = lightPossition - worldPossition.xyz;
		toCameraVector = cameraPossition - worldPossition.xyz;
	}
}
