/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jgrapht.alg.util.Triple
 */
package org.jgrapht.nio.graphml;

import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.jgrapht.alg.util.Triple;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.AttributeType;
import org.jgrapht.nio.BaseEventDrivenImporter;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.EventDrivenImporter;
import org.jgrapht.nio.ImportEvent;
import org.jgrapht.nio.ImportException;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class GraphMLEventDrivenImporter
extends BaseEventDrivenImporter<String, Triple<String, String, Double>>
implements EventDrivenImporter<String, Triple<String, String, Double>> {
    private static final String GRAPHML_SCHEMA_FILENAME = "graphml.xsd";
    private static final String XLINK_SCHEMA_FILENAME = "xlink.xsd";
    private static final String EDGE_WEIGHT_DEFAULT_ATTRIBUTE_NAME = "weight";
    private String edgeWeightAttributeName = "weight";
    private boolean schemaValidation = true;

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

    @Override
    public void importInput(Reader input) {
        try {
            XMLReader xmlReader = this.createXMLReader();
            GraphMLHandler handler = new GraphMLHandler();
            xmlReader.setContentHandler(handler);
            xmlReader.setErrorHandler(handler);
            this.notifyImportEvent(ImportEvent.START);
            xmlReader.parse(new InputSource(input));
            handler.notifyInterestedParties();
            this.notifyImportEvent(ImportEvent.END);
        }
        catch (Exception e) {
            throw new ImportException("Failed to parse GraphML", e);
        }
    }

    private XMLReader createXMLReader() throws ImportException {
        try {
            SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
            SAXParserFactory spf = SAXParserFactory.newInstance();
            if (this.schemaValidation) {
                InputStream xsdStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(GRAPHML_SCHEMA_FILENAME);
                if (xsdStream == null) {
                    throw new ImportException("Failed to locate GraphML xsd");
                }
                InputStream xlinkStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(XLINK_SCHEMA_FILENAME);
                if (xlinkStream == null) {
                    throw new ImportException("Failed to locate XLink xsd");
                }
                Source[] sources = new Source[]{new StreamSource(xlinkStream), new StreamSource(xsdStream)};
                Schema schema = schemaFactory.newSchema(sources);
                spf.setSchema(schema);
            }
            spf.setNamespaceAware(true);
            SAXParser saxParser = spf.newSAXParser();
            return saxParser.getXMLReader();
        }
        catch (Exception e) {
            throw new ImportException("Failed to parse GraphML", e);
        }
    }

    private class GraphMLHandler
    extends DefaultHandler {
        private static final String GRAPH = "graph";
        private static final String GRAPH_ID = "id";
        private static final String NODE = "node";
        private static final String NODE_ID = "id";
        private static final String EDGE = "edge";
        private static final String ALL = "all";
        private static final String EDGE_SOURCE = "source";
        private static final String EDGE_TARGET = "target";
        private static final String KEY = "key";
        private static final String KEY_FOR = "for";
        private static final String KEY_ATTR_NAME = "attr.name";
        private static final String KEY_ATTR_TYPE = "attr.type";
        private static final String KEY_ID = "id";
        private static final String DEFAULT = "default";
        private static final String DATA = "data";
        private static final String DATA_KEY = "key";
        private Map<String, GraphElement> nodes;
        private List<GraphElement> edges;
        private boolean insideDefault;
        private boolean insideData;
        private Data currentData;
        private Key currentKey;
        private Deque<GraphElement> currentGraphElement;
        private Map<String, Key> nodeValidKeys;
        private Map<String, Key> edgeValidKeys;

        private GraphMLHandler() {
        }

        public void notifyInterestedParties() throws ImportException {
            if (this.nodes.isEmpty()) {
                return;
            }
            HashSet<String> graphNodes = new HashSet<String>();
            for (Map.Entry<String, GraphElement> en : this.nodes.entrySet()) {
                String nodeId = en.getKey();
                if (nodeId == null) {
                    throw new ImportException("Node id missing");
                }
                Map<String, String> collectedAttributes = en.getValue().attributes;
                LinkedHashMap<String, DefaultAttribute<String>> finalAttributes = new LinkedHashMap<String, DefaultAttribute<String>>();
                for (Key validKey : this.nodeValidKeys.values()) {
                    String validId = validKey.id;
                    AttributeType validType = validKey.type;
                    if (collectedAttributes.containsKey(validId)) {
                        finalAttributes.put(validKey.attributeName, new DefaultAttribute<String>(collectedAttributes.get(validId), validType));
                        continue;
                    }
                    if (validKey.defaultValue == null) continue;
                    finalAttributes.put(validKey.attributeName, new DefaultAttribute<String>(validKey.defaultValue, validType));
                }
                GraphMLEventDrivenImporter.this.notifyVertex(nodeId);
                for (String key : finalAttributes.keySet()) {
                    GraphMLEventDrivenImporter.this.notifyVertexAttribute(nodeId, key, (Attribute)finalAttributes.get(key));
                }
                graphNodes.add(nodeId);
            }
            boolean handleSpecialEdgeWeights = false;
            double defaultSpecialEdgeWeight = 1.0;
            for (Key k : this.edgeValidKeys.values()) {
                if (!k.attributeName.equals(GraphMLEventDrivenImporter.this.edgeWeightAttributeName)) continue;
                handleSpecialEdgeWeights = true;
                String defaultValue = k.defaultValue;
                try {
                    if (defaultValue == null) break;
                    defaultSpecialEdgeWeight = Double.parseDouble(defaultValue);
                }
                catch (NumberFormatException key) {}
                break;
            }
            for (GraphElement p : this.edges) {
                if (p.id1 == null) {
                    throw new ImportException("Edge source vertex missing");
                }
                if (!graphNodes.contains(p.id1)) {
                    throw new ImportException("Source vertex " + p.id1 + " not found");
                }
                if (p.id2 == null) {
                    throw new ImportException("Edge target vertex missing");
                }
                if (!graphNodes.contains(p.id2)) {
                    throw new ImportException("Target vertex " + p.id2 + " not found");
                }
                Map<String, String> collectedAttributes = p.attributes;
                LinkedHashMap<String, DefaultAttribute<String>> finalAttributes = new LinkedHashMap<String, DefaultAttribute<String>>();
                for (Key validKey : this.edgeValidKeys.values()) {
                    String validId = validKey.id;
                    AttributeType validType = validKey.type;
                    if (collectedAttributes.containsKey(validId)) {
                        finalAttributes.put(validKey.attributeName, new DefaultAttribute<String>(collectedAttributes.get(validId), validType));
                        continue;
                    }
                    if (validKey.defaultValue == null) continue;
                    finalAttributes.put(validKey.attributeName, new DefaultAttribute<String>(validKey.defaultValue, validType));
                }
                Triple te = Triple.of((Object)p.id1, (Object)p.id2, null);
                if (handleSpecialEdgeWeights && finalAttributes.containsKey(GraphMLEventDrivenImporter.this.edgeWeightAttributeName)) {
                    try {
                        te.setThird((Object)Double.parseDouble(((Attribute)finalAttributes.get(GraphMLEventDrivenImporter.this.edgeWeightAttributeName)).getValue()));
                    }
                    catch (NumberFormatException nfe) {
                        te.setThird((Object)defaultSpecialEdgeWeight);
                    }
                }
                GraphMLEventDrivenImporter.this.notifyEdge(te);
                for (String key : finalAttributes.keySet()) {
                    GraphMLEventDrivenImporter.this.notifyEdgeAttribute(te, key, (Attribute)finalAttributes.get(key));
                }
            }
        }

        @Override
        public void startDocument() throws SAXException {
            this.nodes = new LinkedHashMap<String, GraphElement>();
            this.edges = new ArrayList<GraphElement>();
            this.nodeValidKeys = new LinkedHashMap<String, Key>();
            this.edgeValidKeys = new LinkedHashMap<String, Key>();
            this.insideDefault = false;
            this.insideData = false;
            this.currentKey = null;
            this.currentData = null;
            this.currentGraphElement = new ArrayDeque<GraphElement>();
            this.currentGraphElement.push(new GraphElement("graphml"));
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            switch (localName) {
                case "graph": {
                    this.currentGraphElement.push(new GraphElement(this.findAttribute("id", attributes)));
                    break;
                }
                case "node": {
                    this.currentGraphElement.push(new GraphElement(this.findAttribute("id", attributes)));
                    break;
                }
                case "edge": {
                    this.currentGraphElement.push(new GraphElement(this.findAttribute(EDGE_SOURCE, attributes), this.findAttribute(EDGE_TARGET, attributes)));
                    break;
                }
                case "key": {
                    String keyId = this.findAttribute("id", attributes);
                    String keyFor = this.findAttribute(KEY_FOR, attributes);
                    String keyAttrName = this.findAttribute(KEY_ATTR_NAME, attributes);
                    String keyAttrType = this.findAttribute(KEY_ATTR_TYPE, attributes);
                    this.currentKey = new Key(keyId, keyAttrName, null, null);
                    if (keyAttrType != null) {
                        this.currentKey.type = AttributeType.create(keyAttrType);
                    }
                    if (keyFor == null) break;
                    switch (keyFor) {
                        case "edge": {
                            this.currentKey.target = KeyTarget.EDGE;
                            break;
                        }
                        case "node": {
                            this.currentKey.target = KeyTarget.NODE;
                            break;
                        }
                        case "all": {
                            this.currentKey.target = KeyTarget.ALL;
                        }
                    }
                    break;
                }
                case "default": {
                    this.insideDefault = true;
                    break;
                }
                case "data": {
                    this.insideData = true;
                    this.currentData = new Data(this.findAttribute("key", attributes), null);
                    break;
                }
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            switch (localName) {
                case "graph": {
                    this.currentGraphElement.pop();
                    break;
                }
                case "node": {
                    GraphElement currentNode = this.currentGraphElement.pop();
                    if (this.nodes.containsKey(currentNode.id1)) {
                        throw new SAXException("Node with id " + currentNode.id1 + " already exists");
                    }
                    this.nodes.put(currentNode.id1, currentNode);
                    break;
                }
                case "edge": {
                    GraphElement currentEdge = this.currentGraphElement.pop();
                    this.edges.add(currentEdge);
                    break;
                }
                case "key": {
                    if (this.currentKey.isValid()) {
                        switch (this.currentKey.target) {
                            case NODE: {
                                this.nodeValidKeys.put(this.currentKey.id, this.currentKey);
                                break;
                            }
                            case EDGE: {
                                this.edgeValidKeys.put(this.currentKey.id, this.currentKey);
                                break;
                            }
                            case ALL: {
                                this.nodeValidKeys.put(this.currentKey.id, this.currentKey);
                                this.edgeValidKeys.put(this.currentKey.id, this.currentKey);
                            }
                        }
                    }
                    this.currentKey = null;
                    break;
                }
                case "default": {
                    this.insideDefault = false;
                    break;
                }
                case "data": {
                    if (this.currentData.isValid()) {
                        this.currentGraphElement.peek().attributes.put(this.currentData.key, this.currentData.value);
                    }
                    this.insideData = false;
                    this.currentData = null;
                    break;
                }
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (this.insideDefault) {
                this.currentKey.defaultValue = this.currentKey.defaultValue != null ? this.currentKey.defaultValue + new String(ch, start, length) : new String(ch, start, length);
            } else if (this.insideData) {
                this.currentData.value = this.currentData.value != null ? this.currentData.value + new String(ch, start, length) : new String(ch, start, length);
            }
        }

        @Override
        public void warning(SAXParseException e) throws SAXException {
            throw e;
        }

        @Override
        public void error(SAXParseException e) throws SAXException {
            throw e;
        }

        @Override
        public void fatalError(SAXParseException e) throws SAXException {
            throw e;
        }

        private String findAttribute(String localName, Attributes attributes) {
            for (int i = 0; i < attributes.getLength(); ++i) {
                String attrLocalName = attributes.getLocalName(i);
                if (!attrLocalName.equals(localName)) continue;
                return attributes.getValue(i);
            }
            return null;
        }
    }

    private class GraphElement {
        String id1;
        String id2;
        Map<String, String> attributes;

        public GraphElement(String id1) {
            this.id1 = id1;
            this.id2 = null;
            this.attributes = new LinkedHashMap<String, String>();
        }

        public GraphElement(String id1, String id2) {
            this.id1 = id1;
            this.id2 = id2;
            this.attributes = new LinkedHashMap<String, String>();
        }
    }

    private class Data {
        String key;
        String value;

        public Data(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public boolean isValid() {
            return this.key != null && this.value != null;
        }
    }

    private class Key {
        String id;
        String attributeName;
        String defaultValue;
        KeyTarget target;
        AttributeType type;

        public Key(String id, String attributeName, String defaultValue, KeyTarget target) {
            this.id = id;
            this.attributeName = attributeName;
            this.defaultValue = defaultValue;
            this.target = target;
            this.type = AttributeType.STRING;
        }

        public boolean isValid() {
            return this.id != null && this.attributeName != null && this.target != null;
        }
    }

    private static enum KeyTarget {
        NODE,
        EDGE,
        ALL;

    }
}

