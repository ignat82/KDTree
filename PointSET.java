/* *****************************************************************************
 * Brute-force implementation by Set data type
 * performs operations:
 * - storing the set of 2-dimensional points (Point2D objects from external library)
 * - picking from set the point, nearest to given point
 * - picking all the points, falling in given rectangle
 * - basic operations - inserting points, checking set for emptiness, size,
 * containing given point and drawing the points among with division lines on plane
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdDraw;

/*****************************************************************************
 * main class with only field - Set of Point2D objects
 *****************************************************************************/
public class PointSET {
    private final SET<Point2D> points;

    /*************************************************************************
     * constructor. Initialises new Set of Point2D objects
     ************************************************************************/
    public PointSET() {
        points = new SET<Point2D>();
    }

    /*************************************************************************
     * public method for checking the emptiness of PointSET
     * @return - true if it one or more points in object and false otherwise
     ************************************************************************/
    public boolean isEmpty() {
        return points.isEmpty();
    }

    /*************************************************************************
     * getter for number of points in set
     * @return - number of points in set
     ************************************************************************/
    public int size() {
        return points.size();
    }

    /*************************************************************************
     * public method for adding a point to set
     * @param p - point to insert
     ************************************************************************/
    public void insert(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("provided point is null");
        }
        points.add(p);
    }

    /*************************************************************************
     * public method to check if the point is in set
     * @param p - point to look for
     * @return - true if the point is in set and false otherwise
     ************************************************************************/
    public boolean contains(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("provided point is null");
        }
        return points.contains(p);
    }

    /*************************************************************************
     * public method to draw all the points from set on plane
     ************************************************************************/
    public void draw() {
        StdDraw.enableDoubleBuffering();
        StdDraw.setPenRadius(0.01);
        for (Point2D point : points) {
            point.draw();
        }
        StdDraw.show();
    }

    /*************************************************************************
     * method to get from set all the points, falling in argument rectangle
     * @param rect - rectangle to look the points in
     * @return - Stack of point from set, falling in argument rectangle
     ************************************************************************/
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new IllegalArgumentException("provided rectangle is null");
        }
        Stack<Point2D> insidePoints = new Stack<Point2D>();
        for (Point2D point : points) {
            if (rect.contains(point)) {
                insidePoints.push(point);
            }
        }
        return insidePoints;
    }

    /**************************************************************************
     * public method for picking from tree the point, which is nearest to
     * argument point. Checks the arg for null, point set to emptiness and
     * calls private recursive method with same name which does the most of work
     * @param p - point, nearest to witch method is looking for
     * @return - point from tree which is nearest to argument point
     *************************************************************************/
    public Point2D nearest(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("provided point is null");
        }
        if (points.isEmpty()) {
            return null;
        }
        Point2D nearest = points.min();
        double squareDist = p.distanceSquaredTo(nearest);
        for (Point2D point : points) {
            if (p.distanceSquaredTo(point) < squareDist) {
                nearest = point;
                squareDist = p.distanceSquaredTo(point);
            }
        }
        return nearest;
    }

    /*************************************************************************
     * static method to improve readability of test client
     * @param fileName name of txt file with board
     * @return Board object instance
     *************************************************************************/
    private static PointSET readPointsFromFile(String fileName) {
        In pointsFile = new In(fileName);
        PointSET pointSET = new PointSET();
        double[] coordinates = pointsFile.readAllDoubles();
        for (int i = 0; i < coordinates.length / 2; i++) {
            pointSET.insert(new Point2D(coordinates[2 * i], coordinates[2 * i + 1]));
        }
        pointsFile.close();
        return pointSET;
    }

    /*************************************************************************
     * test client
     * @param args - filename of txt file with points coordinates
     *************************************************************************/
    public static void main(String[] args) {
        PointSET pointSET = readPointsFromFile(args[0]);
        pointSET.draw();
        System.out.println("there are " + pointSET.size() + " points in set");
    }
}
