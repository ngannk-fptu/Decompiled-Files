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

final class MlibGradientOpImage
extends AreaOpImage {
    protected KernelJAI kernel_h;
    protected KernelJAI kernel_v;
    private int kh;
    private int kw;
    private int kx;
    private int ky;
    float[] kernel_h_data;
    float[] kernel_v_data;
    double[] dbl_kh_data;
    double[] dbl_kv_data;

    public MlibGradientOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, KernelJAI kernel_h, KernelJAI kernel_v) {
        super(source, layout, config, true, extender, kernel_h.getLeftPadding(), kernel_h.getRightPadding(), kernel_h.getTopPadding(), kernel_h.getBottomPadding());
        this.kernel_h = kernel_h;
        this.kernel_v = kernel_v;
        this.kw = kernel_h.getWidth();
        this.kh = kernel_h.getHeight();
        this.kx = this.kw / 2;
        this.ky = this.kh / 2;
        this.kernel_h_data = kernel_h.getKernelData();
        this.kernel_v_data = kernel_v.getKernelData();
        int count = this.kw * this.kh;
        this.dbl_kh_data = new double[count];
        this.dbl_kv_data = new double[count];
        for (int i = 0; i < count; ++i) {
            this.dbl_kh_data[i] = this.kernel_h_data[i];
            this.dbl_kv_data[i] = this.kernel_v_data[i];
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
                    Image.GradientMxN((mediaLibImage)dstML[i], (mediaLibImage)srcML[i], (double[])this.dbl_kh_data, (double[])this.dbl_kv_data, (int)this.kw, (int)this.kh, (int)this.kx, (int)this.ky, (int)((1 << numBands) - 1), (int)0);
                    continue block4;
                }
                case 4: 
                case 5: {
                    Image.GradientMxN_Fp((mediaLibImage)dstML[i], (mediaLibImage)srcML[i], (double[])this.dbl_kh_data, (double[])this.dbl_kv_data, (int)this.kw, (int)this.kh, (int)this.kx, (int)this.ky, (int)((1 << numBands) - 1), (int)0);
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

