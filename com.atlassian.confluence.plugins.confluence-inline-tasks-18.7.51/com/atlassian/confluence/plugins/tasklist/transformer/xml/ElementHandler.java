/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.tasklist.transformer.xml;

import com.atlassian.confluence.plugins.tasklist.transformer.helper.XMLSink;
import com.atlassian.confluence.plugins.tasklist.transformer.xml.ParsingContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

public abstract class ElementHandler {
    private final QName name;

    public ElementHandler(QName name) {
        this.name = name;
    }

    public final boolean consumeIfHandled(ParsingContext context, XMLEventReader xmlReader, XMLSink xmlWriter) throws XMLStreamException {
        if (this.handles(xmlReader)) {
            this.consume(context, xmlReader, xmlWriter);
            return true;
        }
        return false;
    }

    protected boolean handles(XMLEventReader xmlReader) throws XMLStreamException {
        XMLEvent event = xmlReader.peek();
        return event.isStartElement() && this.name.equals(event.asStartElement().getName());
    }

    protected void consume(ParsingContext context, XMLEventReader xmlReader, XMLSink xmlWriter) throws XMLStreamException {
        boolean complete = false;
        xmlWriter.add(xmlReader.nextEvent());
        while (!complete && xmlReader.hasNext()) {
            if (this.consumeChildIfHandled(context, xmlReader, xmlWriter)) continue;
            XMLEvent event = xmlReader.nextEvent();
            if (event.isEndElement() && this.name.equals(event.asEndElement().getName())) {
                complete = true;
            }
            xmlWriter.add(event);
        }
    }

    protected boolean consumeChildIfHandled(ParsingContext context, XMLEventReader xmlReader, XMLSink xmlWriter) throws XMLStreamException {
        return false;
    }
}

