/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.loader.impl;

import org.apache.xmlgraphics.image.loader.ImageFlavor;
import org.apache.xmlgraphics.image.loader.ImageInfo;
import org.apache.xmlgraphics.image.loader.spi.ImageLoader;
import org.apache.xmlgraphics.image.loader.spi.ImageLoaderFactory;

public abstract class AbstractImageLoaderFactory
implements ImageLoaderFactory {
    @Override
    public boolean isSupported(ImageInfo imageInfo) {
        return true;
    }

    @Override
    public int getUsagePenalty(String mime, ImageFlavor flavor) {
        ImageLoader loader = this.newImageLoader(flavor);
        return loader.getUsagePenalty();
    }
}

