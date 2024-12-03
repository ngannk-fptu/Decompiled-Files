/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.imageio.spi.ImageReaderSpiBase
 *  com.twelvemonkeys.imageio.spi.ReaderWriterProviderInfo
 *  com.twelvemonkeys.imageio.util.IIOUtil
 */
package com.twelvemonkeys.imageio.plugins.tiff;

import com.twelvemonkeys.imageio.plugins.tiff.TIFFImageReader;
import com.twelvemonkeys.imageio.plugins.tiff.TIFFProviderInfo;
import com.twelvemonkeys.imageio.spi.ImageReaderSpiBase;
import com.twelvemonkeys.imageio.spi.ReaderWriterProviderInfo;
import com.twelvemonkeys.imageio.util.IIOUtil;
import java.io.EOFException;
import java.io.IOException;
import java.util.Locale;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ServiceRegistry;
import javax.imageio.stream.ImageInputStream;

public final class TIFFImageReaderSpi
extends ImageReaderSpiBase {
    public TIFFImageReaderSpi() {
        super((ReaderWriterProviderInfo)new TIFFProviderInfo());
    }

    public void onRegistration(ServiceRegistry serviceRegistry, Class<?> clazz) {
        ImageReaderSpi imageReaderSpi = (ImageReaderSpi)IIOUtil.lookupProviderByName((ServiceRegistry)serviceRegistry, (String)"com.sun.imageio.plugins.tiff.TIFFImageReaderSpi", ImageReaderSpi.class);
        if (imageReaderSpi != null && imageReaderSpi.getVendorName() != null && (imageReaderSpi.getVendorName().startsWith("Apple") || imageReaderSpi.getVendorName().startsWith("Oracle"))) {
            serviceRegistry.setOrdering(clazz, this, imageReaderSpi);
        }
    }

    public boolean canDecodeInput(Object object) throws IOException {
        return TIFFImageReaderSpi.canDecodeAs(object, 42);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static boolean canDecodeAs(Object object, int n) throws IOException {
        if (!(object instanceof ImageInputStream)) {
            return false;
        }
        ImageInputStream imageInputStream = (ImageInputStream)object;
        imageInputStream.mark();
        try {
            byte[] byArray = new byte[4];
            imageInputStream.readFully(byArray);
            boolean bl = byArray[0] == 73 && byArray[1] == 73 && byArray[2] == (n & 0xFF) && byArray[3] == n >>> 8 || byArray[0] == 77 && byArray[1] == 77 && byArray[2] == n >>> 8 && byArray[3] == (n & 0xFF);
            return bl;
        }
        catch (EOFException eOFException) {
            boolean bl = false;
            return bl;
        }
        finally {
            imageInputStream.reset();
        }
    }

    public TIFFImageReader createReaderInstance(Object object) {
        return new TIFFImageReader((ImageReaderSpi)((Object)this));
    }

    public String getDescription(Locale locale) {
        return "Aldus/Adobe Tagged Image File Format (TIFF) image reader";
    }
}

