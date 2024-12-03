/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.sax;

import org.apache.tika.sax.TextContentHandler;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class TextAndAttributeContentHandler
extends TextContentHandler {
    public TextAndAttributeContentHandler(ContentHandler delegate) {
        this(delegate, false);
    }

    public TextAndAttributeContentHandler(ContentHandler delegate, boolean addSpaceBetweenElements) {
        super(delegate, addSpaceBetweenElements);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        int attributeLength = attributes.getLength();
        if (attributeLength > 0) {
            char[] elementName = (localName.trim() + " ").toCharArray();
            this.characters(elementName, 0, elementName.length);
            for (int i = 0; i < attributeLength; ++i) {
                char[] attributeName = (attributes.getLocalName(i).trim() + " ").toCharArray();
                char[] attributeValue = (attributes.getValue(i).trim() + " ").toCharArray();
                this.characters(attributeName, 0, attributeName.length);
                this.characters(attributeValue, 0, attributeValue.length);
            }
        }
    }
}

