// This file is part of RogueGL game project
// Copyright (C) 2019 Marek Zalewski aka Drwalin aka DrwalinPCF

#version 400 core

const int MAX_LIGHT_SOURCES = 16; 

in vec2 texCoord;
in vec2 screenCoord;
in vec3 toCamera;

uniform sampler2D cameraColorBuffer;
uniform sampler2D cameraDepthBuffer;
uniform sampler2D cameraNormalBuffer;
uniform sampler2D cameraMaterialBuffer;

uniform vec3 cameraPosition;
uniform mat4 cameraMatrix;			// invert( projectionMaterix * viewMatrix )

uniform vec3 lightsPosition[MAX_LIGHT_SOURCES]; 
uniform mat4 lightsMatrix[MAX_LIGHT_SOURCES];		// [i] = projectionMaterix * viewMatrix
uniform vec3 lightsColor[MAX_LIGHT_SOURCES];
uniform vec3 lightsAttenuation[MAX_LIGHT_SOURCES];
uniform sampler2D lightsDepthBuffer[MAX_LIGHT_SOURCES];

uniform int currentlyUsedLightSorces;

uniform vec2 cameraNearFar;

out vec4 fragColor;


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

float ShadowCalculation( vec3 normal, vec3 toLight, sampler2D sampler, mat4 matrix, vec4 _fragPosLightSpace )
{
	vec4 fragPosLightSpace = matrix * _fragPosLightSpace;
    vec3 projCoords = fragPosLightSpace.xyz / fragPosLightSpace.w;
    projCoords = projCoords * 0.5 + 0.5;
    float closestDepth = GetDepthValue(sampler, projCoords.xy);
    float bias = 0.00007;  
    float shadow = projCoords.z-bias < closestDepth  ? 1.0 : 0.0;

    return shadow;
}

void main( void )
{
	vec3 color = texture( cameraColorBuffer, texCoord ).rgb;
	vec3 normal = normalize( texture( cameraNormalBuffer, texCoord ).xyz * 2 - 1 );
	
	vec3 light = vec3(0.2);
	
	vec3 worldPoint = DecodeLocation( cameraDepthBuffer, texCoord );
	
	for( int i = 0; i < currentlyUsedLightSorces; ++i )
	{
		vec4 inLightPoint = ( lightsMatrix[i] * vec4( worldPoint, 1 ) );
		float pointDistance = inLightPoint.z; 
		inLightPoint.xyz /= inLightPoint.w;
		inLightPoint.xyz = inLightPoint.xyz * 0.5 + 0.5;
		float depthValue = texture( lightsDepthBuffer[i], inLightPoint.xy ).r;
		vec3 toLightVector = lightsPosition[i] - worldPoint;
		
		if( ShadowCalculation( normal, toLightVector, lightsDepthBuffer[i], lightsMatrix[i], vec4(worldPoint,1) ) > 0.5 && inLightPoint.x > 0 && inLightPoint.y > 0 && inLightPoint.x < 1 && inLightPoint.y < 1 )
		{
			light += vec3(0.4);
		}
	}
	
	fragColor = vec4( color * vec3(light), 1 );
}
