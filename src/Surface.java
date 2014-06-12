import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.JPanel;



public class Surface extends JPanel {
	
	private void draw(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		
		for (int i = 0; i < points.length; i++)
			g2d.drawLine(points[i].x, points[i].y, points[i].x, points[i].y);
	}
	
	@Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        draw(g);
    }

	public Point[] points;
}
