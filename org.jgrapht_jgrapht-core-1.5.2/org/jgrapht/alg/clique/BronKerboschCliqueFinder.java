/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.clique;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.alg.clique.BaseBronKerboschCliqueFinder;

public class BronKerboschCliqueFinder<V, E>
extends BaseBronKerboschCliqueFinder<V, E> {
    public BronKerboschCliqueFinder(Graph<V, E> graph) {
        this(graph, 0L, TimeUnit.SECONDS);
    }

    public BronKerboschCliqueFinder(Graph<V, E> graph, long timeout, TimeUnit unit) {
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
            this.findCliques(new ArrayList(), new ArrayList(this.graph.vertexSet()), new ArrayList(), nanosTimeLimit);
        }
    }

    private void findCliques(List<V> potentialClique, List<V> candidates, List<V> alreadyFound, long nanosTimeLimit) {
        for (V v : alreadyFound) {
            if (!candidates.stream().allMatch(c -> this.graph.containsEdge(v, c))) continue;
            return;
        }
        for (V candidate : new ArrayList<V>(candidates)) {
            if (nanosTimeLimit - System.nanoTime() < 0L) {
                this.timeLimitReached = true;
                return;
            }
            ArrayList<V> newCandidates = new ArrayList<V>();
            ArrayList<V> newAlreadyFound = new ArrayList<V>();
            potentialClique.add(candidate);
            candidates.remove(candidate);
            for (V newCandidate : candidates) {
                if (!this.graph.containsEdge(candidate, newCandidate)) continue;
                newCandidates.add(newCandidate);
            }
            for (V newFound : alreadyFound) {
                if (!this.graph.containsEdge(candidate, newFound)) continue;
                newAlreadyFound.add(newFound);
            }
            if (newCandidates.isEmpty() && newAlreadyFound.isEmpty()) {
                HashSet<V> maximalClique = new HashSet<V>(potentialClique);
                this.allMaximalCliques.add(maximalClique);
                this.maxSize = Math.max(this.maxSize, maximalClique.size());
            } else {
                this.findCliques(potentialClique, newCandidates, newAlreadyFound, nanosTimeLimit);
            }
            alreadyFound.add(candidate);
            potentialClique.remove(candidate);
        }
    }
}

