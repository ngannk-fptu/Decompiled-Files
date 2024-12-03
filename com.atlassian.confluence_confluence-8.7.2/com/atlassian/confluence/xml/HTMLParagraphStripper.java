/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.xml;

import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

public class HTMLParagraphStripper {
    private final XmlEventReaderFactory xmlEventReaderFactory;
    private final XMLOutputFactory xmlOutputFactory;

    public HTMLParagraphStripper(XMLOutputFactory xmlOutputFactory, XmlEventReaderFactory xmlEventReaderFactory) {
        this.xmlOutputFactory = xmlOutputFactory;
        this.xmlEventReaderFactory = xmlEventReaderFactory;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String stripFirstParagraph(String xml) throws XMLStreamException {
        StringWriter outputWriter = new StringWriter();
        XMLEventWriter eventWriter = this.xmlOutputFactory.createXMLEventWriter(outputWriter);
        XMLEventReader eventReader = this.xmlEventReaderFactory.createStorageXmlEventReader(new StringReader(xml));
        if (!eventReader.hasNext()) {
            return xml;
        }
        if (!eventReader.peek().isStartElement() || !"p".equals(eventReader.peek().asStartElement().getName().getLocalPart())) {
            return xml;
        }
        try (XMLEventReader bodyEventReader = this.xmlEventReaderFactory.createXmlFragmentBodyEventReader(eventReader);){
            eventWriter.add(bodyEventReader);
        }
        eventWriter.add(eventReader);
        eventWriter.flush();
        return outputWriter.toString();
    }

    public String stripIfSoloParagraph(String xml) throws XMLStreamException {
        XMLEventReader eventReader = this.xmlEventReaderFactory.createStorageXmlEventReader(new StringReader(xml));
        int paragraphCount = 0;
        while (eventReader.hasNext()) {
            XMLEvent event = eventReader.peek();
            if (event.isStartElement() && "p".equals(event.asStartElement().getName().getLocalPart())) {
                XMLEventReader paragraphReader = this.xmlEventReaderFactory.createXmlFragmentEventReader(eventReader);
                while (paragraphReader.hasNext()) {
                    paragraphReader.nextEvent();
                }
                if (++paragraphCount <= 1) continue;
                break;
            }
            eventReader.nextEvent();
        }
        if (paragraphCount == 1) {
            return this.stripFirstParagraph(xml);
        }
        return xml;
    }
}

