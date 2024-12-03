/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.plugins.bmp;

import com.twelvemonkeys.imageio.plugins.bmp.DIBImageReader;
import com.twelvemonkeys.imageio.plugins.bmp.ICOImageReaderSpi;
import javax.imageio.spi.ImageReaderSpi;

public final class ICOImageReader
extends DIBImageReader {
    public ICOImageReader() {
        super((ImageReaderSpi)((Object)new ICOImageReaderSpi()));
    }

    protected ICOImageReader(ImageReaderSpi imageReaderSpi) {
        super(imageReaderSpi);
    }
}

