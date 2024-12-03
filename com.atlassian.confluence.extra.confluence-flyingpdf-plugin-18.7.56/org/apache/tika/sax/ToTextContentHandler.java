/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.sax;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Locale;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ToTextContentHandler
extends DefaultHandler {
    private static final String STYLE = "STYLE";
    private static final String SCRIPT = "SCRIPT";
    private int styleDepth = 0;
    private int scriptDepth = 0;
    private final Writer writer;

    public ToTextContentHandler(Writer writer) {
        this.writer = writer;
    }

    public ToTextContentHandler(OutputStream stream) {
        this(new OutputStreamWriter(stream, Charset.defaultCharset()));
    }

    public ToTextContentHandler(OutputStream stream, String encoding) throws UnsupportedEncodingException {
        this(new OutputStreamWriter(stream, encoding));
    }

    public ToTextContentHandler() {
        this(new StringWriter());
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (this.styleDepth + this.scriptDepth != 0) {
            return;
        }
        try {
            this.writer.write(ch, start, length);
        }
        catch (IOException e) {
            throw new SAXException("Error writing: " + new String(ch, start, length), e);
        }
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        this.characters(ch, start, length);
    }

    @Override
    public void endDocument() throws SAXException {
        try {
            this.writer.flush();
        }
        catch (IOException e) {
            throw new SAXException("Error flushing character output", e);
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        String uc;
        String string = uc = qName == null ? "" : qName.toUpperCase(Locale.ENGLISH);
        if (uc.equals(STYLE)) {
            ++this.styleDepth;
        }
        if (uc.equals(SCRIPT)) {
            ++this.scriptDepth;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        String uc;
        String string = uc = qName == null ? "" : qName.toUpperCase(Locale.ENGLISH);
        if (uc.equals(STYLE)) {
            --this.styleDepth;
        }
        if (uc.equals(SCRIPT)) {
            --this.scriptDepth;
        }
    }

    public String toString() {
        return this.writer.toString();
    }
}

