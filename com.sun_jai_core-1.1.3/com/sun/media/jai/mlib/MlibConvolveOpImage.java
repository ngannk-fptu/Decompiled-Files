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
import javax.media.jai.KernelJAI;

final class MlibConvolveOpImage
extends AreaOpImage {
    protected KernelJAI kernel;
    private int kw;
    private int kh;
    private int kx;
    private int ky;
    float[] kData;
    double[] doublekData;
    int[] intkData;
    int shift = -1;

    public MlibConvolveOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, KernelJAI kernel) {
        super(source, layout, config, true, extender, kernel.getLeftPadding(), kernel.getRightPadding(), kernel.getTopPadding(), kernel.getBottomPadding());
        this.kernel = kernel;
        this.kw = kernel.getWidth();
        this.kh = kernel.getHeight();
        this.kx = this.kw / 2;
        this.ky = this.kh / 2;
        this.kData = kernel.getKernelData();
        int count = this.kw * this.kh;
        this.intkData = new int[count];
        this.doublekData = new double[count];
        for (int i = 0; i < count; ++i) {
            this.doublekData[i] = this.kData[i];
        }
    }

    private synchronized void setShift(int formatTag) {
        if (this.shift == -1) {
            int mediaLibDataType = MediaLibAccessor.getMediaLibDataType(formatTag);
            this.shift = Image.ConvKernelConvert((int[])this.intkData, (double[])this.doublekData, (int)this.kw, (int)this.kh, (int)mediaLibDataType);
        }
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        Raster source = sources[0];
        Rectangle srcRect = this.mapDestRect(destRect, 0);
        int formatTag = MediaLibAccessor.findCompatibleTag(sources, dest);
        MediaLibAccessor srcAccessor = new MediaLibAccessor(source, srcRect, formatTag);
        MediaLibAccessor dstAccessor = new MediaLibAccessor(dest, destRect, formatTag);
        int numBands = this.getSampleModel().getNumBands();
        mediaLibImage[] srcML = srcAccessor.getMediaLibImages();
        mediaLibImage[] dstML = dstAccessor.getMediaLibImages();
        block4: for (int i = 0; i < dstML.length; ++i) {
            switch (dstAccessor.getDataType()) {
                case 0: 
                case 1: 
                case 2: 
                case 3: {
                    if (this.shift == -1) {
                        this.setShift(formatTag);
                    }
                    Image.ConvMxN((mediaLibImage)dstML[i], (mediaLibImage)srcML[i], (int[])this.intkData, (int)this.kw, (int)this.kh, (int)this.kx, (int)this.ky, (int)this.shift, (int)((1 << numBands) - 1), (int)0);
                    continue block4;
                }
                case 4: 
                case 5: {
                    Image.ConvMxN_Fp((mediaLibImage)dstML[i], (mediaLibImage)srcML[i], (double[])this.doublekData, (int)this.kw, (int)this.kh, (int)this.kx, (int)this.ky, (int)((1 << numBands) - 1), (int)0);
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

