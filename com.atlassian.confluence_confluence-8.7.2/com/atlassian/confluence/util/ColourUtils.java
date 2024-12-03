/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.math.NumberUtils
 */
package com.atlassian.confluence.util;

import java.awt.Color;
import org.apache.commons.lang3.math.NumberUtils;

public class ColourUtils {
    public static final Double EPSILON = 1.0E-5;

    public static String colorToHex(Color color) {
        return "#" + Integer.toHexString(color.getRGB()).substring(2);
    }

    public static Color hexToColor(String hexColor) {
        return Color.decode(hexColor.trim());
    }

    public static String darken(String hexColor, double amount) {
        Color color = ColourUtils.hexToColor(hexColor);
        return ColourUtils.colorToHex(ColourUtils.darken(color, amount));
    }

    public static String lighten(String hexColor, double amount) {
        Color color = ColourUtils.hexToColor(hexColor);
        return ColourUtils.colorToHex(ColourUtils.lighten(color, amount));
    }

    public static String saturate(String hexColor, double amount) {
        Color color = ColourUtils.hexToColor(hexColor);
        return ColourUtils.colorToHex(ColourUtils.saturate(color, amount));
    }

    public static String desaturate(String hexColor, double amount) {
        Color color = ColourUtils.hexToColor(hexColor);
        return ColourUtils.colorToHex(ColourUtils.desaturate(color, amount));
    }

    public static Color darken(Color color, double amount) {
        float[] hsl = ColourUtils.toHSL(color);
        hsl[2] = (float)((double)hsl[2] - amount / 100.0);
        hsl[2] = ColourUtils.clamp(hsl[2]);
        int[] rgb = ColourUtils.hslToRgb(hsl);
        return new Color(rgb[0], rgb[1], rgb[2], color.getAlpha());
    }

    public static Color lighten(Color color, double amount) {
        float[] hsl = ColourUtils.toHSL(color);
        hsl[2] = (float)((double)hsl[2] + amount / 100.0);
        hsl[2] = ColourUtils.clamp(hsl[2]);
        int[] rgb = ColourUtils.hslToRgb(hsl);
        return new Color(rgb[0], rgb[1], rgb[2], color.getAlpha());
    }

    public static Color desaturate(Color color, double amount) {
        float[] hsl = ColourUtils.toHSL(color);
        hsl[1] = (float)((double)hsl[1] - amount / 100.0);
        hsl[1] = ColourUtils.clamp(hsl[1]);
        int[] rgb = ColourUtils.hslToRgb(hsl);
        return new Color(rgb[0], rgb[1], rgb[2], color.getAlpha());
    }

    public static Color saturate(Color color, double amount) {
        float[] hsl = ColourUtils.toHSL(color);
        hsl[1] = (float)((double)hsl[1] + amount / 100.0);
        hsl[1] = ColourUtils.clamp(hsl[1]);
        int[] rgb = ColourUtils.hslToRgb(hsl);
        return new Color(rgb[0], rgb[1], rgb[2], color.getAlpha());
    }

    public static float[] toHSL(Color color) {
        float[] rgba = color.getRGBComponents(null);
        float r = rgba[0];
        float g = rgba[1];
        float b = rgba[2];
        float a = rgba[3];
        float max = NumberUtils.max((float)r, (float)g, (float)b);
        float min = NumberUtils.min((float)r, (float)g, (float)b);
        float chroma = max - min;
        float hPrime = 0.0f;
        if (ColourUtils.isAlmostEqual(chroma, 0.0f).booleanValue()) {
            hPrime = 0.0f;
        } else if (ColourUtils.isAlmostEqual(max, r).booleanValue()) {
            hPrime = (g - b) / chroma;
            if (hPrime < 0.0f) {
                hPrime += 6.0f;
            }
        } else if (ColourUtils.isAlmostEqual(max, g).booleanValue()) {
            hPrime = (b - r) / chroma + 2.0f;
        } else if (ColourUtils.isAlmostEqual(max, b).booleanValue()) {
            hPrime = (r - g) / chroma + 4.0f;
        }
        float h = 60.0f * hPrime;
        float l = (max + min) * 0.5f;
        float s = ColourUtils.isAlmostEqual(chroma, 0.0f) != false ? 0.0f : chroma / (1.0f - Math.abs(2.0f * l - 1.0f));
        return new float[]{h, s, l, a};
    }

    public static int[] hslToRgb(float[] hsl) {
        float b1;
        float g1;
        float r1;
        float h = hsl[0];
        float s = hsl[1];
        float l = hsl[2];
        float a = hsl[3];
        float chroma = (1.0f - Math.abs(2.0f * l - 1.0f)) * s;
        float hPrime = h / 60.0f;
        float hMod2 = hPrime;
        if (hMod2 >= 4.0f) {
            hMod2 -= 4.0f;
        } else if (hMod2 >= 2.0f) {
            hMod2 -= 2.0f;
        }
        float x = chroma * (1.0f - Math.abs(hMod2 - 1.0f));
        if (hPrime < 1.0f) {
            r1 = chroma;
            g1 = x;
            b1 = 0.0f;
        } else if (hPrime < 2.0f) {
            r1 = x;
            g1 = chroma;
            b1 = 0.0f;
        } else if (hPrime < 3.0f) {
            r1 = 0.0f;
            g1 = chroma;
            b1 = x;
        } else if (hPrime < 4.0f) {
            r1 = 0.0f;
            g1 = x;
            b1 = chroma;
        } else if (hPrime < 5.0f) {
            r1 = x;
            g1 = 0.0f;
            b1 = chroma;
        } else {
            r1 = chroma;
            g1 = 0.0f;
            b1 = x;
        }
        float m = l - 0.5f * chroma;
        int r = (int)((r1 + m) * 255.0f + 0.5f);
        int g = (int)((g1 + m) * 255.0f + 0.5f);
        int b = (int)((b1 + m) * 255.0f + 0.5f);
        return new int[]{r, g, b};
    }

    private static float clamp(float f) {
        return Math.min(1.0f, Math.max(0.0f, f));
    }

    private static Boolean isAlmostEqual(float a, float b) {
        return (double)Math.abs(a - b) < EPSILON;
    }
}

