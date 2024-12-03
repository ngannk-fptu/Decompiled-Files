/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import javax.media.jai.InterpolationTable;

public final class InterpolationBicubic2
extends InterpolationTable {
    private static final int PRECISION_BITS = 8;
    private static final float A = -1.0f;
    private static final float A3 = 1.0f;
    private static final float A2 = -2.0f;
    private static final float A0 = 1.0f;
    private static final float B3 = -1.0f;
    private static final float B2 = 5.0f;
    private static final float B1 = -8.0f;
    private static final float B0 = 4.0f;

    private static float[] dataHelper(int subsampleBits) {
        int one = 1 << subsampleBits;
        int arrayLength = one * 4;
        float[] tableValues = new float[arrayLength];
        float onef = one;
        int count = 0;
        for (int i = 0; i < one; ++i) {
            float t = i;
            float f = (float)i / onef;
            tableValues[count++] = InterpolationBicubic2.bicubic(f + 1.0f);
            tableValues[count++] = InterpolationBicubic2.bicubic(f);
            tableValues[count++] = InterpolationBicubic2.bicubic(f - 1.0f);
            tableValues[count++] = InterpolationBicubic2.bicubic(f - 2.0f);
        }
        return tableValues;
    }

    private static float bicubic(float x) {
        if (x < 0.0f) {
            x = -x;
        }
        if (x >= 1.0f) {
            return ((-1.0f * x + 5.0f) * x + -8.0f) * x + 4.0f;
        }
        return (1.0f * x + -2.0f) * x * x + 1.0f;
    }

    public InterpolationBicubic2(int subsampleBits) {
        super(1, 1, 4, 4, subsampleBits, subsampleBits, 8, InterpolationBicubic2.dataHelper(subsampleBits), (float[])null);
    }
}

