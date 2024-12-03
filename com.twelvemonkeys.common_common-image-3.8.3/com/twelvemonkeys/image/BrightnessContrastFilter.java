/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.image;

import java.awt.image.RGBImageFilter;

public class BrightnessContrastFilter
extends RGBImageFilter {
    private final int[] LUT;

    public BrightnessContrastFilter() {
        this(0.3f, 0.3f);
    }

    public BrightnessContrastFilter(float f, float f2) {
        this.canFilterIndexColorModel = true;
        this.LUT = BrightnessContrastFilter.createLUT(f, f2);
    }

    private static int[] createLUT(float f, float f2) {
        int[] nArray = new int[256];
        double d = f2 > 0.0f ? Math.pow(f2, 7.0) * 127.0 : (double)f2;
        double d2 = (double)f + 1.0;
        for (int i = 0; i < 256; ++i) {
            nArray[i] = BrightnessContrastFilter.clamp((int)(127.5 * d2 + (double)(i - 127) * (d + 1.0)));
        }
        if (f2 == 1.0f) {
            nArray[127] = nArray[126];
        }
        return nArray;
    }

    private static int clamp(int n) {
        if (n < 0) {
            return 0;
        }
        if (n > 255) {
            return 255;
        }
        return n;
    }

    @Override
    public int filterRGB(int n, int n2, int n3) {
        int n4 = n3 >> 16 & 0xFF;
        int n5 = n3 >> 8 & 0xFF;
        int n6 = n3 & 0xFF;
        n4 = this.LUT[n4];
        n5 = this.LUT[n5];
        n6 = this.LUT[n6];
        return n3 & 0xFF000000 | n4 << 16 | n5 << 8 | n6;
    }
}

