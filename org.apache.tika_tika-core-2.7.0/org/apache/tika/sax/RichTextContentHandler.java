/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.sax;

import java.io.Writer;
import org.apache.tika.sax.WriteOutContentHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class RichTextContentHandler
extends WriteOutContentHandler {
    public RichTextContentHandler(Writer writer) {
        super(writer);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        String nfo;
        super.startElement(uri, localName, qName, attributes);
        if ("img".equals(localName) && attributes.getValue("alt") != null) {
            nfo = "[image: " + attributes.getValue("alt") + ']';
            this.characters(nfo.toCharArray(), 0, nfo.length());
        }
        if ("a".equals(localName) && attributes.getValue("name") != null) {
            nfo = "[bookmark: " + attributes.getValue("name") + ']';
            this.characters(nfo.toCharArray(), 0, nfo.length());
        }
    }
}

