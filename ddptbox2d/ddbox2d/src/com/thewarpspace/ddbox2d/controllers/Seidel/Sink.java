package com.thewarpspace.ddbox2d.controllers.Seidel;

public class Sink extends Node {
	public Trapezoid Trapezoid;

    private Sink(Trapezoid trapezoid)
    {
    	super(null, null);
        Trapezoid = trapezoid;
        trapezoid.Sink = this;
    }

    public static Sink Isink(Trapezoid trapezoid)
    {
        if (trapezoid.Sink == null)
            return new Sink(trapezoid);

        return trapezoid.Sink;
    }

    public Sink Locate(Edge edge)
    {
        return this;
    }
}
