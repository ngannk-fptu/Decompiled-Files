/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.java2d.color;

import java.awt.Color;
import java.awt.color.ColorSpace;
import org.apache.xmlgraphics.java2d.color.ColorWithAlternatives;

public class CIELabColorSpace
extends ColorSpace {
    private static final long serialVersionUID = -1821569090707520704L;
    private static final float REF_X_D65 = 95.047f;
    private static final float REF_Y_D65 = 100.0f;
    private static final float REF_Z_D65 = 108.883f;
    private static final float REF_X_D50 = 96.42f;
    private static final float REF_Y_D50 = 100.0f;
    private static final float REF_Z_D50 = 82.49f;
    private static final double D = 0.20689655172413793;
    private static final double REF_A = 1.0 / (3.0 * Math.pow(0.20689655172413793, 2.0));
    private static final double REF_B = 0.13793103448275862;
    private static final double T0 = Math.pow(0.20689655172413793, 3.0);
    private float wpX;
    private float wpY;
    private float wpZ;
    private static final String CIE_LAB_ONLY_HAS_3_COMPONENTS = "CIE Lab only has 3 components!";

    public CIELabColorSpace() {
        this(CIELabColorSpace.getD65WhitePoint());
    }

    public CIELabColorSpace(float[] whitePoint) {
        super(1, 3);
        this.checkNumComponents(whitePoint, 3);
        this.wpX = whitePoint[0];
        this.wpY = whitePoint[1];
        this.wpZ = whitePoint[2];
    }

    public static float[] getD65WhitePoint() {
        return new float[]{95.047f, 100.0f, 108.883f};
    }

    public static float[] getD50WhitePoint() {
        return new float[]{96.42f, 100.0f, 82.49f};
    }

    private void checkNumComponents(float[] colorvalue) {
        this.checkNumComponents(colorvalue, this.getNumComponents());
    }

    private void checkNumComponents(float[] colorvalue, int expected) {
        if (colorvalue == null) {
            throw new NullPointerException("color value may not be null");
        }
        if (colorvalue.length != expected) {
            throw new IllegalArgumentException("Expected " + expected + " components, but got " + colorvalue.length);
        }
    }

    public float[] getWhitePoint() {
        return new float[]{this.wpX, this.wpY, this.wpZ};
    }

    @Override
    public float getMinValue(int component) {
        switch (component) {
            case 0: {
                return 0.0f;
            }
            case 1: 
            case 2: {
                return -128.0f;
            }
        }
        throw new IllegalArgumentException(CIE_LAB_ONLY_HAS_3_COMPONENTS);
    }

    @Override
    public float getMaxValue(int component) {
        switch (component) {
            case 0: {
                return 100.0f;
            }
            case 1: 
            case 2: {
                return 128.0f;
            }
        }
        throw new IllegalArgumentException(CIE_LAB_ONLY_HAS_3_COMPONENTS);
    }

    @Override
    public String getName(int component) {
        switch (component) {
            case 0: {
                return "L*";
            }
            case 1: {
                return "a*";
            }
            case 2: {
                return "b*";
            }
        }
        throw new IllegalArgumentException(CIE_LAB_ONLY_HAS_3_COMPONENTS);
    }

    @Override
    public float[] fromCIEXYZ(float[] colorvalue) {
        this.checkNumComponents(colorvalue, 3);
        float x = colorvalue[0];
        float y = colorvalue[1];
        float z = colorvalue[2];
        double varX = x / this.wpX;
        double varY = y / this.wpY;
        double varZ = z / this.wpZ;
        varX = varX > T0 ? Math.pow(varX, 0.3333333333333333) : REF_A * varX + 0.13793103448275862;
        varY = varY > T0 ? Math.pow(varY, 0.3333333333333333) : REF_A * varY + 0.13793103448275862;
        varZ = varZ > T0 ? Math.pow(varZ, 0.3333333333333333) : REF_A * varZ + 0.13793103448275862;
        float l = (float)(116.0 * varY - 16.0);
        float a = (float)(500.0 * (varX - varY));
        float b = (float)(200.0 * (varY - varZ));
        l = this.normalize(l, 0);
        a = this.normalize(a, 1);
        b = this.normalize(b, 2);
        return new float[]{l, a, b};
    }

    @Override
    public float[] fromRGB(float[] rgbvalue) {
        ColorSpace sRGB = ColorSpace.getInstance(1000);
        float[] xyz = sRGB.toCIEXYZ(rgbvalue);
        return this.fromCIEXYZ(xyz);
    }

    @Override
    public float[] toCIEXYZ(float[] colorvalue) {
        this.checkNumComponents(colorvalue);
        float l = this.denormalize(colorvalue[0], 0);
        float a = this.denormalize(colorvalue[1], 1);
        float b = this.denormalize(colorvalue[2], 2);
        return this.toCIEXYZNative(l, a, b);
    }

    public float[] toCIEXYZNative(float l, float a, float b) {
        double varY = (double)(l + 16.0f) / 116.0;
        double varX = (double)(a / 500.0f) + varY;
        double varZ = varY - (double)b / 200.0;
        varY = Math.pow(varY, 3.0) > T0 ? Math.pow(varY, 3.0) : (varY - 0.13793103448275862) / REF_A;
        varX = Math.pow(varX, 3.0) > T0 ? Math.pow(varX, 3.0) : (varX - 0.13793103448275862) / REF_A;
        varZ = Math.pow(varZ, 3.0) > T0 ? Math.pow(varZ, 3.0) : (varZ - 0.13793103448275862) / REF_A;
        float x = (float)((double)this.wpX * varX / 100.0);
        float y = (float)((double)this.wpY * varY / 100.0);
        float z = (float)((double)this.wpZ * varZ / 100.0);
        return new float[]{x, y, z};
    }

    @Override
    public float[] toRGB(float[] colorvalue) {
        ColorSpace sRGB = ColorSpace.getInstance(1000);
        float[] xyz = this.toCIEXYZ(colorvalue);
        return sRGB.fromCIEXYZ(xyz);
    }

    private float getNativeValueRange(int component) {
        return this.getMaxValue(component) - this.getMinValue(component);
    }

    private float normalize(float value, int component) {
        return (value - this.getMinValue(component)) / this.getNativeValueRange(component);
    }

    private float denormalize(float value, int component) {
        return value * this.getNativeValueRange(component) + this.getMinValue(component);
    }

    public float[] toNativeComponents(float[] comps) {
        this.checkNumComponents(comps);
        float[] nativeComps = new float[comps.length];
        int c = comps.length;
        for (int i = 0; i < c; ++i) {
            nativeComps[i] = this.denormalize(comps[i], i);
        }
        return nativeComps;
    }

    public Color toColor(float[] colorvalue, float alpha) {
        int c = colorvalue.length;
        float[] normalized = new float[c];
        for (int i = 0; i < c; ++i) {
            normalized[i] = this.normalize(colorvalue[i], i);
        }
        return new ColorWithAlternatives(this, normalized, alpha, null);
    }

    public Color toColor(float l, float a, float b, float alpha) {
        return this.toColor(new float[]{l, a, b}, alpha);
    }
}

