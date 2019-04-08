// This file is part of RogueGL game project
// Copyright (C) 2019 Marek Zalewski aka Drwalin aka DrwalinPCF

#version 400 core

const int MAX_LIGHT_SOURCES = 16; 

in vec2 texCoord;
in vec2 screenCoord;

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

// returns value in: (0,1)
float LinearizeDepth( vec2 uv )
{
	float n = cameraNearFar.x; // camera z near
	float f = cameraNearFar.y; // camera z far
	float z = texture(cameraDepthBuffer, uv).x;
	return (2 * n) / (f + n - z * (f - n));
}


void main( void )
{
	fragColor = vec4( texture( cameraColorBuffer, texCoord ).rgb, 1 );
}
