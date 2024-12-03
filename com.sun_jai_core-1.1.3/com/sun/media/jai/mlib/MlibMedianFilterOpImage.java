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
import javax.media.jai.AreaOpImage;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.operator.MedianFilterDescriptor;
import javax.media.jai.operator.MedianFilterShape;

final class MlibMedianFilterOpImage
extends AreaOpImage {
    protected int maskType;
    protected int maskSize;

    public MlibMedianFilterOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, MedianFilterShape maskType, int maskSize) {
        super(source, layout, config, true, extender, (maskSize - 1) / 2, (maskSize - 1) / 2, maskSize / 2, maskSize / 2);
        this.maskType = MlibMedianFilterOpImage.mapToMlibMaskType(maskType);
        this.maskSize = maskSize;
    }

    private static int mapToMlibMaskType(MedianFilterShape maskType) {
        if (maskType.equals(MedianFilterDescriptor.MEDIAN_MASK_SQUARE)) {
            return 0;
        }
        if (maskType.equals(MedianFilterDescriptor.MEDIAN_MASK_PLUS)) {
            return 1;
        }
        if (maskType.equals(MedianFilterDescriptor.MEDIAN_MASK_X)) {
            return 2;
        }
        if (maskType.equals(MedianFilterDescriptor.MEDIAN_MASK_SQUARE_SEPARABLE)) {
            return 3;
        }
        throw new RuntimeException(JaiI18N.getString("MedianFilterOpImage0"));
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        Raster source = sources[0];
        Rectangle srcRect = this.mapDestRect(destRect, 0);
        int formatTag = MediaLibAccessor.findCompatibleTag(sources, dest);
        MediaLibAccessor srcAccessor = new MediaLibAccessor(source, srcRect, formatTag);
        MediaLibAccessor dstAccessor = new MediaLibAccessor(dest, destRect, formatTag);
        int numBands = this.getSampleModel().getNumBands();
        int cmask = (1 << numBands) - 1;
        mediaLibImage[] srcML = srcAccessor.getMediaLibImages();
        mediaLibImage[] dstML = dstAccessor.getMediaLibImages();
        block4: for (int i = 0; i < dstML.length; ++i) {
            switch (dstAccessor.getDataType()) {
                case 0: 
                case 1: 
                case 2: 
                case 3: {
                    if (this.maskSize == 3) {
                        Image.MedianFilter3x3((mediaLibImage)dstML[i], (mediaLibImage)srcML[i], (int)this.maskType, (int)cmask, (int)0);
                        continue block4;
                    }
                    if (this.maskSize == 5) {
                        Image.MedianFilter5x5((mediaLibImage)dstML[i], (mediaLibImage)srcML[i], (int)this.maskType, (int)cmask, (int)0);
                        continue block4;
                    }
                    if (this.maskSize == 7) {
                        Image.MedianFilter7x7((mediaLibImage)dstML[i], (mediaLibImage)srcML[i], (int)this.maskType, (int)cmask, (int)0);
                        continue block4;
                    }
                    Image.MedianFilterMxN((mediaLibImage)dstML[i], (mediaLibImage)srcML[i], (int)this.maskSize, (int)this.maskSize, (int)this.maskType, (int)cmask, (int)0);
                    continue block4;
                }
                case 4: 
                case 5: {
                    if (this.maskSize == 3) {
                        Image.MedianFilter3x3_Fp((mediaLibImage)dstML[i], (mediaLibImage)srcML[i], (int)this.maskType, (int)cmask, (int)0);
                        continue block4;
                    }
                    if (this.maskSize == 5) {
                        Image.MedianFilter5x5_Fp((mediaLibImage)dstML[i], (mediaLibImage)srcML[i], (int)this.maskType, (int)cmask, (int)0);
                        continue block4;
                    }
                    if (this.maskSize == 7) {
                        Image.MedianFilter7x7_Fp((mediaLibImage)dstML[i], (mediaLibImage)srcML[i], (int)this.maskType, (int)cmask, (int)0);
                        continue block4;
                    }
                    Image.MedianFilterMxN_Fp((mediaLibImage)dstML[i], (mediaLibImage)srcML[i], (int)this.maskSize, (int)this.maskSize, (int)this.maskType, (int)cmask, (int)0);
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

