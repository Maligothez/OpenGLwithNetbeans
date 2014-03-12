package rasterDemo;

/*
 * A class to hold data about the user dragging a mouse across a RasterPanel
 * 
 * 
 * @author phingsto
 */
public class RasterEvent {

    // holds start point and current position of mouse
    private Line line;
    // holds current polyline being built
    private Polyline polyline;
    // the RasterPanel that the event relates to
    private RasterPanel source;

    public RasterEvent(RasterPanel source, Polyline polyline, Line line) {
        this.source = source;
        if (line != null) {
            this.line = new Line(line);
        }
        if (polyline != null) {
            this.polyline = new Polyline(polyline);
        }
    }

    public Line getLine() {
        return line;
    }

    public Polyline getPolyline() {
        return polyline;
    }

    public RasterPanel getSource() {
        return source;
    }
}
