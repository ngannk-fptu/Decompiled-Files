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
import com.sun.media.jai.mlib.MlibScaleOpImage;
import com.sun.medialib.mlib.Image;
import com.sun.medialib.mlib.mediaLibImage;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.Interpolation;

final class MlibScaleNearestOpImage
extends MlibScaleOpImage {
    public MlibScaleNearestOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, float xScale, float yScale, float xTrans, float yTrans, Interpolation interp) {
        super(source, extender, config, layout, xScale, yScale, xTrans, yTrans, interp, true);
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        Raster source = sources[0];
        Rectangle srcRect = source.getBounds();
        int formatTag = MediaLibAccessor.findCompatibleTag(sources, dest);
        MediaLibAccessor srcAccessor = new MediaLibAccessor(source, srcRect, formatTag);
        MediaLibAccessor dstAccessor = new MediaLibAccessor(dest, destRect, formatTag);
        float mlibScaleX = this.scaleX;
        float mlibScaleY = this.scaleY;
        long tempDenomX = this.scaleXRationalDenom * this.transXRationalDenom;
        long tempDenomY = this.scaleYRationalDenom * this.transYRationalDenom;
        long tempNumerX = (long)srcRect.x * this.scaleXRationalNum * this.transXRationalDenom + this.transXRationalNum * this.scaleXRationalDenom - (long)destRect.x * tempDenomX;
        long tempNumerY = (long)srcRect.y * this.scaleYRationalNum * this.transYRationalDenom + this.transYRationalNum * this.scaleYRationalDenom - (long)destRect.y * tempDenomY;
        float tx = (float)tempNumerX / (float)tempDenomX;
        float ty = (float)tempNumerY / (float)tempDenomY;
        switch (dstAccessor.getDataType()) {
            case 0: 
            case 1: 
            case 2: 
            case 3: {
                mediaLibImage[] srcML = srcAccessor.getMediaLibImages();
                mediaLibImage[] dstML = dstAccessor.getMediaLibImages();
                for (int i = 0; i < dstML.length; ++i) {
                    Image.ZoomTranslate((mediaLibImage)dstML[i], (mediaLibImage)srcML[i], (double)mlibScaleX, (double)mlibScaleY, (double)tx, (double)ty, (int)0, (int)0);
                }
                break;
            }
            case 4: 
            case 5: {
                mediaLibImage[] srcML = srcAccessor.getMediaLibImages();
                mediaLibImage[] dstML = dstAccessor.getMediaLibImages();
                for (int i = 0; i < dstML.length; ++i) {
                    Image.ZoomTranslate_Fp((mediaLibImage)dstML[i], (mediaLibImage)srcML[i], (double)mlibScaleX, (double)mlibScaleY, (double)tx, (double)ty, (int)0, (int)0);
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

