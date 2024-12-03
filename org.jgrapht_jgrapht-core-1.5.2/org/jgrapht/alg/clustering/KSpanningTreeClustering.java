/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.clustering;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.alg.interfaces.ClusteringAlgorithm;
import org.jgrapht.alg.interfaces.SpanningTreeAlgorithm;
import org.jgrapht.alg.spanning.PrimMinimumSpanningTree;
import org.jgrapht.alg.util.UnionFind;

public class KSpanningTreeClustering<V, E>
implements ClusteringAlgorithm<V> {
    private Graph<V, E> graph;
    private int k;

    public KSpanningTreeClustering(Graph<V, E> graph, int k) {
        this.graph = GraphTests.requireUndirected(graph);
        if (k < 1 || k > graph.vertexSet().size()) {
            throw new IllegalArgumentException("Illegal number of clusters");
        }
        this.k = k;
    }

    @Override
    public ClusteringAlgorithm.Clustering<V> getClustering() {
        SpanningTreeAlgorithm.SpanningTree<E> mst = new PrimMinimumSpanningTree<V, E>(this.graph).getSpanningTree();
        UnionFind<V> forest = new UnionFind<V>(this.graph.vertexSet());
        ArrayList<Object> allEdges = new ArrayList<Object>(mst.getEdges());
        allEdges.sort(Comparator.comparingDouble(this.graph::getEdgeWeight));
        for (E e : allEdges) {
            if (forest.numberOfSets() == this.k) break;
            V source = this.graph.getEdgeSource(e);
            V target = this.graph.getEdgeTarget(e);
            if (forest.find(source).equals(forest.find(target))) continue;
            forest.union(source, target);
        }
        LinkedHashMap<V, LinkedHashSet<V>> clusterMap = new LinkedHashMap<V, LinkedHashSet<V>>();
        for (V v : this.graph.vertexSet()) {
            V rv = forest.find(v);
            LinkedHashSet<V> cluster = (LinkedHashSet<V>)clusterMap.get(rv);
            if (cluster == null) {
                cluster = new LinkedHashSet<V>();
                clusterMap.put(rv, cluster);
            }
            cluster.add(v);
        }
        return new ClusteringAlgorithm.ClusteringImpl(new ArrayList(clusterMap.values()));
    }
}

