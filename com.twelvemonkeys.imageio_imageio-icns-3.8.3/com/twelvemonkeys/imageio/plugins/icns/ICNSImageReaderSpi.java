/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.imageio.spi.ImageReaderSpiBase
 *  com.twelvemonkeys.imageio.spi.ReaderWriterProviderInfo
 */
package com.twelvemonkeys.imageio.plugins.icns;

import com.twelvemonkeys.imageio.plugins.icns.ICNSImageReader;
import com.twelvemonkeys.imageio.plugins.icns.ICNSProviderInfo;
import com.twelvemonkeys.imageio.spi.ImageReaderSpiBase;
import com.twelvemonkeys.imageio.spi.ReaderWriterProviderInfo;
import java.io.IOException;
import java.util.Locale;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

public final class ICNSImageReaderSpi
extends ImageReaderSpiBase {
    public ICNSImageReaderSpi() {
        super((ReaderWriterProviderInfo)new ICNSProviderInfo());
    }

    public boolean canDecodeInput(Object object) throws IOException {
        return object instanceof ImageInputStream && ICNSImageReaderSpi.canDecode((ImageInputStream)object);
    }

    private static boolean canDecode(ImageInputStream imageInputStream) throws IOException {
        try {
            imageInputStream.mark();
            boolean bl = imageInputStream.readInt() == 1768124019;
            return bl;
        }
        finally {
            imageInputStream.reset();
        }
    }

    public ImageReader createReaderInstance(Object object) throws IOException {
        return new ICNSImageReader((ImageReaderSpi)((Object)this));
    }

    public String getDescription(Locale locale) {
        return "Apple Icon Image (icns) format Reader";
    }
}

