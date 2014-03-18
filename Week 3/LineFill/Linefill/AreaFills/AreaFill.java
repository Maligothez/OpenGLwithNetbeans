package AreaFills;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import Edge.EdgeData;
import Edge.EdgeList;
import RasterDemo.*;

/**
 * A class to illustrate scan conversion of polygons.
 *
 * The user can specify two polygons, which are filled using a polygon scan
 * conversion method like that in Hearn and Baker, pages 196-200
 *
 * @author phingsto
 *
 */
public class AreaFill implements RasterListener {
    // approximate size of openGL rendering area in pixels

    private static final int WIDTH = 600;
    private static final int HEIGHT = 600;
    // approximate height of panel to hold user controls
    private static final int CONTROL_HEIGHT = 150;
    // size of "raster" to use
    private static final int COLS = 30;
    private static final int ROWS = 30;
    private JFrame frame; // a frame to show the application in
    private RasterPanel raster; // a rendering area with some added functionality
    private ControlPanel control; // holds user controls
    // mode indicates which polygon is being specified
    private static final int POLY1_MODE = 0;
    private static final int POLY2_MODE = 1;
    private int mode = POLY1_MODE;
    // the current polygons that the algorithm fills
    private Polyline polyline1;
    private Polyline polyline2;
    // and two collection of pixels that fill them
    private ArrayList<Pixel> fillPixels1;
    private ArrayList<Pixel> fillPixels2;
    // a rubberband line used when the user is setting the next edge to draw
    private Line rubberLine;
    private boolean settingLine = false;

    /**
     * @param args - ignored
     */
    public static void main(String[] args) {
        new AreaFill();
    }
    /*
     * Constructor creates the GUI
     */

    public AreaFill() {
        frame = new JFrame("Area fill Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(WIDTH, HEIGHT + CONTROL_HEIGHT));
        frame.setLayout(new BorderLayout());
        // add an openGL panel in the middle
        raster = new RasterPanel(WIDTH, HEIGHT, COLS, ROWS, RasterUserMode.POLYLINE);
        raster.addListener(this);
        frame.add(raster, BorderLayout.CENTER);

        // and a control panel at the top
        control = new ControlPanel(WIDTH, CONTROL_HEIGHT);
        frame.add(control, BorderLayout.NORTH);
        frame.setVisible(true);
        raster.startJOGL();
        draw();
    }

    // Here's the guts of it.
    // Similar to Hearn and Baker
    public void scanConvert(Polyline polygon) {
        // sorted edge table and active edge list for scan conversion algorithm
        ArrayList<EdgeList> sortedEdgeTable;
        EdgeList activeEdgeList;

        // store pixels to be filled
        ArrayList<Pixel> fillPixels = new ArrayList<Pixel>();

        if (mode == POLY1_MODE) {
            fillPixels1 = fillPixels;
        }
        if (mode == POLY2_MODE) {
            fillPixels2 = fillPixels;
        }

        // this will be the first row for which pixels are drawn
        int firstRow = -1;

        // first build a sortededge table
        // start with an empty list for each row
        sortedEdgeTable = new ArrayList<EdgeList>();
        for (int row = 0; row < ROWS; row++) {
            sortedEdgeTable.add(new EdgeList());
        }

        // process polygon edges one at a time and add data to sorted edge table
        ArrayList<Line> lines = polygon.getLines();
        for (int edge = 0; edge < lines.size(); edge++) {
            Line line = lines.get(edge);
            // ignore horizontal lines
            if (line.getM() != 0) {
                // get an upwards version of the edge
                Line upline = line.upwards();

                // the edge starts on this scan line
                int row = upline.startRow;
                if (firstRow == -1 || firstRow > row) {
                    firstRow = row;
                }

                // so we add its data into this bucket
                EdgeList edgeList = (EdgeList) sortedEdgeTable.get(row);

                // create entry and shorten line
                EdgeData data = new EdgeData(upline);
                data.endRow--;

                edgeList.add(data);
            }
        }

        // now compute the pixels for each scan line
        // initialise active edge list
        activeEdgeList = new EdgeList();
        if (firstRow == -1) {
            return; // no edges
        }
        // start on the first scan line with pixels to be filled
        int row = firstRow;
        activeEdgeList.add((EdgeList) sortedEdgeTable.get(row));

        // process rows until past top of polygon
        while (!activeEdgeList.getEdges().isEmpty()) {
            // current edge data
            ArrayList<EdgeData> edges = activeEdgeList.getEdges();

            boolean inside = false; // start off outside polygon
            int startCol = -1;
            for (EdgeData data : edges) {
                inside = !inside; // swap from inside to out, or vice versa
                if (inside) {
                    // start of a new span
                    startCol = leftEdge(data.col);
                } else {
                    // found end of this span
                    int endCol = rightEdge(data.col);
                    for (int col = startCol; col <= endCol; col++) {
                        if (mode == POLY1_MODE) {
                            fillPixels.add(new Pixel(col, row, 1.0f, 0.0f, 0.0f, 0.5f));
                        }
                        if (mode == POLY2_MODE) {
                            fillPixels.add(new Pixel(col, row, 0.0f, 0.0f, 1.0f, 0.5f));
                        }
                    }
                }
            }

            // step to next row (update current edges and remove finished ones)
            row++;
            activeEdgeList.step(row);

            // add new edges
            if (row < sortedEdgeTable.size()) {
                activeEdgeList.add((EdgeList) sortedEdgeTable.get(row));
            }
        }
    }

    // round up for left edges
    private static int leftEdge(double x) {
        if (x - (int) x == 0) {
            return (int) x;
        } else {
            return (int) Math.ceil((float) x);
        }
    }

    // round down for right edges
    private static int rightEdge(double x) {
        if (x - (int) x == 0) {
            return (int) x - 1;
        } else {
            return (int) Math.floor((float) x);
        }
    }

    // The rest handles drawing and user interaction
        /*
     * send drawing instructions to the RasterPanel -- called whenever the
     * picture should change
     */
    private void draw() {
        // remove all pixels and lines
        raster.clearPolylines();
        raster.clearLines();
        raster.clearPixels();

        // fill area
        if (fillPixels1 != null) {
            for (Pixel pixel : fillPixels1) {
                raster.putPixel(pixel);
            }
        }

        if (fillPixels2 != null) {
            for (Pixel pixel : fillPixels2) {
                raster.putPixel(pixel);
            }
        }

        // draw the current polyline
        if (polyline1 != null) {
            try {
                raster.putPolyline(polyline1);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        if (polyline2 != null) {
            try {
                raster.putPolyline(polyline2);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        // a rubberband effect used when the user is adding a new edge to the polyline
        if (settingLine) {
            raster.putLine(rubberLine);
        }
    }

    // RasterListener methods
    /*
     * called when the user is in the process of drawing a polygon
     *
     * @param event - the event info
     */
    private void listen(RasterEvent event) {
        if (mode == POLY1_MODE) {
            polyline1 = event.getPolyline();
        }
        if (mode == POLY2_MODE) {
            polyline2 = event.getPolyline();
        }
        rubberLine = event.getLine();
        settingLine = true;
        draw();
    }
    /*
     * called when the user presses the mouse button at the start of a drag
     *
     * @param event - the event info
     */

    public void start(RasterEvent event) {
        control.clearCanvas(); 
        listen(event);
    }

    /*
     * called when the user moves the mouse while dragging
     *
     * @param event - the event info
     */
    public void move(RasterEvent event) {
        listen(event);
    }

    /*
     * called when the user clicks on the next vertex (and it's not the same as
     * the starting vertex)
     *
     * @param event - the event info
     */
    public void next(RasterEvent event) {
        listen(event);
    }

    /*
     * called when the user clicks on the starting point of the polygon while in
     * the process of drawing a polygon
     *
     * @param event - the event info
     */
    public void end(RasterEvent event) {
        if (mode == POLY1_MODE) {
            polyline1 = event.getPolyline();
        }
        if (mode == POLY2_MODE) {
            polyline2 = event.getPolyline();
        }
        rubberLine = event.getLine();
        settingLine = false;
        if (mode == POLY1_MODE) {
            scanConvert(polyline1);
        }
        if (mode == POLY2_MODE) {
            scanConvert(polyline2);
        }
        draw();
    }

    /*
     * called when the user clicks outside the raster area
     *
     * @param event - the event info
     */
    public void abort(RasterEvent event) {
        if (mode == POLY1_MODE) {
            polyline1 = null;
            fillPixels1 = null;
        }
        if (mode == POLY2_MODE) {
            polyline2 = null;
            fillPixels2 = null;
        }
        settingLine = false;
        draw();
    }

    /*
     * Inner class for a panel to hold the user controls
     */
    public class ControlPanel extends JPanel implements ActionListener {

        private static final long serialVersionUID = 1L;
        private JToggleButton button1;
        private JToggleButton button2;
        private JButton button3;
        private JButton button4;
        /*
         * @param width - desired width of control @param height - desired
         * height of control
         */

        public ControlPanel(int width, int height) {
            setSize(new Dimension(width, height));
            setBorder(new TitledBorder("mode"));
            ButtonGroup group = new ButtonGroup();

            button1 = new JToggleButton("1st polyline");
            button1.addActionListener(this);
            add(button1);
            group.add(button1);

            button2 = new JToggleButton("2nd polyline");
            button2.addActionListener(this);
            add(button2);
            group.add(button2);

            button3 = new JButton("Clear Canvas");
            button3.addActionListener(this);
            add(button3);
          //  group.add(button3);

            button4 = new JButton("Exit");
            button4.addActionListener(this);
            add(button4);
            //group.add(button3);
            
            button1.setSelected(true);
        }

        public void actionPerformed(ActionEvent evt) {
            if (evt.getSource() == button1) {
                mode = POLY1_MODE;
                clearCanvas();
            }
            if (evt.getSource() == button2) {
                mode = POLY2_MODE;
                clearCanvas();
            } else if (evt.getSource() == button3) {
                //clear the canvas with this.
                clearCanvas();
            } else { 
                System.exit(0);
            }
        }
        public void clearCanvas() {
               fillPixels1 = new ArrayList<Pixel>();
                fillPixels2 = new ArrayList<Pixel>();
                polyline1 = null;
                polyline2 = null;
                settingLine = false;
                draw();
        }
    }
}
