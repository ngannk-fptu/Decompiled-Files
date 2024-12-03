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

final class MlibSeparableConvolveOpImage
extends AreaOpImage {
    protected KernelJAI kernel;
    private int kw;
    private int kh;
    float[] hValues;
    float[] vValues;
    double[] hDoubleData;
    double[] vDoubleData;
    int[] hIntData;
    int[] vIntData;
    int shift = -1;

    public MlibSeparableConvolveOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, KernelJAI kernel) {
        super(source, layout, config, true, extender, kernel.getLeftPadding(), kernel.getRightPadding(), kernel.getTopPadding(), kernel.getBottomPadding());
        int i;
        this.kernel = kernel;
        this.kw = kernel.getWidth();
        this.kh = kernel.getHeight();
        this.hValues = kernel.getHorizontalKernelData();
        this.vValues = kernel.getVerticalKernelData();
        this.hDoubleData = new double[this.hValues.length];
        for (i = 0; i < this.hValues.length; ++i) {
            this.hDoubleData[i] = this.hValues[i];
        }
        this.vDoubleData = new double[this.vValues.length];
        for (i = 0; i < this.vValues.length; ++i) {
            this.vDoubleData[i] = this.vValues[i];
        }
        this.hIntData = new int[this.hValues.length];
        this.vIntData = new int[this.vValues.length];
    }

    private synchronized void setShift(int formatTag) {
        if (this.shift == -1) {
            int mediaLibDataType = MediaLibAccessor.getMediaLibDataType(formatTag);
            this.shift = Image.SConvKernelConvert((int[])this.hIntData, (int[])this.vIntData, (double[])this.hDoubleData, (double[])this.vDoubleData, (int)this.kw, (int)this.kh, (int)mediaLibDataType);
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
        block14: for (int i = 0; i < dstML.length; ++i) {
            switch (dstAccessor.getDataType()) {
                case 0: 
                case 1: 
                case 2: 
                case 3: {
                    if (this.shift == -1) {
                        this.setShift(formatTag);
                    }
                    switch (this.kw) {
                        case 3: {
                            Image.SConv3x3((mediaLibImage)dstML[i], (mediaLibImage)srcML[i], (int[])this.hIntData, (int[])this.vIntData, (int)this.shift, (int)((1 << numBands) - 1), (int)0);
                            break;
                        }
                        case 5: {
                            Image.SConv5x5((mediaLibImage)dstML[i], (mediaLibImage)srcML[i], (int[])this.hIntData, (int[])this.vIntData, (int)this.shift, (int)((1 << numBands) - 1), (int)0);
                            break;
                        }
                        case 7: {
                            Image.SConv7x7((mediaLibImage)dstML[i], (mediaLibImage)srcML[i], (int[])this.hIntData, (int[])this.vIntData, (int)this.shift, (int)((1 << numBands) - 1), (int)0);
                        }
                    }
                    continue block14;
                }
                case 4: 
                case 5: {
                    switch (this.kw) {
                        case 3: {
                            Image.SConv3x3_Fp((mediaLibImage)dstML[i], (mediaLibImage)srcML[i], (double[])this.hDoubleData, (double[])this.vDoubleData, (int)((1 << numBands) - 1), (int)0);
                            break;
                        }
                        case 5: {
                            Image.SConv5x5_Fp((mediaLibImage)dstML[i], (mediaLibImage)srcML[i], (double[])this.hDoubleData, (double[])this.vDoubleData, (int)((1 << numBands) - 1), (int)0);
                            break;
                        }
                        case 7: {
                            Image.SConv7x7_Fp((mediaLibImage)dstML[i], (mediaLibImage)srcML[i], (double[])this.hDoubleData, (double[])this.vDoubleData, (int)((1 << numBands) - 1), (int)0);
                        }
                    }
                    continue block14;
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

