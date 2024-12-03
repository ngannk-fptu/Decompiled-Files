/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jgrapht.Graph
 *  org.jgrapht.GraphType
 *  org.jgrapht.alg.util.Pair
 *  org.jgrapht.alg.util.Triple
 */
package org.jgrapht.nio.json;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
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
import org.jgrapht.nio.json.JSONEventDrivenImporter;

public class JSONImporter<V, E>
extends BaseEventDrivenImporter<V, E>
implements GraphImporter<V, E> {
    public static final String DEFAULT_VERTEX_ID_KEY = "ID";
    public static final String DEFAULT_VERTICES_COLLECTION_NAME = "nodes";
    public static final String DEFAULT_EDGES_COLLECTION_NAME = "edges";
    private Function<String, V> vertexFactory;
    private BiFunction<String, Map<String, Attribute>, V> vertexWithAttributesFactory;
    private Function<Map<String, Attribute>, E> edgeWithAttributesFactory;
    private String verticesCollectionName = "nodes";
    private String edgesCollectionName = "edges";

    @Override
    public void importGraph(Graph<V, E> graph, Reader input) {
        boolean verticesOutOfOrder = this.vertexWithAttributesFactory == null;
        boolean edgesOutOfOrder = this.edgeWithAttributesFactory == null;
        JSONEventDrivenImporter genericImporter = new JSONEventDrivenImporter(verticesOutOfOrder, edgesOutOfOrder);
        genericImporter.setVerticesCollectionName(this.verticesCollectionName);
        genericImporter.setEdgesCollectionName(this.edgesCollectionName);
        Consumers consumers = new Consumers(graph);
        if (this.vertexWithAttributesFactory != null) {
            genericImporter.addVertexWithAttributesConsumer(consumers.vertexWithAttributesConsumer);
        } else {
            genericImporter.addVertexConsumer(consumers.vertexConsumer);
        }
        genericImporter.addVertexAttributeConsumer(consumers.vertexAttributeConsumer);
        if (this.edgeWithAttributesFactory != null) {
            genericImporter.addEdgeWithAttributesConsumer(consumers.edgeWithAttributesConsumer);
        } else {
            genericImporter.addEdgeConsumer(consumers.edgeConsumer);
        }
        genericImporter.addEdgeAttributeConsumer(consumers.edgeAttributeConsumer);
        genericImporter.importInput(input);
    }

    public Function<String, V> getVertexFactory() {
        return this.vertexFactory;
    }

    public void setVertexFactory(Function<String, V> vertexFactory) {
        this.vertexFactory = vertexFactory;
    }

    public void setVertexWithAttributesFactory(BiFunction<String, Map<String, Attribute>, V> vertexWithAttributesFactory) {
        this.vertexWithAttributesFactory = vertexWithAttributesFactory;
    }

    public Function<Map<String, Attribute>, E> getEdgeWithAttributesFactory() {
        return this.edgeWithAttributesFactory;
    }

    public void setEdgeWithAttributesFactory(Function<Map<String, Attribute>, E> edgeWithAttributesFactory) {
        this.edgeWithAttributesFactory = edgeWithAttributesFactory;
    }

    public String getVerticesCollectionName() {
        return this.verticesCollectionName;
    }

    public void setVerticesCollectionName(String verticesCollectionName) {
        this.verticesCollectionName = Objects.requireNonNull(verticesCollectionName);
    }

    public String getEdgesCollectionName() {
        return this.edgesCollectionName;
    }

    public void setEdgesCollectionName(String edgesCollectionName) {
        this.edgesCollectionName = Objects.requireNonNull(edgesCollectionName);
    }

    private class Consumers {
        private Graph<V, E> graph;
        private GraphType graphType;
        private Map<String, V> map;
        private Triple<String, String, Double> lastTriple;
        private E lastEdge;
        public final Consumer<String> vertexConsumer = t -> {
            Object v;
            if (this.map.containsKey(t)) {
                throw new ImportException("Node " + t + " already exists");
            }
            if (JSONImporter.this.vertexFactory != null) {
                v = JSONImporter.this.vertexFactory.apply((String)t);
                this.graph.addVertex(v);
            } else {
                v = this.graph.addVertex();
            }
            this.map.put((String)t, v);
            JSONImporter.this.notifyVertex(v);
            JSONImporter.this.notifyVertexAttribute(v, JSONImporter.DEFAULT_VERTEX_ID_KEY, DefaultAttribute.createAttribute(t));
        };
        public final BiConsumer<String, Map<String, Attribute>> vertexWithAttributesConsumer = (t, attrs) -> {
            Object v;
            if (this.map.containsKey(t)) {
                throw new ImportException("Node " + t + " already exists");
            }
            if (JSONImporter.this.vertexWithAttributesFactory != null) {
                v = JSONImporter.this.vertexWithAttributesFactory.apply((String)t, (Map<String, Attribute>)attrs);
                this.graph.addVertex(v);
            } else {
                v = this.graph.addVertex();
            }
            this.map.put((String)t, v);
            attrs.put(JSONImporter.DEFAULT_VERTEX_ID_KEY, DefaultAttribute.createAttribute(t));
            JSONImporter.this.notifyVertexWithAttributes(v, attrs);
        };
        public final BiConsumer<Pair<String, String>, Attribute> vertexAttributeConsumer = (p, a) -> {
            String vertex = (String)p.getFirst();
            if (!this.map.containsKey(vertex)) {
                throw new ImportException("Node " + vertex + " does not exist");
            }
            JSONImporter.this.notifyVertexAttribute(this.map.get(vertex), (String)p.getSecond(), a);
        };
        public final Consumer<Triple<String, String, Double>> edgeConsumer = t -> {
            String source = (String)t.getFirst();
            Object from = this.map.get(source);
            if (from == null) {
                throw new ImportException("Node " + source + " does not exist");
            }
            String target = (String)t.getSecond();
            Object to = this.map.get(target);
            if (to == null) {
                throw new ImportException("Node " + target + " does not exist");
            }
            Object e = this.graph.addEdge(from, to);
            if (this.graphType.isWeighted() && t.getThird() != null) {
                this.graph.setEdgeWeight(e, ((Double)t.getThird()).doubleValue());
            }
            JSONImporter.this.notifyEdge(e);
            this.lastTriple = t;
            this.lastEdge = e;
        };
        public final BiConsumer<Triple<String, String, Double>, Map<String, Attribute>> edgeWithAttributesConsumer = (t, attrs) -> {
            Object e;
            String source = (String)t.getFirst();
            Object from = this.map.get(source);
            if (from == null) {
                throw new ImportException("Node " + source + " does not exist");
            }
            String target = (String)t.getSecond();
            Object to = this.map.get(target);
            if (to == null) {
                throw new ImportException("Node " + target + " does not exist");
            }
            if (JSONImporter.this.edgeWithAttributesFactory != null) {
                e = JSONImporter.this.edgeWithAttributesFactory.apply((Map<String, Attribute>)attrs);
                this.graph.addEdge(from, to, e);
            } else {
                e = this.graph.addEdge(from, to);
            }
            JSONImporter.this.notifyEdgeWithAttributes(e, attrs);
            this.lastTriple = t;
            this.lastEdge = e;
        };
        public final BiConsumer<Pair<Triple<String, String, Double>, String>, Attribute> edgeAttributeConsumer = (p, a) -> {
            Triple t = (Triple)p.getFirst();
            if (t == this.lastTriple) {
                JSONImporter.this.notifyEdgeAttribute(this.lastEdge, (String)p.getSecond(), a);
            }
        };

        public Consumers(Graph<V, E> graph) {
            this.graph = graph;
            this.graphType = graph.getType();
            this.map = new HashMap();
        }
    }
}

