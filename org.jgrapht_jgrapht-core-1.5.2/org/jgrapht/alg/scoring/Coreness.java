/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.scoring;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.VertexScoringAlgorithm;

public final class Coreness<V, E>
implements VertexScoringAlgorithm<V, Integer> {
    private final Graph<V, E> g;
    private Map<V, Integer> scores;
    private int degeneracy;

    public Coreness(Graph<V, E> g) {
        this.g = GraphTests.requireUndirected(g);
    }

    @Override
    public Map<V, Integer> getScores() {
        this.lazyRun();
        return Collections.unmodifiableMap(this.scores);
    }

    @Override
    public Integer getVertexScore(V v) {
        if (!this.g.containsVertex(v)) {
            throw new IllegalArgumentException("Cannot return score of unknown vertex");
        }
        this.lazyRun();
        return this.scores.get(v);
    }

    public int getDegeneracy() {
        this.lazyRun();
        return this.degeneracy;
    }

    private void lazyRun() {
        if (this.scores != null) {
            return;
        }
        if (!GraphTests.isSimple(this.g)) {
            throw new IllegalArgumentException("Graph must be simple");
        }
        this.scores = new HashMap<V, Integer>();
        this.degeneracy = 0;
        int n = this.g.vertexSet().size();
        int maxDegree = n - 1;
        Set[] buckets = (Set[])Array.newInstance(Set.class, maxDegree + 1);
        for (int i = 0; i < buckets.length; ++i) {
            buckets[i] = new HashSet();
        }
        int minDegree = n;
        HashMap<V, Integer> degrees = new HashMap<V, Integer>();
        for (Object v : this.g.vertexSet()) {
            int d = this.g.degreeOf(v);
            buckets[d].add(v);
            degrees.put(v, d);
            minDegree = Math.min(minDegree, d);
        }
        while (minDegree < n) {
            Object v;
            Set b = buckets[minDegree];
            if (b.isEmpty()) {
                ++minDegree;
                continue;
            }
            v = b.iterator().next();
            b.remove(v);
            this.scores.put((Integer)v, minDegree);
            this.degeneracy = Math.max(this.degeneracy, minDegree);
            for (E e : this.g.edgesOf(v)) {
                V u = Graphs.getOppositeVertex(this.g, e, v);
                int uDegree = (Integer)degrees.get(u);
                if (uDegree <= minDegree || this.scores.containsKey(u)) continue;
                buckets[uDegree].remove(u);
                degrees.put(u, --uDegree);
                buckets[uDegree].add(u);
                minDegree = Math.min(minDegree, uDegree);
            }
        }
    }
}

