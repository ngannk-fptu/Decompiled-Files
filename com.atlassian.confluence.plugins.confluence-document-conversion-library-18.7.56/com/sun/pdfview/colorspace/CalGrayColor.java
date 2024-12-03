/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.colorspace;

import com.sun.pdfview.PDFObject;
import java.awt.color.ColorSpace;
import java.io.IOException;

public class CalGrayColor
extends ColorSpace {
    float[] white = new float[]{1.0f, 1.0f, 1.0f};
    float[] black = new float[]{0.0f, 0.0f, 0.0f};
    float gamma = 1.0f;
    static ColorSpace cie = ColorSpace.getInstance(1000);

    public CalGrayColor(PDFObject obj) throws IOException {
        super(6, 1);
        PDFObject g;
        int i;
        PDFObject ary = obj.getDictRef("WhitePoint");
        if (ary != null) {
            for (i = 0; i < 3; ++i) {
                this.white[i] = ary.getAt(i).getFloatValue();
            }
        }
        if ((ary = obj.getDictRef("BlackPoint")) != null) {
            for (i = 0; i < 3; ++i) {
                this.black[i] = ary.getAt(i).getFloatValue();
            }
        }
        if ((g = obj.getDictRef("Gamma")) != null) {
            this.gamma = g.getFloatValue();
        }
    }

    public CalGrayColor() {
        super(6, 1);
    }

    @Override
    public int getNumComponents() {
        return 1;
    }

    @Override
    public float[] toRGB(float[] comp) {
        if (comp.length == 1) {
            float mul = (float)Math.pow(comp[0], this.gamma);
            float[] xyz = new float[]{this.white[0] * mul, 0.0f, 0.0f};
            float[] rgb = cie.fromCIEXYZ(xyz);
            return rgb;
        }
        return this.black;
    }

    @Override
    public float[] fromRGB(float[] rgbvalue) {
        return new float[1];
    }

    @Override
    public float[] fromCIEXYZ(float[] colorvalue) {
        return new float[1];
    }

    @Override
    public int getType() {
        return 6;
    }

    @Override
    public float[] toCIEXYZ(float[] colorvalue) {
        return new float[3];
    }
}

