/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.svg.LiveAttributeException
 *  org.w3c.dom.svg.SVGAnimatedEnumeration
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.AbstractElement;
import org.apache.batik.anim.dom.AbstractSVGAnimatedValue;
import org.apache.batik.anim.dom.AnimationTarget;
import org.apache.batik.anim.values.AnimatableStringValue;
import org.apache.batik.anim.values.AnimatableValue;
import org.apache.batik.dom.svg.LiveAttributeException;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGAnimatedEnumeration;

public class SVGOMAnimatedEnumeration
extends AbstractSVGAnimatedValue
implements SVGAnimatedEnumeration {
    protected String[] values;
    protected short defaultValue;
    protected boolean valid;
    protected short baseVal;
    protected short animVal;
    protected boolean changing;

    public SVGOMAnimatedEnumeration(AbstractElement elt, String ns, String ln, String[] val, short def) {
        super(elt, ns, ln);
        this.values = val;
        this.defaultValue = def;
    }

    public short getBaseVal() {
        if (!this.valid) {
            this.update();
        }
        return this.baseVal;
    }

    public String getBaseValAsString() {
        if (!this.valid) {
            this.update();
        }
        return this.values[this.baseVal];
    }

    protected void update() {
        String val = this.element.getAttributeNS(this.namespaceURI, this.localName);
        this.baseVal = val.length() == 0 ? this.defaultValue : this.getEnumerationNumber(val);
        this.valid = true;
    }

    protected short getEnumerationNumber(String s) {
        for (short i = 0; i < this.values.length; i = (short)(i + 1)) {
            if (!s.equals(this.values[i])) continue;
            return i;
        }
        return 0;
    }

    public void setBaseVal(short baseVal) throws DOMException {
        if (baseVal >= 0 && baseVal < this.values.length) {
            try {
                this.baseVal = baseVal;
                this.valid = true;
                this.changing = true;
                this.element.setAttributeNS(this.namespaceURI, this.localName, this.values[baseVal]);
            }
            finally {
                this.changing = false;
            }
        }
    }

    public short getAnimVal() {
        if (this.hasAnimVal) {
            return this.animVal;
        }
        if (!this.valid) {
            this.update();
        }
        return this.baseVal;
    }

    public short getCheckedVal() {
        if (this.hasAnimVal) {
            return this.animVal;
        }
        if (!this.valid) {
            this.update();
        }
        if (this.baseVal == 0) {
            throw new LiveAttributeException((Element)((Object)this.element), this.localName, 1, this.getBaseValAsString());
        }
        return this.baseVal;
    }

    @Override
    public AnimatableValue getUnderlyingValue(AnimationTarget target) {
        return new AnimatableStringValue(target, this.getBaseValAsString());
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

    @Override
    protected void updateAnimatedValue(AnimatableValue val) {
        if (val == null) {
            this.hasAnimVal = false;
        } else {
            this.hasAnimVal = true;
            this.animVal = this.getEnumerationNumber(((AnimatableStringValue)val).getString());
            this.fireAnimatedAttributeListeners();
        }
        this.fireAnimatedAttributeListeners();
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
}

