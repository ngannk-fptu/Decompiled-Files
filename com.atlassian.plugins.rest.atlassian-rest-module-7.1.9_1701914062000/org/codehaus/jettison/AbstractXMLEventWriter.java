/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jettison;

import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class AbstractXMLEventWriter
implements XMLEventWriter {
    private XMLStreamWriter streamWriter;

    public AbstractXMLEventWriter(XMLStreamWriter streamWriter) {
        this.streamWriter = streamWriter;
    }

    @Override
    public void add(XMLEvent event) throws XMLStreamException {
        if (event.isStartDocument()) {
            this.streamWriter.writeStartDocument();
        } else if (event.isStartElement()) {
            StartElement element = event.asStartElement();
            QName elQName = element.getName();
            if (elQName.getPrefix().length() > 0 && elQName.getNamespaceURI().length() > 0) {
                this.streamWriter.writeStartElement(elQName.getPrefix(), elQName.getLocalPart(), elQName.getNamespaceURI());
            } else if (elQName.getNamespaceURI().length() > 0) {
                this.streamWriter.writeStartElement(elQName.getNamespaceURI(), elQName.getLocalPart());
            } else {
                this.streamWriter.writeStartElement(elQName.getLocalPart());
            }
            Iterator<Namespace> namespaces = element.getNamespaces();
            while (namespaces.hasNext()) {
                Namespace ns = namespaces.next();
                String prefix = ns.getPrefix();
                String nsURI = ns.getNamespaceURI();
                this.streamWriter.writeNamespace(prefix, nsURI);
            }
            Iterator<Attribute> attris = element.getAttributes();
            while (attris.hasNext()) {
                Attribute attr = attris.next();
                QName atQName = attr.getName();
                String value = attr.getValue();
                if (atQName.getPrefix().length() > 0 && atQName.getNamespaceURI().length() > 0) {
                    this.streamWriter.writeAttribute(atQName.getPrefix(), atQName.getNamespaceURI(), atQName.getLocalPart(), value);
                    continue;
                }
                if (atQName.getNamespaceURI().length() > 0) {
                    this.streamWriter.writeAttribute(atQName.getNamespaceURI(), atQName.getLocalPart(), value);
                    continue;
                }
                this.streamWriter.writeAttribute(atQName.getLocalPart(), value);
            }
        } else if (event.isCharacters()) {
            Characters chars = event.asCharacters();
            this.streamWriter.writeCharacters(chars.getData());
        } else if (event.isEndElement()) {
            this.streamWriter.writeEndElement();
        } else if (event.isEndDocument()) {
            this.streamWriter.writeEndDocument();
        } else {
            throw new XMLStreamException("Unsupported event type: " + event);
        }
    }

    @Override
    public void add(XMLEventReader eventReader) throws XMLStreamException {
        while (eventReader.hasNext()) {
            XMLEvent event = eventReader.nextEvent();
            this.add(event);
        }
        this.close();
    }

    @Override
    public void close() throws XMLStreamException {
        this.streamWriter.close();
    }

    @Override
    public void flush() throws XMLStreamException {
        this.streamWriter.flush();
    }

    @Override
    public NamespaceContext getNamespaceContext() {
        return this.streamWriter.getNamespaceContext();
    }

    @Override
    public String getPrefix(String prefix) throws XMLStreamException {
        return this.streamWriter.getPrefix(prefix);
    }

    @Override
    public void setDefaultNamespace(String namespace) throws XMLStreamException {
        this.streamWriter.setDefaultNamespace(namespace);
    }

    @Override
    public void setNamespaceContext(NamespaceContext nsContext) throws XMLStreamException {
        this.streamWriter.setNamespaceContext(nsContext);
    }

    @Override
    public void setPrefix(String prefix, String uri) throws XMLStreamException {
        this.streamWriter.setPrefix(prefix, uri);
    }
}

