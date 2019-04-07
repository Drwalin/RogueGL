
#version 400 core

in vec3 position;
in vec2 textureCoords;
in vec3 normal;
in vec3 tangent;

out vec2 texCoord;
out vec3 toLightVector;
out vec3 toCameraVector;
out vec3 norm_;
out vec3 tang_;

uniform mat4 worldTransform;
uniform mat4 combinedTransform;

uniform vec3 lightPossition;

uniform vec3 cameraPossition;

uniform int useFakeLighting;

uniform int drawingShadow;

void main( void )
{
	vec4 worldPosition = worldTransform * vec4(position,1);
	gl_Position = combinedTransform * vec4(position,1);
	texCoord = textureCoords;
	
	if( drawingShadow == 0 )
	{
		vec3 currentNormal;
		if( useFakeLighting == 1 )
			currentNormal = vec3(0,1,0);
		else
			currentNormal = normal;
		
		norm_ = (worldTransform * vec4(currentNormal,0)).xyz;
		tang_ = (worldTransform * vec4(tangent, 0)).xyz;
		
		toLightVector = ( ( vec4(lightPossition,1) ).xyz - worldPosition.xyz );
		toCameraVector = cameraPossition - worldPosition.xyz;
	}
}
