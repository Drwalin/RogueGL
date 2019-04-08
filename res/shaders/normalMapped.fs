
#version 400 core

in vec2 texCoord;
in vec3 toLightVector;
in vec3 toCameraVector;
in vec3 norm_;
in vec3 tang_;

out vec4 fragColor;
out vec4 fragNormal;
out vec4 fragMaterial;

uniform sampler2D textureSampler;
uniform sampler2D normalSampler;

uniform vec3 lightAttenuation;
uniform vec3 lightColor;
uniform float shineDamper;
uniform float reflectivity;

uniform vec3 ambientLightColor;

uniform int drawingShadow;

vec4 GetNormalVectorFromMap( vec2 coord )
{
	return texture( normalSampler, vec2(coord.x,1-coord.y), -3.11 ) * 2 - 1;
}


void main( void )
{
	vec4 textureColorPoint = texture( textureSampler, vec2(texCoord.x,1-texCoord.y) );
	if( textureColorPoint.a < 0.3 )
		discard;
	
	if( drawingShadow == 0 )
	{
		vec3 tang = normalize( tang_ );
		vec3 norm = normalize( norm_ );
		vec3 bitang = normalize( cross(norm,tang) );
		
		mat3 toTangentSpace = mat3(
			tang.x, tang.y, tang.z,
			bitang.x, bitang.y, bitang.z,
			norm.x, norm.y, norm.z
		);
		
		vec3 unitNormal = normalize( toTangentSpace * GetNormalVectorFromMap(texCoord).xyz );
		
		vec3 unitVectorToCamera = normalize( toCameraVector );
		
		vec3 totalDiffuse = vec3(0,0,0);
		vec3 totalSpecular = vec3(0,0,0);
		
		float distance = length(toLightVector);
		float attFactor = lightAttenuation.x + (lightAttenuation.y * distance) + (lightAttenuation.z * (distance * distance));
		vec3 unitLightVector = normalize( toLightVector );	
		float nDotl = dot( unitNormal, unitLightVector );
		float brightness = max( nDotl, 0 );
		vec3 lightDirection = -unitLightVector;
		vec3 reflectedLightDirection = reflect( lightDirection, unitNormal );
		float specularFactor = dot( reflectedLightDirection , unitVectorToCamera );
		specularFactor = max( specularFactor, 0.0 );
		float dampedFactor = pow( specularFactor, shineDamper );
		totalDiffuse += lightColor * ( brightness / attFactor );
		totalSpecular += lightColor * ( dampedFactor * reflectivity /attFactor );
		
		totalDiffuse = max(totalDiffuse, ambientLightColor);
	
		fragColor = vec4(totalDiffuse,1) * textureColorPoint + vec4(totalSpecular,0);
		
		
		//fragColor = textureColorPoint;
		
		fragNormal = vec4( unitNormal*0.5 + 0.5, 1 );
		fragMaterial = vec4( shineDamper/32, reflectivity/4, 0, 1 ); 
	}
	else
	{
		// ...
	}
}
