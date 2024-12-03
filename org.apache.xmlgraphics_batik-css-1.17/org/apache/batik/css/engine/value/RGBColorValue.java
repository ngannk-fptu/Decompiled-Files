/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.engine.value;

import org.apache.batik.css.engine.value.AbstractValue;
import org.apache.batik.css.engine.value.Value;
import org.w3c.dom.DOMException;

public class RGBColorValue
extends AbstractValue {
    protected Value red;
    protected Value green;
    protected Value blue;

    public RGBColorValue(Value r, Value g, Value b) {
        this.red = r;
        this.green = g;
        this.blue = b;
    }

    @Override
    public short getPrimitiveType() {
        return 25;
    }

    @Override
    public String getCssText() {
        return "rgb(" + this.red.getCssText() + ", " + this.green.getCssText() + ", " + this.blue.getCssText() + ')';
    }

    @Override
    public Value getRed() throws DOMException {
        return this.red;
    }

    @Override
    public Value getGreen() throws DOMException {
        return this.green;
    }

    @Override
    public Value getBlue() throws DOMException {
        return this.blue;
    }

    public String toString() {
        return this.getCssText();
    }
}

