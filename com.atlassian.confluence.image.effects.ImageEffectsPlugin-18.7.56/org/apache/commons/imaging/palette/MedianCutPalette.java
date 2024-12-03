/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.palette;

import org.apache.commons.imaging.palette.ColorGroup;
import org.apache.commons.imaging.palette.SimplePalette;

class MedianCutPalette
extends SimplePalette {
    private final ColorGroup root;

    MedianCutPalette(ColorGroup root, int[] palette) {
        super(palette);
        this.root = root;
    }

    @Override
    public int getPaletteIndex(int rgb) {
        ColorGroup cg = this.root;
        while (cg.cut != null) {
            cg = cg.cut.getColorGroup(rgb);
        }
        return cg.paletteIndex;
    }
}

