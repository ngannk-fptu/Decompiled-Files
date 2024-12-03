/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data.xml;

import org.jfree.data.xml.DatasetTags;
import org.jfree.data.xml.ItemHandler;
import org.jfree.data.xml.RootHandler;
import org.jfree.data.xml.ValueHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class KeyHandler
extends DefaultHandler
implements DatasetTags {
    private RootHandler rootHandler;
    private ItemHandler itemHandler;
    private StringBuffer currentText;

    public KeyHandler(RootHandler rootHandler, ItemHandler itemHandler) {
        this.rootHandler = rootHandler;
        this.itemHandler = itemHandler;
        this.currentText = new StringBuffer();
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        if (!qName.equals("Key")) {
            throw new SAXException("Expecting <Key> but found " + qName);
        }
        this.clearCurrentText();
    }

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        if (!qName.equals("Key")) {
            throw new SAXException("Expecting </Key> but found " + qName);
        }
        this.itemHandler.setKey((Comparable)((Object)this.getCurrentText()));
        this.rootHandler.popSubHandler();
        this.rootHandler.pushSubHandler(new ValueHandler(this.rootHandler, this.itemHandler));
    }

    public void characters(char[] ch, int start, int length) {
        if (this.currentText != null) {
            this.currentText.append(String.copyValueOf(ch, start, length));
        }
    }

    protected String getCurrentText() {
        return this.currentText.toString();
    }

    protected void clearCurrentText() {
        this.currentText.delete(0, this.currentText.length());
    }
}

