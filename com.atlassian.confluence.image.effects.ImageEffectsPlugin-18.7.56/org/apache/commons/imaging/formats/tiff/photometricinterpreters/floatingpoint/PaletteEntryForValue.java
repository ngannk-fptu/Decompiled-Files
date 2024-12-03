/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff.photometricinterpreters.floatingpoint;

import java.awt.Color;
import org.apache.commons.imaging.formats.tiff.photometricinterpreters.floatingpoint.PaletteEntry;

public class PaletteEntryForValue
implements PaletteEntry {
    private final float value;
    private final int iColor;
    private final Color color;
    private boolean isNull;

    public PaletteEntryForValue(float value, Color color) {
        if (color == null) {
            throw new IllegalArgumentException("Null colors not allowed");
        }
        this.value = value;
        this.color = color;
        this.iColor = color.getRGB();
        this.isNull = Float.isNaN(value);
    }

    @Override
    public boolean isCovered(float f) {
        if (this.isNull) {
            return Float.isNaN(f);
        }
        return f == this.value;
    }

    @Override
    public int getARGB(float f) {
        if (this.isNull && Float.isNaN(f)) {
            return this.iColor;
        }
        if (f == this.value) {
            return this.iColor;
        }
        return 0;
    }

    @Override
    public Color getColor(float f) {
        if (this.isNull && Float.isNaN(f)) {
            return this.color;
        }
        if (f == this.value) {
            return this.color;
        }
        return null;
    }

    @Override
    public boolean coversSingleEntry() {
        return true;
    }

    @Override
    public float getLowerBound() {
        return this.value;
    }

    @Override
    public float getUpperBound() {
        return this.value;
    }

    public String toString() {
        return "PaletteEntry for single value" + this.value;
    }
}

