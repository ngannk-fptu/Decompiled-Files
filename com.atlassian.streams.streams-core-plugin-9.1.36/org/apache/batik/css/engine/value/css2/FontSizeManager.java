/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.engine.value.css2;

import org.apache.batik.css.engine.CSSContext;
import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.CSSStylableElement;
import org.apache.batik.css.engine.StyleMap;
import org.apache.batik.css.engine.value.FloatValue;
import org.apache.batik.css.engine.value.LengthManager;
import org.apache.batik.css.engine.value.StringMap;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.ValueConstants;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.dom.DOMException;

public class FontSizeManager
extends LengthManager {
    protected static final StringMap values = new StringMap();

    public StringMap getIdentifiers() {
        return values;
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
    public String getPropertyName() {
        return "font-size";
    }

    @Override
    public int getPropertyType() {
        return 39;
    }

    @Override
    public Value getDefaultValue() {
        return ValueConstants.MEDIUM_VALUE;
    }

    @Override
    public Value createValue(LexicalUnit lu, CSSEngine engine) throws DOMException {
        switch (lu.getLexicalUnitType()) {
            case 12: {
                return ValueConstants.INHERIT_VALUE;
            }
            case 35: {
                String s = lu.getStringValue().toLowerCase().intern();
                Object v = values.get(s);
                if (v == null) {
                    throw this.createInvalidIdentifierDOMException(s);
                }
                return (Value)v;
            }
        }
        return super.createValue(lu, engine);
    }

    @Override
    public Value createStringValue(short type, String value, CSSEngine engine) throws DOMException {
        if (type != 21) {
            throw this.createInvalidStringTypeDOMException(type);
        }
        Object v = values.get(value.toLowerCase().intern());
        if (v == null) {
            throw this.createInvalidIdentifierDOMException(value);
        }
        return (Value)v;
    }

    @Override
    public Value computeValue(CSSStylableElement elt, String pseudo, CSSEngine engine, int idx, StyleMap sm, Value value) {
        float scale = 1.0f;
        boolean doParentRelative = false;
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
                doParentRelative = true;
                scale = value.getFloatValue();
                break;
            }
            case 4: {
                doParentRelative = true;
                scale = value.getFloatValue() * 0.5f;
                break;
            }
            case 2: {
                doParentRelative = true;
                scale = value.getFloatValue() * 0.01f;
                break;
            }
        }
        if (value == ValueConstants.LARGER_VALUE) {
            doParentRelative = true;
            scale = 1.2f;
        } else if (value == ValueConstants.SMALLER_VALUE) {
            doParentRelative = true;
            scale = 0.8333333f;
        }
        if (doParentRelative) {
            float fs;
            sm.putParentRelative(idx, true);
            CSSStylableElement p = CSSEngine.getParentCSSStylableElement(elt);
            if (p == null) {
                CSSContext ctx = engine.getCSSContext();
                fs = ctx.getMediumFontSize();
            } else {
                fs = engine.getComputedStyle(p, null, idx).getFloatValue();
            }
            return new FloatValue(1, fs * scale);
        }
        CSSContext ctx = engine.getCSSContext();
        float fs = ctx.getMediumFontSize();
        String s = value.getStringValue();
        block11 : switch (s.charAt(0)) {
            case 'm': {
                break;
            }
            case 's': {
                fs = (float)((double)fs / 1.2);
                break;
            }
            case 'l': {
                fs = (float)((double)fs * 1.2);
                break;
            }
            default: {
                switch (s.charAt(1)) {
                    case 'x': {
                        switch (s.charAt(3)) {
                            case 's': {
                                fs = (float)((double)fs / 1.2 / 1.2 / 1.2);
                                break block11;
                            }
                        }
                        fs = (float)((double)fs * 1.2 * 1.2 * 1.2);
                        break block11;
                    }
                }
                switch (s.charAt(2)) {
                    case 's': {
                        fs = (float)((double)fs / 1.2 / 1.2);
                        break block11;
                    }
                }
                fs = (float)((double)fs * 1.2 * 1.2);
            }
        }
        return new FloatValue(1, fs);
    }

    @Override
    protected int getOrientation() {
        return 1;
    }

    static {
        values.put("all", ValueConstants.ALL_VALUE);
        values.put("large", ValueConstants.LARGE_VALUE);
        values.put("larger", ValueConstants.LARGER_VALUE);
        values.put("medium", ValueConstants.MEDIUM_VALUE);
        values.put("small", ValueConstants.SMALL_VALUE);
        values.put("smaller", ValueConstants.SMALLER_VALUE);
        values.put("x-large", ValueConstants.X_LARGE_VALUE);
        values.put("x-small", ValueConstants.X_SMALL_VALUE);
        values.put("xx-large", ValueConstants.XX_LARGE_VALUE);
        values.put("xx-small", ValueConstants.XX_SMALL_VALUE);
    }
}

