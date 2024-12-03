/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.imageio.spi.ReaderWriterProviderInfo
 */
package com.twelvemonkeys.imageio.plugins.bmp;

import com.twelvemonkeys.imageio.spi.ReaderWriterProviderInfo;

final class CURProviderInfo
extends ReaderWriterProviderInfo {
    CURProviderInfo() {
        super(CURProviderInfo.class, new String[]{"cur", "CUR"}, new String[]{"cur"}, new String[]{"image/vnd.microsoft.cursor", "image/x-cursor", "image/cursor"}, "com.twelvemonkeys.imageio.plugins.bmp.CURImageReader", new String[]{"com.twelvemonkeys.imageio.plugins.bmp.CURImageReaderSpi"}, null, null, false, null, null, null, null, true, null, null, null, null);
    }
}

