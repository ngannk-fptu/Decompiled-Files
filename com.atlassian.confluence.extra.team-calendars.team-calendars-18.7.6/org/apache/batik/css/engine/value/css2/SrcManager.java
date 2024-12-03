/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.w3c.css.sac.LexicalUnit
 */
package org.apache.batik.css.engine.value.css2;

import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.value.IdentifierManager;
import org.apache.batik.css.engine.value.ListValue;
import org.apache.batik.css.engine.value.StringMap;
import org.apache.batik.css.engine.value.StringValue;
import org.apache.batik.css.engine.value.URIValue;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.ValueConstants;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.dom.DOMException;

public class SrcManager
extends IdentifierManager {
    protected static final StringMap values = new StringMap();

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
        return 38;
    }

    @Override
    public String getPropertyName() {
        return "src";
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
            default: {
                throw this.createInvalidLexicalUnitDOMException(lu.getLexicalUnitType());
            }
            case 24: 
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
                case 24: {
                    String uri = SrcManager.resolveURI(engine.getCSSBaseURI(), lu.getStringValue());
                    result.append(new URIValue(lu.getStringValue(), uri));
                    lu = lu.getNextLexicalUnit();
                    if (lu == null || lu.getLexicalUnitType() != 41 || !lu.getFunctionName().equalsIgnoreCase("format")) break;
                    lu = lu.getNextLexicalUnit();
                    break;
                }
                case 35: {
                    StringBuffer sb = new StringBuffer(lu.getStringValue());
                    lu = lu.getNextLexicalUnit();
                    if (lu != null && lu.getLexicalUnitType() == 35) {
                        do {
                            sb.append(' ');
                            sb.append(lu.getStringValue());
                        } while ((lu = lu.getNextLexicalUnit()) != null && lu.getLexicalUnitType() == 35);
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

    @Override
    public StringMap getIdentifiers() {
        return values;
    }

    static {
        values.put("none", ValueConstants.NONE_VALUE);
    }
}

