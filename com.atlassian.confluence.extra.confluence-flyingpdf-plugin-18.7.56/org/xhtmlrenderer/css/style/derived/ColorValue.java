/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.style.derived;

import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.parser.FSColor;
import org.xhtmlrenderer.css.parser.PropertyValue;
import org.xhtmlrenderer.css.style.DerivedValue;

public class ColorValue
extends DerivedValue {
    private FSColor _color;

    public ColorValue(CSSName name, PropertyValue value) {
        super(name, value.getPrimitiveType(), value.getCssText(), value.getCssText());
        this._color = value.getFSColor();
    }

    @Override
    public FSColor asColor() {
        return this._color;
    }
}

