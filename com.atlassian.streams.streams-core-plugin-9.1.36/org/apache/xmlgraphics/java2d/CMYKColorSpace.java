/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.java2d;

import java.awt.color.ColorSpace;

public class CMYKColorSpace
extends ColorSpace {
    private static final long serialVersionUID = 2925508946083542974L;
    private static CMYKColorSpace instance;

    protected CMYKColorSpace(int type, int numcomponents) {
        super(type, numcomponents);
    }

    public static CMYKColorSpace getInstance() {
        if (instance == null) {
            instance = new CMYKColorSpace(9, 4);
        }
        return instance;
    }

    @Override
    public float[] toRGB(float[] colorvalue) {
        return new float[]{(1.0f - colorvalue[0]) * (1.0f - colorvalue[3]), (1.0f - colorvalue[1]) * (1.0f - colorvalue[3]), (1.0f - colorvalue[2]) * (1.0f - colorvalue[3])};
    }

    @Override
    public float[] fromRGB(float[] rgbvalue) {
        throw new UnsupportedOperationException("NYI");
    }

    @Override
    public float[] toCIEXYZ(float[] colorvalue) {
        throw new UnsupportedOperationException("NYI");
    }

    @Override
    public float[] fromCIEXYZ(float[] colorvalue) {
        throw new UnsupportedOperationException("NYI");
    }
}

