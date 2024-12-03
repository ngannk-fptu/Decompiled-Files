/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.imageio.spi.ImageWriterSpiBase
 *  com.twelvemonkeys.imageio.spi.ReaderWriterProviderInfo
 */
package com.twelvemonkeys.imageio.plugins.icns;

import com.twelvemonkeys.imageio.plugins.icns.ICNSImageWriter;
import com.twelvemonkeys.imageio.plugins.icns.ICNSProviderInfo;
import com.twelvemonkeys.imageio.spi.ImageWriterSpiBase;
import com.twelvemonkeys.imageio.spi.ReaderWriterProviderInfo;
import java.util.Locale;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.spi.ImageWriterSpi;

public final class ICNSImageWriterSpi
extends ImageWriterSpiBase {
    public ICNSImageWriterSpi() {
        super((ReaderWriterProviderInfo)new ICNSProviderInfo());
    }

    public boolean canEncodeImage(ImageTypeSpecifier imageTypeSpecifier) {
        return true;
    }

    public ICNSImageWriter createWriterInstance(Object object) {
        return new ICNSImageWriter((ImageWriterSpi)((Object)this));
    }

    public String getDescription(Locale locale) {
        return "Apple Icon Image (icns) format Writer";
    }
}

