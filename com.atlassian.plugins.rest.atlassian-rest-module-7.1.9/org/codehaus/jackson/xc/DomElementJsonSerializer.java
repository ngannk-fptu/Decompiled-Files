/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.xc;

import java.io.IOException;
import java.lang.reflect.Type;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;
import org.codehaus.jackson.node.ObjectNode;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class DomElementJsonSerializer
extends SerializerBase<Element> {
    public DomElementJsonSerializer() {
        super(Element.class);
    }

    @Override
    public void serialize(Element value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
        NodeList children;
        NamedNodeMap attributes;
        jgen.writeStartObject();
        jgen.writeStringField("name", value.getTagName());
        if (value.getNamespaceURI() != null) {
            jgen.writeStringField("namespace", value.getNamespaceURI());
        }
        if ((attributes = value.getAttributes()) != null && attributes.getLength() > 0) {
            jgen.writeArrayFieldStart("attributes");
            for (int i = 0; i < attributes.getLength(); ++i) {
                Attr attribute = (Attr)attributes.item(i);
                jgen.writeStartObject();
                jgen.writeStringField("$", attribute.getValue());
                jgen.writeStringField("name", attribute.getName());
                String ns = attribute.getNamespaceURI();
                if (ns != null) {
                    jgen.writeStringField("namespace", ns);
                }
                jgen.writeEndObject();
            }
            jgen.writeEndArray();
        }
        if ((children = value.getChildNodes()) != null && children.getLength() > 0) {
            jgen.writeArrayFieldStart("children");
            block5: for (int i = 0; i < children.getLength(); ++i) {
                Node child = children.item(i);
                switch (child.getNodeType()) {
                    case 3: 
                    case 4: {
                        jgen.writeStartObject();
                        jgen.writeStringField("$", child.getNodeValue());
                        jgen.writeEndObject();
                        continue block5;
                    }
                    case 1: {
                        this.serialize((Element)child, jgen, provider);
                    }
                }
            }
            jgen.writeEndArray();
        }
        jgen.writeEndObject();
    }

    @Override
    public JsonNode getSchema(SerializerProvider provider, Type typeHint) throws JsonMappingException {
        ObjectNode o = this.createSchemaNode("object", true);
        o.put("name", this.createSchemaNode("string"));
        o.put("namespace", this.createSchemaNode("string", true));
        o.put("attributes", this.createSchemaNode("array", true));
        o.put("children", this.createSchemaNode("array", true));
        return o;
    }
}

