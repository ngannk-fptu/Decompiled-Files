/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.color;

import java.awt.color.ColorSpace;
import java.awt.image.ComponentColorModel;

public final class UInt32ColorModel
extends ComponentColorModel {
    public UInt32ColorModel(ColorSpace colorSpace, boolean bl, boolean bl2) {
        super(colorSpace, bl, bl2, bl ? 3 : 1, 3);
    }

    @Override
    public float[] getNormalizedComponents(Object object, float[] fArray, int n) {
        float f;
        int n2 = this.getNumComponents();
        if (fArray == null) {
            fArray = new float[n2 + n];
        }
        int[] nArray = (int[])object;
        int n3 = 0;
        int n4 = n;
        while (n3 < n2) {
            fArray[n4] = (float)((long)nArray[n3] & 0xFFFFFFFFL) / (float)((1L << this.getComponentSize(n3)) - 1L);
            ++n3;
            ++n4;
        }
        n3 = this.getNumColorComponents();
        if (this.hasAlpha() && this.isAlphaPremultiplied() && (f = fArray[n3 + n]) != 0.0f) {
            float f2 = 1.0f / f;
            int n5 = n;
            while (n5 < n3 + n) {
                int n6 = n5++;
                fArray[n6] = fArray[n6] * f2;
            }
        }
        return fArray;
    }
}

