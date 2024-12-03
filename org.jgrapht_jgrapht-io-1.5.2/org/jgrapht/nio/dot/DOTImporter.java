/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jgrapht.Graph
 *  org.jgrapht.alg.util.Pair
 */
package org.jgrapht.nio.dot;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import org.jgrapht.Graph;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.BaseEventDrivenImporter;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.GraphImporter;
import org.jgrapht.nio.ImportException;
import org.jgrapht.nio.dot.DOTEventDrivenImporter;

public class DOTImporter<V, E>
extends BaseEventDrivenImporter<V, E>
implements GraphImporter<V, E> {
    public static final String DEFAULT_VERTEX_ID_KEY = "ID";
    private Function<String, V> vertexFactory;
    private BiFunction<String, Map<String, Attribute>, V> vertexWithAttributesFactory;
    private Function<Map<String, Attribute>, E> edgeWithAttributesFactory;

    @Override
    public void importGraph(Graph<V, E> graph, Reader input) {
        boolean verticesOutOfOrder = this.vertexWithAttributesFactory == null;
        boolean edgesOutOfOrder = this.edgeWithAttributesFactory == null;
        DOTEventDrivenImporter genericImporter = new DOTEventDrivenImporter(verticesOutOfOrder, edgesOutOfOrder);
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
        genericImporter.addGraphAttributeConsumer(consumers.graphAttributeConsumer);
        genericImporter.importInput(input);
    }

    public Function<String, V> getVertexFactory() {
        return this.vertexFactory;
    }

    public void setVertexFactory(Function<String, V> vertexFactory) {
        this.vertexFactory = vertexFactory;
    }

    public BiFunction<String, Map<String, Attribute>, V> getVertexWithAttributesFactory() {
        return this.vertexWithAttributesFactory;
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

    private class Consumers {
        private Graph<V, E> graph;
        private Map<String, V> map;
        private Pair<String, String> lastPair;
        private E lastEdge;
        public final BiConsumer<String, Attribute> graphAttributeConsumer = (k, a) -> DOTImporter.access$700(DOTImporter.this, k, a);
        public final Consumer<String> vertexConsumer = t -> {
            Object v;
            if (this.map.containsKey(t)) {
                throw new ImportException("Node " + t + " already exists");
            }
            if (DOTImporter.this.vertexFactory != null) {
                v = DOTImporter.this.vertexFactory.apply((String)t);
                this.graph.addVertex(v);
            } else {
                v = this.graph.addVertex();
            }
            this.map.put((String)t, v);
            DOTImporter.this.notifyVertex(v);
            DOTImporter.this.notifyVertexAttribute(v, DOTImporter.DEFAULT_VERTEX_ID_KEY, DefaultAttribute.createAttribute(t));
        };
        public final BiConsumer<String, Map<String, Attribute>> vertexWithAttributesConsumer = (t, attrs) -> {
            Object v;
            if (this.map.containsKey(t)) {
                throw new ImportException("Node " + t + " already exists");
            }
            if (DOTImporter.this.vertexWithAttributesFactory != null) {
                v = DOTImporter.this.vertexWithAttributesFactory.apply((String)t, (Map<String, Attribute>)attrs);
                this.graph.addVertex(v);
            } else {
                v = this.graph.addVertex();
            }
            this.map.put((String)t, v);
            attrs.put(DOTImporter.DEFAULT_VERTEX_ID_KEY, DefaultAttribute.createAttribute(t));
            DOTImporter.this.notifyVertexWithAttributes(v, attrs);
        };
        public final BiConsumer<Pair<String, String>, Attribute> vertexAttributeConsumer = (p, a) -> {
            String vertex = (String)p.getFirst();
            if (!this.map.containsKey(vertex)) {
                throw new ImportException("Node " + vertex + " does not exist");
            }
            DOTImporter.this.notifyVertexAttribute(this.map.get(vertex), (String)p.getSecond(), a);
        };
        public final Consumer<Pair<String, String>> edgeConsumer = p -> {
            String source = (String)p.getFirst();
            Object from = this.map.get(p.getFirst());
            if (from == null) {
                throw new ImportException("Node " + source + " does not exist");
            }
            String target = (String)p.getSecond();
            Object to = this.map.get(target);
            if (to == null) {
                throw new ImportException("Node " + target + " does not exist");
            }
            Object e = this.graph.addEdge(from, to);
            DOTImporter.this.notifyEdge(e);
            this.lastPair = p;
            this.lastEdge = e;
        };
        public final BiConsumer<Pair<String, String>, Map<String, Attribute>> edgeWithAttributesConsumer = (p, attrs) -> {
            Object e;
            String source = (String)p.getFirst();
            Object from = this.map.get(p.getFirst());
            if (from == null) {
                throw new ImportException("Node " + source + " does not exist");
            }
            String target = (String)p.getSecond();
            Object to = this.map.get(target);
            if (to == null) {
                throw new ImportException("Node " + target + " does not exist");
            }
            if (DOTImporter.this.edgeWithAttributesFactory != null) {
                e = DOTImporter.this.edgeWithAttributesFactory.apply((Map<String, Attribute>)attrs);
                this.graph.addEdge(from, to, e);
            } else {
                e = this.graph.addEdge(from, to);
            }
            DOTImporter.this.notifyEdgeWithAttributes(e, attrs);
            this.lastPair = p;
            this.lastEdge = e;
        };
        public final BiConsumer<Pair<Pair<String, String>, String>, Attribute> edgeAttributeConsumer = (p, a) -> {
            if (p.getFirst() == this.lastPair) {
                DOTImporter.this.notifyEdgeAttribute(this.lastEdge, (String)p.getSecond(), a);
            }
        };

        public Consumers(Graph<V, E> graph) {
            this.graph = graph;
            this.map = new HashMap();
        }
    }
}

