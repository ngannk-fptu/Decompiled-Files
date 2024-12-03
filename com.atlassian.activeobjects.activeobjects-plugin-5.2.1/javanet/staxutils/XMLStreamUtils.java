/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils;

import java.util.HashMap;
import java.util.Iterator;
import javanet.staxutils.XMLStreamEventWriter;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.XMLEventConsumer;
import javax.xml.transform.Result;
import javax.xml.transform.Source;

public class XMLStreamUtils {
    private static XMLInputFactory inputFactory = XMLInputFactory.newInstance();
    private static XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
    private static final String[] EVENT_NAMES = new String[16];

    public static final String getEventTypeName(int eventType) {
        if (eventType > 0 || eventType < EVENT_NAMES.length) {
            return EVENT_NAMES[eventType];
        }
        return "UNKNOWN";
    }

    public static final String attributeValue(XMLStreamReader reader, String name) {
        return reader.getAttributeValue("", name);
    }

    public static final String attributeValue(XMLStreamReader reader, QName name) {
        return reader.getAttributeValue(name.getNamespaceURI(), name.getLocalPart());
    }

    public static final void skipElement(XMLEventReader reader) throws XMLStreamException {
        XMLStreamUtils.copyElement(reader, null);
    }

    public static final void copyElement(XMLEventReader reader, XMLEventConsumer consumer) throws XMLStreamException {
        if (!reader.hasNext()) {
            return;
        }
        XMLEvent event = reader.peek();
        if (!event.isStartElement()) {
            return;
        }
        int depth = 0;
        do {
            XMLEvent currEvt;
            if ((currEvt = reader.nextEvent()).isStartElement()) {
                ++depth;
            } else if (currEvt.isEndElement()) {
                --depth;
            }
            if (consumer == null) continue;
            consumer.add(currEvt);
        } while (depth > 0 && reader.hasNext());
    }

    public static final void skipElementContent(XMLEventReader reader) throws XMLStreamException {
        XMLStreamUtils.copyElementContent(reader, null);
    }

    public static final void copyElementContent(XMLEventReader reader, XMLEventConsumer consumer) throws XMLStreamException {
        if (!reader.hasNext()) {
            return;
        }
        int depth = 1;
        while (true) {
            XMLEvent currEvt;
            if ((currEvt = reader.peek()).isEndElement()) {
                if (--depth == 0) {
                    break;
                }
            } else if (currEvt.isStartElement()) {
                ++depth;
            }
            currEvt = reader.nextEvent();
            if (consumer == null) continue;
            consumer.add(currEvt);
        }
    }

    public static final void skipElement(XMLStreamReader reader) throws XMLStreamException {
        if (reader.isStartElement()) {
            XMLStreamUtils.skipElementContent(reader);
        }
    }

    public static final void skipElementContent(XMLStreamReader reader) throws XMLStreamException {
        int depth = 0;
        while (depth >= 0) {
            reader.next();
            if (reader.isStartElement()) {
                ++depth;
                continue;
            }
            if (!reader.isEndElement()) continue;
            --depth;
        }
    }

    public static final void requireElement(XMLStreamReader reader, QName name) throws XMLStreamException {
        reader.require(1, name.getNamespaceURI(), name.getLocalPart());
    }

    public static final void copy(XMLEventReader reader, XMLEventConsumer consumer) throws XMLStreamException {
        if (consumer instanceof XMLEventWriter) {
            XMLStreamUtils.copy(reader, (XMLEventWriter)consumer);
        } else {
            while (reader.hasNext()) {
                consumer.add(reader.nextEvent());
            }
        }
    }

    public static final void copy(XMLEventReader reader, XMLEventWriter writer) throws XMLStreamException {
        writer.add(reader);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static final void copy(XMLStreamReader reader, XMLStreamWriter writer) throws XMLStreamException {
        XMLEventReader r = inputFactory.createXMLEventReader(reader);
        XMLStreamEventWriter w = new XMLStreamEventWriter(writer);
        try {
            w.add(r);
        }
        finally {
            w.flush();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static final void copy(XMLStreamReader reader, XMLStreamWriter writer, XMLInputFactory factory) throws XMLStreamException {
        if (factory == null) {
            factory = inputFactory;
        }
        XMLEventReader r = factory.createXMLEventReader(reader);
        XMLStreamEventWriter w = new XMLStreamEventWriter(writer);
        try {
            w.add(r);
        }
        finally {
            w.flush();
        }
    }

    public static final void copy(Source source, XMLStreamWriter writer) throws XMLStreamException {
        XMLStreamReader reader = inputFactory.createXMLStreamReader(source);
        XMLStreamUtils.copy(reader, writer);
    }

    public static final void copy(Source source, XMLEventWriter writer) throws XMLStreamException {
        XMLEventReader reader = inputFactory.createXMLEventReader(source);
        XMLStreamUtils.copy(reader, writer);
    }

    public static final void copy(XMLEventReader reader, Result result) throws XMLStreamException {
        XMLEventWriter writer = outputFactory.createXMLEventWriter(result);
        XMLStreamUtils.copy(reader, writer);
        writer.flush();
    }

    public static final void copy(XMLStreamReader reader, Result result) throws XMLStreamException {
        XMLStreamWriter writer = outputFactory.createXMLStreamWriter(result);
        XMLStreamUtils.copy(reader, writer);
        writer.flush();
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static final void requireStartElement(XMLEventReader reader, QName qname) throws XMLStreamException {
        StartElement start;
        QName name;
        if (!reader.hasNext()) throw new XMLStreamException("Encountered unexpected end of stream; expected element " + qname);
        XMLEvent nextEvent = reader.peek();
        if (!nextEvent.isStartElement()) throw new XMLStreamException("Encountered unexpected event; expected " + qname + " start-tag, but found event " + nextEvent);
        if (qname == null || (name = (start = nextEvent.asStartElement()).getName()).equals(qname)) return;
        throw new XMLStreamException("Encountered unexpected element; expected " + qname + ", but found " + name);
    }

    public static StartElement mergeAttributes(StartElement tag, Iterator attrs, XMLEventFactory factory) {
        HashMap<QName, Attribute> attributes = new HashMap<QName, Attribute>();
        Iterator<Attribute> i = tag.getAttributes();
        while (i.hasNext()) {
            Attribute attr = i.next();
            attributes.put(attr.getName(), attr);
        }
        while (attrs.hasNext()) {
            Attribute attr = (Attribute)attrs.next();
            attributes.put(attr.getName(), attr);
        }
        factory.setLocation(tag.getLocation());
        QName tagName = tag.getName();
        return factory.createStartElement(tagName.getPrefix(), tagName.getNamespaceURI(), tagName.getLocalPart(), attributes.values().iterator(), tag.getNamespaces(), tag.getNamespaceContext());
    }

    public static final String readTextElement(XMLEventReader reader, QName elemName) throws XMLStreamException {
        if (elemName != null) {
            XMLStreamUtils.requireStartElement(reader, elemName);
        }
        String text = reader.getElementText();
        reader.nextEvent();
        return text;
    }

    public static final XMLEvent nextTag(XMLEventReader reader) throws XMLStreamException {
        while (reader.hasNext()) {
            XMLEvent nextEvent = reader.peek();
            if (nextEvent.isStartElement() || nextEvent.isEndElement()) {
                return nextEvent;
            }
            reader.nextEvent();
        }
        return null;
    }

    public static final StartElement nextElement(XMLEventReader reader) throws XMLStreamException {
        return XMLStreamUtils.nextElement(reader, null);
    }

    public static final StartElement nextElement(XMLEventReader reader, QName name) throws XMLStreamException {
        while (reader.hasNext()) {
            XMLEvent nextEvent = reader.peek();
            if (nextEvent.isStartElement()) {
                StartElement start = nextEvent.asStartElement();
                if (name != null && !start.getName().equals(name)) break;
                return start;
            }
            if (nextEvent.isEndElement()) break;
            reader.nextEvent();
        }
        return null;
    }

    static {
        XMLStreamUtils.EVENT_NAMES[0] = "";
        XMLStreamUtils.EVENT_NAMES[10] = "ATTRIBUTE";
        XMLStreamUtils.EVENT_NAMES[12] = "CDATA";
        XMLStreamUtils.EVENT_NAMES[4] = "CHARACTERS";
        XMLStreamUtils.EVENT_NAMES[5] = "COMMENT";
        XMLStreamUtils.EVENT_NAMES[11] = "DTD";
        XMLStreamUtils.EVENT_NAMES[8] = "END_DOCUMENT";
        XMLStreamUtils.EVENT_NAMES[2] = "END_ELEMENT";
        XMLStreamUtils.EVENT_NAMES[15] = "ENTITY_DECLARATION";
        XMLStreamUtils.EVENT_NAMES[9] = "ENTITY_REFERENCE";
        XMLStreamUtils.EVENT_NAMES[13] = "NAMESPACE";
        XMLStreamUtils.EVENT_NAMES[14] = "NOTATION_DECLARATION";
        XMLStreamUtils.EVENT_NAMES[3] = "PROCESSING_INSTRUCTION";
        XMLStreamUtils.EVENT_NAMES[6] = "SPACE";
        XMLStreamUtils.EVENT_NAMES[7] = "START_DOCUMENT";
        XMLStreamUtils.EVENT_NAMES[1] = "START_ELEMENT";
    }
}

