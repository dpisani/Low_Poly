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
	
	protected int dot (Point a, Point b)
	{
		return a.x*b.x + a.y+b.y;
	}
	
	public boolean sharesEdge(Triangle t)
	{
		int pointsShared = 0;
		
		if (a == t.a || a == t.b || a == t.c)
			pointsShared++;
		if (b == t.a || b == t.b || b == t.c)
			pointsShared++;
		if (c == t.a || c == t.b || c == t.c)
			pointsShared++;
		
		return pointsShared > 1;
	}
	
	protected boolean edgeLiesOnLineSegment(Point a, Point b)
	{
		int pointsShared = 0;
		
		pointsShared += isOnEdge(a,b, this.a) ? 1:0;
		pointsShared += isOnEdge(a,b, this.b) ? 1:0;
		pointsShared += isOnEdge(a,b, this.c) ? 1:0;
			
		return pointsShared >= 2;
	}
	
	public boolean bordersTriangle(Triangle t)
	{
		return edgeLiesOnLineSegment(t.a,t.b) || edgeLiesOnLineSegment(t.a,t.c) || edgeLiesOnLineSegment(t.c,t.b);
	}
	
	/**
	 * Tests whether a point lies on a given line segment
	 * @param a first end point of segment
	 * @param b second end point of segment
	 * @param p point being tested
	 * @return whether p lies on the line segment ab
	 */
	protected boolean isOnEdge(Point a, Point b, Point p)
	{
		int asd = (p.x - a.x)*(p.x - a.x) + (p.y - a.y)*(p.y - a.y);
		int bsd = (p.x - b.x)*(p.x - b.x) + (p.y - b.y)*(p.y - b.y);
		int abl = (a.x - b.x)*(a.x - b.x) + (a.y - b.y)*(a.y - b.y);
		
		return asd + bsd == abl;
	}
	
	public boolean isInTriangle(Point p)
	{	
		double invdenom = 1 / (double)(((b.y - c.y)*(a.x - c.x) + (c.x - b.x)*(a.y - c.y)));
		
		double i = ((b.y - c.y)*(p.x - c.x) + (c.x - b.x)*(p.y - c.y)) * invdenom;
		double j = ((c.y - a.y)*(p.x - c.x) + (a.x - c.x)*(p.y - c.y)) * invdenom;
		double k = 1 - i - j;
		
		return i >= 0 && i <=1 && j >=0 && j <= 1 && k >= 0 && k <= 1;
	}
	
	protected boolean isColinear()
	{
		return (b.y - a.y)*(c.x - a.x) == (c.y - b.y)*(b.x-a.x);
	}
	
	protected boolean isColinear(Point a, Point b, Point c)
	{
		return (b.y - a.y)*(c.x - a.x) == (c.y - b.y)*(b.x-a.x);
	}
	
	public String toString()
	{
		return "a: " + a + " b: " + b + " c: " + c;
	}
}
