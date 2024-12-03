/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.w3c.css.sac.LexicalUnit
 */
package org.apache.batik.css.engine.value;

import org.apache.batik.css.engine.CSSContext;
import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.CSSStylableElement;
import org.apache.batik.css.engine.StyleMap;
import org.apache.batik.css.engine.value.AbstractValueManager;
import org.apache.batik.css.engine.value.FloatValue;
import org.apache.batik.css.engine.value.Value;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.dom.DOMException;

public abstract class LengthManager
extends AbstractValueManager {
    static final double SQRT2 = Math.sqrt(2.0);
    protected static final int HORIZONTAL_ORIENTATION = 0;
    protected static final int VERTICAL_ORIENTATION = 1;
    protected static final int BOTH_ORIENTATION = 2;

    @Override
    public Value createValue(LexicalUnit lu, CSSEngine engine) throws DOMException {
        switch (lu.getLexicalUnitType()) {
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
        throw this.createInvalidLexicalUnitDOMException(lu.getLexicalUnitType());
    }

    @Override
    public Value createFloatValue(short type, float floatValue) throws DOMException {
        switch (type) {
            case 1: 
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 6: 
            case 7: 
            case 8: 
            case 9: 
            case 10: {
                return new FloatValue(type, floatValue);
            }
        }
        throw this.createInvalidFloatTypeDOMException(type);
    }

    @Override
    public Value computeValue(CSSStylableElement elt, String pseudo, CSSEngine engine, int idx, StyleMap sm, Value value) {
        if (value.getCssValueType() != 1) {
            return value;
        }
        switch (value.getPrimitiveType()) {
            case 1: 
            case 5: {
                return value;
            }
            case 7: {
                CSSContext ctx = engine.getCSSContext();
                float v = value.getFloatValue();
                return new FloatValue(1, v / ctx.getPixelUnitToMillimeter());
            }
            case 6: {
                CSSContext ctx = engine.getCSSContext();
                float v = value.getFloatValue();
                return new FloatValue(1, v * 10.0f / ctx.getPixelUnitToMillimeter());
            }
            case 8: {
                CSSContext ctx = engine.getCSSContext();
                float v = value.getFloatValue();
                return new FloatValue(1, v * 25.4f / ctx.getPixelUnitToMillimeter());
            }
            case 9: {
                CSSContext ctx = engine.getCSSContext();
                float v = value.getFloatValue();
                return new FloatValue(1, v * 25.4f / (72.0f * ctx.getPixelUnitToMillimeter()));
            }
            case 10: {
                CSSContext ctx = engine.getCSSContext();
                float v = value.getFloatValue();
                return new FloatValue(1, v * 25.4f / (6.0f * ctx.getPixelUnitToMillimeter()));
            }
            case 3: {
                sm.putFontSizeRelative(idx, true);
                float v = value.getFloatValue();
                int fsidx = engine.getFontSizeIndex();
                float fs = engine.getComputedStyle(elt, pseudo, fsidx).getFloatValue();
                return new FloatValue(1, v * fs);
            }
            case 4: {
                sm.putFontSizeRelative(idx, true);
                float v = value.getFloatValue();
                int fsidx = engine.getFontSizeIndex();
                float fs = engine.getComputedStyle(elt, pseudo, fsidx).getFloatValue();
                return new FloatValue(1, v * fs * 0.5f);
            }
            case 2: {
                float fs;
                CSSContext ctx = engine.getCSSContext();
                switch (this.getOrientation()) {
                    case 0: {
                        sm.putBlockWidthRelative(idx, true);
                        fs = value.getFloatValue() * ctx.getBlockWidth(elt) / 100.0f;
                        break;
                    }
                    case 1: {
                        sm.putBlockHeightRelative(idx, true);
                        fs = value.getFloatValue() * ctx.getBlockHeight(elt) / 100.0f;
                        break;
                    }
                    default: {
                        sm.putBlockWidthRelative(idx, true);
                        sm.putBlockHeightRelative(idx, true);
                        double w = ctx.getBlockWidth(elt);
                        double h = ctx.getBlockHeight(elt);
                        fs = (float)((double)value.getFloatValue() * (Math.sqrt(w * w + h * h) / SQRT2) / 100.0);
                    }
                }
                return new FloatValue(1, fs);
            }
        }
        return value;
    }

    protected abstract int getOrientation();
}

