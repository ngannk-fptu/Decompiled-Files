/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.drawing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.jgrapht.alg.drawing.model.Box2D;
import org.jgrapht.alg.drawing.model.Boxes;
import org.jgrapht.alg.drawing.model.Point2D;
import org.jgrapht.alg.util.Pair;

class FRQuadTree {
    private static final int NW = 0;
    private static final int NE = 1;
    private static final int SW = 2;
    private static final int SE = 3;
    private Node root;

    public FRQuadTree(Box2D box) {
        this.root = new Node(box);
    }

    public void insert(Point2D p) {
        Node cur = this.root;
        while (true) {
            if (cur.isLeaf()) {
                if (cur.points.size() == 0) {
                    cur.points.add(p);
                    return;
                }
                Box2D rect = cur.getBox();
                Pair<Box2D, Box2D> xsplit = Boxes.splitAlongXAxis(rect);
                Pair<Box2D, Box2D> west = Boxes.splitAlongYAxis(xsplit.getFirst());
                Pair<Box2D, Box2D> east = Boxes.splitAlongYAxis(xsplit.getSecond());
                cur.children = new Node[4];
                cur.children[0] = new Node(west.getSecond());
                cur.children[1] = new Node(east.getSecond());
                cur.children[2] = new Node(west.getFirst());
                cur.children[3] = new Node(east.getFirst());
                double centroidX = 0.0;
                double centroidY = 0.0;
                for (Point2D point : cur.points) {
                    if (Boxes.containsPoint(cur.children[0].getBox(), point)) {
                        cur.children[0].points.add(point);
                    } else if (Boxes.containsPoint(cur.children[1].getBox(), point)) {
                        cur.children[1].points.add(point);
                    } else if (Boxes.containsPoint(cur.children[2].getBox(), point)) {
                        cur.children[2].points.add(point);
                    } else if (Boxes.containsPoint(cur.children[3].getBox(), point)) {
                        cur.children[3].points.add(point);
                    }
                    centroidX += point.getX();
                    centroidY += point.getY();
                }
                cur.totalPoints = cur.points.size();
                cur.centroid = Point2D.of(centroidX / (double)cur.totalPoints, centroidY / (double)cur.totalPoints);
                cur.points = null;
            }
            ++cur.totalPoints;
            cur.centroid = Point2D.of((cur.centroid.getX() * (double)(cur.totalPoints - 1) + p.getX()) / (double)cur.totalPoints, (cur.centroid.getY() * (double)(cur.totalPoints - 1) + p.getY()) / (double)cur.totalPoints);
            if (Boxes.containsPoint(cur.children[0].getBox(), p)) {
                cur = cur.children[0];
                continue;
            }
            if (Boxes.containsPoint(cur.children[1].getBox(), p)) {
                cur = cur.children[1];
                continue;
            }
            if (Boxes.containsPoint(cur.children[2].getBox(), p)) {
                cur = cur.children[2];
                continue;
            }
            if (!Boxes.containsPoint(cur.children[3].getBox(), p)) break;
            cur = cur.children[3];
        }
        throw new IllegalArgumentException();
    }

    public Node getRoot() {
        return this.root;
    }

    public class Node {
        Box2D box;
        int totalPoints;
        Point2D centroid;
        Node[] children;
        List<Point2D> points;

        public Node(Box2D box) {
            this.box = Objects.requireNonNull(box);
            this.points = new ArrayList<Point2D>();
        }

        public boolean isLeaf() {
            return this.points != null;
        }

        public List<Point2D> getPoints() {
            if (this.points != null) {
                return this.points;
            }
            ArrayList<Point2D> result = new ArrayList<Point2D>();
            this.getChildren().forEach(node -> result.addAll(node.getPoints()));
            return result;
        }

        public boolean hasPoints() {
            if (this.points != null) {
                return this.points.size() != 0;
            }
            return this.totalPoints != 0;
        }

        public Box2D getBox() {
            return this.box;
        }

        public int getNumberOfPoints() {
            if (this.points != null) {
                return this.points.size();
            }
            return this.totalPoints;
        }

        public Point2D getCentroid() {
            if (this.points != null) {
                int numPoints = this.points.size();
                if (numPoints == 0) {
                    throw new IllegalArgumentException("No points");
                }
                double x = 0.0;
                double y = 0.0;
                for (Point2D p : this.points) {
                    x += p.getX();
                    y += p.getY();
                }
                return Point2D.of(x / (double)numPoints, y / (double)numPoints);
            }
            return this.centroid;
        }

        public List<Node> getChildren() {
            if (this.children == null) {
                return Collections.emptyList();
            }
            return Arrays.asList(this.children);
        }
    }
}

