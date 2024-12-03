/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.imageio.spi.ImageWriterSpiBase
 *  com.twelvemonkeys.imageio.spi.ReaderWriterProviderInfo
 *  com.twelvemonkeys.imageio.util.IIOUtil
 */
package com.twelvemonkeys.imageio.plugins.bmp;

import com.twelvemonkeys.imageio.plugins.bmp.BMPImageWriter;
import com.twelvemonkeys.imageio.plugins.bmp.BMPProviderInfo;
import com.twelvemonkeys.imageio.spi.ImageWriterSpiBase;
import com.twelvemonkeys.imageio.spi.ReaderWriterProviderInfo;
import com.twelvemonkeys.imageio.util.IIOUtil;
import java.util.Locale;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.spi.ServiceRegistry;

public final class BMPImageWriterSpi
extends ImageWriterSpiBase {
    public BMPImageWriterSpi() {
        super((ReaderWriterProviderInfo)new BMPProviderInfo());
    }

    public void onRegistration(ServiceRegistry serviceRegistry, Class<?> clazz) {
        ImageWriterSpi imageWriterSpi = (ImageWriterSpi)IIOUtil.lookupProviderByName((ServiceRegistry)serviceRegistry, (String)"com.sun.imageio.plugins.bmp.BMPImageWriterSpi", ImageWriterSpi.class);
        if (imageWriterSpi != null && imageWriterSpi.getVendorName() != null) {
            serviceRegistry.setOrdering(clazz, imageWriterSpi, this);
        }
    }

    public boolean canEncodeImage(ImageTypeSpecifier imageTypeSpecifier) {
        return imageTypeSpecifier.getBufferedImageType() == 6;
    }

    public BMPImageWriter createWriterInstance(Object object) {
        return new BMPImageWriter((ImageWriterSpi)((Object)this));
    }

    public String getDescription(Locale locale) {
        return "Windows Device Independent Bitmap Format (BMP) Writer";
    }
}

