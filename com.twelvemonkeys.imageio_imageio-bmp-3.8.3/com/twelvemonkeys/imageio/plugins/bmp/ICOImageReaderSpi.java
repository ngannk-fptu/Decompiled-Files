/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.imageio.spi.ImageReaderSpiBase
 *  com.twelvemonkeys.imageio.spi.ReaderWriterProviderInfo
 */
package com.twelvemonkeys.imageio.plugins.bmp;

import com.twelvemonkeys.imageio.plugins.bmp.ICOImageReader;
import com.twelvemonkeys.imageio.plugins.bmp.ICOProviderInfo;
import com.twelvemonkeys.imageio.spi.ImageReaderSpiBase;
import com.twelvemonkeys.imageio.spi.ReaderWriterProviderInfo;
import java.io.IOException;
import java.util.Locale;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

public final class ICOImageReaderSpi
extends ImageReaderSpiBase {
    public ICOImageReaderSpi() {
        super((ReaderWriterProviderInfo)new ICOProviderInfo());
    }

    public boolean canDecodeInput(Object object) throws IOException {
        return object instanceof ImageInputStream && ICOImageReaderSpi.canDecode((ImageInputStream)object, 1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static boolean canDecode(ImageInputStream imageInputStream, int n) throws IOException {
        byte[] byArray = new byte[4];
        try {
            imageInputStream.mark();
            imageInputStream.readFully(byArray);
            int n2 = imageInputStream.readByte() + (imageInputStream.readByte() << 8);
            boolean bl = byArray[0] == 0 && byArray[1] == 0 && byArray[2] == n && byArray[3] == 0 && n2 > 0;
            return bl;
        }
        finally {
            imageInputStream.reset();
        }
    }

    public ImageReader createReaderInstance(Object object) throws IOException {
        return new ICOImageReader((ImageReaderSpi)((Object)this));
    }

    public String getDescription(Locale locale) {
        return "Windows Icon Format (ICO) Reader";
    }
}

