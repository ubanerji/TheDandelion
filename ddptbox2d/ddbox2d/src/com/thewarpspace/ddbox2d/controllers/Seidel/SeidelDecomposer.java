package com.thewarpspace.ddbox2d.controllers.Seidel;

import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.math.Vector2;
import com.thewarpspace.ddbox2d.controllers.Seidel.MarchingSquare.Vertices;

public class SeidelDecomposer {
	public static ArrayList<Vertices> ConvexPartition(Vertices vertices, float sheer)
    {       

        ArrayList<Point> compatList = new ArrayList<Point>(vertices.getVertices().size());
        
        for(Iterator<Vector2> iter = vertices.getVertices().iterator(); iter.hasNext();) {
        	Vector2 vertex = (Vector2) iter.next();
            compatList.add(new Point(vertex.x, vertex.x));
        }
        System.out.println(compatList.size());

        Triangulator t = new Triangulator(compatList, sheer);
        	
        ArrayList<Vertices> list = new ArrayList<Vertices>();

        for(Iterator<ArrayList<Point>> iter = t.Triangles.iterator(); iter.hasNext();){
        	ArrayList<Point> triangle = (ArrayList<Point>) iter.next();
            Vertices outTriangles = new Vertices(triangle.size());
            for(Iterator<Point> iter1 = triangle.iterator(); iter1.hasNext();){
            	Point outTriangle = (Point) iter1.next();
            	outTriangles.getVertices().add(new Vector2(outTriangle.X, outTriangle.Y));
            }

            list.add(outTriangles);
        }

        return list;
    }

    /// <summary>
    /// Decompose the polygon into several smaller non-concave polygons.
    /// </summary>
    /// <param name="vertices">The polygon to decompose.</param>
    /// <param name="sheer">The sheer to use if you get bad results, try using a higher value.</param>
    /// <returns>A list of trapezoids</returns>
    public static ArrayList<Vertices> ConvexPartitionTrapezoid(Vertices vertices, float sheer)
    {
        ArrayList<Point> compatList = new ArrayList<Point>(vertices.getVertices().size());
        for(Iterator<Vector2> iter = vertices.getVertices().iterator(); iter.hasNext();){
        	Vector2 vertex = (Vector2) iter.next();
            compatList.add(new Point(vertex.x, vertex.y));
        }

        Triangulator t = new Triangulator(compatList, sheer);

        ArrayList<Vertices> list = new ArrayList<Vertices>();
        
        for(Iterator<Trapezoid> iter = t.Trapezoids.iterator(); iter.hasNext();){
        	Trapezoid trapezoid = (Trapezoid) iter.next();
            Vertices verts = new Vertices();

            ArrayList<Point> points = trapezoid.GetVertices();
            for(Iterator<Point> iter1 = points.iterator(); iter1.hasNext();){
            	Point point = (Point) iter1.next();
                verts.getVertices().add(new Vector2(point.X, point.Y));
            }

            list.add(verts);
        }

        return list;
    }
}
