/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.AreaOpImage;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFormatTag;
import javax.media.jai.operator.MaxFilterShape;

abstract class MaxFilterOpImage
extends AreaOpImage {
    protected MaxFilterShape maskType;
    protected int maskSize;

    public MaxFilterOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, MaxFilterShape maskType, int maskSize) {
        super(source, layout, config, true, extender, (maskSize - 1) / 2, (maskSize - 1) / 2, maskSize / 2, maskSize / 2);
        this.maskType = maskType;
        this.maskSize = maskSize;
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        RasterFormatTag[] formatTags = this.getFormatTags();
        Raster source = sources[0];
        Rectangle srcRect = this.mapDestRect(destRect, 0);
        RasterAccessor srcAccessor = new RasterAccessor(source, srcRect, formatTags[0], this.getSourceImage(0).getColorModel());
        RasterAccessor dstAccessor = new RasterAccessor(dest, destRect, formatTags[1], this.getColorModel());
        switch (dstAccessor.getDataType()) {
            case 0: {
                this.byteLoop(srcAccessor, dstAccessor, this.maskSize);
                break;
            }
            case 2: {
                this.shortLoop(srcAccessor, dstAccessor, this.maskSize);
                break;
            }
            case 1: {
                this.ushortLoop(srcAccessor, dstAccessor, this.maskSize);
                break;
            }
            case 3: {
                this.intLoop(srcAccessor, dstAccessor, this.maskSize);
                break;
            }
            case 4: {
                this.floatLoop(srcAccessor, dstAccessor, this.maskSize);
                break;
            }
            case 5: {
                this.doubleLoop(srcAccessor, dstAccessor, this.maskSize);
            }
        }
        if (dstAccessor.isDataCopy()) {
            dstAccessor.clampDataArrays();
            dstAccessor.copyDataToRaster();
        }
    }

    protected abstract void byteLoop(RasterAccessor var1, RasterAccessor var2, int var3);

    protected abstract void shortLoop(RasterAccessor var1, RasterAccessor var2, int var3);

    protected abstract void ushortLoop(RasterAccessor var1, RasterAccessor var2, int var3);

    protected abstract void intLoop(RasterAccessor var1, RasterAccessor var2, int var3);

    protected abstract void floatLoop(RasterAccessor var1, RasterAccessor var2, int var3);

    protected abstract void doubleLoop(RasterAccessor var1, RasterAccessor var2, int var3);

    static final int maxFilter(int[] data) {
        if (data.length == 3) {
            int a = data[0];
            int b = data[1];
            int c = data[2];
            if (a < b) {
                return b < c ? c : b;
            }
            return a < c ? c : a;
        }
        int max = data[0];
        for (int i = 1; i < data.length; ++i) {
            if (max >= data[i]) continue;
            max = data[i];
        }
        return max;
    }

    static final float maxFilterFloat(float[] data) {
        if (data.length == 3) {
            float a = data[0];
            float b = data[1];
            float c = data[2];
            if (a < b) {
                return b < c ? c : b;
            }
            return a < c ? c : a;
        }
        float max = data[0];
        for (int i = 1; i < data.length; ++i) {
            if (!(max < data[i])) continue;
            max = data[i];
        }
        return max;
    }

    static final double maxFilterDouble(double[] data) {
        if (data.length == 3) {
            double a = data[0];
            double b = data[1];
            double c = data[2];
            if (a < b) {
                return b < c ? c : b;
            }
            return a < c ? c : a;
        }
        double max = data[0];
        for (int i = 1; i < data.length; ++i) {
            if (!(max < data[i])) continue;
            max = data[i];
        }
        return max;
    }
}

