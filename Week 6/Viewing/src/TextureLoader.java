/*
 * Class TextureLoader
 * 
 * A class that can be used for loading of textures from files.
 * This uses the java ImageIO class for file reading, so it should
 * work fine with any image file formats that are supported by
 * ImageIO.
 * 
 * Author: David Wolff, based on original code by Pepijn Van Eeckhoudt
 * 
 * Licensed under the Creative Commons Attribution License 2.5:
 * http://creativecommons.org/licenses/by/2.5/
 */

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

/**
 * Image loading class that converts BufferedImages into a data
 * structure that can be easily passed to OpenGL.
 * @author David Wolff
 */
public class TextureLoader {
	
	/** Attempts to read an image file using ImageIO and return 
	 * the data in a TextureLoader.Texture object.
	 * @param filename the name of the file (can be a jar resource)
	 * @return the Texture object, or null on failure
	 */
	public static Texture readTexture(String filename) throws IOException {
		BufferedImage img = ImageIO.read( TextureLoader.getResourceAsStream(filename) );
		if( img != null )
			return readTexture(img);
		else
			return null;
	}
	
	/** Attempts to read an image from an InputStream and return the data in a 
	 * TextureLoader.Texture object.
	 * @param str the InputStream object to read from.
	 * @return the Texture object, or null on failure
	 */
	public static Texture readTexture( InputStream str ) throws IOException {
		return readTexture( ImageIO.read(str) );
	}

	/** Attempts to find the file as a jar resource.  If not found, tries to
	 * open the file from the system.  Returns an InputStream for the file.
	 * @param filename the name of the file or resource
	 * @return an InputStream that can be used to read from the file
	 */
	private static InputStream getResourceAsStream(final String filename) throws IOException {
		// Try to load resource from jar
		InputStream stream = TextureLoader.class.getResourceAsStream(filename);
		// If not found in jar, then load from disk
		if (stream == null) {
			return new BufferedInputStream( new FileInputStream(filename) );
		} else {
			return stream;
		}
	}

	/** Reads the pixels from the BufferedImage and stores them in a ByteBuffer
	 * within a TextureLoader.Texture object.  The data is stored in the ByteBuffer
	 * as RGBA bytes.
	 * @param img the BufferedImage
	 * @return a Texture object that contains the image data
	 */
	public static Texture readTexture(BufferedImage img) {
		int bytesPerPixel = -1, pix;
		
		// 3 or 4 bytes per pixel?
		if( img.getColorModel().hasAlpha() )
			bytesPerPixel = 4;
		else
			bytesPerPixel = 3;
	
		// Allocate a ByteBuffer
		ByteBuffer unpackedPixels = 
			ByteBuffer.allocateDirect(img.getWidth() * img.getHeight() * bytesPerPixel);
		
		// Pack the pixels into the ByteBuffer in RGBA, 4 byte format.
		for(int row = img.getHeight() - 1; row >= 0; row--) {
			for (int col = 0; col < img.getWidth(); col++) {
				pix = img.getRGB(col,row);  // Should return the pixel in format TYPE_INT_ARGB 
				unpackedPixels.put((byte) ((pix >> 16) & 0xFF));      // red
				unpackedPixels.put((byte) ((pix >> 8 ) & 0xFF));      // green
				unpackedPixels.put((byte) ((pix      ) & 0xFF));      // blue
				if (bytesPerPixel == 4) {
					unpackedPixels.put((byte) ((pix >> 24) & 0xFF));  // alpha
				}
			}
		}
		
		if(bytesPerPixel == 4 ) {
			return new Texture(unpackedPixels,img.getWidth(), img.getHeight(), 
					Texture.TEXTURE_4BYTE_RGBA);
		} else {
			return new Texture(unpackedPixels,img.getWidth(), img.getHeight(), 
					Texture.TEXTURE_3BYTE_RGB);
		}
	}
	
	/** Class for temporary storage of a Texture.  Pixel data is stored in a 
	 * ByteBuffer.  The bytes are organized as either 3 bytes per pixel (RGB),
	 * or 4 bytes per pixel (RGBA).
	 */
	public static class Texture {
		/* Texture types */
		public static final int TEXTURE_3BYTE_RGB = 0;
		public static final int TEXTURE_4BYTE_RGBA = 1;
		
		private ByteBuffer pixels;  // The pixels packed in a byte buffer
		private int width;          // Image width
		private int height;         // Image height
		private int type;           // Data storage type

		/** Creates a new Texture with the given data.
		 * @param pixels the packed pixels
		 * @param width the width of the image
		 * @param height the height of the image
		 * @param type the storage type of the image.  Must be TYPE_3BYTE_RGB 
		 *    or TYPE_4BYTE_RGBA.
		 */
		public Texture(ByteBuffer pixels, int width, int height, int type) {
			this.height = height;
			this.pixels = pixels;
			this.width = width;
			this.type = type;
			this.pixels.rewind();
		}
		
		/** Returns the storage type of this Texture
		 * @return the storage type, value will be one of TYPE_3BYTE_RGB or TYPE_4BYTE_RGBA
		 */
		public int getType() { return type; }

		/** Returns the height of this texture.
		 * @return the height of this texture
		 */
		public int getHeight() {
			return height;
		}

		/** Returns the pixel data for this texture.
		 * @return the pixel data in a ByteBuffer
		 */
		public ByteBuffer getPixels() {
			return pixels;
		}

		/** Returns the width of this texture.
		 * @return the width of this texture
		 */
		public int getWidth() {
			return width;
		}
		
		/**
		 *  Loads the texture object into the given OpenGL context.  Optionally builds
		 * mip-maps and sets the mag filter to linear and the min filter to 
		 * GL_LINEAR or GL_LINEAR_MIPMAP_NEAREST (depending on mipmap).
		 * @param gl a GL context
		 * @param glu a GLU object (used for mipmaps)
		 * @param mipmap whether or not to compute mipmaps
		 * @return the texture's OpenGL ID.
		 */
		public int toGL( GL gl, GLU glu, boolean mipmap ) {
			// Generate the texture ID
			int[] temp = new int[1];
			int name;
			gl.glGenTextures(1, temp, 0);
			name = temp[0];
			
			gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);
			
			// Bind the texture
			gl.glBindTexture( GL.GL_TEXTURE_2D, name );

			// Build Mipmaps 
			if( mipmap ) {
				switch(this.type) {
				case TEXTURE_3BYTE_RGB:
					glu.gluBuild2DMipmaps(GL.GL_TEXTURE_2D, 
							GL.GL_RGB8, 
							this.width, this.height, 
							GL.GL_RGB,
							GL.GL_UNSIGNED_BYTE, 
							this.pixels
						);
					break;
				        
				case TEXTURE_4BYTE_RGBA:
					glu.gluBuild2DMipmaps(GL.GL_TEXTURE_2D, 
							GL.GL_RGBA, 
							this.width, this.height, 
							GL.GL_RGBA, 
							GL.GL_UNSIGNED_BYTE, 
							this.pixels
						);
					break;
				default:
					throw new RuntimeException("Unknown texture type: " + this.type);
				} 
				
				// Assign the mip map levels and texture info
				gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR );
				gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, 
				        GL.GL_LINEAR_MIPMAP_NEAREST);
				gl.glTexEnvf(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_MODULATE);
			} else {
				switch(this.type) {
				case TEXTURE_3BYTE_RGB:
					gl.glTexImage2D(GL.GL_TEXTURE_2D, 
							0, GL.GL_RGB, 
							this.width, this.height, 0,
							GL.GL_RGB, 
							GL.GL_UNSIGNED_BYTE, 
							this.pixels
						);
					break;
				        
				case TEXTURE_4BYTE_RGBA:
					gl.glTexImage2D(GL.GL_TEXTURE_2D,
							0, GL.GL_RGBA,
							this.width, this.height, 0,
							GL.GL_RGBA,
							GL.GL_UNSIGNED_BYTE,
							this.pixels 
						);
					break;
				default:
					throw new RuntimeException("Unknown texture type: " + this.type);
				}
				
				//Assign the mip map levels and texture info
				gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR );
				gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, 
				        GL.GL_LINEAR);
				gl.glTexEnvf(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_MODULATE);
			}
			
			return name;
		}        
	}
}
