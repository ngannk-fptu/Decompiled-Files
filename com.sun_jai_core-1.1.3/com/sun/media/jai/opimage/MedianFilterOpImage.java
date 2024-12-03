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
import javax.media.jai.operator.MedianFilterShape;

abstract class MedianFilterOpImage
extends AreaOpImage {
    protected MedianFilterShape maskType;
    protected int maskSize;

    public MedianFilterOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, MedianFilterShape maskType, int maskSize) {
        super(source, layout, config, true, extender, (maskSize - 1) / 2, (maskSize - 1) / 2, maskSize / 2, maskSize / 2);
        this.maskType = maskType;
        this.maskSize = maskSize;
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        RasterFormatTag[] formatTags = this.getFormatTags();
        Raster source = sources[0];
        Rectangle srcRect = this.mapDestRect(destRect, 0);
        RasterAccessor srcAccessor = new RasterAccessor(source, srcRect, formatTags[0], this.getSource(0).getColorModel());
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

    protected int medianFilter(int[] data) {
        if (data.length == 3) {
            int a = data[0];
            int b = data[1];
            int c = data[2];
            if (a < b) {
                if (b < c) {
                    return b;
                }
                if (c > a) {
                    return c;
                }
                return a;
            }
            if (a < c) {
                return a;
            }
            if (b < c) {
                return c;
            }
            return b;
        }
        int left = 0;
        int right = data.length - 1;
        int target = data.length / 2;
        while (true) {
            int oleft = left;
            int oright = right;
            int mid = data[(left + right) / 2];
            while (true) {
                if (data[left] < mid) {
                    ++left;
                    continue;
                }
                while (mid < data[right]) {
                    --right;
                }
                if (left <= right) {
                    int tmp = data[left];
                    data[left] = data[right];
                    data[right] = tmp;
                    ++left;
                    --right;
                }
                if (left > right) break;
            }
            if (oleft < right && right >= target) {
                left = oleft;
                continue;
            }
            if (left >= oright || left > target) break;
            right = oright;
        }
        return data[target];
    }

    protected float medianFilterFloat(float[] data) {
        if (data.length == 3) {
            float a = data[0];
            float b = data[1];
            float c = data[2];
            if (a < b) {
                if (b < c) {
                    return b;
                }
                if (c > a) {
                    return c;
                }
                return a;
            }
            if (a < c) {
                return a;
            }
            if (b < c) {
                return c;
            }
            return b;
        }
        int left = 0;
        int right = data.length - 1;
        int target = data.length / 2;
        while (true) {
            int oleft = left;
            int oright = right;
            float mid = data[(left + right) / 2];
            while (true) {
                if (data[left] < mid) {
                    ++left;
                    continue;
                }
                while (mid < data[right]) {
                    --right;
                }
                if (left <= right) {
                    float tmp = data[left];
                    data[left] = data[right];
                    data[right] = tmp;
                    ++left;
                    --right;
                }
                if (left > right) break;
            }
            if (oleft < right && right >= target) {
                left = oleft;
                continue;
            }
            if (left >= oright || left > target) break;
            right = oright;
        }
        return data[target];
    }

    protected double medianFilterDouble(double[] data) {
        if (data.length == 3) {
            double a = data[0];
            double b = data[1];
            double c = data[2];
            if (a < b) {
                if (b < c) {
                    return b;
                }
                if (c > a) {
                    return c;
                }
                return a;
            }
            if (a < c) {
                return a;
            }
            if (b < c) {
                return c;
            }
            return b;
        }
        int left = 0;
        int right = data.length - 1;
        int target = data.length / 2;
        while (true) {
            int oleft = left;
            int oright = right;
            double mid = data[(left + right) / 2];
            while (true) {
                if (data[left] < mid) {
                    ++left;
                    continue;
                }
                while (mid < data[right]) {
                    --right;
                }
                if (left <= right) {
                    double tmp = data[left];
                    data[left] = data[right];
                    data[right] = tmp;
                    ++left;
                    --right;
                }
                if (left > right) break;
            }
            if (oleft < right && right >= target) {
                left = oleft;
                continue;
            }
            if (left >= oright || left > target) break;
            right = oright;
        }
        return data[target];
    }
}

