/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.lca;

import java.util.HashSet;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.alg.interfaces.LowestCommonAncestorAlgorithm;
import org.jgrapht.graph.EdgeReversedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

public class NaiveLCAFinder<V, E>
implements LowestCommonAncestorAlgorithm<V> {
    private final Graph<V, E> graph;

    public NaiveLCAFinder(Graph<V, E> graph) {
        this.graph = GraphTests.requireDirected(graph);
    }

    @Override
    public V getLCA(V a, V b) {
        this.checkNodes(a, b);
        Set<V> lcaSet = this.getLCASet(a, b);
        if (lcaSet.isEmpty()) {
            return null;
        }
        return lcaSet.iterator().next();
    }

    @Override
    public Set<V> getLCASet(V a, V b) {
        Set<V> commonAncestors;
        this.checkNodes(a, b);
        EdgeReversedGraph<V, E> edgeReversed = new EdgeReversedGraph<V, E>(this.graph);
        Set<V> aAncestors = this.getAncestors(edgeReversed, a);
        Set<V> bAncestors = this.getAncestors(edgeReversed, b);
        if (aAncestors.size() < bAncestors.size()) {
            aAncestors.retainAll(bAncestors);
            commonAncestors = aAncestors;
        } else {
            bAncestors.retainAll(aAncestors);
            commonAncestors = bAncestors;
        }
        HashSet<V> leaves = new HashSet<V>();
        for (V ancestor : commonAncestors) {
            boolean isLeaf = true;
            for (E edge : this.graph.outgoingEdgesOf(ancestor)) {
                V target = this.graph.getEdgeTarget(edge);
                if (!commonAncestors.contains(target)) continue;
                isLeaf = false;
                break;
            }
            if (!isLeaf) continue;
            leaves.add(ancestor);
        }
        return leaves;
    }

    private Set<V> getAncestors(Graph<V, E> graph, V start) {
        HashSet ancestors = new HashSet();
        BreadthFirstIterator<V, E> bfs = new BreadthFirstIterator<V, E>(graph, start);
        while (bfs.hasNext()) {
            ancestors.add(bfs.next());
        }
        return ancestors;
    }

    private void checkNodes(V a, V b) {
        if (!this.graph.containsVertex(a)) {
            throw new IllegalArgumentException("invalid vertex: " + a);
        }
        if (!this.graph.containsVertex(b)) {
            throw new IllegalArgumentException("invalid vertex: " + b);
        }
    }
}

