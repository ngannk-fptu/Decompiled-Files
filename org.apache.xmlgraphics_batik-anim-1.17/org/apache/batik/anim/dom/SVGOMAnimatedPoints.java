/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.svg.AbstractSVGList
 *  org.apache.batik.dom.svg.AbstractSVGPointList
 *  org.apache.batik.dom.svg.ListBuilder
 *  org.apache.batik.dom.svg.ListHandler
 *  org.apache.batik.dom.svg.LiveAttributeException
 *  org.apache.batik.dom.svg.SVGItem
 *  org.apache.batik.dom.svg.SVGPointItem
 *  org.apache.batik.parser.ParseException
 *  org.w3c.dom.svg.SVGAnimatedPoints
 *  org.w3c.dom.svg.SVGException
 *  org.w3c.dom.svg.SVGPoint
 *  org.w3c.dom.svg.SVGPointList
 */
package org.apache.batik.anim.dom;

import java.util.ArrayList;
import java.util.Iterator;
import org.apache.batik.anim.dom.AbstractElement;
import org.apache.batik.anim.dom.AbstractSVGAnimatedValue;
import org.apache.batik.anim.dom.AnimationTarget;
import org.apache.batik.anim.dom.SVGOMElement;
import org.apache.batik.anim.values.AnimatablePointListValue;
import org.apache.batik.anim.values.AnimatableValue;
import org.apache.batik.dom.svg.AbstractSVGList;
import org.apache.batik.dom.svg.AbstractSVGPointList;
import org.apache.batik.dom.svg.ListBuilder;
import org.apache.batik.dom.svg.ListHandler;
import org.apache.batik.dom.svg.LiveAttributeException;
import org.apache.batik.dom.svg.SVGItem;
import org.apache.batik.dom.svg.SVGPointItem;
import org.apache.batik.parser.ParseException;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGAnimatedPoints;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGPoint;
import org.w3c.dom.svg.SVGPointList;

public class SVGOMAnimatedPoints
extends AbstractSVGAnimatedValue
implements SVGAnimatedPoints {
    protected BaseSVGPointList baseVal;
    protected AnimSVGPointList animVal;
    protected boolean changing;
    protected String defaultValue;

    public SVGOMAnimatedPoints(AbstractElement elt, String ns, String ln, String defaultValue) {
        super(elt, ns, ln);
        this.defaultValue = defaultValue;
    }

    public SVGPointList getPoints() {
        if (this.baseVal == null) {
            this.baseVal = new BaseSVGPointList();
        }
        return this.baseVal;
    }

    public SVGPointList getAnimatedPoints() {
        if (this.animVal == null) {
            this.animVal = new AnimSVGPointList();
        }
        return this.animVal;
    }

    public void check() {
        if (!this.hasAnimVal) {
            if (this.baseVal == null) {
                this.baseVal = new BaseSVGPointList();
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
        SVGPointList pl = this.getPoints();
        int n = pl.getNumberOfItems();
        float[] points = new float[n * 2];
        for (int i = 0; i < n; ++i) {
            SVGPoint p = pl.getItem(i);
            points[i * 2] = p.getX();
            points[i * 2 + 1] = p.getY();
        }
        return new AnimatablePointListValue(target, points);
    }

    @Override
    protected void updateAnimatedValue(AnimatableValue val) {
        if (val == null) {
            this.hasAnimVal = false;
        } else {
            this.hasAnimVal = true;
            AnimatablePointListValue animPointList = (AnimatablePointListValue)val;
            if (this.animVal == null) {
                this.animVal = new AnimSVGPointList();
            }
            this.animVal.setAnimatedValue(animPointList.getNumbers());
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

    protected class AnimSVGPointList
    extends AbstractSVGPointList {
        public AnimSVGPointList() {
            this.itemList = new ArrayList(1);
        }

        protected DOMException createDOMException(short type, String key, Object[] args) {
            return SVGOMAnimatedPoints.this.element.createDOMException(type, key, args);
        }

        protected SVGException createSVGException(short type, String key, Object[] args) {
            return ((SVGOMElement)SVGOMAnimatedPoints.this.element).createSVGException(type, key, args);
        }

        public int getNumberOfItems() {
            if (SVGOMAnimatedPoints.this.hasAnimVal) {
                return super.getNumberOfItems();
            }
            return SVGOMAnimatedPoints.this.getPoints().getNumberOfItems();
        }

        public SVGPoint getItem(int index) throws DOMException {
            if (SVGOMAnimatedPoints.this.hasAnimVal) {
                return super.getItem(index);
            }
            return SVGOMAnimatedPoints.this.getPoints().getItem(index);
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
            throw SVGOMAnimatedPoints.this.element.createDOMException((short)7, "readonly.point.list", null);
        }

        public SVGPoint initialize(SVGPoint newItem) throws DOMException, SVGException {
            throw SVGOMAnimatedPoints.this.element.createDOMException((short)7, "readonly.point.list", null);
        }

        public SVGPoint insertItemBefore(SVGPoint newItem, int index) throws DOMException, SVGException {
            throw SVGOMAnimatedPoints.this.element.createDOMException((short)7, "readonly.point.list", null);
        }

        public SVGPoint replaceItem(SVGPoint newItem, int index) throws DOMException, SVGException {
            throw SVGOMAnimatedPoints.this.element.createDOMException((short)7, "readonly.point.list", null);
        }

        public SVGPoint removeItem(int index) throws DOMException {
            throw SVGOMAnimatedPoints.this.element.createDOMException((short)7, "readonly.point.list", null);
        }

        public SVGPoint appendItem(SVGPoint newItem) throws DOMException {
            throw SVGOMAnimatedPoints.this.element.createDOMException((short)7, "readonly.point.list", null);
        }

        protected void setAnimatedValue(float[] pts) {
            int i;
            int size = this.itemList.size();
            for (i = 0; i < size && i < pts.length / 2; ++i) {
                SVGPointItem p = (SVGPointItem)this.itemList.get(i);
                p.setX(pts[i * 2]);
                p.setY(pts[i * 2 + 1]);
            }
            while (i < pts.length / 2) {
                this.appendItemImpl(new SVGPointItem(pts[i * 2], pts[i * 2 + 1]));
                ++i;
            }
            while (size > pts.length / 2) {
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

    protected class BaseSVGPointList
    extends AbstractSVGPointList {
        protected boolean missing;
        protected boolean malformed;

        protected BaseSVGPointList() {
        }

        protected DOMException createDOMException(short type, String key, Object[] args) {
            return SVGOMAnimatedPoints.this.element.createDOMException(type, key, args);
        }

        protected SVGException createSVGException(short type, String key, Object[] args) {
            return ((SVGOMElement)SVGOMAnimatedPoints.this.element).createSVGException(type, key, args);
        }

        protected String getValueAsString() {
            Attr attr = SVGOMAnimatedPoints.this.element.getAttributeNodeNS(SVGOMAnimatedPoints.this.namespaceURI, SVGOMAnimatedPoints.this.localName);
            if (attr == null) {
                return SVGOMAnimatedPoints.this.defaultValue;
            }
            return attr.getValue();
        }

        protected void setAttributeValue(String value) {
            try {
                SVGOMAnimatedPoints.this.changing = true;
                SVGOMAnimatedPoints.this.element.setAttributeNS(SVGOMAnimatedPoints.this.namespaceURI, SVGOMAnimatedPoints.this.localName, value);
            }
            finally {
                SVGOMAnimatedPoints.this.changing = false;
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

