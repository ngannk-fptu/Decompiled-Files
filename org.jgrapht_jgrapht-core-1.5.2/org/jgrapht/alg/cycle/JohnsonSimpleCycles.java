/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.cycle;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.alg.cycle.DirectedSimpleCycles;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.builder.GraphTypeBuilder;

public class JohnsonSimpleCycles<V, E>
implements DirectedSimpleCycles<V, E> {
    private Graph<V, E> graph;
    private Consumer<List<V>> cycleConsumer = null;
    private V[] iToV = null;
    private Map<V, Integer> vToI = null;
    private Set<V> blocked = null;
    private Map<V, Set<V>> bSets = null;
    private ArrayDeque<V> stack = null;
    private List<Set<V>> foundSCCs = null;
    private int index = 0;
    private Map<V, Integer> vIndex = null;
    private Map<V, Integer> vLowlink = null;
    private ArrayDeque<V> path = null;
    private Set<V> pathSet = null;

    public JohnsonSimpleCycles(Graph<V, E> graph) {
        this.graph = GraphTests.requireDirected(graph, "Graph must be directed");
        if (GraphTests.hasMultipleEdges(graph)) {
            throw new IllegalArgumentException("Graph should not have multiple (parallel) edges");
        }
    }

    @Override
    public void findSimpleCycles(Consumer<List<V>> consumer) {
        Pair<Graph<V, E>, Integer> minSCCGResult;
        if (this.graph == null) {
            throw new IllegalArgumentException("Null graph.");
        }
        this.initState(consumer);
        int size = this.graph.vertexSet().size();
        for (int startIndex = 0; startIndex < size && (minSCCGResult = this.findMinSCSG(startIndex)) != null; ++startIndex) {
            startIndex = minSCCGResult.getSecond();
            Graph<V, E> scg = minSCCGResult.getFirst();
            V startV = this.toV(startIndex);
            for (E e : scg.outgoingEdgesOf(startV)) {
                V v = this.graph.getEdgeTarget(e);
                this.blocked.remove(v);
                this.getBSet(v).clear();
            }
            this.findCyclesInSCG(startIndex, startIndex, scg);
        }
        this.clearState();
    }

    private Pair<Graph<V, E>, Integer> findMinSCSG(int startIndex) {
        this.initMinSCGState();
        List<Set<V>> foundSCCs = this.findSCCS(startIndex);
        int minIndexFound = Integer.MAX_VALUE;
        Set<V> minSCC = null;
        for (Set<V> set : foundSCCs) {
            for (V v2 : set) {
                int t = this.toI(v2);
                if (t >= minIndexFound) continue;
                minIndexFound = t;
                minSCC = set;
            }
        }
        if (minSCC == null) {
            return null;
        }
        Graph resultGraph = GraphTypeBuilder.directed().edgeSupplier(this.graph.getEdgeSupplier()).vertexSupplier(this.graph.getVertexSupplier()).allowingMultipleEdges(false).allowingSelfLoops(true).buildGraph();
        for (Object v : minSCC) {
            resultGraph.addVertex(v);
        }
        for (Object v : minSCC) {
            for (V w : minSCC) {
                E edge = this.graph.getEdge(v, w);
                if (edge == null) continue;
                resultGraph.addEdge(v, w, edge);
            }
        }
        Pair<Graph<V, E>, Integer> pair = Pair.of(resultGraph, minIndexFound);
        this.clearMinSCCState();
        return pair;
    }

    private List<Set<V>> findSCCS(int startIndex) {
        for (V v : this.graph.vertexSet()) {
            int vI = this.toI(v);
            if (vI < startIndex || this.vIndex.containsKey(v)) continue;
            this.getSCCs(startIndex, vI);
        }
        List<Set<V>> result = this.foundSCCs;
        this.foundSCCs = null;
        return result;
    }

    private void getSCCs(int startIndex, int vertexIndex) {
        V vertex = this.toV(vertexIndex);
        this.vIndex.put((Integer)vertex, this.index);
        this.vLowlink.put((Integer)vertex, this.index);
        ++this.index;
        this.path.push(vertex);
        this.pathSet.add(vertex);
        Set<E> edges = this.graph.outgoingEdgesOf(vertex);
        for (E e : edges) {
            V successor = this.graph.getEdgeTarget(e);
            int successorIndex = this.toI(successor);
            if (successorIndex < startIndex) continue;
            if (!this.vIndex.containsKey(successor)) {
                this.getSCCs(startIndex, successorIndex);
                this.vLowlink.put((Integer)vertex, Math.min(this.vLowlink.get(vertex), this.vLowlink.get(successor)));
                continue;
            }
            if (!this.pathSet.contains(successor)) continue;
            this.vLowlink.put((Integer)vertex, Math.min(this.vLowlink.get(vertex), this.vIndex.get(successor)));
        }
        if (this.vLowlink.get(vertex).equals(this.vIndex.get(vertex))) {
            V temp;
            HashSet<V> result = new HashSet<V>();
            do {
                temp = this.path.pop();
                this.pathSet.remove(temp);
                result.add(temp);
            } while (!vertex.equals(temp));
            if (result.size() == 1) {
                Object v = result.iterator().next();
                if (this.graph.containsEdge(vertex, v)) {
                    this.foundSCCs.add(result);
                }
            } else {
                this.foundSCCs.add(result);
            }
        }
    }

    private boolean findCyclesInSCG(int startIndex, int vertexIndex, Graph<V, E> scg) {
        boolean foundCycle = false;
        V vertex = this.toV(vertexIndex);
        this.stack.push(vertex);
        this.blocked.add(vertex);
        for (E e : scg.outgoingEdgesOf(vertex)) {
            V successor = scg.getEdgeTarget(e);
            int successorIndex = this.toI(successor);
            if (successorIndex == startIndex) {
                ArrayList cycle = new ArrayList(this.stack.size());
                this.stack.descendingIterator().forEachRemaining(cycle::add);
                this.cycleConsumer.accept(cycle);
                foundCycle = true;
                continue;
            }
            if (this.blocked.contains(successor)) continue;
            boolean gotCycle = this.findCyclesInSCG(startIndex, successorIndex, scg);
            foundCycle = foundCycle || gotCycle;
        }
        if (foundCycle) {
            this.unblock(vertex);
        } else {
            for (E ew : scg.outgoingEdgesOf(vertex)) {
                V w = scg.getEdgeTarget(ew);
                Set<V> bSet = this.getBSet(w);
                bSet.add(vertex);
            }
        }
        this.stack.pop();
        return foundCycle;
    }

    private void unblock(V vertex) {
        this.blocked.remove(vertex);
        Set<V> bSet = this.getBSet(vertex);
        while (bSet.size() > 0) {
            V w = bSet.iterator().next();
            bSet.remove(w);
            if (!this.blocked.contains(w)) continue;
            this.unblock(w);
        }
    }

    private void initState(Consumer<List<V>> consumer) {
        this.cycleConsumer = consumer;
        this.iToV = this.graph.vertexSet().toArray();
        this.vToI = new HashMap<V, Integer>();
        this.blocked = new HashSet<V>();
        this.bSets = new HashMap<V, Set<V>>();
        this.stack = new ArrayDeque();
        for (int i = 0; i < this.iToV.length; ++i) {
            this.vToI.put((Integer)this.iToV[i], i);
        }
    }

    private void clearState() {
        this.cycleConsumer = null;
        this.iToV = null;
        this.vToI = null;
        this.blocked = null;
        this.bSets = null;
        this.stack = null;
    }

    private void initMinSCGState() {
        this.index = 0;
        this.foundSCCs = new ArrayList<Set<V>>();
        this.vIndex = new HashMap<V, Integer>();
        this.vLowlink = new HashMap<V, Integer>();
        this.path = new ArrayDeque();
        this.pathSet = new HashSet<V>();
    }

    private void clearMinSCCState() {
        this.index = 0;
        this.foundSCCs = null;
        this.vIndex = null;
        this.vLowlink = null;
        this.path = null;
        this.pathSet = null;
    }

    private Integer toI(V vertex) {
        return this.vToI.get(vertex);
    }

    private V toV(Integer i) {
        return this.iToV[i];
    }

    private Set<V> getBSet(V v) {
        return this.bSets.computeIfAbsent((Set)v, (Function<Set, Set<Set>>)((Function<Object, Set>)k -> new HashSet()));
    }
}

