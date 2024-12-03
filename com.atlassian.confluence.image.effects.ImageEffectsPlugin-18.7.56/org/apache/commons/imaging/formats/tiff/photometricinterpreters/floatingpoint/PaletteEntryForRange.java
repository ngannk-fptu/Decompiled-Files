/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff.photometricinterpreters.floatingpoint;

import java.awt.Color;
import org.apache.commons.imaging.formats.tiff.photometricinterpreters.floatingpoint.PaletteEntry;

public class PaletteEntryForRange
implements PaletteEntry {
    private final float v0;
    private final float v1;
    private final float r0;
    private final float r1;
    private final float g0;
    private final float g1;
    private final float b0;
    private final float b1;
    private final float a0;
    private final float a1;

    public PaletteEntryForRange(float v0, float v1, Color color0, Color color1) {
        this.v0 = v0;
        this.v1 = v1;
        float deltaV = v1 - v0;
        if (deltaV <= 0.0f || Float.isNaN(deltaV)) {
            throw new IllegalArgumentException("Specified values must be v0<v1");
        }
        if (color0 == null || color1 == null) {
            throw new IllegalArgumentException("Null colors not allowed");
        }
        int argb0 = color0.getRGB();
        this.a0 = argb0 >> 24 & 0xFF;
        this.r0 = argb0 >> 16 & 0xFF;
        this.g0 = argb0 >> 8 & 0xFF;
        this.b0 = argb0 & 0xFF;
        int argb1 = color1.getRGB();
        this.a1 = argb1 >> 24 & 0xFF;
        this.r1 = argb1 >> 16 & 0xFF;
        this.g1 = argb1 >> 8 & 0xFF;
        this.b1 = argb1 & 0xFF;
    }

    public PaletteEntryForRange(float v0, float v1, Color color) {
        this.v0 = v0;
        this.v1 = v1;
        float deltaV = v1 - v0;
        if (deltaV <= 0.0f || Float.isNaN(deltaV)) {
            throw new IllegalArgumentException("Specified values must be v0<v1");
        }
        if (color == null) {
            throw new IllegalArgumentException("Null colors not allowed");
        }
        int argb0 = color.getRGB();
        this.a0 = argb0 >> 24 & 0xFF;
        this.r0 = argb0 >> 16 & 0xFF;
        this.g0 = argb0 >> 8 & 0xFF;
        this.b0 = argb0 & 0xFF;
        int argb1 = color.getRGB();
        this.a1 = argb1 >> 24 & 0xFF;
        this.r1 = argb1 >> 16 & 0xFF;
        this.g1 = argb1 >> 8 & 0xFF;
        this.b1 = argb1 & 0xFF;
    }

    @Override
    public boolean isCovered(float f) {
        return this.v0 <= f && f < this.v1;
    }

    @Override
    public int getARGB(float f) {
        if (this.v0 <= f && f <= this.v1) {
            float t = (f - this.v0) / (this.v1 - this.v0);
            int a = (int)((double)(t * (this.a1 - this.a0) + this.a0) + 0.5);
            int r = (int)((double)(t * (this.r1 - this.r0) + this.r0) + 0.5);
            int g = (int)((double)(t * (this.g1 - this.g0) + this.g0) + 0.5);
            int b = (int)((double)(t * (this.b1 - this.b0) + this.b0) + 0.5);
            return ((a << 8 | r) << 8 | g) << 8 | b;
        }
        return 0;
    }

    @Override
    public Color getColor(float f) {
        if (this.v0 <= f && f <= this.v1) {
            float t = (f - this.v0) / (this.v1 - this.v0);
            int a = (int)((double)(t * (this.a1 - this.a0) + this.a0) + 0.5);
            int r = (int)((double)(t * (this.r1 - this.r0) + this.r0) + 0.5);
            int g = (int)((double)(t * (this.g1 - this.g0) + this.g0) + 0.5);
            int b = (int)((double)(t * (this.b1 - this.b0) + this.b0) + 0.5);
            return new Color(r, g, b, a);
        }
        return null;
    }

    @Override
    public boolean coversSingleEntry() {
        return false;
    }

    @Override
    public float getLowerBound() {
        return this.v0;
    }

    @Override
    public float getUpperBound() {
        return this.v1;
    }

    public String toString() {
        return "PaletteEntry for range " + this.v0 + ", " + this.v1;
    }
}

