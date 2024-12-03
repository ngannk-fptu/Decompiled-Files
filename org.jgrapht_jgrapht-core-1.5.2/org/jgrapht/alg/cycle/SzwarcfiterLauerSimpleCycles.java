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
import org.jgrapht.alg.connectivity.KosarajuStrongConnectivityInspector;
import org.jgrapht.alg.cycle.DirectedSimpleCycles;

public class SzwarcfiterLauerSimpleCycles<V, E>
implements DirectedSimpleCycles<V, E> {
    private Graph<V, E> graph;
    private Consumer<List<V>> cycleConsumer = null;
    private V[] iToV = null;
    private Map<V, Integer> vToI = null;
    private Map<V, Set<V>> bSets = null;
    private ArrayDeque<V> stack = null;
    private Set<V> marked = null;
    private Map<V, Set<V>> removed = null;
    private int[] position = null;
    private boolean[] reach = null;
    private List<V> startVertices = null;

    public SzwarcfiterLauerSimpleCycles() {
    }

    public SzwarcfiterLauerSimpleCycles(Graph<V, E> graph) {
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
        KosarajuStrongConnectivityInspector<V, E> inspector = new KosarajuStrongConnectivityInspector<V, E>(this.graph);
        List<Set<V>> sccs = inspector.stronglyConnectedSets();
        for (Set<V> scc : sccs) {
            int maxInDegree = -1;
            Object startVertex = null;
            for (V v : scc) {
                int inDegree = this.graph.inDegreeOf(v);
                if (inDegree <= maxInDegree) continue;
                maxInDegree = inDegree;
                startVertex = v;
            }
            this.startVertices.add(startVertex);
        }
        for (Set<V> vertex : this.startVertices) {
            this.cycle(this.toI(vertex), 0);
        }
        this.clearState();
    }

    private boolean cycle(int v, int q) {
        int t;
        boolean foundCycle = false;
        V vV = this.toV(v);
        this.marked.add(vV);
        this.stack.push(vV);
        this.position[v] = t = this.stack.size();
        if (!this.reach[v]) {
            q = t;
        }
        Set<V> avRemoved = this.getRemoved(vV);
        Set<E> edgeSet = this.graph.outgoingEdgesOf(vV);
        for (E e : edgeSet) {
            V wV = this.graph.getEdgeTarget(e);
            if (avRemoved.contains(wV)) continue;
            int w = this.toI(wV);
            if (!this.marked.contains(wV)) {
                boolean gotCycle = this.cycle(w, q);
                if (gotCycle) {
                    foundCycle = true;
                    continue;
                }
                this.noCycle(v, w);
                continue;
            }
            if (this.position[w] <= q) {
                V current;
                foundCycle = true;
                ArrayList<V> cycle = new ArrayList<V>();
                Iterator<V> it = this.stack.descendingIterator();
                while (it.hasNext() && !wV.equals(current = it.next())) {
                }
                cycle.add(wV);
                while (it.hasNext()) {
                    current = it.next();
                    cycle.add(current);
                    if (!current.equals(vV)) continue;
                }
                this.cycleConsumer.accept(cycle);
                continue;
            }
            this.noCycle(v, w);
        }
        this.stack.pop();
        if (foundCycle) {
            this.unmark(v);
        }
        this.reach[v] = true;
        this.position[v] = this.graph.vertexSet().size();
        return foundCycle;
    }

    private void noCycle(int x, int y) {
        V xV = this.toV(x);
        V yV = this.toV(y);
        Set<V> by = this.getBSet(yV);
        Set<V> axRemoved = this.getRemoved(xV);
        by.add(xV);
        axRemoved.add(yV);
    }

    private void unmark(int x) {
        V xV = this.toV(x);
        this.marked.remove(xV);
        Set<V> bx = this.getBSet(xV);
        for (V yV : bx) {
            Set<V> ayRemoved = this.getRemoved(yV);
            ayRemoved.remove(xV);
            if (!this.marked.contains(yV)) continue;
            this.unmark(this.toI(yV));
        }
        bx.clear();
    }

    private void initState(Consumer<List<V>> consumer) {
        this.cycleConsumer = consumer;
        this.iToV = this.graph.vertexSet().toArray();
        this.vToI = new HashMap<V, Integer>();
        this.bSets = new HashMap<V, Set<V>>();
        this.stack = new ArrayDeque();
        this.marked = new HashSet<V>();
        this.removed = new HashMap<V, Set<V>>();
        int size = this.graph.vertexSet().size();
        this.position = new int[size];
        this.reach = new boolean[size];
        this.startVertices = new ArrayList<V>();
        for (int i = 0; i < this.iToV.length; ++i) {
            this.vToI.put((Integer)this.iToV[i], i);
        }
    }

    private void clearState() {
        this.cycleConsumer = null;
        this.iToV = null;
        this.vToI = null;
        this.bSets = null;
        this.stack = null;
        this.marked = null;
        this.removed = null;
        this.position = null;
        this.reach = null;
        this.startVertices = null;
    }

    private Integer toI(V v) {
        return this.vToI.get(v);
    }

    private V toV(int i) {
        return this.iToV[i];
    }

    private Set<V> getBSet(V v) {
        return this.bSets.computeIfAbsent((Set)v, (Function<Set, Set<Set>>)((Function<Object, Set>)k -> new HashSet()));
    }

    private Set<V> getRemoved(V v) {
        return this.removed.computeIfAbsent((Set)v, (Function<Set, Set<Set>>)((Function<Object, Set>)k -> new HashSet()));
    }
}

