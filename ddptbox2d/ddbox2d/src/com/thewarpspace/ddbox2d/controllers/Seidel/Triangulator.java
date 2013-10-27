package com.thewarpspace.ddbox2d.controllers.Seidel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Collections;

public class Triangulator {
	// Trapezoid decomposition list
    public ArrayList<Trapezoid> Trapezoids;
    public ArrayList<ArrayList<Point>> Triangles;

    // Initialize trapezoidal map and query structure
    private Trapezoid _boundingBox;
    private ArrayList<Edge> _edgeList;
    private QueryGraph _queryGraph;
    private float _sheer = 0.001f;
    private TrapezoidalMap _trapezoidalMap;
    private ArrayList<MonotoneMountain> _xMonoPoly;

    public Triangulator(ArrayList<Point> polyLine, float sheer)
    {
        _sheer = sheer;
        Triangles = new ArrayList<ArrayList<Point>>();
        Trapezoids = new ArrayList<Trapezoid>();
        _xMonoPoly = new ArrayList<MonotoneMountain>();
        _edgeList = InitEdges(polyLine);
        _trapezoidalMap = new TrapezoidalMap();
        _boundingBox = _trapezoidalMap.BoundingBox(_edgeList);
        _queryGraph = new QueryGraph(Sink.Isink(_boundingBox));

        Process();
        System.out.println("aaaaaaaaaaaaaaaa");
    }

    // Build the trapezoidal map and query graph
    private void Process()
    {
    	for(Iterator<Edge> iter = _edgeList.iterator(); iter.hasNext();){
    		Edge edge = (Edge) iter.next();
            ArrayList<Trapezoid> traps = _queryGraph.FollowEdge(edge);

            // Remove trapezoids from trapezoidal Map
            for(Iterator<Trapezoid> iter1 = traps.iterator(); iter1.hasNext();){
            	Trapezoid t = (Trapezoid) iter1.next();
                _trapezoidalMap.Map.remove(t);

                boolean cp = t.Contains(edge.P);
                boolean cq = t.Contains(edge.Q);
                Trapezoid[] tList;

                if (cp && cq)
                {
                    tList = _trapezoidalMap.Case1(t, edge);
                    _queryGraph.Case1(t.Sink, edge, tList);
                }
                else if (cp && !cq)
                {
                    tList = _trapezoidalMap.Case2(t, edge);
                    _queryGraph.Case2(t.Sink, edge, tList);
                }
                else if (!cp && !cq)
                {
                    tList = _trapezoidalMap.Case3(t, edge);
                    _queryGraph.Case3(t.Sink, edge, tList);
                }
                else
                {
                    tList = _trapezoidalMap.Case4(t, edge);
                    _queryGraph.Case4(t.Sink, edge, tList);
                }
                // Add new trapezoids to map
                for(int i = 0; i< tList.length; i++){
                	Trapezoid y = tList[i];
                    _trapezoidalMap.Map.add(y);
                }
            }
            _trapezoidalMap.Clear();
        }

        // Mark outside trapezoids
    	for(Iterator<Trapezoid> iter = _trapezoidalMap.Map.iterator(); iter.hasNext();){
    		Trapezoid t = (Trapezoid) iter.next();
            MarkOutside(t);
        }

        // Collect interior trapezoids
    	for(Iterator<Trapezoid> iter = _trapezoidalMap.Map.iterator(); iter.hasNext();){
    		Trapezoid t = (Trapezoid) iter.next();
            if (t.Inside)
            {
                Trapezoids.add(t);
                t.AddPoints();
            }
        }

        // Generate the triangles
        CreateMountains();
    }

    // Build a list of x-monotone mountains
    private void CreateMountains()
    {
    	for(Iterator<Edge> iter = _edgeList.iterator(); iter.hasNext();){
    		Edge edge = (Edge) iter.next();
            if (edge.MPoints.size() > 2)
            {
                MonotoneMountain mountain = new MonotoneMountain();

                // Sorting is a perfromance hit. Literature says this can be accomplised in
                // linear time, although I don't see a way around using traditional methods
                // when using a randomized incremental algorithm

                // Insertion sort is one of the fastest algorithms for sorting arrays containing 
                // fewer than ten elements, or for lists that are already mostly sorted.

                ArrayList<Point> points = new ArrayList<Point>(edge.MPoints);
                Collections.sort(points);

                for(Iterator<Point> iter1 = points.iterator(); iter1.hasNext();){
                	Point p = (Point) iter1.next();
                    mountain.Add(p);
                }

                // Triangulate monotone mountain
                mountain.Process();

                // Extract the triangles into a single list
                for(Iterator<ArrayList<Point>> iter1 = mountain.Triangles.iterator(); iter1.hasNext();){
                	ArrayList<Point> t = (ArrayList<Point>) iter1.next();
                    Triangles.add(t);
                }

                _xMonoPoly.add(mountain);
            }
        }
    }

    // Mark the outside trapezoids surrounding the polygon
    private void MarkOutside(Trapezoid t)
    {
        if (t.Top == _boundingBox.Top || t.Bottom == _boundingBox.Bottom)
            t.TrimNeighbors();
    }

    // Create segments and connect end points; update edge event pointer
    private ArrayList<Edge> InitEdges(ArrayList<Point> points)
    {
        ArrayList<Edge> edges = new ArrayList<Edge>();

        for (int i = 0; i < points.size() - 1; i++)
        {
            edges.add(new Edge(points.get(i), points.get(i+1)));
        }
        edges.add(new Edge(points.get(0), points.get(points.size()-1)));
        return OrderSegments(edges);
    }

    private ArrayList<Edge> OrderSegments(ArrayList<Edge> edgeInput)
    {
        // Ignore vertical segments!
        ArrayList<Edge> edges = new ArrayList<Edge>();
        
        for(Iterator<Edge> iter = edgeInput.iterator(); iter.hasNext();){
        	Edge e = (Edge) iter.next();
            Point p = ShearTransform(e.P);
            Point q = ShearTransform(e.Q);

            // Point p must be to the left of point q
            if (p.X > q.X)
            {
                edges.add(new Edge(q, p));
            }
            else if (p.X < q.X)
            {
                edges.add(new Edge(p, q));
            }
        }

        // Randomized triangulation improves performance
        // See Seidel's paper, or O'Rourke's book, p. 57 
        Shuffle(edges);
        return edges;
    }

    private static <T> void Shuffle(ArrayList<T> list1)
    {
        Random rng = new Random();
        int n = list1.size();
        while (n > 1)
        {
            n--;
            int k = rng.nextInt(n + 1);
            Collections.swap(list1, k, n);
        }
    }

    // Prevents any two distinct endpoints from lying on a common vertical line, and avoiding
    // the degenerate case. See Mark de Berg et al, Chapter 6.3
    private Point ShearTransform(Point point)
    {
        return new Point(point.X + _sheer * point.Y, point.Y);
    }
}
