import java.awt.Point;
import java.util.*;

public class DelaunayTriangulator {
	
	private DelaunayTriangle root;
	
	public DelaunayTriangulator(int width, int height)
	{
		root = new DelaunayTriangle(new Point(-width*3, -height), new Point(width*2, -height), new Point(width*3, height*3));
		root.splitOnPoint(new Point(0,0));
		root.splitOnPoint(new Point(width,0));
		root.splitOnPoint(new Point(0,height));
		root.splitOnPoint(new Point(width,height));
	}
	
	public void addPoints(Point[] points)
	{
		for (int i = 0; i < points.length; i++)
		{
			insertPoint(points[i]);
		}
	}
	
	public Triangle[] getTriangulation()
	{
		LinkedList<Triangle> leaves = reportLeafTriangles();
		Triangle[] triangulation = new Triangle[leaves.size()];
		leaves.toArray(triangulation);
		
		return triangulation;
	}
	
	private LinkedList<Triangle> reportLeafTriangles()
	{
		LinkedList<Triangle> discovered = new LinkedList<Triangle>();
		
		Stack<DelaunayTriangle> unobserved = new Stack<DelaunayTriangle>();
		
		unobserved.add(root);
		
		while (!unobserved.empty())
		{
			DelaunayTriangle t = unobserved.pop();
			
			if (t.isLeaf() && !t.bordersTriangle(root))
				discovered.add(t);
			else
				t.registerChildrenTo(unobserved);
		}
		
		return discovered;
	}
	
	private void insertPoint(Point p)
	{
		DelaunayTriangle[] split = root.splitOnPoint(p);
	}

}
