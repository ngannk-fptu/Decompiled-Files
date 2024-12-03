/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.colorspace;

import com.sun.pdfview.PDFPaint;
import java.awt.Color;
import java.awt.color.ColorSpace;

public class MaskColorSpace
extends ColorSpace {
    private PDFPaint paint;
    ColorSpace cie = ColorSpace.getInstance(1001);
    float[] prev1 = this.cie.fromRGB(this.toRGB(new float[]{1.0f}));
    float[] prev0 = this.cie.fromRGB(this.toRGB(new float[]{0.0f}));

    public MaskColorSpace(PDFPaint paint) {
        super(5, 1);
        this.paint = paint;
    }

    @Override
    public float[] fromCIEXYZ(float[] colorvalue) {
        float x = colorvalue[0];
        float y = colorvalue[1];
        float z = colorvalue[2];
        float[] mask = new float[]{Math.round(x) > 0 || Math.round(y) > 0 || Math.round(z) > 0 ? 1.0f : 0.0f};
        return mask;
    }

    @Override
    public float[] fromRGB(float[] rgbvalue) {
        float r = rgbvalue[0];
        float g = rgbvalue[1];
        float b = rgbvalue[2];
        float[] mask = new float[]{Math.round(r) > 0 || Math.round(g) > 0 || Math.round(b) > 0 ? 1.0f : 0.0f};
        return mask;
    }

    @Override
    public float[] toCIEXYZ(float[] colorvalue) {
        if (colorvalue[0] == 1.0f) {
            return this.prev1;
        }
        if (colorvalue[0] == 0.0f) {
            return this.prev0;
        }
        return this.cie.fromRGB(this.toRGB(colorvalue));
    }

    @Override
    public float[] toRGB(float[] colorvalue) {
        return ((Color)this.paint.getPaint()).getRGBColorComponents(null);
    }

    @Override
    public int getNumComponents() {
        return 1;
    }
}

