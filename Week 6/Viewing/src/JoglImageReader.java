import java.awt.*;
import java.awt.color.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.File;
import java.net.URL;

import javax.imageio.*;

/**
 * Reads an image file and copies the image to a new image with a colour model
 * suitable for OpenGL. The new image is also made larger, so that its
 * dimensions are powers of two. This allows it to be used as a texture.
 *
 * @author phingsto
 *
 */public class JoglImageReader
 {
     public static JoglImageData read(String imagefile) throws Exception
     {
         URL url = new URL("file:"+System.getProperty("user.dir")+"/"+imagefile);
         BufferedImage image = ImageIO.read(url.openStream());
         int w = image.getWidth();
         int h = image.getHeight();
         
         // find next highest power of two for dimensions
         int width = 1;
         while(w > 0)
         {
             w /= 2;
             width *= 2;
         }
         
         int height = 1;
         while(h > 0)
         {
             h /= 2;
             height *= 2;
         }
         
         // convert to a different colour model
         WritableRaster raster =
                 Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,
                 width,
                 height,
                 4,
                 null);
         ComponentColorModel colorModel =
                 new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
                 new int[] {8,8,8,8},
                 true,
                 false,
                 ComponentColorModel.TRANSLUCENT,
                 DataBuffer.TYPE_BYTE);
         BufferedImage img =
                 new BufferedImage(colorModel,
                 raster,
                 false,
                 null);
         
         Graphics2D g = img.createGraphics();
         
         g.drawImage(image, 0, height-image.getHeight(), null);
         
         // pass back an object containing the new image, and what portions of it are useable
         return new JoglImageData(img, image.getWidth()/(double)width, image.getHeight()/(double)height);
     }
 }
