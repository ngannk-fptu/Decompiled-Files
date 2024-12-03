/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.drawing;

import java.util.OptionalDouble;
import java.util.stream.StreamSupport;
import org.jgrapht.Graph;
import org.jgrapht.alg.drawing.BaseLayoutAlgorithm2D;
import org.jgrapht.alg.drawing.model.Box2D;
import org.jgrapht.alg.drawing.model.LayoutModel2D;
import org.jgrapht.alg.drawing.model.Point2D;

public class RescaleLayoutAlgorithm2D<V, E>
extends BaseLayoutAlgorithm2D<V, E> {
    private double scale;

    public RescaleLayoutAlgorithm2D(double scale) {
        if (scale <= 0.0) {
            throw new IllegalArgumentException("Scale must be positive");
        }
        this.scale = scale;
    }

    @Override
    public void layout(Graph<V, E> graph, LayoutModel2D<V> model) {
        double allMax;
        Point2D newP;
        double newY;
        OptionalDouble optMeanY;
        Box2D oldArea = model.getDrawableArea();
        double oldCenterX = oldArea.getMinX() + oldArea.getWidth() / 2.0;
        double oldCenterY = oldArea.getMinY() + oldArea.getHeight() / 2.0;
        double maxX = 0.0;
        double maxY = 0.0;
        OptionalDouble optMeanX = StreamSupport.stream(model.spliterator(), false).mapToDouble(e -> ((Point2D)e.getValue()).getX()).average();
        if (optMeanX.isPresent()) {
            double meanX = optMeanX.getAsDouble();
            for (V v : graph.vertexSet()) {
                Point2D p = model.get(v);
                double newX = p.getX() - meanX;
                Point2D newP2 = Point2D.of(newX, p.getY());
                model.put(v, newP2);
                maxX = Math.max(Math.abs(newX), maxX);
            }
        }
        if ((optMeanY = StreamSupport.stream(model.spliterator(), false).mapToDouble(e -> ((Point2D)e.getValue()).getY()).average()).isPresent()) {
            double meanY = optMeanY.getAsDouble();
            for (V v : graph.vertexSet()) {
                Point2D p = model.get(v);
                newY = p.getY() - meanY;
                newP = Point2D.of(p.getX(), newY);
                model.put(v, newP);
                maxY = Math.max(Math.abs(newY), maxY);
            }
        }
        if ((allMax = Math.max(maxX, maxY)) > 0.0) {
            for (V v : graph.vertexSet()) {
                Point2D p = model.get(v);
                double newX = oldCenterX + p.getX() * this.scale / allMax;
                newP = Point2D.of(newX, p.getY());
                model.put(v, newP);
            }
        }
        if (allMax > 0.0) {
            for (V v : graph.vertexSet()) {
                Point2D p = model.get(v);
                newY = oldCenterY + p.getY() * this.scale / allMax;
                newP = Point2D.of(p.getX(), newY);
                model.put(v, newP);
            }
        }
        model.setDrawableArea(Box2D.of(oldCenterX - this.scale, oldCenterY - this.scale, 2.0 * this.scale, 2.0 * this.scale));
    }
}

