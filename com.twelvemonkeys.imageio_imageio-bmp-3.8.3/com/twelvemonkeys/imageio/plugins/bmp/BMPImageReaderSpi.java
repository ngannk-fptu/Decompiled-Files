/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.imageio.spi.ImageReaderSpiBase
 *  com.twelvemonkeys.imageio.spi.ReaderWriterProviderInfo
 *  com.twelvemonkeys.imageio.util.IIOUtil
 */
package com.twelvemonkeys.imageio.plugins.bmp;

import com.twelvemonkeys.imageio.plugins.bmp.BMPImageReader;
import com.twelvemonkeys.imageio.plugins.bmp.BMPProviderInfo;
import com.twelvemonkeys.imageio.spi.ImageReaderSpiBase;
import com.twelvemonkeys.imageio.spi.ReaderWriterProviderInfo;
import com.twelvemonkeys.imageio.util.IIOUtil;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Locale;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ServiceRegistry;
import javax.imageio.stream.ImageInputStream;

public final class BMPImageReaderSpi
extends ImageReaderSpiBase {
    public BMPImageReaderSpi() {
        super((ReaderWriterProviderInfo)new BMPProviderInfo());
    }

    public void onRegistration(ServiceRegistry serviceRegistry, Class<?> clazz) {
        ImageReaderSpi imageReaderSpi = (ImageReaderSpi)IIOUtil.lookupProviderByName((ServiceRegistry)serviceRegistry, (String)"com.sun.imageio.plugins.bmp.BMPImageReaderSpi", ImageReaderSpi.class);
        if (imageReaderSpi != null) {
            serviceRegistry.setOrdering(clazz, this, imageReaderSpi);
        }
    }

    public boolean canDecodeInput(Object object) throws IOException {
        return object instanceof ImageInputStream && BMPImageReaderSpi.canDecode((ImageInputStream)object);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static boolean canDecode(ImageInputStream imageInputStream) throws IOException {
        byte[] byArray = new byte[18];
        try {
            imageInputStream.mark();
            imageInputStream.readFully(byArray);
            if (byArray[0] != 66 || byArray[1] != 77) {
                boolean bl = false;
                return bl;
            }
            ByteBuffer byteBuffer = ByteBuffer.wrap(byArray);
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            int n = byteBuffer.getInt(2);
            if (n <= 0) {
                boolean bl = false;
                return bl;
            }
            int n2 = byteBuffer.getInt(10);
            if (n2 <= 0) {
                boolean bl = false;
                return bl;
            }
            int n3 = byteBuffer.getInt(14);
            switch (n3) {
                case 12: 
                case 16: 
                case 40: 
                case 52: 
                case 56: 
                case 64: 
                case 108: 
                case 124: {
                    boolean bl = true;
                    return bl;
                }
            }
            boolean bl = false;
            return bl;
        }
        finally {
            imageInputStream.reset();
        }
    }

    public ImageReader createReaderInstance(Object object) {
        return new BMPImageReader((ImageReaderSpi)((Object)this));
    }

    public String getDescription(Locale locale) {
        return "Windows Device Independent Bitmap Format (BMP) Reader";
    }
}

