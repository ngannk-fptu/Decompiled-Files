/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.imageio.spi.ImageWriterSpiBase
 *  com.twelvemonkeys.imageio.spi.ReaderWriterProviderInfo
 */
package com.twelvemonkeys.imageio.plugins.tiff;

import com.twelvemonkeys.imageio.plugins.tiff.BigTIFFProviderInfo;
import com.twelvemonkeys.imageio.plugins.tiff.TIFFImageWriter;
import com.twelvemonkeys.imageio.spi.ImageWriterSpiBase;
import com.twelvemonkeys.imageio.spi.ReaderWriterProviderInfo;
import java.util.Locale;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.spi.ImageWriterSpi;

public final class BigTIFFImageWriterSpi
extends ImageWriterSpiBase {
    public BigTIFFImageWriterSpi() {
        super((ReaderWriterProviderInfo)new BigTIFFProviderInfo());
    }

    public boolean canEncodeImage(ImageTypeSpecifier imageTypeSpecifier) {
        return true;
    }

    public TIFFImageWriter createWriterInstance(Object object) {
        return new TIFFImageWriter((ImageWriterSpi)((Object)this));
    }

    public String getDescription(Locale locale) {
        return "BigTIFF image writer";
    }
}

