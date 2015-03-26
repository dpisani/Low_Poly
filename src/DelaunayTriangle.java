import java.awt.Color;
import java.awt.Point;
import java.util.*;


/**
 * @author Dean
 *
 */
public class DelaunayTriangle extends Triangle 
{

	//the triangles has either neighbours or sub triangles
	private DelaunayTriangle[] subTriangles;
	private DelaunayTriangle[] neighbours;
	private DelaunayTriangle parent;
	
	public DelaunayTriangle(Point a, Point b, Point c)
	{
		super(a, b, c);
		
		subTriangles = neighbours = null;
		parent = null;
		
		//wind the vertices counter-clockwise
		
		//use cross product to determine winding
		Point u = new Point(b.x - a.x, b.y - a.y);
		Point v = new Point(c.x - a.x, c.y - a.y);
		
		int polarity = u.x * v.y - u.y * v.x;
		
		//if cross product is negative then winding is clockwise
		if (polarity < 0)
		{
			//swap ordering
			this.b = c;
			this.c = b;
		}
	}
	
	
	private void registerNeighbour(DelaunayTriangle t)
	{
		if (t == null)
			return;
		/* we wish to link up with the smallest eligible triangle
		The main concern is for the leaf triangles to be linked correctly,
		since neighbour connections are only used on leaves. */
		
		if (!isLeaf())
		{
			for (int i = 0; i < subTriangles.length; i++)
			{
				DelaunayTriangle child = subTriangles[i];
				if (child.sharesEdge(t))
				{
					child.registerNeighbour(t);
					//return;
				}
			}
		}
		
		
		if (!sharesEdge(t))
			return;

		
		if (neighbours == null)
			neighbours = new DelaunayTriangle[3];
		
		//add a link, either by filling an empty slot or by replacing a large triangle by its child
		for (int i = 0; i < 3; i++)
		{
			if (neighbours[i] == t || neighbours[i] == null || !neighbours[i].isLeaf())
			{
				neighbours[i] = t;
				return;
			}
			
		}
		
		//we shouldn't ever get here
		System.out.println("Oh no!: Problem registering " + toString() + " with " + t.toString());

	}
	
	public boolean isInCircumcircle (Point p)
	{
		int A = a.x - p.x;
		int B = a.y - p.y;
		int C = ( (a.x*a.x) - (p.x*p.x) ) + ((a.y*a.y)-(p.y*p.y));
		
		int D = b.x - p.x;
		int E = b.y - p.y;
		int F = ( (b.x*b.x) - (p.x*p.x) ) + ((b.y*b.y)-(p.y*p.y));
		
		int G = c.x - p.x;
		int H = c.y - p.y;
		int I = ( (c.x*c.x) - (p.x*p.x) ) + ((c.y*c.y)-(p.y*p.y));
		
		//if determinant > 0 then point is inside
		
		int determinant = (A*E*I + B*F*G + C*D*H) - (C*E*G + B*D*I + A*F*H);
		
		return determinant > 0;
	}
	
	
	private boolean sharesEdge(Triangle t, Point a, Point b)
	{
		int pointsShared = 0;
		
		if (a == t.a || a == t.b || a == t.c)
			pointsShared++;
		if (b == t.a || b == t.b || b == t.c)
			pointsShared++;
		
		return pointsShared == 2;
	}
	
	private DelaunayTriangle getNeighbourOnEdge(Point a, Point b)
	{
		if (neighbours == null)
			return null;
		
		for (int i = 0; i < neighbours.length; i++)
		{
			if (neighbours[i] != null)
			{
				if (sharesEdge(neighbours[i], a, b))
					return neighbours[i];
			}
		}
		
		return null;
	}
	
	public boolean isLeaf()
	{
		return subTriangles == null;
	}
	
	public boolean isRoot()
	{
		return parent == null;
	}
	
	public void registerChildrenTo(Collection<DelaunayTriangle> register)
	{
		if (subTriangles != null)
		{
			for (int i = 0; i < subTriangles.length; i++)
			{
				if (subTriangles[i] != null)
					register.add(subTriangles[i]);	
			}
		}
	}
	
	/**
	 * Part of the incremental step of the Delaunay triangulation.
	 * Splits a triangle into smaller triangles who all share a vertex with a given point.
	 * @param p The point to be added to the triangulation
	 * @return The new triangles created by the split. Returns three points if the point was inside a triangle. Returns four if on an edge. Returns null if point was not inside the triangle.
	 */
	public DelaunayTriangle[] splitOnPoint(Point p)
	{
		//do edge checking
		Point edgeA, edgeB, opposite;
		edgeA = edgeB = opposite = null;

		//check if the point lies on an edge
		if (isOnEdge(a,b,p))
		{
			edgeA = a;
			edgeB = b;
			opposite = c;
		}
		else if (isOnEdge(a,c,p))
		{
			edgeA = a;
			edgeB = c;
			opposite = b;
		}
		else if (isOnEdge(c,b,p))
		{
			edgeA = c;
			edgeB = b;
			opposite = a;
		}
		
		if (!isInTriangle(p) && edgeA == null)
			return null;
		
		if (!isLeaf())
		{
			ArrayList<DelaunayTriangle> split = new ArrayList<DelaunayTriangle>();
			for (int i = 0; i < subTriangles.length; i++)
			{
				DelaunayTriangle[] t = subTriangles[i].splitOnPoint(p);
				if (t != null)
				{
					for (int j = 0; j < t.length; j++)
						split.add(t[j]);
				}
			}
			DelaunayTriangle[] result = new DelaunayTriangle[split.size()];
			split.toArray(result);
			return result;
		}
		else
		{
			
			
			if (edgeA != null)
			{
				//split into 2 triangles
				DelaunayTriangle ta = new DelaunayTriangle(p, edgeA, opposite);
				DelaunayTriangle tb = new DelaunayTriangle(p, edgeB, opposite);
				
				subTriangles = new DelaunayTriangle[2];
				subTriangles[0] = ta;
				subTriangles[1] = tb;
				
				if (ta.isColinear())
					System.out.println("AAH!");
				if (tb.isColinear())
					System.out.println("AAH!");
				
				ta.parent = this;
				tb.parent = this;
				
				//Link up with neighbours
				//ta neighbours
				if (getNeighbourOnEdge(edgeA, opposite) != null)
					getNeighbourOnEdge(edgeA, opposite).registerNeighbour(ta);
				if (getNeighbourOnEdge(edgeA, p) != null)
					getNeighbourOnEdge(edgeA, p).registerNeighbour(ta);
				
				ta.registerNeighbour(getNeighbourOnEdge(edgeA, opposite));
				ta.registerNeighbour(getNeighbourOnEdge(edgeA, p));
				ta.registerNeighbour(tb);
				
				//tb neighbours
				if (getNeighbourOnEdge(edgeB, opposite) != null)
					getNeighbourOnEdge(edgeB, opposite).registerNeighbour(tb);
				if (getNeighbourOnEdge(edgeB, p) != null)
					getNeighbourOnEdge(edgeB, p).registerNeighbour(tb);
				
				tb.registerNeighbour(getNeighbourOnEdge(edgeB, opposite));
				tb.registerNeighbour(getNeighbourOnEdge(edgeB, p));
				tb.registerNeighbour(ta);
				
				//TODO A dangerous move, consider copying the array
				return subTriangles;
			}
			else
			{
				//split into 3 triangles
				DelaunayTriangle ta = new DelaunayTriangle(a, b, p);
				DelaunayTriangle tb = new DelaunayTriangle(a, c, p);
				DelaunayTriangle tc = new DelaunayTriangle(c, b, p);
				
				if (ta.isColinear())
					System.out.println("AAH!");
				if (tb.isColinear())
					System.out.println("AAH!");
				if (tc.isColinear())
					System.out.println("AAH!");
				
				ta.parent = this;
				tb.parent = this;
				tc.parent = this;
				
				subTriangles = new DelaunayTriangle[3];
				subTriangles[0] = ta;
				subTriangles[1] = tb;
				subTriangles[2] = tc;
				
				//Link with neighbours
				//ta
				if (getNeighbourOnEdge(a,b) != null)
					getNeighbourOnEdge(a,b).registerNeighbour(ta);
				tb.registerNeighbour(ta);
				tc.registerNeighbour(ta);
				
				ta.registerNeighbour(getNeighbourOnEdge(a,b));
				
				//tb
				if (getNeighbourOnEdge(a,c) != null)
					getNeighbourOnEdge(a,c).registerNeighbour(tb);
				ta.registerNeighbour(tb);
				tc.registerNeighbour(tb);
				
				tb.registerNeighbour(getNeighbourOnEdge(a,c));
				
				//tc
				if (getNeighbourOnEdge(c,b) != null)
					getNeighbourOnEdge(c,b).registerNeighbour(tc);
				tb.registerNeighbour(tc);
				ta.registerNeighbour(tc);
				
				ta.registerNeighbour(getNeighbourOnEdge(c,b));
				
				//TODO A dangerous move, consider copying the array
				return subTriangles;
			}
		}
	}

}
