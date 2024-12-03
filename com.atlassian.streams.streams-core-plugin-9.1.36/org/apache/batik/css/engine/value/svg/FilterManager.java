/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.engine.value.svg;

import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.value.AbstractValueManager;
import org.apache.batik.css.engine.value.URIValue;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.svg.SVGValueConstants;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.dom.DOMException;

public class FilterManager
extends AbstractValueManager {
    @Override
    public boolean isInheritedProperty() {
        return false;
    }

    @Override
    public String getPropertyName() {
        return "filter";
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
    public Value getDefaultValue() {
        return SVGValueConstants.NONE_VALUE;
    }

    @Override
    public Value createValue(LexicalUnit lu, CSSEngine engine) throws DOMException {
        switch (lu.getLexicalUnitType()) {
            case 12: {
                return SVGValueConstants.INHERIT_VALUE;
            }
            case 24: {
                return new URIValue(lu.getStringValue(), FilterManager.resolveURI(engine.getCSSBaseURI(), lu.getStringValue()));
            }
            case 35: {
                if (lu.getStringValue().equalsIgnoreCase("none")) {
                    return SVGValueConstants.NONE_VALUE;
                }
                throw this.createInvalidIdentifierDOMException(lu.getStringValue());
            }
        }
        throw this.createInvalidLexicalUnitDOMException(lu.getLexicalUnitType());
    }

    @Override
    public Value createStringValue(short type, String value, CSSEngine engine) throws DOMException {
        if (type == 21) {
            if (value.equalsIgnoreCase("none")) {
                return SVGValueConstants.NONE_VALUE;
            }
            throw this.createInvalidIdentifierDOMException(value);
        }
        if (type == 20) {
            return new URIValue(value, FilterManager.resolveURI(engine.getCSSBaseURI(), value));
        }
        throw this.createInvalidStringTypeDOMException(type);
    }
}

