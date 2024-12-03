/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.stream;

import com.twelvemonkeys.imageio.spi.ProviderInfo;
import com.twelvemonkeys.imageio.stream.BufferedFileImageInputStream;
import com.twelvemonkeys.imageio.stream.StreamProviderInfo;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Locale;
import javax.imageio.spi.ImageInputStreamSpi;
import javax.imageio.spi.ServiceRegistry;
import javax.imageio.stream.ImageInputStream;

public class BufferedFileImageInputStreamSpi
extends ImageInputStreamSpi {
    public BufferedFileImageInputStreamSpi() {
        this(new StreamProviderInfo());
    }

    private BufferedFileImageInputStreamSpi(ProviderInfo providerInfo) {
        super(providerInfo.getVendorName(), providerInfo.getVersion(), File.class);
    }

    @Override
    public void onRegistration(ServiceRegistry serviceRegistry, Class<?> clazz) {
        Iterator<ImageInputStreamSpi> iterator = serviceRegistry.getServiceProviders(ImageInputStreamSpi.class, new FileInputFilter(), true);
        while (iterator.hasNext()) {
            ImageInputStreamSpi imageInputStreamSpi = iterator.next();
            if (imageInputStreamSpi == this) continue;
            serviceRegistry.setOrdering(ImageInputStreamSpi.class, this, imageInputStreamSpi);
        }
    }

    @Override
    public ImageInputStream createInputStreamInstance(Object object, boolean bl, File file) {
        if (object instanceof File) {
            try {
                return new BufferedFileImageInputStream((File)object);
            }
            catch (FileNotFoundException fileNotFoundException) {
                return null;
            }
        }
        throw new IllegalArgumentException("Expected input of type File: " + object);
    }

    @Override
    public boolean canUseCacheFile() {
        return false;
    }

    @Override
    public String getDescription(Locale locale) {
        return "Service provider that instantiates an ImageInputStream from a File";
    }

    private static class FileInputFilter
    implements ServiceRegistry.Filter {
        private FileInputFilter() {
        }

        @Override
        public boolean filter(Object object) {
            return ((ImageInputStreamSpi)object).getInputClass() == File.class;
        }
    }
}

