/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.style.derived;

import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.parser.FSFunction;
import org.xhtmlrenderer.css.parser.PropertyValue;
import org.xhtmlrenderer.css.style.DerivedValue;

public class FunctionValue
extends DerivedValue {
    private FSFunction _function;

    public FunctionValue(CSSName name, PropertyValue value) {
        super(name, value.getPrimitiveType(), value.getCssText(), value.getCssText());
        this._function = value.getFunction();
    }

    public FSFunction getFunction() {
        return this._function;
    }
}

