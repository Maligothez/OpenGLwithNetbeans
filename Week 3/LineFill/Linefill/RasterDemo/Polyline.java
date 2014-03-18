package RasterDemo;

import java.util.ArrayList;
import java.util.Iterator;

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
