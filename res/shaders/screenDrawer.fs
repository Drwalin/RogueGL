
#version 400 core

in vec2 texCoord;
in vec2 screenCoord;

uniform sampler2D colorBuffer;

out vec4 fragColor;

void main( void )
{
	fragColor = vec4( texture( colorBuffer, texCoord ).rgb, 1 );
}
