
#version 400 core

in vec2 position;

out vec2 screenCoord;
out vec2 texCoord;

void main( void )
{
	gl_Position = vec4( position, 0, 1 );
	texCoord = (position+1)/2;
}
