/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jgrapht.Graph
 *  org.jgrapht.alg.util.Pair
 *  org.jgrapht.alg.util.Triple
 */
package org.jgrapht.nio.graphml;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import org.jgrapht.Graph;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.alg.util.Triple;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.BaseEventDrivenImporter;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.GraphImporter;
import org.jgrapht.nio.graphml.GraphMLEventDrivenImporter;

public class GraphMLImporter<V, E>
extends BaseEventDrivenImporter<V, E>
implements GraphImporter<V, E> {
    public static final String DEFAULT_VERTEX_ID_KEY = "ID";
    private static final String EDGE_WEIGHT_DEFAULT_ATTRIBUTE_NAME = "weight";
    private String edgeWeightAttributeName = "weight";
    private boolean schemaValidation = true;
    private Function<String, V> vertexFactory;

    public String getEdgeWeightAttributeName() {
        return this.edgeWeightAttributeName;
    }

    public void setEdgeWeightAttributeName(String edgeWeightAttributeName) {
        if (edgeWeightAttributeName == null) {
            throw new IllegalArgumentException("Edge weight attribute name cannot be null");
        }
        this.edgeWeightAttributeName = edgeWeightAttributeName;
    }

    public boolean isSchemaValidation() {
        return this.schemaValidation;
    }

    public void setSchemaValidation(boolean schemaValidation) {
        this.schemaValidation = schemaValidation;
    }

    public Function<String, V> getVertexFactory() {
        return this.vertexFactory;
    }

    public void setVertexFactory(Function<String, V> vertexFactory) {
        this.vertexFactory = vertexFactory;
    }

    @Override
    public void importGraph(Graph<V, E> graph, Reader input) {
        GraphMLEventDrivenImporter genericImporter = new GraphMLEventDrivenImporter();
        genericImporter.setEdgeWeightAttributeName(this.edgeWeightAttributeName);
        genericImporter.setSchemaValidation(this.schemaValidation);
        Consumers globalConsumer = new Consumers(graph);
        genericImporter.addGraphAttributeConsumer(globalConsumer.graphAttributeConsumer);
        genericImporter.addVertexAttributeConsumer(globalConsumer.vertexAttributeConsumer);
        genericImporter.addEdgeAttributeConsumer(globalConsumer.edgeAttributeConsumer);
        genericImporter.addVertexConsumer(globalConsumer.vertexConsumer);
        genericImporter.addEdgeConsumer(globalConsumer.edgeConsumer);
        genericImporter.importInput(input);
    }

    private class Consumers {
        private Graph<V, E> graph;
        private Map<String, V> nodesMap;
        private E lastEdge;
        private Triple<String, String, Double> lastTriple;
        public final BiConsumer<String, Attribute> graphAttributeConsumer = (key, a) -> GraphMLImporter.access$500(GraphMLImporter.this, key, a);
        public final BiConsumer<Pair<String, String>, Attribute> vertexAttributeConsumer = (vertexAndKey, a) -> GraphMLImporter.access$400(GraphMLImporter.this, this.mapNode((String)vertexAndKey.getFirst()), (String)vertexAndKey.getSecond(), a);
        public final BiConsumer<Pair<Triple<String, String, Double>, String>, Attribute> edgeAttributeConsumer = (edgeAndKey, a) -> {
            Triple qe = (Triple)edgeAndKey.getFirst();
            if (qe == this.lastTriple) {
                if (qe.getThird() != null && GraphMLImporter.this.edgeWeightAttributeName.equals(edgeAndKey.getSecond()) && this.graph.getType().isWeighted()) {
                    this.graph.setEdgeWeight(this.lastEdge, ((Double)qe.getThird()).doubleValue());
                }
                GraphMLImporter.this.notifyEdgeAttribute(this.lastEdge, (String)edgeAndKey.getSecond(), a);
            }
        };
        public final Consumer<String> vertexConsumer = vId -> {
            Object v = this.mapNode((String)vId);
            GraphMLImporter.this.notifyVertex(v);
            GraphMLImporter.this.notifyVertexAttribute(v, GraphMLImporter.DEFAULT_VERTEX_ID_KEY, DefaultAttribute.createAttribute(vId));
        };
        public final Consumer<Triple<String, String, Double>> edgeConsumer = qe -> {
            if (this.lastTriple != qe) {
                String source = (String)qe.getFirst();
                String target = (String)qe.getSecond();
                Double weight = (Double)qe.getThird();
                Object e = this.graph.addEdge(this.mapNode(source), this.mapNode(target));
                if (weight != null && this.graph.getType().isWeighted()) {
                    this.graph.setEdgeWeight(e, weight.doubleValue());
                }
                this.lastEdge = e;
                this.lastTriple = qe;
                GraphMLImporter.this.notifyEdge(this.lastEdge);
            }
        };

        public Consumers(Graph<V, E> graph) {
            this.graph = graph;
            this.nodesMap = new HashMap();
            this.lastEdge = null;
            this.lastTriple = null;
        }

        private V mapNode(String vId) {
            Object vertex = this.nodesMap.get(vId);
            if (vertex == null) {
                if (GraphMLImporter.this.vertexFactory != null) {
                    vertex = GraphMLImporter.this.vertexFactory.apply(vId);
                    this.graph.addVertex(vertex);
                } else {
                    vertex = this.graph.addVertex();
                }
                this.nodesMap.put(vId, vertex);
            }
            return vertex;
        }
    }
}

