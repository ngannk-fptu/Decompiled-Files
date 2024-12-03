/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.svg.SVGOMAngle
 *  org.w3c.dom.svg.SVGAngle
 *  org.w3c.dom.svg.SVGAnimatedAngle
 *  org.w3c.dom.svg.SVGAnimatedEnumeration
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.AbstractElement;
import org.apache.batik.anim.dom.AbstractSVGAnimatedValue;
import org.apache.batik.anim.dom.AnimationTarget;
import org.apache.batik.anim.values.AnimatableValue;
import org.apache.batik.dom.svg.SVGOMAngle;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGAngle;
import org.w3c.dom.svg.SVGAnimatedAngle;
import org.w3c.dom.svg.SVGAnimatedEnumeration;

public class SVGOMAnimatedMarkerOrientValue
extends AbstractSVGAnimatedValue {
    protected boolean valid;
    protected AnimatedAngle animatedAngle = new AnimatedAngle();
    protected AnimatedEnumeration animatedEnumeration = new AnimatedEnumeration();
    protected BaseSVGAngle baseAngleVal;
    protected short baseEnumerationVal;
    protected AnimSVGAngle animAngleVal;
    protected short animEnumerationVal;
    protected boolean changing;

    public SVGOMAnimatedMarkerOrientValue(AbstractElement elt, String ns, String ln) {
        super(elt, ns, ln);
    }

    @Override
    protected void updateAnimatedValue(AnimatableValue val) {
        throw new UnsupportedOperationException("Animation of marker orient value is not implemented");
    }

    @Override
    public AnimatableValue getUnderlyingValue(AnimationTarget target) {
        throw new UnsupportedOperationException("Animation of marker orient value is not implemented");
    }

    public void attrAdded(Attr node, String newv) {
        if (!this.changing) {
            this.valid = false;
        }
        this.fireBaseAttributeListeners();
        if (!this.hasAnimVal) {
            this.fireAnimatedAttributeListeners();
        }
    }

    public void attrModified(Attr node, String oldv, String newv) {
        if (!this.changing) {
            this.valid = false;
        }
        this.fireBaseAttributeListeners();
        if (!this.hasAnimVal) {
            this.fireAnimatedAttributeListeners();
        }
    }

    public void attrRemoved(Attr node, String oldv) {
        if (!this.changing) {
            this.valid = false;
        }
        this.fireBaseAttributeListeners();
        if (!this.hasAnimVal) {
            this.fireAnimatedAttributeListeners();
        }
    }

    public void setAnimatedValueToAngle(short unitType, float value) {
        this.hasAnimVal = true;
        this.animAngleVal.setAnimatedValue(unitType, value);
        this.animEnumerationVal = (short)2;
        this.fireAnimatedAttributeListeners();
    }

    public void setAnimatedValueToAuto() {
        this.hasAnimVal = true;
        this.animAngleVal.setAnimatedValue(1, 0.0f);
        this.animEnumerationVal = 1;
        this.fireAnimatedAttributeListeners();
    }

    public void resetAnimatedValue() {
        this.hasAnimVal = false;
        this.fireAnimatedAttributeListeners();
    }

    public SVGAnimatedAngle getAnimatedAngle() {
        return this.animatedAngle;
    }

    public SVGAnimatedEnumeration getAnimatedEnumeration() {
        return this.animatedEnumeration;
    }

    protected class AnimatedEnumeration
    implements SVGAnimatedEnumeration {
        protected AnimatedEnumeration() {
        }

        public short getBaseVal() {
            if (SVGOMAnimatedMarkerOrientValue.this.baseAngleVal == null) {
                SVGOMAnimatedMarkerOrientValue.this.baseAngleVal = new BaseSVGAngle();
            }
            SVGOMAnimatedMarkerOrientValue.this.baseAngleVal.revalidate();
            return SVGOMAnimatedMarkerOrientValue.this.baseEnumerationVal;
        }

        public void setBaseVal(short baseVal) throws DOMException {
            if (baseVal == 1) {
                SVGOMAnimatedMarkerOrientValue.this.baseEnumerationVal = baseVal;
                if (SVGOMAnimatedMarkerOrientValue.this.baseAngleVal == null) {
                    SVGOMAnimatedMarkerOrientValue.this.baseAngleVal = new BaseSVGAngle();
                }
                SVGOMAnimatedMarkerOrientValue.this.baseAngleVal.setUnitType((short)1);
                SVGOMAnimatedMarkerOrientValue.this.baseAngleVal.setValue(0.0f);
                SVGOMAnimatedMarkerOrientValue.this.baseAngleVal.reset();
            } else if (baseVal == 2) {
                SVGOMAnimatedMarkerOrientValue.this.baseEnumerationVal = baseVal;
                if (SVGOMAnimatedMarkerOrientValue.this.baseAngleVal == null) {
                    SVGOMAnimatedMarkerOrientValue.this.baseAngleVal = new BaseSVGAngle();
                }
                SVGOMAnimatedMarkerOrientValue.this.baseAngleVal.reset();
            }
        }

        public short getAnimVal() {
            if (SVGOMAnimatedMarkerOrientValue.this.hasAnimVal) {
                return SVGOMAnimatedMarkerOrientValue.this.animEnumerationVal;
            }
            if (SVGOMAnimatedMarkerOrientValue.this.baseAngleVal == null) {
                SVGOMAnimatedMarkerOrientValue.this.baseAngleVal = new BaseSVGAngle();
            }
            SVGOMAnimatedMarkerOrientValue.this.baseAngleVal.revalidate();
            return SVGOMAnimatedMarkerOrientValue.this.baseEnumerationVal;
        }
    }

    protected class AnimatedAngle
    implements SVGAnimatedAngle {
        protected AnimatedAngle() {
        }

        public SVGAngle getBaseVal() {
            if (SVGOMAnimatedMarkerOrientValue.this.baseAngleVal == null) {
                SVGOMAnimatedMarkerOrientValue.this.baseAngleVal = new BaseSVGAngle();
            }
            return SVGOMAnimatedMarkerOrientValue.this.baseAngleVal;
        }

        public SVGAngle getAnimVal() {
            if (SVGOMAnimatedMarkerOrientValue.this.animAngleVal == null) {
                SVGOMAnimatedMarkerOrientValue.this.animAngleVal = new AnimSVGAngle();
            }
            return SVGOMAnimatedMarkerOrientValue.this.animAngleVal;
        }
    }

    protected class AnimSVGAngle
    extends SVGOMAngle {
        protected AnimSVGAngle() {
        }

        public short getUnitType() {
            if (SVGOMAnimatedMarkerOrientValue.this.hasAnimVal) {
                return super.getUnitType();
            }
            return SVGOMAnimatedMarkerOrientValue.this.animatedAngle.getBaseVal().getUnitType();
        }

        public float getValue() {
            if (SVGOMAnimatedMarkerOrientValue.this.hasAnimVal) {
                return super.getValue();
            }
            return SVGOMAnimatedMarkerOrientValue.this.animatedAngle.getBaseVal().getValue();
        }

        public float getValueInSpecifiedUnits() {
            if (SVGOMAnimatedMarkerOrientValue.this.hasAnimVal) {
                return super.getValueInSpecifiedUnits();
            }
            return SVGOMAnimatedMarkerOrientValue.this.animatedAngle.getBaseVal().getValueInSpecifiedUnits();
        }

        public String getValueAsString() {
            if (SVGOMAnimatedMarkerOrientValue.this.hasAnimVal) {
                return super.getValueAsString();
            }
            return SVGOMAnimatedMarkerOrientValue.this.animatedAngle.getBaseVal().getValueAsString();
        }

        public void setValue(float value) throws DOMException {
            throw SVGOMAnimatedMarkerOrientValue.this.element.createDOMException((short)7, "readonly.angle", null);
        }

        public void setValueInSpecifiedUnits(float value) throws DOMException {
            throw SVGOMAnimatedMarkerOrientValue.this.element.createDOMException((short)7, "readonly.angle", null);
        }

        public void setValueAsString(String value) throws DOMException {
            throw SVGOMAnimatedMarkerOrientValue.this.element.createDOMException((short)7, "readonly.angle", null);
        }

        public void newValueSpecifiedUnits(short unit, float value) {
            throw SVGOMAnimatedMarkerOrientValue.this.element.createDOMException((short)7, "readonly.angle", null);
        }

        public void convertToSpecifiedUnits(short unit) {
            throw SVGOMAnimatedMarkerOrientValue.this.element.createDOMException((short)7, "readonly.angle", null);
        }

        protected void setAnimatedValue(int type, float val) {
            super.newValueSpecifiedUnits((short)type, val);
        }
    }

    protected class BaseSVGAngle
    extends SVGOMAngle {
        protected BaseSVGAngle() {
        }

        public void invalidate() {
            SVGOMAnimatedMarkerOrientValue.this.valid = false;
        }

        protected void reset() {
            try {
                String value;
                SVGOMAnimatedMarkerOrientValue.this.changing = true;
                SVGOMAnimatedMarkerOrientValue.this.valid = true;
                if (SVGOMAnimatedMarkerOrientValue.this.baseEnumerationVal == 2) {
                    value = this.getValueAsString();
                } else if (SVGOMAnimatedMarkerOrientValue.this.baseEnumerationVal == 1) {
                    value = "auto";
                } else {
                    return;
                }
                SVGOMAnimatedMarkerOrientValue.this.element.setAttributeNS(SVGOMAnimatedMarkerOrientValue.this.namespaceURI, SVGOMAnimatedMarkerOrientValue.this.localName, value);
            }
            finally {
                SVGOMAnimatedMarkerOrientValue.this.changing = false;
            }
        }

        protected void revalidate() {
            if (!SVGOMAnimatedMarkerOrientValue.this.valid) {
                Attr attr = SVGOMAnimatedMarkerOrientValue.this.element.getAttributeNodeNS(SVGOMAnimatedMarkerOrientValue.this.namespaceURI, SVGOMAnimatedMarkerOrientValue.this.localName);
                if (attr == null) {
                    this.setUnitType((short)1);
                    this.value = 0.0f;
                } else {
                    this.parse(attr.getValue());
                }
                SVGOMAnimatedMarkerOrientValue.this.valid = true;
            }
        }

        protected void parse(String s) {
            if (s.equals("auto")) {
                this.setUnitType((short)1);
                this.value = 0.0f;
                SVGOMAnimatedMarkerOrientValue.this.baseEnumerationVal = 1;
            } else {
                super.parse(s);
                SVGOMAnimatedMarkerOrientValue.this.baseEnumerationVal = this.getUnitType() == 0 ? (short)0 : (short)2;
            }
        }
    }
}

