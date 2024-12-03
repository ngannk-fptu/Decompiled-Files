/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.drawing;

import java.util.Objects;
import java.util.Random;
import org.jgrapht.Graph;
import org.jgrapht.alg.drawing.BaseLayoutAlgorithm2D;
import org.jgrapht.alg.drawing.model.Box2D;
import org.jgrapht.alg.drawing.model.LayoutModel2D;
import org.jgrapht.alg.drawing.model.Point2D;

public class RandomLayoutAlgorithm2D<V, E>
extends BaseLayoutAlgorithm2D<V, E> {
    private Random rng;

    public RandomLayoutAlgorithm2D() {
        this(new Random());
    }

    public RandomLayoutAlgorithm2D(long seed) {
        this(new Random(seed));
    }

    public RandomLayoutAlgorithm2D(Random rng) {
        this.rng = Objects.requireNonNull(rng, "Random number generator cannot be null");
    }

    @Override
    public void layout(Graph<V, E> graph, LayoutModel2D<V> model) {
        super.init(graph, model);
        Box2D drawableArea = model.getDrawableArea();
        double minX = drawableArea.getMinX();
        double minY = drawableArea.getMinX();
        double width = drawableArea.getWidth();
        double height = drawableArea.getHeight();
        for (V v : graph.vertexSet()) {
            double x = this.rng.nextDouble() * width;
            double y = this.rng.nextDouble() * height;
            model.put(v, Point2D.of(minX + x, minY + y));
        }
    }
}

