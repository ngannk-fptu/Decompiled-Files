/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.plugins.bmp;

import com.twelvemonkeys.imageio.plugins.bmp.BitmapDescriptor;
import com.twelvemonkeys.imageio.plugins.bmp.BitmapIndexed;
import com.twelvemonkeys.imageio.plugins.bmp.DIBHeader;
import com.twelvemonkeys.imageio.plugins.bmp.DirectoryEntry;
import java.awt.image.BufferedImage;

class BitmapMask
extends BitmapDescriptor {
    protected final BitmapIndexed bitMask;

    public BitmapMask(DirectoryEntry directoryEntry, DIBHeader dIBHeader) {
        super(directoryEntry, dIBHeader);
        this.bitMask = new BitmapIndexed(directoryEntry, dIBHeader);
    }

    boolean isTransparent(int n, int n2) {
        return this.bitMask.bits[n + n2 * this.getWidth()] != 0;
    }

    @Override
    public BufferedImage getImage() {
        return this.bitMask.getImage();
    }
}

