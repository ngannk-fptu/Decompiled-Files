/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.engine.value.css2;

import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.value.AbstractValueManager;
import org.apache.batik.css.engine.value.FloatValue;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.ValueConstants;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.dom.DOMException;

public class FontSizeAdjustManager
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
        return false;
    }

    @Override
    public int getPropertyType() {
        return 44;
    }

    @Override
    public String getPropertyName() {
        return "font-size-adjust";
    }

    @Override
    public Value getDefaultValue() {
        return ValueConstants.NONE_VALUE;
    }

    @Override
    public Value createValue(LexicalUnit lu, CSSEngine engine) throws DOMException {
        switch (lu.getLexicalUnitType()) {
            case 12: {
                return ValueConstants.INHERIT_VALUE;
            }
            case 13: {
                return new FloatValue(1, lu.getIntegerValue());
            }
            case 14: {
                return new FloatValue(1, lu.getFloatValue());
            }
            case 35: {
                if (lu.getStringValue().equalsIgnoreCase("none")) {
                    return ValueConstants.NONE_VALUE;
                }
                throw this.createInvalidIdentifierDOMException(lu.getStringValue());
            }
        }
        throw this.createInvalidLexicalUnitDOMException(lu.getLexicalUnitType());
    }

    @Override
    public Value createStringValue(short type, String value, CSSEngine engine) throws DOMException {
        if (type != 21) {
            throw this.createInvalidStringTypeDOMException(type);
        }
        if (value.equalsIgnoreCase("none")) {
            return ValueConstants.NONE_VALUE;
        }
        throw this.createInvalidIdentifierDOMException(value);
    }

    @Override
    public Value createFloatValue(short type, float floatValue) throws DOMException {
        if (type == 1) {
            return new FloatValue(type, floatValue);
        }
        throw this.createInvalidFloatTypeDOMException(type);
    }
}

