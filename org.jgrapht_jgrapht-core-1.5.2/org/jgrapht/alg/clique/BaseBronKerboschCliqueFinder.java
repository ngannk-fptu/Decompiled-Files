/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.clique;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.MaximalCliqueEnumerationAlgorithm;

abstract class BaseBronKerboschCliqueFinder<V, E>
implements MaximalCliqueEnumerationAlgorithm<V, E> {
    protected final Graph<V, E> graph;
    protected final long nanos;
    protected boolean timeLimitReached;
    protected List<Set<V>> allMaximalCliques;
    protected int maxSize;

    public BaseBronKerboschCliqueFinder(Graph<V, E> graph, long timeout, TimeUnit unit) {
        this.graph = Objects.requireNonNull(graph, "Graph cannot be null");
        this.nanos = timeout == 0L ? Long.MAX_VALUE : unit.toNanos(timeout);
        if (this.nanos < 1L) {
            throw new IllegalArgumentException("Invalid timeout, must be positive");
        }
        this.timeLimitReached = false;
    }

    @Override
    public Iterator<Set<V>> iterator() {
        this.lazyRun();
        return this.allMaximalCliques.iterator();
    }

    public Iterator<Set<V>> maximumIterator() {
        this.lazyRun();
        return this.allMaximalCliques.stream().filter(c -> c.size() == this.maxSize).iterator();
    }

    public boolean isTimeLimitReached() {
        return this.timeLimitReached;
    }

    protected abstract void lazyRun();
}

