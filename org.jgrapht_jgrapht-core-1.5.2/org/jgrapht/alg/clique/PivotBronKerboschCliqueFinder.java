/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.clique;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.Graphs;
import org.jgrapht.alg.clique.BaseBronKerboschCliqueFinder;

public class PivotBronKerboschCliqueFinder<V, E>
extends BaseBronKerboschCliqueFinder<V, E> {
    public PivotBronKerboschCliqueFinder(Graph<V, E> graph) {
        this(graph, 0L, TimeUnit.SECONDS);
    }

    public PivotBronKerboschCliqueFinder(Graph<V, E> graph, long timeout, TimeUnit unit) {
        super(graph, timeout, unit);
    }

    @Override
    protected void lazyRun() {
        if (this.allMaximalCliques == null) {
            long nanosTimeLimit;
            if (!GraphTests.isSimple(this.graph)) {
                throw new IllegalArgumentException("Graph must be simple");
            }
            this.allMaximalCliques = new ArrayList();
            try {
                nanosTimeLimit = Math.addExact(System.nanoTime(), this.nanos);
            }
            catch (ArithmeticException ignore) {
                nanosTimeLimit = Long.MAX_VALUE;
            }
            this.findCliques(new HashSet(this.graph.vertexSet()), new HashSet(), new HashSet(), nanosTimeLimit);
        }
    }

    private V choosePivot(Set<V> p, Set<V> x) {
        int max = -1;
        V pivot = null;
        Iterator it = Stream.concat(p.stream(), x.stream()).iterator();
        while (it.hasNext()) {
            Object u = it.next();
            int count = 0;
            for (Object e : this.graph.edgesOf(u)) {
                if (!p.contains(Graphs.getOppositeVertex(this.graph, e, u))) continue;
                ++count;
            }
            if (count <= max) continue;
            max = count;
            pivot = (V)u;
        }
        return pivot;
    }

    protected void findCliques(Set<V> p, Set<V> r, Set<V> x, long nanosTimeLimit) {
        if (p.isEmpty() && x.isEmpty()) {
            HashSet<V> maximalClique = new HashSet<V>(r);
            this.allMaximalCliques.add(maximalClique);
            this.maxSize = Math.max(this.maxSize, maximalClique.size());
            return;
        }
        if (nanosTimeLimit - System.nanoTime() < 0L) {
            this.timeLimitReached = true;
            return;
        }
        V u = this.choosePivot(p, x);
        HashSet<V> uNeighbors = new HashSet<V>();
        for (Object e : this.graph.edgesOf(u)) {
            uNeighbors.add(Graphs.getOppositeVertex(this.graph, e, u));
        }
        HashSet<V> candidates = new HashSet<V>();
        for (Object v : p) {
            if (uNeighbors.contains(v)) continue;
            candidates.add(v);
        }
        for (Object v : candidates) {
            HashSet<V> vNeighbors = new HashSet<V>();
            for (Object e : this.graph.edgesOf(v)) {
                vNeighbors.add(Graphs.getOppositeVertex(this.graph, e, v));
            }
            Set newP = p.stream().filter(vNeighbors::contains).collect(Collectors.toSet());
            Set newX = x.stream().filter(vNeighbors::contains).collect(Collectors.toSet());
            HashSet<V> newR = new HashSet<V>(r);
            newR.add(v);
            this.findCliques(newP, newR, newX, nanosTimeLimit);
            p.remove(v);
            x.add(v);
        }
    }
}

