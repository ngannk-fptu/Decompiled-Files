/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.engine.value.svg12;

import org.apache.batik.css.engine.value.svg12.AbstractCIEColor;

public class CIELabColor
extends AbstractCIEColor {
    public static final String CIE_LAB_COLOR_FUNCTION = "cielab";

    public CIELabColor(float l, float a, float b, float[] whitepoint) {
        super(new float[]{l, a, b}, whitepoint);
    }

    public CIELabColor(float l, float a, float b) {
        this(l, a, b, null);
    }

    @Override
    public String getFunctionName() {
        return CIE_LAB_COLOR_FUNCTION;
    }
}

