/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.colorspace;

import java.awt.color.ColorSpace;

public class CMYKColorSpace
extends ColorSpace {
    public CMYKColorSpace() {
        super(9, 4);
    }

    @Override
    public float[] fromCIEXYZ(float[] colorvalue) {
        return new float[3];
    }

    @Override
    public float[] fromRGB(float[] rgbvalue) {
        float[] color = new float[4];
        float c = 1.0f - rgbvalue[0];
        float m = 1.0f - rgbvalue[1];
        float y = 1.0f - rgbvalue[2];
        float k = Math.min(c, Math.min(m, y));
        float ik = 1.0f - k;
        if (ik == 0.0f) {
            c = 1.0f;
            m = 1.0f;
            y = 1.0f;
        } else {
            c = (c - k) / ik;
            m = (m - k) / ik;
            y = (y - k) / ik;
        }
        color[0] = c;
        color[1] = m;
        color[2] = y;
        color[3] = k;
        return color;
    }

    @Override
    public int getNumComponents() {
        return 4;
    }

    @Override
    public String getName(int idx) {
        return "CMYK";
    }

    @Override
    public int getType() {
        return 9;
    }

    @Override
    public float[] toCIEXYZ(float[] colorvalue) {
        return new float[3];
    }

    @Override
    public float[] toRGB(float[] colorvalue) {
        if (colorvalue.length == 4) {
            float[] color = new float[3];
            float k = colorvalue[3];
            float ik = 1.0f - k;
            color[0] = 1.0f - (colorvalue[0] * ik + k);
            color[1] = 1.0f - (colorvalue[1] * ik + k);
            color[2] = 1.0f - (colorvalue[2] * ik + k);
            return color;
        }
        return new float[3];
    }
}

