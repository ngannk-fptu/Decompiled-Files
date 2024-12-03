/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.parser.NumberListHandler
 *  org.apache.batik.parser.NumberListParser
 *  org.apache.batik.parser.ParseException
 *  org.w3c.dom.svg.SVGException
 *  org.w3c.dom.svg.SVGNumber
 *  org.w3c.dom.svg.SVGNumberList
 */
package org.apache.batik.dom.svg;

import org.apache.batik.dom.svg.AbstractSVGList;
import org.apache.batik.dom.svg.ListHandler;
import org.apache.batik.dom.svg.SVGItem;
import org.apache.batik.dom.svg.SVGNumberItem;
import org.apache.batik.parser.NumberListHandler;
import org.apache.batik.parser.NumberListParser;
import org.apache.batik.parser.ParseException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGNumber;
import org.w3c.dom.svg.SVGNumberList;

public abstract class AbstractSVGNumberList
extends AbstractSVGList
implements SVGNumberList {
    public static final String SVG_NUMBER_LIST_SEPARATOR = " ";

    @Override
    protected String getItemSeparator() {
        return SVG_NUMBER_LIST_SEPARATOR;
    }

    protected abstract SVGException createSVGException(short var1, String var2, Object[] var3);

    protected abstract Element getElement();

    protected AbstractSVGNumberList() {
    }

    public SVGNumber initialize(SVGNumber newItem) throws DOMException, SVGException {
        return (SVGNumber)this.initializeImpl(newItem);
    }

    public SVGNumber getItem(int index) throws DOMException {
        return (SVGNumber)this.getItemImpl(index);
    }

    public SVGNumber insertItemBefore(SVGNumber newItem, int index) throws DOMException, SVGException {
        return (SVGNumber)this.insertItemBeforeImpl(newItem, index);
    }

    public SVGNumber replaceItem(SVGNumber newItem, int index) throws DOMException, SVGException {
        return (SVGNumber)this.replaceItemImpl(newItem, index);
    }

    public SVGNumber removeItem(int index) throws DOMException {
        return (SVGNumber)this.removeItemImpl(index);
    }

    public SVGNumber appendItem(SVGNumber newItem) throws DOMException, SVGException {
        return (SVGNumber)this.appendItemImpl(newItem);
    }

    @Override
    protected SVGItem createSVGItem(Object newItem) {
        SVGNumber l = (SVGNumber)newItem;
        return new SVGNumberItem(l.getValue());
    }

    @Override
    protected void doParse(String value, ListHandler handler) throws ParseException {
        NumberListParser NumberListParser2 = new NumberListParser();
        NumberListBuilder builder = new NumberListBuilder(handler);
        NumberListParser2.setNumberListHandler((NumberListHandler)builder);
        NumberListParser2.parse(value);
    }

    @Override
    protected void checkItemType(Object newItem) throws SVGException {
        if (!(newItem instanceof SVGNumber)) {
            this.createSVGException((short)0, "expected SVGNumber", null);
        }
    }

    protected static class NumberListBuilder
    implements NumberListHandler {
        protected ListHandler listHandler;
        protected float currentValue;

        public NumberListBuilder(ListHandler listHandler) {
            this.listHandler = listHandler;
        }

        public void startNumberList() throws ParseException {
            this.listHandler.startList();
        }

        public void startNumber() throws ParseException {
            this.currentValue = 0.0f;
        }

        public void numberValue(float v) throws ParseException {
            this.currentValue = v;
        }

        public void endNumber() throws ParseException {
            this.listHandler.item(new SVGNumberItem(this.currentValue));
        }

        public void endNumberList() throws ParseException {
            this.listHandler.endList();
        }
    }
}

