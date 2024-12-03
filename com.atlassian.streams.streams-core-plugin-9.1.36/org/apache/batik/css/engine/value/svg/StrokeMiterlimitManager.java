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

public class StrokeMiterlimitManager
extends AbstractValueManager {
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
        return 25;
    }

    @Override
    public String getPropertyName() {
        return "stroke-miterlimit";
    }

    @Override
    public Value getDefaultValue() {
        return SVGValueConstants.NUMBER_4;
    }

    @Override
    public Value createValue(LexicalUnit lu, CSSEngine engine) throws DOMException {
        switch (lu.getLexicalUnitType()) {
            case 12: {
                return SVGValueConstants.INHERIT_VALUE;
            }
            case 13: {
                return new FloatValue(1, lu.getIntegerValue());
            }
            case 14: {
                return new FloatValue(1, lu.getFloatValue());
            }
        }
        throw this.createInvalidLexicalUnitDOMException(lu.getLexicalUnitType());
    }

    @Override
    public Value createFloatValue(short unitType, float floatValue) throws DOMException {
        if (unitType == 1) {
            return new FloatValue(unitType, floatValue);
        }
        throw this.createInvalidFloatTypeDOMException(unitType);
    }
}

