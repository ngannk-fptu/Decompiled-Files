/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.engine.value.svg;

import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.CSSStylableElement;
import org.apache.batik.css.engine.StyleMap;
import org.apache.batik.css.engine.value.LengthManager;
import org.apache.batik.css.engine.value.ListValue;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.svg.SVGValueConstants;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.dom.DOMException;

public class StrokeDasharrayManager
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
        return false;
    }

    @Override
    public int getPropertyType() {
        return 34;
    }

    @Override
    public String getPropertyName() {
        return "stroke-dasharray";
    }

    @Override
    public Value getDefaultValue() {
        return SVGValueConstants.NONE_VALUE;
    }

    @Override
    public Value createValue(LexicalUnit lu, CSSEngine engine) throws DOMException {
        switch (lu.getLexicalUnitType()) {
            case 12: {
                return SVGValueConstants.INHERIT_VALUE;
            }
            case 35: {
                if (lu.getStringValue().equalsIgnoreCase("none")) {
                    return SVGValueConstants.NONE_VALUE;
                }
                throw this.createInvalidIdentifierDOMException(lu.getStringValue());
            }
        }
        ListValue lv = new ListValue(' ');
        do {
            Value v = super.createValue(lu, engine);
            lv.append(v);
            lu = lu.getNextLexicalUnit();
            if (lu == null || lu.getLexicalUnitType() != 0) continue;
            lu = lu.getNextLexicalUnit();
        } while (lu != null);
        return lv;
    }

    @Override
    public Value createStringValue(short type, String value, CSSEngine engine) throws DOMException {
        if (type != 21) {
            throw this.createInvalidStringTypeDOMException(type);
        }
        if (value.equalsIgnoreCase("none")) {
            return SVGValueConstants.NONE_VALUE;
        }
        throw this.createInvalidIdentifierDOMException(value);
    }

    @Override
    public Value computeValue(CSSStylableElement elt, String pseudo, CSSEngine engine, int idx, StyleMap sm, Value value) {
        switch (value.getCssValueType()) {
            case 1: {
                return value;
            }
        }
        ListValue lv = (ListValue)value;
        ListValue result = new ListValue(' ');
        for (int i = 0; i < lv.getLength(); ++i) {
            result.append(super.computeValue(elt, pseudo, engine, idx, sm, lv.item(i)));
        }
        return result;
    }

    @Override
    protected int getOrientation() {
        return 2;
    }
}

