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
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.ImageLayout;
import javax.media.jai.PointOpImage;

final class MlibMinOpImage
extends PointOpImage {
    public MlibMinOpImage(RenderedImage source1, RenderedImage source2, Map config, ImageLayout layout) {
        super(source1, source2, layout, config, true);
        this.permitInPlaceOperation();
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        int formatTag = MediaLibAccessor.findCompatibleTag(sources, dest);
        MediaLibAccessor srcMA1 = new MediaLibAccessor(sources[0], destRect, formatTag);
        MediaLibAccessor srcMA2 = new MediaLibAccessor(sources[1], destRect, formatTag);
        MediaLibAccessor dstMA = new MediaLibAccessor(dest, destRect, formatTag);
        mediaLibImage[] srcMLI1 = srcMA1.getMediaLibImages();
        mediaLibImage[] srcMLI2 = srcMA2.getMediaLibImages();
        mediaLibImage[] dstMLI = dstMA.getMediaLibImages();
        switch (dstMA.getDataType()) {
            case 0: 
            case 1: 
            case 2: 
            case 3: {
                for (int i = 0; i < dstMLI.length; ++i) {
                    Image.Min((mediaLibImage)dstMLI[i], (mediaLibImage)srcMLI1[i], (mediaLibImage)srcMLI2[i]);
                }
                break;
            }
            case 4: 
            case 5: {
                for (int i = 0; i < dstMLI.length; ++i) {
                    Image.Min_Fp((mediaLibImage)dstMLI[i], (mediaLibImage)srcMLI1[i], (mediaLibImage)srcMLI2[i]);
                }
                break;
            }
            default: {
                throw new RuntimeException(JaiI18N.getString("Generic2"));
            }
        }
        if (dstMA.isDataCopy()) {
            dstMA.clampDataArrays();
            dstMA.copyDataToRaster();
        }
    }
}

