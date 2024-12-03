/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.palette;

import org.apache.commons.imaging.ImageWriteException;

public interface Palette {
    public int getPaletteIndex(int var1) throws ImageWriteException;

    public int getEntry(int var1);

    public int length();
}

