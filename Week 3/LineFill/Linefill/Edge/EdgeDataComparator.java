package Edge;

import java.util.Comparator;

public class EdgeDataComparator implements Comparator
{
    
    public EdgeDataComparator()
    {
    }
    
    public int compare(Object arg0, Object arg1)
    {
        EdgeData e1 = (EdgeData)arg0;
        EdgeData e2 = (EdgeData)arg1;
        
        return e1.col < e2.col? -1: e1.col > e2.col? 1: 0;
    }
}
