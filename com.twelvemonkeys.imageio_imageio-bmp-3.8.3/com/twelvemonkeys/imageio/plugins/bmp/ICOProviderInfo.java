/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.imageio.spi.ReaderWriterProviderInfo
 */
package com.twelvemonkeys.imageio.plugins.bmp;

import com.twelvemonkeys.imageio.spi.ReaderWriterProviderInfo;

final class ICOProviderInfo
extends ReaderWriterProviderInfo {
    ICOProviderInfo() {
        super(ICOProviderInfo.class, new String[]{"ico", "ICO"}, new String[]{"ico"}, new String[]{"image/vnd.microsoft.icon", "image/x-icon", "image/ico"}, "com.twelvemonkeys.imageio.plugins.bmp.ICOImageReader", new String[]{"com.twelvemonkeys.imageio.plugins.bmp.ICOImageReaderSpi"}, "com.twelvemonkeys.imageio.plugins.bmp.ICOImageWriter", new String[]{"com.twelvemonkeys.imageio.plugins.bmp.ICOImageWriterSpi"}, false, null, null, null, null, true, null, null, null, null);
    }
}

