/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.ExtendedColor;

public class GrayColor
extends ExtendedColor {
    private static final long serialVersionUID = -6571835680819282746L;
    private float gray;
    public static final GrayColor GRAYBLACK = new GrayColor(0.0f);
    public static final GrayColor GRAYWHITE = new GrayColor(1.0f);

    public GrayColor(int intGray) {
        this((float)intGray / 255.0f);
    }

    public GrayColor(float floatGray) {
        super(1, floatGray, floatGray, floatGray);
        this.gray = GrayColor.normalize(floatGray);
    }

    public float getGray() {
        return this.gray;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof GrayColor && ((GrayColor)obj).gray == this.gray;
    }

    @Override
    public int hashCode() {
        return Float.floatToIntBits(this.gray);
    }
}

