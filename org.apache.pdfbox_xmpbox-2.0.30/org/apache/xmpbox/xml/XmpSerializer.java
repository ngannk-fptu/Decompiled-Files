/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmpbox.xml;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.XMPSchema;
import org.apache.xmpbox.type.AbstractComplexProperty;
import org.apache.xmpbox.type.AbstractField;
import org.apache.xmpbox.type.AbstractSimpleProperty;
import org.apache.xmpbox.type.AbstractStructuredType;
import org.apache.xmpbox.type.ArrayProperty;
import org.apache.xmpbox.type.Attribute;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;

public class XmpSerializer {
    private final TransformerFactory transformerFactory;
    private final DocumentBuilder documentBuilder;
    private boolean parseTypeResourceForLi = true;

    public XmpSerializer() {
        this(TransformerFactory.newInstance(), DocumentBuilderFactory.newInstance());
    }

    public XmpSerializer(TransformerFactory transformerFactory, DocumentBuilderFactory documentBuilderFactory) {
        this.transformerFactory = transformerFactory;
        try {
            this.documentBuilder = documentBuilderFactory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public void serialize(XMPMetadata metadata, OutputStream os, boolean withXpacket) throws TransformerException {
        Document doc = this.documentBuilder.newDocument();
        Element rdf = this.createRdfElement(doc, metadata, withXpacket);
        for (XMPSchema schema : metadata.getAllSchemas()) {
            rdf.appendChild(this.serializeSchema(doc, schema));
        }
        this.save(doc, os, "UTF-8");
    }

    protected Element serializeSchema(Document doc, XMPSchema schema) {
        Element selem = doc.createElementNS("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "rdf:Description");
        selem.setAttributeNS("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "rdf:about", schema.getAboutValue());
        selem.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + schema.getPrefix(), schema.getNamespace());
        this.fillElementWithAttributes(selem, schema);
        List<AbstractField> fields = schema.getAllProperties();
        this.serializeFields(doc, selem, fields, schema.getPrefix(), null, true);
        return selem;
    }

    public void serializeFields(Document doc, Element parent, List<AbstractField> fields, String resourceNS, String prefix, boolean wrapWithProperty) {
        boolean usePrefix = prefix != null && !prefix.isEmpty();
        for (AbstractField field : fields) {
            if (field instanceof AbstractSimpleProperty) {
                AbstractSimpleProperty simple = (AbstractSimpleProperty)field;
                String localPrefix = usePrefix ? prefix : simple.getPrefix();
                Element esimple = doc.createElement(localPrefix + ":" + simple.getPropertyName());
                esimple.setTextContent(simple.getStringValue());
                List<Attribute> attributes = simple.getAllAttributes();
                for (Attribute attribute : attributes) {
                    esimple.setAttributeNS(attribute.getNamespace(), attribute.getName(), attribute.getValue());
                }
                parent.appendChild(esimple);
                continue;
            }
            if (field instanceof ArrayProperty) {
                ArrayProperty array = (ArrayProperty)field;
                Element asimple = doc.createElement(array.getPrefix() + ":" + array.getPropertyName());
                parent.appendChild(asimple);
                this.fillElementWithAttributes(asimple, array);
                Element econtainer = doc.createElement("rdf:" + (Object)((Object)array.getArrayType()));
                asimple.appendChild(econtainer);
                List<AbstractField> innerFields = array.getAllProperties();
                this.serializeFields(doc, econtainer, innerFields, resourceNS, "rdf", false);
                continue;
            }
            if (field instanceof AbstractStructuredType) {
                AbstractStructuredType structured = (AbstractStructuredType)field;
                List<AbstractField> innerFields = structured.getAllProperties();
                Element listParent = parent;
                if (wrapWithProperty) {
                    Element nstructured = doc.createElement(resourceNS + ":" + structured.getPropertyName());
                    parent.appendChild(nstructured);
                    listParent = nstructured;
                }
                Element estructured = doc.createElement("rdf:li");
                listParent.appendChild(estructured);
                if (this.parseTypeResourceForLi) {
                    estructured.setAttribute("rdf:parseType", "Resource");
                    this.serializeFields(doc, estructured, innerFields, resourceNS, null, true);
                    continue;
                }
                Element econtainer = doc.createElement("rdf:Description");
                estructured.appendChild(econtainer);
                this.serializeFields(doc, econtainer, innerFields, resourceNS, null, true);
                continue;
            }
            System.err.println(">> TODO >> " + field.getClass());
        }
    }

    private void fillElementWithAttributes(Element target, AbstractComplexProperty property) {
        List<Attribute> toSerialize = this.normalizeAttributes(property);
        for (Attribute attribute : toSerialize) {
            if ("http://www.w3.org/1999/02/22-rdf-syntax-ns#".equals(attribute.getNamespace())) {
                target.setAttribute("rdf:" + attribute.getName(), attribute.getValue());
                continue;
            }
            target.setAttribute(attribute.getName(), attribute.getValue());
        }
        for (Map.Entry entry : property.getAllNamespacesWithPrefix().entrySet()) {
            target.setAttribute("xmlns:" + (String)entry.getValue(), (String)entry.getKey());
        }
    }

    private List<Attribute> normalizeAttributes(AbstractComplexProperty property) {
        List<Attribute> attributes = property.getAllAttributes();
        ArrayList<Attribute> toSerialize = new ArrayList<Attribute>();
        List<AbstractField> fields = property.getAllProperties();
        for (Attribute attribute : attributes) {
            boolean matchesField = false;
            for (AbstractField field : fields) {
                if (attribute.getName().compareTo(field.getPropertyName()) != 0) continue;
                matchesField = true;
                break;
            }
            if (matchesField) continue;
            toSerialize.add(attribute);
        }
        return toSerialize;
    }

    protected Element createRdfElement(Document doc, XMPMetadata metadata, boolean withXpacket) {
        if (withXpacket) {
            ProcessingInstruction beginXPacket = doc.createProcessingInstruction("xpacket", "begin=\"" + metadata.getXpacketBegin() + "\" id=\"" + metadata.getXpacketId() + "\"");
            doc.appendChild(beginXPacket);
        }
        Element xmpmeta = doc.createElementNS("adobe:ns:meta/", "x:xmpmeta");
        xmpmeta.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:x", "adobe:ns:meta/");
        doc.appendChild(xmpmeta);
        if (withXpacket) {
            ProcessingInstruction endXPacket = doc.createProcessingInstruction("xpacket", "end=\"" + metadata.getEndXPacket() + "\"");
            doc.appendChild(endXPacket);
        }
        Element rdf = doc.createElementNS("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "rdf:RDF");
        xmpmeta.appendChild(rdf);
        return rdf;
    }

    private void save(Node doc, OutputStream outStream, String encoding) throws TransformerException {
        Transformer transformer = this.transformerFactory.newTransformer();
        transformer.setOutputProperty("indent", "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.setOutputProperty("encoding", encoding);
        transformer.setOutputProperty("omit-xml-declaration", "yes");
        StreamResult result = new StreamResult(outStream);
        DOMSource source = new DOMSource(doc);
        transformer.transform(source, result);
    }
}

