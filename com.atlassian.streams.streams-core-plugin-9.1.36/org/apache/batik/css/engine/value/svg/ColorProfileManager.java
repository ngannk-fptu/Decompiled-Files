/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.engine.value.svg;

import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.value.AbstractValueManager;
import org.apache.batik.css.engine.value.StringValue;
import org.apache.batik.css.engine.value.URIValue;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.svg.SVGValueConstants;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.dom.DOMException;

public class ColorProfileManager
extends AbstractValueManager {
    @Override
    public boolean isInheritedProperty() {
        return true;
    }

    @Override
    public String getPropertyName() {
        return "color-profile";
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
        return SVGValueConstants.AUTO_VALUE;
    }

    @Override
    public Value createValue(LexicalUnit lu, CSSEngine engine) throws DOMException {
        switch (lu.getLexicalUnitType()) {
            case 12: {
                return SVGValueConstants.INHERIT_VALUE;
            }
            case 35: {
                String s = lu.getStringValue().toLowerCase();
                if (s.equals("auto")) {
                    return SVGValueConstants.AUTO_VALUE;
                }
                if (s.equals("srgb")) {
                    return SVGValueConstants.SRGB_VALUE;
                }
                return new StringValue(21, s);
            }
            case 24: {
                return new URIValue(lu.getStringValue(), ColorProfileManager.resolveURI(engine.getCSSBaseURI(), lu.getStringValue()));
            }
        }
        throw this.createInvalidLexicalUnitDOMException(lu.getLexicalUnitType());
    }

    @Override
    public Value createStringValue(short type, String value, CSSEngine engine) throws DOMException {
        switch (type) {
            case 21: {
                String s = value.toLowerCase();
                if (s.equals("auto")) {
                    return SVGValueConstants.AUTO_VALUE;
                }
                if (s.equals("srgb")) {
                    return SVGValueConstants.SRGB_VALUE;
                }
                return new StringValue(21, s);
            }
            case 20: {
                return new URIValue(value, ColorProfileManager.resolveURI(engine.getCSSBaseURI(), value));
            }
        }
        throw this.createInvalidStringTypeDOMException(type);
    }
}

