// This file is part of RogueGL game project
// Copyright (C) 2019 Marek Zalewski aka Drwalin aka DrwalinPCF

#version 400 core

in vec2 position;

out vec2 screenCoord;
out vec2 texCoord;

uniform mat4 cameraMatrix;
uniform vec2 cameraNearFar;

void main( void )
{
	gl_Position = vec4( position, 0, 1 );
	texCoord = (position+1)/2;
	screenCoord = position;
}
