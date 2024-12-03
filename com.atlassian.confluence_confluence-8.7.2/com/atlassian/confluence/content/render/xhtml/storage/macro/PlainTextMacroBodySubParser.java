/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.storage.macro;

import com.atlassian.confluence.content.render.xhtml.definition.PlainTextMacroBody;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

class PlainTextMacroBodySubParser {
    PlainTextMacroBodySubParser() {
    }

    static PlainTextMacroBody parse(XMLEventReader reader) throws XMLStreamException {
        StringBuilder body = new StringBuilder();
        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (!event.isCharacters() || !event.asCharacters().isCData()) continue;
            body.append(event.asCharacters().getData());
        }
        return new PlainTextMacroBody(body.toString());
    }
}

