/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.stream;

import com.twelvemonkeys.imageio.spi.ProviderInfo;
import com.twelvemonkeys.imageio.stream.ByteArrayImageInputStream;
import com.twelvemonkeys.imageio.stream.StreamProviderInfo;
import java.io.File;
import java.util.Locale;
import javax.imageio.spi.ImageInputStreamSpi;
import javax.imageio.stream.ImageInputStream;

public class ByteArrayImageInputStreamSpi
extends ImageInputStreamSpi {
    public ByteArrayImageInputStreamSpi() {
        this(new StreamProviderInfo());
    }

    private ByteArrayImageInputStreamSpi(ProviderInfo providerInfo) {
        super(providerInfo.getVendorName(), providerInfo.getVersion(), byte[].class);
    }

    @Override
    public ImageInputStream createInputStreamInstance(Object object, boolean bl, File file) {
        if (object instanceof byte[]) {
            return new ByteArrayImageInputStream((byte[])object);
        }
        throw new IllegalArgumentException("Expected input of type byte[]: " + object);
    }

    @Override
    public String getDescription(Locale locale) {
        return "Service provider that instantiates an ImageInputStream from a byte array";
    }
}

