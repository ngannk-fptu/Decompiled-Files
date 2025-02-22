/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.w3c.css.sac.LexicalUnit
 */
package org.apache.batik.css.engine.value.css2;

import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.value.InheritValue;
import org.apache.batik.css.engine.value.RectManager;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.ValueConstants;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.dom.DOMException;

public class ClipManager
extends RectManager {
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
        return 19;
    }

    @Override
    public String getPropertyName() {
        return "clip";
    }

    @Override
    public Value getDefaultValue() {
        return ValueConstants.AUTO_VALUE;
    }

    @Override
    public Value createValue(LexicalUnit lu, CSSEngine engine) throws DOMException {
        switch (lu.getLexicalUnitType()) {
            case 12: {
                return InheritValue.INSTANCE;
            }
            case 35: {
                if (!lu.getStringValue().equalsIgnoreCase("auto")) break;
                return ValueConstants.AUTO_VALUE;
            }
        }
        return super.createValue(lu, engine);
    }

    @Override
    public Value createStringValue(short type, String value, CSSEngine engine) throws DOMException {
        if (type != 21) {
            throw this.createInvalidStringTypeDOMException(type);
        }
        if (!value.equalsIgnoreCase("auto")) {
            throw this.createInvalidIdentifierDOMException(value);
        }
        return ValueConstants.AUTO_VALUE;
    }
}

