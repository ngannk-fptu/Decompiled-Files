/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.imageio.spi.ImageWriterSpiBase
 *  com.twelvemonkeys.imageio.spi.ReaderWriterProviderInfo
 *  com.twelvemonkeys.imageio.util.IIOUtil
 */
package com.twelvemonkeys.imageio.plugins.tiff;

import com.twelvemonkeys.imageio.plugins.tiff.TIFFImageWriter;
import com.twelvemonkeys.imageio.plugins.tiff.TIFFProviderInfo;
import com.twelvemonkeys.imageio.spi.ImageWriterSpiBase;
import com.twelvemonkeys.imageio.spi.ReaderWriterProviderInfo;
import com.twelvemonkeys.imageio.util.IIOUtil;
import java.util.Locale;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.spi.ServiceRegistry;

public final class TIFFImageWriterSpi
extends ImageWriterSpiBase {
    public TIFFImageWriterSpi() {
        super((ReaderWriterProviderInfo)new TIFFProviderInfo());
    }

    public void onRegistration(ServiceRegistry serviceRegistry, Class<?> clazz) {
        ImageWriterSpi imageWriterSpi = (ImageWriterSpi)IIOUtil.lookupProviderByName((ServiceRegistry)serviceRegistry, (String)"com.sun.imageio.plugins.tiff.TIFFImageWriterSpi", ImageWriterSpi.class);
        if (imageWriterSpi != null && imageWriterSpi.getVendorName() != null && imageWriterSpi.getVendorName().startsWith("Oracle")) {
            serviceRegistry.setOrdering(clazz, this, imageWriterSpi);
        }
    }

    public boolean canEncodeImage(ImageTypeSpecifier imageTypeSpecifier) {
        return true;
    }

    public TIFFImageWriter createWriterInstance(Object object) {
        return new TIFFImageWriter((ImageWriterSpi)((Object)this));
    }

    public String getDescription(Locale locale) {
        return "Aldus/Adobe Tagged Image File Format (TIFF) image writer";
    }
}

