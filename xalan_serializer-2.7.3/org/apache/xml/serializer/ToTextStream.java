/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.serializer;

import java.io.IOException;
import java.io.Writer;
import org.apache.xml.serializer.Encodings;
import org.apache.xml.serializer.ToStream;
import org.apache.xml.serializer.utils.Utils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class ToTextStream
extends ToStream {
    @Override
    protected void startDocumentInternal() throws SAXException {
        super.startDocumentInternal();
        this.m_needToCallStartDocument = false;
    }

    @Override
    public void endDocument() throws SAXException {
        this.flushPending();
        this.flushWriter();
        if (this.m_tracer != null) {
            super.fireEndDoc();
        }
    }

    @Override
    public void startElement(String namespaceURI, String localName, String name, Attributes atts) throws SAXException {
        if (this.m_tracer != null) {
            super.fireStartElem(name);
            this.firePseudoAttributes();
        }
    }

    @Override
    public void endElement(String namespaceURI, String localName, String name) throws SAXException {
        if (this.m_tracer != null) {
            super.fireEndElem(name);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        this.flushPending();
        try {
            if (this.inTemporaryOutputState()) {
                this.m_writer.write(ch, start, length);
            } else {
                this.writeNormalizedChars(ch, start, length, this.m_lineSepUse);
            }
            if (this.m_tracer != null) {
                super.fireCharEvent(ch, start, length);
            }
        }
        catch (IOException ioe) {
            throw new SAXException(ioe);
        }
    }

    @Override
    public void charactersRaw(char[] ch, int start, int length) throws SAXException {
        try {
            this.writeNormalizedChars(ch, start, length, this.m_lineSepUse);
        }
        catch (IOException ioe) {
            throw new SAXException(ioe);
        }
    }

    void writeNormalizedChars(char[] ch, int start, int length, boolean useLineSep) throws IOException, SAXException {
        String encoding = this.getEncoding();
        Writer writer = this.m_writer;
        int end = start + length;
        int S_LINEFEED = 10;
        for (int i = start; i < end; ++i) {
            char c = ch[i];
            if ('\n' == c && useLineSep) {
                writer.write(this.m_lineSep, 0, this.m_lineSepLen);
                continue;
            }
            if (this.m_encodingInfo.isInEncoding(c)) {
                writer.write(c);
                continue;
            }
            if (Encodings.isHighUTF16Surrogate(c)) {
                int codePoint = this.writeUTF16Surrogate(c, ch, i, end);
                if (codePoint != 0) {
                    String integralValue = Integer.toString(codePoint);
                    String msg = Utils.messages.createMessage("ER_ILLEGAL_CHARACTER", new Object[]{integralValue, encoding});
                    System.err.println(msg);
                }
                ++i;
                continue;
            }
            if (encoding != null) {
                writer.write(38);
                writer.write(35);
                writer.write(Integer.toString(c));
                writer.write(59);
                String integralValue = Integer.toString(c);
                String msg = Utils.messages.createMessage("ER_ILLEGAL_CHARACTER", new Object[]{integralValue, encoding});
                System.err.println(msg);
                continue;
            }
            writer.write(c);
        }
    }

    @Override
    public void cdata(char[] ch, int start, int length) throws SAXException {
        try {
            this.writeNormalizedChars(ch, start, length, this.m_lineSepUse);
            if (this.m_tracer != null) {
                super.fireCDATAEvent(ch, start, length);
            }
        }
        catch (IOException ioe) {
            throw new SAXException(ioe);
        }
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        try {
            this.writeNormalizedChars(ch, start, length, this.m_lineSepUse);
        }
        catch (IOException ioe) {
            throw new SAXException(ioe);
        }
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
        this.flushPending();
        if (this.m_tracer != null) {
            super.fireEscapingEvent(target, data);
        }
    }

    @Override
    public void comment(String data) throws SAXException {
        int length = data.length();
        if (length > this.m_charsBuff.length) {
            this.m_charsBuff = new char[length * 2 + 1];
        }
        data.getChars(0, length, this.m_charsBuff, 0);
        this.comment(this.m_charsBuff, 0, length);
    }

    @Override
    public void comment(char[] ch, int start, int length) throws SAXException {
        this.flushPending();
        if (this.m_tracer != null) {
            super.fireCommentEvent(ch, start, length);
        }
    }

    @Override
    public void entityReference(String name) throws SAXException {
        if (this.m_tracer != null) {
            super.fireEntityReference(name);
        }
    }

    @Override
    public void addAttribute(String uri, String localName, String rawName, String type, String value, boolean XSLAttribute) {
    }

    @Override
    public void endCDATA() throws SAXException {
    }

    @Override
    public void endElement(String elemName) throws SAXException {
        if (this.m_tracer != null) {
            super.fireEndElem(elemName);
        }
    }

    @Override
    public void startElement(String elementNamespaceURI, String elementLocalName, String elementName) throws SAXException {
        if (this.m_needToCallStartDocument) {
            this.startDocumentInternal();
        }
        if (this.m_tracer != null) {
            super.fireStartElem(elementName);
            this.firePseudoAttributes();
        }
    }

    @Override
    public void characters(String characters) throws SAXException {
        int length = characters.length();
        if (length > this.m_charsBuff.length) {
            this.m_charsBuff = new char[length * 2 + 1];
        }
        characters.getChars(0, length, this.m_charsBuff, 0);
        this.characters(this.m_charsBuff, 0, length);
    }

    @Override
    public void addAttribute(String name, String value) {
    }

    @Override
    public void addUniqueAttribute(String qName, String value, int flags) throws SAXException {
    }

    @Override
    public boolean startPrefixMapping(String prefix, String uri, boolean shouldFlush) throws SAXException {
        return false;
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
    }

    @Override
    public void namespaceAfterStartElement(String prefix, String uri) throws SAXException {
    }

    @Override
    public void flushPending() throws SAXException {
        if (this.m_needToCallStartDocument) {
            this.startDocumentInternal();
            this.m_needToCallStartDocument = false;
        }
    }
}

