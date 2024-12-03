/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.traverse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;

public class RandomWalkVertexIterator<V, E>
implements Iterator<V> {
    private final Random rng;
    private final Graph<V, E> graph;
    private final boolean weighted;
    private final Map<V, Double> outEdgesTotalWeight;
    private final long maxHops;
    private long hops;
    private V nextVertex;

    public RandomWalkVertexIterator(Graph<V, E> graph, V vertex) {
        this(graph, vertex, Long.MAX_VALUE, false, new Random());
    }

    public RandomWalkVertexIterator(Graph<V, E> graph, V vertex, long maxHops) {
        this(graph, vertex, maxHops, false, new Random());
    }

    public RandomWalkVertexIterator(Graph<V, E> graph, V vertex, long maxHops, boolean weighted, Random rng) {
        this.graph = Objects.requireNonNull(graph);
        this.weighted = weighted;
        this.outEdgesTotalWeight = new HashMap<V, Double>();
        this.hops = 0L;
        this.nextVertex = Objects.requireNonNull(vertex);
        if (!graph.containsVertex(vertex)) {
            throw new IllegalArgumentException("Random walk must start at a graph vertex");
        }
        this.maxHops = maxHops;
        this.rng = rng;
    }

    @Override
    public boolean hasNext() {
        return this.nextVertex != null;
    }

    @Override
    public V next() {
        if (this.nextVertex == null) {
            throw new NoSuchElementException();
        }
        V value = this.nextVertex;
        this.computeNext();
        return value;
    }

    private void computeNext() {
        if (this.hops >= this.maxHops) {
            this.nextVertex = null;
            return;
        }
        ++this.hops;
        if (this.graph.outDegreeOf(this.nextVertex) == 0) {
            this.nextVertex = null;
            return;
        }
        Object e = null;
        if (this.weighted) {
            double outEdgesWeight = this.outEdgesTotalWeight.computeIfAbsent((Double)this.nextVertex, (Function<Double, Double>)((Function<Object, Double>)v -> this.graph.outgoingEdgesOf(v).stream().collect(Collectors.summingDouble(this.graph::getEdgeWeight))));
            double p = outEdgesWeight * this.rng.nextDouble();
            double cumulativeP = 0.0;
            for (E curEdge : this.graph.outgoingEdgesOf(this.nextVertex)) {
                if (!(p <= (cumulativeP += this.graph.getEdgeWeight(curEdge)))) continue;
                e = curEdge;
                break;
            }
        } else {
            ArrayList<E> outEdges = new ArrayList<E>(this.graph.outgoingEdgesOf(this.nextVertex));
            e = outEdges.get(this.rng.nextInt(outEdges.size()));
        }
        this.nextVertex = Graphs.getOppositeVertex(this.graph, e, this.nextVertex);
    }
}

