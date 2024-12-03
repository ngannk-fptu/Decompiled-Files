/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.JaiI18N;
import com.sun.media.jai.util.ImageUtil;
import com.sun.media.jai.util.JDKWorkarounds;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.ImageLayout;
import javax.media.jai.PackedImageData;
import javax.media.jai.PixelAccessor;
import javax.media.jai.PointOpImage;
import javax.media.jai.UnpackedImageData;

final class BinarizeOpImage
extends PointOpImage {
    private static byte[] byteTable = new byte[]{-128, 64, 32, 16, 8, 4, 2, 1};
    private static int[] bitsOn = null;
    private double threshold;

    public BinarizeOpImage(RenderedImage source, Map config, ImageLayout layout, double threshold) {
        super(source, BinarizeOpImage.layoutHelper(source, layout, config), config, true);
        if (source.getSampleModel().getNumBands() != 1) {
            throw new IllegalArgumentException(JaiI18N.getString("BinarizeOpImage0"));
        }
        this.threshold = threshold;
    }

    private static ImageLayout layoutHelper(RenderedImage source, ImageLayout il, Map config) {
        ColorModel cm;
        ImageLayout layout = il == null ? new ImageLayout() : (ImageLayout)il.clone();
        SampleModel sm = layout.getSampleModel(source);
        if (!ImageUtil.isBinary(sm)) {
            sm = new MultiPixelPackedSampleModel(0, layout.getTileWidth(source), layout.getTileHeight(source), 1);
            layout.setSampleModel(sm);
        }
        if ((cm = layout.getColorModel(null)) == null || !JDKWorkarounds.areCompatibleDataModels(sm, cm)) {
            layout.setColorModel(ImageUtil.getCompatibleColorModel(sm, config));
        }
        return layout;
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        switch (sources[0].getSampleModel().getDataType()) {
            case 0: {
                this.byteLoop(sources[0], dest, destRect);
                break;
            }
            case 2: {
                this.shortLoop(sources[0], dest, destRect);
                break;
            }
            case 1: {
                this.ushortLoop(sources[0], dest, destRect);
                break;
            }
            case 3: {
                this.intLoop(sources[0], dest, destRect);
                break;
            }
            case 4: {
                this.floatLoop(sources[0], dest, destRect);
                break;
            }
            case 5: {
                this.doubleLoop(sources[0], dest, destRect);
                break;
            }
            default: {
                throw new RuntimeException(JaiI18N.getString("BinarizeOpImage1"));
            }
        }
    }

    private void byteLoop(Raster source, WritableRaster dest, Rectangle destRect) {
        if (this.threshold <= 0.0) {
            this.setTo1(dest, destRect);
            return;
        }
        if (this.threshold > 255.0) {
            return;
        }
        short thresholdI = (short)Math.ceil(this.threshold);
        Rectangle srcRect = this.mapDestRect(destRect, 0);
        PixelAccessor pa = new PixelAccessor(dest.getSampleModel(), null);
        PackedImageData pid = pa.getPackedPixels(dest, destRect, true, false);
        int offset = pid.offset;
        PixelAccessor srcPa = new PixelAccessor(source.getSampleModel(), null);
        UnpackedImageData srcImD = srcPa.getPixels(source, srcRect, 0, false);
        int srcOffset = srcImD.bandOffsets[0];
        byte[] srcData = ((byte[][])srcImD.data)[0];
        int pixelStride = srcImD.pixelStride;
        int ind0 = pid.bitOffset;
        for (int h = 0; h < destRect.height; ++h) {
            int indE = ind0 + destRect.width;
            int b = ind0;
            int s = srcOffset;
            while (b < indE) {
                if ((srcData[s] & 0xFF) >= thresholdI) {
                    int n = offset + (b >> 3);
                    pid.data[n] = (byte)(pid.data[n] | byteTable[b % 8]);
                }
                ++b;
                s += pixelStride;
            }
            offset += pid.lineStride;
            srcOffset += srcImD.lineStride;
        }
        pa.setPackedPixels(pid);
    }

    private void shortLoop(Raster source, WritableRaster dest, Rectangle destRect) {
        if (this.threshold <= -32768.0) {
            this.setTo1(dest, destRect);
            return;
        }
        if (this.threshold > 32767.0) {
            return;
        }
        short thresholdS = (short)Math.ceil(this.threshold);
        Rectangle srcRect = this.mapDestRect(destRect, 0);
        PixelAccessor pa = new PixelAccessor(dest.getSampleModel(), null);
        PackedImageData pid = pa.getPackedPixels(dest, destRect, true, false);
        int offset = pid.offset;
        PixelAccessor srcPa = new PixelAccessor(source.getSampleModel(), null);
        UnpackedImageData srcImD = srcPa.getPixels(source, srcRect, 2, false);
        int srcOffset = srcImD.bandOffsets[0];
        short[] srcData = ((short[][])srcImD.data)[0];
        int pixelStride = srcImD.pixelStride;
        int ind0 = pid.bitOffset;
        for (int h = 0; h < destRect.height; ++h) {
            int indE = ind0 + destRect.width;
            int b = ind0;
            int s = srcOffset;
            while (b < indE) {
                if (srcData[s] >= thresholdS) {
                    int n = offset + (b >> 3);
                    pid.data[n] = (byte)(pid.data[n] | byteTable[b % 8]);
                }
                ++b;
                s += pixelStride;
            }
            offset += pid.lineStride;
            srcOffset += srcImD.lineStride;
        }
        pa.setPackedPixels(pid);
    }

    private void ushortLoop(Raster source, WritableRaster dest, Rectangle destRect) {
        if (this.threshold <= 0.0) {
            this.setTo1(dest, destRect);
            return;
        }
        if (this.threshold > 65535.0) {
            return;
        }
        int thresholdI = (int)Math.ceil(this.threshold);
        Rectangle srcRect = this.mapDestRect(destRect, 0);
        PixelAccessor pa = new PixelAccessor(dest.getSampleModel(), null);
        PackedImageData pid = pa.getPackedPixels(dest, destRect, true, false);
        int offset = pid.offset;
        PixelAccessor srcPa = new PixelAccessor(source.getSampleModel(), null);
        UnpackedImageData srcImD = srcPa.getPixels(source, srcRect, 1, false);
        int srcOffset = srcImD.bandOffsets[0];
        short[] srcData = ((short[][])srcImD.data)[0];
        int pixelStride = srcImD.pixelStride;
        int ind0 = pid.bitOffset;
        for (int h = 0; h < destRect.height; ++h) {
            int indE = ind0 + destRect.width;
            int b = ind0;
            int s = srcOffset;
            while (b < indE) {
                if ((srcData[s] & 0xFFFF) >= thresholdI) {
                    int n = offset + (b >> 3);
                    pid.data[n] = (byte)(pid.data[n] | byteTable[b % 8]);
                }
                ++b;
                s += pixelStride;
            }
            offset += pid.lineStride;
            srcOffset += srcImD.lineStride;
        }
        pa.setPackedPixels(pid);
    }

    private void intLoop(Raster source, WritableRaster dest, Rectangle destRect) {
        if (this.threshold <= -2.147483648E9) {
            this.setTo1(dest, destRect);
            return;
        }
        if (this.threshold > 2.147483647E9) {
            return;
        }
        int thresholdI = (int)Math.ceil(this.threshold);
        Rectangle srcRect = this.mapDestRect(destRect, 0);
        PixelAccessor pa = new PixelAccessor(dest.getSampleModel(), null);
        PackedImageData pid = pa.getPackedPixels(dest, destRect, true, false);
        int offset = pid.offset;
        PixelAccessor srcPa = new PixelAccessor(source.getSampleModel(), null);
        UnpackedImageData srcImD = srcPa.getPixels(source, srcRect, 3, false);
        int srcOffset = srcImD.bandOffsets[0];
        int[] srcData = ((int[][])srcImD.data)[0];
        int pixelStride = srcImD.pixelStride;
        int ind0 = pid.bitOffset;
        for (int h = 0; h < destRect.height; ++h) {
            int indE = ind0 + destRect.width;
            int b = ind0;
            int s = srcOffset;
            while (b < indE) {
                if ((double)srcData[s] >= this.threshold) {
                    int n = offset + (b >> 3);
                    pid.data[n] = (byte)(pid.data[n] | byteTable[b % 8]);
                }
                ++b;
                s += pixelStride;
            }
            offset += pid.lineStride;
            srcOffset += srcImD.lineStride;
        }
        pa.setPackedPixels(pid);
    }

    private void floatLoop(Raster source, WritableRaster dest, Rectangle destRect) {
        Rectangle srcRect = this.mapDestRect(destRect, 0);
        PixelAccessor pa = new PixelAccessor(dest.getSampleModel(), null);
        PackedImageData pid = pa.getPackedPixels(dest, destRect, true, false);
        int offset = pid.offset;
        PixelAccessor srcPa = new PixelAccessor(source.getSampleModel(), null);
        UnpackedImageData srcImD = srcPa.getPixels(source, srcRect, 4, false);
        int srcOffset = srcImD.bandOffsets[0];
        float[] srcData = ((float[][])srcImD.data)[0];
        int pixelStride = srcImD.pixelStride;
        int ind0 = pid.bitOffset;
        for (int h = 0; h < destRect.height; ++h) {
            int indE = ind0 + destRect.width;
            int b = ind0;
            int s = srcOffset;
            while (b < indE) {
                if ((double)srcData[s] > this.threshold) {
                    int n = offset + (b >> 3);
                    pid.data[n] = (byte)(pid.data[n] | byteTable[b % 8]);
                }
                ++b;
                s += pixelStride;
            }
            offset += pid.lineStride;
            srcOffset += srcImD.lineStride;
        }
        pa.setPackedPixels(pid);
    }

    private void doubleLoop(Raster source, WritableRaster dest, Rectangle destRect) {
        Rectangle srcRect = this.mapDestRect(destRect, 0);
        PixelAccessor pa = new PixelAccessor(dest.getSampleModel(), null);
        PackedImageData pid = pa.getPackedPixels(dest, destRect, true, false);
        int offset = pid.offset;
        PixelAccessor srcPa = new PixelAccessor(source.getSampleModel(), null);
        UnpackedImageData srcImD = srcPa.getPixels(source, srcRect, 5, false);
        int srcOffset = srcImD.bandOffsets[0];
        double[] srcData = ((double[][])srcImD.data)[0];
        int pixelStride = srcImD.pixelStride;
        int ind0 = pid.bitOffset;
        for (int h = 0; h < destRect.height; ++h) {
            int indE = ind0 + destRect.width;
            int b = ind0;
            int s = srcOffset;
            while (b < indE) {
                if (srcData[s] > this.threshold) {
                    int n = offset + (b >> 3);
                    pid.data[n] = (byte)(pid.data[n] | byteTable[b % 8]);
                }
                ++b;
                s += pixelStride;
            }
            offset += pid.lineStride;
            srcOffset += srcImD.lineStride;
        }
        pa.setPackedPixels(pid);
    }

    private void setTo1(Raster dest, Rectangle destRect) {
        BinarizeOpImage.initBitsOn();
        PixelAccessor pa = new PixelAccessor(dest.getSampleModel(), null);
        PackedImageData pid = pa.getPackedPixels(dest, destRect, true, false);
        int offset = pid.offset;
        for (int h = 0; h < destRect.height; ++h) {
            int ind0 = pid.bitOffset;
            int indE = ind0 + destRect.width - 1;
            if (indE < 8) {
                pid.data[offset] = (byte)(pid.data[offset] | bitsOn[indE]);
            } else {
                pid.data[offset] = (byte)(pid.data[offset] | bitsOn[7]);
                for (int b = offset + 1; b <= offset + (indE - 7) / 8; ++b) {
                    pid.data[b] = -1;
                }
                int remBits = indE % 8;
                if (remBits % 8 != 7) {
                    indE = offset + indE / 8;
                    pid.data[indE] = (byte)(pid.data[indE] | bitsOn[remBits]);
                }
            }
            offset += pid.lineStride;
        }
        pa.setPackedPixels(pid);
    }

    private static synchronized void initBitsOn() {
        if (bitsOn != null) {
            return;
        }
        bitsOn = new int[64];
        for (int i = 0; i < 8; ++i) {
            for (int j = i; j < 8; ++j) {
                int bi = 255 >> i;
                int bj = 255 << 7 - j;
                BinarizeOpImage.bitsOn[j + (i << 3)] = bi & bj;
            }
        }
    }
}

