/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml;

import com.atlassian.confluence.content.render.xhtml.ForwardingXmlEventReader;
import java.io.StringWriter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

public class TextExtractingXmlFragmentEventReader
extends ForwardingXmlEventReader {
    private final StringBuilder stringBuilder = new StringBuilder("");

    public TextExtractingXmlFragmentEventReader(XMLEventReader xmlEventReader) throws XMLStreamException {
        super(xmlEventReader);
    }

    @Override
    public XMLEvent nextEvent() throws XMLStreamException {
        XMLEvent event = this.delegate.nextEvent();
        if (event.isCharacters()) {
            this.stringBuilder.append(event.asCharacters().getData());
        } else if (event.isEntityReference()) {
            StringWriter writer = new StringWriter();
            event.writeAsEncodedUnicode(writer);
            writer.flush();
            this.stringBuilder.append(writer.getBuffer());
        }
        return event;
    }

    public String getText() {
        return this.stringBuilder.toString();
    }
}

