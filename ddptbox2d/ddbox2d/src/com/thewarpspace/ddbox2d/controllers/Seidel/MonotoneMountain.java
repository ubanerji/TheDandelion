package com.thewarpspace.ddbox2d.controllers.Seidel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class MonotoneMountain {
	// Almost Pi!
    private final float PiSlop = 3.1f;

    // Triangles that constitute the mountain
    public ArrayList<ArrayList<Point>> Triangles;
    private HashSet<Point> _convexPoints;
    private Point _head;

    // Monotone mountain points
    private ArrayList<Point> _monoPoly;

    // Used to track which side of the line we are on
    private boolean _positive;
    private int _size;
    private Point _tail;

    public MonotoneMountain()
    {
        _size = 0;
        _tail = null;
        _head = null;
        _positive = false;
        _convexPoints = new HashSet<Point>();
        _monoPoly = new ArrayList<Point>();
        Triangles = new ArrayList<ArrayList<Point>>();
    }

    // Append a point to the list
    public void Add(Point point)
    {
        if (_size == 0)
        {
            _head = point;
            _size = 1;
        }
        else if (_size == 1)
        {
            // Keep repeat points out of the list
            _tail = point;
            _tail.Prev = _head;
            _head.Next = _tail;
            _size = 2;
        }
        else
        {
            // Keep repeat points out of the list
            _tail.Next = point;
            point.Prev = _tail;
            _tail = point;
            _size += 1;
        }
    }

    // Remove a point from the list
    public void Remove(Point point)
    {
        Point next = point.Next;
        Point prev = point.Prev;
        point.Prev.Next = next;
        point.Next.Prev = prev;
        _size -= 1;
    }

    // Partition a x-monotone mountain into triangles O(n)
    // See "Computational Geometry in C", 2nd edition, by Joseph O'Rourke, page 52
    public void Process()
    {
        // Establish the proper sign
        _positive = AngleSign();
        // create monotone polygon - for dubug purposes
        GenMonoPoly();

        // Initialize internal angles at each nonbase vertex
        // Link strictly convex vertices into a list, ignore reflex vertices
        Point p = _head.Next;
        while (p.Neq(_tail))
        {
            float a = Angle(p);
            // If the point is almost colinear with it's neighbor, remove it!
            if (a >= PiSlop || a <= -PiSlop || a == 0.0f)
                Remove(p);
            else if (IsConvex(p))
                _convexPoints.add(p);
            p = p.Next;
        }

        Triangulate();
    }

    private void Triangulate()
    {
        while (_convexPoints.size() != 0)
        {
            Iterator<Point> e = _convexPoints.iterator();            
            Point ear = e.next();

            _convexPoints.remove(ear);
            Point a = ear.Prev;
            Point b = ear;
            Point c = ear.Next;
            ArrayList<Point> triangle = new ArrayList<Point>(3);
            triangle.add(a);
            triangle.add(b);
            triangle.add(c);

            Triangles.add(triangle);

            // Remove ear, update angles and convex list
            Remove(ear);
            if (Valid(a))
                _convexPoints.add(a);
            if (Valid(c))
                _convexPoints.add(c);
        }

        System.out.println("Triangulation bug, please report");
    }

    private boolean Valid(Point p)
    {
        return p.Neq(_head) && p.Neq(_tail) && IsConvex(p);
    }

    // Create the monotone polygon
    private void GenMonoPoly()
    {
        Point p = _head;
        while (p != null)
        {
            _monoPoly.add(p);
            p = p.Next;
        }
    }

    private float Angle(Point p)
    {
        Point a = Point.substract(p.Next, p);
        Point b = Point.substract(p.Prev, p);
        return (float)Math.atan2(a.Cross(b), a.Dot(b));
    }

    private boolean AngleSign()
    {
        Point a = Point.substract(_head.Next , _head);
        Point b = Point.substract(_tail , _head);
        return Math.atan2(a.Cross(b), a.Dot(b)) >= 0;
    }

    // Determines if the inslide angle is convex or reflex
    private boolean IsConvex(Point p)
    {
        if (_positive != (Angle(p) >= 0))
            return false;
        return true;
    }
}
