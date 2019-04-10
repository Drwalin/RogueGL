// This file is part of RogueGL game project
// Copyright (C) 2019 Marek Zalewski aka Drwalin aka DrwalinPCF

#version 400 core

const int MAX_LIGHT_SOURCES = 16; 

in vec2 texCoord;
in vec2 screenCoord;
in vec3 fromCamera;

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


float Linearize( float n, float f, float z )
{
	return (2 * n) / (f + n - z * (f - n));
}

// returns value in: (0,1)
float LinearizeDepth( vec2 uv )
{
	float n = cameraNearFar.x; // camera z near
	float f = cameraNearFar.y; // camera z far
	float z = texture(cameraDepthBuffer, uv).x;
	return (2 * n) / (f + n - z * (f - n));
}

// returns value in: (0,1)
float LinearizeDepthLight( sampler2D sampler, vec2 uv )
{
	float n = 0.1; // camera z near
	float f = 300; // camera z far
	float z = texture(sampler, uv).x;
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

float ShadowCalculation( sampler2D sampler, mat4 matrix, vec4 _fragPosLightSpace )
{
	vec4 fragPosLightSpace = matrix * _fragPosLightSpace;
    vec3 projCoords = fragPosLightSpace.xyz / fragPosLightSpace.w;
    projCoords = projCoords * 0.5 + 0.5;
    float closestDepth = texture(sampler, projCoords.xy).r;
    float shadow = projCoords.z-0.0001 < closestDepth  ? 1.0 : 0.0;

    return shadow;
}  

void main( void )
{
	vec3 color = texture( cameraColorBuffer, texCoord ).rgb;
	
	vec3 light = vec3(0.2);
	
	vec3 worldPoint = DecodeLocation( cameraDepthBuffer, texCoord );
	
	//for( int i = 0; i < currentlyUsedLightSorces; ++i )
	int i = 0;
	//{
		vec4 inLightPoint = ( lightsMatrix[i] * vec4( worldPoint, 1 ) );
		inLightPoint.xyz /= inLightPoint.w;
		inLightPoint.xyz = inLightPoint.xyz * 0.5 + 0.5;
		float depthValue = texture( lightsDepthBuffer[i], inLightPoint.xy/100 ).x;
		
		if( ShadowCalculation( lightsDepthBuffer[i], lightsMatrix[i], vec4(worldPoint,1) ) > 0.5 && inLightPoint.x > 0 && inLightPoint.y > 0 && inLightPoint.x < 1 && inLightPoint.y < 1 )
		{
//			light += vec3(1) * (abs(inLightPoint.z));//( 1.001 - Linearize(0.1,300,inLightPoint.z) );
//			light += vec3( 1-Linearize(0.1,300,inLightPoint.z) );
			light += vec3(0.4);
		}
		else
		{
//			light = vec3(0);
		}
	//}
	
	fragColor = vec4( color * vec3(light), 1 );
	
//	if( screenCoord.x < 0 )
//		fragColor = vec4( vec3(Linearize( 0.1, 300, depthValue )), 1 );
	//	fragColor = vec4( vec3(1/depthValue), 1 );
//	else
	//	fragColor = vec4( vec3(1/inLightPoint.z), 1 );
//		fragColor = vec4( vec3(-300*Linearize( 0.1, 300, inLightPoint.z )), 1 );
	
	
//	fragColor = vec4( vec3( depthValue ), 1 );
}
