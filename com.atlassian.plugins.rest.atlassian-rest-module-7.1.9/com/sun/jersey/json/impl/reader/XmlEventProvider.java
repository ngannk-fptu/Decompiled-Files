/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.json.impl.reader;

import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.json.impl.reader.CharactersEvent;
import com.sun.jersey.json.impl.reader.EndDocumentEvent;
import com.sun.jersey.json.impl.reader.EndElementEvent;
import com.sun.jersey.json.impl.reader.JsonXmlEvent;
import com.sun.jersey.json.impl.reader.StartDocumentEvent;
import com.sun.jersey.json.impl.reader.StartElementEvent;
import com.sun.jersey.json.impl.reader.StaxLocation;
import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import org.codehaus.jackson.JsonLocation;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;

public abstract class XmlEventProvider {
    private static final Logger LOGGER = Logger.getLogger(XmlEventProvider.class.getName());
    private final JSONConfiguration configuration;
    private final CachedJsonParser parser;
    private final String rootName;
    private final Deque<JsonXmlEvent> eventQueue = new LinkedList<JsonXmlEvent>();
    private final Stack<ProcessingInfo> processingStack = new Stack();

    protected XmlEventProvider(JsonParser parser, JSONConfiguration configuration, String rootName) throws XMLStreamException {
        this.parser = new CachedJsonParser(parser);
        this.configuration = configuration;
        this.rootName = rootName;
        try {
            this.readNext();
        }
        catch (XMLStreamException ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
            throw new XMLStreamException(ex);
        }
    }

    void close() throws XMLStreamException {
        this.eventQueue.clear();
        this.processingStack.empty();
        try {
            this.parser.close();
        }
        catch (IOException ioe) {
            throw new XMLStreamException(ioe);
        }
    }

    protected JsonXmlEvent createEndElementEvent(QName elementName, Location location) {
        return new EndElementEvent(elementName, location);
    }

    protected JsonXmlEvent createStartElementEvent(QName elementName, Location location) {
        return new StartElementEvent(elementName, location);
    }

    protected String getAttributeName(String jsonFieldName) {
        return '@' == jsonFieldName.charAt(0) ? jsonFieldName.substring(1) : jsonFieldName;
    }

    protected abstract QName getAttributeQName(String var1);

    JsonXmlEvent getCurrentNode() {
        return this.eventQueue.peek();
    }

    protected abstract QName getElementQName(String var1);

    protected JSONConfiguration getJsonConfiguration() {
        return this.configuration;
    }

    private String getPrimitiveFieldValue(JsonToken jsonToken, String jsonFieldValue) throws IOException {
        if (jsonToken == JsonToken.VALUE_FALSE || jsonToken == JsonToken.VALUE_TRUE || jsonToken == JsonToken.VALUE_STRING || jsonToken == JsonToken.VALUE_NUMBER_FLOAT || jsonToken == JsonToken.VALUE_NUMBER_INT || jsonToken == JsonToken.VALUE_NULL) {
            return jsonFieldValue;
        }
        throw new IOException("Not an XML value, expected primitive value!");
    }

    protected abstract boolean isAttribute(String var1);

    void processAttributesOfCurrentElement() throws XMLStreamException {
        this.eventQueue.peek().setAttributes(new LinkedList<JsonXmlEvent.Attribute>());
        this.processTokens(true);
    }

    private JsonXmlEvent processTokens(boolean processAttributes) throws XMLStreamException {
        if (!processAttributes) {
            this.eventQueue.poll();
        }
        try {
            if (this.eventQueue.isEmpty() || processAttributes) {
                JsonToken jsonToken;
                block10: while (true) {
                    ProcessingInfo pi;
                    jsonToken = this.parser.nextToken();
                    ProcessingInfo processingInfo = pi = this.processingStack.isEmpty() ? null : this.processingStack.peek();
                    if (jsonToken == null) {
                        return this.getCurrentNode();
                    }
                    switch (jsonToken) {
                        case FIELD_NAME: {
                            String fieldName = this.parser.getCurrentName();
                            if (this.isAttribute(fieldName)) {
                                QName attributeName = this.getAttributeQName(fieldName);
                                String attributeValue = this.getPrimitiveFieldValue(this.parser.nextToken(), this.parser.getText());
                                this.eventQueue.peek().getAttributes().add(new JsonXmlEvent.Attribute(attributeName, attributeValue));
                                continue block10;
                            }
                            processAttributes = false;
                            if ("$".equals(fieldName)) {
                                String value = this.getPrimitiveFieldValue(this.parser.nextToken(), this.parser.getText());
                                this.eventQueue.add(new CharactersEvent(value, (Location)new StaxLocation(this.parser.getCurrentLocation())));
                                continue block10;
                            }
                            QName elementName = this.getElementQName(fieldName);
                            JsonLocation currentLocation = this.parser.getCurrentLocation();
                            boolean isRootEmpty = this.isEmptyElement(fieldName, true);
                            if (isRootEmpty) {
                                this.eventQueue.add(this.createStartElementEvent(elementName, new StaxLocation(currentLocation)));
                                this.eventQueue.add(this.createEndElementEvent(elementName, new StaxLocation(currentLocation)));
                                this.eventQueue.add(new EndDocumentEvent(new StaxLocation(this.parser.getCurrentLocation())));
                            } else {
                                if (!this.isEmptyArray() && !this.isEmptyElement(fieldName, false)) {
                                    this.eventQueue.add(this.createStartElementEvent(elementName, new StaxLocation(currentLocation)));
                                    this.processingStack.add(new ProcessingInfo(elementName, false, true));
                                }
                                if (!this.parser.hasMoreTokens()) {
                                    this.eventQueue.add(new EndDocumentEvent(new StaxLocation(this.parser.getCurrentLocation())));
                                }
                            }
                            if (this.eventQueue.isEmpty()) continue block10;
                            return this.getCurrentNode();
                        }
                        case START_OBJECT: {
                            if (pi == null) {
                                this.eventQueue.add(new StartDocumentEvent(new StaxLocation(0, 0, 0)));
                                return this.getCurrentNode();
                            }
                            if (pi.isArray && !pi.isFirstElement) {
                                this.eventQueue.add(this.createStartElementEvent(pi.name, new StaxLocation(this.parser.getCurrentLocation())));
                                return this.getCurrentNode();
                            }
                            pi.isFirstElement = false;
                            continue block10;
                        }
                        case END_OBJECT: {
                            processAttributes = false;
                            this.eventQueue.add(this.createEndElementEvent(pi.name, new StaxLocation(this.parser.getCurrentLocation())));
                            if (!pi.isArray) {
                                this.processingStack.pop();
                            }
                            if (this.processingStack.isEmpty()) {
                                this.eventQueue.add(new EndDocumentEvent(new StaxLocation(this.parser.getCurrentLocation())));
                                JsonToken nextToken = this.parser.nextToken();
                                if (nextToken != null && nextToken != JsonToken.END_OBJECT || this.parser.peek() != null) {
                                    throw new RuntimeException("Unexpected token: " + this.parser.getText());
                                }
                            }
                            return this.getCurrentNode();
                        }
                        case VALUE_FALSE: 
                        case VALUE_NULL: 
                        case VALUE_NUMBER_FLOAT: 
                        case VALUE_NUMBER_INT: 
                        case VALUE_TRUE: 
                        case VALUE_STRING: {
                            if (!pi.isFirstElement) {
                                this.eventQueue.add(this.createStartElementEvent(pi.name, new StaxLocation(this.parser.getCurrentLocation())));
                            } else {
                                pi.isFirstElement = false;
                            }
                            if (jsonToken != JsonToken.VALUE_NULL) {
                                this.eventQueue.add(new CharactersEvent(this.parser.getText(), (Location)new StaxLocation(this.parser.getCurrentLocation())));
                            }
                            this.eventQueue.add(new EndElementEvent(pi.name, (Location)new StaxLocation(this.parser.getCurrentLocation())));
                            if (!pi.isArray) {
                                this.processingStack.pop();
                            }
                            if (this.processingStack.isEmpty()) {
                                this.eventQueue.add(new EndDocumentEvent(new StaxLocation(this.parser.getCurrentLocation())));
                            }
                            processAttributes = false;
                            return this.getCurrentNode();
                        }
                        case START_ARRAY: {
                            this.processingStack.peek().isArray = true;
                            continue block10;
                        }
                        case END_ARRAY: {
                            this.processingStack.pop();
                            processAttributes = false;
                            continue block10;
                        }
                    }
                    break;
                }
                throw new IllegalStateException("Unknown JSON token: " + (Object)((Object)jsonToken));
            }
            return this.eventQueue.peek();
        }
        catch (Exception e) {
            throw new XMLStreamException(e);
        }
    }

    private boolean isEmptyArray() throws IOException {
        JsonToken jsonToken = this.parser.peek();
        if (jsonToken == JsonToken.START_ARRAY && this.parser.peekNext() == JsonToken.END_ARRAY) {
            this.parser.poll();
            this.parser.poll();
            return true;
        }
        return false;
    }

    private boolean isEmptyElement(String fieldName, boolean checkRoot) throws IOException {
        JsonToken jsonToken;
        if ((!checkRoot || fieldName != null && fieldName.equals(this.rootName)) && (jsonToken = this.parser.peek()) == JsonToken.VALUE_NULL) {
            this.parser.poll();
            return true;
        }
        return false;
    }

    JsonXmlEvent readNext() throws XMLStreamException {
        return this.processTokens(false);
    }

    private static class CachedJsonParser {
        private final JsonParser parser;
        private final Queue<JsonToken> tokens = new LinkedList<JsonToken>();

        public CachedJsonParser(JsonParser parser) {
            this.parser = parser;
        }

        public JsonToken nextToken() throws IOException {
            return this.tokens.isEmpty() ? this.parser.nextToken() : this.tokens.poll();
        }

        public JsonToken peekNext() throws IOException {
            JsonToken jsonToken = this.parser.nextToken();
            this.tokens.add(jsonToken);
            return jsonToken;
        }

        public JsonToken peek() throws IOException {
            if (this.tokens.isEmpty()) {
                this.tokens.add(this.parser.nextToken());
            }
            return this.tokens.peek();
        }

        public JsonToken poll() throws IOException {
            return this.tokens.poll();
        }

        public void close() throws IOException {
            this.parser.close();
        }

        public JsonLocation getCurrentLocation() {
            return this.parser.getCurrentLocation();
        }

        public String getText() throws IOException {
            return this.parser.getText();
        }

        public String getCurrentName() throws IOException {
            return this.parser.getCurrentName();
        }

        public boolean hasMoreTokens() throws IOException {
            try {
                return this.peek() != null;
            }
            catch (IOException e) {
                return false;
            }
        }
    }

    private static class ProcessingInfo {
        QName name;
        boolean isArray;
        boolean isFirstElement;

        ProcessingInfo(QName name, boolean isArray, boolean isFirstElement) {
            this.name = name;
            this.isArray = isArray;
            this.isFirstElement = isFirstElement;
        }
    }
}

