package com.thewarpspace.ddbox2d.controllers.Seidel;

import java.util.ArrayList;

public class QueryGraph {
	private Node _head;

    public QueryGraph(Node head)
    {
        _head = head;
    }

    private Trapezoid Locate(Edge edge)
    {
        return _head.Locate(edge).Trapezoid;
    }

    public ArrayList<Trapezoid> FollowEdge(Edge edge)
    {
        ArrayList<Trapezoid> trapezoids = new ArrayList<Trapezoid>();
        trapezoids.add(Locate(edge));
        int j = 0;

        System.out.println("follow edge");
        System.out.println(edge.Q.X);
        
	        while (edge.Q.X > trapezoids.get(j).RightPoint.X)
	        {
	        	System.out.println("looping");
	        	System.out.println(trapezoids.get(j).RightPoint.X);
	        	System.out.println(trapezoids.size());
	            if (edge.IsAbove(trapezoids.get(j).RightPoint))
	            {
	                trapezoids.add(trapezoids.get(j).UpperRight);
	                System.out.println("if");
	            }
	            else
	            {
	                trapezoids.add(trapezoids.get(j).LowerRight);
	                System.out.println("else");
	                System.out.println(trapezoids.size());
	            }
	            j += 1;
	            if(trapezoids.get(j) == null) break;
	        }
        
        return trapezoids;
    }

    private void Replace(Sink sink, Node node)
    {
        if (sink.ParentList.size() == 0)
            _head = node;
        else
            node.Replace(sink);
    }

    public void Case1(Sink sink, Edge edge, Trapezoid[] tList)
    {
        YNode yNode = new YNode(edge, Sink.Isink(tList[1]), Sink.Isink(tList[2]));
        XNode qNode = new XNode(edge.Q, yNode, Sink.Isink(tList[3]));
        XNode pNode = new XNode(edge.P, Sink.Isink(tList[0]), qNode);
        Replace(sink, pNode);
    }

    public void Case2(Sink sink, Edge edge, Trapezoid[] tList)
    {
        YNode yNode = new YNode(edge, Sink.Isink(tList[1]), Sink.Isink(tList[2]));
        XNode pNode = new XNode(edge.P, Sink.Isink(tList[0]), yNode);
        Replace(sink, pNode);
    }

    public void Case3(Sink sink, Edge edge, Trapezoid[] tList)
    {
        YNode yNode = new YNode(edge, Sink.Isink(tList[0]), Sink.Isink(tList[1]));
        Replace(sink, yNode);
    }

    public void Case4(Sink sink, Edge edge, Trapezoid[] tList)
    {
        YNode yNode = new YNode(edge, Sink.Isink(tList[0]), Sink.Isink(tList[1]));
        XNode qNode = new XNode(edge.Q, yNode, Sink.Isink(tList[2]));
        Replace(sink, qNode);
    }
}
