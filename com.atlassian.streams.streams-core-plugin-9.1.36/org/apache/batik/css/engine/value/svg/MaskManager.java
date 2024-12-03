/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.engine.value.svg;

import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.value.AbstractValueManager;
import org.apache.batik.css.engine.value.URIValue;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.ValueConstants;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.dom.DOMException;

public class MaskManager
extends AbstractValueManager {
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
        return 20;
    }

    @Override
    public String getPropertyName() {
        return "mask";
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
            case 24: {
                return new URIValue(lu.getStringValue(), MaskManager.resolveURI(engine.getCSSBaseURI(), lu.getStringValue()));
            }
            case 35: {
                if (!lu.getStringValue().equalsIgnoreCase("none")) break;
                return ValueConstants.NONE_VALUE;
            }
        }
        throw this.createInvalidLexicalUnitDOMException(lu.getLexicalUnitType());
    }

    @Override
    public Value createStringValue(short type, String value, CSSEngine engine) throws DOMException {
        switch (type) {
            case 21: {
                if (!value.equalsIgnoreCase("none")) break;
                return ValueConstants.NONE_VALUE;
            }
            case 20: {
                return new URIValue(value, MaskManager.resolveURI(engine.getCSSBaseURI(), value));
            }
        }
        throw this.createInvalidStringTypeDOMException(type);
    }
}

