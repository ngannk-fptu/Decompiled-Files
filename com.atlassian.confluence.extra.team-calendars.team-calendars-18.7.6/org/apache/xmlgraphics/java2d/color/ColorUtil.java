/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.java2d.color;

import java.awt.Color;
import org.apache.xmlgraphics.java2d.color.ColorWithAlternatives;
import org.apache.xmlgraphics.java2d.color.DeviceCMYKColorSpace;

public final class ColorUtil {
    private ColorUtil() {
    }

    public static Color lightenColor(Color col, float factor) {
        float[] cols = new float[4];
        cols = col.getRGBComponents(cols);
        if (factor > 0.0f) {
            cols[0] = cols[0] + (1.0f - cols[0]) * factor;
            cols[1] = cols[1] + (1.0f - cols[1]) * factor;
            cols[2] = cols[2] + (1.0f - cols[2]) * factor;
        } else {
            cols[0] = cols[0] - cols[0] * -factor;
            cols[1] = cols[1] - cols[1] * -factor;
            cols[2] = cols[2] - cols[2] * -factor;
        }
        return new Color(cols[0], cols[1], cols[2], cols[3]);
    }

    public static boolean isGray(Color col) {
        return col.getRed() == col.getBlue() && col.getRed() == col.getGreen();
    }

    public static Color toCMYKGrayColor(float black) {
        float[] cmyk = new float[]{0.0f, 0.0f, 0.0f, 1.0f - black};
        return DeviceCMYKColorSpace.createCMYKColor(cmyk);
    }

    public static Color toSRGBColor(Color col) {
        if (col.getColorSpace().isCS_sRGB()) {
            return col;
        }
        float[] comps = col.getColorComponents(null);
        float[] srgb = col.getColorSpace().toRGB(comps);
        comps = col.getComponents(null);
        float alpha = comps[comps.length - 1];
        return new Color(srgb[0], srgb[1], srgb[2], alpha);
    }

    public static boolean isSameColor(Color col1, Color col2) {
        float[] comps2;
        if (!col1.equals(col2)) {
            return false;
        }
        Class<Object> cl1 = col1.getClass();
        if (col1 instanceof ColorWithAlternatives && !((ColorWithAlternatives)col1).hasAlternativeColors()) {
            cl1 = Color.class;
        }
        Class<Object> cl2 = col2.getClass();
        if (col2 instanceof ColorWithAlternatives && !((ColorWithAlternatives)col2).hasAlternativeColors()) {
            cl2 = Color.class;
        }
        if (cl1 != cl2) {
            return false;
        }
        if (!col1.getColorSpace().equals(col2.getColorSpace())) {
            return false;
        }
        float[] comps1 = col1.getComponents(null);
        if (comps1.length != (comps2 = col2.getComponents(null)).length) {
            return false;
        }
        int c = comps1.length;
        for (int i = 0; i < c; ++i) {
            if (comps1[i] == comps2[i]) continue;
            return false;
        }
        if (col1 instanceof ColorWithAlternatives && col2 instanceof ColorWithAlternatives) {
            ColorWithAlternatives ca1 = (ColorWithAlternatives)col1;
            ColorWithAlternatives ca2 = (ColorWithAlternatives)col2;
            return ca1.hasSameAlternativeColors(ca2);
        }
        return true;
    }
}

