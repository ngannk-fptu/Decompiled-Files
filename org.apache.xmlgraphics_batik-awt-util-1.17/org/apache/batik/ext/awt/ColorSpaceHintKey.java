/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt;

import java.awt.RenderingHints;

public final class ColorSpaceHintKey
extends RenderingHints.Key {
    public static Object VALUE_COLORSPACE_ARGB = new Object();
    public static Object VALUE_COLORSPACE_RGB = new Object();
    public static Object VALUE_COLORSPACE_GREY = new Object();
    public static Object VALUE_COLORSPACE_AGREY = new Object();
    public static Object VALUE_COLORSPACE_ALPHA = new Object();
    public static Object VALUE_COLORSPACE_ALPHA_CONVERT = new Object();
    public static final String PROPERTY_COLORSPACE = "org.apache.batik.gvt.filter.Colorspace";

    ColorSpaceHintKey(int number) {
        super(number);
    }

    @Override
    public boolean isCompatibleValue(Object val) {
        if (val == VALUE_COLORSPACE_ARGB) {
            return true;
        }
        if (val == VALUE_COLORSPACE_RGB) {
            return true;
        }
        if (val == VALUE_COLORSPACE_GREY) {
            return true;
        }
        if (val == VALUE_COLORSPACE_AGREY) {
            return true;
        }
        if (val == VALUE_COLORSPACE_ALPHA) {
            return true;
        }
        return val == VALUE_COLORSPACE_ALPHA_CONVERT;
    }
}

