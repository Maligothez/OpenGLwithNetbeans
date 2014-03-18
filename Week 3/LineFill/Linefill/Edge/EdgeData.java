package Edge;

import RasterDemo.*;

public class EdgeData
{
    public double col;
    public double inverseM;
    public int endRow;
    
    public EdgeData(Line line)
    {
        col = line.startCol;
        inverseM = 1/line.getM();
        endRow = line.endRow;
    }
    
    public EdgeData(EdgeData data)
    {
        this.col = data.col;
        this.inverseM = data.inverseM;
        this.endRow = data.endRow;
    }
    
    // increment row
    public void step()
    {
        col += inverseM;
    }
}
