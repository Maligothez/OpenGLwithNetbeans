package rasterDemo;

import java.util.ArrayList;
import java.util.Iterator;

/*
 * A class to manage an arbitrary list of lines
 * 
 * 
 * @author phingsto
 */
public class Polyline
{
	private ArrayList<Line> lines; // an arbitrary list of lines

	public Polyline()
	{
		lines = new ArrayList<Line>();
	}
	
	public Polyline(Polyline polyline) 
	{
		this();
		
		for(Line line: polyline.lines)
		{
			add(new Line(line));
		}
	}
	
	public void add(Line line) 
	{
		lines.add(new Line(line));
	}
	
	public ArrayList<Line> getLines()
	{
		return lines;
	}
}
