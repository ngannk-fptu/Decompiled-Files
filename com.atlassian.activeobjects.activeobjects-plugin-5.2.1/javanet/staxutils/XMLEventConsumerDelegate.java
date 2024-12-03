/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils;

import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.XMLEventConsumer;

public class XMLEventConsumerDelegate
implements XMLEventConsumer {
    private XMLEventConsumer consumer;
    private XMLEventFactory factory;

    public XMLEventConsumerDelegate(XMLEventConsumer consumer) {
        this.consumer = consumer;
        this.factory = XMLEventFactory.newInstance();
    }

    public XMLEventConsumerDelegate(XMLEventConsumer consumer, XMLEventFactory factory) {
        this.consumer = consumer;
        this.factory = factory == null ? XMLEventFactory.newInstance() : factory;
    }

    public XMLEventConsumer getConsumer() {
        return this.consumer;
    }

    public void setConsumer(XMLEventConsumer consumer) {
        this.consumer = consumer;
    }

    public XMLEventFactory getEventFactory() {
        return this.factory;
    }

    public void setEventFactory(XMLEventFactory factory) {
        this.factory = factory;
    }

    public void add(XMLEvent event) throws XMLStreamException {
        this.consumer.add(event);
    }

    public void addDTD(String dtd) throws XMLStreamException {
        this.add(this.factory.createDTD(dtd));
    }

    public void addCData(String content) throws XMLStreamException {
        this.add(this.factory.createCData(content));
    }

    public void addText(String content) throws XMLStreamException {
        this.add(this.factory.createCharacters(content));
    }

    public void addIgnorableSpace(String content) throws XMLStreamException {
        this.add(this.factory.createIgnorableSpace(content));
    }

    public void addSpace(String content) throws XMLStreamException {
        this.add(this.factory.createSpace(content));
    }

    public void addComment(String comment) throws XMLStreamException {
        this.add(this.factory.createComment(comment));
    }

    public void addStartDocument() throws XMLStreamException {
        this.add(this.factory.createStartDocument());
    }

    public void addStartDocument(String encoding) throws XMLStreamException {
        this.add(this.factory.createStartDocument(encoding));
    }

    public void addStartDocument(String encoding, String version) throws XMLStreamException {
        this.add(this.factory.createStartDocument(encoding, version));
    }

    public void addStartDocument(String encoding, String version, boolean standalone) throws XMLStreamException {
        this.add(this.factory.createStartDocument(encoding, version, standalone));
    }

    public void addEndDocument() throws XMLStreamException {
        this.add(this.factory.createEndDocument());
    }

    public void addStartElement(String localName, NamespaceContext context) throws XMLStreamException {
        this.addStartElement(localName, null, null, context);
    }

    public void addStartElement(String localName, Iterator attributes, Iterator namespaces, NamespaceContext context) throws XMLStreamException {
        this.add(this.factory.createStartElement("", "", localName, attributes, namespaces, context));
    }

    public void addStartElement(String ns, String localName, NamespaceContext context) throws XMLStreamException {
        this.addStartElement(ns, localName, null, null, context);
    }

    public void addStartElement(String ns, String localName, Iterator attributes, Iterator namespaces, NamespaceContext context) throws XMLStreamException {
        this.add(this.factory.createStartElement("", ns, localName, attributes, namespaces, context));
    }

    public void addStartElement(QName name, NamespaceContext context) throws XMLStreamException {
        this.addStartElement(name, null, null, context);
    }

    public void addStartElement(QName name, Iterator attributes, Iterator namespaces, NamespaceContext context) throws XMLStreamException {
        this.add(this.factory.createStartElement(name.getPrefix(), name.getNamespaceURI(), name.getLocalPart(), attributes, namespaces, context));
    }

    public void addEndElement(String localName) throws XMLStreamException {
        this.addEndElement(localName, (Iterator)null);
    }

    public void addEndElement(String localName, Iterator namespaces) throws XMLStreamException {
        this.add(this.factory.createEndElement(null, null, localName, namespaces));
    }

    public void addEndElement(String ns, String localName) throws XMLStreamException {
        this.addEndElement(ns, localName, null);
    }

    public void addEndElement(String ns, String localName, Iterator namespaces) throws XMLStreamException {
        this.add(this.factory.createEndElement(null, ns, localName, namespaces));
    }

    public void addEndElement(QName name) throws XMLStreamException {
        this.addEndElement(name, (Iterator)null);
    }

    public void addEndElement(QName name, Iterator namespaces) throws XMLStreamException {
        this.add(this.factory.createEndElement(name, namespaces));
    }

    public void addTextElement(String name, String text, NamespaceContext context) throws XMLStreamException {
        this.addStartElement(name, context);
        if (text != null) {
            this.addText(text);
        }
        this.addEndElement(name);
    }

    public void addTextElement(QName name, String text, NamespaceContext context) throws XMLStreamException {
        this.addStartElement(name, context);
        if (text != null) {
            this.addText(text);
        }
        this.addEndElement(name);
    }

    public void addTextElement(String name, boolean text, NamespaceContext context) throws XMLStreamException {
        this.addTextElement(name, Boolean.toString(text), context);
    }

    public void addTextElement(QName name, boolean text, NamespaceContext context) throws XMLStreamException {
        this.addTextElement(name, Boolean.toString(text), context);
    }

    public void addTextElement(String name, int text, NamespaceContext context) throws XMLStreamException {
        this.addTextElement(name, Integer.toString(text), context);
    }

    public void addTextElement(QName name, int text, NamespaceContext context) throws XMLStreamException {
        this.addTextElement(name, Integer.toString(text), context);
    }

    public void addTextElement(String name, long text, NamespaceContext context) throws XMLStreamException {
        this.addTextElement(name, Long.toString(text), context);
    }

    public void addTextElement(QName name, long text, NamespaceContext context) throws XMLStreamException {
        this.addTextElement(name, Long.toString(text), context);
    }

    public void addTextElement(String name, float text, NamespaceContext context) throws XMLStreamException {
        this.addTextElement(name, Float.toString(text), context);
    }

    public void addTextElement(QName name, float text, NamespaceContext context) throws XMLStreamException {
        this.addTextElement(name, Float.toString(text), context);
    }

    public void addTextElement(String name, double text, NamespaceContext context) throws XMLStreamException {
        this.addTextElement(name, Double.toString(text), context);
    }

    public void addTextElement(QName name, double text, NamespaceContext context) throws XMLStreamException {
        this.addTextElement(name, Double.toString(text), context);
    }

    public void addTextElement(String name, Number text, NamespaceContext context) throws XMLStreamException {
        if (text != null) {
            this.addTextElement(name, text.toString(), context);
        } else {
            this.addTextElement(name, (String)null, context);
        }
    }

    public void addTextElement(QName name, Number text, NamespaceContext context) throws XMLStreamException {
        if (text != null) {
            this.addTextElement(name, text.toString(), context);
        } else {
            this.addTextElement(name, (String)null, context);
        }
    }
}

