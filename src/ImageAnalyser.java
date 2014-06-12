import java.awt.Color;
import java.awt.Point;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.imageio.ImageIO;

public class ImageAnalyser {
	
	private BufferedImage image;
	
	public ImageAnalyser (String path)
	{
		try {
			image = ImageIO.read(new File(path));
		} catch (IOException e) {
			System.out.println("An error occured");
		}
	}
	
	public BufferedImage getImage()
	{
		return image;
	}
	
	public Point[] generateFeaturePoints()
	{
		return getCannyEdges();
	}
	
	private int clampX(int x)
	{
		int width = image.getWidth();
		if (x < 0)
			x = 0;
		else if (x > width-1)
			x = width-1;
		
		return x;
	}
	
	private int clampY(int y)
	{
		int height = image.getHeight();
		if (y < 0)
			y = 0;
		else if (y > height-1)
			y = height-1;
		
		return y;
	}
	
	private Point[] getCannyEdges()
	{
		return findEdges(smoothImage(), 10);
	}
	
	private BufferedImage smoothImage()
	{
		int[][] mask = {
				{2,4,5,4,2},
				{4,9,12,9,4},
				{5,12,15,12,5},
				{4,9,12,9,4},
				{2,4,5,4,2}
		};
		int width = image.getWidth();
		int height = image.getHeight();
		
		BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		for (int i = 0; i < width; i++)
		{
			for (int j = 0; j < height; j++)
			{
				int r,g,b;
				r = g = b = 0;
				for (int k = -2; k <= 2; k++)
				{
					for (int l = -2; l <= 2; l++)
					{
						int x = i + k;
						x = clampX(x);
						
						int y = j + l;
						y = clampY(y);
						
						Color c = new Color(image.getRGB(x,y));
						
						int intensity = mask[k+2][l+2];
						r += c.getRed()*intensity;
						g += c.getGreen()*intensity;
						b += c.getBlue()*intensity;
					}
				}
				
				r = r / 159;
				g = g / 159;
				b = b / 159;
				
				Color nc = new Color(r,g,b);
				newImage.setRGB(i, j, nc.getRGB());
			}
		}
		
		return newImage;
	}
	
	private Point[] findEdges(BufferedImage img, double threshold)
	{
		int[][] maskGy = {
				{-1,0,1},
				{-2,0,2},
				{-1,0,1}
		};
		
		int[][] maskGx = {
				{1,2,1},
				{0,0,0},
				{-1,-2,-1}
		};
		
		int width = img.getWidth();
		int height = img.getHeight();
		int[][] edgeStrength = new int[width][height];
		int[][] edgeDirection = new int[width][height];
		ArrayList<Point> edges = new ArrayList<Point>();
		
		for (int i = 0; i < width; i++)
		{
			for (int j = 0; j < height; j++)
			{
				//get GX
				int gx = 0;
				//apply the mask
				for (int k = -1; k <= 1; k++)
				{
					for (int l = -1; l <= 1; l++)
					{
						//clamp positions
						int x = i + k;
						x = clampX(x);
						
						int y = j + l;
						y = clampY(y);
						
						//apply mask to grayscale intensity
						int intensity = maskGx[k+1][l+1];
						Color c = new Color(img.getRGB(x, y));
						int val = (c.getRed() + c.getBlue() + c.getGreen())/3;
						gx += intensity * val;
					}
				}
				
				//Get gy
				int gy = 0;
				for (int k = -1; k <= 1; k++)
				{
					for (int l = -1; l <= 1; l++)
					{
						int x = i + k;
						x = clampX(x);
						
						int y = j + l;
						y = clampY(y);
						
						int intensity = maskGy[k+1][l+1];
						Color c = new Color(img.getRGB(x, y));
						int val = (c.getRed() + c.getBlue() + c.getGreen())/3;
						gy += intensity * val;
					}
				}
				
				gx = Math.abs(gx);
				gy = Math.abs(gy);
				
				//calculate combined intensity
				edgeStrength[i][j] = gx + gy;
				
				//find direction
				int direction;
				if (gx == 0)
				{
					if (gy == 0)
						direction = 0;
					else
						direction = 90;
				}
				else
				{
					double dir = Math.atan2(gy, gx);
					//to degrees
					dir = dir * (180/Math.PI);
					//snap to 4 directions
					if (dir > 22.5 && dir <= 67.5)
						direction = 45;
					else if (dir > 67.5 && dir <= 112.5)
						direction = 90;
					else if (dir > 112.5 && dir <= 157.5)
						direction = 135;
					else direction = 0;
				}
				
				edgeDirection[i][j] = direction;
			}
		}
		
		//perform non maximum suppression
		for (int i = 0; i < width; i++)
		{
			for (int j = 0; j < height; j++)
			{
				boolean max = true;
				int strength = edgeStrength[i][j];
				switch (edgeDirection[i][j])
				{
				case 0:
					if (edgeStrength[clampX(i-1)][j] <= strength)
						max = false;
					if (edgeStrength[clampX(i+1)][j] <= strength)
						max = false;
					break;
				case 90:
					if (edgeStrength[i][clampY(j-1)] <= strength)
						max = false;
					if (edgeStrength[i][clampY(j+1)] <= strength)
						max = false;
					break;
				case 135:
					if (edgeStrength[clampX(i-1)][clampY(j-1)] <= strength)
						max = false;
					if (edgeStrength[clampX(i+1)][clampY(j+1)] <= strength)
						max = false;
					break;
				case 45:
					if (edgeStrength[clampX(i+1)][clampY(j-1)] <= strength)
						max = false;
					if (edgeStrength[clampX(i-1)][clampY(j+1)] <= strength)
						max = false;
					break;
				}
				
				//report edges
				if (max && strength > threshold)
					edges.add(new Point(i,j));
			}
		}
		
		Point[] points = new Point[edges.size()];
		edges.toArray(points);
		
		return points;
	}

	private LinkedList<LinkedList<Point>> clusterStep(Point[] points, Point[] centroids)
	{
		LinkedList<LinkedList<Point>> clusters = new LinkedList<LinkedList<Point>>();
		
		return clusters;
	}
}