/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jgrapht.alg.util.Triple
 */
package org.jgrapht.nio.graphml;

import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class SimpleGraphMLEventDrivenImporter
extends BaseEventDrivenImporter<String, Triple<String, String, Double>>
implements EventDrivenImporter<String, Triple<String, String, Double>> {
    private static final String GRAPHML_SCHEMA_FILENAME = "graphml.xsd";
    private static final String XLINK_SCHEMA_FILENAME = "xlink.xsd";
    private static final String EDGE_WEIGHT_DEFAULT_ATTRIBUTE_NAME = "weight";
    private boolean schemaValidation = true;
    private String edgeWeightAttributeName = "weight";

    public String getEdgeWeightAttributeName() {
        return this.edgeWeightAttributeName;
    }

    public void setEdgeWeightAttributeName(String edgeWeightAttributeName) {
        this.edgeWeightAttributeName = Objects.requireNonNull(edgeWeightAttributeName, "Edge weight attribute name cannot be null");
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
            this.notifyImportEvent(ImportEvent.END);
        }
        catch (Exception e) {
            throw new ImportException("Failed to parse GraphML", e);
        }
    }

    private XMLReader createXMLReader() {
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
        private static final String GRAPH_EDGE_DEFAULT = "edgedefault";
        private static final String NODE = "node";
        private static final String NODE_ID = "id";
        private static final String EDGE = "edge";
        private static final String EDGE_ID = "id";
        private static final String EDGE_SOURCE = "source";
        private static final String EDGE_TARGET = "target";
        private static final String ALL = "all";
        private static final String KEY = "key";
        private static final String KEY_FOR = "for";
        private static final String KEY_ATTR_NAME = "attr.name";
        private static final String KEY_ATTR_TYPE = "attr.type";
        private static final String KEY_ID = "id";
        private static final String DEFAULT = "default";
        private static final String DATA = "data";
        private static final String DATA_KEY = "key";
        private int insideData;
        private int insideGraph;
        private int insideNode;
        private String currentNode;
        private int insideEdge;
        private Triple<String, String, Double> currentEdge;
        private Key currentKey;
        private String currentDataKey;
        private StringBuilder currentDataValue;
        private Map<String, Key> nodeValidKeys;
        private Map<String, Key> edgeValidKeys;
        private Map<String, Key> graphValidKeys;

        @Override
        public void startDocument() throws SAXException {
            this.insideData = 0;
            this.insideGraph = 0;
            this.insideNode = 0;
            this.currentNode = null;
            this.insideEdge = 0;
            this.currentEdge = null;
            this.currentKey = null;
            this.currentDataKey = null;
            this.currentDataValue = new StringBuilder();
            this.nodeValidKeys = new HashMap<String, Key>();
            this.edgeValidKeys = new HashMap<String, Key>();
            this.graphValidKeys = new HashMap<String, Key>();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            switch (localName) {
                case "graph": {
                    if (this.insideGraph > 0) {
                        throw new IllegalArgumentException("This importer does not support nested graphs");
                    }
                    ++this.insideGraph;
                    this.findAttribute("id", attributes).ifPresent(value -> SimpleGraphMLEventDrivenImporter.this.notifyGraphAttribute("id", DefaultAttribute.createAttribute(value)));
                    this.findAttribute(GRAPH_EDGE_DEFAULT, attributes).ifPresent(value -> SimpleGraphMLEventDrivenImporter.this.notifyGraphAttribute(GRAPH_EDGE_DEFAULT, DefaultAttribute.createAttribute(value)));
                    break;
                }
                case "node": {
                    String nodeId;
                    if (this.insideNode > 0 || this.insideEdge > 0) {
                        throw new IllegalArgumentException("Nodes cannot be inside other nodes or edges");
                    }
                    ++this.insideNode;
                    this.currentNode = nodeId = this.findAttribute("id", attributes).orElseThrow(() -> new IllegalArgumentException("Node must have an identifier"));
                    SimpleGraphMLEventDrivenImporter.this.notifyVertex(this.currentNode);
                    SimpleGraphMLEventDrivenImporter.this.notifyVertexAttribute(this.currentNode, "id", DefaultAttribute.createAttribute(nodeId));
                    break;
                }
                case "edge": {
                    if (this.insideNode > 0 || this.insideEdge > 0) {
                        throw new IllegalArgumentException("Edges cannot be inside other nodes or edges");
                    }
                    ++this.insideEdge;
                    String sourceId = this.findAttribute(EDGE_SOURCE, attributes).orElseThrow(() -> new IllegalArgumentException("Edge source missing"));
                    String targetId = this.findAttribute(EDGE_TARGET, attributes).orElseThrow(() -> new IllegalArgumentException("Edge target missing"));
                    String edgeId = this.findAttribute("id", attributes).orElse(null);
                    this.currentEdge = Triple.of((Object)sourceId, (Object)targetId, null);
                    SimpleGraphMLEventDrivenImporter.this.notifyEdge(this.currentEdge);
                    if (edgeId != null) {
                        SimpleGraphMLEventDrivenImporter.this.notifyEdgeAttribute(this.currentEdge, "id", DefaultAttribute.createAttribute(edgeId));
                    }
                    SimpleGraphMLEventDrivenImporter.this.notifyEdgeAttribute(this.currentEdge, EDGE_SOURCE, DefaultAttribute.createAttribute(sourceId));
                    SimpleGraphMLEventDrivenImporter.this.notifyEdgeAttribute(this.currentEdge, EDGE_TARGET, DefaultAttribute.createAttribute(targetId));
                    break;
                }
                case "key": {
                    String keyId = this.findAttribute("id", attributes).orElseThrow(() -> new IllegalArgumentException("Key id missing"));
                    String keyAttrName = this.findAttribute(KEY_ATTR_NAME, attributes).orElseThrow(() -> new IllegalArgumentException("Key attribute name missing"));
                    this.currentKey = new Key(keyId, keyAttrName, this.findAttribute(KEY_ATTR_TYPE, attributes).map(AttributeType::create).orElse(AttributeType.UNKNOWN), this.findAttribute(KEY_FOR, attributes).orElse("ALL"));
                    break;
                }
                case "default": {
                    break;
                }
                case "data": {
                    ++this.insideData;
                    this.findAttribute("key", attributes).ifPresent(data -> {
                        this.currentDataKey = data;
                    });
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
                    this.currentNode = null;
                    --this.insideNode;
                    break;
                }
                case "edge": {
                    if (this.currentEdge != null && this.currentEdge.getThird() != null) {
                        SimpleGraphMLEventDrivenImporter.this.notifyEdgeAttribute(this.currentEdge, SimpleGraphMLEventDrivenImporter.this.edgeWeightAttributeName, DefaultAttribute.createAttribute((Double)this.currentEdge.getThird()));
                    }
                    this.currentEdge = null;
                    --this.insideEdge;
                    break;
                }
                case "key": {
                    this.registerKey();
                    this.currentKey = null;
                    break;
                }
                case "default": {
                    break;
                }
                case "data": {
                    if (--this.insideData != 0) break;
                    this.notifyData();
                    this.currentDataValue.setLength(0);
                    this.currentDataKey = null;
                    break;
                }
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (this.insideData == 1) {
                this.currentDataValue.append(ch, start, length);
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

        private void notifyData() {
            Key key;
            if (this.currentDataKey == null || this.currentDataValue.length() == 0) {
                return;
            }
            if (this.currentNode != null && (key = this.nodeValidKeys.get(this.currentDataKey)) != null) {
                SimpleGraphMLEventDrivenImporter.this.notifyVertexAttribute(this.currentNode, key.attributeName, new DefaultAttribute<String>(this.currentDataValue.toString(), key.type));
            }
            if (this.currentEdge != null && (key = this.edgeValidKeys.get(this.currentDataKey)) != null) {
                if (key.attributeName.equals(SimpleGraphMLEventDrivenImporter.this.edgeWeightAttributeName)) {
                    try {
                        this.currentEdge.setThird((Object)Double.parseDouble(this.currentDataValue.toString()));
                    }
                    catch (NumberFormatException numberFormatException) {}
                } else {
                    SimpleGraphMLEventDrivenImporter.this.notifyEdgeAttribute(this.currentEdge, key.attributeName, new DefaultAttribute<String>(this.currentDataValue.toString(), key.type));
                }
            }
            if ((key = this.graphValidKeys.get(this.currentDataKey)) != null) {
                SimpleGraphMLEventDrivenImporter.this.notifyGraphAttribute(key.attributeName, new DefaultAttribute<String>(this.currentDataValue.toString(), key.type));
            }
        }

        private void registerKey() {
            if (this.currentKey.isValid()) {
                switch (this.currentKey.target) {
                    case "node": {
                        this.nodeValidKeys.put(this.currentKey.id, this.currentKey);
                        break;
                    }
                    case "edge": {
                        this.edgeValidKeys.put(this.currentKey.id, this.currentKey);
                        break;
                    }
                    case "graph": {
                        this.graphValidKeys.put(this.currentKey.id, this.currentKey);
                        break;
                    }
                    case "all": {
                        this.nodeValidKeys.put(this.currentKey.id, this.currentKey);
                        this.edgeValidKeys.put(this.currentKey.id, this.currentKey);
                        this.graphValidKeys.put(this.currentKey.id, this.currentKey);
                    }
                }
            }
        }
    }

    private static class Key {
        String id;
        String attributeName;
        String target;
        AttributeType type;

        public Key(String id, String attributeName, AttributeType type, String target) {
            this.id = id;
            this.attributeName = attributeName;
            this.type = type;
            this.target = target;
        }

        public boolean isValid() {
            return this.id != null && this.attributeName != null && this.target != null;
        }
    }
}

