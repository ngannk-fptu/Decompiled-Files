/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.stream;

import com.twelvemonkeys.imageio.spi.ProviderInfo;
import com.twelvemonkeys.imageio.stream.BufferedFileImageInputStream;
import com.twelvemonkeys.imageio.stream.StreamProviderInfo;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.Locale;
import javax.imageio.spi.ImageInputStreamSpi;
import javax.imageio.spi.ServiceRegistry;
import javax.imageio.stream.ImageInputStream;

public class BufferedRAFImageInputStreamSpi
extends ImageInputStreamSpi {
    public BufferedRAFImageInputStreamSpi() {
        this(new StreamProviderInfo());
    }

    private BufferedRAFImageInputStreamSpi(ProviderInfo providerInfo) {
        super(providerInfo.getVendorName(), providerInfo.getVersion(), RandomAccessFile.class);
    }

    @Override
    public void onRegistration(ServiceRegistry serviceRegistry, Class<?> clazz) {
        Iterator<ImageInputStreamSpi> iterator = serviceRegistry.getServiceProviders(ImageInputStreamSpi.class, new RAFInputFilter(), true);
        while (iterator.hasNext()) {
            ImageInputStreamSpi imageInputStreamSpi = iterator.next();
            if (imageInputStreamSpi == this) continue;
            serviceRegistry.setOrdering(ImageInputStreamSpi.class, this, imageInputStreamSpi);
        }
    }

    @Override
    public ImageInputStream createInputStreamInstance(Object object, boolean bl, File file) {
        if (object instanceof RandomAccessFile) {
            return new BufferedFileImageInputStream((RandomAccessFile)object);
        }
        throw new IllegalArgumentException("Expected input of type RandomAccessFile: " + object);
    }

    @Override
    public boolean canUseCacheFile() {
        return false;
    }

    @Override
    public String getDescription(Locale locale) {
        return "Service provider that instantiates an ImageInputStream from a RandomAccessFile";
    }

    private static class RAFInputFilter
    implements ServiceRegistry.Filter {
        private RAFInputFilter() {
        }

        @Override
        public boolean filter(Object object) {
            return ((ImageInputStreamSpi)object).getInputClass() == RandomAccessFile.class;
        }
    }
}

