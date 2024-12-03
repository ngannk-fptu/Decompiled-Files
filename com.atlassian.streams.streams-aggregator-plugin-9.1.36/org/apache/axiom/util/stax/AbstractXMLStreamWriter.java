/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.axiom.util.stax;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.util.namespace.ScopedNamespaceContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractXMLStreamWriter
implements XMLStreamWriter {
    private static final Log log = LogFactory.getLog(AbstractXMLStreamWriter.class);
    private final ScopedNamespaceContext namespaceContext = new ScopedNamespaceContext();
    private boolean inEmptyElement;

    public final NamespaceContext getNamespaceContext() {
        return this.namespaceContext;
    }

    public final void setNamespaceContext(NamespaceContext context) throws XMLStreamException {
        throw new UnsupportedOperationException();
    }

    public final String getPrefix(String uri) throws XMLStreamException {
        return this.namespaceContext.getPrefix(uri);
    }

    private void internalSetPrefix(String prefix, String uri) {
        if (this.inEmptyElement) {
            log.warn((Object)"The behavior of XMLStreamWriter#setPrefix and XMLStreamWriter#setDefaultNamespace is undefined when invoked in the context of an empty element");
        }
        this.namespaceContext.setPrefix(prefix, uri);
    }

    public final void setDefaultNamespace(String uri) throws XMLStreamException {
        this.internalSetPrefix("", uri);
    }

    public final void setPrefix(String prefix, String uri) throws XMLStreamException {
        this.internalSetPrefix(prefix, uri);
    }

    public final void writeStartDocument() throws XMLStreamException {
        this.doWriteStartDocument();
    }

    protected abstract void doWriteStartDocument() throws XMLStreamException;

    public final void writeStartDocument(String encoding, String version) throws XMLStreamException {
        this.doWriteStartDocument(encoding, version);
    }

    protected abstract void doWriteStartDocument(String var1, String var2) throws XMLStreamException;

    public final void writeStartDocument(String version) throws XMLStreamException {
        this.doWriteStartDocument(version);
    }

    protected abstract void doWriteStartDocument(String var1) throws XMLStreamException;

    public final void writeDTD(String dtd) throws XMLStreamException {
        this.doWriteDTD(dtd);
    }

    protected abstract void doWriteDTD(String var1) throws XMLStreamException;

    public final void writeEndDocument() throws XMLStreamException {
        this.doWriteEndDocument();
    }

    protected abstract void doWriteEndDocument() throws XMLStreamException;

    private String internalGetPrefix(String namespaceURI) throws XMLStreamException {
        String prefix = this.namespaceContext.getPrefix(namespaceURI);
        if (prefix == null) {
            throw new XMLStreamException("Unbound namespace URI '" + namespaceURI + "'");
        }
        return prefix;
    }

    public final void writeStartElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
        this.doWriteStartElement(prefix, localName, namespaceURI);
        this.namespaceContext.startScope();
        this.inEmptyElement = false;
    }

    public final void writeStartElement(String namespaceURI, String localName) throws XMLStreamException {
        this.doWriteStartElement(this.internalGetPrefix(namespaceURI), localName, namespaceURI);
        this.namespaceContext.startScope();
        this.inEmptyElement = false;
    }

    protected abstract void doWriteStartElement(String var1, String var2, String var3) throws XMLStreamException;

    public final void writeStartElement(String localName) throws XMLStreamException {
        this.doWriteStartElement(localName);
        this.namespaceContext.startScope();
        this.inEmptyElement = false;
    }

    protected abstract void doWriteStartElement(String var1) throws XMLStreamException;

    public final void writeEndElement() throws XMLStreamException {
        this.doWriteEndElement();
        this.namespaceContext.endScope();
        this.inEmptyElement = false;
    }

    protected abstract void doWriteEndElement() throws XMLStreamException;

    public final void writeEmptyElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
        this.doWriteEmptyElement(prefix, localName, namespaceURI);
        this.inEmptyElement = true;
    }

    public final void writeEmptyElement(String namespaceURI, String localName) throws XMLStreamException {
        this.doWriteEmptyElement(this.internalGetPrefix(namespaceURI), localName, namespaceURI);
        this.inEmptyElement = true;
    }

    protected abstract void doWriteEmptyElement(String var1, String var2, String var3) throws XMLStreamException;

    public final void writeEmptyElement(String localName) throws XMLStreamException {
        this.doWriteEmptyElement(localName);
        this.inEmptyElement = true;
    }

    protected abstract void doWriteEmptyElement(String var1) throws XMLStreamException;

    public final void writeAttribute(String prefix, String namespaceURI, String localName, String value) throws XMLStreamException {
        this.doWriteAttribute(prefix, namespaceURI, localName, value);
    }

    public final void writeAttribute(String namespaceURI, String localName, String value) throws XMLStreamException {
        this.doWriteAttribute(this.internalGetPrefix(namespaceURI), namespaceURI, localName, value);
    }

    protected abstract void doWriteAttribute(String var1, String var2, String var3, String var4) throws XMLStreamException;

    public final void writeAttribute(String localName, String value) throws XMLStreamException {
        this.doWriteAttribute(localName, value);
    }

    protected abstract void doWriteAttribute(String var1, String var2) throws XMLStreamException;

    public final void writeNamespace(String prefix, String namespaceURI) throws XMLStreamException {
        this.doWriteNamespace(prefix, namespaceURI);
    }

    protected abstract void doWriteNamespace(String var1, String var2) throws XMLStreamException;

    public final void writeDefaultNamespace(String namespaceURI) throws XMLStreamException {
        this.doWriteDefaultNamespace(namespaceURI);
    }

    protected abstract void doWriteDefaultNamespace(String var1) throws XMLStreamException;

    public final void writeCharacters(char[] text, int start, int len) throws XMLStreamException {
        this.doWriteCharacters(text, start, len);
        this.inEmptyElement = false;
    }

    protected abstract void doWriteCharacters(char[] var1, int var2, int var3) throws XMLStreamException;

    public final void writeCharacters(String text) throws XMLStreamException {
        this.doWriteCharacters(text);
        this.inEmptyElement = false;
    }

    protected abstract void doWriteCharacters(String var1) throws XMLStreamException;

    public final void writeCData(String data) throws XMLStreamException {
        this.doWriteCData(data);
        this.inEmptyElement = false;
    }

    protected abstract void doWriteCData(String var1) throws XMLStreamException;

    public final void writeComment(String data) throws XMLStreamException {
        this.doWriteComment(data);
        this.inEmptyElement = false;
    }

    protected abstract void doWriteComment(String var1) throws XMLStreamException;

    public final void writeEntityRef(String name) throws XMLStreamException {
        this.doWriteEntityRef(name);
        this.inEmptyElement = false;
    }

    protected abstract void doWriteEntityRef(String var1) throws XMLStreamException;

    public final void writeProcessingInstruction(String target, String data) throws XMLStreamException {
        this.doWriteProcessingInstruction(target, data);
        this.inEmptyElement = false;
    }

    protected abstract void doWriteProcessingInstruction(String var1, String var2) throws XMLStreamException;

    public final void writeProcessingInstruction(String target) throws XMLStreamException {
        this.doWriteProcessingInstruction(target);
        this.inEmptyElement = false;
    }

    protected abstract void doWriteProcessingInstruction(String var1) throws XMLStreamException;
}

