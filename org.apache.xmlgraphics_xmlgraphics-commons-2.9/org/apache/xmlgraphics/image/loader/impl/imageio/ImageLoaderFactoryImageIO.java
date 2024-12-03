/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.loader.impl.imageio;

import javax.imageio.ImageIO;
import org.apache.xmlgraphics.image.loader.ImageFlavor;
import org.apache.xmlgraphics.image.loader.impl.AbstractImageLoaderFactory;
import org.apache.xmlgraphics.image.loader.impl.imageio.ImageLoaderImageIO;
import org.apache.xmlgraphics.image.loader.spi.ImageLoader;

public class ImageLoaderFactoryImageIO
extends AbstractImageLoaderFactory {
    private static final ImageFlavor[] FLAVORS = new ImageFlavor[]{ImageFlavor.RENDERED_IMAGE, ImageFlavor.BUFFERED_IMAGE};

    @Override
    public String[] getSupportedMIMETypes() {
        return ImageIO.getReaderMIMETypes();
    }

    @Override
    public ImageFlavor[] getSupportedFlavors(String mime) {
        return FLAVORS;
    }

    @Override
    public ImageLoader newImageLoader(ImageFlavor targetFlavor) {
        return new ImageLoaderImageIO(targetFlavor);
    }

    @Override
    public boolean isAvailable() {
        return this.getSupportedMIMETypes().length > 0;
    }
}

