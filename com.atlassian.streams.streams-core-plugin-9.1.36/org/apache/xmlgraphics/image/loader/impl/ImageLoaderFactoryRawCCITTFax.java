/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.xmlgraphics.image.loader.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlgraphics.image.loader.ImageFlavor;
import org.apache.xmlgraphics.image.loader.ImageInfo;
import org.apache.xmlgraphics.image.loader.impl.AbstractImageLoaderFactory;
import org.apache.xmlgraphics.image.loader.impl.ImageLoaderRawCCITTFax;
import org.apache.xmlgraphics.image.loader.spi.ImageLoader;

public class ImageLoaderFactoryRawCCITTFax
extends AbstractImageLoaderFactory {
    private transient Log log = LogFactory.getLog(ImageLoaderFactoryRawCCITTFax.class);
    private static final String[] MIMES = new String[]{"image/tiff"};
    private static final ImageFlavor[][] FLAVORS = new ImageFlavor[][]{{ImageFlavor.RAW_CCITTFAX}};

    public static String getMimeForRawFlavor(ImageFlavor flavor) {
        int ci = FLAVORS.length;
        for (int i = 0; i < ci; ++i) {
            int cj = FLAVORS[i].length;
            for (int j = 0; j < cj; ++j) {
                if (!FLAVORS[i][j].equals(flavor)) continue;
                return MIMES[i];
            }
        }
        throw new IllegalArgumentException("ImageFlavor is not a \"raw\" flavor: " + flavor);
    }

    @Override
    public String[] getSupportedMIMETypes() {
        return MIMES;
    }

    @Override
    public ImageFlavor[] getSupportedFlavors(String mime) {
        int c = MIMES.length;
        for (int i = 0; i < c; ++i) {
            if (!MIMES[i].equals(mime)) continue;
            return FLAVORS[i];
        }
        throw new IllegalArgumentException("Unsupported MIME type: " + mime);
    }

    @Override
    public ImageLoader newImageLoader(ImageFlavor targetFlavor) {
        if (targetFlavor.equals(ImageFlavor.RAW_CCITTFAX)) {
            return new ImageLoaderRawCCITTFax();
        }
        throw new IllegalArgumentException("Unsupported image flavor: " + targetFlavor);
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public boolean isSupported(ImageInfo imageInfo) {
        Boolean tiled = (Boolean)imageInfo.getCustomObjects().get("TIFF_TILED");
        if (Boolean.TRUE.equals(tiled)) {
            this.log.trace((Object)"Raw CCITT loading not supported for tiled TIFF image");
            return false;
        }
        Integer compression = (Integer)imageInfo.getCustomObjects().get("TIFF_COMPRESSION");
        if (compression == null) {
            return false;
        }
        switch (compression) {
            case 2: 
            case 3: 
            case 4: {
                boolean supported;
                Integer stripCount = (Integer)imageInfo.getCustomObjects().get("TIFF_STRIP_COUNT");
                boolean bl = supported = stripCount != null && stripCount == 1;
                if (!supported) {
                    this.log.trace((Object)"Raw CCITT loading not supported for multi-strip TIFF image");
                }
                return supported;
            }
        }
        return false;
    }
}

