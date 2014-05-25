import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

public class JoglImageData
{
    public BufferedImage buff;
    public double xProp;
    public double yProp;
    
    public JoglImageData(BufferedImage buff, double xProp, double yProp)
    {
        this.buff = buff;
        this.xProp = xProp;
        this.yProp = yProp;
    }
}
