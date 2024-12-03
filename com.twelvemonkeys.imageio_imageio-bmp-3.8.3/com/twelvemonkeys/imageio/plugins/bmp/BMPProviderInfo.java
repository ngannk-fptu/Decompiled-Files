/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.imageio.spi.ReaderWriterProviderInfo
 */
package com.twelvemonkeys.imageio.plugins.bmp;

import com.twelvemonkeys.imageio.spi.ReaderWriterProviderInfo;

final class BMPProviderInfo
extends ReaderWriterProviderInfo {
    BMPProviderInfo() {
        super(BMPProviderInfo.class, new String[]{"bmp", "BMP"}, new String[]{"bmp", "rle"}, new String[]{"image/bmp", "image/x-bmp", "image/vnd.microsoft.bitmap"}, "com.twelvemonkeys.imageio.plugins.bmp.BMPImageReader", new String[]{"com.twelvemonkeys.imageio.plugins.bmp.BMPImageReaderSpi"}, "com.twelvemonkeys.imageio.plugins.bmp.BMPImageWriter", new String[]{"com.twelvemonkeys.imageio.plugins.bmp.BMPImageWriterSpi"}, false, null, null, null, null, true, "javax_imageio_bmp_1.0", "com.sun.imageio.plugins.bmp.BMPMetadataFormat", null, null);
    }
}

