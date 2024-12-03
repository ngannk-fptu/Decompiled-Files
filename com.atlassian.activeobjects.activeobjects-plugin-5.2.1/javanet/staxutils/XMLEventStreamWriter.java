/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils;

import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartElement;

public class XMLEventStreamWriter
implements XMLStreamWriter {
    private XMLEventWriter out;
    private XMLEventFactory factory;
    private static final String DEFAULT_ENCODING = "UTF-8";
    private int depth = 0;
    private EndElement[] stack = new EndElement[]{null, null, null, null};

    public XMLEventStreamWriter(XMLEventWriter out) {
        this(out, XMLEventFactory.newInstance());
    }

    public XMLEventStreamWriter(XMLEventWriter out, XMLEventFactory factory) {
        this.out = out;
        this.factory = factory;
    }

    private void write(StartElement start) throws XMLStreamException {
        if (this.stack.length <= this.depth) {
            EndElement[] newStack = new EndElement[this.stack.length * 2];
            System.arraycopy(this.stack, 0, newStack, 0, this.stack.length);
            this.stack = newStack;
        }
        this.out.add(start);
        this.stack[this.depth++] = this.factory.createEndElement(start.getName(), null);
    }

    private void write(Namespace space) throws XMLStreamException {
        ArrayList<Namespace> spaces = new ArrayList<Namespace>();
        EndElement oldEnd = this.stack[this.depth - 1];
        Iterator<Namespace> oldSpaces = oldEnd.getNamespaces();
        if (oldSpaces != null) {
            while (oldSpaces.hasNext()) {
                spaces.add(oldSpaces.next());
            }
        }
        spaces.add(space);
        EndElement end = this.factory.createEndElement(oldEnd.getName(), spaces.iterator());
        this.out.add(space);
        this.stack[this.depth - 1] = end;
    }

    public Object getProperty(String name) throws IllegalArgumentException {
        throw new IllegalArgumentException();
    }

    public void setNamespaceContext(NamespaceContext context) throws XMLStreamException {
        this.out.setNamespaceContext(context);
    }

    public NamespaceContext getNamespaceContext() {
        return this.out.getNamespaceContext();
    }

    public void setDefaultNamespace(String uri) throws XMLStreamException {
        this.out.setDefaultNamespace(uri);
    }

    public String getPrefix(String uri) throws XMLStreamException {
        return this.out.getPrefix(uri);
    }

    public void setPrefix(String prefix, String uri) throws XMLStreamException {
        this.out.setPrefix(prefix, uri);
    }

    public void writeStartDocument() throws XMLStreamException {
        this.out.add(this.factory.createStartDocument(DEFAULT_ENCODING));
    }

    public void writeStartDocument(String version) throws XMLStreamException {
        this.writeStartDocument(DEFAULT_ENCODING, version);
    }

    public void writeStartDocument(String encoding, String version) throws XMLStreamException {
        this.out.add(this.factory.createStartDocument(encoding, version));
    }

    public void writeDTD(String dtd) throws XMLStreamException {
        this.out.add(this.factory.createDTD(dtd));
    }

    public void writeComment(String data) throws XMLStreamException {
        this.out.add(this.factory.createComment(data));
    }

    public void writeProcessingInstruction(String target) throws XMLStreamException {
        this.writeProcessingInstruction(target, "");
    }

    public void writeProcessingInstruction(String target, String data) throws XMLStreamException {
        this.out.add(this.factory.createProcessingInstruction(target, data));
    }

    public void writeEmptyElement(String localName) throws XMLStreamException {
        this.writeStartElement(localName);
        this.writeEndElement();
    }

    public void writeEmptyElement(String namespaceURI, String localName) throws XMLStreamException {
        this.writeStartElement(namespaceURI, localName);
        this.writeEndElement();
    }

    public void writeEmptyElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
        this.writeStartElement(prefix, localName, namespaceURI);
        this.writeEndElement();
    }

    public void writeStartElement(String localName) throws XMLStreamException {
        this.write(this.factory.createStartElement(new QName(localName), null, null));
    }

    public void writeStartElement(String namespaceURI, String localName) throws XMLStreamException {
        this.write(this.factory.createStartElement(new QName(namespaceURI, localName), null, null));
    }

    public void writeStartElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
        this.write(this.factory.createStartElement(new QName(namespaceURI, localName, prefix), null, null));
    }

    public void writeAttribute(String localName, String value) throws XMLStreamException {
        this.out.add(this.factory.createAttribute(localName, value));
    }

    public void writeAttribute(String namespaceURI, String localName, String value) throws XMLStreamException {
        this.out.add(this.factory.createAttribute(new QName(namespaceURI, localName), value));
    }

    public void writeAttribute(String prefix, String namespaceURI, String localName, String value) throws XMLStreamException {
        this.out.add(this.factory.createAttribute(prefix, namespaceURI, localName, value));
    }

    public void writeDefaultNamespace(String namespaceURI) throws XMLStreamException {
        this.write(this.factory.createNamespace(namespaceURI));
    }

    public void writeNamespace(String prefix, String namespaceURI) throws XMLStreamException {
        this.write(this.factory.createNamespace(prefix, namespaceURI));
    }

    public void writeCharacters(String text) throws XMLStreamException {
        this.out.add(this.factory.createCharacters(text));
    }

    public void writeCharacters(char[] text, int start, int len) throws XMLStreamException {
        this.writeCharacters(new String(text, start, len));
    }

    public void writeCData(String data) throws XMLStreamException {
        this.out.add(this.factory.createCData(data));
    }

    public void writeEntityRef(String name) throws XMLStreamException {
        this.out.add(this.factory.createEntityReference(name, null));
    }

    public void writeEndElement() throws XMLStreamException {
        if (this.depth <= 0) {
            this.out.add(this.factory.createEndElement(new QName("unknown"), null));
        } else {
            this.out.add(this.stack[this.depth - 1]);
            --this.depth;
            this.stack[this.depth] = null;
        }
    }

    public void writeEndDocument() throws XMLStreamException {
        try {
            while (this.depth > 0) {
                this.writeEndElement();
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        this.out.add(this.factory.createEndDocument());
        this.depth = 0;
    }

    public void flush() throws XMLStreamException {
        this.out.flush();
    }

    public void close() throws XMLStreamException {
        this.out.close();
    }
}

