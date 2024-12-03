/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Random;
import org.jgrapht.Graph;
import org.jgrapht.alg.color.GreedyColoring;

public class RandomGreedyColoring<V, E>
extends GreedyColoring<V, E> {
    private Random rng;

    public RandomGreedyColoring(Graph<V, E> graph) {
        this(graph, new Random());
    }

    public RandomGreedyColoring(Graph<V, E> graph, Random rng) {
        super(graph);
        this.rng = Objects.requireNonNull(rng, "Random number generator cannot be null");
    }

    @Override
    protected Iterable<V> getVertexOrdering() {
        ArrayList order = new ArrayList(this.graph.vertexSet());
        Collections.shuffle(order, this.rng);
        return order;
    }
}

