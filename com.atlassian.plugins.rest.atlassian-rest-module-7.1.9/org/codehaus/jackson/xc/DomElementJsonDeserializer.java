/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.xc;

import java.io.IOException;
import java.util.Iterator;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.deser.std.StdDeserializer;
import org.codehaus.jackson.node.ArrayNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class DomElementJsonDeserializer
extends StdDeserializer<Element> {
    private final DocumentBuilder builder;

    public DomElementJsonDeserializer() {
        super(Element.class);
        try {
            DocumentBuilderFactory bf = DocumentBuilderFactory.newInstance();
            bf.setNamespaceAware(true);
            bf.setExpandEntityReferences(false);
            bf.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
            this.builder = bf.newDocumentBuilder();
        }
        catch (ParserConfigurationException e) {
            throw new RuntimeException("Problem creating DocumentBuilder: " + e.toString());
        }
    }

    public DomElementJsonDeserializer(DocumentBuilder builder) {
        super(Element.class);
        this.builder = builder;
    }

    @Override
    public Element deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        Document document = this.builder.newDocument();
        return this.fromNode(document, jp.readValueAsTree());
    }

    protected Element fromNode(Document document, JsonNode jsonNode) throws IOException {
        JsonNode childsNode;
        String name;
        String ns = jsonNode.get("namespace") != null ? jsonNode.get("namespace").asText() : null;
        String string = name = jsonNode.get("name") != null ? jsonNode.get("name").asText() : null;
        if (name == null) {
            throw new JsonMappingException("No name for DOM element was provided in the JSON object.");
        }
        Element element = document.createElementNS(ns, name);
        JsonNode attributesNode = jsonNode.get("attributes");
        if (attributesNode != null && attributesNode instanceof ArrayNode) {
            Iterator<JsonNode> atts = attributesNode.getElements();
            while (atts.hasNext()) {
                String value;
                JsonNode node = atts.next();
                ns = node.get("namespace") != null ? node.get("namespace").asText() : null;
                name = node.get("name") != null ? node.get("name").asText() : null;
                String string2 = value = node.get("$") != null ? node.get("$").asText() : null;
                if (name == null) continue;
                element.setAttributeNS(ns, name, value);
            }
        }
        if ((childsNode = jsonNode.get("children")) != null && childsNode instanceof ArrayNode) {
            Iterator<JsonNode> els = childsNode.getElements();
            while (els.hasNext()) {
                String value;
                JsonNode node = els.next();
                name = node.get("name") != null ? node.get("name").asText() : null;
                String string3 = value = node.get("$") != null ? node.get("$").asText() : null;
                if (value != null) {
                    element.appendChild(document.createTextNode(value));
                    continue;
                }
                if (name == null) continue;
                element.appendChild(this.fromNode(document, node));
            }
        }
        return element;
    }
}

