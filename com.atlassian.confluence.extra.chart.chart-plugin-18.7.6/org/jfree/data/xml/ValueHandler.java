/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data.xml;

import org.jfree.data.xml.DatasetTags;
import org.jfree.data.xml.ItemHandler;
import org.jfree.data.xml.RootHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ValueHandler
extends DefaultHandler
implements DatasetTags {
    private RootHandler rootHandler;
    private ItemHandler itemHandler;
    private StringBuffer currentText;

    public ValueHandler(RootHandler rootHandler, ItemHandler itemHandler) {
        this.rootHandler = rootHandler;
        this.itemHandler = itemHandler;
        this.currentText = new StringBuffer();
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        if (!qName.equals("Value")) {
            throw new SAXException("Expecting <Value> but found " + qName);
        }
        this.clearCurrentText();
    }

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        Double value;
        if (qName.equals("Value")) {
            try {
                value = Double.valueOf(this.currentText.toString());
                if (value.isNaN()) {
                    value = null;
                }
            }
            catch (NumberFormatException e1) {
                value = null;
            }
        } else {
            throw new SAXException("Expecting </Value> but found " + qName);
        }
        this.itemHandler.setValue(value);
        this.rootHandler.popSubHandler();
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

