import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

public class Creature
{
    // in creature's modelling coordinate system
    // the creature is centred at (0,0) and is 100x100 units when legs fully extended
    private static double BODY_SIZE = 0.5;
    
    // define how many legs
    private static double NLEGS = 11;
    
    // location of eye in body coordinates
    private static double EYE_X = 0.3;
    private static double EYE_Y = 0.1;
    // and size
    private static double EYE_SIZE = 0.25;
    
    // creature's size in world coordinates
    private double width;
    private double height;
    
    // limits of creature's horizontal movement in world coordinates
    private double left;
    private double right;
    
    // location of creature's centre in world coordinates
    private double creatureX;
    private double ground;
    
    // current position of legs in degrees
    private double legAngle = 0;
    
    // how much the creature is squatting
    private double squat = 0.0;
    
    // creature's height above the ground in creature coordinates
    private double elevation = 0;
    
    // what the creature is doing at the moment
    private int state = CreatureState.MOVING_RIGHT;
    private int dir = Direction.RIGHT;
    
    // useful things for drawing filled circles
    private GLUquadric circle;
    private GLU glu;
    
    // axes can be used to show the coordinate system after transformations
    private Axes axes = new Axes();
    private boolean SHOW_AXES = false;
    
    // here's an example of a display list
    private int legDL = 0;
    
    public Creature(double ground, double left, double right, double width, double height)
    {
        this.ground = ground;
        this.left = left;
        this.right = right;
        this.width = width;
        this.height = height;
        
        creatureX = left;
        
        // useful for drawing circles later
        glu = new GLU();
        circle = glu.gluNewQuadric();
    }
    
    // the next few methods draw various bits of the creature
    // and are combined together using various transformations
    
    public void draw(GL gl)
    {
        SHOW_AXES = false;
        if(SHOW_AXES)
        {
            axes.draw(gl, 100, 0, 0, 1);
        }
        
        gl.glPushMatrix();
        {
            // set size and location of creature
            gl.glTranslated(creatureX, ground, 0);
            gl.glScaled(width/2, height/2, 1);

            drawCreature(gl);
        }
        gl.glPopMatrix();
    }
    
    // draw a creature using creature coordinates
    // i.e. inside an square with bl at (-1,-1) and tr at (1, 1)
    public void drawCreature(GL gl)
    {
        if(SHOW_AXES)
        {
            axes.draw(gl, 0.5, 1, 1, 1);
        }
        if(dir == Direction.LEFT)
            {
                gl.glScaled(-1, 1, 1);
            }
        gl.glPushMatrix();
        {    
            gl.glScaled(1.0, 1.0-squat, 1);
            gl.glTranslated(0, 1+elevation, 0);

           // this transformation makes the
        // legs spin around when the creature walks
            
            gl.glPushMatrix();
        // this transformation makes the
        // legs spin around when the creature walks
            gl.glRotated(legAngle, 0, 0, 1);
            drawLegs(gl);
            gl.glPopMatrix();
           

            gl.glPushMatrix();
            {
                // this one controls the relative size of the creature's body
                gl.glScaled(BODY_SIZE, BODY_SIZE, 1);
                drawBody(gl);
                
            }
            
            
            gl.glPopMatrix();
        }
        
        
        gl.glPopMatrix();
        
        
    }
    
    // draw legs using leg coordinates
    // i.e. inside an square with bl at (-1,-1) and tr at (1, 1)
    public void drawLegs(GL gl)
    {
        if(SHOW_AXES)
        {
            axes.draw(gl, 0.5, 0, 1, 1);
        }

        gl.glPushAttrib(GL.GL_ALL_ATTRIB_BITS);
        {
            // legs will be red
            gl.glColor3d(1, 0, 0);

            gl.glPushMatrix();
            for(int i = 0; i < NLEGS; i++)
                {
                    drawLeg(gl);
                    // rotate the next leg a bit further
                    // so the legs will point in all directions
                    gl.glRotated(360.0/NLEGS, 0, 0, 1);
                }
            gl.glPopMatrix();
        }
        gl.glPopAttrib();
    }
    
    public void drawEye(GL gl) {
        
        if(SHOW_AXES) {
            axes.draw(gl, -1, 1, 1 , 1);
        }
        
        gl.glPushAttrib(GL.GL_ALL_ATTRIB_BITS);
        {
            // body is dark red
            gl.glColor3d(0, 0, 0);

            glu.gluDisk(circle, 1, 2, 20, 1);
        }
        gl.glPopAttrib();
        
        
    }
    
    // draws one leg (vertical)
    public void drawLeg(GL gl)
    {
        if(legDL == 0)
        {
            legDL = makeLegDL(gl);
        }
        
        gl.glCallList(legDL);
    }
    
    // this method is only called once, and the "compiled"
    // instructions are stored in the display list
    // later, they can be executed by calling glCallList
    // as in drawLeg() above
    public int makeLegDL(GL gl)
    {
        int dl = gl.glGenLists(1);
        
        gl.glNewList(dl, GL.GL_COMPILE);
        {
            gl.glBegin(GL.GL_LINES);
            {
                gl.glVertex2i(0, 0);
                gl.glVertex2d(0.1, -0.6);
                gl.glVertex2d(0.1, -0.6);
                gl.glVertex2d(-0.1, -0.9);
                gl.glVertex2d(-0.1, -0.9);
                gl.glVertex2d(0, -1);
            }
            gl.glEnd();
        }
        gl.glEndList();
        
        return dl;
    }
    
    // draw body using body coordinates
    // i.e. inside an square with bl at (-1,-1) and tr at (1, 1)
    public void drawBody(GL gl)
    {
        gl.glPushAttrib(GL.GL_ALL_ATTRIB_BITS);
        {
            // body is dark red
            gl.glColor3d(0.5, 0, 0);

            glu.gluDisk(circle, 0, 1, 20, 1);
        }
        gl.glPopAttrib();
        
        gl.glPushMatrix();
        // the eye is a little off centre
        gl.glTranslated(EYE_X, EYE_Y, 0);
        // this determines the size of the eye
        // relative to the body
        gl.glScaled(EYE_SIZE, EYE_SIZE, 1);
        drawEye(gl);
        gl.glPopMatrix();
    }
    
    // the rest controls the movements of the creature
    // it might be better if this was a separate class, but ...
       
    public class CreatureState
    {
        public static final int MOVING_RIGHT = 0;
        public static final int MOVING_LEFT = 1;
        public static final int SQUATTING = 2;
        public static final int JUMPING = 3;
        public static final int FALLING = 4;
    }
    
    public class Direction
    {
        public static final int RIGHT = 0;
        public static final int LEFT = 1;
    }
    
    private static double STEP = 2.5;
    private static double ROTATE_STEP = 9;
    private static double SQUAT_STEP = 0.1;
    private static double MAX_SQUAT = 0.15;
    private static double ELEVATION_STEP = 0.15;
    private static double MAX_ELEVATION = 1.2;
    
    public void update()
    {
        switch(state)
        {
            case CreatureState.MOVING_RIGHT:
                creatureX += STEP;
                legAngle -= ROTATE_STEP;
                if(creatureX >= right)
                {
                    creatureX = right;
                    state = CreatureState.SQUATTING;
                }
                break;
            case CreatureState.MOVING_LEFT:
                creatureX -= STEP;
                legAngle -= ROTATE_STEP;
                if(creatureX <= left)
                {
                    creatureX = left;
                    state = CreatureState.SQUATTING;
                }
                break;
            case CreatureState.SQUATTING:
                if(squat >= MAX_SQUAT)
                {
                    state = CreatureState.JUMPING;
                }
                else
                {
                    squat += SQUAT_STEP;
                }
                break;
            case CreatureState.JUMPING:
                if(elevation >= MAX_ELEVATION)
                {
                    state = CreatureState.FALLING;
                    squat = 0.0;
                }
                else
                {
                    elevation += ELEVATION_STEP;
                    squat -= SQUAT_STEP;
                    if(squat < 0.0) squat = 0.0;
                }
                break;
            case CreatureState.FALLING:
                if(elevation <= 0)
                {
                    dir = (dir == Direction.LEFT)? Direction.RIGHT: Direction.LEFT;
                    state = (dir == Direction.LEFT)? CreatureState.MOVING_LEFT: CreatureState.MOVING_RIGHT;
                    elevation = 0.0;
                }
                else
                {
                    elevation -= ELEVATION_STEP;
                    if(elevation < 0.0) elevation = 0.0;
                }
                break;
        }
    }
}
