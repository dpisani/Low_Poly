import java.awt.Color;
import java.awt.Insets;
import java.awt.Point;

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
		DelaunayTriangulator delaunay = new DelaunayTriangulator(image.getImage().getWidth(), image.getImage().getHeight());
		Point[] testPoints = new Point[2];
		testPoints[0] = new Point(30,30);
		testPoints[1] = new Point(30,100);
		delaunay.addPoints(testPoints);//image.generateFeaturePoints());
		delaunay.addPoints(image.generateFeaturePoints());
		Triangle[] triangles = delaunay.getTriangulation();
		
		
		//colour triangles
		for (int i = 0; i < triangles.length; i++)
		{
			float u = triangles[i].getCenter().x / (float)image.getImage().getWidth();
			float v = triangles[i].getCenter().y / (float)image.getImage().getHeight();
			if (u < 0) u = 0; if (u > 1) u = 1;
			if (v < 0) v = 0; if (v > 1) v = 1;
			Color c = new Color(u,v,0.5f);
			triangles[i].setColour(c);
			//triangles[i].setColour(image.getColourAt(triangles[i].getCenter()));
		}
		
		surface.triangles = triangles;
		surface.points = image.generateFeaturePoints();
	}
}
