/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.txw2.output;

import com.sun.xml.txw2.output.CharacterEscapeHandler;
import com.sun.xml.txw2.output.DumbEscapeHandler;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

public class XMLWriter
extends XMLFilterImpl
implements LexicalHandler {
    private final HashMap locallyDeclaredPrefix = new HashMap();
    private final Attributes EMPTY_ATTS = new AttributesImpl();
    private boolean inCDATA = false;
    private int elementLevel = 0;
    private Writer output;
    private String encoding;
    private boolean writeXmlDecl = true;
    private String header = null;
    private final CharacterEscapeHandler escapeHandler;
    private boolean startTagIsClosed = true;

    public XMLWriter(Writer writer, String encoding, CharacterEscapeHandler _escapeHandler) {
        this.init(writer, encoding);
        this.escapeHandler = _escapeHandler;
    }

    public XMLWriter(Writer writer, String encoding) {
        this(writer, encoding, DumbEscapeHandler.theInstance);
    }

    private void init(Writer writer, String encoding) {
        this.setOutput(writer, encoding);
    }

    public void reset() {
        this.elementLevel = 0;
        this.startTagIsClosed = true;
    }

    public void flush() throws IOException {
        this.output.flush();
    }

    public void setOutput(Writer writer, String _encoding) {
        this.output = writer == null ? new OutputStreamWriter(System.out) : writer;
        this.encoding = _encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setXmlDecl(boolean _writeXmlDecl) {
        this.writeXmlDecl = _writeXmlDecl;
    }

    public void setHeader(String _header) {
        this.header = _header;
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        this.locallyDeclaredPrefix.put(prefix, uri);
    }

    @Override
    public void startDocument() throws SAXException {
        try {
            this.reset();
            if (this.writeXmlDecl) {
                String e = "";
                if (this.encoding != null) {
                    e = " encoding=\"" + this.encoding + "\"";
                }
                this.write("<?xml version=\"1.0\"" + e + " standalone=\"yes\"?>\n");
            }
            if (this.header != null) {
                this.write(this.header);
            }
            super.startDocument();
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public void endDocument() throws SAXException {
        try {
            if (!this.startTagIsClosed) {
                this.write("/>");
                this.startTagIsClosed = true;
            }
            this.write('\n');
            super.endDocument();
            try {
                this.flush();
            }
            catch (IOException e) {
                throw new SAXException(e);
            }
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        try {
            if (!this.startTagIsClosed) {
                this.write(">");
            }
            ++this.elementLevel;
            this.write('<');
            this.writeName(uri, localName, qName, true);
            this.writeAttributes(atts);
            if (!this.locallyDeclaredPrefix.isEmpty()) {
                for (Map.Entry e : this.locallyDeclaredPrefix.entrySet()) {
                    String p = (String)e.getKey();
                    String u = (String)e.getValue();
                    if (u == null) {
                        u = "";
                    }
                    this.write(' ');
                    if ("".equals(p)) {
                        this.write("xmlns=\"");
                    } else {
                        this.write("xmlns:");
                        this.write(p);
                        this.write("=\"");
                    }
                    char[] ch = u.toCharArray();
                    this.writeEsc(ch, 0, ch.length, true);
                    this.write('\"');
                }
                this.locallyDeclaredPrefix.clear();
            }
            super.startElement(uri, localName, qName, atts);
            this.startTagIsClosed = false;
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        try {
            if (this.startTagIsClosed) {
                this.write("</");
                this.writeName(uri, localName, qName, true);
                this.write('>');
            } else {
                this.write("/>");
                this.startTagIsClosed = true;
            }
            if (this.elementLevel == 1) {
                this.write('\n');
            }
            super.endElement(uri, localName, qName);
            --this.elementLevel;
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public void characters(char[] ch, int start, int len) throws SAXException {
        try {
            if (!this.startTagIsClosed) {
                this.write('>');
                this.startTagIsClosed = true;
            }
            if (this.inCDATA) {
                this.output.write(ch, start, len);
            } else {
                this.writeEsc(ch, start, len, false);
            }
            super.characters(ch, start, len);
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        try {
            this.writeEsc(ch, start, length, false);
            super.ignorableWhitespace(ch, start, length);
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
        try {
            if (!this.startTagIsClosed) {
                this.write('>');
                this.startTagIsClosed = true;
            }
            this.write("<?");
            this.write(target);
            this.write(' ');
            this.write(data);
            this.write("?>");
            if (this.elementLevel < 1) {
                this.write('\n');
            }
            super.processingInstruction(target, data);
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
    }

    public void startElement(String uri, String localName) throws SAXException {
        this.startElement(uri, localName, "", this.EMPTY_ATTS);
    }

    public void startElement(String localName) throws SAXException {
        this.startElement("", localName, "", this.EMPTY_ATTS);
    }

    public void endElement(String uri, String localName) throws SAXException {
        this.endElement(uri, localName, "");
    }

    public void endElement(String localName) throws SAXException {
        this.endElement("", localName, "");
    }

    public void dataElement(String uri, String localName, String qName, Attributes atts, String content) throws SAXException {
        this.startElement(uri, localName, qName, atts);
        this.characters(content);
        this.endElement(uri, localName, qName);
    }

    public void dataElement(String uri, String localName, String content) throws SAXException {
        this.dataElement(uri, localName, "", this.EMPTY_ATTS, content);
    }

    public void dataElement(String localName, String content) throws SAXException {
        this.dataElement("", localName, "", this.EMPTY_ATTS, content);
    }

    public void characters(String data) throws SAXException {
        try {
            if (!this.startTagIsClosed) {
                this.write('>');
                this.startTagIsClosed = true;
            }
            char[] ch = data.toCharArray();
            this.characters(ch, 0, ch.length);
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public void startDTD(String name, String publicId, String systemId) throws SAXException {
    }

    @Override
    public void endDTD() throws SAXException {
    }

    @Override
    public void startEntity(String name) throws SAXException {
    }

    @Override
    public void endEntity(String name) throws SAXException {
    }

    @Override
    public void startCDATA() throws SAXException {
        try {
            if (!this.startTagIsClosed) {
                this.write('>');
                this.startTagIsClosed = true;
            }
            this.write("<![CDATA[");
            this.inCDATA = true;
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public void endCDATA() throws SAXException {
        try {
            this.inCDATA = false;
            this.write("]]>");
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public void comment(char[] ch, int start, int length) throws SAXException {
        try {
            this.output.write("<!--");
            this.output.write(ch, start, length);
            this.output.write("-->");
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
    }

    private void write(char c) throws IOException {
        this.output.write(c);
    }

    private void write(String s) throws IOException {
        this.output.write(s);
    }

    private void writeAttributes(Attributes atts) throws IOException, SAXException {
        int len = atts.getLength();
        for (int i = 0; i < len; ++i) {
            char[] ch = atts.getValue(i).toCharArray();
            this.write(' ');
            this.writeName(atts.getURI(i), atts.getLocalName(i), atts.getQName(i), false);
            this.write("=\"");
            this.writeEsc(ch, 0, ch.length, true);
            this.write('\"');
        }
    }

    private void writeEsc(char[] ch, int start, int length, boolean isAttVal) throws SAXException, IOException {
        this.escapeHandler.escape(ch, start, length, isAttVal, this.output);
    }

    private void writeName(String uri, String localName, String qName, boolean isElement) throws IOException {
        this.write(qName);
    }
}

