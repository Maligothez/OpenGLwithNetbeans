package rasterDemo;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.media.opengl.*;

import com.sun.opengl.util.GLUT;

import au.edu.ecu.jogl.SimpleGLPanel;

/*
 * A panel that uses openGL to draw a "raster" display.
 *
 * Draws a grid with row and column coordinates starting in the bottom left corner
 *
 * The programmer can specify "pixels" to be filled, and "lines" to be drawn,
 * and can set the colours to use for these.
 *
 * @author phingsto
 */
public class RasterPanel extends SimpleGLPanel implements MouseListener, MouseMotionListener {

    private static final long serialVersionUID = 1L;

    // world coordinates: 0-WIDTH x 0-HEIGHT
    private static final int WIDTH = 100;
    private static final int HEIGHT = 100;
    // space around the edges
    private static final int INSET = 5;

    public static final float backgroundRed = 0.9f;
    public static final float backgroundGreen = 0.9f;
    public static final float backgroundBlue = 0.9f;
    public static final float backgroundAlpha = 1.0f;

    // size of raster
    private int rows;
    private int cols;

    // pixels to display
    private ArrayList<Pixel> pixels = new ArrayList<Pixel>();
    // lines to display
    private ArrayList<Line> lines = new ArrayList<Line>();
    // polylines to display
    private ArrayList<Polyline> polylines = new ArrayList<Polyline>();
    // current colour values for drawing
    private float pixelRed = 0.0f;
    private float pixelGreen = 0.0f;
    private float pixelBlue = 0.0f;
    private float pixelAlpha = 0.0f;

    // listeners get told when the user drags the mouse
    private ArrayList<RasterListener> listeners;

    // mode determines what the user is specifying
    private RasterUserMode mode = RasterUserMode.POLYLINE;
    // represents the line the user is dragging out with the mouse
    private Line userLine;
    // represents the polyline the user is creating
    private Polyline userPolyline;
    // true when the user is dragging
    private boolean setting = false;

    /*
     * @param w - width of display area in real pixels
     * @param h - height in real pixels
     * @param cols - number of columns in raster
     * @param rows - number of rows in raster
     * @param mode - use interaction mode
     *
     */
    public RasterPanel(int w, int h, int cols, int rows, RasterUserMode mode) {
        super(w, h, WIDTH, HEIGHT);

        this.rows = rows;
        this.cols = cols;

        this.mode = mode;

        userLine = new Line(0, 0, 0, 0, 0.0f, 0.0f, 0.0f, 1.0f);
        userPolyline = null;

        listeners = new ArrayList<RasterListener>();

        addMouseListener(this);
        addMouseMotionListener(this);
    }

    // manage listeners
    public void addListener(RasterListener listener) {
        listeners.add(listener);
    }

    public void removeListener(RasterListener listener) {
        listeners.remove(listener);
    }

    /*
     * this is the method that gets called when the display area needs to be drawn
     *
     * @param drawable - the GLAutoDrawable to use
     */
    public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        GLUT glut = new GLUT();

        gl.glClear(GL.GL_COLOR_BUFFER_BIT);

        gl.glPushAttrib(GL.GL_ALL_ATTRIB_BITS);

        // enable transparency
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

        // set background of raster area to grey
        gl.glColor4f(backgroundRed, backgroundGreen, backgroundBlue, backgroundAlpha);

        // draw pixels
        double rowHeight = (HEIGHT - 2 * INSET) / (double) rows;
        double colWidth = (WIDTH - 2 * INSET) / (double) cols;
        double rowGap = rowHeight / 25;
        double colGap = colWidth / 25;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                gl.glRectd(INSET + col * colWidth + colGap, INSET + row * rowHeight + rowGap, INSET + (col + 1) * colWidth - colGap, INSET + (row + 1) * rowHeight - rowGap);
            }
        }
        for (Pixel pixel : pixels) {
            gl.glColor4f(pixel.red, pixel.green, pixel.blue, pixel.alpha);
            gl.glRectd(INSET + pixel.col * colWidth + colGap, INSET + pixel.row * rowHeight + rowGap, INSET + (pixel.col + 1) * colWidth - colGap, INSET + (pixel.row + 1) * rowHeight - rowGap);
        }

        gl.glColor3f(0.0f, 0.0f, 0.0f);
        gl.glBegin(GL.GL_LINES);

        gl.glLineWidth(3.0f);
        // add x-axis
        gl.glVertex2d(INSET, INSET);
        gl.glVertex2d(WIDTH - INSET + 2, INSET);
        gl.glVertex2d(WIDTH - INSET + 2, INSET);
        gl.glVertex2d(WIDTH - INSET, INSET - 1);
        gl.glVertex2d(WIDTH - INSET + 2, INSET);
        gl.glVertex2d(WIDTH - INSET, INSET + 1);
        // add y-axis
        gl.glVertex2d(INSET, INSET);
        gl.glVertex2d(INSET, HEIGHT - INSET + 2);
        gl.glVertex2d(INSET, HEIGHT - INSET + 2);
        gl.glVertex2d(INSET - 1, HEIGHT - INSET);
        gl.glVertex2d(INSET, HEIGHT - INSET + 2);
        gl.glVertex2d(INSET + 1, HEIGHT - INSET);

        gl.glLineWidth(0.5f);

        gl.glEnd();

        // add pixel coordinates
        for (int row = 1; row < rows; row++) {
            gl.glRasterPos2d(INSET / 2, INSET + (row - 0.5) * rowHeight);
            glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "" + (row - 1));
        }
        for (int col = 1; col < cols; col++) {
            gl.glRasterPos2d(INSET + (col - 0.5) * rowHeight, INSET / 2);
            glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "" + (col - 1));
        }

        // draw lines
        gl.glBegin(GL.GL_LINES);
        gl.glLineWidth(5.0f);
        for (Line line : lines) {
            gl.glColor4f(line.red, line.green, line.blue, line.alpha);
            gl.glVertex2d(INSET + (line.startCol + 0.5) * colWidth, INSET + (line.startRow + 0.5) * rowHeight);
            gl.glVertex2d(INSET + (line.endCol + 0.5) * colWidth, INSET + (line.endRow + 0.5) * rowHeight);
        }
        gl.glEnd();

        // draw polylines
        gl.glBegin(GL.GL_LINES);
        gl.glLineWidth(5.0f);
        for (Polyline polyline : polylines) {
            for (Line line : polyline.getLines()) {
                gl.glColor4f(line.red, line.green, line.blue, line.alpha);
                gl.glVertex2d(INSET + (line.startCol + 0.5) * colWidth, INSET + (line.startRow + 0.5) * rowHeight);
                gl.glVertex2d(INSET + (line.endCol + 0.5) * colWidth, INSET + (line.endRow + 0.5) * rowHeight);
            }
        }
        gl.glEnd();

        gl.glPopAttrib();

        gl.glFlush();
    }

    // drawing instructions issued by client class
    /*
     * sets the colour for subsequent pixels and lines
     *
     * @param red
     * @param green
     * @param blue
     * @param alpha - all obvious really
     */
    public void setColor4f(float red, float green, float blue, float alpha) {
        pixelRed = red;
        pixelGreen = green;
        pixelBlue = blue;
        pixelAlpha = alpha;
    }

    /*
     * instruction to draw a pixel in the current colour
     *
     * @param col - x coordinate of pixel
     * @param row - y coordinate of pixel
     */
    public void putPixel(int col, int row) {
        pixels.add(new Pixel(col, row, pixelRed, pixelGreen, pixelBlue, pixelAlpha));
    }

    /*
     * instruction to draw a pixel in the current colour
     *
     * @param pixel - the pixel to draw (holds its own colour)
     */
    public void putPixel(Pixel pixel) {
        pixels.add(pixel);
    }

    /*
     * instruction to remove all existing pixels
     */
    public void clearPixels() {
        pixels = new ArrayList<Pixel>();
    }

    /*
     * instruction to draw a line in the current colour
     *
     * @param startCol - x coordinate of first endpoint of line
     * @param startRow - y coordinate of first endpoint of line
     * @param endCol - x coordinate of second endpoint of line
     * @param endRow - y coordinate of second endpoint of line
     */
    public void putLine(int startCol, int startRow, int endCol, int endRow) {
        lines.add(new Line(startCol, startRow, endCol, endRow, pixelRed, pixelGreen, pixelBlue, pixelAlpha));
    }

    /*
     * instruction to draw a line in the current colour
     *
     * @param line - the line to draw (holds its own colour)
     */
    public void putLine(Line line) {
        lines.add(line);
    }

    /*
     * instruction to remove all existing lines
     */
    public void clearLines() {
        lines = new ArrayList<Line>();
    }

    /*
     * instruction to draw a polyline in its own colours
     *
     * @param polyline - the polyline to draw
     */
    public void putPolyline(Polyline polyline) {
        polylines.add(new Polyline(polyline));
    }

    /*
     * instruction to remove all existing lines
     */
    public void clearPolylines() {
        polylines = new ArrayList<Polyline>();
    }

    // helper methods to convert mouse coordinates to "raster" coordinates
    private int getCol(double x) {
        int w = getWidth();

        int col = (int) Math.round(cols * WIDTH * (x / w - INSET / (double) WIDTH) / (WIDTH - 2 * INSET) - 0.5);

        if (col >= cols) {
            col = -1;
        }

        return col;
    }

    private int getRow(double y) {
        int h = getHeight();

        int row = rows - 1 - (int) Math.round(rows * WIDTH * (y / h - INSET / (double) WIDTH) / (WIDTH - 2 * INSET) - 0.5);

        if (row >= rows) {
            row = -1;
        }

        return row;
    }

    // mouseListener and mouseMotionListener event handlers
    public void mouseClicked(MouseEvent evt) {
        if (mode == RasterUserMode.POINTS || mode == RasterUserMode.FLOOD4
                || mode == RasterUserMode.FLOOD8 || mode == RasterUserMode.BOUNDARY) {
            return;
        }

        Point point = evt.getPoint();

        int col = getCol(point.getX());
        int row = getRow(point.getY());

        if (col >= 0 && row >= 0) {
            if (setting) {
                userLine.endCol = col;
                userLine.endRow = row;

                // if doing a polyline, add another segment
                if (mode == RasterUserMode.POLYLINE) {
                    try {
                        userPolyline.add(userLine);
                    } catch (Exception e) {
                    }

                    int startCol = userPolyline.getLines().get(0).startCol;
                    int startRow = userPolyline.getLines().get(0).startRow;
                    if (col == startCol && row == startRow) // back at the start
                    {
                        RasterEvent revt = new RasterEvent(this, userPolyline, userLine);

                        for (RasterListener listener : listeners) {
                            listener.end(revt);
                        }

                        setting = false;
                    } else // get ready for the next segment
                    {
                        userLine.startCol = col;
                        userLine.startRow = row;

                        RasterEvent revt = new RasterEvent(this, userPolyline, userLine);

                        for (RasterListener listener : listeners) {
                            listener.next(revt);
                        }
                    }
                } else // otherwise, completing a line
                {
                    RasterEvent revt = new RasterEvent(this, userPolyline, userLine);

                    for (RasterListener listener : listeners) {
                        listener.end(revt);
                    }

                    setting = false;
                }
            } else // start new line
            {
                userLine.startCol = col;
                userLine.startRow = row;
                userLine.endCol = col;
                userLine.endRow = row;

                if (mode == RasterUserMode.POLYLINE) {
                    userPolyline = new Polyline();
                } else {
                    userPolyline = null;
                }

                RasterEvent revt = new RasterEvent(this, userPolyline, userLine);

                for (RasterListener listener : listeners) {
                    listener.start(revt);
                }

                setting = true;
            }
        } else // click outside raster
        {
            RasterEvent revt = new RasterEvent(this, null, null);

            for (RasterListener listener : listeners) {
                listener.abort(revt);
            }

            setting = false;
        }
    }

    public void mousePressed(MouseEvent evt) {
        if (mode == RasterUserMode.LINE || mode == RasterUserMode.POLYLINE) {
            return;
        }

        Point point = evt.getPoint();

        int col = getCol(point.getX());
        int row = getRow(point.getY());

        if (col >= 0 && row >= 0) {
            userLine.startCol = col;
            userLine.startRow = row;
            userLine.endCol = col;
            userLine.endRow = row;

            RasterEvent revt = new RasterEvent(this, null, userLine);

            for (RasterListener listener : listeners) {
                listener.start(revt);
            }
        }
    }

    public void mouseReleased(MouseEvent evt) {
        if (mode == RasterUserMode.LINE || mode == RasterUserMode.POLYLINE) {
            return;
        }

        Point point = evt.getPoint();

        int col = getCol(point.getX());
        int row = getRow(point.getY());

        if (col >= 0 && row >= 0) {
            userLine.startCol = col;
            userLine.startRow = row;
            userLine.endCol = col;
            userLine.endRow = row;

            RasterEvent revt = new RasterEvent(this, null, userLine);

            for (RasterListener listener : listeners) {
                listener.end(revt);
            }
        }
    }

    public void mouseEntered(MouseEvent arg0) {
    }

    public void mouseExited(MouseEvent arg0) {
    }

    public void mouseDragged(MouseEvent evt) {
        if (mode == RasterUserMode.LINE || mode == RasterUserMode.POLYLINE) {
            return;
        }

        Point point = evt.getPoint();

        int col = getCol(point.getX());
        int row = getRow(point.getY());

        if (col >= 0 && row >= 0) {
            userLine.startCol = col;
            userLine.startRow = row;
            userLine.endCol = col;
            userLine.endRow = row;

            RasterEvent revt = new RasterEvent(this, null, userLine);

            for (RasterListener listener : listeners) {
                listener.move(revt);
            }
        }
    }

    public void mouseMoved(MouseEvent evt) {
        if (mode == RasterUserMode.POINTS || mode == RasterUserMode.FLOOD4
                || mode == RasterUserMode.FLOOD8 || mode == RasterUserMode.BOUNDARY) {
            return;
        }

        if (setting) {
            Point point = evt.getPoint();

            int col = getCol(point.getX());
            int row = getRow(point.getY());

            userLine.endCol = col;
            userLine.endRow = row;

            if (col >= 0 && row >= 0) {
                RasterEvent revt = new RasterEvent(this, userPolyline, userLine);

                for (RasterListener listener : listeners) {
                    listener.move(revt);
                }
            }
        }
    }
}
