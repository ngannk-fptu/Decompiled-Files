/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.imageio.spi.ImageReaderSpiBase
 *  com.twelvemonkeys.imageio.spi.ReaderWriterProviderInfo
 */
package com.twelvemonkeys.imageio.plugins.tiff;

import com.twelvemonkeys.imageio.plugins.tiff.BigTIFFProviderInfo;
import com.twelvemonkeys.imageio.plugins.tiff.TIFFImageReader;
import com.twelvemonkeys.imageio.plugins.tiff.TIFFImageReaderSpi;
import com.twelvemonkeys.imageio.spi.ImageReaderSpiBase;
import com.twelvemonkeys.imageio.spi.ReaderWriterProviderInfo;
import java.io.IOException;
import java.util.Locale;
import javax.imageio.spi.ImageReaderSpi;

public final class BigTIFFImageReaderSpi
extends ImageReaderSpiBase {
    public BigTIFFImageReaderSpi() {
        super((ReaderWriterProviderInfo)new BigTIFFProviderInfo());
    }

    public boolean canDecodeInput(Object object) throws IOException {
        return TIFFImageReaderSpi.canDecodeAs(object, 43);
    }

    public TIFFImageReader createReaderInstance(Object object) {
        return new TIFFImageReader((ImageReaderSpi)((Object)this));
    }

    public String getDescription(Locale locale) {
        return "BigTIFF image reader";
    }
}

