import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;

import com.sun.opengl.util.GLUT;

import au.edu.ecu.jogl.*;

/*
 * A panel that uses openGL to draw an image.
 *
 * Draws the image using textures, so that we can do zooming and panning.
 *
 * Your job is to implement the zooming and panning routines.
 *
 * @author phingsto
 */
public class ImagePanel extends SimpleGLPanel implements SimpleGLEventListener
{
    // initial size of the world in world coordinates
    private static final double WIDTH = 100.0;
    private static final double HEIGHT = 100.0;
    
    // initially, world coordinates: 0-WIDTH x 0-HEIGHT
    private double xwmin = 0.0;
    private double xwmax = WIDTH;
    private double ywmin = 0.0;
    private double ywmax = HEIGHT;
    
    // image stuff
    private boolean loaded = false; // whether the image is loaded
    private JoglImageData imageData;	// the image and info about it
    private TextureLoader.Texture tex;	// structure to store image as a texture
    private int texture;	// an ID for the texture
    private String error;	// error in case of problems loading
    
    private GLU glu = new GLU();
    private GLUT glut = new GLUT();
    
    // Here are the methods you must implement.
    //
    // the clipping/viewing window will be set with the call
    // gluOrtho2D(xwmin, xwmax, ywmin, ywmax)
    //
    // You must set the values of xwmin, xwmax, ywmin, ywmax so
    // that the desired effect is achieved
    //
    
    /* factor is the zoom factor
     * - e.g. factor = 2 should make the image appear twice as large
     */
    public void zoomIn(double factor)
    {
        // This one is done for you as an example
        
        synchronized(this)
        {
            // find centre
            double centrex = (xwmin + xwmax)/2;
            double centrey = (ywmin + ywmax)/2;
            
            // calculate new width and height
            double width = (xwmax-xwmin)/factor;
            double height = (ywmax-ywmin)/factor;
            
            // we want to leave the centre unchanged
            // and change the width and height
            xwmin = centrex - width/2;
            xwmax = xwmin + width;
            ywmin = centrey - height/2;
            ywmax = ywmin + height;
        }
    }
    
    public void zoomOut(double factor)
    {
         synchronized(this)
        {
            // find centre
            double centrex = (xwmin + xwmax)/2;
            double centrey = (ywmin + ywmax)/2;
            
            // calculate new width and height
            double width = (xwmax-xwmin)*factor;
            double height = (ywmax-ywmin)*factor;
            
            // we want to leave the centre unchanged
            // and change the width and height
            xwmin = centrex - width / 2;
            xwmax = xwmin + width;
            ywmin = centrey - height / 2;
            ywmax = ywmin + height;
        }
    }
    
    /* factor is what fraction of the image to pan by
     * e.g. factor = 0.1 means 10% of the image 
     */
    public void panLeft(double factor)
    {
        // you need to change xwmin and xwmax for this one
        
        synchronized(this)
        {
            // find centre
            factor = factor * 2;
            double centrex = (xwmin + xwmax)/2;
            double centrey = (ywmin + ywmax)/2;
            
            // calculate new width and height
            double width = (xwmax-xwmin);
            
            
            // we want to leave the centre unchanged
            // and change the width and height
            xwmin = xwmin - factor;
            xwmax = xwmax  - factor;
            
        }
    
    }
    
    public void panRight(double factor)
    {
        // and this one
        
         synchronized(this)
        {
            // find centre
            factor = factor * 2;
            double centrex = (xwmin + xwmax)/2;
            double centrey = (ywmin + ywmax)/2;
            
            // calculate new width and height
            double width = (xwmax-xwmin);
            
            
            // we want to leave the centre unchanged
            // and change the width and height
            xwmin = xwmin + factor;
            xwmax = xwmax + factor;
            
        }
    }
    
    public void panDown(double factor)
    {
        // for this one, change ywmin and ywmax
        factor = factor * 2;
        double centrex = (xwmin + xwmax)/2;
            double centrey = (ywmin + ywmax)/2;
            
            // calculate new width and height
            
            
            
            // we want to leave the centre unchanged
            // and change the width and height
            ywmin = ywmin - factor;
            ywmax = ywmax - factor;
    }
    
    public void panUp(double factor)
    {
        // here too
        factor = factor * 2;
        double centrex = (xwmin + xwmax)/2;
            double centrey = (ywmin + ywmax)/2;
            
            // calculate new width and height
            
            
            
            // we want to leave the centre unchanged
            // and change the width and height
            ywmin = ywmin + factor;
            ywmax = ywmax + factor;
    }
    
    public void reset()
    {
        synchronized(this)
        {
            xwmin = 0.0;
            xwmax = WIDTH;
            ywmin = 0.0;
            ywmax = HEIGHT;
        }
        
        zoomIn(2);
    }
    
    // The rest of the code in this class should not be changed
    
        /*
         * @param w - width of display area in real pixels
         * @param h - height in real pixels
         * @param filename - name of the image file to display
         *
         */
    public ImagePanel(int w, int h, String filename)
    {
        super(w, h, WIDTH, HEIGHT);
        
        try
        {
            imageData = JoglImageReader.read(filename);
            tex = TextureLoader.readTexture(imageData.buff);
            
            loaded = true;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            error = e+"";
        }
        
        reset();
        
        setListener(this);
    }
    
    public void _init(GLAutoDrawable drawable)
    {
        GL gl = drawable.getGL();
        
        if(loaded)
        {
            texture = tex.toGL(gl, glu, false);
            gl.glEnable(GL.GL_TEXTURE_2D);
            gl.glBindTexture(GL.GL_TEXTURE_2D, texture);
        }
    }
    
        /*
         * this is the method that gets called when the display area needs to be drawn
         *
         * @param drawable - the GLAutoDrawable to use
         */
    public void display(GLAutoDrawable drawable)
    {
        // Grab context for drawing
        GL gl = drawable.getGL();
        
        // inset the image by 20 pixels
        gl.glViewport(20, 20, this.getWidth()-40, this.getHeight()-40);
        
        // set clipping/viewing window
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glPushMatrix();
        {
            gl.glLoadIdentity();
            synchronized(this)
            {
                glu.gluOrtho2D(xwmin, xwmax, ywmin, ywmax);
            }
            
            // switch to modelling mode
            gl.glMatrixMode(GL.GL_MODELVIEW);
            
            // clear the page (default colour is white)
            gl.glClear(GL.GL_COLOR_BUFFER_BIT);
            
            if(loaded)
            {
                gl.glBegin(GL.GL_QUADS);
                {
                    gl.glTexCoord2d(0,0);
                    gl.glVertex2d(0,0);
                    
                    gl.glTexCoord2d(imageData.xProp,0);
                    gl.glVertex2d(WIDTH,0);
                    
                    gl.glTexCoord2d(imageData.xProp,imageData.yProp);
                    gl.glVertex2d(WIDTH,HEIGHT);
                    
                    gl.glTexCoord2d(0,imageData.yProp);
                    gl.glVertex2d(0,HEIGHT);
                }
                gl.glEnd();
            }
            else
            {
                // write a message
                gl.glColor3f(0.0f, 0.0f, 0.0f);
                gl.glRasterPos2i(10, 10);
                glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, error);
            }
        }
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glPopMatrix();
        
        gl.glMatrixMode(GL.GL_MODELVIEW);
        
        // don't forget this!!
        gl.glFlush();
    }
}
