/*
 * Decompiled with CFR 0.152.
 */
package groovy.xml;

import groovy.util.BuilderSupport;
import groovy.xml.QName;
import java.util.Map;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class SAXBuilder
extends BuilderSupport {
    private ContentHandler handler;
    private Attributes emptyAttributes = new AttributesImpl();

    public SAXBuilder(ContentHandler handler) {
        this.handler = handler;
    }

    @Override
    protected void setParent(Object parent, Object child) {
    }

    @Override
    protected Object createNode(Object name) {
        this.doStartElement(name, this.emptyAttributes);
        return name;
    }

    @Override
    protected Object createNode(Object name, Object value) {
        this.doStartElement(name, this.emptyAttributes);
        this.doText(value);
        return name;
    }

    private void doText(Object value) {
        try {
            char[] text = value.toString().toCharArray();
            this.handler.characters(text, 0, text.length);
        }
        catch (SAXException e) {
            this.handleException(e);
        }
    }

    @Override
    protected Object createNode(Object name, Map attributeMap, Object text) {
        AttributesImpl attributes = new AttributesImpl();
        for (Map.Entry entry : attributeMap.entrySet()) {
            String valueText;
            Object key = entry.getKey();
            Object value = entry.getValue();
            String uri = "";
            String localName = null;
            String qualifiedName = "";
            String string = valueText = value != null ? value.toString() : "";
            if (key instanceof QName) {
                QName qname = (QName)key;
                uri = qname.getNamespaceURI();
                localName = qname.getLocalPart();
                qualifiedName = qname.getQualifiedName();
            } else {
                qualifiedName = localName = key.toString();
            }
            attributes.addAttribute(uri, localName, qualifiedName, "CDATA", valueText);
        }
        this.doStartElement(name, attributes);
        if (text != null) {
            this.doText(text);
        }
        return name;
    }

    protected void doStartElement(Object name, Attributes attributes) {
        String uri = "";
        String localName = null;
        String qualifiedName = "";
        if (name instanceof QName) {
            QName qname = (QName)name;
            uri = qname.getNamespaceURI();
            localName = qname.getLocalPart();
            qualifiedName = qname.getQualifiedName();
        } else {
            qualifiedName = localName = name.toString();
        }
        try {
            this.handler.startElement(uri, localName, qualifiedName, attributes);
        }
        catch (SAXException e) {
            this.handleException(e);
        }
    }

    @Override
    protected void nodeCompleted(Object parent, Object name) {
        String uri = "";
        String localName = null;
        String qualifiedName = "";
        if (name instanceof QName) {
            QName qname = (QName)name;
            uri = qname.getNamespaceURI();
            localName = qname.getLocalPart();
            qualifiedName = qname.getQualifiedName();
        } else {
            qualifiedName = localName = name.toString();
        }
        try {
            this.handler.endElement(uri, localName, qualifiedName);
        }
        catch (SAXException e) {
            this.handleException(e);
        }
    }

    protected void handleException(SAXException e) {
        throw new RuntimeException(e);
    }

    @Override
    protected Object createNode(Object name, Map attributes) {
        return this.createNode(name, attributes, null);
    }
}

