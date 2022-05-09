/* *****************************************************************************
 * Data type to represent a set of points in the unit square (all points have
 * x- and y-coordinates between 0 and 1) using a 2d-tree to support efficient
 * range search (find all of the points contained in a query rectangle)
 * and nearest-neighbor search (find a closest point to a query point).
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.Color;
import java.util.Comparator;

/*****************************************************************************
 * main class implements 2-dimensional binary search tree for storing the
 * points, located on plane.
 * Each node of tree contains the point with given coordinates and by the
 * properties of 2-dimensional tree might be associated with the area on plane,
 * in witch the points of subtrees of given node are located
 * KdTree object instance performs operations:
 * - storing the set of 2-dimensional points (Point2D objects from external library)
 * - picking from set the point, nearest to given point
 * - picking all the points, falling in given rectangle
 * - basic operations - inserting points, checking set for emptiness, size,
 * containing given point and drawing the points among with division lines on plane
 *****************************************************************************/
public class KdTree {
    /**************************************************************************
     * custom comparators to compare points on either horizontal or vertical
     * axis and brake ties on another axis. There are two comparators necessary cuz on
     * each level of 2-dimensional search tree the base of comparing keys is
     * switched between horizontal and vertical coordinates of point
     *************************************************************************/
    private static final Comparator<Point2D> H_ORDER = new HOrder();
    private static final Comparator<Point2D> V_ORDER = new VOrder();

    /**************************************************************************
     * KdTree object contains just two attributes - the root node of tree and
     * the number of points in it
     **************************************************************************/
    private Node rootNode;
    private int size;

    /**************************************************************************
     * nested class implementing the node of 2-dimensional three for storing
     * the Point2D objects
     **************************************************************************/
    private class Node {
        private boolean devHorizontaly;
        private final Point2D point;
        private Node left, right;

        /**********************************************************************
         * constructor puts the point in object and sets the dimension of splits
         * which depends from level of tree
         *********************************************************************/
        private Node(Node parent, Point2D point) {
            this.point = new Point2D(point.x(), point.y());
            if (parent == null) {
                this.devHorizontaly = true;
            }
            else {
                this.devHorizontaly = !parent.devHorizontaly;
            }
        }
    }

    /**************************************************************************
     * comparator to compare Point2D objects by x-coordinates breaking ties
     * by y-coordinates
     *************************************************************************/
    private static class HOrder implements Comparator<Point2D> {
        public int compare(Point2D p1, Point2D p2) {
            if (p1.x() == p2.x()) {
                return Double.compare(p1.y(), p2.y());
            }
            else {
                return Double.compare(p1.x(), p2.x());
            }
        }
    }

    /**************************************************************************
     * comparator to compare Point2D objects by y-coordinates breaking ties
     * by x-coordinates
     *************************************************************************/
    private static class VOrder implements Comparator<Point2D> {
        public int compare(Point2D p1, Point2D p2) {
            if (p1.y() == p2.y()) {
                return Double.compare(p1.x(), p2.x());
            }
            else {
                return Double.compare(p1.y(), p2.y());
            }
        }
    }

    /**************************************************************************
     * constructor of KdTree object
     *************************************************************************/
    public KdTree() {
        size = 0;
    }

    /**************************************************************************
     * checking the tree for emptiness
     * @return - true of the tree is empty, false otherwise
     *************************************************************************/
    public boolean isEmpty() {
        return rootNode == null;
    }

    /**************************************************************************
     * getter for number of points currently in set
     * @return - number of points in tree
     *************************************************************************/
    public int size() {
        return size;
    }

    /**************************************************************************
     * public method for inserting new point in set checks the arg for null and
     * calls private recursive method with same name which does the most of work
     * Point2D object is immutable, so here and after it's save to pass the
     * pointer to it directly to method. Otherwise it would be reasonable
     * to pass newly constructed object - new Point2D(p.x(), p.y())
     * @param p - point to insert in tree
     *************************************************************************/
    public void insert(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("point, provided for insertion is null");
        }
        rootNode = insert(null, rootNode, p);
    }

    /**************************************************************************
     * public method for checking if the set contains argument point checks the
     * arg for null and calls private recursive method with same name which
     * does the most of work
     * @param point - point we're looking for in tree
     * @return - boolean flag showing, if the argument point was found in tree
     *************************************************************************/
    public boolean contains(Point2D point) {
        if (rootNode == null) {
            return false;
        }
        if (point == null) {
            throw new IllegalArgumentException("point, provided for checking is null");
        }
        return contains(point, rootNode);
    }

    /**************************************************************************
     * public method for drawing the point set. Takes the argument and calls
     * the private recursive method with same name
     *************************************************************************/
    public void draw() {
        StdDraw.enableDoubleBuffering();
        StdDraw.setPenRadius(0.01);
        draw(rootNode, new RectHV(0.0, 0.0, 1.0, 1.0));
        StdDraw.show();
    }

    /**************************************************************************
     * public method for checking if the set contains argument point checks the
     * arg for null and calls private recursive method with same name which
     * does the most of work
     * @param rect - rectangle, to look for points in
     * @return - Stack of Point2D objects
     *************************************************************************/
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new IllegalArgumentException("rectangle to search in is null");
        }
        if (size == 0) {
            return null;
        }
        Stack<Point2D> points = new Stack<Point2D>();
        range(rect, rootNode, new RectHV(0.0, 0.0, 1.1, 1.1), points);
        return points;
    }

    /**************************************************************************
     * public method for picking from tree the point, which is nearest to
     * argument point. Checks the arg for null, point set to emptiness and
     * calls private recursive method with same name which does the most of work
     * @param point - point, nearest to witch method is looking for
     * @return - point from tree which is nearest to argument point
     *************************************************************************/
    public Point2D nearest(Point2D point) {
        if (point == null) {
            throw new IllegalArgumentException("point, provided to find closest to it is null");
        }
        if (size == 0) {
            return null;
        }
        Point2D nearest;
        nearest = nearest(point, rootNode.point, rootNode, new RectHV(0.0, 0.0, 1.1, 1.1));
        return nearest;
    }

    /**************************************************************************
     * private recursive method witch inserts new point to 2-dimensional tree
     * @param parent - parent node of this level of recursion node
     * @param node - this level of recursion node
     * @param p - point to be inserted
     * @return - constructed node (all the nodes on the way to newly constructed
     * node are reconstructed just for simplicity of code
     *************************************************************************/
    private Node insert(Node parent, Node node, Point2D p) {
        if (node == null) {
            size++;
            return new Node(parent, p);
        }
        int cmp;
        if (node.devHorizontaly) {
            cmp = H_ORDER.compare(p, node.point);
        }
        else {
            cmp = V_ORDER.compare(p, node.point);
        }
        if (cmp < 0) {
            node.left = insert(node, node.left, p);
        }
        else if (cmp > 0) {
            node.right = insert(node, node.right, p);
        }
        return node;
    }

    /**************************************************************************
     * private recursive method witch checks if the tree contains an argument
     * point
     * @param point - point, which being looked for
     * @param node - node, inspected on current level of recursion
     * @return - boolean flag showing, if the argument point was found in tree
     ************************************************************************/
    private boolean contains(Point2D point, Node node) {
        if (node.devHorizontaly) {
            if (H_ORDER.compare(point, node.point) < 0) {
                if (node.left != null) {
                    return contains(point, node.left);
                }
                else {
                    return false;
                }
            }
            else if (H_ORDER.compare(point, node.point) > 0) {
                if (node.right != null) {
                    return contains(point, node.right);
                }
                else {
                    return false;
                }
            }
            else {
                return true;
            }
        }
        else {
            if (V_ORDER.compare(point, node.point) < 0) {
                if (node.left != null) {
                    return contains(point, node.left);
                }
                else {
                    return false;
                }
            }
            else if (V_ORDER.compare(point, node.point) > 0) {
                if (node.right != null) {
                    return contains(point, node.right);
                }
                else {
                    return false;
                }
            }
            else {
                return true;
            }
        }
    }

    /**************************************************************************
     * private recursive method for drawing the points and corresponding
     * split line on plane
     * @param node - node being drawn on this level of recursion
     * @param rectHV - rectangle, constraining the area all the points of given
     *               node subtrees are located in
     *************************************************************************/
    private void draw(Node node, RectHV rectHV) {
        if (node == null) {
            return;
        }
        node.point.draw();
        drawLine(node, rectHV);
        RectHV leftUpRect, rightDownRect;
        if (node.devHorizontaly) {
            leftUpRect = new RectHV(rectHV.xmin(), rectHV.ymin(), node.point.x(),
                                    rectHV.ymax());
            rightDownRect = new RectHV(node.point.x(), rectHV.ymin(), rectHV.xmax(),
                                       rectHV.ymax());
        }
        else {
            leftUpRect = new RectHV(rectHV.xmin(), rectHV.ymin(), rectHV.xmax(),
                                    node.point.y());
            rightDownRect = new RectHV(rectHV.xmin(), node.point.y(), rectHV.xmax(),
                                       rectHV.ymax());
        }
        draw(node.left, leftUpRect);
        draw(node.right, rightDownRect);
    }

    /**************************************************************************
     * private method for driving the split line, corresponding to given node
     * of 2d tree (point from node and rectangle area are provided from invoking
     * method draw(Node node, RectHV rectHV)
     * @param node - node, being drawn on this level of recursion
     * @param rectHV - rectangle, constraining the area all the points of given
     *               node subtrees are located in
     *************************************************************************/
    private void drawLine(Node node, RectHV rectHV) {
        StdDraw.setPenRadius(0.001);
        if (node.devHorizontaly) {
            StdDraw.setPenColor(Color.red);
            StdDraw.line(node.point.x(), rectHV.ymin(), node.point.x(), rectHV.ymax());
        }
        else {
            StdDraw.setPenColor(Color.blue);
            StdDraw.line(rectHV.xmin(), node.point.y(), rectHV.xmax(), node.point.y());
        }
        StdDraw.setPenColor(Color.black);
        StdDraw.setPenRadius(0.01);
    }

    /**************************************************************************
     * recursive private method to fill the Stack with points, falling in given
     * rectangle.
     * pruning rule: if the query rectangle does not intersect the rectangle
     * corresponding to a node, there is no need to explore that node (or its subtrees).
     * @param rect - rectangle, to look for points in
     * @param node - current node of tree, being explored
     * @param nodeRect - the rectangle of current node
     * @param points - Stack to witch the found point are collected
     *************************************************************************/
    private void range(RectHV rect, Node node, RectHV nodeRect, Stack<Point2D> points) {
        if (rect.contains(node.point)) {
            points.push(node.point);
        }
        RectHV leftUpRect, rightDownRect;
        if (node.left != null) {
            if (node.devHorizontaly) {
                leftUpRect = new RectHV(nodeRect.xmin(), nodeRect.ymin(), node.point.x(),
                                        nodeRect.ymax());
            }
            else {
                leftUpRect = new RectHV(nodeRect.xmin(), nodeRect.ymin(), nodeRect.xmax(),
                                        node.point.y());
            }
            if (rect.intersects(leftUpRect)) {
                range(rect, node.left, leftUpRect, points);
            }
        }
        if (node.right != null) {
            if (node.devHorizontaly) {
                rightDownRect = new RectHV(node.point.x(), nodeRect.ymin(), nodeRect.xmax(),
                                           nodeRect.ymax());
            }
            else {
                rightDownRect = new RectHV(nodeRect.xmin(), node.point.y(), nodeRect.xmax(),
                                           nodeRect.ymax());
            }
            if (rect.intersects(rightDownRect)) {
                range(rect, node.right, rightDownRect, points);
            }
        }
    }

    /**************************************************************************
     * private recursive method for picking from the tree the point, which is
     * nearest to given.
     * Pruning rule: if the closest point discovered so far is closer than the
     * distance between the query point and the rectangle corresponding to a node,
     * there is no need to explore that node (or its subtrees).
     * The recursive method is organised so that when there are two possible
     * subtrees to go down, it always chooses the subtree that is on the same
     * side of the splitting line as the query point as the first subtree
     * to explore. So the closest point found while exploring the first subtree
     * may enable pruning of the second subtree.
     * @param point - argument point, to which we're looking the closest point for
     * @param nearest - the most nearest point has been found so far
     * @param node - currently explored node
     * @param nodeRect - the rectangle area, containing all the points of current
     *                 node subtrees
     * @return - Point2D object
     **************************************************************************/
    private Point2D nearest(Point2D point, Point2D nearest, Node node, RectHV nodeRect) {
        if (point.distanceSquaredTo(node.point) < point.distanceSquaredTo(nearest)) {
            nearest = node.point;
        }
        RectHV leftUpRect = null;
        RectHV rightDownRect = null;
        boolean goLeftFirst = false;
        if ((node.left != null) && (node.right != null)) {
            if (node.devHorizontaly) {
                goLeftFirst = (point.x() < node.point.x());
            }
            else {
                goLeftFirst = (point.y() < node.point.y());
            }
        }
        if (node.left != null) {
            if (node.devHorizontaly) {
                leftUpRect = new RectHV(nodeRect.xmin(), nodeRect.ymin(), node.point.x(),
                                        nodeRect.ymax());
            }
            else {
                leftUpRect = new RectHV(nodeRect.xmin(), nodeRect.ymin(), nodeRect.xmax(),
                                        node.point.y());
            }
        }
        if (node.right != null) {
            if (node.devHorizontaly) {
                rightDownRect = new RectHV(node.point.x(), nodeRect.ymin(), nodeRect.xmax(),
                                           nodeRect.ymax());
            }
            else {
                rightDownRect = new RectHV(nodeRect.xmin(), node.point.y(), nodeRect.xmax(),
                                           nodeRect.ymax());
            }
        }
        if (goLeftFirst) {
            if (node.left != null) {
                if (point.distanceSquaredTo(nearest) > leftUpRect.distanceSquaredTo(point)) {
                    nearest = nearest(point, nearest, node.left, leftUpRect);
                }
            }
            if (node.right != null) {
                if (point.distanceSquaredTo(nearest) > rightDownRect.distanceSquaredTo(point)) {
                    nearest = nearest(point, nearest, node.right, rightDownRect);
                }
            }
        }
        else {
            if (node.right != null) {
                if (point.distanceSquaredTo(nearest) > rightDownRect.distanceSquaredTo(point)) {
                    nearest = nearest(point, nearest, node.right, rightDownRect);
                }
            }
            if (node.left != null) {
                if (point.distanceSquaredTo(nearest) > leftUpRect.distanceSquaredTo(point)) {
                    nearest = nearest(point, nearest, node.left, leftUpRect);
                }
            }
        }
        return nearest;
    }

    /*************************************************************************
     * private method to print all points from the tree. used for debugging
     * invokes recursive method with same name
     *************************************************************************/
    private void print() {
        print(rootNode);
    }

    /*************************************************************************
     * recursive private method to print all points from the tree. used for debugging
     * @param node - current node, being printed out
     ************************************************************************/
    private void print(Node node) {
        if (node == null) {
            return;
        }
        print(node.left);
        System.out.println(node.point + "; " + node.devHorizontaly);
        print(node.right);
    }

    /*************************************************************************
     * private method for reading points from file and inserting them in tree
     * @param fileName - name of the file
     * @return KdTree object with points redden from file
     ************************************************************************/
    private static KdTree readPointsFromFile(String fileName) {
        In pointsFile = new In(fileName);
        KdTree kdTree = new KdTree();
        double[] coordinates = pointsFile.readAllDoubles();
        for (int i = 0; i < coordinates.length / 2; i++) {
            kdTree.insert(new Point2D(coordinates[2 * i], coordinates[2 * i + 1]));
        }
        pointsFile.close();
        return kdTree;
    }

    /*************************************************************************
     * test client
     * @param args - name of file with points coordinates
     *************************************************************************/
    public static void main(String[] args) {
        KdTree kdTree = readPointsFromFile(args[0]);
        System.out.println(kdTree.isEmpty());
        System.out.println(kdTree.size());
        // kdTree.print();
        Point2D point = new Point2D(0.8, 0.18);
        Point2D nearest = kdTree.nearest(point);
        System.out.println(nearest);
        /*
        kdTree.draw();

        StdDraw.setPenRadius(0.03);
        StdDraw.setPenColor(Color.red);
        point.draw();
        StdDraw.setPenColor(Color.green);
        nearest.draw();
        StdDraw.setPenColor(Color.black);
        kdTree.draw();
        */
    }
}
