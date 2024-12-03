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
import com.sun.media.jai.util.ImageUtil;
import com.sun.medialib.mlib.Image;
import com.sun.medialib.mlib.mediaLibImage;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.ImageLayout;
import javax.media.jai.PointOpImage;

final class MlibClampOpImage
extends PointOpImage {
    private double[] low;
    private int[] lowInt;
    private double[] high;
    private int[] highInt;

    public MlibClampOpImage(RenderedImage source, Map config, ImageLayout layout, double[] low, double[] high) {
        super(source, layout, config, true);
        int numBands = this.getSampleModel().getNumBands();
        this.low = new double[numBands];
        this.lowInt = new int[numBands];
        this.high = new double[numBands];
        this.highInt = new int[numBands];
        for (int i = 0; i < numBands; ++i) {
            this.low[i] = low.length < numBands ? low[0] : low[i];
            this.lowInt[i] = ImageUtil.clampRoundInt(this.low[i]);
            this.high[i] = high.length < numBands ? high[0] : high[i];
            this.highInt[i] = ImageUtil.clampRoundInt(this.high[i]);
        }
        this.permitInPlaceOperation();
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        int formatTag = MediaLibAccessor.findCompatibleTag(sources, dest);
        MediaLibAccessor srcMA = new MediaLibAccessor(sources[0], destRect, formatTag);
        MediaLibAccessor dstMA = new MediaLibAccessor(dest, destRect, formatTag);
        mediaLibImage[] srcMLI = srcMA.getMediaLibImages();
        mediaLibImage[] dstMLI = dstMA.getMediaLibImages();
        switch (dstMA.getDataType()) {
            case 0: 
            case 1: 
            case 2: 
            case 3: {
                for (int i = 0; i < dstMLI.length; ++i) {
                    int[] mlLow = dstMA.getIntParameters(i, this.lowInt);
                    int[] mlHigh = dstMA.getIntParameters(i, this.highInt);
                    Image.Thresh4((mediaLibImage)dstMLI[i], (mediaLibImage)srcMLI[i], (int[])mlHigh, (int[])mlLow, (int[])mlHigh, (int[])mlLow);
                }
                break;
            }
            case 4: 
            case 5: {
                for (int i = 0; i < dstMLI.length; ++i) {
                    double[] mlLow = dstMA.getDoubleParameters(i, this.low);
                    double[] mlHigh = dstMA.getDoubleParameters(i, this.high);
                    Image.Thresh4_Fp((mediaLibImage)dstMLI[i], (mediaLibImage)srcMLI[i], (double[])mlHigh, (double[])mlLow, (double[])mlHigh, (double[])mlLow);
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

