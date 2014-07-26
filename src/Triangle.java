import java.awt.Color;
import java.awt.Point;

public class Triangle {
	protected Point a, b, c;
	protected Color colour;
	
	public Triangle (Point a, Point b, Point c)
	{
		this.a = a;
		this.b = b;
		this.c = c;
		
		colour = Color.black;
	}

	public int[] getXPoints()
	{
		int[] points = new int[3];
		points[0] = a.x;
		points[1] = b.x;
		points[2] = c.x;
		
		return points;
	}
	
	public int[] getYPoints()
	{
		int[] points = new int[3];
		points[0] = a.y;
		points[1] = b.y;
		points[2] = c.y;
		
		return points;
	}
	
	public void setColour(Color c)
	{
		colour = c;
	}
	
	public Color getColour()
	{
		return colour;
	}
	
	public Point getCenter()
	{
		double x = (a.x + b.x + c.x)/3.0;
		double y = (a.y + b.y + c.y)/3.0;
		
		return new Point((int)x, (int)y);
	}
}
