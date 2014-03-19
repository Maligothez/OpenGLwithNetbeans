package AreaFills;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import RasterDemo.*;

/**
 * A class to illustrate flood fill and boundary fill algorithms.
 *
 * 4-connected flood fill has been implemented.
 *
 * Your task is to implement 8-connected flood fill and also boundary fill.
 *
 * Hot stream: Modify the fill methods to use a stack of horizontal spans as
 * described on p203 of Hearn and Baker.
 *
 * @author phingsto
 *
 */
public class FloodFill implements RasterListener {
    // approximate size of openGL rendering area in pixels

    private static final int WIDTH = 600;
    private static final int HEIGHT = 600;
    // approximate height of panel to hold user controls
    private static final int CONTROL_HEIGHT = 250;
    // size of "raster" to use
    private static final int COLS = 30;
    private static final int ROWS = 30;
    private JFrame frame; // a frame to show the application in
    private RasterPanel raster; // a rendering area with some added functionality
    private ControlPanel control; // holds user controls
    // current user mode --- POINTS, FLOOD4, FLOOD8 or BOUNDARY
    private RasterUserMode mode = RasterUserMode.POINTS;
    // current selected colour
    private Color colour = Color.RED;
    // and boundary colour for boundary fills
    private Color boundaryColour = Color.BLUE;
    // a collection of pixels that have been painted
    private ArrayList<Pixel> pixels = new ArrayList<Pixel>();

    /**
     * @param args - ignored
     */
    public static void main(String[] args) {
        new FloodFill();
    }

    /**
     * Constructor creates the GUI
     */
    public FloodFill() {
        frame = new JFrame("Flood fill");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(WIDTH, HEIGHT + CONTROL_HEIGHT));
        frame.setLayout(new BorderLayout());

        // an openGL panel in the middle
        raster = new RasterPanel(WIDTH, HEIGHT, COLS, ROWS, RasterUserMode.POINTS);
        raster.addListener(this);
        frame.add(raster, BorderLayout.CENTER);

        // and a control panel at the top
        control = new ControlPanel(WIDTH, CONTROL_HEIGHT);
        frame.add(control, BorderLayout.NORTH);

        frame.setVisible(true);
        raster.startJOGL();

        draw();
    }

    /**
     * Here is the flood fill routine. Performs a flood fill using the currently
     * selected colour.
     *
     * This method should be called in a separate thread
     *
     * @param col - the column number for the start pixel
     * @param row - the row number for the start pixel
     * @param startColour - the seed colour
     */
    private void flood4(int col, int row, Color startColour) {
        // here's the algorithm from Hearn and Baker, page 205

        if (startColour.equals(getPixel(col, row))) {
            setPixel(col, row);

            // this bit makes the animation work by
            // drawing intermediate results, and slowing the updates down
            synchronized (this) {
                draw();
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }

            
           //left
            if (col > 0) {
                flood4(col - 1, row, startColour);
            }
            
            //right
            if (col < COLS - 1) {
                flood4(col + 1, row, startColour);
            }
            
            //down
            if (row > 0) {
                flood4(col, row - 1, startColour);
            }
            //up
            if (row < ROWS - 1) {
                flood4(col, row + 1, startColour);
            }
        }
    }

    private void flood8(int col, int row, Color startColour) {
       // System.out.println("8- connected flood not yet implemented");
        // your task to add code here
        
        
        if (startColour.equals(getPixel(col, row))) {
            setPixel(col, row);

            // this bit makes the animation work by
            // drawing intermediate results, and slowing the updates down
            synchronized (this) {
                draw();
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }

            // now we call the routine recursively for each neighbour
            // the "guard" surrounding each call ensures that we do
            // no try to keep going past the edge of the raster
           //left
            if (col > 0) {
                flood8(col - 1, row, startColour);
            }
            //down-left
             if (col > 0 && row > 0 ) {
                flood8(col - 1, row -1, startColour); 
             }
             //down
             if (row > 0) {
                flood8(col, row - 1, startColour);
            }
             //down-right
             if (col < COLS - 1 && row > 0 ) {
                flood8(col + 1, row -1, startColour); 
             }
             //right
              if (row < ROWS - 1) {
                flood8(col, row + 1, startColour);
            }
               //up-right
             if (col < COLS -1  && row < ROWS - 1 ) {
                flood8(col + 1, row +1, startColour); 
             }
               //up
            if (col < COLS - 1) {
                flood8(col + 1, row, startColour);
            }
              //up-left
             if (col > 0 && row < ROWS -1  ) {
                flood8(col - 1, row + 1, startColour); 
             }
            
                 
          
            
           
           
        }
    }

    private void boundary(int col, int row, Color startColour, Color boundaryColour) {
        System.out.println("boundary fill not yet implemented");
        
         //if (boundaryColour.equals(getPixel(col, row))) {
            if (boundaryColour! = startColour && boundaryColour!= boundary){
            setPixel(col, row);
            
            // this bit makes the animation work by
            // drawing intermediate results, and slowing the updates down
            synchronized (this) {
               draw();
            }

           try {
               Thread.sleep(10);
            } catch (InterruptedException e) {
            }

            //now we call the routine recursively for each neighbour
            // the "guard" surrounding each call ensures that we do
            // no try to keep going past the edge of the raster
          // left
            if (col > 0) {
                boundary(col - 1, row, startColour);
            }
            
            //right
            if (col < COLS - 1) {
                boundary(col + 1, row, startColour);
            }
            
            //down
            if (row > 0) {
                boundary(col, row - 1, startColour);
            }
            //up
           if (row < ROWS - 1) {
                boundary(col, row + 1, startColour);
            }
        }
      
    }

    // a couple of helper methods to change and read pixel colours
    synchronized private void setPixel(int col, int row) {
        int index = pixelIndex(col, row);

        if (index >= 0) {
            Pixel pixel = pixels.get(index);
            pixel.setColour(colour);
        } else {
            pixels.add(new Pixel(col, row, colour));
        }
    }

    synchronized private int pixelIndex(int col, int row) {
        for (int indx = 0; indx < pixels.size(); indx++) {
            Pixel pixel = pixels.get(indx);
            if (pixel.row == row && pixel.col == col) {
                return indx;
            }
        }

        return -1;
    }

    synchronized private Color getPixel(int col, int row) {
        int index = pixelIndex(col, row);

        if (index >= 0) {
            return pixels.get(index).getColour();
        } else {
            return new Color(
                    (int) (RasterPanel.backgroundRed * 255),
                    (int) (RasterPanel.backgroundGreen * 255),
                    (int) (RasterPanel.backgroundBlue * 255),
                    (int) (RasterPanel.backgroundAlpha * 255));
        }
    }

    // The rest handles drawing and user interaction
    /**
     * send drawing instructions to the RasterPanel -- called whenever the
     * picture should change
     */
    private void draw() {
        // remove all pixels
        raster.clearPixels();

        // fill pixels
        for (Pixel pixel : pixels) {
            raster.putPixel(pixel);
        }
    }

    // RasterListener methods
    /**
     * Helper method called when the user is in the process of drawing points
     *
     * @param event - the event info
     */
    private void listen(RasterEvent event) {
        if (mode == RasterUserMode.POINTS) {
            Line line = event.getLine();
            setPixel(line.endCol, line.endRow);
        }
        draw();
    }

    /**
     * Called when the user presses the mouse button at the start of a drag
     *
     * @param event - the event info
     */
    public void start(RasterEvent event) {
        listen(event);

        if (mode == RasterUserMode.FLOOD4) {
            Line line = event.getLine();

            Flood4 runner = new Flood4(line.endCol, line.endRow, getPixel(line.endCol, line.endRow));

            (new Thread(runner)).start();
        } else if (mode == RasterUserMode.FLOOD8) {
            Line line = event.getLine();

            Flood8 runner = new Flood8(line.endCol, line.endRow, getPixel(line.endCol, line.endRow));

            (new Thread(runner)).start();
        } else if (mode == RasterUserMode.BOUNDARY) {
            Line line = event.getLine();

            Boundary runner = new Boundary(line.endCol, line.endRow);

            (new Thread(runner)).start();
        }
    }

    /*
     * Called when the user moves the mouse while dragging
     *
     * @param event - the event info
     */
    public void move(RasterEvent event) {
        listen(event);
    }

    /*
     * Use for polyline drawing - not needed for this program
     *
     * @param event - the event info
     */
    public void next(RasterEvent event) {
        // Used for polyline drawing, but it's not needed for this program. 
        // But left here for completeness.
    }

    public void end(RasterEvent event) {
        listen(event);
    }

    /*
     * Called when the user clicks outside the raster area
     *
     * @param event - the event info
     */
    public void abort(RasterEvent event) {
    }

    // Inner class for a panel to hold the user controls
    public class ControlPanel extends JPanel implements ActionListener {

        private static final long serialVersionUID = 1L;
        private JToggleButton pointButton;
        private JToggleButton flood4Button;
        private JToggleButton flood8Button;
        private JToggleButton boundaryButton;
        private JButton colourButton;
        private JButton boundaryColourButton;
        private JButton clearButton;

        /*
         * @param width - desired width of control @param height - desired
         * height of control
         */
        public ControlPanel(int width, int height) {
            JPanel innerPanel = new JPanel();

            innerPanel.setSize(new Dimension(width, height));

            // create panel for mode buttons

            JPanel modePanel = new JPanel();
            modePanel.setLayout(new GridLayout(2, 2));
            modePanel.setBorder(new TitledBorder("mode"));

            ButtonGroup group = new ButtonGroup();

            pointButton = new JToggleButton("draw pixels");
            pointButton.addActionListener(this);
            modePanel.add(pointButton);
            group.add(pointButton);

            flood4Button = new JToggleButton("4-connected flood");
            flood4Button.addActionListener(this);
            modePanel.add(flood4Button);
            group.add(flood4Button);

            flood8Button = new JToggleButton("8-connected flood");
            flood8Button.addActionListener(this);
            modePanel.add(flood8Button);
            group.add(flood8Button);

            boundaryButton = new JToggleButton("boundary fill");
            boundaryButton.addActionListener(this);
            modePanel.add(boundaryButton);
            group.add(boundaryButton);

            pointButton.setSelected(true);

            innerPanel.add(modePanel);

            // And a panel for setting colours

            JPanel colourPanel = new JPanel();
            colourPanel.setLayout(new GridLayout(2, 1));
            colourPanel.setBorder(new TitledBorder("colours"));

            colourButton = new JButton("change pixel colour");
            colourButton.addActionListener(this);
            colourPanel.add(colourButton);

            boundaryColourButton = new JButton("change boundary colour");
            boundaryColourButton.addActionListener(this);
            colourPanel.add(boundaryColourButton);

            innerPanel.add(colourPanel);

            // and a button to clear the raster

            clearButton = new JButton("clear");
            clearButton.addActionListener(this);
            innerPanel.add(clearButton);

            add(innerPanel);
        }

        public void actionPerformed(ActionEvent evt) {
            if (evt.getSource() == pointButton) {
                mode = RasterUserMode.POINTS;
            } else if (evt.getSource() == flood4Button) {
                mode = RasterUserMode.FLOOD4;
            } else if (evt.getSource() == flood8Button) {
                mode = RasterUserMode.FLOOD8;
            } else if (evt.getSource() == boundaryButton) {
                mode = RasterUserMode.BOUNDARY;
            } else if (evt.getSource() == colourButton) {
                colour = JColorChooser.showDialog(
                        this,
                        "Choose pixel colour",
                        colour);
            } else if (evt.getSource() == boundaryColourButton) {
                boundaryColour = JColorChooser.showDialog(
                        this,
                        "Choose boundary colour",
                        boundaryColour);
            } else if (evt.getSource() == clearButton) {
                pixels = new ArrayList<Pixel>();
                draw();
            }
        }
    }

    // An inner class that is used to enable the 4-connected flood fill to execute in a separate thread,
    // allowing animation to occur
    public class Flood4 implements Runnable {

        private int startCol;
        private int startRow;
        public Color startColour;

        public Flood4(int startCol, int startRow, Color startColour) {
            this.startCol = startCol;
            this.startRow = startRow;
            this.startColour = startColour;
        }

        public void run() {
            flood4(startCol, startRow, startColour);
            // should have some synchronisation, but we'll risk it!!
        }
    }

    // An inner class that is used to enable the 8-connected flood fill to execute in a separate thread,
    // allowing animation to occur
    public class Flood8 implements Runnable {
        private int startCol;
        private int startRow;
        public Color startColour;

        public Flood8(int startCol, int startRow, Color startColour) {
            this.startCol = startCol;
            this.startRow = startRow;
            this.startColour = startColour;
        }

        public void run() {
            flood8(startCol, startRow, startColour);
            // should have some synchronisation, but we'll risk it!!
        }
    }

    // An inner class that is used to enable the boundary fill to execute in a separate thread,
    // allowing animation to occur
    public class Boundary implements Runnable {
        private int startCol;
        private int startRow;
        public Color startColour;
        public Color boundaryColour;

        public Boundary(int startCol, int startRow) {
            this.startCol = startCol;
            this.startRow = startRow;
            this.startColour = startColour;
            this.boundaryColour =  boundaryColour;
        }

        public void run() {
            boundary(startCol, startRow, startColour, boundaryColour);
            // should have some synchronisation, but we'll risk it!!
        }
    }
}