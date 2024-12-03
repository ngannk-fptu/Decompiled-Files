/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.drawing;

import java.util.function.Function;
import org.jgrapht.Graph;
import org.jgrapht.alg.drawing.LayoutAlgorithm2D;
import org.jgrapht.alg.drawing.model.LayoutModel2D;
import org.jgrapht.alg.drawing.model.Point2D;

abstract class BaseLayoutAlgorithm2D<V, E>
implements LayoutAlgorithm2D<V, E> {
    protected Function<V, Point2D> initializer;

    public BaseLayoutAlgorithm2D() {
        this(null);
    }

    public BaseLayoutAlgorithm2D(Function<V, Point2D> initializer) {
        this.initializer = initializer;
    }

    public Function<V, Point2D> getInitializer() {
        return this.initializer;
    }

    public void setInitializer(Function<V, Point2D> initializer) {
        this.initializer = initializer;
    }

    protected void init(Graph<V, E> graph, LayoutModel2D<V> model) {
        Function<V, Point2D> initializer = this.getInitializer();
        if (initializer != null) {
            for (V v : graph.vertexSet()) {
                Point2D value = initializer.apply(v);
                if (value == null) continue;
                model.put(v, value);
            }
        }
    }
}

