/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.engine.value.svg;

import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.CSSStylableElement;
import org.apache.batik.css.engine.StyleMap;
import org.apache.batik.css.engine.value.ListValue;
import org.apache.batik.css.engine.value.URIValue;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.svg.SVGColorManager;
import org.apache.batik.css.engine.value.svg.SVGValueConstants;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.dom.DOMException;

public class SVGPaintManager
extends SVGColorManager {
    public SVGPaintManager(String prop) {
        super(prop);
    }

    public SVGPaintManager(String prop, Value v) {
        super(prop, v);
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
        return 7;
    }

    @Override
    public Value createValue(LexicalUnit lu, CSSEngine engine) throws DOMException {
        switch (lu.getLexicalUnitType()) {
            case 35: {
                if (lu.getStringValue().equalsIgnoreCase("none")) {
                    return SVGValueConstants.NONE_VALUE;
                }
            }
            default: {
                return super.createValue(lu, engine);
            }
            case 24: 
        }
        String value = lu.getStringValue();
        String uri = SVGPaintManager.resolveURI(engine.getCSSBaseURI(), value);
        lu = lu.getNextLexicalUnit();
        if (lu == null) {
            return new URIValue(value, uri);
        }
        ListValue result = new ListValue(' ');
        result.append(new URIValue(value, uri));
        if (lu.getLexicalUnitType() == 35 && lu.getStringValue().equalsIgnoreCase("none")) {
            result.append(SVGValueConstants.NONE_VALUE);
            return result;
        }
        Value v = super.createValue(lu, engine);
        if (v.getCssValueType() == 3) {
            ListValue lv = (ListValue)v;
            for (int i = 0; i < lv.getLength(); ++i) {
                result.append(lv.item(i));
            }
        } else {
            result.append(v);
        }
        return result;
    }

    @Override
    public Value computeValue(CSSStylableElement elt, String pseudo, CSSEngine engine, int idx, StyleMap sm, Value value) {
        ListValue lv;
        Value v;
        if (value == SVGValueConstants.NONE_VALUE) {
            return value;
        }
        if (value.getCssValueType() == 2 && (v = (lv = (ListValue)value).item(0)).getPrimitiveType() == 20) {
            v = lv.item(1);
            if (v == SVGValueConstants.NONE_VALUE) {
                return value;
            }
            Value t = super.computeValue(elt, pseudo, engine, idx, sm, v);
            if (t != v) {
                ListValue result = new ListValue(' ');
                result.append(lv.item(0));
                result.append(t);
                if (lv.getLength() == 3) {
                    result.append(lv.item(1));
                }
                return result;
            }
            return value;
        }
        return super.computeValue(elt, pseudo, engine, idx, sm, value);
    }
}

