/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.java2d.color;

import java.awt.Color;
import java.awt.color.ColorSpace;
import java.util.Arrays;
import org.apache.xmlgraphics.java2d.color.ColorUtil;

public class ColorWithAlternatives
extends Color {
    private static final long serialVersionUID = -6125884937776779150L;
    private Color[] alternativeColors;

    public ColorWithAlternatives(float r, float g, float b, float a, Color[] alternativeColors) {
        super(r, g, b, a);
        this.initAlternativeColors(alternativeColors);
    }

    public ColorWithAlternatives(float r, float g, float b, Color[] alternativeColors) {
        super(r, g, b);
        this.initAlternativeColors(alternativeColors);
    }

    public ColorWithAlternatives(int rgba, boolean hasalpha, Color[] alternativeColors) {
        super(rgba, hasalpha);
        this.initAlternativeColors(alternativeColors);
    }

    public ColorWithAlternatives(int r, int g, int b, int a, Color[] alternativeColors) {
        super(r, g, b, a);
        this.initAlternativeColors(alternativeColors);
    }

    public ColorWithAlternatives(int r, int g, int b, Color[] alternativeColors) {
        super(r, g, b);
        this.initAlternativeColors(alternativeColors);
    }

    public ColorWithAlternatives(int rgb, Color[] alternativeColors) {
        super(rgb);
        this.initAlternativeColors(alternativeColors);
    }

    public ColorWithAlternatives(ColorSpace cspace, float[] components, float alpha, Color[] alternativeColors) {
        super(cspace, components, alpha);
        this.initAlternativeColors(alternativeColors);
    }

    private void initAlternativeColors(Color[] colors) {
        if (colors != null) {
            this.alternativeColors = new Color[colors.length];
            System.arraycopy(colors, 0, this.alternativeColors, 0, colors.length);
        }
    }

    public Color[] getAlternativeColors() {
        if (this.alternativeColors != null) {
            Color[] cols = new Color[this.alternativeColors.length];
            System.arraycopy(this.alternativeColors, 0, cols, 0, this.alternativeColors.length);
            return cols;
        }
        return new Color[0];
    }

    public boolean hasAlternativeColors() {
        return this.alternativeColors != null && this.alternativeColors.length > 0;
    }

    public boolean hasSameAlternativeColors(ColorWithAlternatives col) {
        Color[] alt2;
        if (!this.hasAlternativeColors()) {
            return !col.hasAlternativeColors();
        }
        if (!col.hasAlternativeColors()) {
            return false;
        }
        Color[] alt1 = this.getAlternativeColors();
        if (alt1.length != (alt2 = col.getAlternativeColors()).length) {
            return false;
        }
        int c = alt1.length;
        for (int i = 0; i < c; ++i) {
            Color c1 = alt1[i];
            Color c2 = alt2[i];
            if (ColorUtil.isSameColor(c1, c2)) continue;
            return false;
        }
        return true;
    }

    public Color getFirstAlternativeOfType(int colorSpaceType) {
        if (this.hasAlternativeColors()) {
            for (Color alternativeColor : this.alternativeColors) {
                if (alternativeColor.getColorSpace().getType() != colorSpaceType) continue;
                return alternativeColor;
            }
        }
        return null;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        if (this.alternativeColors != null) {
            hash = 37 * hash + Arrays.hashCode(this.alternativeColors);
        }
        return hash;
    }
}

