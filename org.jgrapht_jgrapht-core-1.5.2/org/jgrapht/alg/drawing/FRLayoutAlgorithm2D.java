/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.drawing;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.function.BiFunction;
import org.jgrapht.Graph;
import org.jgrapht.alg.drawing.BaseLayoutAlgorithm2D;
import org.jgrapht.alg.drawing.RandomLayoutAlgorithm2D;
import org.jgrapht.alg.drawing.model.Box2D;
import org.jgrapht.alg.drawing.model.LayoutModel2D;
import org.jgrapht.alg.drawing.model.MapLayoutModel2D;
import org.jgrapht.alg.drawing.model.Point2D;
import org.jgrapht.alg.drawing.model.Points;
import org.jgrapht.alg.util.ToleranceDoubleComparator;

public class FRLayoutAlgorithm2D<V, E>
extends BaseLayoutAlgorithm2D<V, E> {
    public static final int DEFAULT_ITERATIONS = 100;
    public static final double DEFAULT_NORMALIZATION_FACTOR = 0.5;
    protected Random rng;
    protected double optimalDistance;
    protected double normalizationFactor;
    protected int iterations;
    protected BiFunction<LayoutModel2D<V>, Integer, TemperatureModel> temperatureModelSupplier;
    protected final ToleranceDoubleComparator comparator;

    public FRLayoutAlgorithm2D() {
        this(100, 0.5, new Random());
    }

    public FRLayoutAlgorithm2D(int iterations) {
        this(iterations, 0.5, new Random());
    }

    public FRLayoutAlgorithm2D(int iterations, double normalizationFactor) {
        this(iterations, normalizationFactor, new Random());
    }

    public FRLayoutAlgorithm2D(int iterations, double normalizationFactor, Random rng) {
        this(iterations, normalizationFactor, rng, 1.0E-9);
    }

    public FRLayoutAlgorithm2D(int iterations, double normalizationFactor, Random rng, double tolerance) {
        this.rng = Objects.requireNonNull(rng);
        this.iterations = iterations;
        this.normalizationFactor = normalizationFactor;
        this.temperatureModelSupplier = (model, totalIterations) -> {
            double dimension = Math.min(model.getDrawableArea().getWidth(), model.getDrawableArea().getHeight());
            return new InverseLinearTemperatureModel(-1.0 * dimension / (10.0 * (double)totalIterations.intValue()), dimension / 10.0);
        };
        this.comparator = new ToleranceDoubleComparator(tolerance);
    }

    public FRLayoutAlgorithm2D(int iterations, double normalizationFactor, BiFunction<LayoutModel2D<V>, Integer, TemperatureModel> temperatureModelSupplier, Random rng) {
        this(iterations, normalizationFactor, temperatureModelSupplier, rng, 1.0E-9);
    }

    public FRLayoutAlgorithm2D(int iterations, double normalizationFactor, BiFunction<LayoutModel2D<V>, Integer, TemperatureModel> temperatureModelSupplier, Random rng, double tolerance) {
        this.rng = Objects.requireNonNull(rng);
        this.iterations = iterations;
        this.normalizationFactor = normalizationFactor;
        this.temperatureModelSupplier = Objects.requireNonNull(temperatureModelSupplier);
        this.comparator = new ToleranceDoubleComparator(tolerance);
    }

    @Override
    public void layout(Graph<V, E> graph, LayoutModel2D<V> model) {
        Box2D drawableArea = model.getDrawableArea();
        double minX = drawableArea.getMinX();
        double minY = drawableArea.getMinY();
        if (this.getInitializer() != null) {
            this.init(graph, model);
            for (V v : graph.vertexSet()) {
                Point2D vPos = model.get(v);
                if (vPos != null) continue;
                model.put(v, Point2D.of(minX, minY));
            }
        } else {
            MapLayoutModel2D<V> randomModel = new MapLayoutModel2D<V>(drawableArea);
            new RandomLayoutAlgorithm2D<V, E>(this.rng).layout(graph, randomModel);
            for (V v : graph.vertexSet()) {
                model.put(v, randomModel.get(v));
            }
        }
        double width = drawableArea.getWidth();
        double height = drawableArea.getHeight();
        double area = width * height;
        int n = graph.vertexSet().size();
        if (n == 0) {
            return;
        }
        this.optimalDistance = this.normalizationFactor * Math.sqrt(area / (double)n);
        TemperatureModel temperatureModel = this.temperatureModelSupplier.apply(model, this.iterations);
        for (int i = 0; i < this.iterations; ++i) {
            Map<V, Point2D> repulsiveDisp = this.calculateRepulsiveForces(graph, model);
            Map<Point2D, Point2D> attractiveDisp = this.calculateAttractiveForces(graph, model);
            double temp = temperatureModel.temperature(i, this.iterations);
            for (V v : graph.vertexSet()) {
                Point2D vDisp = Points.add(repulsiveDisp.get(v), attractiveDisp.getOrDefault(v, Point2D.of(0.0, 0.0)));
                if (this.comparator.compare(vDisp.getX(), 0.0) == 0 && this.comparator.compare(vDisp.getY(), 0.0) == 0) continue;
                double vDispLen = Points.length(vDisp);
                Point2D vPos = Points.add(model.get(v), Points.scalarMultiply(vDisp, Math.min(vDispLen, temp) / vDispLen));
                vPos = Point2D.of(Math.min(minX + width, Math.max(minX, vPos.getX())), Math.min(minY + height, Math.max(minY, vPos.getY())));
                model.put(v, vPos);
            }
        }
    }

    protected double attractiveForce(double distance) {
        return distance * distance / this.optimalDistance;
    }

    protected double repulsiveForce(double distance) {
        return this.optimalDistance * this.optimalDistance / distance;
    }

    protected Map<V, Point2D> calculateRepulsiveForces(Graph<V, E> graph, LayoutModel2D<V> model) {
        Point2D origin = Point2D.of(model.getDrawableArea().getMinX(), model.getDrawableArea().getMinY());
        HashMap<V, Point2D> disp = new HashMap<V, Point2D>();
        for (V v : graph.vertexSet()) {
            Point2D vPos = Points.subtract(model.get(v), origin);
            Point2D vDisp = Point2D.of(0.0, 0.0);
            for (V u : graph.vertexSet()) {
                if (v.equals(u)) continue;
                Point2D uPos = Points.subtract(model.get(u), origin);
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

    protected Map<V, Point2D> calculateAttractiveForces(Graph<V, E> graph, LayoutModel2D<V> model) {
        Point2D origin = Point2D.of(model.getDrawableArea().getMinX(), model.getDrawableArea().getMinY());
        HashMap<V, Point2D> disp = new HashMap<V, Point2D>();
        for (E e : graph.edgeSet()) {
            V v = graph.getEdgeSource(e);
            V u = graph.getEdgeTarget(e);
            Point2D vPos = Points.subtract(model.get(v), origin);
            Point2D uPos = Points.subtract(model.get(u), origin);
            if (this.comparator.compare(vPos.getX(), uPos.getX()) == 0 && this.comparator.compare(vPos.getY(), uPos.getY()) == 0) continue;
            Point2D delta = Points.subtract(vPos, uPos);
            double deltaLen = Points.length(delta);
            Point2D dispContribution = Points.scalarMultiply(delta, this.attractiveForce(deltaLen) / deltaLen);
            disp.put(v, Points.add(disp.getOrDefault(v, Point2D.of(0.0, 0.0)), Points.negate(dispContribution)));
            disp.put(u, Points.add(disp.getOrDefault(u, Point2D.of(0.0, 0.0)), dispContribution));
        }
        return disp;
    }

    public static interface TemperatureModel {
        public double temperature(int var1, int var2);
    }

    protected class InverseLinearTemperatureModel
    implements TemperatureModel {
        private double a;
        private double b;

        public InverseLinearTemperatureModel(double a, double b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public double temperature(int iteration, int maxIterations) {
            if (iteration >= maxIterations - 1) {
                return 0.0;
            }
            return this.a * (double)iteration + this.b;
        }
    }
}

