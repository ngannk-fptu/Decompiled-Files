/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jgrapht.Graph
 */
package org.jgrapht.nio.graphml;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import org.jgrapht.Graph;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.AttributeType;
import org.jgrapht.nio.BaseExporter;
import org.jgrapht.nio.ExportException;
import org.jgrapht.nio.GraphExporter;
import org.jgrapht.nio.IntegerIdProvider;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class GraphMLExporter<V, E>
extends BaseExporter<V, E>
implements GraphExporter<V, E> {
    private Map<String, AttributeDetails> registeredAttributes = new LinkedHashMap<String, AttributeDetails>();
    private static final String ATTRIBUTE_KEY_PREFIX = "key";
    private int totalAttributes = 0;
    private static final String VERTEX_LABEL_DEFAULT_ATTRIBUTE_NAME = "VertexLabel";
    private static final String EDGE_WEIGHT_DEFAULT_ATTRIBUTE_NAME = "weight";
    private static final String EDGE_LABEL_DEFAULT_ATTRIBUTE_NAME = "EdgeLabel";
    private String vertexLabelAttributeName = "VertexLabel";
    private String edgeWeightAttributeName = "weight";
    private String edgeLabelAttributeName = "EdgeLabel";
    private boolean exportEdgeWeights = false;
    private boolean exportVertexLabels = false;
    private boolean exportEdgeLabels = false;

    public GraphMLExporter() {
        this(new IntegerIdProvider());
    }

    public GraphMLExporter(Function<V, String> vertexIdProvider) {
        super(vertexIdProvider);
    }

    public void registerAttribute(String name, AttributeCategory category, AttributeType type) {
        this.registerAttribute(name, category, type, null);
    }

    public void registerAttribute(String name, AttributeCategory category, AttributeType type, String defaultValue) {
        if (name == null) {
            throw new IllegalArgumentException("Attribute name cannot be null");
        }
        if (name.equals(this.vertexLabelAttributeName) || name.equals(this.edgeWeightAttributeName) || name.equals(this.edgeLabelAttributeName)) {
            throw new IllegalArgumentException("Reserved attribute name");
        }
        if (category == null) {
            throw new IllegalArgumentException("Attribute category must be one of node, edge, graph or all");
        }
        String nextKey = ATTRIBUTE_KEY_PREFIX + this.totalAttributes++;
        this.registeredAttributes.put(name, new AttributeDetails(nextKey, category, type, defaultValue));
    }

    public void unregisterAttribute(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Attribute name cannot be null");
        }
        if (name.equals(this.vertexLabelAttributeName) || name.equals(this.edgeWeightAttributeName) || name.equals(this.edgeLabelAttributeName)) {
            throw new IllegalArgumentException("Reserved attribute name");
        }
        this.registeredAttributes.remove(name);
    }

    public boolean isExportEdgeWeights() {
        return this.exportEdgeWeights;
    }

    public void setExportEdgeWeights(boolean exportEdgeWeights) {
        this.exportEdgeWeights = exportEdgeWeights;
    }

    public boolean isExportVertexLabels() {
        return this.exportVertexLabels;
    }

    public void setExportVertexLabels(boolean exportVertexLabels) {
        this.exportVertexLabels = exportVertexLabels;
    }

    public boolean isExportEdgeLabels() {
        return this.exportEdgeLabels;
    }

    public void setExportEdgeLabels(boolean exportEdgeLabels) {
        this.exportEdgeLabels = exportEdgeLabels;
    }

    public String getVertexLabelAttributeName() {
        return this.vertexLabelAttributeName;
    }

    public void setVertexLabelAttributeName(String vertexLabelAttributeName) {
        if (vertexLabelAttributeName == null) {
            throw new IllegalArgumentException("Vertex label attribute name cannot be null");
        }
        String key = vertexLabelAttributeName.trim();
        if (this.registeredAttributes.containsKey(key)) {
            throw new IllegalArgumentException("Reserved attribute name");
        }
        this.vertexLabelAttributeName = key;
    }

    public String getEdgeLabelAttributeName() {
        return this.edgeLabelAttributeName;
    }

    public void setEdgeLabelAttributeName(String edgeLabelAttributeName) {
        if (edgeLabelAttributeName == null) {
            throw new IllegalArgumentException("Edge label attribute name cannot be null");
        }
        String key = edgeLabelAttributeName.trim();
        if (this.registeredAttributes.containsKey(key)) {
            throw new IllegalArgumentException("Reserved attribute name");
        }
        this.edgeLabelAttributeName = key;
    }

    public String getEdgeWeightAttributeName() {
        return this.edgeWeightAttributeName;
    }

    public void setEdgeWeightAttributeName(String edgeWeightAttributeName) {
        if (edgeWeightAttributeName == null) {
            throw new IllegalArgumentException("Edge weight attribute name cannot be null");
        }
        String key = edgeWeightAttributeName.trim();
        if (this.registeredAttributes.containsKey(key)) {
            throw new IllegalArgumentException("Reserved attribute name");
        }
        this.edgeWeightAttributeName = key;
    }

    @Override
    public void exportGraph(Graph<V, E> g, Writer writer) {
        try {
            SAXTransformerFactory factory = (SAXTransformerFactory)SAXTransformerFactory.newInstance();
            TransformerHandler handler = factory.newTransformerHandler();
            handler.getTransformer().setOutputProperty("encoding", "UTF-8");
            handler.getTransformer().setOutputProperty("indent", "yes");
            handler.setResult(new StreamResult(new PrintWriter(writer)));
            handler.startDocument();
            this.writeHeader(handler);
            this.writeKeys(handler);
            this.writeGraphStart(handler, g);
            this.writeNodes(handler, g);
            this.writeEdges(handler, g);
            this.writeGraphEnd(handler);
            this.writeFooter(handler);
            handler.endDocument();
            writer.flush();
        }
        catch (Exception e) {
            throw new ExportException("Failed to export as GraphML", e);
        }
    }

    private void writeHeader(TransformerHandler handler) throws SAXException {
        handler.startPrefixMapping("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        handler.endPrefixMapping("xsi");
        AttributesImpl attr = new AttributesImpl();
        attr.addAttribute("", "", "xsi:schemaLocation", "CDATA", "http://graphml.graphdrawing.org/xmlns http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd");
        handler.startElement("http://graphml.graphdrawing.org/xmlns", "", "graphml", attr);
    }

    private void writeGraphStart(TransformerHandler handler, Graph<V, E> g) throws SAXException {
        AttributesImpl attr = new AttributesImpl();
        attr.addAttribute("", "", "edgedefault", "CDATA", g.getType().isDirected() ? "directed" : "undirected");
        handler.startElement("", "", "graph", attr);
    }

    private void writeGraphEnd(TransformerHandler handler) throws SAXException {
        handler.endElement("", "", "graph");
    }

    private void writeFooter(TransformerHandler handler) throws SAXException {
        handler.endElement("", "", "graphml");
    }

    private void writeKeys(TransformerHandler handler) throws SAXException {
        if (this.exportVertexLabels) {
            this.writeAttribute(handler, this.vertexLabelAttributeName, new AttributeDetails("vertex_label_key", AttributeCategory.NODE, AttributeType.STRING, null));
        }
        if (this.exportEdgeLabels) {
            this.writeAttribute(handler, this.edgeLabelAttributeName, new AttributeDetails("edge_label_key", AttributeCategory.EDGE, AttributeType.STRING, null));
        }
        if (this.exportEdgeWeights) {
            this.writeAttribute(handler, this.edgeWeightAttributeName, new AttributeDetails("edge_weight_key", AttributeCategory.EDGE, AttributeType.DOUBLE, Double.toString(1.0)));
        }
        for (String attributeName : this.registeredAttributes.keySet()) {
            AttributeDetails details = this.registeredAttributes.get(attributeName);
            this.writeAttribute(handler, attributeName, details);
        }
    }

    private void writeData(TransformerHandler handler, String key, String value) throws SAXException {
        AttributesImpl attr = new AttributesImpl();
        attr.addAttribute("", "", ATTRIBUTE_KEY_PREFIX, "CDATA", key);
        handler.startElement("", "", "data", attr);
        handler.characters(value.toCharArray(), 0, value.length());
        handler.endElement("", "", "data");
    }

    private void writeAttribute(TransformerHandler handler, String name, AttributeDetails details) throws SAXException {
        AttributesImpl attr = new AttributesImpl();
        attr.addAttribute("", "", "id", "CDATA", details.key);
        attr.addAttribute("", "", "for", "CDATA", details.category.toString());
        attr.addAttribute("", "", "attr.name", "CDATA", name);
        attr.addAttribute("", "", "attr.type", "CDATA", details.type.toString());
        handler.startElement("", "", ATTRIBUTE_KEY_PREFIX, attr);
        if (details.defaultValue != null) {
            handler.startElement("", "", "default", null);
            handler.characters(details.defaultValue.toCharArray(), 0, details.defaultValue.length());
            handler.endElement("", "", "default");
        }
        handler.endElement("", "", ATTRIBUTE_KEY_PREFIX);
    }

    private void writeNodes(TransformerHandler handler, Graph<V, E> g) throws SAXException {
        for (Object v : g.vertexSet()) {
            AttributesImpl attr = new AttributesImpl();
            attr.addAttribute("", "", "id", "CDATA", this.getVertexId(v));
            handler.startElement("", "", "node", attr);
            Optional<Attribute> vertexLabelAttribute = this.getVertexAttribute(v, this.vertexLabelAttributeName);
            if (this.exportVertexLabels) {
                if (vertexLabelAttribute.isPresent()) {
                    this.writeData(handler, "vertex_label_key", vertexLabelAttribute.get().getValue());
                } else {
                    this.writeData(handler, "vertex_label_key", String.valueOf(v));
                }
            }
            Map vertexAttributes = this.getVertexAttributes(v).orElse(Collections.emptyMap());
            for (Map.Entry<String, AttributeDetails> e : this.registeredAttributes.entrySet()) {
                AttributeDetails details = e.getValue();
                if (!details.category.equals((Object)AttributeCategory.NODE) && !details.category.equals((Object)AttributeCategory.ALL)) continue;
                String name = e.getKey();
                String defaultValue = details.defaultValue;
                if (!vertexAttributes.containsKey(name)) continue;
                Attribute attribute = (Attribute)vertexAttributes.get(name);
                String value = attribute.getValue();
                if (defaultValue != null && defaultValue.equals(value) || value == null) continue;
                this.writeData(handler, details.key, value);
            }
            handler.endElement("", "", "node");
        }
    }

    private void writeEdges(TransformerHandler handler, Graph<V, E> g) throws SAXException {
        for (Object e : g.edgeSet()) {
            Double weight;
            AttributesImpl attr = new AttributesImpl();
            this.getEdgeId(e).ifPresent(eId -> attr.addAttribute("", "", "id", "CDATA", (String)eId));
            attr.addAttribute("", "", "source", "CDATA", this.getVertexId(g.getEdgeSource(e)));
            attr.addAttribute("", "", "target", "CDATA", this.getVertexId(g.getEdgeTarget(e)));
            handler.startElement("", "", "edge", attr);
            Optional<Attribute> edgeLabelAttribute = this.getEdgeAttribute(e, this.edgeLabelAttributeName);
            if (this.exportEdgeLabels) {
                if (edgeLabelAttribute.isPresent()) {
                    this.writeData(handler, "edge_label_key", edgeLabelAttribute.get().getValue());
                } else {
                    this.writeData(handler, "edge_label_key", String.valueOf(e));
                }
            }
            if (this.exportEdgeWeights && !(weight = Double.valueOf(g.getEdgeWeight(e))).equals(1.0)) {
                this.writeData(handler, "edge_weight_key", String.valueOf(weight));
            }
            Map edgeAttributes = this.getEdgeAttributes(e).orElse(Collections.emptyMap());
            for (Map.Entry<String, AttributeDetails> entry : this.registeredAttributes.entrySet()) {
                AttributeDetails details = entry.getValue();
                if (!details.category.equals((Object)AttributeCategory.EDGE) && !details.category.equals((Object)AttributeCategory.ALL)) continue;
                String name = entry.getKey();
                String defaultValue = details.defaultValue;
                if (!edgeAttributes.containsKey(name)) continue;
                Attribute attribute = (Attribute)edgeAttributes.get(name);
                String value = attribute.getValue();
                if (defaultValue != null && defaultValue.equals(value) || value == null) continue;
                this.writeData(handler, details.key, value);
            }
            handler.endElement("", "", "edge");
        }
    }

    public static enum AttributeCategory {
        GRAPH("graph"),
        NODE("node"),
        EDGE("edge"),
        ALL("all");

        private String name;

        private AttributeCategory(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }
    }

    private class AttributeDetails {
        public String key;
        public AttributeCategory category;
        public AttributeType type;
        public String defaultValue;

        public AttributeDetails(String key, AttributeCategory category, AttributeType type, String defaultValue) {
            this.key = key;
            this.category = category;
            this.type = type;
            this.defaultValue = defaultValue;
        }
    }
}

