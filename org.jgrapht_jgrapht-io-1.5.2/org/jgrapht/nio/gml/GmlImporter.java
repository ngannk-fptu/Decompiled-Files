/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jgrapht.Graph
 *  org.jgrapht.GraphType
 *  org.jgrapht.alg.util.Pair
 *  org.jgrapht.alg.util.Triple
 */
package org.jgrapht.nio.gml;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import org.jgrapht.Graph;
import org.jgrapht.GraphType;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.alg.util.Triple;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.BaseEventDrivenImporter;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.GraphImporter;
import org.jgrapht.nio.ImportException;
import org.jgrapht.nio.gml.GmlEventDrivenImporter;

public class GmlImporter<V, E>
extends BaseEventDrivenImporter<V, E>
implements GraphImporter<V, E> {
    public static final String DEFAULT_VERTEX_ID_KEY = "ID";
    private Function<Integer, V> vertexFactory;

    @Override
    public void importGraph(Graph<V, E> graph, Reader input) {
        GmlEventDrivenImporter genericImporter = new GmlEventDrivenImporter();
        Consumers consumers = new Consumers(graph);
        genericImporter.addVertexConsumer(consumers.vertexConsumer);
        genericImporter.addVertexAttributeConsumer(consumers.vertexAttributeConsumer);
        genericImporter.addEdgeConsumer(consumers.edgeConsumer);
        genericImporter.addEdgeAttributeConsumer(consumers.edgeAttributeConsumer);
        genericImporter.importInput(input);
    }

    public Function<Integer, V> getVertexFactory() {
        return this.vertexFactory;
    }

    public void setVertexFactory(Function<Integer, V> vertexFactory) {
        this.vertexFactory = vertexFactory;
    }

    private class Consumers {
        private Graph<V, E> graph;
        private GraphType graphType;
        private Map<Integer, V> map;
        private Triple<Integer, Integer, Double> lastTriple;
        private E lastEdge;
        public final Consumer<Integer> vertexConsumer = t -> this.getVertex((Integer)t);
        public final BiConsumer<Pair<Integer, String>, Attribute> vertexAttributeConsumer = (p, a) -> {
            Integer vertex = (Integer)p.getFirst();
            if (!this.map.containsKey(vertex)) {
                throw new ImportException("Node " + vertex + " does not exist");
            }
            GmlImporter.this.notifyVertexAttribute(this.map.get(vertex), (String)p.getSecond(), a);
        };
        public final Consumer<Triple<Integer, Integer, Double>> edgeConsumer = t -> {
            Object from = this.getVertex((Integer)t.getFirst());
            Object to = this.getVertex((Integer)t.getSecond());
            Object e = this.graph.addEdge(from, to);
            if (this.graphType.isWeighted() && t.getThird() != null) {
                this.graph.setEdgeWeight(e, ((Double)t.getThird()).doubleValue());
            }
            GmlImporter.this.notifyEdge(e);
            this.lastTriple = t;
            this.lastEdge = e;
        };
        public final BiConsumer<Pair<Triple<Integer, Integer, Double>, String>, Attribute> edgeAttributeConsumer = (p, a) -> {
            if (p.getFirst() == this.lastTriple) {
                GmlImporter.this.notifyEdgeAttribute(this.lastEdge, (String)p.getSecond(), a);
            }
        };

        public Consumers(Graph<V, E> graph) {
            this.graph = graph;
            this.graphType = graph.getType();
            this.map = new HashMap();
        }

        private V getVertex(Integer id) {
            Object v = this.map.get(id);
            if (v == null) {
                if (GmlImporter.this.vertexFactory != null) {
                    v = GmlImporter.this.vertexFactory.apply(id);
                    this.graph.addVertex(v);
                } else {
                    v = this.graph.addVertex();
                }
                this.map.put(id, v);
                GmlImporter.this.notifyVertex(v);
                GmlImporter.this.notifyVertexAttribute(v, GmlImporter.DEFAULT_VERTEX_ID_KEY, DefaultAttribute.createAttribute(id));
            }
            return v;
        }
    }
}

