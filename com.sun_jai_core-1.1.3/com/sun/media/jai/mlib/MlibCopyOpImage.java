/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.medialib.mlib.Image
 *  com.sun.medialib.mlib.mediaLibImage
 */
package com.sun.media.jai.mlib;

import com.sun.media.jai.mlib.MediaLibAccessor;
import com.sun.medialib.mlib.Image;
import com.sun.medialib.mlib.mediaLibImage;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.ImageLayout;
import javax.media.jai.PointOpImage;

final class MlibCopyOpImage
extends PointOpImage {
    public MlibCopyOpImage(RenderedImage source, Map config, ImageLayout layout) {
        super(source, layout, config, true);
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        int formatTag = MediaLibAccessor.findCompatibleTag(sources, dest);
        MediaLibAccessor srcMA = new MediaLibAccessor(sources[0], destRect, formatTag);
        MediaLibAccessor dstMA = new MediaLibAccessor(dest, destRect, formatTag);
        mediaLibImage[] srcMLI = srcMA.getMediaLibImages();
        mediaLibImage[] dstMLI = dstMA.getMediaLibImages();
        for (int i = 0; i < dstMLI.length; ++i) {
            Image.Copy((mediaLibImage)dstMLI[i], (mediaLibImage)srcMLI[i]);
        }
        if (dstMA.isDataCopy()) {
            dstMA.copyDataToRaster();
        }
    }
}

