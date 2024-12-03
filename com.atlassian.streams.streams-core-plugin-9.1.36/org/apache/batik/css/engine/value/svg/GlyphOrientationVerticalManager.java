/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.engine.value.svg;

import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.svg.GlyphOrientationManager;
import org.apache.batik.css.engine.value.svg.SVGValueConstants;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.dom.DOMException;

public class GlyphOrientationVerticalManager
extends GlyphOrientationManager {
    @Override
    public String getPropertyName() {
        return "glyph-orientation-vertical";
    }

    @Override
    public Value getDefaultValue() {
        return SVGValueConstants.AUTO_VALUE;
    }

    @Override
    public Value createValue(LexicalUnit lu, CSSEngine engine) throws DOMException {
        if (lu.getLexicalUnitType() == 35) {
            if (lu.getStringValue().equalsIgnoreCase("auto")) {
                return SVGValueConstants.AUTO_VALUE;
            }
            throw this.createInvalidIdentifierDOMException(lu.getStringValue());
        }
        return super.createValue(lu, engine);
    }

    @Override
    public Value createStringValue(short type, String value, CSSEngine engine) throws DOMException {
        if (type != 21) {
            throw this.createInvalidStringTypeDOMException(type);
        }
        if (value.equalsIgnoreCase("auto")) {
            return SVGValueConstants.AUTO_VALUE;
        }
        throw this.createInvalidIdentifierDOMException(value);
    }
}

