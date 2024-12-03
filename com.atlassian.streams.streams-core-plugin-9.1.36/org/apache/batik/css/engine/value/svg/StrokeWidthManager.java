/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.engine.value.svg;

import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.value.LengthManager;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.svg.SVGValueConstants;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.dom.DOMException;

public class StrokeWidthManager
extends LengthManager {
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
    public int getPropertyType() {
        return 17;
    }

    @Override
    public String getPropertyName() {
        return "stroke-width";
    }

    @Override
    public Value getDefaultValue() {
        return SVGValueConstants.NUMBER_1;
    }

    @Override
    public Value createValue(LexicalUnit lu, CSSEngine engine) throws DOMException {
        if (lu.getLexicalUnitType() == 12) {
            return SVGValueConstants.INHERIT_VALUE;
        }
        return super.createValue(lu, engine);
    }

    @Override
    protected int getOrientation() {
        return 2;
    }
}

