/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.fork;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.tika.fork.ForkResource;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

class ContentHandlerResource
implements ForkResource {
    private final ContentHandler handler;

    public ContentHandlerResource(ContentHandler handler) {
        this.handler = handler;
    }

    @Override
    public Throwable process(DataInputStream input, DataOutputStream output) throws IOException {
        try {
            this.internalProcess(input);
            return null;
        }
        catch (SAXException e) {
            return e;
        }
    }

    private void internalProcess(DataInputStream input) throws IOException, SAXException {
        int type = input.readUnsignedByte();
        if (type == 1) {
            this.handler.startDocument();
        } else if (type == 2) {
            this.handler.endDocument();
        } else if (type == 3) {
            this.handler.startPrefixMapping(this.readString(input), this.readString(input));
        } else if (type == 4) {
            this.handler.endPrefixMapping(this.readString(input));
        } else if (type == 5) {
            String uri = this.readString(input);
            String localName = this.readString(input);
            String qName = this.readString(input);
            AttributesImpl atts = null;
            int n = input.readInt();
            if (n >= 0) {
                atts = new AttributesImpl();
                for (int i = 0; i < n; ++i) {
                    atts.addAttribute(this.readString(input), this.readString(input), this.readString(input), this.readString(input), this.readString(input));
                }
            }
            this.handler.startElement(uri, localName, qName, atts);
        } else if (type == 6) {
            String uri = this.readString(input);
            String localName = this.readString(input);
            String qName = this.readString(input);
            this.handler.endElement(uri, localName, qName);
        } else if (type == 7) {
            char[] ch = this.readCharacters(input);
            this.handler.characters(ch, 0, ch.length);
        } else if (type == 8) {
            char[] ch = this.readCharacters(input);
            this.handler.characters(ch, 0, ch.length);
        } else if (type == 9) {
            this.handler.processingInstruction(this.readString(input), this.readString(input));
        } else if (type == 10) {
            this.handler.skippedEntity(this.readString(input));
        }
    }

    private String readString(DataInputStream input) throws IOException {
        if (input.readBoolean()) {
            return this.readStringUTF(input);
        }
        return null;
    }

    private char[] readCharacters(DataInputStream input) throws IOException {
        return this.readStringUTF(input).toCharArray();
    }

    private String readStringUTF(DataInputStream input) throws IOException {
        int frags = input.readInt();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < frags; ++i) {
            sb.append(input.readUTF());
        }
        return sb.toString();
    }
}

