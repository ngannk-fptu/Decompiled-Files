/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jgrapht.Graph
 *  org.jgrapht.alg.util.Pair
 */
package org.jgrapht.nio.graph6;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import org.jgrapht.Graph;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.nio.BaseEventDrivenImporter;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.GraphImporter;
import org.jgrapht.nio.ImportException;
import org.jgrapht.nio.graph6.Graph6Sparse6EventDrivenImporter;

public class Graph6Sparse6Importer<V, E>
extends BaseEventDrivenImporter<V, E>
implements GraphImporter<V, E> {
    public static final String DEFAULT_VERTEX_ID_KEY = "ID";
    private Function<Integer, V> vertexFactory;

    public Function<Integer, V> getVertexFactory() {
        return this.vertexFactory;
    }

    public void setVertexFactory(Function<Integer, V> vertexFactory) {
        this.vertexFactory = vertexFactory;
    }

    @Override
    public void importGraph(Graph<V, E> graph, Reader input) {
        Graph6Sparse6EventDrivenImporter genericImporter = new Graph6Sparse6EventDrivenImporter();
        Consumers consumers = new Consumers(graph);
        genericImporter.addVertexConsumer(consumers.vertexConsumer);
        genericImporter.addEdgeConsumer(consumers.edgeConsumer);
        genericImporter.importInput(input);
    }

    private class Consumers {
        private Graph<V, E> graph;
        private Map<Integer, V> map;
        public final Consumer<Integer> vertexConsumer = t -> {
            Object v;
            if (this.map.containsKey(t)) {
                throw new ImportException("Node " + t + " reported twice");
            }
            if (Graph6Sparse6Importer.this.vertexFactory != null) {
                v = Graph6Sparse6Importer.this.vertexFactory.apply((Integer)t);
                this.graph.addVertex(v);
            } else {
                v = this.graph.addVertex();
            }
            this.map.put((Integer)t, v);
            Graph6Sparse6Importer.this.notifyVertex(v);
            Graph6Sparse6Importer.this.notifyVertexAttribute(v, Graph6Sparse6Importer.DEFAULT_VERTEX_ID_KEY, DefaultAttribute.createAttribute(t));
        };
        public final Consumer<Pair<Integer, Integer>> edgeConsumer = p -> {
            int source = (Integer)p.getFirst();
            Object from = this.map.get(p.getFirst());
            if (from == null) {
                throw new ImportException("Node " + source + " does not exist");
            }
            int target = (Integer)p.getSecond();
            Object to = this.map.get(target);
            if (to == null) {
                throw new ImportException("Node " + target + " does not exist");
            }
            Object e = this.graph.addEdge(from, to);
            Graph6Sparse6Importer.this.notifyEdge(e);
        };

        public Consumers(Graph<V, E> graph) {
            this.graph = graph;
            this.map = new HashMap();
        }
    }
}

