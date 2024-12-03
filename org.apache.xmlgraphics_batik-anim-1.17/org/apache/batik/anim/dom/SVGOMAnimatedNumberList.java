/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.svg.AbstractSVGList
 *  org.apache.batik.dom.svg.AbstractSVGNumberList
 *  org.apache.batik.dom.svg.ListBuilder
 *  org.apache.batik.dom.svg.ListHandler
 *  org.apache.batik.dom.svg.LiveAttributeException
 *  org.apache.batik.dom.svg.SVGItem
 *  org.apache.batik.dom.svg.SVGNumberItem
 *  org.apache.batik.parser.ParseException
 *  org.w3c.dom.svg.SVGAnimatedNumberList
 *  org.w3c.dom.svg.SVGException
 *  org.w3c.dom.svg.SVGNumber
 *  org.w3c.dom.svg.SVGNumberList
 */
package org.apache.batik.anim.dom;

import java.util.ArrayList;
import java.util.Iterator;
import org.apache.batik.anim.dom.AbstractElement;
import org.apache.batik.anim.dom.AbstractSVGAnimatedValue;
import org.apache.batik.anim.dom.AnimationTarget;
import org.apache.batik.anim.dom.SVGOMElement;
import org.apache.batik.anim.values.AnimatableNumberListValue;
import org.apache.batik.anim.values.AnimatableValue;
import org.apache.batik.dom.svg.AbstractSVGList;
import org.apache.batik.dom.svg.AbstractSVGNumberList;
import org.apache.batik.dom.svg.ListBuilder;
import org.apache.batik.dom.svg.ListHandler;
import org.apache.batik.dom.svg.LiveAttributeException;
import org.apache.batik.dom.svg.SVGItem;
import org.apache.batik.dom.svg.SVGNumberItem;
import org.apache.batik.parser.ParseException;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGAnimatedNumberList;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGNumber;
import org.w3c.dom.svg.SVGNumberList;

public class SVGOMAnimatedNumberList
extends AbstractSVGAnimatedValue
implements SVGAnimatedNumberList {
    protected BaseSVGNumberList baseVal;
    protected AnimSVGNumberList animVal;
    protected boolean changing;
    protected String defaultValue;
    protected boolean emptyAllowed;

    public SVGOMAnimatedNumberList(AbstractElement elt, String ns, String ln, String defaultValue, boolean emptyAllowed) {
        super(elt, ns, ln);
        this.defaultValue = defaultValue;
        this.emptyAllowed = emptyAllowed;
    }

    public SVGNumberList getBaseVal() {
        if (this.baseVal == null) {
            this.baseVal = new BaseSVGNumberList();
        }
        return this.baseVal;
    }

    public SVGNumberList getAnimVal() {
        if (this.animVal == null) {
            this.animVal = new AnimSVGNumberList();
        }
        return this.animVal;
    }

    public void check() {
        if (!this.hasAnimVal) {
            if (this.baseVal == null) {
                this.baseVal = new BaseSVGNumberList();
            }
            this.baseVal.revalidate();
            if (this.baseVal.missing) {
                throw new LiveAttributeException((Element)((Object)this.element), this.localName, 0, null);
            }
            if (this.baseVal.malformed) {
                throw new LiveAttributeException((Element)((Object)this.element), this.localName, 1, this.baseVal.getValueAsString());
            }
        }
    }

    @Override
    public AnimatableValue getUnderlyingValue(AnimationTarget target) {
        SVGNumberList nl = this.getBaseVal();
        int n = nl.getNumberOfItems();
        float[] numbers = new float[n];
        for (int i = 0; i < n; ++i) {
            numbers[i] = nl.getItem(n).getValue();
        }
        return new AnimatableNumberListValue(target, numbers);
    }

    @Override
    protected void updateAnimatedValue(AnimatableValue val) {
        if (val == null) {
            this.hasAnimVal = false;
        } else {
            this.hasAnimVal = true;
            AnimatableNumberListValue animNumList = (AnimatableNumberListValue)val;
            if (this.animVal == null) {
                this.animVal = new AnimSVGNumberList();
            }
            this.animVal.setAnimatedValue(animNumList.getNumbers());
        }
        this.fireAnimatedAttributeListeners();
    }

    public void attrAdded(Attr node, String newv) {
        if (!this.changing && this.baseVal != null) {
            this.baseVal.invalidate();
        }
        this.fireBaseAttributeListeners();
        if (!this.hasAnimVal) {
            this.fireAnimatedAttributeListeners();
        }
    }

    public void attrModified(Attr node, String oldv, String newv) {
        if (!this.changing && this.baseVal != null) {
            this.baseVal.invalidate();
        }
        this.fireBaseAttributeListeners();
        if (!this.hasAnimVal) {
            this.fireAnimatedAttributeListeners();
        }
    }

    public void attrRemoved(Attr node, String oldv) {
        if (!this.changing && this.baseVal != null) {
            this.baseVal.invalidate();
        }
        this.fireBaseAttributeListeners();
        if (!this.hasAnimVal) {
            this.fireAnimatedAttributeListeners();
        }
    }

    protected class AnimSVGNumberList
    extends AbstractSVGNumberList {
        public AnimSVGNumberList() {
            this.itemList = new ArrayList(1);
        }

        protected DOMException createDOMException(short type, String key, Object[] args) {
            return SVGOMAnimatedNumberList.this.element.createDOMException(type, key, args);
        }

        protected SVGException createSVGException(short type, String key, Object[] args) {
            return ((SVGOMElement)SVGOMAnimatedNumberList.this.element).createSVGException(type, key, args);
        }

        protected Element getElement() {
            return SVGOMAnimatedNumberList.this.element;
        }

        public int getNumberOfItems() {
            if (SVGOMAnimatedNumberList.this.hasAnimVal) {
                return super.getNumberOfItems();
            }
            return SVGOMAnimatedNumberList.this.getBaseVal().getNumberOfItems();
        }

        public SVGNumber getItem(int index) throws DOMException {
            if (SVGOMAnimatedNumberList.this.hasAnimVal) {
                return super.getItem(index);
            }
            return SVGOMAnimatedNumberList.this.getBaseVal().getItem(index);
        }

        protected String getValueAsString() {
            if (this.itemList.size() == 0) {
                return "";
            }
            StringBuffer sb = new StringBuffer(this.itemList.size() * 8);
            Iterator i = this.itemList.iterator();
            if (i.hasNext()) {
                sb.append(((SVGItem)i.next()).getValueAsString());
            }
            while (i.hasNext()) {
                sb.append(this.getItemSeparator());
                sb.append(((SVGItem)i.next()).getValueAsString());
            }
            return sb.toString();
        }

        protected void setAttributeValue(String value) {
        }

        public void clear() throws DOMException {
            throw SVGOMAnimatedNumberList.this.element.createDOMException((short)7, "readonly.number.list", null);
        }

        public SVGNumber initialize(SVGNumber newItem) throws DOMException, SVGException {
            throw SVGOMAnimatedNumberList.this.element.createDOMException((short)7, "readonly.number.list", null);
        }

        public SVGNumber insertItemBefore(SVGNumber newItem, int index) throws DOMException, SVGException {
            throw SVGOMAnimatedNumberList.this.element.createDOMException((short)7, "readonly.number.list", null);
        }

        public SVGNumber replaceItem(SVGNumber newItem, int index) throws DOMException, SVGException {
            throw SVGOMAnimatedNumberList.this.element.createDOMException((short)7, "readonly.number.list", null);
        }

        public SVGNumber removeItem(int index) throws DOMException {
            throw SVGOMAnimatedNumberList.this.element.createDOMException((short)7, "readonly.number.list", null);
        }

        public SVGNumber appendItem(SVGNumber newItem) throws DOMException {
            throw SVGOMAnimatedNumberList.this.element.createDOMException((short)7, "readonly.number.list", null);
        }

        protected void setAnimatedValue(float[] values) {
            int i;
            int size = this.itemList.size();
            for (i = 0; i < size && i < values.length; ++i) {
                SVGNumberItem n = (SVGNumberItem)this.itemList.get(i);
                n.setValue(values[i]);
            }
            while (i < values.length) {
                this.appendItemImpl(new SVGNumberItem(values[i]));
                ++i;
            }
            while (size > values.length) {
                this.removeItemImpl(--size);
            }
        }

        protected void resetAttribute() {
        }

        protected void resetAttribute(SVGItem item) {
        }

        protected void revalidate() {
            this.valid = true;
        }
    }

    public class BaseSVGNumberList
    extends AbstractSVGNumberList {
        protected boolean missing;
        protected boolean malformed;

        protected DOMException createDOMException(short type, String key, Object[] args) {
            return SVGOMAnimatedNumberList.this.element.createDOMException(type, key, args);
        }

        protected SVGException createSVGException(short type, String key, Object[] args) {
            return ((SVGOMElement)SVGOMAnimatedNumberList.this.element).createSVGException(type, key, args);
        }

        protected Element getElement() {
            return SVGOMAnimatedNumberList.this.element;
        }

        protected String getValueAsString() {
            Attr attr = SVGOMAnimatedNumberList.this.element.getAttributeNodeNS(SVGOMAnimatedNumberList.this.namespaceURI, SVGOMAnimatedNumberList.this.localName);
            if (attr == null) {
                return SVGOMAnimatedNumberList.this.defaultValue;
            }
            return attr.getValue();
        }

        protected void setAttributeValue(String value) {
            try {
                SVGOMAnimatedNumberList.this.changing = true;
                SVGOMAnimatedNumberList.this.element.setAttributeNS(SVGOMAnimatedNumberList.this.namespaceURI, SVGOMAnimatedNumberList.this.localName, value);
            }
            finally {
                SVGOMAnimatedNumberList.this.changing = false;
            }
        }

        protected void resetAttribute() {
            super.resetAttribute();
            this.missing = false;
            this.malformed = false;
        }

        protected void resetAttribute(SVGItem item) {
            super.resetAttribute(item);
            this.missing = false;
            this.malformed = false;
        }

        protected void revalidate() {
            boolean isEmpty;
            if (this.valid) {
                return;
            }
            this.valid = true;
            this.missing = false;
            this.malformed = false;
            String s = this.getValueAsString();
            boolean bl = isEmpty = s != null && s.length() == 0;
            if (s == null || isEmpty && !SVGOMAnimatedNumberList.this.emptyAllowed) {
                this.missing = true;
                return;
            }
            if (isEmpty) {
                this.itemList = new ArrayList(1);
            } else {
                try {
                    ListBuilder builder = new ListBuilder((AbstractSVGList)this);
                    this.doParse(s, (ListHandler)builder);
                    if (builder.getList() != null) {
                        this.clear(this.itemList);
                    }
                    this.itemList = builder.getList();
                }
                catch (ParseException e) {
                    this.itemList = new ArrayList(1);
                    this.valid = true;
                    this.malformed = true;
                }
            }
        }
    }
}

