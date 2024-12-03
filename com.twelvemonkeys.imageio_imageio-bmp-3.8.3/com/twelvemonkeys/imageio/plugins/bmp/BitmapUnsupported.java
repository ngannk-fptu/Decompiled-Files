/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.plugins.bmp;

import com.twelvemonkeys.imageio.plugins.bmp.BitmapDescriptor;
import com.twelvemonkeys.imageio.plugins.bmp.DIBHeader;
import com.twelvemonkeys.imageio.plugins.bmp.DirectoryEntry;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.IIOException;

class BitmapUnsupported
extends BitmapDescriptor {
    private String message;

    public BitmapUnsupported(DirectoryEntry directoryEntry, DIBHeader dIBHeader, String string) {
        super(directoryEntry, dIBHeader);
        this.message = string;
    }

    @Override
    public BufferedImage getImage() throws IOException {
        throw new IIOException(this.message);
    }
}

