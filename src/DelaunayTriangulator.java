import java.awt.Point;
import java.util.*;


public class DelaunayTriangulator {
	
	public Triangle[] triangulate(Point[] points)
	{
		DelaunayTriangle root = new DelaunayTriangle(new Point(0, 0), new Point(500, 0), new Point(500, 5000));
		
		for (int i = 0; i < points.length; i++)
			root.addPoint(points[i]);
		
		LinkedList<Triangle> leaves = reportLeafTriangles(root);
		Triangle[] triangulation = new Triangle[leaves.size()];
		leaves.toArray(triangulation);
		
		return triangulation;
	}
	
	private LinkedList<Triangle> reportLeafTriangles(DelaunayTriangle root)
	{
		LinkedList<Triangle> discovered = new LinkedList<Triangle>();
		
		Stack<DelaunayTriangle> unobserved = new Stack<DelaunayTriangle>();
		
		unobserved.add(root);
		
		while (!unobserved.empty())
		{
			DelaunayTriangle t = unobserved.pop();
			
			if (t.isLeaf())
				discovered.add(t);
			else
				t.registerChildrenTo(unobserved);
		}
		
		return discovered;
	}

}
