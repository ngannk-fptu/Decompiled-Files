/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.svg.AbstractSVGList
 *  org.apache.batik.dom.svg.AbstractSVGTransformList
 *  org.apache.batik.dom.svg.AbstractSVGTransformList$SVGTransformItem
 *  org.apache.batik.dom.svg.ListBuilder
 *  org.apache.batik.dom.svg.ListHandler
 *  org.apache.batik.dom.svg.LiveAttributeException
 *  org.apache.batik.dom.svg.SVGItem
 *  org.apache.batik.parser.ParseException
 *  org.w3c.dom.svg.SVGAnimatedTransformList
 *  org.w3c.dom.svg.SVGException
 *  org.w3c.dom.svg.SVGTransform
 *  org.w3c.dom.svg.SVGTransformList
 */
package org.apache.batik.anim.dom;

import java.util.ArrayList;
import java.util.Iterator;
import org.apache.batik.anim.dom.AbstractElement;
import org.apache.batik.anim.dom.AbstractSVGAnimatedValue;
import org.apache.batik.anim.dom.AnimationTarget;
import org.apache.batik.anim.dom.SVGOMElement;
import org.apache.batik.anim.values.AnimatableTransformListValue;
import org.apache.batik.anim.values.AnimatableValue;
import org.apache.batik.dom.svg.AbstractSVGList;
import org.apache.batik.dom.svg.AbstractSVGTransformList;
import org.apache.batik.dom.svg.ListBuilder;
import org.apache.batik.dom.svg.ListHandler;
import org.apache.batik.dom.svg.LiveAttributeException;
import org.apache.batik.dom.svg.SVGItem;
import org.apache.batik.parser.ParseException;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGAnimatedTransformList;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGTransform;
import org.w3c.dom.svg.SVGTransformList;

public class SVGOMAnimatedTransformList
extends AbstractSVGAnimatedValue
implements SVGAnimatedTransformList {
    protected BaseSVGTransformList baseVal;
    protected AnimSVGTransformList animVal;
    protected boolean changing;
    protected String defaultValue;

    public SVGOMAnimatedTransformList(AbstractElement elt, String ns, String ln, String defaultValue) {
        super(elt, ns, ln);
        this.defaultValue = defaultValue;
    }

    public SVGTransformList getBaseVal() {
        if (this.baseVal == null) {
            this.baseVal = new BaseSVGTransformList();
        }
        return this.baseVal;
    }

    public SVGTransformList getAnimVal() {
        if (this.animVal == null) {
            this.animVal = new AnimSVGTransformList();
        }
        return this.animVal;
    }

    public void check() {
        if (!this.hasAnimVal) {
            if (this.baseVal == null) {
                this.baseVal = new BaseSVGTransformList();
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
        SVGTransformList tl = this.getBaseVal();
        int n = tl.getNumberOfItems();
        ArrayList<SVGTransform> v = new ArrayList<SVGTransform>(n);
        for (int i = 0; i < n; ++i) {
            v.add(tl.getItem(i));
        }
        return new AnimatableTransformListValue(target, v);
    }

    @Override
    protected void updateAnimatedValue(AnimatableValue val) {
        if (val == null) {
            this.hasAnimVal = false;
        } else {
            this.hasAnimVal = true;
            AnimatableTransformListValue aval = (AnimatableTransformListValue)val;
            if (this.animVal == null) {
                this.animVal = new AnimSVGTransformList();
            }
            this.animVal.setAnimatedValue(aval.getTransforms());
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

    protected class AnimSVGTransformList
    extends AbstractSVGTransformList {
        public AnimSVGTransformList() {
            this.itemList = new ArrayList(1);
        }

        protected DOMException createDOMException(short type, String key, Object[] args) {
            return SVGOMAnimatedTransformList.this.element.createDOMException(type, key, args);
        }

        protected SVGException createSVGException(short type, String key, Object[] args) {
            return ((SVGOMElement)SVGOMAnimatedTransformList.this.element).createSVGException(type, key, args);
        }

        public int getNumberOfItems() {
            if (SVGOMAnimatedTransformList.this.hasAnimVal) {
                return super.getNumberOfItems();
            }
            return SVGOMAnimatedTransformList.this.getBaseVal().getNumberOfItems();
        }

        public SVGTransform getItem(int index) throws DOMException {
            if (SVGOMAnimatedTransformList.this.hasAnimVal) {
                return super.getItem(index);
            }
            return SVGOMAnimatedTransformList.this.getBaseVal().getItem(index);
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
            throw SVGOMAnimatedTransformList.this.element.createDOMException((short)7, "readonly.transform.list", null);
        }

        public SVGTransform initialize(SVGTransform newItem) throws DOMException, SVGException {
            throw SVGOMAnimatedTransformList.this.element.createDOMException((short)7, "readonly.transform.list", null);
        }

        public SVGTransform insertItemBefore(SVGTransform newItem, int index) throws DOMException, SVGException {
            throw SVGOMAnimatedTransformList.this.element.createDOMException((short)7, "readonly.transform.list", null);
        }

        public SVGTransform replaceItem(SVGTransform newItem, int index) throws DOMException, SVGException {
            throw SVGOMAnimatedTransformList.this.element.createDOMException((short)7, "readonly.transform.list", null);
        }

        public SVGTransform removeItem(int index) throws DOMException {
            throw SVGOMAnimatedTransformList.this.element.createDOMException((short)7, "readonly.transform.list", null);
        }

        public SVGTransform appendItem(SVGTransform newItem) throws DOMException {
            throw SVGOMAnimatedTransformList.this.element.createDOMException((short)7, "readonly.transform.list", null);
        }

        public SVGTransform consolidate() {
            throw SVGOMAnimatedTransformList.this.element.createDOMException((short)7, "readonly.transform.list", null);
        }

        protected void setAnimatedValue(Iterator it) {
            int i;
            int size = this.itemList.size();
            for (i = 0; i < size && it.hasNext(); ++i) {
                AbstractSVGTransformList.SVGTransformItem t = (AbstractSVGTransformList.SVGTransformItem)this.itemList.get(i);
                t.assign((SVGTransform)it.next());
            }
            while (it.hasNext()) {
                this.appendItemImpl(new AbstractSVGTransformList.SVGTransformItem((SVGTransform)it.next()));
                ++i;
            }
            while (size > i) {
                this.removeItemImpl(--size);
            }
        }

        protected void setAnimatedValue(SVGTransform transform) {
            int size = this.itemList.size();
            while (size > 1) {
                this.removeItemImpl(--size);
            }
            if (size == 0) {
                this.appendItemImpl(new AbstractSVGTransformList.SVGTransformItem(transform));
            } else {
                AbstractSVGTransformList.SVGTransformItem t = (AbstractSVGTransformList.SVGTransformItem)this.itemList.get(0);
                t.assign(transform);
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

    public class BaseSVGTransformList
    extends AbstractSVGTransformList {
        protected boolean missing;
        protected boolean malformed;

        protected DOMException createDOMException(short type, String key, Object[] args) {
            return SVGOMAnimatedTransformList.this.element.createDOMException(type, key, args);
        }

        protected SVGException createSVGException(short type, String key, Object[] args) {
            return ((SVGOMElement)SVGOMAnimatedTransformList.this.element).createSVGException(type, key, args);
        }

        protected String getValueAsString() {
            Attr attr = SVGOMAnimatedTransformList.this.element.getAttributeNodeNS(SVGOMAnimatedTransformList.this.namespaceURI, SVGOMAnimatedTransformList.this.localName);
            if (attr == null) {
                return SVGOMAnimatedTransformList.this.defaultValue;
            }
            return attr.getValue();
        }

        protected void setAttributeValue(String value) {
            try {
                SVGOMAnimatedTransformList.this.changing = true;
                SVGOMAnimatedTransformList.this.element.setAttributeNS(SVGOMAnimatedTransformList.this.namespaceURI, SVGOMAnimatedTransformList.this.localName, value);
            }
            finally {
                SVGOMAnimatedTransformList.this.changing = false;
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
            if (this.valid) {
                return;
            }
            this.valid = true;
            this.missing = false;
            this.malformed = false;
            String s = this.getValueAsString();
            if (s == null) {
                this.missing = true;
                return;
            }
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
                this.malformed = true;
            }
        }
    }
}

