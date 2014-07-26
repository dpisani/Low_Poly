import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;

import javax.swing.JPanel;



public class Surface extends JPanel {
	
	private void draw(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		
		for (int i = 0; i < triangles.length; i++)
		{
			g2d.setColor(triangles[i].getColour());
			g2d.fillPolygon(triangles[i].getXPoints(), triangles[i].getYPoints(), 3);
		}
	}
	
	@Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        draw(g);
    }

	public Triangle[] triangles;
}
