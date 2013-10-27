package com.thewarpspace.ddbox2d.controllers.Seidel;

import java.util.ArrayList;
import java.util.Iterator;

public abstract class Node {
	protected Node LeftChild;
    public ArrayList<Node> ParentList;
    protected Node RightChild;

    protected Node(Node left, Node right)
    {
        ParentList = new ArrayList<Node>();
        LeftChild = left;
        RightChild = right;

        if (left != null)
            left.ParentList.add(this);
        if (right != null)
            right.ParentList.add(this);
    }

    public abstract Sink Locate(Edge s);

    // Replace a node in the graph with this node
    // Make sure parent pointers are updated
    public void Replace(Node node)
    {
    	for(Iterator<Node> iter = node.ParentList.iterator(); iter.hasNext();){
    		Node parent = (Node) iter.next();        
            // Select the correct node to replace (left or right child)
            if (parent.LeftChild == node)
                parent.LeftChild = this;
            else
                parent.RightChild = this;
        }
    	for(Iterator<Node> iter = node.ParentList.iterator(); iter.hasNext();){
    		Node parent = (Node) iter.next();      
    		ParentList.add(parent);
    	}
    }
}
