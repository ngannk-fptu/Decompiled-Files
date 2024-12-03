/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.engine.value.svg;

import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.value.AbstractValueManager;
import org.apache.batik.css.engine.value.FloatValue;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.svg.SVGValueConstants;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.dom.DOMException;

public abstract class GlyphOrientationManager
extends AbstractValueManager {
    @Override
    public boolean isInheritedProperty() {
        return true;
    }

    @Override
    public boolean isAnimatableProperty() {
        return false;
    }

    @Override
    public boolean isAdditiveProperty() {
        return false;
    }

    @Override
    public int getPropertyType() {
        return 5;
    }

    @Override
    public Value createValue(LexicalUnit lu, CSSEngine engine) throws DOMException {
        switch (lu.getLexicalUnitType()) {
            case 12: {
                return SVGValueConstants.INHERIT_VALUE;
            }
            case 28: {
                return new FloatValue(11, lu.getFloatValue());
            }
            case 29: {
                return new FloatValue(13, lu.getFloatValue());
            }
            case 30: {
                return new FloatValue(12, lu.getFloatValue());
            }
            case 13: {
                int n = lu.getIntegerValue();
                return new FloatValue(11, n);
            }
            case 14: {
                float n = lu.getFloatValue();
                return new FloatValue(11, n);
            }
        }
        throw this.createInvalidLexicalUnitDOMException(lu.getLexicalUnitType());
    }

    @Override
    public Value createFloatValue(short type, float floatValue) throws DOMException {
        switch (type) {
            case 11: 
            case 12: 
            case 13: {
                return new FloatValue(type, floatValue);
            }
        }
        throw this.createInvalidFloatValueDOMException(floatValue);
    }
}

