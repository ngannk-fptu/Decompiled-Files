/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.loader.impl;

import org.apache.xmlgraphics.image.loader.ImageFlavor;
import org.apache.xmlgraphics.image.loader.impl.AbstractImageLoaderFactory;
import org.apache.xmlgraphics.image.loader.impl.ImageLoaderPNG;
import org.apache.xmlgraphics.image.loader.spi.ImageLoader;

public class ImageLoaderFactoryPNG
extends AbstractImageLoaderFactory {
    private static final String[] MIMES = new String[]{"image/png"};
    private static final ImageFlavor[] FLAVORS = new ImageFlavor[]{ImageFlavor.RENDERED_IMAGE};

    @Override
    public String[] getSupportedMIMETypes() {
        return MIMES;
    }

    @Override
    public ImageFlavor[] getSupportedFlavors(String mime) {
        if ("image/png".equals(mime)) {
            return FLAVORS;
        }
        throw new IllegalArgumentException("Unsupported MIME type: " + mime);
    }

    @Override
    public ImageLoader newImageLoader(ImageFlavor targetFlavor) {
        return new ImageLoaderPNG();
    }

    @Override
    public boolean isAvailable() {
        return true;
    }
}

