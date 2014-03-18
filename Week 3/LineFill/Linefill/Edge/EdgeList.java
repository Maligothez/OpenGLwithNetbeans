package Edge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class EdgeList
{
    private static EdgeDataComparator comparator = new EdgeDataComparator();
    
    private ArrayList<EdgeData> edgeList;
    
    public EdgeList()
    {
        edgeList = new ArrayList<EdgeData>();
    }
    
    public void add(EdgeData data)
    {
        if(edgeList.isEmpty() ||  data.col < ((EdgeData)edgeList.get(0)).col)
        {
            edgeList.add(0, new EdgeData(data));
        }
        else
        {
            int prev = 0;
            
            do
            {
                prev++;
            }
            while(prev < edgeList.size() && data.col >= ((EdgeData)edgeList.get(prev)).col);
            
            edgeList.add(prev, new EdgeData(data));
        }
    }
    
    public void add(EdgeList edges)
    {
        for(EdgeData data: edges.getEdges())
        {
            add(data);
        }
    }
    
    public void step(int row)
    {
        // remove completed ones
        boolean changed = false;
        do
        {
            changed = false;
            for(int i = 0; i < edgeList.size(); i++)
            {
                if(edgeList.get(i).endRow < row)
                {
                    edgeList.remove(i);
                    changed = true;
                    break;
                }
            }
        }while(changed);
        
        // step to next row
        for(EdgeData data: edgeList)
        {
            data.step();
        }
        
        // re-sort
        Collections.sort(edgeList, comparator);
    }
    
    public ArrayList<EdgeData> getEdges()
    {
        return edgeList;
    }
}
