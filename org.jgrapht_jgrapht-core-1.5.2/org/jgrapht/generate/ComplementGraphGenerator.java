/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.generate;

import java.util.ArrayList;
import java.util.Map;
import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.Graphs;
import org.jgrapht.generate.GraphGenerator;

public class ComplementGraphGenerator<V, E>
implements GraphGenerator<V, E, V> {
    private final Graph<V, E> graph;
    private final boolean generateSelfLoops;

    public ComplementGraphGenerator(Graph<V, E> graph) {
        this(graph, false);
    }

    public ComplementGraphGenerator(Graph<V, E> graph, boolean generateSelfLoops) {
        this.graph = GraphTests.requireDirectedOrUndirected(graph);
        this.generateSelfLoops = generateSelfLoops;
    }

    @Override
    public void generateGraph(Graph<V, E> target, Map<String, V> resultMap) {
        Graphs.addAllVertices(target, this.graph.vertexSet());
        if (this.graph.getType().isDirected()) {
            for (V u : this.graph.vertexSet()) {
                for (V v : this.graph.vertexSet()) {
                    if (u == v || this.graph.containsEdge(u, v)) continue;
                    target.addEdge(u, v);
                }
            }
        } else {
            ArrayList<V> vertices = new ArrayList<V>(this.graph.vertexSet());
            for (int i = 0; i < vertices.size() - 1; ++i) {
                for (int j = i + 1; j < vertices.size(); ++j) {
                    Object v;
                    Object u = vertices.get(i);
                    if (this.graph.containsEdge(u, v = vertices.get(j))) continue;
                    target.addEdge(u, v);
                }
            }
        }
        if (this.generateSelfLoops && target.getType().isAllowingSelfLoops()) {
            for (V v : this.graph.vertexSet()) {
                if (this.graph.containsEdge(v, v)) continue;
                target.addEdge(v, v);
            }
        }
    }
}

