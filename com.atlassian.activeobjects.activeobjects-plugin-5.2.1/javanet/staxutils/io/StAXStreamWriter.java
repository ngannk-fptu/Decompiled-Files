/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import javanet.staxutils.helpers.ElementContext;
import javanet.staxutils.io.XMLWriterUtils;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class StAXStreamWriter
implements XMLStreamWriter {
    private Writer writer;
    private boolean closed;
    private NamespaceContext rootContext;
    private ElementContext elementContext;

    public StAXStreamWriter(OutputStream stream) {
        this(new OutputStreamWriter(stream));
    }

    public StAXStreamWriter(OutputStream stream, String encoding) throws UnsupportedEncodingException {
        this(new OutputStreamWriter(stream, encoding));
    }

    public StAXStreamWriter(Writer writer) {
        this.writer = writer;
    }

    public StAXStreamWriter(Writer writer, NamespaceContext rootContext) {
        this.writer = writer;
        this.rootContext = rootContext;
    }

    public synchronized void close() throws XMLStreamException {
        if (!this.closed) {
            this.flush();
            this.closed = true;
            this.writer = null;
        }
    }

    public synchronized void flush() throws XMLStreamException {
        this.closeElementContext();
        try {
            this.writer.flush();
        }
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    public String getPrefix(String uri) throws XMLStreamException {
        return this.getNamespaceContext().getPrefix(uri);
    }

    public Object getProperty(String name) throws IllegalArgumentException {
        throw new IllegalArgumentException(name + " property not supported");
    }

    public void writeStartDocument() throws XMLStreamException {
        try {
            XMLWriterUtils.writeStartDocument(this.writer);
        }
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    public void writeStartDocument(String version) throws XMLStreamException {
        try {
            XMLWriterUtils.writeStartDocument(version, this.writer);
        }
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    public synchronized void writeStartDocument(String encoding, String version) throws XMLStreamException {
        try {
            XMLWriterUtils.writeStartDocument(version, encoding, this.writer);
        }
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    public synchronized void writeEndDocument() throws XMLStreamException {
        this.closeElementContext();
        while (this.elementContext != null) {
            this.writeEndElement();
        }
    }

    public synchronized void writeCData(String data) throws XMLStreamException {
        if (data == null) {
            throw new IllegalArgumentException("CDATA argument was null");
        }
        this.closeElementContext();
        try {
            XMLWriterUtils.writeCData(data, this.writer);
        }
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    public synchronized void writeCharacters(char[] text, int start, int len) throws XMLStreamException {
        if (text == null) {
            throw new IllegalArgumentException("Character text argument was null");
        }
        this.closeElementContext();
        try {
            XMLWriterUtils.writeCharacters(text, start, len, this.writer);
        }
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    public synchronized void writeCharacters(String text) throws XMLStreamException {
        if (text == null) {
            throw new IllegalArgumentException("Character text argument was null");
        }
        this.closeElementContext();
        try {
            XMLWriterUtils.writeCharacters(text, this.writer);
        }
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    public synchronized void writeComment(String data) throws XMLStreamException {
        if (data == null) {
            throw new IllegalArgumentException("Comment data argument was null");
        }
        this.closeElementContext();
        try {
            XMLWriterUtils.writeComment(data, this.writer);
        }
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    public synchronized void writeDTD(String dtd) throws XMLStreamException {
        if (dtd == null) {
            throw new IllegalArgumentException("dtd argument was null");
        }
        try {
            XMLWriterUtils.writeDTD(dtd, this.writer);
        }
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    public synchronized void writeEntityRef(String name) throws XMLStreamException {
        this.closeElementContext();
        try {
            XMLWriterUtils.writeEntityReference(name, this.writer);
        }
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    public synchronized void writeProcessingInstruction(String target, String data) throws XMLStreamException {
        this.closeElementContext();
        try {
            XMLWriterUtils.writeProcessingInstruction(target, data, this.writer);
        }
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    public void writeProcessingInstruction(String target) throws XMLStreamException {
        this.writeProcessingInstruction(target, null);
    }

    public NamespaceContext getNamespaceContext() {
        return this.elementContext;
    }

    public void setNamespaceContext(NamespaceContext context) throws XMLStreamException {
        if (this.rootContext != null || this.elementContext != null) {
            throw new IllegalStateException("NamespaceContext has already been set or document is already in progress");
        }
        this.rootContext = context;
    }

    public synchronized void setDefaultNamespace(String uri) throws XMLStreamException {
        this.elementContext.putNamespace("", uri);
    }

    public synchronized void setPrefix(String prefix, String uri) throws XMLStreamException {
        this.elementContext.putNamespace(prefix, uri);
    }

    public synchronized void writeStartElement(String prefix, String localName, String namespaceURI, boolean isEmpty) throws XMLStreamException {
        if (prefix == null) {
            throw new IllegalArgumentException("prefix may not be null @ [" + this.getCurrentPath() + "]");
        }
        if (localName == null) {
            throw new IllegalArgumentException("localName may not be null @ [" + this.getCurrentPath() + "]");
        }
        if (namespaceURI == null) {
            throw new IllegalArgumentException("namespaceURI may not be null @ [" + this.getCurrentPath() + "]");
        }
        if (this.elementContext != null) {
            this.closeElementContext();
            if (this.elementContext == null) {
                throw new XMLStreamException("Writing start tag after close of root element");
            }
        }
        QName name = new QName(namespaceURI, localName, prefix);
        this.elementContext = new ElementContext(name, this.elementContext, isEmpty);
    }

    public void writeStartElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
        this.writeStartElement(prefix, localName, namespaceURI, false);
    }

    public void writeStartElement(String namespaceURI, String localName) throws XMLStreamException {
        this.writeStartElement("", localName, namespaceURI, false);
    }

    public void writeStartElement(String localName) throws XMLStreamException {
        this.writeStartElement("", localName, "", false);
    }

    public void writeEmptyElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
        this.writeStartElement(prefix, localName, namespaceURI, true);
    }

    public void writeEmptyElement(String namespaceURI, String localName) throws XMLStreamException {
        this.writeStartElement("", localName, namespaceURI, true);
    }

    public void writeEmptyElement(String localName) throws XMLStreamException {
        this.writeStartElement("", localName, "", true);
    }

    public synchronized void writeAttribute(QName name, String value) throws XMLStreamException {
        if (this.elementContext == null || this.elementContext.isReadOnly()) {
            throw new XMLStreamException(this.getCurrentPath() + ": attributes must be written directly following a start element.");
        }
        this.elementContext.putAttribute(name, value);
    }

    public void writeAttribute(String prefix, String namespaceURI, String localName, String value) throws XMLStreamException {
        if (prefix == null) {
            throw new IllegalArgumentException("attribute prefix may not be null @ [" + this.getCurrentPath() + "]");
        }
        if (localName == null) {
            throw new IllegalArgumentException("attribute localName may not be null @ [" + this.getCurrentPath() + "]");
        }
        if (namespaceURI == null) {
            throw new IllegalArgumentException("attribute namespaceURI may not be null @ [" + this.getCurrentPath() + "]");
        }
        this.writeAttribute(new QName(namespaceURI, localName, prefix), value);
    }

    public void writeAttribute(String namespaceURI, String localName, String value) throws XMLStreamException {
        this.writeAttribute("", namespaceURI, localName, value);
    }

    public void writeAttribute(String localName, String value) throws XMLStreamException {
        this.writeAttribute("", "", localName, value);
    }

    public void writeDefaultNamespace(String namespaceURI) throws XMLStreamException {
        this.writeNamespace("", namespaceURI);
    }

    public synchronized void writeNamespace(String prefix, String namespaceURI) throws XMLStreamException {
        if (prefix == null) {
            throw new IllegalArgumentException("Namespace prefix may not be null @ [" + this.getCurrentPath() + "]");
        }
        if (namespaceURI == null) {
            throw new IllegalArgumentException("Namespace URI may not be null @ [" + this.getCurrentPath() + "]");
        }
        if (this.elementContext == null || this.elementContext.isReadOnly()) {
            throw new XMLStreamException(this.getCurrentPath() + ": Namespaces must be written directly following a start tag");
        }
        this.elementContext.putNamespace(prefix, namespaceURI);
    }

    public synchronized void writeEndElement() throws XMLStreamException {
        this.closeElementContext();
        if (this.elementContext != null) {
            QName name = this.elementContext.getName();
            try {
                XMLWriterUtils.writeEndElement(name, this.writer);
            }
            catch (IOException e) {
                throw new XMLStreamException(this.getCurrentPath() + ": Error writing end element to stream", e);
            }
        } else {
            throw new XMLStreamException("Unmatched END_ELEMENT");
        }
        this.elementContext = this.elementContext.getParentContext();
    }

    public synchronized String getCurrentPath() {
        if (this.elementContext == null) {
            return "/";
        }
        return this.elementContext.getPath();
    }

    protected void closeElementContext() throws XMLStreamException {
        if (this.elementContext != null && !this.elementContext.isReadOnly()) {
            this.elementContext.setReadOnly();
            try {
                int i;
                this.writer.write(60);
                XMLWriterUtils.writeQName(this.elementContext.getName(), this.writer);
                int s = this.elementContext.attributeCount();
                for (i = 0; i < s; ++i) {
                    QName name = this.elementContext.getAttributeName(i);
                    String value = this.elementContext.getAttribute(i);
                    this.writer.write(32);
                    XMLWriterUtils.writeAttribute(name, value, this.writer);
                }
                s = this.elementContext.namespaceCount();
                for (i = 0; i < s; ++i) {
                    String prefix = this.elementContext.getNamespacePrefix(i);
                    String uri = this.elementContext.getNamespaceURI(i);
                    this.writer.write(32);
                    XMLWriterUtils.writeNamespace(prefix, uri, this.writer);
                }
                if (this.elementContext.isEmpty()) {
                    this.writer.write("/>");
                    this.elementContext = this.elementContext.getParentContext();
                } else {
                    this.writer.write(62);
                }
            }
            catch (IOException e) {
                throw new XMLStreamException(this.getCurrentPath() + ": error writing start tag to stream", e);
            }
        }
    }
}

