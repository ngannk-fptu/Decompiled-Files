/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jgrapht.alg.util.Triple
 */
package org.jgrapht.nio.gexf;

import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.jgrapht.alg.util.Triple;
import org.jgrapht.nio.AttributeType;
import org.jgrapht.nio.BaseEventDrivenImporter;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.EventDrivenImporter;
import org.jgrapht.nio.ImportEvent;
import org.jgrapht.nio.ImportException;
import org.jgrapht.nio.gexf.GEXFAttributeType;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class SimpleGEXFEventDrivenImporter
extends BaseEventDrivenImporter<String, Triple<String, String, Double>>
implements EventDrivenImporter<String, Triple<String, String, Double>> {
    private static final List<String> SCHEMA_FILENAMES = List.of("viz.xsd", "gexf.xsd");
    private boolean schemaValidation = true;
    private static final List<String> GRAPH_ATTRS = List.of("defaultedgetype", "timeformat", "mode", "start", "end");
    private static final List<String> NODE_ATTRS = List.of("label", "pid");
    private static final List<String> EDGE_ATTRS = List.of("type", "label");

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
            GEXFHandler handler = new GEXFHandler();
            xmlReader.setContentHandler(handler);
            xmlReader.setErrorHandler(handler);
            this.notifyImportEvent(ImportEvent.START);
            xmlReader.parse(new InputSource(input));
            this.notifyImportEvent(ImportEvent.END);
        }
        catch (Exception e) {
            throw new ImportException("Failed to parse GEXF", e);
        }
    }

    private Schema createSchema() throws SAXException {
        Source[] sources = (Source[])SCHEMA_FILENAMES.stream().map(filename -> {
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream((String)filename);
            if (is == null) {
                throw new ImportException("Failed to locate xsd: " + filename);
            }
            return is;
        }).map(is -> new StreamSource((InputStream)is)).toArray(Source[]::new);
        SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        return factory.newSchema(sources);
    }

    private XMLReader createXMLReader() {
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            if (this.schemaValidation) {
                spf.setSchema(this.createSchema());
            }
            spf.setNamespaceAware(true);
            SAXParser saxParser = spf.newSAXParser();
            return saxParser.getXMLReader();
        }
        catch (Exception e) {
            throw new ImportException("Failed to parse GEXF", e);
        }
    }

    private static AttributeType toAttributeType(GEXFAttributeType type) {
        switch (type) {
            case BOOLEAN: {
                return AttributeType.BOOLEAN;
            }
            case INTEGER: {
                return AttributeType.INT;
            }
            case LONG: {
                return AttributeType.LONG;
            }
            case FLOAT: {
                return AttributeType.FLOAT;
            }
            case DOUBLE: {
                return AttributeType.DOUBLE;
            }
            case ANYURI: 
            case LISTSTRING: 
            case STRING: {
                return AttributeType.STRING;
            }
        }
        return AttributeType.UNKNOWN;
    }

    private class GEXFHandler
    extends DefaultHandler {
        private static final String GRAPH = "graph";
        private static final String NODE = "node";
        private static final String NODE_ID = "id";
        private static final String EDGE = "edge";
        private static final String EDGE_ID = "id";
        private static final String EDGE_SOURCE = "source";
        private static final String EDGE_TARGET = "target";
        private static final String EDGE_WEIGHT = "weight";
        private static final String ATTRIBUTES = "attributes";
        private static final String ATTRIBUTES_CLASS = "class";
        private static final String ATTRIBUTE = "attribute";
        private static final String ATTRIBUTE_ID = "id";
        private static final String ATTRIBUTE_TITLE = "title";
        private static final String ATTRIBUTE_TYPE = "type";
        private static final String ATTVALUES = "attvalues";
        private static final String ATTVALUE = "attvalue";
        private static final String ATTVALUE_FOR = "for";
        private static final String ATTVALUE_VALUE = "value";
        private int insideGraph;
        private int insideNode;
        private String currentNode;
        private int insideEdge;
        private Triple<String, String, Double> currentEdge;
        private int insideAttributes;
        private String attributesClass;
        private int insideAttribute;
        private int insideAttValues;
        private int insideAttValue;
        private Map<String, Attribute> nodeValidAttributes;
        private Map<String, Attribute> edgeValidAttributes;

        @Override
        public void startDocument() throws SAXException {
            this.insideGraph = 0;
            this.insideNode = 0;
            this.currentNode = null;
            this.insideEdge = 0;
            this.currentEdge = null;
            this.insideAttributes = 0;
            this.attributesClass = null;
            this.insideAttribute = 0;
            this.insideAttValues = 0;
            this.insideAttValue = 0;
            this.nodeValidAttributes = new HashMap<String, Attribute>();
            this.edgeValidAttributes = new HashMap<String, Attribute>();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            switch (localName) {
                case "graph": {
                    ++this.insideGraph;
                    if (this.insideGraph != 1) break;
                    for (String attrName : GRAPH_ATTRS) {
                        this.findAttribute(attrName, attributes).ifPresent(value -> SimpleGEXFEventDrivenImporter.this.notifyGraphAttribute(attrName, DefaultAttribute.createAttribute(value)));
                    }
                    break;
                }
                case "node": {
                    String nodeId;
                    ++this.insideNode;
                    if (!(this.insideNode == 1 ^ this.insideEdge == 1)) break;
                    this.currentNode = nodeId = this.findAttribute("id", attributes).orElseThrow(() -> new IllegalArgumentException("Node must have an identifier"));
                    SimpleGEXFEventDrivenImporter.this.notifyVertex(this.currentNode);
                    for (String attrName : NODE_ATTRS) {
                        this.findAttribute(attrName, attributes).ifPresent(value -> SimpleGEXFEventDrivenImporter.this.notifyVertexAttribute(this.currentNode, attrName, DefaultAttribute.createAttribute(value)));
                    }
                    break;
                }
                case "edge": {
                    ++this.insideEdge;
                    if (!(this.insideNode == 1 ^ this.insideEdge == 1)) break;
                    String sourceId = this.findAttribute(EDGE_SOURCE, attributes).orElseThrow(() -> new IllegalArgumentException("Edge source missing"));
                    String targetId = this.findAttribute(EDGE_TARGET, attributes).orElseThrow(() -> new IllegalArgumentException("Edge target missing"));
                    String edgeId = this.findAttribute("id", attributes).orElse(null);
                    String edgeWeight = this.findAttribute(EDGE_WEIGHT, attributes).orElse(null);
                    Double edgeWeightAsDouble = null;
                    if (edgeWeight != null) {
                        try {
                            edgeWeightAsDouble = Double.parseDouble(edgeWeight);
                        }
                        catch (NumberFormatException numberFormatException) {
                            // empty catch block
                        }
                    }
                    this.currentEdge = Triple.of((Object)sourceId, (Object)targetId, (Object)edgeWeightAsDouble);
                    SimpleGEXFEventDrivenImporter.this.notifyEdge(this.currentEdge);
                    if (edgeId != null) {
                        SimpleGEXFEventDrivenImporter.this.notifyEdgeAttribute(this.currentEdge, "id", DefaultAttribute.createAttribute(edgeId));
                    }
                    SimpleGEXFEventDrivenImporter.this.notifyEdgeAttribute(this.currentEdge, EDGE_SOURCE, DefaultAttribute.createAttribute(sourceId));
                    SimpleGEXFEventDrivenImporter.this.notifyEdgeAttribute(this.currentEdge, EDGE_TARGET, DefaultAttribute.createAttribute(targetId));
                    if (edgeWeightAsDouble != null) {
                        SimpleGEXFEventDrivenImporter.this.notifyEdgeAttribute(this.currentEdge, EDGE_WEIGHT, DefaultAttribute.createAttribute(edgeWeightAsDouble));
                    }
                    for (String attrName : EDGE_ATTRS) {
                        this.findAttribute(attrName, attributes).ifPresent(value -> SimpleGEXFEventDrivenImporter.this.notifyEdgeAttribute(this.currentEdge, attrName, DefaultAttribute.createAttribute(value)));
                    }
                    break;
                }
                case "attributes": {
                    ++this.insideAttributes;
                    if (this.insideGraph != 1 || this.insideAttributes != 1) break;
                    this.attributesClass = this.findAttribute(ATTRIBUTES_CLASS, attributes).orElseThrow(() -> new IllegalArgumentException("Attributes class missing"));
                    break;
                }
                case "attribute": {
                    ++this.insideAttribute;
                    if (this.insideGraph != 1 || this.insideAttributes != 1 || this.insideAttribute != 1) break;
                    String attributeId = this.findAttribute("id", attributes).orElseThrow(() -> new IllegalArgumentException("Attribute id missing"));
                    String attributeTitle = this.findAttribute(ATTRIBUTE_TITLE, attributes).orElseThrow(() -> new IllegalArgumentException("Attribute title missing"));
                    String attributeType = this.findAttribute(ATTRIBUTE_TYPE, attributes).orElseThrow(() -> new IllegalArgumentException("Attribute type missing"));
                    Attribute curAttribute = new Attribute(attributeId, attributeTitle, GEXFAttributeType.create(attributeType));
                    if (NODE.equals(this.attributesClass)) {
                        this.nodeValidAttributes.put(curAttribute.id, curAttribute);
                        break;
                    }
                    if (EDGE.equals(this.attributesClass)) {
                        this.edgeValidAttributes.put(curAttribute.id, curAttribute);
                        break;
                    }
                    throw new IllegalArgumentException("Wrong attribute class provided");
                }
                case "attvalues": {
                    ++this.insideAttValues;
                    break;
                }
                case "attvalue": {
                    ++this.insideAttValue;
                    if (this.insideAttValues != 1 || this.insideAttValue != 1 || !(this.insideNode == 1 ^ this.insideEdge == 1)) break;
                    String attValueFor = this.findAttribute(ATTVALUE_FOR, attributes).orElseThrow(() -> new IllegalArgumentException("Attribute for missing"));
                    String attValueValue = this.findAttribute(ATTVALUE_VALUE, attributes).orElseThrow(() -> new IllegalArgumentException("Attribute value missing"));
                    if (this.insideNode == 1 && this.currentNode != null) {
                        Attribute attr = this.nodeValidAttributes.get(attValueFor);
                        SimpleGEXFEventDrivenImporter.this.notifyVertexAttribute(this.currentNode, attr.title, new DefaultAttribute<String>(attValueValue, SimpleGEXFEventDrivenImporter.toAttributeType(attr.type)));
                        break;
                    }
                    if (this.insideEdge != 1 || this.currentEdge == null) break;
                    Attribute attr = this.edgeValidAttributes.get(attValueFor);
                    SimpleGEXFEventDrivenImporter.this.notifyEdgeAttribute(this.currentEdge, attr.title, new DefaultAttribute<String>(attValueValue, SimpleGEXFEventDrivenImporter.toAttributeType(attr.type)));
                    break;
                }
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            switch (localName) {
                case "graph": {
                    --this.insideGraph;
                    break;
                }
                case "node": {
                    --this.insideNode;
                    if (this.insideNode != 0) break;
                    this.currentNode = null;
                    break;
                }
                case "edge": {
                    --this.insideEdge;
                    if (this.insideEdge != 0) break;
                    this.currentEdge = null;
                    break;
                }
                case "attributes": {
                    --this.insideAttributes;
                    if (this.insideAttributes != 0) break;
                    this.attributesClass = null;
                    break;
                }
                case "attribute": {
                    --this.insideAttribute;
                    break;
                }
                case "attvalues": {
                    --this.insideAttValues;
                    break;
                }
                case "attvalue": {
                    --this.insideAttValue;
                    break;
                }
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

        private Optional<String> findAttribute(String localName, Attributes attributes) {
            for (int i = 0; i < attributes.getLength(); ++i) {
                String attrLocalName = attributes.getLocalName(i);
                if (!attrLocalName.equals(localName)) continue;
                return Optional.ofNullable(attributes.getValue(i));
            }
            return Optional.empty();
        }
    }

    private static class Attribute {
        String id;
        String title;
        GEXFAttributeType type;

        public Attribute(String id, String title, GEXFAttributeType type) {
            this.id = id;
            this.title = title;
            this.type = type;
        }
    }
}

