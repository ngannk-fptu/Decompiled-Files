/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.java2d.color;

import java.awt.Color;
import java.awt.color.ColorSpace;
import org.apache.xmlgraphics.java2d.color.ColorSpaceOrigin;

public class NamedColorSpace
extends ColorSpace
implements ColorSpaceOrigin {
    private static final long serialVersionUID = -8957543225908514658L;
    private final String name;
    private final float[] xyz;
    private final String profileName;
    private final String profileURI;

    public NamedColorSpace(String name, float[] xyz) {
        this(name, xyz, null, null);
    }

    public NamedColorSpace(String name, float[] xyz, String profileName, String profileURI) {
        super(6, 1);
        this.checkNumComponents(xyz, 3);
        if (name == null || name.trim().length() == 0) {
            throw new IllegalArgumentException("No name provided for named color space");
        }
        this.name = name.trim();
        this.xyz = new float[3];
        System.arraycopy(xyz, 0, this.xyz, 0, 3);
        this.profileName = profileName;
        this.profileURI = profileURI;
    }

    public NamedColorSpace(String name, Color color, String profileName, String profileURI) {
        this(name, color.getColorSpace().toCIEXYZ(color.getColorComponents(null)), profileName, profileURI);
    }

    public NamedColorSpace(String name, Color color) {
        this(name, color.getColorSpace().toCIEXYZ(color.getColorComponents(null)), null, null);
    }

    private void checkNumComponents(float[] colorvalue, int expected) {
        if (colorvalue == null) {
            throw new NullPointerException("color value may not be null");
        }
        if (colorvalue.length != expected) {
            throw new IllegalArgumentException("Expected " + expected + " components, but got " + colorvalue.length);
        }
    }

    public String getColorName() {
        return this.name;
    }

    @Override
    public String getProfileName() {
        return this.profileName;
    }

    @Override
    public String getProfileURI() {
        return this.profileURI;
    }

    public float[] getXYZ() {
        float[] result = new float[this.xyz.length];
        System.arraycopy(this.xyz, 0, result, 0, this.xyz.length);
        return result;
    }

    public Color getRGBColor() {
        float[] comps = this.toRGB(this.xyz);
        return new Color(comps[0], comps[1], comps[2]);
    }

    @Override
    public float getMinValue(int component) {
        return this.getMaxValue(component);
    }

    @Override
    public float getMaxValue(int component) {
        switch (component) {
            case 0: {
                return 1.0f;
            }
        }
        throw new IllegalArgumentException("A named color space only has 1 component!");
    }

    @Override
    public String getName(int component) {
        switch (component) {
            case 0: {
                return "Tint";
            }
        }
        throw new IllegalArgumentException("A named color space only has 1 component!");
    }

    @Override
    public float[] fromCIEXYZ(float[] colorvalue) {
        return new float[]{1.0f};
    }

    @Override
    public float[] fromRGB(float[] rgbvalue) {
        return new float[]{1.0f};
    }

    @Override
    public float[] toCIEXYZ(float[] colorvalue) {
        float[] ret = new float[3];
        System.arraycopy(this.xyz, 0, ret, 0, this.xyz.length);
        return ret;
    }

    @Override
    public float[] toRGB(float[] colorvalue) {
        ColorSpace sRGB = ColorSpace.getInstance(1000);
        return sRGB.fromCIEXYZ(this.xyz);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof NamedColorSpace)) {
            return false;
        }
        NamedColorSpace other = (NamedColorSpace)obj;
        if (!this.name.equals(other.name)) {
            return false;
        }
        int c = this.xyz.length;
        for (int i = 0; i < c; ++i) {
            if (this.xyz[i] == other.xyz[i]) continue;
            return false;
        }
        return true;
    }

    public int hashCode() {
        return (this.profileName + this.name).hashCode();
    }

    public String toString() {
        return "Named Color Space: " + this.getColorName();
    }
}

