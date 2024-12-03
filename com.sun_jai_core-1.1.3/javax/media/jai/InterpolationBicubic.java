/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import javax.media.jai.InterpolationTable;

public final class InterpolationBicubic
extends InterpolationTable {
    private static final int PRECISION_BITS = 8;
    private static final float A = -0.5f;
    private static final float A3 = 1.5f;
    private static final float A2 = -2.5f;
    private static final float A0 = 1.0f;
    private static final float B3 = -0.5f;
    private static final float B2 = 2.5f;
    private static final float B1 = -4.0f;
    private static final float B0 = 2.0f;

    private static float[] dataHelper(int subsampleBits) {
        int one = 1 << subsampleBits;
        int arrayLength = one * 4;
        float[] tableValues = new float[arrayLength];
        float onef = one;
        int count = 0;
        for (int i = 0; i < one; ++i) {
            float t = i;
            float f = (float)i / onef;
            tableValues[count++] = InterpolationBicubic.bicubic(f + 1.0f);
            tableValues[count++] = InterpolationBicubic.bicubic(f);
            tableValues[count++] = InterpolationBicubic.bicubic(f - 1.0f);
            tableValues[count++] = InterpolationBicubic.bicubic(f - 2.0f);
        }
        return tableValues;
    }

    private static float bicubic(float x) {
        if (x < 0.0f) {
            x = -x;
        }
        if (x >= 1.0f) {
            return ((-0.5f * x + 2.5f) * x + -4.0f) * x + 2.0f;
        }
        return (1.5f * x + -2.5f) * x * x + 1.0f;
    }

    public InterpolationBicubic(int subsampleBits) {
        super(1, 1, 4, 4, subsampleBits, subsampleBits, 8, InterpolationBicubic.dataHelper(subsampleBits), (float[])null);
    }
}

