/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.colorspace;

import com.sun.pdfview.PDFObject;
import java.awt.color.ColorSpace;
import java.io.IOException;

public class LabColor
extends ColorSpace {
    float[] white = new float[]{1.0f, 1.0f, 1.0f};
    float[] black = new float[]{0.0f, 0.0f, 0.0f};
    float[] range = new float[]{-100.0f, 100.0f, -100.0f, 100.0f};
    static ColorSpace cie = ColorSpace.getInstance(1000);

    public LabColor(PDFObject obj) throws IOException {
        super(1, 3);
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
        if ((ary = obj.getDictRef("Range")) != null) {
            for (i = 0; i < 4; ++i) {
                this.range[i] = ary.getAt(i).getFloatValue();
            }
        }
    }

    @Override
    public int getNumComponents() {
        return 3;
    }

    public final float stage2(float s1) {
        return s1 >= 0.20689656f ? s1 * s1 * s1 : 0.12841855f * (s1 - 0.13793103f);
    }

    @Override
    public float[] toRGB(float[] comp) {
        if (comp.length == 3) {
            float l = (comp[0] + 16.0f) / 116.0f + comp[1] / 500.0f;
            float m = (comp[0] + 16.0f) / 116.0f;
            float n = (comp[0] + 16.0f) / 116.0f - comp[2] / 200.0f;
            float[] xyz = new float[]{this.white[0] * this.stage2(l), this.white[0] * this.stage2(m), this.white[0] * this.stage2(n)};
            float[] rgb = cie.fromCIEXYZ(xyz);
            return rgb;
        }
        return this.black;
    }

    @Override
    public float[] fromRGB(float[] rgbvalue) {
        return new float[3];
    }

    @Override
    public float[] fromCIEXYZ(float[] colorvalue) {
        return new float[3];
    }

    @Override
    public int getType() {
        return 1;
    }

    @Override
    public float[] toCIEXYZ(float[] colorvalue) {
        return new float[3];
    }
}

