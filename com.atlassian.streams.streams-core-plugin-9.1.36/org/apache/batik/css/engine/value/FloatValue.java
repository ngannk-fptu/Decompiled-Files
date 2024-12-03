/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.engine.value;

import org.apache.batik.css.engine.value.AbstractValue;
import org.w3c.dom.DOMException;

public class FloatValue
extends AbstractValue {
    protected static final String[] UNITS = new String[]{"", "%", "em", "ex", "px", "cm", "mm", "in", "pt", "pc", "deg", "rad", "grad", "ms", "s", "Hz", "kHz", ""};
    protected float floatValue;
    protected short unitType;

    public static String getCssText(short unit, float value) {
        if (unit < 0 || unit >= UNITS.length) {
            throw new DOMException(12, "");
        }
        String s = String.valueOf(value);
        if (s.endsWith(".0")) {
            s = s.substring(0, s.length() - 2);
        }
        return s + UNITS[unit - 1];
    }

    public FloatValue(short unitType, float floatValue) {
        this.unitType = unitType;
        this.floatValue = floatValue;
    }

    @Override
    public short getPrimitiveType() {
        return this.unitType;
    }

    @Override
    public float getFloatValue() {
        return this.floatValue;
    }

    @Override
    public String getCssText() {
        return FloatValue.getCssText(this.unitType, this.floatValue);
    }

    public String toString() {
        return this.getCssText();
    }
}

