/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.color;

import java.awt.color.ColorSpace;

final class CMYKColorSpace
extends ColorSpace {
    static final ColorSpace INSTANCE = new CMYKColorSpace();
    final ColorSpace sRGB = CMYKColorSpace.getInstance(1000);

    private CMYKColorSpace() {
        super(9, 4);
    }

    public static ColorSpace getInstance() {
        return INSTANCE;
    }

    @Override
    public float[] toRGB(float[] fArray) {
        return new float[]{(1.0f - fArray[0]) * (1.0f - fArray[3]), (1.0f - fArray[1]) * (1.0f - fArray[3]), (1.0f - fArray[2]) * (1.0f - fArray[3])};
    }

    @Override
    public float[] fromRGB(float[] fArray) {
        float f = 1.0f - fArray[0];
        float f2 = 1.0f - fArray[1];
        float f3 = 1.0f - fArray[2];
        float f4 = Math.min(f, Math.min(f2, f3));
        return new float[]{f - f4, f2 - f4, f3 - f4, f4};
    }

    @Override
    public float[] toCIEXYZ(float[] fArray) {
        return this.sRGB.toCIEXYZ(this.toRGB(fArray));
    }

    @Override
    public float[] fromCIEXYZ(float[] fArray) {
        return this.sRGB.fromCIEXYZ(this.fromRGB(fArray));
    }
}

