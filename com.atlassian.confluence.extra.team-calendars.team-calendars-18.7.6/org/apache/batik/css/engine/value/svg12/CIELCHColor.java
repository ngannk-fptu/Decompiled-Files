/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.engine.value.svg12;

import org.apache.batik.css.engine.value.svg12.AbstractCIEColor;

public class CIELCHColor
extends AbstractCIEColor {
    public static final String CIE_LCH_COLOR_FUNCTION = "cielch";

    public CIELCHColor(float l, float c, float h, float[] whitepoint) {
        super(new float[]{l, c, h}, whitepoint);
    }

    public CIELCHColor(float l, float c, float h) {
        this(l, c, h, null);
    }

    @Override
    public String getFunctionName() {
        return CIE_LCH_COLOR_FUNCTION;
    }
}

