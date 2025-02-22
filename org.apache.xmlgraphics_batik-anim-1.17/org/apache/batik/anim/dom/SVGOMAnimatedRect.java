/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.svg.LiveAttributeException
 *  org.apache.batik.dom.svg.SVGOMRect
 *  org.apache.batik.parser.DefaultNumberListHandler
 *  org.apache.batik.parser.NumberListHandler
 *  org.apache.batik.parser.NumberListParser
 *  org.apache.batik.parser.ParseException
 *  org.w3c.dom.svg.SVGAnimatedRect
 *  org.w3c.dom.svg.SVGRect
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.AbstractElement;
import org.apache.batik.anim.dom.AbstractSVGAnimatedValue;
import org.apache.batik.anim.dom.AnimationTarget;
import org.apache.batik.anim.values.AnimatableRectValue;
import org.apache.batik.anim.values.AnimatableValue;
import org.apache.batik.dom.svg.LiveAttributeException;
import org.apache.batik.dom.svg.SVGOMRect;
import org.apache.batik.parser.DefaultNumberListHandler;
import org.apache.batik.parser.NumberListHandler;
import org.apache.batik.parser.NumberListParser;
import org.apache.batik.parser.ParseException;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGAnimatedRect;
import org.w3c.dom.svg.SVGRect;

public class SVGOMAnimatedRect
extends AbstractSVGAnimatedValue
implements SVGAnimatedRect {
    protected BaseSVGRect baseVal;
    protected AnimSVGRect animVal;
    protected boolean changing;
    protected String defaultValue;

    public SVGOMAnimatedRect(AbstractElement elt, String ns, String ln, String def) {
        super(elt, ns, ln);
        this.defaultValue = def;
    }

    public SVGRect getBaseVal() {
        if (this.baseVal == null) {
            this.baseVal = new BaseSVGRect();
        }
        return this.baseVal;
    }

    public SVGRect getAnimVal() {
        if (this.animVal == null) {
            this.animVal = new AnimSVGRect();
        }
        return this.animVal;
    }

    @Override
    protected void updateAnimatedValue(AnimatableValue val) {
        if (val == null) {
            this.hasAnimVal = false;
        } else {
            this.hasAnimVal = true;
            AnimatableRectValue animRect = (AnimatableRectValue)val;
            if (this.animVal == null) {
                this.animVal = new AnimSVGRect();
            }
            this.animVal.setAnimatedValue(animRect.getX(), animRect.getY(), animRect.getWidth(), animRect.getHeight());
        }
        this.fireAnimatedAttributeListeners();
    }

    @Override
    public AnimatableValue getUnderlyingValue(AnimationTarget target) {
        SVGRect r = this.getBaseVal();
        return new AnimatableRectValue(target, r.getX(), r.getY(), r.getWidth(), r.getHeight());
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

    protected class AnimSVGRect
    extends SVGOMRect {
        protected AnimSVGRect() {
        }

        public float getX() {
            if (SVGOMAnimatedRect.this.hasAnimVal) {
                return super.getX();
            }
            return SVGOMAnimatedRect.this.getBaseVal().getX();
        }

        public float getY() {
            if (SVGOMAnimatedRect.this.hasAnimVal) {
                return super.getY();
            }
            return SVGOMAnimatedRect.this.getBaseVal().getY();
        }

        public float getWidth() {
            if (SVGOMAnimatedRect.this.hasAnimVal) {
                return super.getWidth();
            }
            return SVGOMAnimatedRect.this.getBaseVal().getWidth();
        }

        public float getHeight() {
            if (SVGOMAnimatedRect.this.hasAnimVal) {
                return super.getHeight();
            }
            return SVGOMAnimatedRect.this.getBaseVal().getHeight();
        }

        public void setX(float value) throws DOMException {
            throw SVGOMAnimatedRect.this.element.createDOMException((short)7, "readonly.length", null);
        }

        public void setY(float value) throws DOMException {
            throw SVGOMAnimatedRect.this.element.createDOMException((short)7, "readonly.length", null);
        }

        public void setWidth(float value) throws DOMException {
            throw SVGOMAnimatedRect.this.element.createDOMException((short)7, "readonly.length", null);
        }

        public void setHeight(float value) throws DOMException {
            throw SVGOMAnimatedRect.this.element.createDOMException((short)7, "readonly.length", null);
        }

        protected void setAnimatedValue(float x, float y, float w, float h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }
    }

    protected class BaseSVGRect
    extends SVGOMRect {
        protected boolean valid;

        protected BaseSVGRect() {
        }

        public void invalidate() {
            this.valid = false;
        }

        protected void reset() {
            try {
                SVGOMAnimatedRect.this.changing = true;
                SVGOMAnimatedRect.this.element.setAttributeNS(SVGOMAnimatedRect.this.namespaceURI, SVGOMAnimatedRect.this.localName, Float.toString(this.x) + ' ' + this.y + ' ' + this.w + ' ' + this.h);
            }
            finally {
                SVGOMAnimatedRect.this.changing = false;
            }
        }

        protected void revalidate() {
            if (this.valid) {
                return;
            }
            Attr attr = SVGOMAnimatedRect.this.element.getAttributeNodeNS(SVGOMAnimatedRect.this.namespaceURI, SVGOMAnimatedRect.this.localName);
            final String s = attr == null ? SVGOMAnimatedRect.this.defaultValue : attr.getValue();
            final float[] numbers = new float[4];
            NumberListParser p = new NumberListParser();
            p.setNumberListHandler((NumberListHandler)new DefaultNumberListHandler(){
                protected int count;

                public void endNumberList() {
                    if (this.count != 4) {
                        throw new LiveAttributeException((Element)((Object)SVGOMAnimatedRect.this.element), SVGOMAnimatedRect.this.localName, 1, s);
                    }
                }

                public void numberValue(float v) throws ParseException {
                    if (this.count < 4) {
                        numbers[this.count] = v;
                    }
                    if (v < 0.0f && (this.count == 2 || this.count == 3)) {
                        throw new LiveAttributeException((Element)((Object)SVGOMAnimatedRect.this.element), SVGOMAnimatedRect.this.localName, 1, s);
                    }
                    ++this.count;
                }
            });
            p.parse(s);
            this.x = numbers[0];
            this.y = numbers[1];
            this.w = numbers[2];
            this.h = numbers[3];
            this.valid = true;
        }

        public float getX() {
            this.revalidate();
            return this.x;
        }

        public void setX(float x) throws DOMException {
            this.x = x;
            this.reset();
        }

        public float getY() {
            this.revalidate();
            return this.y;
        }

        public void setY(float y) throws DOMException {
            this.y = y;
            this.reset();
        }

        public float getWidth() {
            this.revalidate();
            return this.w;
        }

        public void setWidth(float width) throws DOMException {
            this.w = width;
            this.reset();
        }

        public float getHeight() {
            this.revalidate();
            return this.h;
        }

        public void setHeight(float height) throws DOMException {
            this.h = height;
            this.reset();
        }
    }
}

