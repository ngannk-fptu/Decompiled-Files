/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.lang.Validate
 */
package com.twelvemonkeys.imageio.plugins.bmp;

import com.twelvemonkeys.imageio.plugins.bmp.BitmapMask;
import com.twelvemonkeys.imageio.plugins.bmp.DIBHeader;
import com.twelvemonkeys.imageio.plugins.bmp.DirectoryEntry;
import com.twelvemonkeys.lang.Validate;
import java.awt.image.BufferedImage;
import java.io.IOException;

abstract class BitmapDescriptor {
    protected final DirectoryEntry entry;
    protected final DIBHeader header;
    protected BufferedImage image;
    protected BitmapMask mask;

    public BitmapDescriptor(DirectoryEntry directoryEntry, DIBHeader dIBHeader) {
        this.entry = (DirectoryEntry)Validate.notNull((Object)directoryEntry, (String)"entry");
        this.header = (DIBHeader)Validate.notNull((Object)dIBHeader, (String)"header");
    }

    public abstract BufferedImage getImage() throws IOException;

    public final int getWidth() {
        return this.entry.getWidth();
    }

    public final int getHeight() {
        return this.entry.getHeight();
    }

    protected final int getColorCount() {
        return this.entry.getColorCount() != 0 ? this.entry.getColorCount() : 1 << this.getBitCount();
    }

    protected final int getBitCount() {
        return this.entry.getBitCount() != 0 ? this.entry.getBitCount() : this.header.getBitCount();
    }

    public String toString() {
        return this.getClass().getSimpleName() + "[" + this.entry + ", " + this.header + "]";
    }

    public final void setMask(BitmapMask bitmapMask) {
        this.mask = bitmapMask;
    }

    public final boolean hasMask() {
        return this.header.getHeight() == this.getHeight() * 2;
    }
}

