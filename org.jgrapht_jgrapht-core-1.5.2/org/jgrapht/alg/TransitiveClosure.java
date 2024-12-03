/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;

public class TransitiveClosure {
    public static final TransitiveClosure INSTANCE = new TransitiveClosure();

    private TransitiveClosure() {
    }

    public <V, E> void closeSimpleDirectedGraph(SimpleDirectedGraph<V, E> graph) {
        Set vertexSet = graph.vertexSet();
        HashSet newEdgeTargets = new HashSet();
        int bound = this.computeBinaryLog(vertexSet.size());
        boolean done = false;
        for (int i = 0; !done && i < bound; ++i) {
            done = true;
            for (Object v1 : vertexSet) {
                newEdgeTargets.clear();
                for (Object v1OutEdge : graph.outgoingEdgesOf(v1)) {
                    Object v2 = graph.getEdgeTarget(v1OutEdge);
                    for (Object v2OutEdge : graph.outgoingEdgesOf(v2)) {
                        Object v3 = graph.getEdgeTarget(v2OutEdge);
                        if (v1.equals(v3) || graph.getEdge(v1, v3) != null) continue;
                        newEdgeTargets.add(v3);
                        done = false;
                    }
                }
                for (Object v3 : newEdgeTargets) {
                    graph.addEdge(v1, v3);
                }
            }
        }
    }

    private int computeBinaryLog(int n) {
        assert (n >= 0);
        int result = 0;
        while (n > 0) {
            n >>= 1;
            ++result;
        }
        return result;
    }

    public <V, E> void closeDirectedAcyclicGraph(DirectedAcyclicGraph<V, E> graph) {
        ArrayDeque orderedVertices = new ArrayDeque(graph.vertexSet().size());
        new TopologicalOrderIterator<V, Object>(graph).forEachRemaining(orderedVertices::addFirst);
        for (Object vertex : orderedVertices) {
            for (V successor : Graphs.successorListOf(graph, vertex)) {
                for (V closureVertex : Graphs.successorListOf(graph, successor)) {
                    graph.addEdge(vertex, closureVertex);
                }
            }
        }
    }
}

