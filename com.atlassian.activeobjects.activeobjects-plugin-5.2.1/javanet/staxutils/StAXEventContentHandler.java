/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javanet.staxutils.StAXContentHandler;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.util.XMLEventConsumer;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class StAXEventContentHandler
extends StAXContentHandler {
    private XMLEventConsumer consumer;
    private XMLEventFactory eventFactory;
    private List namespaceStack = new ArrayList();

    public StAXEventContentHandler() {
        this.eventFactory = XMLEventFactory.newInstance();
    }

    public StAXEventContentHandler(XMLEventConsumer consumer) {
        this.consumer = consumer;
        this.eventFactory = XMLEventFactory.newInstance();
    }

    public StAXEventContentHandler(XMLEventConsumer consumer, XMLEventFactory factory) {
        this.consumer = consumer;
        this.eventFactory = factory != null ? factory : XMLEventFactory.newInstance();
    }

    public XMLEventConsumer getEventConsumer() {
        return this.consumer;
    }

    public void setEventConsumer(XMLEventConsumer consumer) {
        this.consumer = consumer;
    }

    public XMLEventFactory getEventFactory() {
        return this.eventFactory;
    }

    public void setEventFactory(XMLEventFactory factory) {
        this.eventFactory = factory;
    }

    public void startDocument() throws SAXException {
        super.startDocument();
        this.namespaceStack.clear();
        this.eventFactory.setLocation(this.getCurrentLocation());
        try {
            this.consumer.add(this.eventFactory.createStartDocument());
        }
        catch (XMLStreamException e) {
            throw new SAXException(e);
        }
    }

    public void endDocument() throws SAXException {
        this.eventFactory.setLocation(this.getCurrentLocation());
        try {
            this.consumer.add(this.eventFactory.createEndDocument());
        }
        catch (XMLStreamException e) {
            throw new SAXException(e);
        }
        super.endDocument();
        this.namespaceStack.clear();
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        this.eventFactory.setLocation(this.getCurrentLocation());
        Collection[] events = new Collection[]{null, null};
        this.createStartEvents(attributes, events);
        this.namespaceStack.add(events[0]);
        try {
            String[] qname = new String[]{null, null};
            StAXEventContentHandler.parseQName(qName, qname);
            this.consumer.add(this.eventFactory.createStartElement(qname[0], uri, qname[1], events[1].iterator(), events[0].iterator()));
        }
        catch (XMLStreamException e) {
            throw new SAXException(e);
        }
        finally {
            super.startElement(uri, localName, qName, attributes);
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        this.eventFactory.setLocation(this.getCurrentLocation());
        String[] qname = new String[]{null, null};
        StAXEventContentHandler.parseQName(qName, qname);
        Collection nsList = (Collection)this.namespaceStack.remove(this.namespaceStack.size() - 1);
        Iterator nsIter = nsList.iterator();
        try {
            this.consumer.add(this.eventFactory.createEndElement(qname[0], uri, qname[1], nsIter));
        }
        catch (XMLStreamException e) {
            throw new SAXException(e);
        }
    }

    public void comment(char[] ch, int start, int length) throws SAXException {
        super.comment(ch, start, length);
        this.eventFactory.setLocation(this.getCurrentLocation());
        try {
            this.consumer.add(this.eventFactory.createComment(new String(ch, start, length)));
        }
        catch (XMLStreamException e) {
            throw new SAXException(e);
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        try {
            if (!this.isCDATA) {
                this.eventFactory.setLocation(this.getCurrentLocation());
                this.consumer.add(this.eventFactory.createCharacters(new String(ch, start, length)));
            }
        }
        catch (XMLStreamException e) {
            throw new SAXException(e);
        }
    }

    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        super.ignorableWhitespace(ch, start, length);
        this.characters(ch, start, length);
    }

    public void processingInstruction(String target, String data) throws SAXException {
        super.processingInstruction(target, data);
        try {
            this.consumer.add(this.eventFactory.createProcessingInstruction(target, data));
        }
        catch (XMLStreamException e) {
            throw new SAXException(e);
        }
    }

    public void endCDATA() throws SAXException {
        this.eventFactory.setLocation(this.getCurrentLocation());
        try {
            this.consumer.add(this.eventFactory.createCData(this.CDATABuffer.toString()));
        }
        catch (XMLStreamException e) {
            throw new SAXException(e);
        }
        super.endCDATA();
    }

    protected void createStartEvents(Attributes attributes, Collection[] events) {
        HashMap<String, Namespace> nsMap = null;
        ArrayList<Attribute> attrs = null;
        if (this.namespaces != null) {
            Iterator prefixes = this.namespaces.getDeclaredPrefixes();
            while (prefixes.hasNext()) {
                String prefix = (String)prefixes.next();
                String uri = this.namespaces.getNamespaceURI(prefix);
                Namespace ns = this.createNamespace(prefix, uri);
                if (nsMap == null) {
                    nsMap = new HashMap<String, Namespace>();
                }
                nsMap.put(prefix, ns);
            }
        }
        String[] qname = new String[]{null, null};
        int s = attributes.getLength();
        for (int i = 0; i < s; ++i) {
            StAXEventContentHandler.parseQName(attributes.getQName(i), qname);
            String attrPrefix = qname[0];
            String attrLocal = qname[1];
            String attrQName = attributes.getQName(i);
            String attrValue = attributes.getValue(i);
            String attrURI = attributes.getURI(i);
            if ("xmlns".equals(attrQName) || "xmlns".equals(attrPrefix)) {
                if (nsMap.containsKey(attrPrefix)) continue;
                Namespace ns = this.createNamespace(attrPrefix, attrValue);
                if (nsMap == null) {
                    nsMap = new HashMap();
                }
                nsMap.put(attrPrefix, ns);
                continue;
            }
            Attribute attribute = attrPrefix.length() > 0 ? this.eventFactory.createAttribute(attrPrefix, attrURI, attrLocal, attrValue) : this.eventFactory.createAttribute(attrLocal, attrValue);
            if (attrs == null) {
                attrs = new ArrayList<Attribute>();
            }
            attrs.add(attribute);
        }
        events[0] = nsMap == null ? Collections.EMPTY_LIST : nsMap.values();
        events[1] = attrs == null ? Collections.EMPTY_LIST : attrs;
    }

    protected Namespace createNamespace(String prefix, String uri) {
        if (prefix == null || prefix.length() == 0) {
            return this.eventFactory.createNamespace(uri);
        }
        return this.eventFactory.createNamespace(prefix, uri);
    }
}

