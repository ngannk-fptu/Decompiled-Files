/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.stream;

import com.twelvemonkeys.imageio.spi.ProviderInfo;
import com.twelvemonkeys.imageio.stream.BufferedFileImageInputStream;
import com.twelvemonkeys.imageio.stream.StreamProviderInfo;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Locale;
import javax.imageio.spi.ImageInputStreamSpi;
import javax.imageio.stream.FileCacheImageInputStream;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;

public class URLImageInputStreamSpi
extends ImageInputStreamSpi {
    public URLImageInputStreamSpi() {
        this(new StreamProviderInfo());
    }

    private URLImageInputStreamSpi(ProviderInfo providerInfo) {
        super(providerInfo.getVendorName(), providerInfo.getVersion(), URL.class);
    }

    @Override
    public ImageInputStream createInputStreamInstance(Object object, boolean bl, File file) throws IOException {
        if (object instanceof URL) {
            URL uRL = (URL)object;
            if ("file".equals(uRL.getProtocol())) {
                try {
                    return new BufferedFileImageInputStream(new File(uRL.toURI()));
                }
                catch (URISyntaxException uRISyntaxException) {
                    uRISyntaxException.printStackTrace();
                }
            }
            final InputStream inputStream = uRL.openStream();
            if (bl) {
                return new FileCacheImageInputStream(inputStream, file){

                    @Override
                    public void close() throws IOException {
                        try {
                            super.close();
                        }
                        finally {
                            inputStream.close();
                        }
                    }
                };
            }
            return new MemoryCacheImageInputStream(inputStream){

                @Override
                public void close() throws IOException {
                    try {
                        super.close();
                    }
                    finally {
                        inputStream.close();
                    }
                }
            };
        }
        throw new IllegalArgumentException("Expected input of type URL: " + object);
    }

    @Override
    public boolean canUseCacheFile() {
        return true;
    }

    @Override
    public String getDescription(Locale locale) {
        return "Service provider that instantiates an ImageInputStream from a URL";
    }
}

