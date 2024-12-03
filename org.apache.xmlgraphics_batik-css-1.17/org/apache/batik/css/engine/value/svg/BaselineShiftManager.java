/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.w3c.css.sac.LexicalUnit
 */
package org.apache.batik.css.engine.value.svg;

import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.CSSStylableElement;
import org.apache.batik.css.engine.StyleMap;
import org.apache.batik.css.engine.value.FloatValue;
import org.apache.batik.css.engine.value.LengthManager;
import org.apache.batik.css.engine.value.StringMap;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.svg.SVGValueConstants;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.dom.DOMException;

public class BaselineShiftManager
extends LengthManager {
    protected static final StringMap values = new StringMap();

    @Override
    public boolean isInheritedProperty() {
        return false;
    }

    @Override
    public boolean isAnimatableProperty() {
        return true;
    }

    @Override
    public boolean isAdditiveProperty() {
        return false;
    }

    @Override
    public int getPropertyType() {
        return 40;
    }

    @Override
    public String getPropertyName() {
        return "baseline-shift";
    }

    @Override
    public Value getDefaultValue() {
        return SVGValueConstants.BASELINE_VALUE;
    }

    @Override
    public Value createValue(LexicalUnit lu, CSSEngine engine) throws DOMException {
        switch (lu.getLexicalUnitType()) {
            case 12: {
                return SVGValueConstants.INHERIT_VALUE;
            }
            case 35: {
                Object v = values.get(lu.getStringValue().toLowerCase().intern());
                if (v == null) {
                    throw this.createInvalidIdentifierDOMException(lu.getStringValue());
                }
                return (Value)v;
            }
        }
        return super.createValue(lu, engine);
    }

    @Override
    public Value createStringValue(short type, String value, CSSEngine engine) throws DOMException {
        if (type != 21) {
            throw this.createInvalidIdentifierDOMException(value);
        }
        Object v = values.get(value.toLowerCase().intern());
        if (v == null) {
            throw this.createInvalidIdentifierDOMException(value);
        }
        return (Value)v;
    }

    @Override
    public Value computeValue(CSSStylableElement elt, String pseudo, CSSEngine engine, int idx, StyleMap sm, Value value) {
        if (value.getPrimitiveType() == 2) {
            sm.putLineHeightRelative(idx, true);
            int fsi = engine.getLineHeightIndex();
            CSSStylableElement parent = (CSSStylableElement)elt.getParentNode();
            if (parent == null) {
                parent = elt;
            }
            Value fs = engine.getComputedStyle(parent, pseudo, fsi);
            float fsv = fs.getFloatValue();
            float v = value.getFloatValue();
            return new FloatValue(1, fsv * v / 100.0f);
        }
        return super.computeValue(elt, pseudo, engine, idx, sm, value);
    }

    @Override
    protected int getOrientation() {
        return 2;
    }

    static {
        values.put("baseline", SVGValueConstants.BASELINE_VALUE);
        values.put("sub", SVGValueConstants.SUB_VALUE);
        values.put("super", SVGValueConstants.SUPER_VALUE);
    }
}

