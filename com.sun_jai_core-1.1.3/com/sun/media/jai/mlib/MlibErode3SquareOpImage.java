/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.medialib.mlib.Image
 *  com.sun.medialib.mlib.mediaLibImage
 */
package com.sun.media.jai.mlib;

import com.sun.media.jai.mlib.JaiI18N;
import com.sun.media.jai.mlib.MediaLibAccessor;
import com.sun.medialib.mlib.Image;
import com.sun.medialib.mlib.mediaLibImage;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.AreaOpImage;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;

final class MlibErode3SquareOpImage
extends AreaOpImage {
    private static Map configHelper(Map configuration) {
        Map config;
        if (configuration == null) {
            config = new RenderingHints(JAI.KEY_REPLACE_INDEX_COLOR_MODEL, Boolean.FALSE);
        } else {
            config = configuration;
            if (!config.containsKey(JAI.KEY_REPLACE_INDEX_COLOR_MODEL)) {
                RenderingHints hints = (RenderingHints)configuration;
                config = (RenderingHints)hints.clone();
                config.put(JAI.KEY_REPLACE_INDEX_COLOR_MODEL, Boolean.FALSE);
            }
        }
        return config;
    }

    public MlibErode3SquareOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout) {
        super(source, layout, MlibErode3SquareOpImage.configHelper(config), true, extender, 1, 1, 1, 1);
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        Raster source = sources[0];
        Rectangle srcRect = this.mapDestRect(destRect, 0);
        int formatTag = MediaLibAccessor.findCompatibleTag(sources, dest);
        MediaLibAccessor srcAccessor = new MediaLibAccessor(source, srcRect, formatTag, true);
        MediaLibAccessor dstAccessor = new MediaLibAccessor(dest, destRect, formatTag, true);
        int numBands = this.getSampleModel().getNumBands();
        mediaLibImage[] srcML = srcAccessor.getMediaLibImages();
        mediaLibImage[] dstML = dstAccessor.getMediaLibImages();
        block4: for (int i = 0; i < dstML.length; ++i) {
            switch (dstAccessor.getDataType()) {
                case 0: 
                case 1: 
                case 2: 
                case 3: {
                    Image.Erode8((mediaLibImage)dstML[i], (mediaLibImage)srcML[i]);
                    continue block4;
                }
                case 4: 
                case 5: {
                    Image.Erode8_Fp((mediaLibImage)dstML[i], (mediaLibImage)srcML[i]);
                    continue block4;
                }
                default: {
                    String className = this.getClass().getName();
                    throw new RuntimeException(JaiI18N.getString("Generic2"));
                }
            }
        }
        if (dstAccessor.isDataCopy()) {
            dstAccessor.copyDataToRaster();
        }
    }
}

