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
import org.apache.batik.css.engine.value.LengthManager;
import org.apache.batik.css.engine.value.ListValue;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.svg.SVGValueConstants;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.dom.DOMException;

public class EnableBackgroundManager
extends LengthManager {
    protected int orientation;

    @Override
    public boolean isInheritedProperty() {
        return false;
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
        return 23;
    }

    @Override
    public String getPropertyName() {
        return "enable-background";
    }

    @Override
    public Value getDefaultValue() {
        return SVGValueConstants.ACCUMULATE_VALUE;
    }

    @Override
    public Value createValue(LexicalUnit lu, CSSEngine engine) throws DOMException {
        switch (lu.getLexicalUnitType()) {
            case 12: {
                return SVGValueConstants.INHERIT_VALUE;
            }
            default: {
                throw this.createInvalidLexicalUnitDOMException(lu.getLexicalUnitType());
            }
            case 35: 
        }
        String id = lu.getStringValue().toLowerCase().intern();
        if (id == "accumulate") {
            return SVGValueConstants.ACCUMULATE_VALUE;
        }
        if (id != "new") {
            throw this.createInvalidIdentifierDOMException(id);
        }
        ListValue result = new ListValue(' ');
        result.append(SVGValueConstants.NEW_VALUE);
        lu = lu.getNextLexicalUnit();
        if (lu == null) {
            return result;
        }
        result.append(super.createValue(lu, engine));
        for (int i = 1; i < 4; ++i) {
            if ((lu = lu.getNextLexicalUnit()) == null) {
                throw this.createMalformedLexicalUnitDOMException();
            }
            result.append(super.createValue(lu, engine));
        }
        return result;
    }

    @Override
    public Value createStringValue(short type, String value, CSSEngine engine) {
        if (type != 21) {
            throw this.createInvalidStringTypeDOMException(type);
        }
        if (!value.equalsIgnoreCase("accumulate")) {
            throw this.createInvalidIdentifierDOMException(value);
        }
        return SVGValueConstants.ACCUMULATE_VALUE;
    }

    @Override
    public Value createFloatValue(short unitType, float floatValue) throws DOMException {
        throw this.createDOMException();
    }

    @Override
    public Value computeValue(CSSStylableElement elt, String pseudo, CSSEngine engine, int idx, StyleMap sm, Value value) {
        ListValue lv;
        if (value.getCssValueType() == 2 && (lv = (ListValue)value).getLength() == 5) {
            Value lv1 = lv.item(1);
            this.orientation = 0;
            Value v1 = super.computeValue(elt, pseudo, engine, idx, sm, lv1);
            Value lv2 = lv.item(2);
            this.orientation = 1;
            Value v2 = super.computeValue(elt, pseudo, engine, idx, sm, lv2);
            Value lv3 = lv.item(3);
            this.orientation = 0;
            Value v3 = super.computeValue(elt, pseudo, engine, idx, sm, lv3);
            Value lv4 = lv.item(4);
            this.orientation = 1;
            Value v4 = super.computeValue(elt, pseudo, engine, idx, sm, lv4);
            if (lv1 != v1 || lv2 != v2 || lv3 != v3 || lv4 != v4) {
                ListValue result = new ListValue(' ');
                result.append(lv.item(0));
                result.append(v1);
                result.append(v2);
                result.append(v3);
                result.append(v4);
                return result;
            }
        }
        return value;
    }

    @Override
    protected int getOrientation() {
        return this.orientation;
    }
}

