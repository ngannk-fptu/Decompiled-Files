/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.cycle;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.CycleBasisAlgorithm;
import org.jgrapht.alg.util.Pair;

public abstract class AbstractFundamentalCycleBasis<V, E>
implements CycleBasisAlgorithm<V, E> {
    protected Graph<V, E> graph;

    public AbstractFundamentalCycleBasis(Graph<V, E> graph) {
        this.graph = GraphTests.requireDirectedOrUndirected(graph);
    }

    @Override
    public CycleBasisAlgorithm.CycleBasis<V, E> getCycleBasis() {
        Map<V, E> spanningForest = this.computeSpanningForest();
        Set treeEdges = spanningForest.entrySet().stream().map(Map.Entry::getValue).filter(Objects::nonNull).collect(Collectors.toSet());
        LinkedHashSet<List<List<E>>> cycles = new LinkedHashSet<List<List<E>>>();
        int length = 0;
        double weight = 0.0;
        for (E e : this.graph.edgeSet()) {
            if (treeEdges.contains(e)) continue;
            Pair<List<E>, Double> c = this.buildFundamentalCycle(e, spanningForest);
            cycles.add(c.getFirst());
            length += c.getFirst().size();
            weight += c.getSecond().doubleValue();
        }
        return new CycleBasisAlgorithm.CycleBasisImpl<V, E>(this.graph, cycles, length, weight);
    }

    protected abstract Map<V, E> computeSpanningForest();

    private Pair<List<E>, Double> buildFundamentalCycle(E e, Map<V, E> spanningForest) {
        E edgeToParent;
        V target;
        V source = this.graph.getEdgeSource(e);
        if (source.equals(target = this.graph.getEdgeTarget(e))) {
            return Pair.of(Collections.singletonList(e), this.graph.getEdgeWeight(e));
        }
        LinkedHashSet<E> path1 = new LinkedHashSet<E>();
        path1.add(e);
        V cur = source;
        while (!cur.equals(target) && (edgeToParent = spanningForest.get(cur)) != null) {
            V parent = Graphs.getOppositeVertex(this.graph, edgeToParent, cur);
            path1.add(edgeToParent);
            cur = parent;
        }
        double path2Weight = 0.0;
        LinkedList path2 = new LinkedList();
        if (!cur.equals(target)) {
            E edgeToParent2;
            cur = target;
            while ((edgeToParent2 = spanningForest.get(cur)) != null) {
                V parent = Graphs.getOppositeVertex(this.graph, edgeToParent2, cur);
                if (path1.contains(edgeToParent2)) {
                    path1.remove(edgeToParent2);
                } else {
                    path2.add(edgeToParent2);
                    path2Weight += this.graph.getEdgeWeight(edgeToParent2);
                }
                cur = parent;
            }
        }
        for (Object a : path1) {
            path2Weight += this.graph.getEdgeWeight(a);
            path2.addFirst(a);
        }
        return Pair.of(path2, path2Weight);
    }
}

