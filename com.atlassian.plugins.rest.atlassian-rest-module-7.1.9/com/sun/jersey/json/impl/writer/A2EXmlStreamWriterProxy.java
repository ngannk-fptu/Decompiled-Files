/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.json.impl.writer;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class A2EXmlStreamWriterProxy
implements XMLStreamWriter {
    XMLStreamWriter underlyingWriter;
    List<String> attr2ElemNames;
    List<XmlAttribute> unwrittenAttrs = null;

    public A2EXmlStreamWriterProxy(XMLStreamWriter writer, Collection<String> attr2ElemNames) {
        this.underlyingWriter = writer;
        this.attr2ElemNames = new LinkedList<String>();
        this.attr2ElemNames.addAll(attr2ElemNames);
    }

    private void flushUnwrittenAttrs() throws XMLStreamException {
        if (this.unwrittenAttrs != null) {
            for (XmlAttribute a : this.unwrittenAttrs) {
                this.underlyingWriter.writeStartElement(a.prefix, a.localName, a.namespaceUri);
                this.underlyingWriter.writeCharacters(a.value);
                this.underlyingWriter.writeEndElement();
            }
            this.unwrittenAttrs = null;
        }
    }

    @Override
    public void writeStartElement(String arg0) throws XMLStreamException {
        this.flushUnwrittenAttrs();
        this.underlyingWriter.writeStartElement(arg0);
    }

    @Override
    public void writeStartElement(String arg0, String arg1) throws XMLStreamException {
        this.flushUnwrittenAttrs();
        this.underlyingWriter.writeStartElement(arg0, arg1);
    }

    @Override
    public void writeStartElement(String arg0, String arg1, String arg2) throws XMLStreamException {
        this.flushUnwrittenAttrs();
        this.underlyingWriter.writeStartElement(arg0, arg1, arg2);
    }

    @Override
    public void writeEmptyElement(String arg0, String arg1) throws XMLStreamException {
        this.flushUnwrittenAttrs();
        this.underlyingWriter.writeEmptyElement(arg0, arg1);
    }

    @Override
    public void writeEmptyElement(String arg0, String arg1, String arg2) throws XMLStreamException {
        this.flushUnwrittenAttrs();
        this.underlyingWriter.writeEmptyElement(arg0, arg1, arg2);
    }

    @Override
    public void writeEmptyElement(String arg0) throws XMLStreamException {
        this.flushUnwrittenAttrs();
        this.underlyingWriter.writeEmptyElement(arg0);
    }

    @Override
    public void writeEndElement() throws XMLStreamException {
        this.flushUnwrittenAttrs();
        this.underlyingWriter.writeEndElement();
    }

    @Override
    public void writeEndDocument() throws XMLStreamException {
        this.underlyingWriter.writeEndDocument();
    }

    @Override
    public void close() throws XMLStreamException {
        this.underlyingWriter.close();
    }

    @Override
    public void flush() throws XMLStreamException {
        this.underlyingWriter.flush();
    }

    @Override
    public void writeAttribute(String localName, String value) throws XMLStreamException {
        this.writeAttribute(null, null, localName, value);
    }

    @Override
    public void writeAttribute(String namespaceURI, String localName, String value) throws XMLStreamException {
        this.writeAttribute(null, namespaceURI, localName, value);
    }

    @Override
    public void writeAttribute(String prefix, String namespaceURI, String localName, String value) throws XMLStreamException {
        if (!this.attr2ElemNames.contains(localName)) {
            this.underlyingWriter.writeAttribute(prefix, namespaceURI, localName, value);
        } else {
            if (this.unwrittenAttrs == null) {
                this.unwrittenAttrs = new LinkedList<XmlAttribute>();
            }
            this.unwrittenAttrs.add(new XmlAttribute(prefix, namespaceURI, localName, value));
        }
    }

    @Override
    public void writeNamespace(String arg0, String arg1) throws XMLStreamException {
        this.underlyingWriter.writeNamespace(arg0, arg1);
    }

    @Override
    public void writeDefaultNamespace(String arg0) throws XMLStreamException {
        this.underlyingWriter.writeDefaultNamespace(arg0);
    }

    @Override
    public void writeComment(String arg0) throws XMLStreamException {
        this.underlyingWriter.writeComment(arg0);
    }

    @Override
    public void writeProcessingInstruction(String arg0) throws XMLStreamException {
        this.underlyingWriter.writeProcessingInstruction(arg0);
    }

    @Override
    public void writeProcessingInstruction(String arg0, String arg1) throws XMLStreamException {
        this.underlyingWriter.writeProcessingInstruction(arg0, arg1);
    }

    @Override
    public void writeCData(String arg0) throws XMLStreamException {
        this.underlyingWriter.writeCData(arg0);
    }

    @Override
    public void writeDTD(String arg0) throws XMLStreamException {
        this.underlyingWriter.writeDTD(arg0);
    }

    @Override
    public void writeEntityRef(String arg0) throws XMLStreamException {
        this.underlyingWriter.writeEntityRef(arg0);
    }

    @Override
    public void writeStartDocument() throws XMLStreamException {
        this.underlyingWriter.writeStartDocument();
    }

    @Override
    public void writeStartDocument(String arg0) throws XMLStreamException {
        this.underlyingWriter.writeStartDocument(arg0);
    }

    @Override
    public void writeStartDocument(String arg0, String arg1) throws XMLStreamException {
        this.underlyingWriter.writeStartDocument(arg0, arg1);
    }

    @Override
    public void writeCharacters(String arg0) throws XMLStreamException {
        this.flushUnwrittenAttrs();
        this.underlyingWriter.writeCharacters(arg0);
    }

    @Override
    public void writeCharacters(char[] arg0, int arg1, int arg2) throws XMLStreamException {
        this.flushUnwrittenAttrs();
        this.underlyingWriter.writeCharacters(arg0, arg1, arg2);
    }

    @Override
    public String getPrefix(String arg0) throws XMLStreamException {
        return this.underlyingWriter.getPrefix(arg0);
    }

    @Override
    public void setPrefix(String arg0, String arg1) throws XMLStreamException {
        this.underlyingWriter.setPrefix(arg0, arg1);
    }

    @Override
    public void setDefaultNamespace(String arg0) throws XMLStreamException {
        this.underlyingWriter.setDefaultNamespace(arg0);
    }

    @Override
    public void setNamespaceContext(NamespaceContext arg0) throws XMLStreamException {
        this.underlyingWriter.setNamespaceContext(arg0);
    }

    @Override
    public NamespaceContext getNamespaceContext() {
        return this.underlyingWriter.getNamespaceContext();
    }

    @Override
    public Object getProperty(String arg0) throws IllegalArgumentException {
        return this.underlyingWriter.getProperty(arg0);
    }

    private class XmlAttribute {
        String prefix;
        String namespaceUri;
        String localName;
        String value;

        XmlAttribute(String prefix, String nsUri, String localName, String value) {
            this.prefix = prefix;
            this.namespaceUri = nsUri;
            this.localName = localName;
            this.value = value;
        }
    }
}

