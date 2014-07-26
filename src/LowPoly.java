import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class LowPoly extends JFrame{

	private ImageAnalyser image;
	
	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

            	LowPoly lp = new LowPoly();
            	lp.init();
            	lp.setVisible(true);
            }
        });
	}

	public void init()
	{
		image = new ImageAnalyser("res/lena.bmp");
		
		setTitle("Low Poly");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Surface surface = new Surface();
        add(surface);
        
        Insets insets = getInsets();
        int w = insets.left + insets.right;
        int h = insets.top + insets.bottom;
        
		setSize(image.getImage().getWidth()+w, image.getImage().getHeight()+h);
		setLocationRelativeTo(null);
		
		//make triangulation
		DelaunayTriangulator delaunay = new DelaunayTriangulator();
		
		Triangle[] triangles = delaunay.triangulate(image.generateFeaturePoints());
		
		//colour triangles
		for (int i = 0; i < triangles.length; i++)
			triangles[i].setColour(image.getColourAt(triangles[i].getCenter()));
		
		surface.triangles = triangles;
	}
}
