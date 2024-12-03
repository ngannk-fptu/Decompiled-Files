/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.spanning;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Objects;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.SpanningTreeAlgorithm;
import org.jgrapht.alg.util.ToleranceDoubleComparator;
import org.jgrapht.alg.util.UnionFind;

public class BoruvkaMinimumSpanningTree<V, E>
implements SpanningTreeAlgorithm<E> {
    private final Graph<V, E> graph;
    private final Comparator<Double> comparator;

    public BoruvkaMinimumSpanningTree(Graph<V, E> graph) {
        this.graph = Objects.requireNonNull(graph, "Graph cannot be null");
        this.comparator = new ToleranceDoubleComparator();
    }

    @Override
    public SpanningTreeAlgorithm.SpanningTree<E> getSpanningTree() {
        LinkedHashSet mstEdges = new LinkedHashSet();
        double mstWeight = 0.0;
        HashMap<E, Integer> edgeOrder = new HashMap<E, Integer>();
        int i = 0;
        for (E e : this.graph.edgeSet()) {
            edgeOrder.put(e, i++);
        }
        UnionFind<V> forest = new UnionFind<V>(this.graph.vertexSet());
        LinkedHashMap<V, E> bestEdge = new LinkedHashMap<V, E>();
        do {
            bestEdge.clear();
            for (E e : this.graph.edgeSet()) {
                V tTree;
                V sTree = forest.find(this.graph.getEdgeSource(e));
                if (sTree.equals(tTree = forest.find(this.graph.getEdgeTarget(e)))) continue;
                double eWeight = this.graph.getEdgeWeight(e);
                Object sTreeEdge = bestEdge.get(sTree);
                if (sTreeEdge == null) {
                    bestEdge.put(sTree, e);
                } else {
                    double sTreeEdgeWeight = this.graph.getEdgeWeight(sTreeEdge);
                    int c = this.comparator.compare(eWeight, sTreeEdgeWeight);
                    if (c < 0 || c == 0 && (Integer)edgeOrder.get(e) < (Integer)edgeOrder.get(sTreeEdge)) {
                        bestEdge.put(sTree, e);
                    }
                }
                Object tTreeEdge = bestEdge.get(tTree);
                if (tTreeEdge == null) {
                    bestEdge.put(tTree, e);
                    continue;
                }
                double tTreeEdgeWeight = this.graph.getEdgeWeight(tTreeEdge);
                int c = this.comparator.compare(eWeight, tTreeEdgeWeight);
                if (c >= 0 && (c != 0 || (Integer)edgeOrder.get(e) >= (Integer)edgeOrder.get(tTreeEdge))) continue;
                bestEdge.put(tTree, e);
            }
            for (Object v : bestEdge.keySet()) {
                V tTree;
                Object e = bestEdge.get(v);
                V sTree = forest.find(this.graph.getEdgeSource(e));
                if (sTree.equals(tTree = forest.find(this.graph.getEdgeTarget(e)))) continue;
                mstEdges.add(e);
                mstWeight += this.graph.getEdgeWeight(e);
                forest.union(sTree, tTree);
            }
        } while (!bestEdge.isEmpty());
        return new SpanningTreeAlgorithm.SpanningTreeImpl(mstEdges, mstWeight);
    }
}

