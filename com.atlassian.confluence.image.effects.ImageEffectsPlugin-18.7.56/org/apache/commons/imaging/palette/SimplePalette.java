/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.palette;

import org.apache.commons.imaging.palette.Palette;

public class SimplePalette
implements Palette {
    private final int[] palette;

    public SimplePalette(int[] palette) {
        this.palette = palette;
    }

    @Override
    public int getPaletteIndex(int rgb) {
        for (int i = 0; i < this.palette.length; ++i) {
            if (this.palette[i] != rgb) continue;
            return i;
        }
        return -1;
    }

    @Override
    public int getEntry(int index) {
        return this.palette[index];
    }

    @Override
    public int length() {
        return this.palette.length;
    }
}

