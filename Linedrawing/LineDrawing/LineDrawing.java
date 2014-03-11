import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import rasterDemo.*;

/**
 * A class to illustrate line drawing algorithms.
 * 
 * A version of the naive algorithm from Page 93 of Hearn and Baker has been implemented.
 * 
 * Your task is to complete the method drawDDALine() and the simple case for drawBresenhamLine().
 * 
 * Hot stream: modify drawBresenham() so that it deals correctly with all cases.
 * 
 * Superheated stream: Add anti-aliased lines using Xiaolin Wu's algorithm (http://en.wikipedia.org/wiki/Xiaolin_Wu%27s_line_algorithm).
 * 
 * @author phingsto
 *
 */
public class LineDrawing implements RasterListener
{
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
	
	// these booleans determine which algorithms are being used
	private boolean naive = false;
	private boolean dda = false;
	private boolean bresenham = false;
	
	// the current line that the chosen algorithms draw
	private Line currentLine = null;
	// a rubberband line used when the user is setting the line to draw
	private Line rubberLine;
	private boolean settingLine = false;
	
	/**
	 * @param args - ignored
	 */
	public static void main(String[] args)
	{
		new LineDrawing();
	}

	/*
	 * 
	 */
	public LineDrawing()
	{
		frame = new JFrame("Line drawing");
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(new Dimension(WIDTH, HEIGHT+CONTROL_HEIGHT));
		frame.setLayout(new BorderLayout());
		
		// an openGL panel in the middle
		raster = new RasterPanel(WIDTH, HEIGHT, COLS, ROWS, RasterUserMode.LINE);
		raster.addListener(this);
		frame.add(raster, BorderLayout.CENTER);
		
		// and a control panel at the top
		control = new ControlPanel(WIDTH, CONTROL_HEIGHT);
		frame.add(control, BorderLayout.NORTH);
		
		frame.setVisible(true);
		raster.startJOGL();
		
		draw();
	}
	
	/*
	 * @param enabled - set whether to use the naive algorithm
	 */
	private void setNaive(boolean enabled)
	{
		naive = enabled;
		draw();
	}
	
	/*
	 * @param enabled - set whether to use the dda algorithm
	 */
	private void setDDA(boolean enabled)
	{
		dda = enabled;
		draw();
	}
	
	/*
	 * @param enabled - set whether to use Bresenham's algorithm
	 */
	private void setBresenham(boolean enabled)
	{
		bresenham = enabled;
		draw();
	}
	
	/*
	 * send drawing instructions to the RasterPanel
	 * -- called whenever the picture should change
	 */
	private void draw()
	{
		// remove all pixels and lines
		raster.clearLines();
		raster.clearPixels();
				
		// draw the current line
		if(currentLine != null)
		{
			// draw pixels for the current line using the naive algorithm
			if(naive)
			{
				raster.setColor4f(1.0f, 0.0f, 0.0f, 0.3f);
				drawNaiveLine(currentLine.startCol, currentLine.startRow, currentLine.endCol, currentLine.endRow);
			}
			
			// draw pixels for the current line using the dda algorithm
			if(dda)
			{
				raster.setColor4f(0.0f, 1.0f, 0.0f, 0.3f);
				drawDDALine(currentLine.startCol, currentLine.startRow, currentLine.endCol, currentLine.endRow);
			}
			
			// draw pixels for the current line using Bresenham's algorithm
			if(bresenham)
			{
				raster.setColor4f(0.0f, 0.0f, 1.0f, 0.3f);
				drawBresenhamLine(currentLine.startCol, currentLine.startRow, currentLine.endCol, currentLine.endRow);
			}

			raster.putLine(currentLine);
		}
		
		// a rubberband effect used when the user is setting a new line to draw
		if(settingLine)
		{
			raster.putLine(rubberLine);
		}
	}

	/*
	 * 
	 * @param startCol - x coordinate of first endpoint
	 * @param startRow - y coordinate of first endpoint
	 * @param endCol - x coordinate of second endpoint
	 * @param endRow - y coordinate of second endpoint
	 * 
	 */
	private void drawNaiveLine(int startCol, int startRow, int endCol, int endRow)
	{		
		
            boolean swap = Math.abs(endRow-startRow) > Math.abs(endCol-startCol);

		// swap x and y if needed, to make sure that the slope is less than 1
		// Why is this necessary?
		if(swap)
		{
			int save;
			
			save = startCol;
			startCol = startRow;
			startRow = save;
			
			save = endCol;
			endCol = endRow;
			endRow = save;
		}

		// the slope
		double m = (endRow-startRow)/(double)(endCol-startCol);
		
		// handles left-to-right or right-to-left
		int xInc = (startCol < endCol)? 1: -1;
		
		// fill in the starting pixel
		int col = startCol;

		if(swap)
			raster.putPixel(startRow, col);
		else
			raster.putPixel(col, startRow);
		
		// now step along, calculating the y value for each x value as we go
		while(col != endCol)
		{
			col += xInc;
			// here's the other half of the swap x and y trick
			if(swap)
			{
				raster.putPixel
				(
					startRow + (int)Math.round(m*(col-startCol)),
					col
				);			
			}
			else
			{
				raster.putPixel
				(
					col,
					startRow + (int)Math.round(m*(col-startCol))
				);
			}
		}
	}
	
	/*
	 * 
	 * @param startCol - x coordinate of first endpoint
	 * @param startRow - y coordinate of first endpoint
	 * @param endCol - x coordinate of second endpoint
	 * @param endRow - y coordinate of second endpoint
	 * 
	 */
	private void drawDDALine(int startCol, int startRow, int endCol, int endRow)
	{
            boolean swap = Math.abs(endRow-startRow) > Math.abs(endCol-startCol);

		// swap x and y if needed, to make sure that the slope is less than 1
		// Why is this necessary?
		if(swap)
		{
			int save;
			
			save = startCol;
			startCol = startRow;
			startRow = save;
			
			save = endCol;
			endCol = endRow;
			endRow = save;
		}

		// the slope
		double m = (endRow-startRow)/(double)(endCol-startCol);
                
                int xInc = (startCol < endCol)? 1: -1;
                
                if ((m) <= 1){
                    int col = startCol;
                    
                    for (int row = startRow; row > endRow; row++) {
                        
                        
                        raster.putPixel(row, col);
                        col = col + (Math.abs(m));
                    }
                    }
                else {
                    
                }
                }
		
		// handles left-to-right or right-to-left
		
		
		// fill in the starting pixel
		int col = startCol;

		if(swap)
			raster.putPixel(startRow, col);
		else
			raster.putPixel(col, startRow);
		
		// now step along, calculating the y value for each x value as we go
		while(col != endCol)
		{
			col += xInc;
			// here's the other half of the swap x and y trick
			if(swap)
			{
				raster.putPixel
				(
					startRow + (int)Math.round(m*(col-startCol)),
					col
				);			
			}
			else
			{
				raster.putPixel
				(
					col,
					startRow + (int)Math.round(m*(col-startCol))
				);
			}
		}
	}
	
	/*
	 * 
	 * @param startCol - x coordinate of first endpoint
	 * @param startRow - y coordinate of first endpoint
	 * @param endCol - x coordinate of second endpoint
	 * @param endRow - y coordinate of second endpoint
	 * 
	 */
	private void drawBresenhamLine(int startCol, int startRow, int endCol, int endRow)
	{
	}
	
	// RasterListener methods
	// used for rubberband effect
	
	/*
	 * called when the user is in the process of drawing a polygon
	 * 
	 * @param event - the event info
	 */
	private void listen(RasterEvent event)
	{
		rubberLine = event.getLine();
		settingLine = true;
		draw();
	}

	/*
	 * called when the user presses the mouse button at the start of a drag
	 * 
	 * @param event - the event info
	 */
	public void start(RasterEvent event)
	{
		listen(event);
	}

	/*
	 * called when the user moves the mouse while dragging
	 * 
	 * @param event - the event info
	 */
	public void move(RasterEvent event)
	{
		listen(event);
	}

	/*
	 * called when the user releases the mouse button at the end of a drag
	 * 
	 * @param event - the event info
	 */
	public void end(RasterEvent event)
	{
		rubberLine = event.getLine();
		if(currentLine == null)
		{
			currentLine = new Line(0, 0, 0, 0, 0.0f, 0.0f, 0.0f, 1.0f);
		}
		currentLine.startCol = rubberLine.startCol;
		currentLine.startRow = rubberLine.startRow;
		currentLine.endCol = rubberLine.endCol;
		currentLine.endRow = rubberLine.endRow;
		settingLine = false;
		draw();
	}

	/*
	 * called when the user releases the mouse button outside the raster area
	 * 
	 * @param event - the event info
	 */
	public void abort(RasterEvent event)
	{
		settingLine = false;
		draw();
	}
	
	/*
	 * use for drawing polylines
	 * called when the user clicks on the next vertex (and it's not the
	 * same as the starting vertex)
	 * 
	 * @param event - the event info
	 */
	public void next(RasterEvent event)
	{
	}

	/*
	 * Inner class for a panel to hold the user controls
	 */
	public class ControlPanel extends JPanel implements ActionListener
	{
		private static final long serialVersionUID = 1L;
		
		// radio buttons determine which algorithms to use
		private JRadioButton naive;
		private JRadioButton dda;
		private JRadioButton bresenham;
		
		/*
		 * @param width - desired width of control
		 * @param height - desired height of control
		 */
		public ControlPanel(int width, int height)
		{
			setSize(new Dimension(width, height));
			setLayout(new GridLayout(2, 3));
			
			JLabel naiveLabel = new JLabel("naive", JLabel.CENTER);
			naiveLabel.setOpaque(true);
			naiveLabel.setBackground(new Color(255, 0, 0, 80));
			add(naiveLabel);
			
			JLabel ddaLabel = new JLabel("dda", JLabel.CENTER);
			ddaLabel.setOpaque(true);
			ddaLabel.setBackground(new Color(0, 255, 0, 80));
			add(ddaLabel);
			
			JLabel bresenhamLabel = new JLabel("bresenham", JLabel.CENTER);
			bresenhamLabel.setOpaque(true);
			bresenhamLabel.setBackground(new Color(0, 0, 255, 80));
			add(bresenhamLabel);
			
			naive = new JRadioButton();
			naive.setHorizontalAlignment(JRadioButton.CENTER);
			naive.addActionListener(this);
			add(naive);
			
			dda = new JRadioButton();
			dda.setHorizontalAlignment(JRadioButton.CENTER);
			dda.addActionListener(this);
			add(dda);
			
			bresenham = new JRadioButton();
			bresenham.setHorizontalAlignment(JRadioButton.CENTER);
			bresenham.addActionListener(this);
			add(bresenham);
		}

		public void actionPerformed(ActionEvent evt)
		{
			if(evt.getSource() == naive)
			{
				setNaive(naive.isSelected());
			}
			else if(evt.getSource() == dda)
			{
				setDDA(dda.isSelected());
			}
			else if(evt.getSource() == bresenham)
			{
				setBresenham(bresenham.isSelected());
			}
		}
	}
}
