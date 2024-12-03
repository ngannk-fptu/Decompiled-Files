/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data.xml;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xml.CategorySeriesHandler;
import org.jfree.data.xml.DatasetTags;
import org.jfree.data.xml.RootHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class CategoryDatasetHandler
extends RootHandler
implements DatasetTags {
    private DefaultCategoryDataset dataset = null;

    public CategoryDataset getDataset() {
        return this.dataset;
    }

    public void addItem(Comparable rowKey, Comparable columnKey, Number value) {
        this.dataset.addValue(value, rowKey, columnKey);
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        DefaultHandler current = this.getCurrentHandler();
        if (current != this) {
            current.startElement(namespaceURI, localName, qName, atts);
        } else if (qName.equals("CategoryDataset")) {
            this.dataset = new DefaultCategoryDataset();
        } else if (qName.equals("Series")) {
            CategorySeriesHandler subhandler = new CategorySeriesHandler(this);
            this.getSubHandlers().push(subhandler);
            subhandler.startElement(namespaceURI, localName, qName, atts);
        } else {
            throw new SAXException("Element not recognised: " + qName);
        }
    }

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        DefaultHandler current = this.getCurrentHandler();
        if (current != this) {
            current.endElement(namespaceURI, localName, qName);
        }
    }
}

