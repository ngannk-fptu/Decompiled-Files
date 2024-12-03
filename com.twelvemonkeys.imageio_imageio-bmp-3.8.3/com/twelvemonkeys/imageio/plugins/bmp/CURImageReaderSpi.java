/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.imageio.spi.ImageReaderSpiBase
 *  com.twelvemonkeys.imageio.spi.ReaderWriterProviderInfo
 */
package com.twelvemonkeys.imageio.plugins.bmp;

import com.twelvemonkeys.imageio.plugins.bmp.CURImageReader;
import com.twelvemonkeys.imageio.plugins.bmp.CURProviderInfo;
import com.twelvemonkeys.imageio.plugins.bmp.ICOImageReaderSpi;
import com.twelvemonkeys.imageio.spi.ImageReaderSpiBase;
import com.twelvemonkeys.imageio.spi.ReaderWriterProviderInfo;
import java.io.IOException;
import java.util.Locale;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

public final class CURImageReaderSpi
extends ImageReaderSpiBase {
    public CURImageReaderSpi() {
        super((ReaderWriterProviderInfo)new CURProviderInfo());
    }

    public boolean canDecodeInput(Object object) throws IOException {
        return object instanceof ImageInputStream && ICOImageReaderSpi.canDecode((ImageInputStream)object, 2);
    }

    public ImageReader createReaderInstance(Object object) throws IOException {
        return new CURImageReader((ImageReaderSpi)((Object)this));
    }

    public String getDescription(Locale locale) {
        return "Windows Cursor Format (CUR) Reader";
    }
}

