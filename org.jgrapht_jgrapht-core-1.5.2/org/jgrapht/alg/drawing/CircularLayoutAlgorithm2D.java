/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.drawing;

import java.util.Comparator;
import java.util.Iterator;
import java.util.stream.Stream;
import org.jgrapht.Graph;
import org.jgrapht.alg.drawing.BaseLayoutAlgorithm2D;
import org.jgrapht.alg.drawing.model.Box2D;
import org.jgrapht.alg.drawing.model.LayoutModel2D;
import org.jgrapht.alg.drawing.model.Point2D;
import org.jgrapht.alg.util.ToleranceDoubleComparator;

public class CircularLayoutAlgorithm2D<V, E>
extends BaseLayoutAlgorithm2D<V, E> {
    protected double radius;
    protected Comparator<Double> comparator = new ToleranceDoubleComparator();
    protected Comparator<V> vertexComparator;

    public CircularLayoutAlgorithm2D() {
        this(0.5);
    }

    public CircularLayoutAlgorithm2D(double radius) {
        this(radius, null);
    }

    public CircularLayoutAlgorithm2D(double radius, Comparator<V> vertexComparator) {
        this.radius = radius;
        if (this.comparator.compare(radius, 0.0) <= 0) {
            throw new IllegalArgumentException("Radius must be positive");
        }
        this.vertexComparator = vertexComparator;
    }

    @Override
    public void layout(Graph<V, E> graph, LayoutModel2D<V> model) {
        super.init(graph, model);
        Box2D drawableArea = model.getDrawableArea();
        double width = drawableArea.getWidth();
        if (this.comparator.compare(2.0 * this.radius, width) > 0) {
            throw new IllegalArgumentException("Circle does not fit into drawable area width");
        }
        double height = drawableArea.getHeight();
        if (this.comparator.compare(2.0 * this.radius, height) > 0) {
            throw new IllegalArgumentException("Circle does not fit into drawable area height");
        }
        double minX = drawableArea.getMinX();
        double minY = drawableArea.getMinY();
        int n = graph.vertexSet().size();
        double angleStep = Math.PI * 2 / (double)n;
        Stream vertexStream = this.vertexComparator != null ? graph.vertexSet().stream().sorted(this.vertexComparator) : graph.vertexSet().stream();
        Iterator it = vertexStream.iterator();
        int i = 0;
        while (it.hasNext()) {
            double x = this.radius * Math.cos(angleStep * (double)i) + width / 2.0;
            double y = this.radius * Math.sin(angleStep * (double)i) + height / 2.0;
            Object v = it.next();
            model.put(v, Point2D.of(minX + x, minY + y));
            ++i;
        }
    }
}

