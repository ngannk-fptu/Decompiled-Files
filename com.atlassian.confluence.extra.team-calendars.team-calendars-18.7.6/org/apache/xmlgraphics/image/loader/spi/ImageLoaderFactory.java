/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.loader.spi;

import org.apache.xmlgraphics.image.loader.ImageFlavor;
import org.apache.xmlgraphics.image.loader.ImageInfo;
import org.apache.xmlgraphics.image.loader.spi.ImageLoader;

public interface ImageLoaderFactory {
    public String[] getSupportedMIMETypes();

    public ImageFlavor[] getSupportedFlavors(String var1);

    public boolean isSupported(ImageInfo var1);

    public ImageLoader newImageLoader(ImageFlavor var1);

    public int getUsagePenalty(String var1, ImageFlavor var2);

    public boolean isAvailable();
}

