
#version 400 core

in vec2 texCoord;
in vec3 surfaceNormal;
in vec3 toLightVector;
in vec3 toCameraVector;

out vec4 fragColor;

uniform sampler2D textureSampler;

uniform vec3 lightColor;
uniform float shineDamper;
uniform float reflectivity;

uniform vec3 ambientLightColor;

uniform int isTwoSided;

uniform int drawingShadow;

void main( void )
{
	vec4 textureColorPoint = texture( textureSampler, texCoord );
	
	if( textureColorPoint.a < 0.3 )
		discard;
	
	if( drawingShadow == 0 )
	{
		vec3 unitNormal = normalize(surfaceNormal);
		vec3 unitToLightVector = normalize(toLightVector);
		vec3 unitToCameraVector = normalize(toCameraVector);
		
		float diffuseBrightness = max( 0.2, dot( unitNormal, unitToLightVector ) );
		
		float specularBrightness = abs( dot( reflect( -unitToLightVector, unitNormal ), unitToCameraVector ) );
		specularBrightness = pow( max( 0, specularBrightness ), shineDamper );
		
		vec3 diffuse = max( lightColor * diffuseBrightness, ambientLightColor );
		
		fragColor = textureColorPoint * vec4(diffuse,1.0) + vec4( lightColor * ( specularBrightness * reflectivity ), 0 );
	}
	else
	{
		// ...
	}
}
