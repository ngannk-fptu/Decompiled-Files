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
import com.sun.media.jai.opimage.SubsampleBinaryToGrayOpImage;
import com.sun.medialib.mlib.Image;
import com.sun.medialib.mlib.mediaLibImage;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.ImageLayout;

class MlibSubsampleBinaryToGrayOpImage
extends SubsampleBinaryToGrayOpImage {
    public MlibSubsampleBinaryToGrayOpImage(RenderedImage source, ImageLayout layout, Map config, float scaleX, float scaleY) {
        super(source, layout, config, scaleX, scaleY);
    }

    protected Rectangle backwardMapRect(Rectangle destRect, int sourceIndex) {
        Rectangle sourceRect = super.backwardMapRect(destRect, sourceIndex);
        sourceRect.width += (int)this.invScaleX;
        sourceRect.height += (int)this.invScaleY;
        return sourceRect.intersection(this.getSourceImage(0).getBounds());
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        Raster source = sources[0];
        Rectangle srcRect = source.getBounds();
        int sourceFormatTag = dest.getSampleModel().getDataType() | 0x100 | 0;
        int destFormatTag = MediaLibAccessor.findCompatibleTag(null, dest);
        MediaLibAccessor srcAccessor = new MediaLibAccessor(source, srcRect, sourceFormatTag, true);
        MediaLibAccessor dstAccessor = new MediaLibAccessor(dest, destRect, destFormatTag);
        switch (dstAccessor.getDataType()) {
            case 0: 
            case 1: 
            case 2: 
            case 3: {
                mediaLibImage[] srcML = srcAccessor.getMediaLibImages();
                mediaLibImage[] dstML = dstAccessor.getMediaLibImages();
                for (int i = 0; i < dstML.length; ++i) {
                    Image.SubsampleBinaryToGray((mediaLibImage)dstML[i], (mediaLibImage)srcML[i], (double)this.scaleX, (double)this.scaleY, (byte[])this.lutGray);
                }
                break;
            }
            default: {
                String className = this.getClass().getName();
                throw new RuntimeException(JaiI18N.getString("Generic2"));
            }
        }
        if (dstAccessor.isDataCopy()) {
            dstAccessor.clampDataArrays();
            dstAccessor.copyDataToRaster();
        }
    }
}

