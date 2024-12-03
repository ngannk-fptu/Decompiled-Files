/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.style.derived;

import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.parser.PropertyValue;
import org.xhtmlrenderer.css.style.CssContext;
import org.xhtmlrenderer.css.style.DerivedValue;

public class NumberValue
extends DerivedValue {
    private float _floatValue;

    public NumberValue(CSSName cssName, PropertyValue value) {
        super(cssName, value.getPrimitiveType(), value.getCssText(), value.getCssText());
        this._floatValue = value.getFloatValue();
    }

    @Override
    public float asFloat() {
        return this._floatValue;
    }

    @Override
    public float getFloatProportionalTo(CSSName cssName, float baseValue, CssContext ctx) {
        return this._floatValue;
    }

    @Override
    public boolean hasAbsoluteUnit() {
        return true;
    }
}

