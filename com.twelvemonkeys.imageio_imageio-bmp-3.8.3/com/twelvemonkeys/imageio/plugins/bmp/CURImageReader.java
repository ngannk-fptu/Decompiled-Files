/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.plugins.bmp;

import com.twelvemonkeys.imageio.plugins.bmp.CURImageReaderSpi;
import com.twelvemonkeys.imageio.plugins.bmp.DIBImageReader;
import com.twelvemonkeys.imageio.plugins.bmp.DirectoryEntry;
import java.awt.Point;
import java.io.IOException;
import javax.imageio.spi.ImageReaderSpi;

public final class CURImageReader
extends DIBImageReader {
    public CURImageReader() {
        super((ImageReaderSpi)((Object)new CURImageReaderSpi()));
    }

    protected CURImageReader(ImageReaderSpi imageReaderSpi) {
        super(imageReaderSpi);
    }

    public final Point getHotSpot(int n) throws IOException {
        DirectoryEntry.CUREntry cUREntry = (DirectoryEntry.CUREntry)this.getEntry(n);
        return cUREntry.getHotspot();
    }
}

