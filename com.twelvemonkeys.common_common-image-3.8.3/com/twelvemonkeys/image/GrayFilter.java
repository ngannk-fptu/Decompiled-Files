/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.image;

import java.awt.image.RGBImageFilter;

public class GrayFilter
extends RGBImageFilter {
    private int low;
    private float range;

    public GrayFilter() {
        this.canFilterIndexColorModel = true;
        this.low = 0;
        this.range = 1.0f;
    }

    public GrayFilter(float f, float f2) {
        this.canFilterIndexColorModel = true;
        this.low = 0;
        this.range = 1.0f;
        if (f > f2) {
            f = 0.0f;
        }
        if (f < 0.0f) {
            f = 0.0f;
        } else if (f > 1.0f) {
            f = 1.0f;
        }
        if (f2 < 0.0f) {
            f2 = 0.0f;
        } else if (f2 > 1.0f) {
            f2 = 1.0f;
        }
        this.low = (int)(f * 255.0f);
        this.range = f2 - f;
    }

    public GrayFilter(int n, int n2) {
        this((float)n / 255.0f, (float)n2 / 255.0f);
    }

    @Override
    public int filterRGB(int n, int n2, int n3) {
        int n4 = n3 >> 16 & 0xFF;
        int n5 = n3 >> 8 & 0xFF;
        int n6 = n3 & 0xFF;
        int n7 = (222 * n4 + 707 * n5 + 71 * n6) / 1000;
        if (this.range != 1.0f) {
            n7 = this.low + (int)((float)n7 * this.range);
        }
        return n3 & 0xFF000000 | n7 << 16 | n7 << 8 | n7;
    }
}

