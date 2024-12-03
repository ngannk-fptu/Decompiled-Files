/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.rendered;

import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.RasterOp;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import org.apache.batik.ext.awt.image.GraphicsUtil;

public class MorphologyOp
implements BufferedImageOp,
RasterOp {
    private int radiusX;
    private int radiusY;
    private boolean doDilation;
    private final int rangeX;
    private final int rangeY;
    private final ColorSpace sRGB = ColorSpace.getInstance(1000);
    private final ColorSpace lRGB = ColorSpace.getInstance(1004);

    public MorphologyOp(int radiusX, int radiusY, boolean doDilation) {
        if (radiusX <= 0 || radiusY <= 0) {
            throw new IllegalArgumentException("The radius of X-axis or Y-axis should not be Zero or Negatives.");
        }
        this.radiusX = radiusX;
        this.radiusY = radiusY;
        this.doDilation = doDilation;
        this.rangeX = 2 * radiusX + 1;
        this.rangeY = 2 * radiusY + 1;
    }

    @Override
    public Rectangle2D getBounds2D(Raster src) {
        this.checkCompatible(src.getSampleModel());
        return new Rectangle(src.getMinX(), src.getMinY(), src.getWidth(), src.getHeight());
    }

    @Override
    public Rectangle2D getBounds2D(BufferedImage src) {
        return new Rectangle(0, 0, src.getWidth(), src.getHeight());
    }

    @Override
    public Point2D getPoint2D(Point2D srcPt, Point2D destPt) {
        if (destPt == null) {
            destPt = new Point2D.Float();
        }
        destPt.setLocation(srcPt.getX(), srcPt.getY());
        return destPt;
    }

    private void checkCompatible(ColorModel colorModel, SampleModel sampleModel) {
        ColorSpace cs = colorModel.getColorSpace();
        if (!cs.equals(this.sRGB) && !cs.equals(this.lRGB)) {
            throw new IllegalArgumentException("Expected CS_sRGB or CS_LINEAR_RGB color model");
        }
        if (!(colorModel instanceof DirectColorModel)) {
            throw new IllegalArgumentException("colorModel should be an instance of DirectColorModel");
        }
        if (sampleModel.getDataType() != 3) {
            throw new IllegalArgumentException("colorModel's transferType should be DataBuffer.TYPE_INT");
        }
        DirectColorModel dcm = (DirectColorModel)colorModel;
        if (dcm.getRedMask() != 0xFF0000) {
            throw new IllegalArgumentException("red mask in source should be 0x00ff0000");
        }
        if (dcm.getGreenMask() != 65280) {
            throw new IllegalArgumentException("green mask in source should be 0x0000ff00");
        }
        if (dcm.getBlueMask() != 255) {
            throw new IllegalArgumentException("blue mask in source should be 0x000000ff");
        }
        if (dcm.getAlphaMask() != -16777216) {
            throw new IllegalArgumentException("alpha mask in source should be 0xff000000");
        }
    }

    private boolean isCompatible(ColorModel colorModel, SampleModel sampleModel) {
        ColorSpace cs = colorModel.getColorSpace();
        if (cs != ColorSpace.getInstance(1000) && cs != ColorSpace.getInstance(1004)) {
            return false;
        }
        if (!(colorModel instanceof DirectColorModel)) {
            return false;
        }
        if (sampleModel.getDataType() != 3) {
            return false;
        }
        DirectColorModel dcm = (DirectColorModel)colorModel;
        if (dcm.getRedMask() != 0xFF0000) {
            return false;
        }
        if (dcm.getGreenMask() != 65280) {
            return false;
        }
        if (dcm.getBlueMask() != 255) {
            return false;
        }
        return dcm.getAlphaMask() == -16777216;
    }

    private void checkCompatible(SampleModel model) {
        if (!(model instanceof SinglePixelPackedSampleModel)) {
            throw new IllegalArgumentException("MorphologyOp only works with Rasters using SinglePixelPackedSampleModels");
        }
        int nBands = model.getNumBands();
        if (nBands != 4) {
            throw new IllegalArgumentException("MorphologyOp only words with Rasters having 4 bands");
        }
        if (model.getDataType() != 3) {
            throw new IllegalArgumentException("MorphologyOp only works with Rasters using DataBufferInt");
        }
        int[] bitOffsets = ((SinglePixelPackedSampleModel)model).getBitOffsets();
        for (int i = 0; i < bitOffsets.length; ++i) {
            if (bitOffsets[i] % 8 == 0) continue;
            throw new IllegalArgumentException("MorphologyOp only works with Rasters using 8 bits per band : " + i + " : " + bitOffsets[i]);
        }
    }

    @Override
    public RenderingHints getRenderingHints() {
        return null;
    }

    @Override
    public WritableRaster createCompatibleDestRaster(Raster src) {
        this.checkCompatible(src.getSampleModel());
        return src.createCompatibleWritableRaster();
    }

    @Override
    public BufferedImage createCompatibleDestImage(BufferedImage src, ColorModel destCM) {
        BufferedImage dest = null;
        if (destCM == null) {
            destCM = src.getColorModel();
        }
        WritableRaster wr = destCM.createCompatibleWritableRaster(src.getWidth(), src.getHeight());
        this.checkCompatible(destCM, wr.getSampleModel());
        dest = new BufferedImage(destCM, wr, destCM.isAlphaPremultiplied(), null);
        return dest;
    }

    static final boolean isBetter(int v1, int v2, boolean doDilation) {
        if (v1 > v2) {
            return doDilation;
        }
        if (v1 < v2) {
            return !doDilation;
        }
        return true;
    }

    private void specialProcessRow(Raster src, WritableRaster dest) {
        int w = src.getWidth();
        int h = src.getHeight();
        DataBufferInt srcDB = (DataBufferInt)src.getDataBuffer();
        DataBufferInt dstDB = (DataBufferInt)dest.getDataBuffer();
        SinglePixelPackedSampleModel sppsm = (SinglePixelPackedSampleModel)src.getSampleModel();
        int srcOff = srcDB.getOffset() + sppsm.getOffset(src.getMinX() - src.getSampleModelTranslateX(), src.getMinY() - src.getSampleModelTranslateY());
        sppsm = (SinglePixelPackedSampleModel)dest.getSampleModel();
        int dstOff = dstDB.getOffset() + sppsm.getOffset(dest.getMinX() - dest.getSampleModelTranslateX(), dest.getMinY() - dest.getSampleModelTranslateY());
        int srcScanStride = ((SinglePixelPackedSampleModel)src.getSampleModel()).getScanlineStride();
        int dstScanStride = ((SinglePixelPackedSampleModel)dest.getSampleModel()).getScanlineStride();
        int[] srcPixels = srcDB.getBankData()[0];
        int[] destPixels = dstDB.getBankData()[0];
        if (w <= this.radiusX) {
            for (int i = 0; i < h; ++i) {
                int k;
                int sp = srcOff + i * srcScanStride;
                int dp = dstOff + i * dstScanStride;
                int pel = srcPixels[sp++];
                int a = pel >>> 24;
                int r = pel & 0xFF0000;
                int g = pel & 0xFF00;
                int b = pel & 0xFF;
                for (k = 1; k < w; ++k) {
                    int currentPixel = srcPixels[sp++];
                    int a1 = currentPixel >>> 24;
                    int r1 = currentPixel & 0xFF0000;
                    int g1 = currentPixel & 0xFF00;
                    int b1 = currentPixel & 0xFF;
                    if (MorphologyOp.isBetter(a1, a, this.doDilation)) {
                        a = a1;
                    }
                    if (MorphologyOp.isBetter(r1, r, this.doDilation)) {
                        r = r1;
                    }
                    if (MorphologyOp.isBetter(g1, g, this.doDilation)) {
                        g = g1;
                    }
                    if (!MorphologyOp.isBetter(b1, b, this.doDilation)) continue;
                    b = b1;
                }
                for (k = 0; k < w; ++k) {
                    destPixels[dp++] = a << 24 | r | g | b;
                }
            }
        } else {
            int[] bufferA = new int[w];
            int[] bufferR = new int[w];
            int[] bufferG = new int[w];
            int[] bufferB = new int[w];
            for (int i = 0; i < h; ++i) {
                int j;
                int b1;
                int g1;
                int r1;
                int a1;
                int sp = srcOff + i * srcScanStride;
                int dp = dstOff + i * dstScanStride;
                int bufferHead = 0;
                int maxIndexA = 0;
                int maxIndexR = 0;
                int maxIndexG = 0;
                int maxIndexB = 0;
                int pel = srcPixels[sp++];
                int a = pel >>> 24;
                int r = pel & 0xFF0000;
                int g = pel & 0xFF00;
                int b = pel & 0xFF;
                bufferA[0] = a;
                bufferR[0] = r;
                bufferG[0] = g;
                bufferB[0] = b;
                for (int k = 1; k <= this.radiusX; ++k) {
                    int currentPixel = srcPixels[sp++];
                    a1 = currentPixel >>> 24;
                    r1 = currentPixel & 0xFF0000;
                    g1 = currentPixel & 0xFF00;
                    b1 = currentPixel & 0xFF;
                    bufferA[k] = a1;
                    bufferR[k] = r1;
                    bufferG[k] = g1;
                    bufferB[k] = b1;
                    if (MorphologyOp.isBetter(a1, a, this.doDilation)) {
                        a = a1;
                        maxIndexA = k;
                    }
                    if (MorphologyOp.isBetter(r1, r, this.doDilation)) {
                        r = r1;
                        maxIndexR = k;
                    }
                    if (MorphologyOp.isBetter(g1, g, this.doDilation)) {
                        g = g1;
                        maxIndexG = k;
                    }
                    if (!MorphologyOp.isBetter(b1, b, this.doDilation)) continue;
                    b = b1;
                    maxIndexB = k;
                }
                destPixels[dp++] = a << 24 | r | g | b;
                for (j = 1; j <= w - this.radiusX - 1; ++j) {
                    int lastPixel = srcPixels[sp++];
                    a = bufferA[maxIndexA];
                    bufferA[j + this.radiusX] = a1 = lastPixel >>> 24;
                    if (MorphologyOp.isBetter(a1, a, this.doDilation)) {
                        a = a1;
                        maxIndexA = j + this.radiusX;
                    }
                    r = bufferR[maxIndexR];
                    bufferR[j + this.radiusX] = r1 = lastPixel & 0xFF0000;
                    if (MorphologyOp.isBetter(r1, r, this.doDilation)) {
                        r = r1;
                        maxIndexR = j + this.radiusX;
                    }
                    g = bufferG[maxIndexG];
                    bufferG[j + this.radiusX] = g1 = lastPixel & 0xFF00;
                    if (MorphologyOp.isBetter(g1, g, this.doDilation)) {
                        g = g1;
                        maxIndexG = j + this.radiusX;
                    }
                    b = bufferB[maxIndexB];
                    bufferB[j + this.radiusX] = b1 = lastPixel & 0xFF;
                    if (MorphologyOp.isBetter(b1, b, this.doDilation)) {
                        b = b1;
                        maxIndexB = j + this.radiusX;
                    }
                    destPixels[dp++] = a << 24 | r | g | b;
                }
                for (j = w - this.radiusX; j <= this.radiusX; ++j) {
                    destPixels[dp] = destPixels[dp - 1];
                    ++dp;
                }
                for (j = this.radiusX + 1; j < w; ++j) {
                    int m;
                    if (maxIndexA == bufferHead) {
                        a = bufferA[bufferHead + 1];
                        maxIndexA = bufferHead + 1;
                        for (m = bufferHead + 2; m < w; ++m) {
                            a1 = bufferA[m];
                            if (!MorphologyOp.isBetter(a1, a, this.doDilation)) continue;
                            a = a1;
                            maxIndexA = m;
                        }
                    } else {
                        a = bufferA[maxIndexA];
                    }
                    if (maxIndexR == bufferHead) {
                        r = bufferR[bufferHead + 1];
                        maxIndexR = bufferHead + 1;
                        for (m = bufferHead + 2; m < w; ++m) {
                            r1 = bufferR[m];
                            if (!MorphologyOp.isBetter(r1, r, this.doDilation)) continue;
                            r = r1;
                            maxIndexR = m;
                        }
                    } else {
                        r = bufferR[maxIndexR];
                    }
                    if (maxIndexG == bufferHead) {
                        g = bufferG[bufferHead + 1];
                        maxIndexG = bufferHead + 1;
                        for (m = bufferHead + 2; m < w; ++m) {
                            g1 = bufferG[m];
                            if (!MorphologyOp.isBetter(g1, g, this.doDilation)) continue;
                            g = g1;
                            maxIndexG = m;
                        }
                    } else {
                        g = bufferG[maxIndexG];
                    }
                    if (maxIndexB == bufferHead) {
                        b = bufferB[bufferHead + 1];
                        maxIndexB = bufferHead + 1;
                        for (m = bufferHead + 2; m < w; ++m) {
                            b1 = bufferB[m];
                            if (!MorphologyOp.isBetter(b1, b, this.doDilation)) continue;
                            b = b1;
                            maxIndexB = m;
                        }
                    } else {
                        b = bufferB[maxIndexB];
                    }
                    ++bufferHead;
                    destPixels[dp++] = a << 24 | r | g | b;
                }
            }
        }
    }

    private void specialProcessColumn(Raster src, WritableRaster dest) {
        int w = src.getWidth();
        int h = src.getHeight();
        DataBufferInt dstDB = (DataBufferInt)dest.getDataBuffer();
        int dstOff = dstDB.getOffset();
        int dstScanStride = ((SinglePixelPackedSampleModel)dest.getSampleModel()).getScanlineStride();
        int[] destPixels = dstDB.getBankData()[0];
        if (h <= this.radiusY) {
            for (int j = 0; j < w; ++j) {
                int k;
                int dp = dstOff + j;
                int cp = dstOff + j;
                int pel = destPixels[cp];
                cp += dstScanStride;
                int a = pel >>> 24;
                int r = pel & 0xFF0000;
                int g = pel & 0xFF00;
                int b = pel & 0xFF;
                for (k = 1; k < h; ++k) {
                    int currentPixel = destPixels[cp];
                    cp += dstScanStride;
                    int a1 = currentPixel >>> 24;
                    int r1 = currentPixel & 0xFF0000;
                    int g1 = currentPixel & 0xFF00;
                    int b1 = currentPixel & 0xFF;
                    if (MorphologyOp.isBetter(a1, a, this.doDilation)) {
                        a = a1;
                    }
                    if (MorphologyOp.isBetter(r1, r, this.doDilation)) {
                        r = r1;
                    }
                    if (MorphologyOp.isBetter(g1, g, this.doDilation)) {
                        g = g1;
                    }
                    if (!MorphologyOp.isBetter(b1, b, this.doDilation)) continue;
                    b = b1;
                }
                for (k = 0; k < h; ++k) {
                    destPixels[dp] = a << 24 | r | g | b;
                    dp += dstScanStride;
                }
            }
        } else {
            int[] bufferA = new int[h];
            int[] bufferR = new int[h];
            int[] bufferG = new int[h];
            int[] bufferB = new int[h];
            for (int j = 0; j < w; ++j) {
                int i;
                int b1;
                int g1;
                int r1;
                int a1;
                int dp = dstOff + j;
                int cp = dstOff + j;
                int bufferHead = 0;
                int maxIndexA = 0;
                int maxIndexR = 0;
                int maxIndexG = 0;
                int maxIndexB = 0;
                int pel = destPixels[cp];
                cp += dstScanStride;
                int a = pel >>> 24;
                int r = pel & 0xFF0000;
                int g = pel & 0xFF00;
                int b = pel & 0xFF;
                bufferA[0] = a;
                bufferR[0] = r;
                bufferG[0] = g;
                bufferB[0] = b;
                for (int k = 1; k <= this.radiusY; ++k) {
                    int currentPixel = destPixels[cp];
                    cp += dstScanStride;
                    a1 = currentPixel >>> 24;
                    r1 = currentPixel & 0xFF0000;
                    g1 = currentPixel & 0xFF00;
                    b1 = currentPixel & 0xFF;
                    bufferA[k] = a1;
                    bufferR[k] = r1;
                    bufferG[k] = g1;
                    bufferB[k] = b1;
                    if (MorphologyOp.isBetter(a1, a, this.doDilation)) {
                        a = a1;
                        maxIndexA = k;
                    }
                    if (MorphologyOp.isBetter(r1, r, this.doDilation)) {
                        r = r1;
                        maxIndexR = k;
                    }
                    if (MorphologyOp.isBetter(g1, g, this.doDilation)) {
                        g = g1;
                        maxIndexG = k;
                    }
                    if (!MorphologyOp.isBetter(b1, b, this.doDilation)) continue;
                    b = b1;
                    maxIndexB = k;
                }
                destPixels[dp] = a << 24 | r | g | b;
                dp += dstScanStride;
                for (i = 1; i <= h - this.radiusY - 1; ++i) {
                    int lastPixel = destPixels[cp];
                    cp += dstScanStride;
                    a = bufferA[maxIndexA];
                    bufferA[i + this.radiusY] = a1 = lastPixel >>> 24;
                    if (MorphologyOp.isBetter(a1, a, this.doDilation)) {
                        a = a1;
                        maxIndexA = i + this.radiusY;
                    }
                    r = bufferR[maxIndexR];
                    bufferR[i + this.radiusY] = r1 = lastPixel & 0xFF0000;
                    if (MorphologyOp.isBetter(r1, r, this.doDilation)) {
                        r = r1;
                        maxIndexR = i + this.radiusY;
                    }
                    g = bufferG[maxIndexG];
                    bufferG[i + this.radiusY] = g1 = lastPixel & 0xFF00;
                    if (MorphologyOp.isBetter(g1, g, this.doDilation)) {
                        g = g1;
                        maxIndexG = i + this.radiusY;
                    }
                    b = bufferB[maxIndexB];
                    bufferB[i + this.radiusY] = b1 = lastPixel & 0xFF;
                    if (MorphologyOp.isBetter(b1, b, this.doDilation)) {
                        b = b1;
                        maxIndexB = i + this.radiusY;
                    }
                    destPixels[dp] = a << 24 | r | g | b;
                    dp += dstScanStride;
                }
                for (i = h - this.radiusY; i <= this.radiusY; ++i) {
                    destPixels[dp] = destPixels[dp - dstScanStride];
                    dp += dstScanStride;
                }
                for (i = this.radiusY + 1; i < h; ++i) {
                    int m;
                    if (maxIndexA == bufferHead) {
                        a = bufferA[bufferHead + 1];
                        maxIndexA = bufferHead + 1;
                        for (m = bufferHead + 2; m < h; ++m) {
                            a1 = bufferA[m];
                            if (!MorphologyOp.isBetter(a1, a, this.doDilation)) continue;
                            a = a1;
                            maxIndexA = m;
                        }
                    } else {
                        a = bufferA[maxIndexA];
                    }
                    if (maxIndexR == bufferHead) {
                        r = bufferR[bufferHead + 1];
                        maxIndexR = bufferHead + 1;
                        for (m = bufferHead + 2; m < h; ++m) {
                            r1 = bufferR[m];
                            if (!MorphologyOp.isBetter(r1, r, this.doDilation)) continue;
                            r = r1;
                            maxIndexR = m;
                        }
                    } else {
                        r = bufferR[maxIndexR];
                    }
                    if (maxIndexG == bufferHead) {
                        g = bufferG[bufferHead + 1];
                        maxIndexG = bufferHead + 1;
                        for (m = bufferHead + 2; m < h; ++m) {
                            g1 = bufferG[m];
                            if (!MorphologyOp.isBetter(g1, g, this.doDilation)) continue;
                            g = g1;
                            maxIndexG = m;
                        }
                    } else {
                        g = bufferG[maxIndexG];
                    }
                    if (maxIndexB == bufferHead) {
                        b = bufferB[bufferHead + 1];
                        maxIndexB = bufferHead + 1;
                        for (m = bufferHead + 2; m < h; ++m) {
                            b1 = bufferB[m];
                            if (!MorphologyOp.isBetter(b1, b, this.doDilation)) continue;
                            b = b1;
                            maxIndexB = m;
                        }
                    } else {
                        b = bufferB[maxIndexB];
                    }
                    ++bufferHead;
                    destPixels[dp] = a << 24 | r | g | b;
                    dp += dstScanStride;
                }
            }
        }
    }

    @Override
    public WritableRaster filter(Raster src, WritableRaster dest) {
        int m;
        int hd;
        int head;
        int count;
        int tail;
        int m2;
        int lastPixel;
        int b1;
        int g1;
        int r1;
        int a1;
        int currentPixel;
        int k;
        int b;
        int g;
        int r;
        int a;
        int pel;
        int maxIndexB;
        int maxIndexG;
        int maxIndexR;
        int maxIndexA;
        int bufferHead;
        int dp;
        int[] bufferB;
        int[] bufferG;
        int[] bufferR;
        int[] bufferA;
        if (dest != null) {
            this.checkCompatible(dest.getSampleModel());
        } else {
            if (src == null) {
                throw new IllegalArgumentException("src should not be null when dest is null");
            }
            dest = this.createCompatibleDestRaster(src);
        }
        int w = src.getWidth();
        int h = src.getHeight();
        DataBufferInt srcDB = (DataBufferInt)src.getDataBuffer();
        DataBufferInt dstDB = (DataBufferInt)dest.getDataBuffer();
        int srcOff = srcDB.getOffset();
        int dstOff = dstDB.getOffset();
        int srcScanStride = ((SinglePixelPackedSampleModel)src.getSampleModel()).getScanlineStride();
        int dstScanStride = ((SinglePixelPackedSampleModel)dest.getSampleModel()).getScanlineStride();
        int[] srcPixels = srcDB.getBankData()[0];
        int[] destPixels = dstDB.getBankData()[0];
        if (w <= 2 * this.radiusX) {
            this.specialProcessRow(src, dest);
        } else {
            bufferA = new int[this.rangeX];
            bufferR = new int[this.rangeX];
            bufferG = new int[this.rangeX];
            bufferB = new int[this.rangeX];
            for (int i = 0; i < h; ++i) {
                int j;
                int sp = srcOff + i * srcScanStride;
                dp = dstOff + i * dstScanStride;
                bufferHead = 0;
                maxIndexA = 0;
                maxIndexR = 0;
                maxIndexG = 0;
                maxIndexB = 0;
                pel = srcPixels[sp++];
                a = pel >>> 24;
                r = pel & 0xFF0000;
                g = pel & 0xFF00;
                b = pel & 0xFF;
                bufferA[0] = a;
                bufferR[0] = r;
                bufferG[0] = g;
                bufferB[0] = b;
                for (k = 1; k <= this.radiusX; ++k) {
                    currentPixel = srcPixels[sp++];
                    a1 = currentPixel >>> 24;
                    r1 = currentPixel & 0xFF0000;
                    g1 = currentPixel & 0xFF00;
                    b1 = currentPixel & 0xFF;
                    bufferA[k] = a1;
                    bufferR[k] = r1;
                    bufferG[k] = g1;
                    bufferB[k] = b1;
                    if (MorphologyOp.isBetter(a1, a, this.doDilation)) {
                        a = a1;
                        maxIndexA = k;
                    }
                    if (MorphologyOp.isBetter(r1, r, this.doDilation)) {
                        r = r1;
                        maxIndexR = k;
                    }
                    if (MorphologyOp.isBetter(g1, g, this.doDilation)) {
                        g = g1;
                        maxIndexG = k;
                    }
                    if (!MorphologyOp.isBetter(b1, b, this.doDilation)) continue;
                    b = b1;
                    maxIndexB = k;
                }
                destPixels[dp++] = a << 24 | r | g | b;
                for (j = 1; j <= this.radiusX; ++j) {
                    lastPixel = srcPixels[sp++];
                    a = bufferA[maxIndexA];
                    bufferA[j + this.radiusX] = a1 = lastPixel >>> 24;
                    if (MorphologyOp.isBetter(a1, a, this.doDilation)) {
                        a = a1;
                        maxIndexA = j + this.radiusX;
                    }
                    r = bufferR[maxIndexR];
                    bufferR[j + this.radiusX] = r1 = lastPixel & 0xFF0000;
                    if (MorphologyOp.isBetter(r1, r, this.doDilation)) {
                        r = r1;
                        maxIndexR = j + this.radiusX;
                    }
                    g = bufferG[maxIndexG];
                    bufferG[j + this.radiusX] = g1 = lastPixel & 0xFF00;
                    if (MorphologyOp.isBetter(g1, g, this.doDilation)) {
                        g = g1;
                        maxIndexG = j + this.radiusX;
                    }
                    b = bufferB[maxIndexB];
                    bufferB[j + this.radiusX] = b1 = lastPixel & 0xFF;
                    if (MorphologyOp.isBetter(b1, b, this.doDilation)) {
                        b = b1;
                        maxIndexB = j + this.radiusX;
                    }
                    destPixels[dp++] = a << 24 | r | g | b;
                }
                for (j = this.radiusX + 1; j <= w - 1 - this.radiusX; ++j) {
                    lastPixel = srcPixels[sp++];
                    a1 = lastPixel >>> 24;
                    r1 = lastPixel & 0xFF0000;
                    g1 = lastPixel & 0xFF00;
                    b1 = lastPixel & 0xFF;
                    bufferA[bufferHead] = a1;
                    bufferR[bufferHead] = r1;
                    bufferG[bufferHead] = g1;
                    bufferB[bufferHead] = b1;
                    if (maxIndexA == bufferHead) {
                        a = bufferA[0];
                        maxIndexA = 0;
                        for (m2 = 1; m2 < this.rangeX; ++m2) {
                            a1 = bufferA[m2];
                            if (!MorphologyOp.isBetter(a1, a, this.doDilation)) continue;
                            a = a1;
                            maxIndexA = m2;
                        }
                    } else {
                        a = bufferA[maxIndexA];
                        if (MorphologyOp.isBetter(a1, a, this.doDilation)) {
                            a = a1;
                            maxIndexA = bufferHead;
                        }
                    }
                    if (maxIndexR == bufferHead) {
                        r = bufferR[0];
                        maxIndexR = 0;
                        for (m2 = 1; m2 < this.rangeX; ++m2) {
                            r1 = bufferR[m2];
                            if (!MorphologyOp.isBetter(r1, r, this.doDilation)) continue;
                            r = r1;
                            maxIndexR = m2;
                        }
                    } else {
                        r = bufferR[maxIndexR];
                        if (MorphologyOp.isBetter(r1, r, this.doDilation)) {
                            r = r1;
                            maxIndexR = bufferHead;
                        }
                    }
                    if (maxIndexG == bufferHead) {
                        g = bufferG[0];
                        maxIndexG = 0;
                        for (m2 = 1; m2 < this.rangeX; ++m2) {
                            g1 = bufferG[m2];
                            if (!MorphologyOp.isBetter(g1, g, this.doDilation)) continue;
                            g = g1;
                            maxIndexG = m2;
                        }
                    } else {
                        g = bufferG[maxIndexG];
                        if (MorphologyOp.isBetter(g1, g, this.doDilation)) {
                            g = g1;
                            maxIndexG = bufferHead;
                        }
                    }
                    if (maxIndexB == bufferHead) {
                        b = bufferB[0];
                        maxIndexB = 0;
                        for (m2 = 1; m2 < this.rangeX; ++m2) {
                            b1 = bufferB[m2];
                            if (!MorphologyOp.isBetter(b1, b, this.doDilation)) continue;
                            b = b1;
                            maxIndexB = m2;
                        }
                    } else {
                        b = bufferB[maxIndexB];
                        if (MorphologyOp.isBetter(b1, b, this.doDilation)) {
                            b = b1;
                            maxIndexB = bufferHead;
                        }
                    }
                    destPixels[dp++] = a << 24 | r | g | b;
                    bufferHead = (bufferHead + 1) % this.rangeX;
                }
                tail = bufferHead == 0 ? this.rangeX - 1 : bufferHead - 1;
                count = this.rangeX - 1;
                for (int j2 = w - this.radiusX; j2 < w; ++j2) {
                    head = (bufferHead + 1) % this.rangeX;
                    if (maxIndexA == bufferHead) {
                        a = bufferA[tail];
                        hd = head;
                        for (m = 1; m < count; ++m) {
                            a1 = bufferA[hd];
                            if (MorphologyOp.isBetter(a1, a, this.doDilation)) {
                                a = a1;
                                maxIndexA = hd;
                            }
                            hd = (hd + 1) % this.rangeX;
                        }
                    }
                    if (maxIndexR == bufferHead) {
                        r = bufferR[tail];
                        hd = head;
                        for (m = 1; m < count; ++m) {
                            r1 = bufferR[hd];
                            if (MorphologyOp.isBetter(r1, r, this.doDilation)) {
                                r = r1;
                                maxIndexR = hd;
                            }
                            hd = (hd + 1) % this.rangeX;
                        }
                    }
                    if (maxIndexG == bufferHead) {
                        g = bufferG[tail];
                        hd = head;
                        for (m = 1; m < count; ++m) {
                            g1 = bufferG[hd];
                            if (MorphologyOp.isBetter(g1, g, this.doDilation)) {
                                g = g1;
                                maxIndexG = hd;
                            }
                            hd = (hd + 1) % this.rangeX;
                        }
                    }
                    if (maxIndexB == bufferHead) {
                        b = bufferB[tail];
                        hd = head;
                        for (m = 1; m < count; ++m) {
                            b1 = bufferB[hd];
                            if (MorphologyOp.isBetter(b1, b, this.doDilation)) {
                                b = b1;
                                maxIndexB = hd;
                            }
                            hd = (hd + 1) % this.rangeX;
                        }
                    }
                    destPixels[dp++] = a << 24 | r | g | b;
                    bufferHead = (bufferHead + 1) % this.rangeX;
                    --count;
                }
            }
        }
        if (h <= 2 * this.radiusY) {
            this.specialProcessColumn(src, dest);
        } else {
            bufferA = new int[this.rangeY];
            bufferR = new int[this.rangeY];
            bufferG = new int[this.rangeY];
            bufferB = new int[this.rangeY];
            for (int j = 0; j < w; ++j) {
                int i;
                dp = dstOff + j;
                int cp = dstOff + j;
                bufferHead = 0;
                maxIndexA = 0;
                maxIndexR = 0;
                maxIndexG = 0;
                maxIndexB = 0;
                pel = destPixels[cp];
                cp += dstScanStride;
                a = pel >>> 24;
                r = pel & 0xFF0000;
                g = pel & 0xFF00;
                b = pel & 0xFF;
                bufferA[0] = a;
                bufferR[0] = r;
                bufferG[0] = g;
                bufferB[0] = b;
                for (k = 1; k <= this.radiusY; ++k) {
                    currentPixel = destPixels[cp];
                    cp += dstScanStride;
                    a1 = currentPixel >>> 24;
                    r1 = currentPixel & 0xFF0000;
                    g1 = currentPixel & 0xFF00;
                    b1 = currentPixel & 0xFF;
                    bufferA[k] = a1;
                    bufferR[k] = r1;
                    bufferG[k] = g1;
                    bufferB[k] = b1;
                    if (MorphologyOp.isBetter(a1, a, this.doDilation)) {
                        a = a1;
                        maxIndexA = k;
                    }
                    if (MorphologyOp.isBetter(r1, r, this.doDilation)) {
                        r = r1;
                        maxIndexR = k;
                    }
                    if (MorphologyOp.isBetter(g1, g, this.doDilation)) {
                        g = g1;
                        maxIndexG = k;
                    }
                    if (!MorphologyOp.isBetter(b1, b, this.doDilation)) continue;
                    b = b1;
                    maxIndexB = k;
                }
                destPixels[dp] = a << 24 | r | g | b;
                dp += dstScanStride;
                for (i = 1; i <= this.radiusY; ++i) {
                    int maxI = i + this.radiusY;
                    lastPixel = destPixels[cp];
                    cp += dstScanStride;
                    a = bufferA[maxIndexA];
                    bufferA[maxI] = a1 = lastPixel >>> 24;
                    if (MorphologyOp.isBetter(a1, a, this.doDilation)) {
                        a = a1;
                        maxIndexA = maxI;
                    }
                    r = bufferR[maxIndexR];
                    bufferR[maxI] = r1 = lastPixel & 0xFF0000;
                    if (MorphologyOp.isBetter(r1, r, this.doDilation)) {
                        r = r1;
                        maxIndexR = maxI;
                    }
                    g = bufferG[maxIndexG];
                    bufferG[maxI] = g1 = lastPixel & 0xFF00;
                    if (MorphologyOp.isBetter(g1, g, this.doDilation)) {
                        g = g1;
                        maxIndexG = maxI;
                    }
                    b = bufferB[maxIndexB];
                    bufferB[maxI] = b1 = lastPixel & 0xFF;
                    if (MorphologyOp.isBetter(b1, b, this.doDilation)) {
                        b = b1;
                        maxIndexB = maxI;
                    }
                    destPixels[dp] = a << 24 | r | g | b;
                    dp += dstScanStride;
                }
                for (i = this.radiusY + 1; i <= h - 1 - this.radiusY; ++i) {
                    lastPixel = destPixels[cp];
                    cp += dstScanStride;
                    a1 = lastPixel >>> 24;
                    r1 = lastPixel & 0xFF0000;
                    g1 = lastPixel & 0xFF00;
                    b1 = lastPixel & 0xFF;
                    bufferA[bufferHead] = a1;
                    bufferR[bufferHead] = r1;
                    bufferG[bufferHead] = g1;
                    bufferB[bufferHead] = b1;
                    if (maxIndexA == bufferHead) {
                        a = bufferA[0];
                        maxIndexA = 0;
                        for (m2 = 1; m2 <= 2 * this.radiusY; ++m2) {
                            a1 = bufferA[m2];
                            if (!MorphologyOp.isBetter(a1, a, this.doDilation)) continue;
                            a = a1;
                            maxIndexA = m2;
                        }
                    } else {
                        a = bufferA[maxIndexA];
                        if (MorphologyOp.isBetter(a1, a, this.doDilation)) {
                            a = a1;
                            maxIndexA = bufferHead;
                        }
                    }
                    if (maxIndexR == bufferHead) {
                        r = bufferR[0];
                        maxIndexR = 0;
                        for (m2 = 1; m2 <= 2 * this.radiusY; ++m2) {
                            r1 = bufferR[m2];
                            if (!MorphologyOp.isBetter(r1, r, this.doDilation)) continue;
                            r = r1;
                            maxIndexR = m2;
                        }
                    } else {
                        r = bufferR[maxIndexR];
                        if (MorphologyOp.isBetter(r1, r, this.doDilation)) {
                            r = r1;
                            maxIndexR = bufferHead;
                        }
                    }
                    if (maxIndexG == bufferHead) {
                        g = bufferG[0];
                        maxIndexG = 0;
                        for (m2 = 1; m2 <= 2 * this.radiusY; ++m2) {
                            g1 = bufferG[m2];
                            if (!MorphologyOp.isBetter(g1, g, this.doDilation)) continue;
                            g = g1;
                            maxIndexG = m2;
                        }
                    } else {
                        g = bufferG[maxIndexG];
                        if (MorphologyOp.isBetter(g1, g, this.doDilation)) {
                            g = g1;
                            maxIndexG = bufferHead;
                        }
                    }
                    if (maxIndexB == bufferHead) {
                        b = bufferB[0];
                        maxIndexB = 0;
                        for (m2 = 1; m2 <= 2 * this.radiusY; ++m2) {
                            b1 = bufferB[m2];
                            if (!MorphologyOp.isBetter(b1, b, this.doDilation)) continue;
                            b = b1;
                            maxIndexB = m2;
                        }
                    } else {
                        b = bufferB[maxIndexB];
                        if (MorphologyOp.isBetter(b1, b, this.doDilation)) {
                            b = b1;
                            maxIndexB = bufferHead;
                        }
                    }
                    destPixels[dp] = a << 24 | r | g | b;
                    dp += dstScanStride;
                    bufferHead = (bufferHead + 1) % this.rangeY;
                }
                tail = bufferHead == 0 ? 2 * this.radiusY : bufferHead - 1;
                count = this.rangeY - 1;
                for (int i2 = h - this.radiusY; i2 < h - 1; ++i2) {
                    head = (bufferHead + 1) % this.rangeY;
                    if (maxIndexA == bufferHead) {
                        a = bufferA[tail];
                        hd = head;
                        for (m = 1; m < count; ++m) {
                            a1 = bufferA[hd];
                            if (MorphologyOp.isBetter(a1, a, this.doDilation)) {
                                a = a1;
                                maxIndexA = hd;
                            }
                            hd = (hd + 1) % this.rangeY;
                        }
                    }
                    if (maxIndexR == bufferHead) {
                        r = bufferR[tail];
                        hd = head;
                        for (m = 1; m < count; ++m) {
                            r1 = bufferR[hd];
                            if (MorphologyOp.isBetter(r1, r, this.doDilation)) {
                                r = r1;
                                maxIndexR = hd;
                            }
                            hd = (hd + 1) % this.rangeY;
                        }
                    }
                    if (maxIndexG == bufferHead) {
                        g = bufferG[tail];
                        hd = head;
                        for (m = 1; m < count; ++m) {
                            g1 = bufferG[hd];
                            if (MorphologyOp.isBetter(g1, g, this.doDilation)) {
                                g = g1;
                                maxIndexG = hd;
                            }
                            hd = (hd + 1) % this.rangeY;
                        }
                    }
                    if (maxIndexB == bufferHead) {
                        b = bufferB[tail];
                        hd = head;
                        for (m = 1; m < count; ++m) {
                            b1 = bufferB[hd];
                            if (MorphologyOp.isBetter(b1, b, this.doDilation)) {
                                b = b1;
                                maxIndexB = hd;
                            }
                            hd = (hd + 1) % this.rangeY;
                        }
                    }
                    destPixels[dp] = a << 24 | r | g | b;
                    dp += dstScanStride;
                    bufferHead = (bufferHead + 1) % this.rangeY;
                    --count;
                }
            }
        }
        return dest;
    }

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dest) {
        if (src == null) {
            throw new NullPointerException("Source image should not be null");
        }
        BufferedImage origSrc = src;
        BufferedImage finalDest = dest;
        if (!this.isCompatible(src.getColorModel(), src.getSampleModel())) {
            src = new BufferedImage(src.getWidth(), src.getHeight(), 3);
            GraphicsUtil.copyData(origSrc, src);
        } else if (!src.isAlphaPremultiplied()) {
            ColorModel srcCM = src.getColorModel();
            ColorModel srcCMPre = GraphicsUtil.coerceColorModel(srcCM, true);
            src = new BufferedImage(srcCMPre, src.getRaster(), true, null);
            GraphicsUtil.copyData(origSrc, src);
        }
        if (dest == null) {
            finalDest = dest = this.createCompatibleDestImage(src, null);
        } else if (!this.isCompatible(dest.getColorModel(), dest.getSampleModel())) {
            dest = this.createCompatibleDestImage(src, null);
        } else if (!dest.isAlphaPremultiplied()) {
            ColorModel dstCM = dest.getColorModel();
            ColorModel dstCMPre = GraphicsUtil.coerceColorModel(dstCM, true);
            dest = new BufferedImage(dstCMPre, finalDest.getRaster(), true, null);
        }
        this.filter(src.getRaster(), dest.getRaster());
        if (src.getRaster() == origSrc.getRaster() && src.isAlphaPremultiplied() != origSrc.isAlphaPremultiplied()) {
            GraphicsUtil.copyData(src, origSrc);
        }
        if (dest.getRaster() != finalDest.getRaster() || dest.isAlphaPremultiplied() != finalDest.isAlphaPremultiplied()) {
            GraphicsUtil.copyData(dest, finalDest);
        }
        return finalDest;
    }
}

