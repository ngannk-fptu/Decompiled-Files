/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.sax;

import org.apache.tika.sax.ContentHandlerDecorator;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class ExpandedTitleContentHandler
extends ContentHandlerDecorator {
    private static final String TITLE_TAG = "TITLE";
    private boolean isTitleTagOpen;

    public ExpandedTitleContentHandler() {
    }

    public ExpandedTitleContentHandler(ContentHandler handler) {
        super(handler);
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        this.isTitleTagOpen = false;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        super.startElement(uri, localName, qName, atts);
        if (TITLE_TAG.equalsIgnoreCase(localName) && "http://www.w3.org/1999/xhtml".equals(uri)) {
            this.isTitleTagOpen = true;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        if (TITLE_TAG.equalsIgnoreCase(localName) && "http://www.w3.org/1999/xhtml".equals(uri)) {
            this.isTitleTagOpen = false;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (this.isTitleTagOpen && length == 0) {
            try {
                super.characters(new char[0], 0, 1);
            }
            catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {}
        } else {
            super.characters(ch, start, length);
        }
    }
}

