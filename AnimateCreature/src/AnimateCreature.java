import java.awt.event.ActionEvent;

import javax.media.opengl.*;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Timer;

import au.edu.ecu.jogl.SimpleGLEventListener;
import au.edu.ecu.jogl.SimpleGLFrame;

/**
 *
 * A program that uses transformations to create a simple animation
 * of a strange creature.
 *
 * @author phingsto
 *
 */
public class AnimateCreature extends SimpleGLFrame implements SimpleGLEventListener
{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    
    private static double GROUND = 200;
    private static double LEFT = 50;
    private static double RIGHT = 550;
    private static double CREATURE_WIDTH = 100;
    private static double CREATURE_HEIGHT = 100;
    
    private Creature strangeOne;
    
    
    public static void main(String[] args)
    {
        new AnimateCreature();
    }
    
    private AnimateCreature()
    {
        // create jogl window
        super("Creature animation", 600, 500, 600, 500);
        setListener(this);
        startJOGL();
        
        // create our creature
        strangeOne = new Creature(GROUND, LEFT, RIGHT, CREATURE_WIDTH, CREATURE_HEIGHT);
        
        // start a timer to control the animation
        creatureTimer.start();
    }
    
    public void _init(GLAutoDrawable drawable)
    {
        // Grab context for drawing
        GL gl = drawable.getGL();
        
        gl.glClearColor(0f, 0f, 0f, 1f);

        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        
        gl.glEnable(GL.GL_LINE_SMOOTH);
    }
    
    public void display(GLAutoDrawable drawable)
    {
        // Grab context for drawing
        GL gl = drawable.getGL();
        
        // clear the page (default colour is white)
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        
        gl.glPushAttrib(GL.GL_ALL_ATTRIB_BITS);
        
        gl.glColor3d(0.5, 0.5, 0);
        gl.glRectd(LEFT, 0, RIGHT, GROUND);
        
        gl.glPopAttrib();
        
        // draw the creature in its current position
        if(strangeOne != null) strangeOne.draw(gl);
        
        // don't forget this!!
        gl.glFlush();
    }
    
    private Action updateCreatureAction = new AbstractAction()
    {
        /**
         *
         */
        private static final long serialVersionUID = 1L;
        
        public void actionPerformed(ActionEvent e)
        {
            strangeOne.update();
        }
    };
    
    private Timer creatureTimer = new Timer(25, updateCreatureAction);
}
