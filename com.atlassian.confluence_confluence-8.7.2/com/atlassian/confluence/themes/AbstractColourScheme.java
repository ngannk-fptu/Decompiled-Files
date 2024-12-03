/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.themes;

import com.atlassian.confluence.themes.AUIDefaultColorScheme;
import com.atlassian.confluence.themes.ColourScheme;
import com.atlassian.confluence.util.ColourUtils;
import java.awt.Color;

public abstract class AbstractColourScheme
implements ColourScheme {
    private static final Double EPSILON = 1.0E-5;

    @Override
    public String get(String colourName, double lightnessDelta) {
        if (lightnessDelta < -1.0 || lightnessDelta > 1.0) {
            throw new IllegalArgumentException("The lightness delta must be -1 <= n <= 1.");
        }
        String originalColour = this.get(colourName);
        if (originalColour == null) {
            throw new IllegalArgumentException("The colour name provided does not exist.");
        }
        if (Math.abs(lightnessDelta - 0.0) < EPSILON) {
            return originalColour;
        }
        double convertedLightness = Math.abs(lightnessDelta) * 100.0;
        return lightnessDelta < 0.0 ? ColourUtils.darken(originalColour, convertedLightness) : ColourUtils.lighten(originalColour, convertedLightness);
    }

    @Deprecated
    public static void rgbToHsl(int rgb, float[] hsl) {
        float[] toHSL = ColourUtils.toHSL(new Color(rgb));
        hsl[0] = toHSL[0];
        hsl[1] = toHSL[1];
        hsl[2] = toHSL[2];
    }

    @Deprecated
    public static int hslToRgb(float[] hsl) {
        float[] hsla = new float[4];
        hsla[0] = hsl[0];
        hsla[1] = hsl[1];
        hsla[2] = hsl[2];
        int[] toRGB = ColourUtils.hslToRgb(hsla);
        return new Color(toRGB[0], toRGB[1], toRGB[2]).getRGB();
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (object == this) {
            return true;
        }
        if (!(object instanceof ColourScheme)) {
            return false;
        }
        ColourScheme colourScheme = (ColourScheme)object;
        for (String colour : ColourScheme.ORDERED_KEYS) {
            String colourSchemeColour = colourScheme.get(colour);
            if (!(colourSchemeColour == null ? this.get(colour) != null : !colourSchemeColour.equals(this.get(colour)))) continue;
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = 0;
        for (String colour : ColourScheme.ORDERED_KEYS) {
            if (this.get(colour) == null) continue;
            result = 31 * result + this.get(colour).hashCode();
        }
        return result;
    }

    @Override
    public boolean isDefaultColourScheme() {
        ColourScheme defaultScheme = AUIDefaultColorScheme.getInstance();
        if (defaultScheme == this) {
            return true;
        }
        for (String colour : ColourScheme.ORDERED_KEYS) {
            String defaultSchemeColour = defaultScheme.get(colour);
            String colourSchemeColour = this.get(colour);
            if ((defaultSchemeColour != null || colourSchemeColour == null) && (defaultSchemeColour == null || defaultSchemeColour.equals(colourSchemeColour))) continue;
            return false;
        }
        return true;
    }
}

