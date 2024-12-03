/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.engine.value.svg;

import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.value.LengthManager;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.svg.SVGValueConstants;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.dom.DOMException;

public class SpacingManager
extends LengthManager {
    protected String property;

    public SpacingManager(String prop) {
        this.property = prop;
    }

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
        return 42;
    }

    @Override
    public String getPropertyName() {
        return this.property;
    }

    @Override
    public Value getDefaultValue() {
        return SVGValueConstants.NORMAL_VALUE;
    }

    @Override
    public Value createValue(LexicalUnit lu, CSSEngine engine) throws DOMException {
        switch (lu.getLexicalUnitType()) {
            case 12: {
                return SVGValueConstants.INHERIT_VALUE;
            }
            case 35: {
                if (lu.getStringValue().equalsIgnoreCase("normal")) {
                    return SVGValueConstants.NORMAL_VALUE;
                }
                throw this.createInvalidIdentifierDOMException(lu.getStringValue());
            }
        }
        return super.createValue(lu, engine);
    }

    @Override
    public Value createStringValue(short type, String value, CSSEngine engine) throws DOMException {
        if (type != 21) {
            throw this.createInvalidStringTypeDOMException(type);
        }
        if (value.equalsIgnoreCase("normal")) {
            return SVGValueConstants.NORMAL_VALUE;
        }
        throw this.createInvalidIdentifierDOMException(value);
    }

    @Override
    protected int getOrientation() {
        return 2;
    }
}

