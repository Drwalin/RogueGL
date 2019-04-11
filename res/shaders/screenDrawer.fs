// This file is part of RogueGL game project
// Copyright (C) 2019 Marek Zalewski aka Drwalin aka DrwalinPCF

#version 400 core

const int MAX_LIGHT_SOURCES = 16; 

in vec2 texCoord;
in vec2 screenCoord;

uniform vec3 ambientLight;

uniform sampler2D cameraDepthBuffer;
uniform sampler2D cameraColorBuffer;
uniform sampler2D cameraNormalBuffer;
uniform sampler2D cameraFlatNormalBuffer;
uniform sampler2D cameraMaterialBuffer;

uniform vec3 cameraPosition;
uniform mat4 cameraMatrix;			// invert( projectionMaterix * viewMatrix )

uniform vec3 lightsPosition[MAX_LIGHT_SOURCES]; 
uniform mat4 lightsMatrix[MAX_LIGHT_SOURCES];		// [i] = projectionMaterix * viewMatrix
uniform vec3 lightsColor[MAX_LIGHT_SOURCES];
uniform vec3 lightsAttenuation[MAX_LIGHT_SOURCES];
uniform sampler2D lightsDepthBuffer[MAX_LIGHT_SOURCES];

uniform int currentlyUsedLightSorces;

uniform vec3 cameraNearFarFov;

out vec4 fragColor;



float Linearize( float n, float f, float z )
{
	return (2 * n) / (f + n - z * (f - n));
}

vec3 DecodeLocation( sampler2D depthSampler, vec2 textureCoords )
{
	vec4 clipSpaceLocation;
	clipSpaceLocation.xy = textureCoords * 2.0f - 1.0f;
	clipSpaceLocation.z = texture(depthSampler, textureCoords).r * 2 - 1;
	clipSpaceLocation.w = 1.0f;
	vec4 homogenousLocation = cameraMatrix * clipSpaceLocation;
	return homogenousLocation.xyz / homogenousLocation.w;
}

const vec2 poissonDisk[4] = vec2[](
	vec2( -0.94201624, -0.39906216 ),
	vec2( 0.94558609, -0.76890725 ),
	vec2( -0.094184101, -0.92938870 ),
	vec2( 0.34495938, 0.29387760 )
);

float GetDepthValue( sampler2D sampler, vec2 uv )
{
	vec2 textureSize = textureSize(sampler,0);
	vec2 d = vec2( 1.0/textureSize.x, 1.0/textureSize.y );
	
	float value = texture( sampler, uv ).r;
	for( int i = 0; i < 4; ++i )
		value += texture( sampler, uv + poissonDisk[i]*d ).r;
	return value/5;
}

mat3 ProcessLightSource( int id, sampler2D depthSampler, vec3 normal, float shineDamper, float reflectivity, vec3 worldPoint, vec3 lightColor, vec3 lightAttenuation, vec3 unitVectorToCamera, vec3 toLightVector )
{
	mat4 lightMatrix = lightsMatrix[id];
	vec4 inLightPoint = ( lightMatrix * vec4( worldPoint, 1 ) );
	inLightPoint.xyz /= inLightPoint.w;
	inLightPoint.xyz = inLightPoint.xyz * 0.5 + 0.5;
	float depthValue = GetDepthValue( depthSampler, inLightPoint.xy );
	
	if( inLightPoint.z-0.00007 < depthValue && inLightPoint.x > 0 && inLightPoint.y > 0 && inLightPoint.x < 1 && inLightPoint.y < 1 )
	{
		float distance = length(toLightVector);
		float attFactor = lightAttenuation.x + (lightAttenuation.y * distance) + (lightAttenuation.z * (distance * distance));
		vec3 unitLightVector = normalize( toLightVector );
		
		float brightness = dot( normal, unitLightVector );
		brightness = ( brightness + 0.5 ) / 1.5;
		if( brightness <= 0 )
			return mat3(vec3(0),vec3(0),vec3(0));
		brightness = max( brightness, 0 ); 
		vec3 totalDiffuse = lightColor * (brightness / attFactor);
		
		vec3 reflectedLightDirection = reflect( -unitLightVector, normal );
		float specularFactor = dot( reflectedLightDirection, unitVectorToCamera );
		if( specularFactor <= 0 )
			return mat3(totalDiffuse,vec3(0),vec3(0));
		
		float dampedFactor = pow( specularFactor, shineDamper );
		vec3 totalSpecular = lightColor * (dampedFactor * reflectivity / attFactor);
		
		return mat3(totalDiffuse,totalSpecular,vec3(0));
	}
	return mat3(vec3(0),vec3(0),vec3(0));
} 

void main( void )
{
	vec3 color = texture( cameraColorBuffer, texCoord ).rgb;
	vec3 normal = normalize( texture( cameraNormalBuffer, texCoord ).xyz * 2 - 1 );
	vec3 flatNormal = normalize( texture( cameraFlatNormalBuffer, texCoord ).xyz * 2 - 1 );
	vec3 material = texture( cameraMaterialBuffer, texCoord ).xyz;
	float shineDamper = material.x * 32;
	float reflectivity = material.y * 4;
	
	vec3 worldPoint = DecodeLocation( cameraDepthBuffer, texCoord );
	vec3 diffuse = vec3(0);
	vec3 specular = vec3(0);
	
	vec3 unitVectorToCamera = normalize(cameraPosition - worldPoint);
	
	for( int i = 0; i < currentlyUsedLightSorces; ++i )
	{
		vec3 toLightVector = lightsPosition[i] - worldPoint;
		mat3 diffSpec = ProcessLightSource( i, lightsDepthBuffer[i], normal, shineDamper, reflectivity, worldPoint, lightsColor[i], lightsAttenuation[i], unitVectorToCamera, toLightVector );
		float d = dot( normalize( toLightVector ), flatNormal );
		if( d > 0 )
		{
			diffuse += max( diffSpec[0], vec3(0) );
			specular += max( diffSpec[1], vec3(0) );
		}
		else 
		{
			d *= 10;
			if( d > -1 )
			{
				if( d < -0.5 )
				{
					float t = (d+1);
					d = 0.1 - 1.6*t*t;
				}
				else
					d = 1 - (4*d*d);
				diffuse += max( diffSpec[0]*d, vec3(0) );
				specular += max( diffSpec[1]*d, vec3(0) );
			}
		}
	}
	
	fragColor = vec4( color * (diffuse+ambientLight) + specular, 1 );
}
