/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.ExtendedColor;

public class CMYKColor
extends ExtendedColor {
    private static final long serialVersionUID = 5940378778276468452L;
    float cyan;
    float magenta;
    float yellow;
    float black;

    public CMYKColor(int intCyan, int intMagenta, int intYellow, int intBlack) {
        this((float)intCyan / 255.0f, (float)intMagenta / 255.0f, (float)intYellow / 255.0f, (float)intBlack / 255.0f);
    }

    public CMYKColor(float floatCyan, float floatMagenta, float floatYellow, float floatBlack) {
        super(2, 1.0f - floatCyan - floatBlack, 1.0f - floatMagenta - floatBlack, 1.0f - floatYellow - floatBlack);
        this.cyan = CMYKColor.normalize(floatCyan);
        this.magenta = CMYKColor.normalize(floatMagenta);
        this.yellow = CMYKColor.normalize(floatYellow);
        this.black = CMYKColor.normalize(floatBlack);
    }

    public float getCyan() {
        return this.cyan;
    }

    public float getMagenta() {
        return this.magenta;
    }

    public float getYellow() {
        return this.yellow;
    }

    public float getBlack() {
        return this.black;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CMYKColor)) {
            return false;
        }
        CMYKColor c2 = (CMYKColor)obj;
        return this.cyan == c2.cyan && this.magenta == c2.magenta && this.yellow == c2.yellow && this.black == c2.black;
    }

    @Override
    public int hashCode() {
        return Float.floatToIntBits(this.cyan) ^ Float.floatToIntBits(this.magenta) ^ Float.floatToIntBits(this.yellow) ^ Float.floatToIntBits(this.black);
    }
}

