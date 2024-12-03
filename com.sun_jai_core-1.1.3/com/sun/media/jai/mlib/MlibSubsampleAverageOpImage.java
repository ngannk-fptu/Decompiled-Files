/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.medialib.mlib.Image
 *  com.sun.medialib.mlib.mediaLibImage
 */
package com.sun.media.jai.mlib;

import com.sun.media.jai.mlib.MediaLibAccessor;
import com.sun.media.jai.opimage.SubsampleAverageOpImage;
import com.sun.medialib.mlib.Image;
import com.sun.medialib.mlib.mediaLibImage;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.ImageLayout;

public class MlibSubsampleAverageOpImage
extends SubsampleAverageOpImage {
    public MlibSubsampleAverageOpImage(RenderedImage source, ImageLayout layout, Map config, double scaleX, double scaleY) {
        super(source, layout, config, scaleX, scaleY);
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        Raster source = sources[0];
        Rectangle srcRect = source.getBounds();
        int formatTag = MediaLibAccessor.findCompatibleTag(sources, dest);
        MediaLibAccessor srcAccessor = new MediaLibAccessor(source, srcRect, formatTag);
        MediaLibAccessor dstAccessor = new MediaLibAccessor(dest, destRect, formatTag);
        switch (dstAccessor.getDataType()) {
            case 0: 
            case 1: 
            case 2: 
            case 3: {
                mediaLibImage[] srcML = srcAccessor.getMediaLibImages();
                mediaLibImage[] dstML = dstAccessor.getMediaLibImages();
                Image.SubsampleAverage((mediaLibImage)dstML[0], (mediaLibImage)srcML[0], (double)this.scaleX, (double)this.scaleY);
                break;
            }
            case 4: 
            case 5: {
                mediaLibImage[] srcML = srcAccessor.getMediaLibImages();
                mediaLibImage[] dstML = dstAccessor.getMediaLibImages();
                Image.SubsampleAverage_Fp((mediaLibImage)dstML[0], (mediaLibImage)srcML[0], (double)this.scaleX, (double)this.scaleY);
                break;
            }
        }
        if (dstAccessor.isDataCopy()) {
            dstAccessor.copyDataToRaster();
        }
    }
}

