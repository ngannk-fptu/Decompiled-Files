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
import com.sun.media.jai.opimage.FilteredSubsampleOpImage;
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

final class MlibFilteredSubsampleOpImage
extends FilteredSubsampleOpImage {
    protected double[] m_hKernel;
    protected double[] m_vKernel;
    private static final boolean DEBUG = false;

    public MlibFilteredSubsampleOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, int scaleX, int scaleY, float[] qsFilter, Interpolation interp) {
        super(source, extender, config, layout, scaleX, scaleY, qsFilter, interp);
        this.m_hKernel = new double[this.hKernel.length];
        this.m_vKernel = new double[this.vKernel.length];
        for (int i = 0; i < this.hKernel.length; ++i) {
            this.m_hKernel[i] = this.hKernel[i];
        }
        for (int j = 0; j < this.vKernel.length; ++j) {
            this.m_vKernel[j] = this.vKernel[j];
        }
    }

    public void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        int formatTag = MediaLibAccessor.findCompatibleTag(sources, dest);
        MediaLibAccessor dst = new MediaLibAccessor(dest, destRect, formatTag);
        MediaLibAccessor src = new MediaLibAccessor(sources[0], this.mapDestRect(destRect, 0), formatTag);
        int transX = this.m_hKernel.length - (this.scaleX + 1) / 2 - this.hParity * (1 + this.scaleX) % 2;
        int transY = this.m_vKernel.length - (this.scaleY + 1) / 2 - this.vParity * (1 + this.scaleY) % 2;
        switch (dst.getDataType()) {
            case 0: 
            case 1: 
            case 2: 
            case 3: {
                mediaLibImage[] srcML = src.getMediaLibImages();
                mediaLibImage[] dstML = dst.getMediaLibImages();
                for (int i = 0; i < dstML.length; ++i) {
                    Image.FilteredSubsample((mediaLibImage)dstML[i], (mediaLibImage)srcML[i], (int)this.scaleX, (int)this.scaleY, (int)transX, (int)transY, (double[])this.m_hKernel, (double[])this.m_vKernel, (int)this.hParity, (int)this.vParity, (int)0);
                }
                break;
            }
            case 4: 
            case 5: {
                mediaLibImage[] srcML = src.getMediaLibImages();
                mediaLibImage[] dstML = dst.getMediaLibImages();
                for (int i = 0; i < dstML.length; ++i) {
                    Image.FilteredSubsample_Fp((mediaLibImage)dstML[i], (mediaLibImage)srcML[i], (int)this.scaleX, (int)this.scaleY, (int)transX, (int)transY, (double[])this.m_hKernel, (double[])this.m_vKernel, (int)this.hParity, (int)this.vParity, (int)0);
                }
                break;
            }
            default: {
                throw new IllegalArgumentException(JaiI18N.getString("Generic2"));
            }
        }
        if (dst.isDataCopy()) {
            dst.clampDataArrays();
            dst.copyDataToRaster();
        }
    }
}

