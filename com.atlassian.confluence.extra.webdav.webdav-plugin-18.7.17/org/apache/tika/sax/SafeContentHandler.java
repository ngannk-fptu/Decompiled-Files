/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.sax;

import org.apache.tika.sax.ContentHandlerDecorator;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class SafeContentHandler
extends ContentHandlerDecorator {
    private static final char[] REPLACEMENT = new char[]{'\ufffd'};
    private final Output charactersOutput = (x$0, x$1, x$2) -> SafeContentHandler.access$201(this, x$0, x$1, x$2);
    private final Output ignorableWhitespaceOutput = (x$0, x$1, x$2) -> SafeContentHandler.access$101(this, x$0, x$1, x$2);

    public SafeContentHandler(ContentHandler handler) {
        super(handler);
    }

    private void filter(char[] ch, int start, int length, Output output) throws SAXException {
        int end = start + length;
        int i = start;
        while (i < end) {
            int c = Character.codePointAt(ch, i, end);
            int j = i + Character.charCount(c);
            if (this.isInvalid(c)) {
                if (i > start) {
                    output.write(ch, start, i - start);
                }
                this.writeReplacement(output);
                start = j;
            }
            i = j;
        }
        output.write(ch, start, end - start);
    }

    private boolean isInvalid(String value) {
        int c;
        char[] ch = value.toCharArray();
        for (int i = 0; i < ch.length; i += Character.charCount(c)) {
            c = Character.codePointAt(ch, i);
            if (!this.isInvalid(c)) continue;
            return true;
        }
        return false;
    }

    protected boolean isInvalid(int ch) {
        if (ch < 32) {
            return ch != 9 && ch != 10 && ch != 13;
        }
        if (ch < 57344) {
            return ch > 55295;
        }
        if (ch < 65536) {
            return ch > 65533;
        }
        return ch > 0x10FFFF;
    }

    protected void writeReplacement(Output output) throws SAXException {
        output.write(REPLACEMENT, 0, REPLACEMENT.length);
    }

    @Override
    public void startElement(String uri, String localName, String name, Attributes atts) throws SAXException {
        for (int i = 0; i < atts.getLength(); ++i) {
            if (!this.isInvalid(atts.getValue(i))) continue;
            AttributesImpl filtered = new AttributesImpl();
            for (int j = 0; j < atts.getLength(); ++j) {
                String value = atts.getValue(j);
                if (j >= i && this.isInvalid(value)) {
                    StringOutput buffer = new StringOutput();
                    this.filter(value.toCharArray(), 0, value.length(), buffer);
                    value = ((Object)buffer).toString();
                }
                filtered.addAttribute(atts.getURI(j), atts.getLocalName(j), atts.getQName(j), atts.getType(j), value);
            }
            atts = filtered;
            break;
        }
        super.startElement(uri, localName, name, atts);
    }

    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {
        super.endElement(uri, localName, name);
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        this.filter(ch, start, length, this.charactersOutput);
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        this.filter(ch, start, length, this.ignorableWhitespaceOutput);
    }

    private static class StringOutput
    implements Output {
        private final StringBuilder builder = new StringBuilder();

        private StringOutput() {
        }

        @Override
        public void write(char[] ch, int start, int length) {
            this.builder.append(ch, start, length);
        }

        public String toString() {
            return this.builder.toString();
        }
    }

    protected static interface Output {
        public void write(char[] var1, int var2, int var3) throws SAXException;
    }
}

