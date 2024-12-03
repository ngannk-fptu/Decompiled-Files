/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.clique;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.Graphs;
import org.jgrapht.alg.clique.PivotBronKerboschCliqueFinder;
import org.jgrapht.traverse.DegeneracyOrderingIterator;

public class DegeneracyBronKerboschCliqueFinder<V, E>
extends PivotBronKerboschCliqueFinder<V, E> {
    public DegeneracyBronKerboschCliqueFinder(Graph<V, E> graph) {
        this(graph, 0L, TimeUnit.SECONDS);
    }

    public DegeneracyBronKerboschCliqueFinder(Graph<V, E> graph, long timeout, TimeUnit unit) {
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
            ArrayList ordering = new ArrayList();
            new DegeneracyOrderingIterator(this.graph).forEachRemaining(ordering::add);
            int n = ordering.size();
            for (int i = 0; i < n; ++i) {
                Object vi = ordering.get(i);
                HashSet viNeighbors = new HashSet();
                for (Object e : this.graph.edgesOf(vi)) {
                    viNeighbors.add(Graphs.getOppositeVertex(this.graph, e, vi));
                }
                HashSet p = new HashSet();
                for (int j = i + 1; j < n; ++j) {
                    Object vj = ordering.get(j);
                    if (!viNeighbors.contains(vj)) continue;
                    p.add(vj);
                }
                HashSet r = new HashSet();
                r.add(vi);
                HashSet x = new HashSet();
                for (int j = 0; j < i; ++j) {
                    Object vj = ordering.get(j);
                    if (!viNeighbors.contains(vj)) continue;
                    x.add(vj);
                }
                this.findCliques(p, r, x, nanosTimeLimit);
            }
        }
    }
}

