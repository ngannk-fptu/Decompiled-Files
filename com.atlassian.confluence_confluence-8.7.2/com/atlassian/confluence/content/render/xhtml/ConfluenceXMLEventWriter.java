/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml;

import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.xml.NoAutoescapeCharacters;
import java.io.IOException;
import java.io.Writer;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;

public class ConfluenceXMLEventWriter
implements XMLEventWriter {
    private final XMLEventWriter xmlEventWriter;
    private final Writer writer;

    public ConfluenceXMLEventWriter(XMLEventWriter xmlEventWriter, Writer writer) {
        this.xmlEventWriter = xmlEventWriter;
        this.writer = writer;
    }

    @Override
    public void add(XMLEvent event) throws XMLStreamException {
        if (event.isCharacters() && this.isHandlingRequired(event.asCharacters())) {
            StaxUtils.flushEventWriter(this.xmlEventWriter);
            try {
                Characters characters = event.asCharacters();
                if (characters instanceof NoAutoescapeCharacters) {
                    this.writer.append(event.asCharacters().getData());
                }
                this.writer.append(this.marshalCharacters(event.asCharacters()));
            }
            catch (IOException e) {
                throw new RuntimeException("Error writing to the writer that backs the xml event writer.", e);
            }
        } else {
            this.xmlEventWriter.add(event);
        }
    }

    @Override
    public void add(XMLEventReader reader) throws XMLStreamException {
        while (reader.hasNext()) {
            this.add(reader.nextEvent());
        }
    }

    public boolean isHandlingRequired(Characters characters) {
        if (characters.isCData()) {
            return false;
        }
        String str = characters.getData();
        for (int i = 0; i < str.length(); ++i) {
            switch (str.charAt(i)) {
                case '\"': 
                case '&': 
                case '\'': 
                case '<': 
                case '>': {
                    return true;
                }
            }
        }
        return false;
    }

    public String marshalCharacters(Characters characters) {
        StringBuilder builder = new StringBuilder();
        String str = characters.getData();
        block7: for (int i = 0; i < str.length(); ++i) {
            char nextCh = str.charAt(i);
            switch (nextCh) {
                case '<': {
                    builder.append("&lt;");
                    continue block7;
                }
                case '>': {
                    builder.append("&gt;");
                    continue block7;
                }
                case '\"': {
                    builder.append("&quot;");
                    continue block7;
                }
                case '\'': {
                    builder.append("&apos;");
                    continue block7;
                }
                case '&': {
                    builder.append("&amp;");
                    continue block7;
                }
                default: {
                    builder.append(nextCh);
                }
            }
        }
        return builder.toString();
    }

    @Override
    public void flush() throws XMLStreamException {
        this.xmlEventWriter.flush();
    }

    @Override
    public void close() throws XMLStreamException {
        this.xmlEventWriter.close();
    }

    @Override
    public String getPrefix(String uri) throws XMLStreamException {
        return this.xmlEventWriter.getPrefix(uri);
    }

    @Override
    public void setPrefix(String prefix, String uri) throws XMLStreamException {
        this.xmlEventWriter.setPrefix(prefix, uri);
    }

    @Override
    public void setDefaultNamespace(String uri) throws XMLStreamException {
        this.xmlEventWriter.setDefaultNamespace(uri);
    }

    @Override
    public void setNamespaceContext(NamespaceContext context) throws XMLStreamException {
        this.xmlEventWriter.setNamespaceContext(context);
    }

    @Override
    public NamespaceContext getNamespaceContext() {
        return this.xmlEventWriter.getNamespaceContext();
    }
}

