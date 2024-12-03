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
import com.sun.media.jai.mlib.MlibUtils;
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
import javax.media.jai.InterpolationBicubic2;

final class MlibScaleBicubicOpImage
extends MlibScaleOpImage {
    public MlibScaleBicubicOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, float xScale, float yScale, float xTrans, float yTrans, Interpolation interp) {
        super(source, extender, config, layout, xScale, yScale, xTrans, yTrans, interp, true);
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        int mlibInterpType = 2;
        if (this.interp instanceof InterpolationBicubic2) {
            mlibInterpType = 3;
        }
        Raster source = sources[0];
        Rectangle srcRect = source.getBounds();
        int formatTag = MediaLibAccessor.findCompatibleTag(sources, dest);
        MediaLibAccessor srcAccessor = new MediaLibAccessor(source, srcRect, formatTag);
        MediaLibAccessor dstAccessor = new MediaLibAccessor(dest, destRect, formatTag);
        double mlibScaleX = (double)this.scaleXRationalNum / (double)this.scaleXRationalDenom;
        double mlibScaleY = (double)this.scaleYRationalNum / (double)this.scaleYRationalDenom;
        long tempDenomX = this.scaleXRationalDenom * this.transXRationalDenom;
        long tempDenomY = this.scaleYRationalDenom * this.transYRationalDenom;
        long tempNumerX = (long)srcRect.x * this.scaleXRationalNum * this.transXRationalDenom + this.transXRationalNum * this.scaleXRationalDenom - (long)destRect.x * tempDenomX;
        long tempNumerY = (long)srcRect.y * this.scaleYRationalNum * this.transYRationalDenom + this.transYRationalNum * this.scaleYRationalDenom - (long)destRect.y * tempDenomY;
        double tx = (double)tempNumerX / (double)tempDenomX;
        double ty = (double)tempNumerY / (double)tempDenomY;
        switch (dstAccessor.getDataType()) {
            case 0: 
            case 1: 
            case 2: 
            case 3: {
                mediaLibImage[] srcML = srcAccessor.getMediaLibImages();
                mediaLibImage[] dstML = dstAccessor.getMediaLibImages();
                for (int i = 0; i < dstML.length; ++i) {
                    Image.ZoomTranslate((mediaLibImage)dstML[i], (mediaLibImage)srcML[i], (double)mlibScaleX, (double)mlibScaleY, (double)tx, (double)ty, (int)mlibInterpType, (int)0);
                    MlibUtils.clampImage(dstML[i], this.getColorModel());
                }
                break;
            }
            case 4: 
            case 5: {
                mediaLibImage[] srcML = srcAccessor.getMediaLibImages();
                mediaLibImage[] dstML = dstAccessor.getMediaLibImages();
                for (int i = 0; i < dstML.length; ++i) {
                    Image.ZoomTranslate_Fp((mediaLibImage)dstML[i], (mediaLibImage)srcML[i], (double)mlibScaleX, (double)mlibScaleY, (double)tx, (double)ty, (int)mlibInterpType, (int)0);
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

