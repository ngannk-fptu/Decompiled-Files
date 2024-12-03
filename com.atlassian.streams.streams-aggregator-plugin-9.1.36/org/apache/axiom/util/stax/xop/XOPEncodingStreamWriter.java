/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 */
package org.apache.axiom.util.stax.xop;

import java.io.IOException;
import javax.activation.DataHandler;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;
import org.apache.axiom.ext.stax.datahandler.DataHandlerWriter;
import org.apache.axiom.util.stax.XMLStreamWriterUtils;
import org.apache.axiom.util.stax.xop.ContentIDGenerator;
import org.apache.axiom.util.stax.xop.OptimizationPolicy;
import org.apache.axiom.util.stax.xop.XOPEncodingStreamWrapper;
import org.apache.axiom.util.stax.xop.XOPUtils;

public class XOPEncodingStreamWriter
extends XOPEncodingStreamWrapper
implements XMLStreamWriter,
DataHandlerWriter {
    private final XMLStreamWriter parent;

    public XOPEncodingStreamWriter(XMLStreamWriter parent, ContentIDGenerator contentIDGenerator, OptimizationPolicy optimizationPolicy) {
        super(contentIDGenerator, optimizationPolicy);
        this.parent = parent;
    }

    public Object getProperty(String name) throws IllegalArgumentException {
        if (DataHandlerWriter.PROPERTY.equals(name)) {
            return this;
        }
        return this.parent.getProperty(name);
    }

    private void writeXOPInclude(String contentID) throws XMLStreamException {
        String writerPrefix = this.parent.getPrefix("http://www.w3.org/2004/08/xop/include");
        if (writerPrefix != null) {
            this.parent.writeStartElement("http://www.w3.org/2004/08/xop/include", "Include");
        } else {
            this.parent.writeStartElement("xop", "Include", "http://www.w3.org/2004/08/xop/include");
            this.parent.setPrefix("xop", "http://www.w3.org/2004/08/xop/include");
            this.parent.writeNamespace("xop", "http://www.w3.org/2004/08/xop/include");
        }
        this.parent.writeAttribute("href", XOPUtils.getURLForContentID(contentID));
        this.parent.writeEndElement();
    }

    public void writeDataHandler(DataHandler dataHandler, String contentID, boolean optimize) throws IOException, XMLStreamException {
        if ((contentID = this.processDataHandler(dataHandler, contentID, optimize)) != null) {
            this.writeXOPInclude(contentID);
        } else {
            XMLStreamWriterUtils.writeBase64(this.parent, dataHandler);
        }
    }

    public void writeDataHandler(DataHandlerProvider dataHandlerProvider, String contentID, boolean optimize) throws IOException, XMLStreamException {
        if ((contentID = this.processDataHandler(dataHandlerProvider, contentID, optimize)) != null) {
            this.writeXOPInclude(contentID);
        } else {
            XMLStreamWriterUtils.writeBase64(this.parent, dataHandlerProvider.getDataHandler());
        }
    }

    public void close() throws XMLStreamException {
        this.parent.close();
    }

    public void flush() throws XMLStreamException {
        this.parent.flush();
    }

    public NamespaceContext getNamespaceContext() {
        return this.parent.getNamespaceContext();
    }

    public String getPrefix(String uri) throws XMLStreamException {
        return this.parent.getPrefix(uri);
    }

    public void setDefaultNamespace(String uri) throws XMLStreamException {
        this.parent.setDefaultNamespace(uri);
    }

    public void setNamespaceContext(NamespaceContext context) throws XMLStreamException {
        this.parent.setNamespaceContext(context);
    }

    public void setPrefix(String prefix, String uri) throws XMLStreamException {
        this.parent.setPrefix(prefix, uri);
    }

    public void writeAttribute(String prefix, String namespaceURI, String localName, String value) throws XMLStreamException {
        this.parent.writeAttribute(prefix, namespaceURI, localName, value);
    }

    public void writeAttribute(String namespaceURI, String localName, String value) throws XMLStreamException {
        this.parent.writeAttribute(namespaceURI, localName, value);
    }

    public void writeAttribute(String localName, String value) throws XMLStreamException {
        this.parent.writeAttribute(localName, value);
    }

    public void writeCData(String data) throws XMLStreamException {
        this.parent.writeCData(data);
    }

    public void writeCharacters(char[] text, int start, int len) throws XMLStreamException {
        this.parent.writeCharacters(text, start, len);
    }

    public void writeCharacters(String text) throws XMLStreamException {
        this.parent.writeCharacters(text);
    }

    public void writeComment(String data) throws XMLStreamException {
        this.parent.writeComment(data);
    }

    public void writeDefaultNamespace(String namespaceURI) throws XMLStreamException {
        this.parent.writeDefaultNamespace(namespaceURI);
    }

    public void writeDTD(String dtd) throws XMLStreamException {
        this.parent.writeDTD(dtd);
    }

    public void writeEmptyElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
        this.parent.writeEmptyElement(prefix, localName, namespaceURI);
    }

    public void writeEmptyElement(String namespaceURI, String localName) throws XMLStreamException {
        this.parent.writeEmptyElement(namespaceURI, localName);
    }

    public void writeEmptyElement(String localName) throws XMLStreamException {
        this.parent.writeEmptyElement(localName);
    }

    public void writeEndDocument() throws XMLStreamException {
        this.parent.writeEndDocument();
    }

    public void writeEndElement() throws XMLStreamException {
        this.parent.writeEndElement();
    }

    public void writeEntityRef(String name) throws XMLStreamException {
        this.parent.writeEntityRef(name);
    }

    public void writeNamespace(String prefix, String namespaceURI) throws XMLStreamException {
        this.parent.writeNamespace(prefix, namespaceURI);
    }

    public void writeProcessingInstruction(String target, String data) throws XMLStreamException {
        this.parent.writeProcessingInstruction(target, data);
    }

    public void writeProcessingInstruction(String target) throws XMLStreamException {
        this.parent.writeProcessingInstruction(target);
    }

    public void writeStartDocument() throws XMLStreamException {
        this.parent.writeStartDocument();
    }

    public void writeStartDocument(String encoding, String version) throws XMLStreamException {
        this.parent.writeStartDocument(encoding, version);
    }

    public void writeStartDocument(String version) throws XMLStreamException {
        this.parent.writeStartDocument(version);
    }

    public void writeStartElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
        this.parent.writeStartElement(prefix, localName, namespaceURI);
    }

    public void writeStartElement(String namespaceURI, String localName) throws XMLStreamException {
        this.parent.writeStartElement(namespaceURI, localName);
    }

    public void writeStartElement(String localName) throws XMLStreamException {
        this.parent.writeStartElement(localName);
    }
}

