// This file is part of RogueGL game project
// Copyright (C) 2019 Marek Zalewski aka Drwalin aka DrwalinPCF

#version 400 core

in vec2 texCoord;
in vec3 surfaceNormal;
in vec3 toLightVector;
in vec3 toCameraVector;

out vec4 fragColor;
out vec4 fragNormal;
out vec4 fragFlatNormal;
out vec4 fragMaterial;

uniform sampler2D textureSampler;

uniform float shineDamper;
uniform float reflectivity;

void main( void )
{
	vec4 textureColorPoint = texture( textureSampler, texCoord );
	
	if( textureColorPoint.a < 0.3 )
		discard;
	
	vec3 unitNormal = normalize(surfaceNormal);
	
	fragColor = textureColorPoint;
	fragFlatNormal = vec4( unitNormal*0.5 + 0.5, 1 );
	fragNormal = vec4( unitNormal*0.5 + 0.5, 1 );
	fragMaterial = vec4( shineDamper/32.0, reflectivity/4.0, 0, 1 );
}
