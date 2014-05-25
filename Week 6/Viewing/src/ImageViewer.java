import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ImageViewer
{
    // approximate size of openGL rendering area in pixels
    private static final int WIDTH = 700;
    private static final int HEIGHT = 500;
    // approximate height of panel to hold user controls
    private static final int CONTROL_HEIGHT = 150;
    
    private static final String MAP_NAME = "london-map-tourism.jpg";
    
    private ControlPanel controlPanel;
    private ImagePanel imagePanel;
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        new ImageViewer();
    }
    
    public ImageViewer()
    {
        JFrame frame = new JFrame("A JOGL image viewer");
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(WIDTH, HEIGHT+CONTROL_HEIGHT));
        frame.setLayout(new BorderLayout());
        
        // an openGL panel in the middle
        imagePanel = new ImagePanel(WIDTH, HEIGHT, MAP_NAME);
        frame.add(imagePanel, BorderLayout.CENTER);
        
        // and a control panel at the top
        controlPanel = new ControlPanel(WIDTH, CONTROL_HEIGHT);
        frame.add(controlPanel, BorderLayout.NORTH);
        
        frame.setVisible(true);
        
        imagePanel.startJOGL();
    }
    
        /*
         * Inner class for a panel to hold the user controls
         */
    public class ControlPanel extends JPanel implements ActionListener
    {
        private static final long serialVersionUID = 1L;
        
        private static final double ZOOM_FACTOR = 1.1;
        private static final double PAN_FACTOR = 0.1;
        
        private JButton reset;
        private JButton zoomIn;
        private JButton zoomOut;
        private JButton panLeft;
        private JButton panRight;
        private JButton panUp;
        private JButton panDown;
        
                /*
                 * @param width - desired width of control
                 * @param height - desired height of control
                 */
        public ControlPanel(int width, int height)
        {
            JPanel innerPanel = new JPanel();
            
            innerPanel.setSize(new Dimension(width, height));
            
            reset  = new JButton("reset");
            reset.addActionListener(this);
            innerPanel.add(reset);
            
            zoomIn = new JButton("zoom in");
            zoomIn.addActionListener(this);
            innerPanel.add(zoomIn);
            
            zoomOut = new JButton("zoom out");
            zoomOut.addActionListener(this);
            innerPanel.add(zoomOut);
            
            panLeft = new JButton("pan left");
            panLeft.addActionListener(this);
            innerPanel.add(panLeft);
            
            panRight = new JButton("pan right");
            panRight.addActionListener(this);
            innerPanel.add(panRight);
            
            panUp = new JButton("pan up");
            panUp.addActionListener(this);
            innerPanel.add(panUp);
            
            panDown = new JButton("pan down");
            panDown.addActionListener(this);
            innerPanel.add(panDown);
            
            add(innerPanel);
        }
        
        public void actionPerformed(ActionEvent arg0)
        {
            Object source = arg0.getSource();
            
            if(source == reset)
            {
                imagePanel.reset();
            }
            if(source == zoomIn)
            {
                imagePanel.zoomIn(ZOOM_FACTOR);
            }
            if(source == zoomOut)
            {
                imagePanel.zoomOut(ZOOM_FACTOR);
            }
            if(source == panLeft)
            {
                imagePanel.panLeft(PAN_FACTOR);
            }
            if(source == panRight)
            {
                imagePanel.panRight(PAN_FACTOR);
            }
            if(source == panUp)
            {
                imagePanel.panUp(PAN_FACTOR);
            }
            if(source == panDown)
            {
                imagePanel.panDown(PAN_FACTOR);
            }
        }
    }
}
