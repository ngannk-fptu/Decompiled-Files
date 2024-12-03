/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.svg.LiveAttributeException
 *  org.apache.batik.dom.svg.LiveAttributeValue
 *  org.w3c.dom.svg.SVGAnimatedLength
 *  org.w3c.dom.svg.SVGLength
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.AbstractElement;
import org.apache.batik.anim.dom.AbstractSVGAnimatedValue;
import org.apache.batik.anim.dom.AbstractSVGLength;
import org.apache.batik.anim.dom.AnimationTarget;
import org.apache.batik.anim.dom.SVGOMElement;
import org.apache.batik.anim.values.AnimatableLengthValue;
import org.apache.batik.anim.values.AnimatableValue;
import org.apache.batik.dom.svg.LiveAttributeException;
import org.apache.batik.dom.svg.LiveAttributeValue;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGLength;

public abstract class AbstractSVGAnimatedLength
extends AbstractSVGAnimatedValue
implements SVGAnimatedLength,
LiveAttributeValue {
    public static final short HORIZONTAL_LENGTH = 2;
    public static final short VERTICAL_LENGTH = 1;
    public static final short OTHER_LENGTH = 0;
    protected short direction;
    protected BaseSVGLength baseVal;
    protected AnimSVGLength animVal;
    protected boolean changing;
    protected boolean nonNegative;

    public AbstractSVGAnimatedLength(AbstractElement elt, String ns, String ln, short dir, boolean nonneg) {
        super(elt, ns, ln);
        this.direction = dir;
        this.nonNegative = nonneg;
    }

    protected abstract String getDefaultValue();

    public SVGLength getBaseVal() {
        if (this.baseVal == null) {
            this.baseVal = new BaseSVGLength(this.direction);
        }
        return this.baseVal;
    }

    public SVGLength getAnimVal() {
        if (this.animVal == null) {
            this.animVal = new AnimSVGLength(this.direction);
        }
        return this.animVal;
    }

    public float getCheckedValue() {
        if (this.hasAnimVal) {
            if (this.animVal == null) {
                this.animVal = new AnimSVGLength(this.direction);
            }
            if (this.nonNegative && this.animVal.value < 0.0f) {
                throw new LiveAttributeException((Element)((Object)this.element), this.localName, 2, this.animVal.getValueAsString());
            }
            return this.animVal.getValue();
        }
        if (this.baseVal == null) {
            this.baseVal = new BaseSVGLength(this.direction);
        }
        this.baseVal.revalidate();
        if (this.baseVal.missing) {
            throw new LiveAttributeException((Element)((Object)this.element), this.localName, 0, null);
        }
        if (this.baseVal.unitType == 0) {
            throw new LiveAttributeException((Element)((Object)this.element), this.localName, 1, this.baseVal.getValueAsString());
        }
        if (this.nonNegative && this.baseVal.value < 0.0f) {
            throw new LiveAttributeException((Element)((Object)this.element), this.localName, 2, this.baseVal.getValueAsString());
        }
        return this.baseVal.getValue();
    }

    @Override
    protected void updateAnimatedValue(AnimatableValue val) {
        if (val == null) {
            this.hasAnimVal = false;
        } else {
            this.hasAnimVal = true;
            AnimatableLengthValue animLength = (AnimatableLengthValue)val;
            if (this.animVal == null) {
                this.animVal = new AnimSVGLength(this.direction);
            }
            this.animVal.setAnimatedValue(animLength.getLengthType(), animLength.getLengthValue());
        }
        this.fireAnimatedAttributeListeners();
    }

    @Override
    public AnimatableValue getUnderlyingValue(AnimationTarget target) {
        SVGLength base = this.getBaseVal();
        return new AnimatableLengthValue(target, base.getUnitType(), base.getValueInSpecifiedUnits(), target.getPercentageInterpretation(this.getNamespaceURI(), this.getLocalName(), false));
    }

    public void attrAdded(Attr node, String newv) {
        this.attrChanged();
    }

    public void attrModified(Attr node, String oldv, String newv) {
        this.attrChanged();
    }

    public void attrRemoved(Attr node, String oldv) {
        this.attrChanged();
    }

    protected void attrChanged() {
        if (!this.changing && this.baseVal != null) {
            this.baseVal.invalidate();
        }
        this.fireBaseAttributeListeners();
        if (!this.hasAnimVal) {
            this.fireAnimatedAttributeListeners();
        }
    }

    protected class AnimSVGLength
    extends AbstractSVGLength {
        public AnimSVGLength(short direction) {
            super(direction);
        }

        @Override
        public short getUnitType() {
            if (AbstractSVGAnimatedLength.this.hasAnimVal) {
                return super.getUnitType();
            }
            return AbstractSVGAnimatedLength.this.getBaseVal().getUnitType();
        }

        @Override
        public float getValue() {
            if (AbstractSVGAnimatedLength.this.hasAnimVal) {
                return super.getValue();
            }
            return AbstractSVGAnimatedLength.this.getBaseVal().getValue();
        }

        @Override
        public float getValueInSpecifiedUnits() {
            if (AbstractSVGAnimatedLength.this.hasAnimVal) {
                return super.getValueInSpecifiedUnits();
            }
            return AbstractSVGAnimatedLength.this.getBaseVal().getValueInSpecifiedUnits();
        }

        @Override
        public String getValueAsString() {
            if (AbstractSVGAnimatedLength.this.hasAnimVal) {
                return super.getValueAsString();
            }
            return AbstractSVGAnimatedLength.this.getBaseVal().getValueAsString();
        }

        @Override
        public void setValue(float value) throws DOMException {
            throw AbstractSVGAnimatedLength.this.element.createDOMException((short)7, "readonly.length", null);
        }

        @Override
        public void setValueInSpecifiedUnits(float value) throws DOMException {
            throw AbstractSVGAnimatedLength.this.element.createDOMException((short)7, "readonly.length", null);
        }

        @Override
        public void setValueAsString(String value) throws DOMException {
            throw AbstractSVGAnimatedLength.this.element.createDOMException((short)7, "readonly.length", null);
        }

        @Override
        public void newValueSpecifiedUnits(short unit, float value) {
            throw AbstractSVGAnimatedLength.this.element.createDOMException((short)7, "readonly.length", null);
        }

        @Override
        public void convertToSpecifiedUnits(short unit) {
            throw AbstractSVGAnimatedLength.this.element.createDOMException((short)7, "readonly.length", null);
        }

        @Override
        protected SVGOMElement getAssociatedElement() {
            return (SVGOMElement)AbstractSVGAnimatedLength.this.element;
        }

        protected void setAnimatedValue(int type, float val) {
            super.newValueSpecifiedUnits((short)type, val);
        }
    }

    protected class BaseSVGLength
    extends AbstractSVGLength {
        protected boolean valid;
        protected boolean missing;

        public BaseSVGLength(short direction) {
            super(direction);
        }

        public void invalidate() {
            this.valid = false;
        }

        @Override
        protected void reset() {
            try {
                AbstractSVGAnimatedLength.this.changing = true;
                this.valid = true;
                String value = this.getValueAsString();
                AbstractSVGAnimatedLength.this.element.setAttributeNS(AbstractSVGAnimatedLength.this.namespaceURI, AbstractSVGAnimatedLength.this.localName, value);
            }
            finally {
                AbstractSVGAnimatedLength.this.changing = false;
            }
        }

        @Override
        protected void revalidate() {
            String s;
            if (this.valid) {
                return;
            }
            this.missing = false;
            this.valid = true;
            Attr attr = AbstractSVGAnimatedLength.this.element.getAttributeNodeNS(AbstractSVGAnimatedLength.this.namespaceURI, AbstractSVGAnimatedLength.this.localName);
            if (attr == null) {
                s = AbstractSVGAnimatedLength.this.getDefaultValue();
                if (s == null) {
                    this.missing = true;
                    return;
                }
            } else {
                s = attr.getValue();
            }
            this.parse(s);
        }

        @Override
        protected SVGOMElement getAssociatedElement() {
            return (SVGOMElement)AbstractSVGAnimatedLength.this.element;
        }
    }
}

