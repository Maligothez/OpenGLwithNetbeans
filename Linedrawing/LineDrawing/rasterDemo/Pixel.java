package rasterDemo;

/**
 *
 * A class to represent a pixel drawn on a rasterPanel
 *
 * @author phingsto
 *
 */
public class Pixel {

    public int col; //the column number (starting from column 0 on the left)
    public int row; //the row number (starting from row 0 at the bottom)

    public float red; //the red component of the colour of this pixel (0-1)
    public float green; //the green component of the colour of this pixel (0-1)
    public float blue; //the blue component of the colour of this pixel (0-1)
    public float alpha; //the alpha component of the colour of this pixel (0-1)

    /**
     *
     * @param col - the column number (starting from column 0 on the left)
     * @param row - the row number (starting from row 0 at the bottom)
     * @param red - the red component of the colour of this pixel (0-1)
     * @param green - the green component of the colour of this pixel (0-1)
     * @param blue - the blue component of the colour of this pixel (0-1)
     * @param alpha - the alpha component of the colour of this pixel (0-1)
     */
    public Pixel(int col, int row, float red, float green, float blue, float alpha) {
        this.col = col;
        this.row = row;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    /**
     * @param col - the column number (starting from column 0 on the left)
     * @param row - the row number (starting from row 0 at the bottom)
     * @param colour - a java.awt.Color for this pixel
     */
    public Pixel(int col, int row, java.awt.Color colour) {
        this.col = col;
        this.row = row;
        this.red = colour.getRed() / (float) 255;
        this.green = colour.getGreen() / (float) 255;
        this.blue = colour.getBlue() / (float) 255;
        this.alpha = colour.getAlpha() / (float) 255;
    }

    /**
     * @param colour - change pixel colour to match
     */
    public void setColour(java.awt.Color colour) {
        this.red = colour.getRed() / (float) 255;
        this.green = colour.getGreen() / (float) 255;
        this.blue = colour.getBlue() / (float) 255;
        this.alpha = colour.getAlpha() / (float) 255;
    }

    public java.awt.Color getColour() {
        return new java.awt.Color((int) (red * 255), (int) (green * 255), (int) (blue * 255), (int) (alpha * 255));
    }
}
