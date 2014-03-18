package RasterDemo;

/**
 *
 * A class to represent a line drawn on a rasterPanel
 *
 * @author phingsto
 *
 */
public class Line
{
    public int startCol; //the column number where the line starts (starting from column 0 on the left)
    public int startRow; //the row number where the line starts (starting from row 0 at the bottom)
    public int endCol; //the column number where the line ends (starting from column 0 on the left)
    public int endRow; //the row number where the line ends (starting from row 0 at the bottom)
    public float red; //the red component of the colour of this pixel (0-1)
    public float green; //the green component of the colour of this pixel (0-1)
    public float blue; //the blue component of the colour of this pixel (0-1)
    public float alpha; //the alpha component of the colour of this pixel (0-1)
    
    public Line(int startCol, int startRow, int endCol, int endRow, float red, float green, float blue, float alpha)
    {
        this.startCol = startCol;
        this.startRow = startRow;
        this.endCol = endCol;
        this.endRow = endRow;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }
    
    public Line(Line line)
    {
        this.startCol = line.startCol;
        this.startRow = line.startRow;
        this.endCol = line.endCol;
        this.endRow = line.endRow;
        this.red = line.red;
        this.green = line.green;
        this.blue = line.blue;
        this.alpha = line.alpha;
    }
    
    public Line upwards()
    {
        if(isUp())
        {
            return this;
        }
        else
        {
            return new Line(endCol, endRow, startCol, startRow, red, green, blue, alpha);
        }
    }
    
    public boolean isUp()
    {
        return startRow < endRow;
    }
    
    public boolean isDown()
    {
        return startRow > endRow;
    }
    
        /*
         * @returns the slope of the line
         *
         */
    public double getM()
    {
        return (endRow-startRow)/(double)(endCol-startCol);
    }
}
