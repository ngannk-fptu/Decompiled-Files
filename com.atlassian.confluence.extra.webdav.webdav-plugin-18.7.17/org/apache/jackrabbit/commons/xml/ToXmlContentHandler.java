/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.xml;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ToXmlContentHandler
extends DefaultHandler {
    private final Writer writer;
    private final String declaration;
    private boolean startTagIsOpen = false;

    public ToXmlContentHandler(OutputStream stream, String encoding) throws UnsupportedEncodingException {
        this.writer = new OutputStreamWriter(stream, encoding);
        this.declaration = "version=\"1.0\" encoding=\"" + encoding + "\"";
    }

    public ToXmlContentHandler(OutputStream stream) {
        this.writer = new OutputStreamWriter(stream, StandardCharsets.UTF_8);
        this.declaration = "version=\"1.0\" encoding=\"UTF-8\"";
    }

    public ToXmlContentHandler(Writer writer) {
        this.writer = writer;
        this.declaration = "version=\"1.0\"";
    }

    public ToXmlContentHandler() {
        this(new StringWriter());
    }

    private void write(char[] ch, int start, int length, boolean attribute) throws SAXException {
        for (int i = start; i < start + length; ++i) {
            try {
                if (ch[i] == '>') {
                    this.writer.write("&gt;");
                    continue;
                }
                if (ch[i] == '<') {
                    this.writer.write("&lt;");
                    continue;
                }
                if (ch[i] == '&') {
                    this.writer.write("&amp;");
                    continue;
                }
                if (attribute && ch[i] == '\"') {
                    this.writer.write("&quot;");
                    continue;
                }
                if (attribute && ch[i] == '\'') {
                    this.writer.write("&apos;");
                    continue;
                }
                this.writer.write(ch[i]);
                continue;
            }
            catch (IOException e) {
                throw new SAXException("Failed to output XML character: " + ch[i], e);
            }
        }
    }

    private void closeStartTagIfOpen() throws SAXException {
        if (this.startTagIsOpen) {
            try {
                this.writer.write(">");
            }
            catch (IOException e) {
                throw new SAXException("Failed to output XML bracket: >", e);
            }
            this.startTagIsOpen = false;
        }
    }

    @Override
    public void startDocument() throws SAXException {
        this.processingInstruction("xml", this.declaration);
    }

    @Override
    public void endDocument() throws SAXException {
        try {
            this.writer.flush();
        }
        catch (IOException e) {
            throw new SAXException("Failed to flush XML output", e);
        }
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
        this.closeStartTagIfOpen();
        try {
            this.writer.write("<?");
            this.writer.write(target);
            if (data != null) {
                this.writer.write(" ");
                this.writer.write(data);
            }
            this.writer.write("?>");
        }
        catch (IOException e) {
            throw new SAXException("Failed to output XML processing instruction: " + target, e);
        }
    }

    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        this.closeStartTagIfOpen();
        try {
            this.writer.write("<");
            this.writer.write(qName);
            for (int i = 0; i < atts.getLength(); ++i) {
                this.writer.write(" ");
                this.writer.write(atts.getQName(i));
                this.writer.write("=\"");
                char[] ch = atts.getValue(i).toCharArray();
                this.write(ch, 0, ch.length, true);
                this.writer.write("\"");
            }
            this.startTagIsOpen = true;
        }
        catch (IOException e) {
            throw new SAXException("Failed to output XML end tag: " + qName, e);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        this.closeStartTagIfOpen();
        this.write(ch, start, length, false);
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        this.characters(ch, start, length);
    }

    @Override
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        try {
            if (this.startTagIsOpen) {
                this.writer.write("/>");
                this.startTagIsOpen = false;
            } else {
                this.writer.write("</");
                this.writer.write(qName);
                this.writer.write(">");
            }
        }
        catch (IOException e) {
            throw new SAXException("Failed to output XML end tag: " + qName, e);
        }
    }

    public String toString() {
        return this.writer.toString();
    }
}

