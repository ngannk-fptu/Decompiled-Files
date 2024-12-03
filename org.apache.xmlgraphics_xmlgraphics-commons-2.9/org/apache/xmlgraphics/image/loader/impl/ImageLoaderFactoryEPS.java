/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.loader.impl;

import org.apache.xmlgraphics.image.loader.ImageFlavor;
import org.apache.xmlgraphics.image.loader.impl.AbstractImageLoaderFactory;
import org.apache.xmlgraphics.image.loader.impl.ImageLoaderEPS;
import org.apache.xmlgraphics.image.loader.spi.ImageLoader;

public class ImageLoaderFactoryEPS
extends AbstractImageLoaderFactory {
    private static final String[] MIMES = new String[]{"application/postscript"};
    private static final ImageFlavor[] FLAVORS = new ImageFlavor[]{ImageFlavor.RAW_EPS};

    @Override
    public String[] getSupportedMIMETypes() {
        return MIMES;
    }

    @Override
    public ImageFlavor[] getSupportedFlavors(String mime) {
        if ("application/postscript".equals(mime)) {
            return FLAVORS;
        }
        throw new IllegalArgumentException("Unsupported MIME type: " + mime);
    }

    @Override
    public ImageLoader newImageLoader(ImageFlavor targetFlavor) {
        return new ImageLoaderEPS();
    }

    @Override
    public boolean isAvailable() {
        return true;
    }
}

