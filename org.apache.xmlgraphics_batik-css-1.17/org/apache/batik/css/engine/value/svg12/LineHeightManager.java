/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.w3c.css.sac.LexicalUnit
 */
package org.apache.batik.css.engine.value.svg12;

import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.CSSStylableElement;
import org.apache.batik.css.engine.StyleMap;
import org.apache.batik.css.engine.value.FloatValue;
import org.apache.batik.css.engine.value.LengthManager;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.svg12.LineHeightValue;
import org.apache.batik.css.engine.value.svg12.SVG12ValueConstants;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.dom.DOMException;

public class LineHeightManager
extends LengthManager {
    @Override
    public boolean isInheritedProperty() {
        return true;
    }

    @Override
    public boolean isAnimatableProperty() {
        return true;
    }

    @Override
    public boolean isAdditiveProperty() {
        return true;
    }

    @Override
    public int getPropertyType() {
        return 43;
    }

    @Override
    public String getPropertyName() {
        return "line-height";
    }

    @Override
    public Value getDefaultValue() {
        return SVG12ValueConstants.NORMAL_VALUE;
    }

    @Override
    public Value createValue(LexicalUnit lu, CSSEngine engine) throws DOMException {
        switch (lu.getLexicalUnitType()) {
            case 12: {
                return SVG12ValueConstants.INHERIT_VALUE;
            }
            case 35: {
                String s = lu.getStringValue().toLowerCase();
                if ("normal".equals(s)) {
                    return SVG12ValueConstants.NORMAL_VALUE;
                }
                throw this.createInvalidIdentifierDOMException(lu.getStringValue());
            }
        }
        return super.createValue(lu, engine);
    }

    @Override
    protected int getOrientation() {
        return 1;
    }

    @Override
    public Value computeValue(CSSStylableElement elt, String pseudo, CSSEngine engine, int idx, StyleMap sm, Value value) {
        if (value.getCssValueType() != 1) {
            return value;
        }
        switch (value.getPrimitiveType()) {
            case 1: {
                return new LineHeightValue(1, value.getFloatValue(), true);
            }
            case 2: {
                float v = value.getFloatValue();
                int fsidx = engine.getFontSizeIndex();
                float fs = engine.getComputedStyle(elt, pseudo, fsidx).getFloatValue();
                return new FloatValue(1, v * fs * 0.01f);
            }
        }
        return super.computeValue(elt, pseudo, engine, idx, sm, value);
    }
}

