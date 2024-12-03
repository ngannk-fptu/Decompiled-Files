/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.cycle;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.alg.cycle.DirectedSimpleCycles;

public class TarjanSimpleCycles<V, E>
implements DirectedSimpleCycles<V, E> {
    private Graph<V, E> graph;
    private Consumer<List<V>> cycleConsumer = null;
    private Set<V> marked;
    private ArrayDeque<V> markedStack;
    private ArrayDeque<V> pointStack;
    private Map<V, Integer> vToI;
    private Map<V, Set<V>> removed;

    public TarjanSimpleCycles() {
    }

    public TarjanSimpleCycles(Graph<V, E> graph) {
        this.graph = GraphTests.requireDirected(graph, "Graph must be directed");
    }

    public Graph<V, E> getGraph() {
        return this.graph;
    }

    public void setGraph(Graph<V, E> graph) {
        this.graph = GraphTests.requireDirected(graph, "Graph must be directed");
    }

    @Override
    public void findSimpleCycles(Consumer<List<V>> consumer) {
        if (this.graph == null) {
            throw new IllegalArgumentException("Null graph.");
        }
        this.initState(consumer);
        for (V start : this.graph.vertexSet()) {
            this.backtrack(start, start);
            while (!this.markedStack.isEmpty()) {
                this.marked.remove(this.markedStack.pop());
            }
        }
        this.clearState();
    }

    private boolean backtrack(V start, V vertex) {
        boolean foundCycle = false;
        this.pointStack.push(vertex);
        this.marked.add(vertex);
        this.markedStack.push(vertex);
        for (E currentEdge : this.graph.outgoingEdgesOf(vertex)) {
            V currentVertex = this.graph.getEdgeTarget(currentEdge);
            if (this.getRemoved(vertex).contains(currentVertex)) continue;
            int comparison = this.toI(currentVertex).compareTo(this.toI(start));
            if (comparison < 0) {
                this.getRemoved(vertex).add(currentVertex);
                continue;
            }
            if (comparison == 0) {
                V v;
                foundCycle = true;
                ArrayList<V> cycle = new ArrayList<V>();
                Iterator<V> it = this.pointStack.descendingIterator();
                while (it.hasNext() && !start.equals(v = it.next())) {
                }
                cycle.add(start);
                while (it.hasNext()) {
                    cycle.add(it.next());
                }
                this.cycleConsumer.accept(cycle);
                continue;
            }
            if (this.marked.contains(currentVertex)) continue;
            boolean gotCycle = this.backtrack(start, currentVertex);
            foundCycle = foundCycle || gotCycle;
        }
        if (foundCycle) {
            while (!this.markedStack.peek().equals(vertex)) {
                this.marked.remove(this.markedStack.pop());
            }
            this.marked.remove(this.markedStack.pop());
        }
        this.pointStack.pop();
        return foundCycle;
    }

    private void initState(Consumer<List<V>> consumer) {
        this.cycleConsumer = consumer;
        this.marked = new HashSet<V>();
        this.markedStack = new ArrayDeque();
        this.pointStack = new ArrayDeque();
        this.vToI = new HashMap<V, Integer>();
        this.removed = new HashMap<V, Set<V>>();
        int index = 0;
        for (V v : this.graph.vertexSet()) {
            this.vToI.put((Integer)v, index++);
        }
    }

    private void clearState() {
        this.cycleConsumer = null;
        this.marked = null;
        this.markedStack = null;
        this.pointStack = null;
        this.vToI = null;
    }

    private Integer toI(V v) {
        return this.vToI.get(v);
    }

    private Set<V> getRemoved(V v) {
        return this.removed.computeIfAbsent((Set)v, (Function<Set, Set<Set>>)((Function<Object, Set>)k -> new HashSet()));
    }
}

