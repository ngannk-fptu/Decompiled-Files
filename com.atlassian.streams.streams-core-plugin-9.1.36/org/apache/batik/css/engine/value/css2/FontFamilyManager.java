/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.engine.value.css2;

import org.apache.batik.css.engine.CSSContext;
import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.CSSStylableElement;
import org.apache.batik.css.engine.StyleMap;
import org.apache.batik.css.engine.value.AbstractValueManager;
import org.apache.batik.css.engine.value.ListValue;
import org.apache.batik.css.engine.value.StringMap;
import org.apache.batik.css.engine.value.StringValue;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.ValueConstants;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.dom.DOMException;

public class FontFamilyManager
extends AbstractValueManager {
    protected static final ListValue DEFAULT_VALUE = new ListValue();
    protected static final StringMap values;

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
        return 26;
    }

    @Override
    public String getPropertyName() {
        return "font-family";
    }

    @Override
    public Value getDefaultValue() {
        return DEFAULT_VALUE;
    }

    @Override
    public Value createValue(LexicalUnit lu, CSSEngine engine) throws DOMException {
        switch (lu.getLexicalUnitType()) {
            case 12: {
                return ValueConstants.INHERIT_VALUE;
            }
            default: {
                throw this.createInvalidLexicalUnitDOMException(lu.getLexicalUnitType());
            }
            case 35: 
            case 36: 
        }
        ListValue result = new ListValue();
        do {
            switch (lu.getLexicalUnitType()) {
                case 36: {
                    result.append(new StringValue(19, lu.getStringValue()));
                    lu = lu.getNextLexicalUnit();
                    break;
                }
                case 35: {
                    StringBuffer sb = new StringBuffer(lu.getStringValue());
                    lu = lu.getNextLexicalUnit();
                    if (lu != null && this.isIdentOrNumber(lu)) {
                        do {
                            sb.append(' ');
                            switch (lu.getLexicalUnitType()) {
                                case 35: {
                                    sb.append(lu.getStringValue());
                                    break;
                                }
                                case 13: {
                                    sb.append(Integer.toString(lu.getIntegerValue()));
                                }
                            }
                        } while ((lu = lu.getNextLexicalUnit()) != null && this.isIdentOrNumber(lu));
                        result.append(new StringValue(19, sb.toString()));
                        break;
                    }
                    String id = sb.toString();
                    String s = id.toLowerCase().intern();
                    Value v = (Value)values.get(s);
                    result.append(v != null ? v : new StringValue(19, id));
                }
            }
            if (lu == null) {
                return result;
            }
            if (lu.getLexicalUnitType() == 0) continue;
            throw this.createInvalidLexicalUnitDOMException(lu.getLexicalUnitType());
        } while ((lu = lu.getNextLexicalUnit()) != null);
        throw this.createMalformedLexicalUnitDOMException();
    }

    private boolean isIdentOrNumber(LexicalUnit lu) {
        short type = lu.getLexicalUnitType();
        switch (type) {
            case 13: 
            case 35: {
                return true;
            }
        }
        return false;
    }

    @Override
    public Value computeValue(CSSStylableElement elt, String pseudo, CSSEngine engine, int idx, StyleMap sm, Value value) {
        if (value == DEFAULT_VALUE) {
            CSSContext ctx = engine.getCSSContext();
            value = ctx.getDefaultFontFamily();
        }
        return value;
    }

    static {
        DEFAULT_VALUE.append(new StringValue(19, "Arial"));
        DEFAULT_VALUE.append(new StringValue(19, "Helvetica"));
        DEFAULT_VALUE.append(new StringValue(21, "sans-serif"));
        values = new StringMap();
        values.put("cursive", ValueConstants.CURSIVE_VALUE);
        values.put("fantasy", ValueConstants.FANTASY_VALUE);
        values.put("monospace", ValueConstants.MONOSPACE_VALUE);
        values.put("serif", ValueConstants.SERIF_VALUE);
        values.put("sans-serif", ValueConstants.SANS_SERIF_VALUE);
    }
}

