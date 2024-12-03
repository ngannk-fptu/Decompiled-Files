/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.drawing;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.jgrapht.Graph;
import org.jgrapht.alg.drawing.FRLayoutAlgorithm2D;
import org.jgrapht.alg.drawing.FRQuadTree;
import org.jgrapht.alg.drawing.model.Box2D;
import org.jgrapht.alg.drawing.model.LayoutModel2D;
import org.jgrapht.alg.drawing.model.Point2D;
import org.jgrapht.alg.drawing.model.Points;

public class IndexedFRLayoutAlgorithm2D<V, E>
extends FRLayoutAlgorithm2D<V, E> {
    public static final double DEFAULT_THETA_FACTOR = 0.5;
    protected double theta;
    protected long savedComparisons;

    public IndexedFRLayoutAlgorithm2D() {
        this(100, 0.5, 0.5);
    }

    public IndexedFRLayoutAlgorithm2D(int iterations, double theta) {
        this(iterations, theta, 0.5);
    }

    public IndexedFRLayoutAlgorithm2D(int iterations, double theta, double normalizationFactor) {
        this(iterations, theta, normalizationFactor, new Random());
    }

    public IndexedFRLayoutAlgorithm2D(int iterations, double theta, double normalizationFactor, Random rng) {
        this(iterations, theta, normalizationFactor, rng, 1.0E-9);
    }

    public IndexedFRLayoutAlgorithm2D(int iterations, double theta, double normalizationFactor, Random rng, double tolerance) {
        super(iterations, normalizationFactor, rng, tolerance);
        this.theta = theta;
        if (theta < 0.0 || theta > 1.0) {
            throw new IllegalArgumentException("Illegal theta value");
        }
        this.savedComparisons = 0L;
    }

    @Override
    public void layout(Graph<V, E> graph, LayoutModel2D<V> model) {
        this.savedComparisons = 0L;
        super.layout(graph, model);
    }

    @Override
    protected Map<V, Point2D> calculateRepulsiveForces(Graph<V, E> graph, LayoutModel2D<V> model) {
        FRQuadTree quadTree = new FRQuadTree(model.getDrawableArea());
        for (V v : graph.vertexSet()) {
            quadTree.insert(model.get(v));
        }
        Point2D origin = Point2D.of(model.getDrawableArea().getMinX(), model.getDrawableArea().getMinY());
        HashMap<V, Point2D> disp = new HashMap<V, Point2D>();
        for (V v : graph.vertexSet()) {
            Point2D vPos = Points.subtract(model.get(v), origin);
            Point2D vDisp = Point2D.of(0.0, 0.0);
            ArrayDeque<FRQuadTree.Node> queue = new ArrayDeque<FRQuadTree.Node>();
            queue.add(quadTree.getRoot());
            while (!queue.isEmpty()) {
                FRQuadTree.Node node = (FRQuadTree.Node)queue.removeFirst();
                Box2D box = node.getBox();
                double boxWidth = box.getWidth();
                Point2D uPos = null;
                if (node.isLeaf()) {
                    if (!node.hasPoints()) continue;
                    uPos = Points.subtract(node.getPoints().iterator().next(), origin);
                } else {
                    double distanceToCentroid = Points.length(Points.subtract(vPos, node.getCentroid()));
                    if (this.comparator.compare(distanceToCentroid, 0.0) == 0) {
                        this.savedComparisons += (long)(node.getNumberOfPoints() - 1);
                        continue;
                    }
                    if (this.comparator.compare(boxWidth / distanceToCentroid, this.theta) < 0) {
                        uPos = Points.subtract(node.getCentroid(), origin);
                        this.savedComparisons += (long)(node.getNumberOfPoints() - 1);
                    } else {
                        for (FRQuadTree.Node child : node.getChildren()) {
                            queue.add(child);
                        }
                        continue;
                    }
                }
                if (this.comparator.compare(vPos.getX(), uPos.getX()) == 0 && this.comparator.compare(vPos.getY(), uPos.getY()) == 0) continue;
                Point2D delta = Points.subtract(vPos, uPos);
                double deltaLen = Points.length(delta);
                Point2D dispContribution = Points.scalarMultiply(delta, this.repulsiveForce(deltaLen) / deltaLen);
                vDisp = Points.add(vDisp, dispContribution);
            }
            disp.put(v, vDisp);
        }
        return disp;
    }

    public long getSavedComparisons() {
        return this.savedComparisons;
    }
}

