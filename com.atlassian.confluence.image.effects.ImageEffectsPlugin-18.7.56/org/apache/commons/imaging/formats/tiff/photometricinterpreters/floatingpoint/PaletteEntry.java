/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff.photometricinterpreters.floatingpoint;

import java.awt.Color;

public interface PaletteEntry {
    public boolean coversSingleEntry();

    public boolean isCovered(float var1);

    public int getARGB(float var1);

    public Color getColor(float var1);

    public float getLowerBound();

    public float getUpperBound();
}

