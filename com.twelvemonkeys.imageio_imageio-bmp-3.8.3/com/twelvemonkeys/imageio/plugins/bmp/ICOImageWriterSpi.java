/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.imageio.spi.ImageWriterSpiBase
 *  com.twelvemonkeys.imageio.spi.ReaderWriterProviderInfo
 */
package com.twelvemonkeys.imageio.plugins.bmp;

import com.twelvemonkeys.imageio.plugins.bmp.ICOImageWriter;
import com.twelvemonkeys.imageio.plugins.bmp.ICOProviderInfo;
import com.twelvemonkeys.imageio.spi.ImageWriterSpiBase;
import com.twelvemonkeys.imageio.spi.ReaderWriterProviderInfo;
import java.util.Locale;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.spi.ImageWriterSpi;

public final class ICOImageWriterSpi
extends ImageWriterSpiBase {
    public ICOImageWriterSpi() {
        super((ReaderWriterProviderInfo)new ICOProviderInfo());
    }

    public boolean canEncodeImage(ImageTypeSpecifier imageTypeSpecifier) {
        return imageTypeSpecifier.getBufferedImageType() == 6;
    }

    public ICOImageWriter createWriterInstance(Object object) {
        return new ICOImageWriter((ImageWriterSpi)((Object)this));
    }

    public String getDescription(Locale locale) {
        return "Windows Icon Format (ICO) Writer";
    }
}

