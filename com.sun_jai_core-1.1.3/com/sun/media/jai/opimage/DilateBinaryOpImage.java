/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.AreaOpImage;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.PackedImageData;
import javax.media.jai.PixelAccessor;

final class DilateBinaryOpImage
extends AreaOpImage {
    protected KernelJAI kernel;
    private int kw;
    private int kh;
    private int kx;
    private int ky;
    private int[] kdataPack;
    private int kwPack;

    private static Map configHelper(Map configuration) {
        Map config;
        if (configuration == null) {
            config = new RenderingHints(JAI.KEY_REPLACE_INDEX_COLOR_MODEL, Boolean.FALSE);
        } else {
            config = configuration;
            if (!config.containsKey(JAI.KEY_REPLACE_INDEX_COLOR_MODEL)) {
                config.put(JAI.KEY_REPLACE_INDEX_COLOR_MODEL, Boolean.FALSE);
                RenderingHints hints = (RenderingHints)configuration;
                config = (RenderingHints)hints.clone();
            }
        }
        return config;
    }

    public DilateBinaryOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, KernelJAI kernel) {
        super(source, layout, DilateBinaryOpImage.configHelper(config), true, extender, kernel.getLeftPadding(), kernel.getRightPadding(), kernel.getTopPadding(), kernel.getBottomPadding());
        this.kernel = kernel;
        this.kw = kernel.getWidth();
        this.kh = kernel.getHeight();
        this.kx = kernel.getXOrigin();
        this.ky = kernel.getYOrigin();
        this.kwPack = (this.kw + 31) / 32;
        this.kdataPack = this.packKernel(kernel);
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        Raster source = sources[0];
        PixelAccessor pa = new PixelAccessor(source.getSampleModel(), null);
        PackedImageData srcIm = pa.getPackedPixels(source, source.getBounds(), false, false);
        pa = new PixelAccessor(dest.getSampleModel(), null);
        PackedImageData dstIm = pa.getPackedPixels(dest, destRect, true, false);
        int[] srcUK = new int[this.kwPack * this.kh];
        int dheight = destRect.height;
        int dwidth = destRect.width;
        int sOffset = srcIm.offset;
        int dOffset = dstIm.offset;
        for (int j = 0; j < dheight; ++j) {
            int val;
            byte selement;
            int sOffsetB;
            int m;
            int byteLoc;
            int bitLoc;
            int lastCol;
            int i;
            for (int m2 = 0; m2 < srcUK.length; ++m2) {
                srcUK[m2] = 0;
            }
            for (i = 0; i < this.kw - 1; ++i) {
                DilateBinaryOpImage.bitShiftMatrixLeft(srcUK, this.kh, this.kwPack);
                lastCol = this.kwPack - 1;
                bitLoc = srcIm.bitOffset + i;
                byteLoc = bitLoc >> 3;
                bitLoc = 7 - (bitLoc & 7);
                m = 0;
                sOffsetB = sOffset;
                while (m < this.kh) {
                    selement = srcIm.data[sOffsetB + byteLoc];
                    val = selement >> bitLoc & 1;
                    int n = lastCol;
                    srcUK[n] = srcUK[n] | val;
                    lastCol += this.kwPack;
                    ++m;
                    sOffsetB += srcIm.lineStride;
                }
            }
            block4: for (i = 0; i < dwidth; ++i) {
                DilateBinaryOpImage.bitShiftMatrixLeft(srcUK, this.kh, this.kwPack);
                lastCol = this.kwPack - 1;
                bitLoc = srcIm.bitOffset + i + this.kw - 1;
                byteLoc = bitLoc >> 3;
                bitLoc = 7 - (bitLoc & 7);
                m = 0;
                sOffsetB = sOffset;
                while (m < this.kh) {
                    selement = srcIm.data[sOffsetB + byteLoc];
                    val = selement >> bitLoc & 1;
                    int n = lastCol;
                    srcUK[n] = srcUK[n] | val;
                    lastCol += this.kwPack;
                    ++m;
                    sOffsetB += srcIm.lineStride;
                }
                for (m = 0; m < srcUK.length; ++m) {
                    if ((srcUK[m] & this.kdataPack[m]) == 0) continue;
                    int dBitLoc = dstIm.bitOffset + i;
                    int dshift = 7 - (dBitLoc & 7);
                    int dByteLoc = (dBitLoc >> 3) + dOffset;
                    int delement = dstIm.data[dByteLoc];
                    dstIm.data[dByteLoc] = (byte)(delement |= 1 << dshift);
                    continue block4;
                }
            }
            sOffset += srcIm.lineStride;
            dOffset += dstIm.lineStride;
        }
        pa.setPackedPixels(dstIm);
    }

    private final int[] packKernel(KernelJAI kernel) {
        int kw = kernel.getWidth();
        int kh = kernel.getHeight();
        int kwPack = (31 + kw) / 32;
        int[] kerPacked = new int[kwPack * kh];
        float[] kdata = kernel.getKernelData();
        for (int j = 0; j < kw; ++j) {
            int m = j;
            int lastCol = kwPack - 1;
            DilateBinaryOpImage.bitShiftMatrixLeft(kerPacked, kh, kwPack);
            int i = 0;
            while (i < kh) {
                if (kdata[m] > 0.9f) {
                    int n = lastCol;
                    kerPacked[n] = kerPacked[n] | 1;
                }
                ++i;
                lastCol += kwPack;
                m += kw;
            }
        }
        return kerPacked;
    }

    private static final void bitShiftMatrixLeft(int[] mat, int rows, int cols) {
        int m = 0;
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < cols - 1; ++j) {
                mat[m] = mat[m] << 1 | mat[m + 1] >>> 31;
                ++m;
            }
            int n = m++;
            mat[n] = mat[n] << 1;
        }
    }
}

