import java.awt.Color;
import java.awt.Point;
import java.util.Collection;
import java.util.LinkedList;


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
		
		//if cross product is positive then winding is clockwise
		if (polarity > 0)
		{
			//swap ordering
			this.b = c;
			this.c = b;
		}
	}
	
	
	private void registerNeighbour(DelaunayTriangle t) throws Exception
	{
		if (t == null)
			return;
		/* we wish to link up with the smallest eligible triangle
		The main concern if for the leaf triangles to be linked correctly,
		since neighbour connections are only used on leaves. */
		
		if (!isLeaf())
		{
			for (int i = 0; i < subTriangles.length; i++)
			{
				DelaunayTriangle child = subTriangles[i];
				if (child.doesShareEdge(t))
				{
					child.registerNeighbour(t);
					return;
				}
			}
		}
		
		
		if (!doesShareEdge(t))
			return;

		//At this point we are either a leaf or the smallest triangle that shares a whole edge
		
		if (neighbours == null)
			neighbours = new DelaunayTriangle[3];
		
		//add a link, either by filling an empty slot or by replacing a large triangle by its child
		for (int i = 0; i < 3; i++)
		{
			if (neighbours[i] == t || neighbours[i] == t.parent || neighbours[i] == null)
			{
				neighbours[i] = t;
				return;
			}
			
		}
		
		//we shouldn't ever get here
		throw new Exception("Improperly linked triangle");

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
	
	public boolean isInTriangle(Point p)
	{
		Point v0 = new Point(c.x-a.x, c.y-a.y);
		Point v1 = new Point(b.x-a.x, b.y-a.y);
		Point v2 = new Point(p.x-a.x, p.y-a.y);
		
		// Compute dot products
		int dot00 = dot(v0, v0);
		int dot01 = dot(v0, v1);
		int dot02 = dot(v0, v2);
		int dot11 = dot(v1, v1);
		int dot12 = dot(v1, v2);
		
		// Compute barycentric coordinates
		double invDenom = 1 / (double)(dot00 * dot11 - dot01 * dot01);
		double u = (dot11 * dot02 - dot01 * dot12) * invDenom;
		double v = (dot00 * dot12 - dot01 * dot02) * invDenom;

		// Check if point is in triangle
		return (u >= 0) && (v >= 0) && (u + v <= 1);
	}
	
	private int dot (Point a, Point b)
	{
		return a.x*b.x + a.y+b.y;
	}

	public boolean doesShareEdge(Triangle t)
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
	
	private boolean doesShareEdge(Triangle t, Point a, Point b)
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
				if (doesShareEdge(neighbours[i], a, b))
					return neighbours[i];
			}
		}
		
		return null;
	}
	
	public boolean isLeaf()
	{
		return subTriangles == null;
	}
	
	public void registerChildrenTo(Collection<DelaunayTriangle> register)
	{
		if (subTriangles != null)
		{
			for (int i = 0; i < 3; i++)
			{
				if (subTriangles[i] != null)
					register.add(subTriangles[i]);	
			}
		}
	}
	
	/**
	 * The main incremental step for performing a Delaunay triangulation.
	 * Adds a point into the set and restores necessary invariants.
	 * @param p The point to be added to the triangulation
	 */
	public void addPoint(Point p)
	{
		if (!isInTriangle(p))
			return;
		
		if (!isLeaf())
		{
			for (int i = 0; i < subTriangles.length; i++)
				subTriangles[i].addPoint(p);
		}
		else
		{
			//check if the point lies on an edge
			Point edgeA, edgeB, opposite;
			edgeA = edgeB = opposite = null;
			
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
			
			if (edgeA != null)
			{
				//split into 2 triangles
				DelaunayTriangle ta = new DelaunayTriangle(p, edgeA, opposite);
				DelaunayTriangle tb = new DelaunayTriangle(p, edgeB, opposite);
				
				subTriangles = new DelaunayTriangle[2];
				subTriangles[0] = ta;
				subTriangles[1] = tb;
				//link up with neighbours
				try 
				{
					//ta neighbours
					getNeighbourOnEdge(edgeA, opposite).registerNeighbour(ta);
					if (getNeighbourOnEdge(edgeA, p) != null)
						getNeighbourOnEdge(edgeA, p).registerNeighbour(ta);
					
					ta.registerNeighbour(getNeighbourOnEdge(edgeA, opposite));
					ta.registerNeighbour(getNeighbourOnEdge(edgeA, p));
					ta.registerNeighbour(tb);
					
					//tb neighbours
					getNeighbourOnEdge(edgeB, opposite).registerNeighbour(tb);
					if (getNeighbourOnEdge(edgeB, p) != null)
						getNeighbourOnEdge(edgeB, p).registerNeighbour(tb);
					
					tb.registerNeighbour(getNeighbourOnEdge(edgeB, opposite));
					tb.registerNeighbour(getNeighbourOnEdge(edgeB, p));
					tb.registerNeighbour(ta);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else
			{
				//split into 3 triangles
				DelaunayTriangle ta = new DelaunayTriangle(a, b, p);
				DelaunayTriangle tb = new DelaunayTriangle(a, c, p);
				DelaunayTriangle tc = new DelaunayTriangle(c, b, p);
				
				subTriangles = new DelaunayTriangle[3];
				subTriangles[0] = ta;
				subTriangles[1] = tb;
				subTriangles[2] = tc;
				
				//link up neighbours
								
				try 
				{
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
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	private boolean isOnEdge(Point a, Point b, Point p)
	{
		int asd = (p.x - a.x)*(p.x - a.x) + (p.y - a.y)*(p.y - a.y);
		int bsd = (p.x - b.x)*(p.x - b.x) + (p.y - b.y)*(p.y - b.y);
		
		return asd == bsd;
	}
}
