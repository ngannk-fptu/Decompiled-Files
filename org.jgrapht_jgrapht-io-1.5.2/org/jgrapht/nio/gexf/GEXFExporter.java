/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jgrapht.Graph
 */
package org.jgrapht.nio.gexf;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import org.jgrapht.Graph;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.BaseExporter;
import org.jgrapht.nio.ExportException;
import org.jgrapht.nio.GraphExporter;
import org.jgrapht.nio.IntegerIdProvider;
import org.jgrapht.nio.gexf.GEXFAttributeType;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class GEXFExporter<V, E>
extends BaseExporter<V, E>
implements GraphExporter<V, E> {
    private static final String LABEL_ATTRIBUTE_NAME = "label";
    private static final String WEIGHT_ATTRIBUTE_NAME = "weight";
    private static final String TYPE_ATTRIBUTE_NAME = "type";
    private static final Set<String> VERTEX_RESERVED_ATTRIBUTES = Set.of("id", "label");
    private static final Set<String> EDGE_RESERVED_ATTRIBUTES = Set.of("id", "type", "label", "source", "target", "weight");
    private int totalVertexAttributes = 0;
    private Map<String, AttributeDetails> registeredVertexAttributes;
    private int totalEdgeAttributes = 0;
    private Map<String, AttributeDetails> registeredEdgeAttributes;
    private final Set<Parameter> parameters;
    private String creator = "The JGraphT Library";
    private String keywords;
    private String description;

    public GEXFExporter() {
        this(new IntegerIdProvider(0), new IntegerIdProvider(0));
    }

    public GEXFExporter(Function<V, String> vertexIdProvider, Function<E, String> edgeIdProvider) {
        super(vertexIdProvider);
        this.edgeIdProvider = Optional.of(edgeIdProvider);
        this.registeredVertexAttributes = new LinkedHashMap<String, AttributeDetails>();
        this.registeredEdgeAttributes = new LinkedHashMap<String, AttributeDetails>();
        this.parameters = new HashSet<Parameter>();
        this.setParameter(Parameter.EXPORT_META, true);
    }

    public boolean isParameter(Parameter p) {
        return this.parameters.contains((Object)p);
    }

    public void setParameter(Parameter p, boolean value) {
        if (value) {
            this.parameters.add(p);
        } else {
            this.parameters.remove((Object)p);
        }
    }

    public void registerAttribute(String name, AttributeCategory category, GEXFAttributeType type) {
        this.registerAttribute(name, category, type, null);
    }

    public void registerAttribute(String name, AttributeCategory category, GEXFAttributeType type, String defaultValue) {
        this.registerAttribute(name, category, type, null, null);
    }

    public void registerAttribute(String name, AttributeCategory category, GEXFAttributeType type, String defaultValue, String options) {
        if (name == null) {
            throw new IllegalArgumentException("Attribute name cannot be null");
        }
        if (category == null) {
            throw new IllegalArgumentException("Attribute category must be one of node or edge");
        }
        if (category.equals((Object)AttributeCategory.NODE)) {
            if (VERTEX_RESERVED_ATTRIBUTES.contains(name.toLowerCase())) {
                throw new IllegalArgumentException("Reserved vertex attribute name");
            }
            this.registeredVertexAttributes.put(name, new AttributeDetails(String.valueOf(this.totalVertexAttributes++), type, defaultValue, options));
        } else if (category.equals((Object)AttributeCategory.EDGE)) {
            if (EDGE_RESERVED_ATTRIBUTES.contains(name.toLowerCase())) {
                throw new IllegalArgumentException("Reserved edge attribute name");
            }
            this.registeredEdgeAttributes.put(name, new AttributeDetails(String.valueOf(this.totalEdgeAttributes++), type, defaultValue, options));
        }
    }

    public void unregisterAttribute(String name, AttributeCategory category) {
        if (name == null) {
            throw new IllegalArgumentException("Attribute name cannot be null");
        }
        if (category == null) {
            throw new IllegalArgumentException("Attribute category must be one of node or edge");
        }
        if (category.equals((Object)AttributeCategory.NODE)) {
            if (VERTEX_RESERVED_ATTRIBUTES.contains(name.toLowerCase())) {
                throw new IllegalArgumentException("Reserved vertex attribute name");
            }
            this.registeredVertexAttributes.remove(name);
        } else if (category.equals((Object)AttributeCategory.EDGE)) {
            if (EDGE_RESERVED_ATTRIBUTES.contains(name.toLowerCase())) {
                throw new IllegalArgumentException("Reserved edge attribute name");
            }
            this.registeredEdgeAttributes.remove(name);
        }
    }

    public String getCreator() {
        return this.creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getKeywords() {
        return this.keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
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
            this.writeMeta(handler);
            this.writeGraphStart(handler, g);
            this.writeVertexAttributes(handler);
            this.writeEdgeAttributes(handler);
            this.writeVertices(handler, g);
            this.writeEdges(handler, g);
            this.writeGraphEnd(handler);
            this.writeFooter(handler);
            handler.endDocument();
            writer.flush();
        }
        catch (Exception e) {
            throw new ExportException("Failed to export as GEFX", e);
        }
    }

    private void writeHeader(TransformerHandler handler) throws SAXException {
        handler.startPrefixMapping("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        handler.endPrefixMapping("xsi");
        AttributesImpl attr = new AttributesImpl();
        attr.addAttribute("", "", "xsi:schemaLocation", "CDATA", "http://www.gexf.net/1.2draft http://www.gexf.net/1.2draft/gexf.xsd");
        attr.addAttribute("", "", "version", "CDATA", "1.2");
        handler.startElement("http://www.gexf.net/1.2draft", "", "gexf", attr);
    }

    private void writeMeta(TransformerHandler handler) throws SAXException {
        boolean exportMeta = this.parameters.contains((Object)Parameter.EXPORT_META);
        if (!exportMeta) {
            return;
        }
        if (this.creator == null && this.description == null && this.keywords == null) {
            return;
        }
        handler.startElement("", "", "meta", null);
        if (this.creator != null) {
            handler.startElement("", "", "creator", null);
            handler.characters(this.creator.toCharArray(), 0, this.creator.length());
            handler.endElement("", "", "creator");
        }
        if (this.description != null) {
            handler.startElement("", "", "description", null);
            handler.characters(this.description.toCharArray(), 0, this.description.length());
            handler.endElement("", "", "description");
        }
        if (this.keywords != null) {
            handler.startElement("", "", "keywords", null);
            handler.characters(this.keywords.toCharArray(), 0, this.keywords.length());
            handler.endElement("", "", "keywords");
        }
        handler.endElement("", "", "meta");
    }

    private void writeGraphStart(TransformerHandler handler, Graph<V, E> g) throws SAXException {
        AttributesImpl attr = new AttributesImpl();
        attr.addAttribute("", "", "defaultedgetype", "CDATA", g.getType().isDirected() ? "directed" : "undirected");
        handler.startElement("", "", "graph", attr);
    }

    private void writeGraphEnd(TransformerHandler handler) throws SAXException {
        handler.endElement("", "", "graph");
    }

    private void writeFooter(TransformerHandler handler) throws SAXException {
        handler.endElement("", "", "gexf");
    }

    private void writeVertexAttributes(TransformerHandler handler) throws SAXException {
        if (this.registeredVertexAttributes.isEmpty()) {
            return;
        }
        AttributesImpl attr = new AttributesImpl();
        attr.addAttribute("", "", "class", "CDATA", "node");
        handler.startElement("", "", "attributes", attr);
        for (Map.Entry<String, AttributeDetails> e : this.registeredVertexAttributes.entrySet()) {
            this.writeAttribute(handler, e.getKey(), e.getValue());
        }
        handler.endElement("", "", "attributes");
    }

    private void writeEdgeAttributes(TransformerHandler handler) throws SAXException {
        if (this.registeredEdgeAttributes.isEmpty()) {
            return;
        }
        AttributesImpl attr = new AttributesImpl();
        attr.addAttribute("", "", "class", "CDATA", "edge");
        handler.startElement("", "", "attributes", attr);
        for (Map.Entry<String, AttributeDetails> e : this.registeredEdgeAttributes.entrySet()) {
            this.writeAttribute(handler, e.getKey(), e.getValue());
        }
        handler.endElement("", "", "attributes");
    }

    private void writeAttribute(TransformerHandler handler, String name, AttributeDetails details) throws SAXException {
        AttributesImpl attr = new AttributesImpl();
        attr.addAttribute("", "", "id", "CDATA", details.key);
        attr.addAttribute("", "", "title", "CDATA", name);
        attr.addAttribute("", "", TYPE_ATTRIBUTE_NAME, "CDATA", details.type.toString());
        handler.startElement("", "", "attribute", attr);
        if (details.defaultValue != null) {
            handler.startElement("", "", "default", null);
            handler.characters(details.defaultValue.toCharArray(), 0, details.defaultValue.length());
            handler.endElement("", "", "default");
        }
        if (details.options != null) {
            handler.startElement("", "", "options", null);
            handler.characters(details.options.toCharArray(), 0, details.options.length());
            handler.endElement("", "", "options");
        }
        handler.endElement("", "", "attribute");
    }

    private void writeVertexAttributeValues(TransformerHandler handler, V v) throws SAXException {
        Map vertexAttributes = this.getVertexAttributes(v).orElse(Collections.emptyMap());
        if (vertexAttributes.isEmpty()) {
            return;
        }
        handler.startElement("", "", "attvalues", null);
        for (Map.Entry<String, AttributeDetails> entry : this.registeredVertexAttributes.entrySet()) {
            AttributeDetails details = entry.getValue();
            String name = entry.getKey();
            String defaultValue = details.defaultValue;
            if (!vertexAttributes.containsKey(name)) continue;
            Attribute attribute = (Attribute)vertexAttributes.get(name);
            String value = attribute.getValue();
            if (defaultValue != null && defaultValue.equals(value) || value == null) continue;
            this.writeAttributeValue(handler, details.key, value);
        }
        handler.endElement("", "", "attvalues");
    }

    private void writeEdgeAttributeValues(TransformerHandler handler, E e) throws SAXException {
        Map edgeAttributes = this.getEdgeAttributes(e).orElse(Collections.emptyMap());
        if (edgeAttributes.isEmpty()) {
            return;
        }
        handler.startElement("", "", "attvalues", null);
        for (Map.Entry<String, AttributeDetails> entry : this.registeredEdgeAttributes.entrySet()) {
            AttributeDetails details = entry.getValue();
            String name = entry.getKey();
            String defaultValue = details.defaultValue;
            if (!edgeAttributes.containsKey(name)) continue;
            Attribute attribute = (Attribute)edgeAttributes.get(name);
            String value = attribute.getValue();
            if (defaultValue != null && defaultValue.equals(value) || value == null) continue;
            this.writeAttributeValue(handler, details.key, value);
        }
        handler.endElement("", "", "attvalues");
    }

    private void writeAttributeValue(TransformerHandler handler, String key, String value) throws SAXException {
        AttributesImpl attr = new AttributesImpl();
        attr.addAttribute("", "", "for", "CDATA", key);
        attr.addAttribute("", "", "value", "CDATA", value);
        handler.startElement("", "", "attvalue", attr);
        handler.endElement("", "", "attvalue");
    }

    private void writeVertices(TransformerHandler handler, Graph<V, E> g) throws SAXException {
        handler.startElement("", "", "nodes", null);
        for (Object v : g.vertexSet()) {
            AttributesImpl attr = new AttributesImpl();
            attr.addAttribute("", "", "id", "CDATA", this.getVertexId(v));
            attr.addAttribute("", "", LABEL_ATTRIBUTE_NAME, "CDATA", this.getVertexAttribute(v, LABEL_ATTRIBUTE_NAME).map(Attribute::getValue).orElse(this.getVertexId(v)));
            handler.startElement("", "", "node", attr);
            this.writeVertexAttributeValues(handler, v);
            handler.endElement("", "", "node");
        }
        handler.endElement("", "", "nodes");
    }

    private void writeEdges(TransformerHandler handler, Graph<V, E> g) throws SAXException {
        boolean exportEdgeWeights = this.parameters.contains((Object)Parameter.EXPORT_EDGE_WEIGHTS);
        boolean exportEdgeTypes = this.parameters.contains((Object)Parameter.EXPORT_EDGE_TYPES);
        boolean exportEdgeLabels = this.parameters.contains((Object)Parameter.EXPORT_EDGE_LABELS);
        boolean isGraphDirected = g.getType().isDirected();
        handler.startElement("", "", "edges", null);
        for (Object e : g.edgeSet()) {
            AttributesImpl attr = new AttributesImpl();
            attr.addAttribute("", "", "id", "CDATA", this.getEdgeId(e).orElseThrow(() -> new IllegalArgumentException("Missing or failing edge id provider.")));
            attr.addAttribute("", "", "source", "CDATA", this.getVertexId(g.getEdgeSource(e)));
            attr.addAttribute("", "", "target", "CDATA", this.getVertexId(g.getEdgeTarget(e)));
            if (exportEdgeTypes) {
                attr.addAttribute("", "", TYPE_ATTRIBUTE_NAME, "CDATA", isGraphDirected ? "directed" : "undirected");
            }
            if (exportEdgeWeights) {
                attr.addAttribute("", "", WEIGHT_ATTRIBUTE_NAME, "CDATA", String.valueOf(g.getEdgeWeight(e)));
            }
            if (exportEdgeLabels) {
                this.getEdgeAttribute(e, LABEL_ATTRIBUTE_NAME).ifPresent(v -> attr.addAttribute("", "", LABEL_ATTRIBUTE_NAME, "CDATA", v.getValue()));
            }
            handler.startElement("", "", "edge", attr);
            this.writeEdgeAttributeValues(handler, e);
            handler.endElement("", "", "edge");
        }
        handler.endElement("", "", "edges");
    }

    public static enum Parameter {
        EXPORT_EDGE_WEIGHTS,
        EXPORT_EDGE_LABELS,
        EXPORT_EDGE_TYPES,
        EXPORT_META;

    }

    public static enum AttributeCategory {
        NODE("node"),
        EDGE("edge");

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
        public GEXFAttributeType type;
        public String defaultValue;
        public String options;

        public AttributeDetails(String key, GEXFAttributeType type, String defaultValue, String options) {
            this.key = key;
            this.type = type;
            this.defaultValue = defaultValue;
            this.options = options;
        }
    }
}

