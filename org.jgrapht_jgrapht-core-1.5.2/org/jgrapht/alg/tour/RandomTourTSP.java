/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.tour;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Random;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.tour.HamiltonianCycleAlgorithmBase;

public class RandomTourTSP<V, E>
extends HamiltonianCycleAlgorithmBase<V, E> {
    private final Random rng;

    public RandomTourTSP() {
        this(new Random());
    }

    public RandomTourTSP(Random rng) {
        this.rng = Objects.requireNonNull(rng, "Random number generator cannot be null");
    }

    @Override
    public GraphPath<V, E> getTour(Graph<V, E> graph) {
        this.checkGraph(graph);
        ArrayList<V> vertices = new ArrayList<V>(graph.vertexSet());
        if (vertices.size() == 1) {
            return this.getSingletonTour(graph);
        }
        Collections.shuffle(vertices, this.rng);
        return this.vertexListToTour(vertices, graph);
    }
}

