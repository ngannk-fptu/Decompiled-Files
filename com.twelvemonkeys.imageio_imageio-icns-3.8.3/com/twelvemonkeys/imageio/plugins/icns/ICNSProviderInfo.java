/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.imageio.spi.ReaderWriterProviderInfo
 */
package com.twelvemonkeys.imageio.plugins.icns;

import com.twelvemonkeys.imageio.spi.ReaderWriterProviderInfo;

final class ICNSProviderInfo
extends ReaderWriterProviderInfo {
    ICNSProviderInfo() {
        super(ICNSProviderInfo.class, new String[]{"icns", "ICNS"}, new String[]{"icns"}, new String[]{"image/x-apple-icons"}, "com.twelvemonkeys.imageio.plugins.icns.ICNSImageReader", new String[]{"com.twelvemonkeys.imageio.plugins.icns.ICNSImageReaderSpi"}, "com.twelvemonkeys.imageio.plugins.icns.ICNSImageWriter", new String[]{"com.twelvemonkeys.imageio.plugins.icns.ICNSImageWriterSpi"}, false, null, null, null, null, true, null, null, null, null);
    }
}

