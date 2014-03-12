package rasterDemo;

/*
 * An interface to enable a class to be told about a user dragging a mouse
 * in a RasterPanel
 * 
 * @author phingsto
 */
public interface RasterListener {

    // called when the user starts dragging the mouse
    public void start(RasterEvent event);

    // called when the user drags the mouse
    public void move(RasterEvent event);

    // called when the user drags the mouse
    public void next(RasterEvent event);

    // called when the user releases the mouse inside the raster area
    public void end(RasterEvent event);

    // called when the user releases the mouse outside the raster area
    public void abort(RasterEvent event);
}
