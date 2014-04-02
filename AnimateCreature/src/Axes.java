/*
 * Axes.java
 *
 * Created on 23 March 2008, 16:22
 *
 */

import com.sun.opengl.util.GLUT;
import javax.media.opengl.GL;

/**
 *
 * @author phingsto
 */
public class Axes
{
    private GLUT glut = new GLUT();
    
    /** Creates a new instance of Spiro */
    public Axes()
    {
    }
    
    public void draw(GL gl, double scale, double red, double green, double blue)
    {
        gl.glPushAttrib(GL.GL_ALL_ATTRIB_BITS);
        {
            gl.glPushMatrix();
            {
                gl.glScaled(scale, scale, 1);

                gl.glColor4d(red, green, blue, 0.4);                
                gl.glRectd(-1, -1, 1, 1);

                gl.glColor4d(red, green, blue, 1);
                gl.glBegin(GL.GL_LINES);
                {
                    gl.glVertex2d(-1.5, 0);
                    gl.glVertex2d(1.5, 0);
                    gl.glVertex2d(-1.4, -0.1);
                    gl.glVertex2d(-1.5, 0);
                    gl.glVertex2d(-1.4, 0.1);
                    gl.glVertex2d(-1.5, 0);
                    gl.glVertex2d(1.4, -0.1);
                    gl.glVertex2d(1.5, 0);
                    gl.glVertex2d(1.4, 0.1);
                    gl.glVertex2d(1.5, 0);

                    gl.glVertex2d(0, -1.5);
                    gl.glVertex2d(0, 1.5);
                    gl.glVertex2d(-0.1, -1.4);
                    gl.glVertex2d(0, -1.5);
                    gl.glVertex2d(0.1, -1.4);
                    gl.glVertex2d(0, -1.5);
                    gl.glVertex2d(-0.1, 1.4);
                    gl.glVertex2d(0, 1.5);
                    gl.glVertex2d(0.1, 1.4);
                    gl.glVertex2d(0, 1.5);
                }
                gl.glEnd();
            }
            gl.glPopMatrix();
        }
        gl.glPopAttrib();
    }
}