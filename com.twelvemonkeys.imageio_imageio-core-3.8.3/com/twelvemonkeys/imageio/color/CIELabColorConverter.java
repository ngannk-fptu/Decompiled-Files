/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.lang.Validate
 */
package com.twelvemonkeys.imageio.color;

import com.twelvemonkeys.lang.Validate;

public final class CIELabColorConverter {
    private final float[] whitePoint;

    public CIELabColorConverter(Illuminant illuminant) {
        this.whitePoint = ((Illuminant)((Object)Validate.notNull((Object)((Object)illuminant), (String)"illuminant"))).getWhitePoint();
    }

    private float clamp(float f) {
        if (f < 0.0f) {
            return 0.0f;
        }
        if (f > 255.0f) {
            return 255.0f;
        }
        return f;
    }

    public void toRGB(float f, float f2, float f3, float[] fArray) {
        this.XYZtoRGB(this.LABtoXYZ(f, f2, f3, fArray), fArray);
    }

    private float[] LABtoXYZ(float f, float f2, float f3, float[] fArray) {
        float f4 = (f + 16.0f) / 116.0f;
        float f5 = f4 * f4 * f4;
        float f6 = f2 / 500.0f + f4;
        float f7 = f6 * f6 * f6;
        float f8 = f4 - f3 / 200.0f;
        float f9 = f8 * f8 * f8;
        f4 = f5 > 0.008856f ? f5 : (f4 - 0.13793103f) / 7.787f;
        f6 = f7 > 0.008856f ? f7 : (f6 - 0.13793103f) / 7.787f;
        f8 = f9 > 0.008856f ? f9 : (f8 - 0.13793103f) / 7.787f;
        fArray[0] = f6 * this.whitePoint[0];
        fArray[1] = f4 * this.whitePoint[1];
        fArray[2] = f8 * this.whitePoint[2];
        return fArray;
    }

    private float[] XYZtoRGB(float[] fArray, float[] fArray2) {
        return this.XYZtoRGB(fArray[0], fArray[1], fArray[2], fArray2);
    }

    private float[] XYZtoRGB(float f, float f2, float f3, float[] fArray) {
        float f4 = f / 100.0f;
        float f5 = f2 / 100.0f;
        float f6 = f3 / 100.0f;
        float f7 = f4 * 3.2406f + f5 * -1.5372f + f6 * -0.4986f;
        float f8 = f4 * -0.9689f + f5 * 1.8758f + f6 * 0.0415f;
        float f9 = f4 * 0.0557f + f5 * -0.204f + f6 * 1.057f;
        f7 = f7 > 0.0031308f ? 1.055f * (float)CIELabColorConverter.pow(f7, 0.4166666666666667) - 0.055f : (f7 *= 12.92f);
        f8 = f8 > 0.0031308f ? 1.055f * (float)CIELabColorConverter.pow(f8, 0.4166666666666667) - 0.055f : (f8 *= 12.92f);
        f9 = f9 > 0.0031308f ? 1.055f * (float)CIELabColorConverter.pow(f9, 0.4166666666666667) - 0.055f : (f9 *= 12.92f);
        fArray[0] = this.clamp(f7 * 255.0f);
        fArray[1] = this.clamp(f8 * 255.0f);
        fArray[2] = this.clamp(f9 * 255.0f);
        return fArray;
    }

    static double pow(double d, double d2) {
        long l = Double.doubleToLongBits(d);
        long l2 = (long)(d2 * (double)(l - 4606921280493453312L)) + 4606921280493453312L;
        return Double.longBitsToDouble(l2);
    }

    public static enum Illuminant {
        D50(new float[]{96.4212f, 100.0f, 82.5188f}),
        D65(new float[]{95.0429f, 100.0f, 108.89f});

        private final float[] whitePoint;

        private Illuminant(float[] fArray) {
            this.whitePoint = (float[])Validate.isTrue((fArray != null && fArray.length == 3 ? 1 : 0) != 0, (Object)fArray, (String)"Bad white point definition: %s");
        }

        public float[] getWhitePoint() {
            return this.whitePoint;
        }
    }
}

