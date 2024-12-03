/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.engine.value.css2;

import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.value.AbstractValueManager;
import org.apache.batik.css.engine.value.ListValue;
import org.apache.batik.css.engine.value.StringMap;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.ValueConstants;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.dom.DOMException;

public class TextDecorationManager
extends AbstractValueManager {
    protected static final StringMap values = new StringMap();

    @Override
    public boolean isInheritedProperty() {
        return false;
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
        return 18;
    }

    @Override
    public String getPropertyName() {
        return "text-decoration";
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
            case 35: {
                if (lu.getStringValue().equalsIgnoreCase("none")) {
                    return ValueConstants.NONE_VALUE;
                }
                ListValue lv = new ListValue(' ');
                do {
                    Object obj;
                    if (lu.getLexicalUnitType() == 35) {
                        String s = lu.getStringValue().toLowerCase().intern();
                        obj = values.get(s);
                        if (obj == null) {
                            throw this.createInvalidIdentifierDOMException(lu.getStringValue());
                        }
                    } else {
                        throw this.createInvalidLexicalUnitDOMException(lu.getLexicalUnitType());
                    }
                    lv.append((Value)obj);
                    lu = lu.getNextLexicalUnit();
                } while (lu != null);
                return lv;
            }
        }
        throw this.createInvalidLexicalUnitDOMException(lu.getLexicalUnitType());
    }

    @Override
    public Value createStringValue(short type, String value, CSSEngine engine) throws DOMException {
        if (type != 21) {
            throw this.createInvalidStringTypeDOMException(type);
        }
        if (!value.equalsIgnoreCase("none")) {
            throw this.createInvalidIdentifierDOMException(value);
        }
        return ValueConstants.NONE_VALUE;
    }

    static {
        values.put("blink", ValueConstants.BLINK_VALUE);
        values.put("line-through", ValueConstants.LINE_THROUGH_VALUE);
        values.put("overline", ValueConstants.OVERLINE_VALUE);
        values.put("underline", ValueConstants.UNDERLINE_VALUE);
    }
}

