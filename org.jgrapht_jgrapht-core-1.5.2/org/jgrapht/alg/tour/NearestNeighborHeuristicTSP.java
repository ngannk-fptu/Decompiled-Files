/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.tour;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.tour.HamiltonianCycleAlgorithmBase;
import org.jgrapht.util.ArrayUtil;

public class NearestNeighborHeuristicTSP<V, E>
extends HamiltonianCycleAlgorithmBase<V, E> {
    private Random rng;
    private Iterator<V> initiaVertex;

    public NearestNeighborHeuristicTSP() {
        this(null, new Random());
    }

    public NearestNeighborHeuristicTSP(V first) {
        this(Collections.singletonList(Objects.requireNonNull(first, "Specified initial vertex cannot be null")), new Random());
    }

    public NearestNeighborHeuristicTSP(Iterable<V> initialVertices) {
        this(Objects.requireNonNull(initialVertices, "Specified initial vertices cannot be null"), new Random());
    }

    public NearestNeighborHeuristicTSP(long seed) {
        this(null, new Random(seed));
    }

    public NearestNeighborHeuristicTSP(Random rng) {
        this(null, Objects.requireNonNull(rng, "Random number generator cannot be null"));
    }

    private NearestNeighborHeuristicTSP(Iterable<V> initialVertices, Random rng) {
        if (initialVertices != null) {
            Iterator<V> iterator = initialVertices.iterator();
            this.initiaVertex = iterator.hasNext() ? iterator : null;
        }
        this.rng = rng;
    }

    @Override
    public GraphPath<V, E> getTour(Graph<V, E> graph) {
        this.checkGraph(graph);
        if (graph.vertexSet().size() == 1) {
            return this.getSingletonTour(graph);
        }
        Set<V> vertexSet = graph.vertexSet();
        int n = vertexSet.size();
        Object[] path = vertexSet.toArray(new Object[n + 1]);
        List<Object> pathList = Arrays.asList(path);
        int initalIndex = this.getFirstVertexIndex(pathList);
        ArrayUtil.swap(path, 0, initalIndex);
        int limit = n - 1;
        for (int i = 1; i < limit; ++i) {
            Object v = path[i - 1];
            int nearestNeighbor = NearestNeighborHeuristicTSP.getNearestNeighbor(v, path, i, graph);
            ArrayUtil.swap(path, i, nearestNeighbor);
        }
        path[n] = path[0];
        return this.closedVertexListToTour(pathList, graph);
    }

    private int getFirstVertexIndex(List<V> path) {
        if (this.initiaVertex != null) {
            int initialIndex;
            V first = this.initiaVertex.next();
            if (!this.initiaVertex.hasNext()) {
                this.initiaVertex = null;
            }
            if ((initialIndex = path.indexOf(first)) < 0) {
                throw new IllegalArgumentException("Specified initial vertex is not in graph");
            }
            return initialIndex;
        }
        return this.rng.nextInt(path.size() - 1);
    }

    private static <V, E> int getNearestNeighbor(V current, V[] vertices, int start, Graph<V, E> g) {
        int closest = -1;
        double minDist = Double.MAX_VALUE;
        int n = vertices.length - 1;
        for (int i = start; i < n; ++i) {
            V v = vertices[i];
            double vDist = g.getEdgeWeight(g.getEdge(current, v));
            if (!(vDist < minDist)) continue;
            closest = i;
            minDist = vDist;
        }
        return closest;
    }
}

