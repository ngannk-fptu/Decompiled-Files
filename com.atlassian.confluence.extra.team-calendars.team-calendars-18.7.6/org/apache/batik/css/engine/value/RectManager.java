/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.w3c.css.sac.LexicalUnit
 */
package org.apache.batik.css.engine.value;

import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.CSSStylableElement;
import org.apache.batik.css.engine.StyleMap;
import org.apache.batik.css.engine.value.FloatValue;
import org.apache.batik.css.engine.value.LengthManager;
import org.apache.batik.css.engine.value.Messages;
import org.apache.batik.css.engine.value.RectValue;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.ValueConstants;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.dom.DOMException;

public abstract class RectManager
extends LengthManager {
    protected int orientation;

    @Override
    public Value createValue(LexicalUnit lu, CSSEngine engine) throws DOMException {
        switch (lu.getLexicalUnitType()) {
            case 41: {
                if (!lu.getFunctionName().equalsIgnoreCase("rect")) break;
            }
            case 38: {
                lu = lu.getParameters();
                Value top = this.createRectComponent(lu);
                lu = lu.getNextLexicalUnit();
                if (lu == null || lu.getLexicalUnitType() != 0) {
                    throw this.createMalformedRectDOMException();
                }
                lu = lu.getNextLexicalUnit();
                Value right = this.createRectComponent(lu);
                if ((lu = lu.getNextLexicalUnit()) == null || lu.getLexicalUnitType() != 0) {
                    throw this.createMalformedRectDOMException();
                }
                lu = lu.getNextLexicalUnit();
                Value bottom = this.createRectComponent(lu);
                if ((lu = lu.getNextLexicalUnit()) == null || lu.getLexicalUnitType() != 0) {
                    throw this.createMalformedRectDOMException();
                }
                lu = lu.getNextLexicalUnit();
                Value left = this.createRectComponent(lu);
                return new RectValue(top, right, bottom, left);
            }
        }
        throw this.createMalformedRectDOMException();
    }

    private Value createRectComponent(LexicalUnit lu) throws DOMException {
        switch (lu.getLexicalUnitType()) {
            case 35: {
                if (!lu.getStringValue().equalsIgnoreCase("auto")) break;
                return ValueConstants.AUTO_VALUE;
            }
            case 15: {
                return new FloatValue(3, lu.getFloatValue());
            }
            case 16: {
                return new FloatValue(4, lu.getFloatValue());
            }
            case 17: {
                return new FloatValue(5, lu.getFloatValue());
            }
            case 19: {
                return new FloatValue(6, lu.getFloatValue());
            }
            case 20: {
                return new FloatValue(7, lu.getFloatValue());
            }
            case 18: {
                return new FloatValue(8, lu.getFloatValue());
            }
            case 21: {
                return new FloatValue(9, lu.getFloatValue());
            }
            case 22: {
                return new FloatValue(10, lu.getFloatValue());
            }
            case 13: {
                return new FloatValue(1, lu.getIntegerValue());
            }
            case 14: {
                return new FloatValue(1, lu.getFloatValue());
            }
            case 23: {
                return new FloatValue(2, lu.getFloatValue());
            }
        }
        throw this.createMalformedRectDOMException();
    }

    @Override
    public Value computeValue(CSSStylableElement elt, String pseudo, CSSEngine engine, int idx, StyleMap sm, Value value) {
        if (value.getCssValueType() != 1) {
            return value;
        }
        if (value.getPrimitiveType() != 24) {
            return value;
        }
        RectValue rect = (RectValue)value;
        this.orientation = 1;
        Value top = super.computeValue(elt, pseudo, engine, idx, sm, rect.getTop());
        Value bottom = super.computeValue(elt, pseudo, engine, idx, sm, rect.getBottom());
        this.orientation = 0;
        Value left = super.computeValue(elt, pseudo, engine, idx, sm, rect.getLeft());
        Value right = super.computeValue(elt, pseudo, engine, idx, sm, rect.getRight());
        if (top != rect.getTop() || right != rect.getRight() || bottom != rect.getBottom() || left != rect.getLeft()) {
            return new RectValue(top, right, bottom, left);
        }
        return value;
    }

    @Override
    protected int getOrientation() {
        return this.orientation;
    }

    private DOMException createMalformedRectDOMException() {
        Object[] p = new Object[]{this.getPropertyName()};
        String s = Messages.formatMessage("malformed.rect", p);
        return new DOMException(12, s);
    }
}

