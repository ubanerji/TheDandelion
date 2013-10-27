package com.thewarpspace.ddbox2d.controllers.Seidel;

import java.util.HashSet;
import java.util.Iterator;

import com.badlogic.gdx.math.Vector2;
import com.thewarpspace.ddbox2d.controllers.Seidel.MarchingSquare.Vertices;

public class SimplifyTools {
	/// <summary>
    /// Provides a set of tools to simplify polygons in various ways.
    /// </summary>
    
        /// <summary>
        /// Removes all collinear points on the polygon.
        /// </summary>
        /// <param name="vertices">The polygon that needs simplification.</param>
        /// <param name="collinearityTolerance">The collinearity tolerance.</param>
        /// <returns>A simplified polygon.</returns>
	
	public static boolean IsCollinear(Vector2 a, Vector2 b, Vector2 c, float tolerance)
    {
        return FloatInRange(Area(a, b, c), -tolerance, tolerance);
    }
    
    public static boolean FloatInRange(float value, float min, float max)
    {
        return (value >= min && value <= max);
    }
    
    public static float Area(Vector2 a, Vector2 b, Vector2 c)
    {
        return a.x * (b.y - c.y) + b.x * (c.y - a.y) + c.x * (a.y - b.y);
    }
    
    public static float DistanceBetweenPointAndLineSegment(Vector2 point, Vector2 start, Vector2 end)
    {
        if (start == end)
            return point.dst( start);

        Vector2 v = new Vector2 (end.x-start.x, end.y- start.y);
        Vector2 w = new Vector2 (point.x-start.x, point.y- start.y);

        float c1 = w.x*v.x + w.y*v.y;
        if (c1 <= 0) return point.dst(start);

        float c2 = v.x*v.x + v.y*v.y;
        if (c2 <= c1) return point.dst(end);

        float b = c1 / c2;
        Vector2 pointOnLine = new Vector2(start.x + v.x*b , start.y + v.y*b);
        return point.dst(pointOnLine);
    }
    
    public static void Cross(Vector2 a, Vector2 b, float c)
    {
        c = a.x * b.y - a.y * b.x;
    }
    
        public static Vertices CollinearSimplify(Vertices vertices, float collinearityTolerance)
        {
            if (vertices.getVertices().size() <= 3)
                return vertices;

            Vertices simplified = new Vertices(vertices.getVertices().size()); /////////////////////////////////
            
            for (int i = 0; i < vertices.getVertices().size(); i++)
            {
                Vector2 prev = vertices.PreviousVertex(i);
                Vector2 current = vertices.getVertices().get(i);
                Vector2 next = vertices.NextVertex(i);
                //If they collinear, continue
                if (IsCollinear(prev,  current,  next, collinearityTolerance)) {
                	continue;                
                }                   

                simplified.getVertices().add(current);
            }
            return simplified;
        }

        /// <summary>
        /// Ramer-Douglas-Peucker polygon simplification algorithm. This is the general recursive version that does not use the
        /// speed-up technique by using the Melkman convex hull.
        /// 
        /// If you pass in 0, it will remove all collinear points.
        /// </summary>
        /// <returns>The simplified polygon</returns>
        public static Vertices DouglasPeuckerSimplify(Vertices vertices, float distanceTolerance)
        {
            if (vertices.getVertices().size() <= 3)
                return vertices;

            boolean[] usePoint = new boolean[vertices.getVertices().size()];

            for (int i = 0; i < vertices.getVertices().size(); i++)
                usePoint[i] = true;

            SimplifySection(vertices, 0, vertices.getVertices().size() - 1, usePoint, distanceTolerance);

            Vertices simplified = new Vertices(vertices.getVertices().size());

            for (int i = 0; i < vertices.getVertices().size(); i++)
            {
                if (usePoint[i])
                    simplified.getVertices().add(vertices.getVertices().get(i));
            }

            return simplified;
        }

        private static void SimplifySection(Vertices vertices, int i, int j, boolean[] usePoint, float distanceTolerance)
        {
            if ((i + 1) == j)
                return;

            Vector2 a = vertices.getVertices().get(i);
            Vector2 b = vertices.getVertices().get(j);

            double maxDistance = -1.0;
            int maxIndex = i;
            for (int k = i + 1; k < j; k++)
            {
                Vector2 point = vertices.getVertices().get(k);

                double distance = DistanceBetweenPointAndLineSegment(point, a, b);

                if (distance > maxDistance)
                {
                    maxDistance = distance;
                    maxIndex = k;
                }
            }

            if (maxDistance <= distanceTolerance)
            {
                for (int k = i + 1; k < j; k++)
                {
                    usePoint[k] = false;
                }
            }
            else
            {
                SimplifySection(vertices, i, maxIndex, usePoint, distanceTolerance);
                SimplifySection(vertices, maxIndex, j, usePoint, distanceTolerance);
            }
        }

        /// <summary>
        /// Merges all parallel edges in the list of vertices
        /// </summary>
        /// <param name="vertices">The vertices.</param>
        /// <param name="tolerance">The tolerance.</param>
        public static Vertices MergeParallelEdges(Vertices vertices, float tolerance)
        {
            //From Eric Jordan's convex decomposition library

            if (vertices.getVertices().size() <= 3)
                return vertices; //Can't do anything useful here to a triangle

            boolean[] mergeMe = new boolean[vertices.getVertices().size()];
            int newNVertices = vertices.getVertices().size();

            //Gather points to process
            for (int i = 0; i < vertices.getVertices().size(); ++i)
            {
                int lower = (i == 0) ? (vertices.getVertices().size() - 1) : (i - 1);
                int middle = i;
                int upper = (i == vertices.getVertices().size() - 1) ? (0) : (i + 1);

                float dx0 = vertices.getVertices().get(middle).x - vertices.getVertices().get(lower).x;
                float dy0 = vertices.getVertices().get(middle).y - vertices.getVertices().get(lower).y;
                float dx1 = vertices.getVertices().get(upper).y - vertices.getVertices().get(middle).x;
                float dy1 = vertices.getVertices().get(upper).y - vertices.getVertices().get(middle).y;
                float norm0 = (float)Math.sqrt(dx0 * dx0 + dy0 * dy0);
                float norm1 = (float)Math.sqrt(dx1 * dx1 + dy1 * dy1);

                if (!(norm0 > 0.0f && norm1 > 0.0f) && newNVertices > 3)
                {
                    //Merge identical points
                    mergeMe[i] = true;
                    --newNVertices;
                }

                dx0 /= norm0;
                dy0 /= norm0;
                dx1 /= norm1;
                dy1 /= norm1;
                float cross = dx0 * dy1 - dx1 * dy0;
                float dot = dx0 * dx1 + dy0 * dy1;

                if (Math.abs(cross) < tolerance && dot > 0 && newNVertices > 3)
                {
                    mergeMe[i] = true;
                    --newNVertices;
                }
                else
                    mergeMe[i] = false;
            }

            if (newNVertices == vertices.getVertices().size() || newNVertices == 0)
                return vertices;

            int currIndex = 0;

            //Copy the vertices to a new list and clear the old
            Vertices newVertices = new Vertices(newNVertices);

            for (int i = 0; i < vertices.getVertices().size(); ++i)
            {
                if (mergeMe[i] || newNVertices == 0 || currIndex == newNVertices)
                    continue;

                //   Debug.Assert(currIndex < newNVertices);

                newVertices.getVertices().add(vertices.getVertices().get(i));
                ++currIndex;
            }

            return newVertices;
        }

        /// <summary>
        /// Merges the identical points in the polygon.
        /// </summary>
        /// <param name="vertices">The vertices.</param>
        public static Vertices MergeIdenticalPoints(Vertices vertices)
        {
            HashSet<Vector2> unique = new HashSet<Vector2>();
            for (Iterator<Vector2> iter = vertices.getVertices().iterator(); iter.hasNext();)
            {
            	Vector2 vertex = (Vector2) iter.next();
            	unique.add(vertex);
            }            
            
            return new Vertices(unique);
        }

        /// <summary>
        /// Reduces the polygon by distance.
        /// </summary>
        /// <param name="vertices">The vertices.</param>
        /// <param name="distance">The distance between points. Points closer than this will be removed.</param>
        public static Vertices ReduceByDistance(Vertices vertices, float distance)
        {
            if (vertices.getVertices().size() <= 3)
                return vertices;

            float distance2 = distance * distance;

            Vertices simplified = new Vertices(vertices.getVertices().size());

            for (int i = 0; i < vertices.getVertices().size(); i++)
            {
                Vector2 current = vertices.getVertices().get(i);
                Vector2 next = vertices.NextVertex(i);

                //If they are closer than the distance, continue
                if (next.dst2(current) <= distance2)
                    continue;

                simplified.getVertices().add(current);
            }

            return simplified;
        }

        /// <summary>
        /// Reduces the polygon by removing the Nth vertex in the vertices list.
        /// </summary>
        /// <param name="vertices">The vertices.</param>
        /// <param name="nth">The Nth point to remove. Example: 5.</param>
        /// <returns></returns>
        public static Vertices ReduceByNth(Vertices vertices, int nth)
        {
            if (vertices.getVertices().size() <= 3)
                return vertices;

            if (nth == 0)
                return vertices;

            Vertices simplified = new Vertices(vertices.getVertices().size());

            for (int i = 0; i < vertices.getVertices().size(); i++)
            {
                if (i % nth == 0)
                    continue;

                simplified.getVertices().add(vertices.getVertices().get(i));
            }

            return simplified;
        }

        /// <summary>
        /// Simplify the polygon by removing all points that in pairs of 3 have an area less than the tolerance.
        /// 
        /// Pass in 0 as tolerance, and it will only remove collinear points.
        /// </summary>
        /// <param name="vertices"></param>
        /// <param name="areaTolerance"></param>
        /// <returns></returns>
        public static Vertices ReduceByArea(Vertices vertices, float areaTolerance)
        {
            //From physics2d.net

            if (vertices.Count <= 3)
                return vertices;

            if (areaTolerance < 0)
                System.out.println("areaTolerance must be equal to or greater than zero.");

            Vertices simplified = new Vertices(vertices.Count);
            Vector2 v3;
            Vector2 v1 = vertices.getVertices().get(vertices.getVertices().size() - 2);
            Vector2 v2 = vertices.getVertices().get(vertices.getVertices().size() - 1);
            areaTolerance *= 2;

            for (int i = 0; i < vertices.Count; ++i, v2 = v3)
            {
                v3 = i == vertices.Count - 1 ? simplified.getVertices().get(0) : vertices.getVertices().get(i);

                float old1=0.0f;
                Cross(v1, v2, old1);

                float old2=0.0f;
                Cross(v2, v3, old2);

                float new1=0.0f;
                Cross(v1, v3, new1);

                if (Math.abs(new1 - (old1 + old2)) > areaTolerance)
                {
                    simplified.getVertices().add(v2);
                    v1 = v2;
                }
            }

            return simplified;
        }
    
}
