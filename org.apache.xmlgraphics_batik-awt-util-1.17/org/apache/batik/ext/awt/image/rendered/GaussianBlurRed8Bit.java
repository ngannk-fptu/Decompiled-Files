/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.rendered;

import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.image.ColorModel;
import java.awt.image.ConvolveOp;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Kernel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.ext.awt.image.rendered.AbstractRed;
import org.apache.batik.ext.awt.image.rendered.CachableRed;

public class GaussianBlurRed8Bit
extends AbstractRed {
    int xinset;
    int yinset;
    double stdDevX;
    double stdDevY;
    RenderingHints hints;
    ConvolveOp[] convOp = new ConvolveOp[2];
    int dX;
    int dY;
    static final float SQRT2PI = (float)Math.sqrt(Math.PI * 2);
    static final float DSQRT2PI = SQRT2PI * 3.0f / 4.0f;
    static final float precision = 0.499f;

    public GaussianBlurRed8Bit(CachableRed src, double stdDev, RenderingHints rh) {
        this(src, stdDev, stdDev, rh);
    }

    public GaussianBlurRed8Bit(CachableRed src, double stdDevX, double stdDevY, RenderingHints rh) {
        boolean highQuality;
        this.stdDevX = stdDevX;
        this.stdDevY = stdDevY;
        this.hints = rh;
        this.xinset = GaussianBlurRed8Bit.surroundPixels(stdDevX, rh);
        this.yinset = GaussianBlurRed8Bit.surroundPixels(stdDevY, rh);
        Rectangle myBounds = src.getBounds();
        myBounds.x += this.xinset;
        myBounds.y += this.yinset;
        myBounds.width -= 2 * this.xinset;
        myBounds.height -= 2 * this.yinset;
        if (myBounds.width <= 0 || myBounds.height <= 0) {
            myBounds.width = 0;
            myBounds.height = 0;
        }
        ColorModel cm = GaussianBlurRed8Bit.fixColorModel(src);
        SampleModel sm = src.getSampleModel();
        int tw = sm.getWidth();
        int th = sm.getHeight();
        if (tw > myBounds.width) {
            tw = myBounds.width;
        }
        if (th > myBounds.height) {
            th = myBounds.height;
        }
        sm = cm.createCompatibleSampleModel(tw, th);
        this.init(src, myBounds, cm, sm, src.getTileGridXOffset() + this.xinset, src.getTileGridYOffset() + this.yinset, null);
        boolean bl = highQuality = this.hints != null && RenderingHints.VALUE_RENDER_QUALITY.equals(this.hints.get(RenderingHints.KEY_RENDERING));
        if (this.xinset != 0 && (stdDevX < 2.0 || highQuality)) {
            this.convOp[0] = new ConvolveOp(this.makeQualityKernelX(this.xinset * 2 + 1));
        } else {
            this.dX = (int)Math.floor((double)DSQRT2PI * stdDevX + 0.5);
        }
        if (this.yinset != 0 && (stdDevY < 2.0 || highQuality)) {
            this.convOp[1] = new ConvolveOp(this.makeQualityKernelY(this.yinset * 2 + 1));
        } else {
            this.dY = (int)Math.floor((double)DSQRT2PI * stdDevY + 0.5);
        }
    }

    public static int surroundPixels(double stdDev) {
        return GaussianBlurRed8Bit.surroundPixels(stdDev, null);
    }

    public static int surroundPixels(double stdDev, RenderingHints hints) {
        boolean highQuality;
        boolean bl = highQuality = hints != null && RenderingHints.VALUE_RENDER_QUALITY.equals(hints.get(RenderingHints.KEY_RENDERING));
        if (stdDev < 2.0 || highQuality) {
            float areaSum = (float)(0.5 / (stdDev * (double)SQRT2PI));
            int i = 0;
            while (areaSum < 0.499f) {
                areaSum += (float)(Math.pow(Math.E, (double)(-i * i) / (2.0 * stdDev * stdDev)) / (stdDev * (double)SQRT2PI));
                ++i;
            }
            return i;
        }
        int diam = (int)Math.floor((double)DSQRT2PI * stdDev + 0.5);
        if (diam % 2 == 0) {
            return diam - 1 + diam / 2;
        }
        return diam - 2 + diam / 2;
    }

    private float[] computeQualityKernelData(int len, double stdDev) {
        int i;
        float[] kernelData = new float[len];
        int mid = len / 2;
        float sum = 0.0f;
        for (i = 0; i < len; ++i) {
            kernelData[i] = (float)(Math.pow(Math.E, (double)(-(i - mid) * (i - mid)) / (2.0 * stdDev * stdDev)) / ((double)SQRT2PI * stdDev));
            sum += kernelData[i];
        }
        i = 0;
        while (i < len) {
            int n = i++;
            kernelData[n] = kernelData[n] / sum;
        }
        return kernelData;
    }

    private Kernel makeQualityKernelX(int len) {
        return new Kernel(len, 1, this.computeQualityKernelData(len, this.stdDevX));
    }

    private Kernel makeQualityKernelY(int len) {
        return new Kernel(1, len, this.computeQualityKernelData(len, this.stdDevY));
    }

    @Override
    public WritableRaster copyData(WritableRaster wr) {
        int skipX;
        CachableRed src = (CachableRed)this.getSources().get(0);
        Rectangle r = wr.getBounds();
        r.x -= this.xinset;
        r.y -= this.yinset;
        r.width += 2 * this.xinset;
        r.height += 2 * this.yinset;
        ColorModel srcCM = src.getColorModel();
        WritableRaster tmpR1 = null;
        WritableRaster tmpR2 = null;
        tmpR1 = srcCM.createCompatibleWritableRaster(r.width, r.height);
        WritableRaster fill = tmpR1.createWritableTranslatedChild(r.x, r.y);
        src.copyData(fill);
        if (srcCM.hasAlpha() && !srcCM.isAlphaPremultiplied()) {
            GraphicsUtil.coerceData(tmpR1, srcCM, true);
        }
        if (this.xinset == 0) {
            skipX = 0;
        } else if (this.convOp[0] != null) {
            tmpR2 = this.getColorModel().createCompatibleWritableRaster(r.width, r.height);
            tmpR2 = this.convOp[0].filter(tmpR1, tmpR2);
            skipX = this.convOp[0].getKernel().getXOrigin();
            WritableRaster tmp = tmpR1;
            tmpR1 = tmpR2;
            tmpR2 = tmp;
        } else if ((this.dX & 1) == 0) {
            tmpR1 = this.boxFilterH(tmpR1, tmpR1, 0, 0, this.dX, this.dX / 2);
            tmpR1 = this.boxFilterH(tmpR1, tmpR1, this.dX / 2, 0, this.dX, this.dX / 2 - 1);
            tmpR1 = this.boxFilterH(tmpR1, tmpR1, this.dX - 1, 0, this.dX + 1, this.dX / 2);
            skipX = this.dX - 1 + this.dX / 2;
        } else {
            tmpR1 = this.boxFilterH(tmpR1, tmpR1, 0, 0, this.dX, this.dX / 2);
            tmpR1 = this.boxFilterH(tmpR1, tmpR1, this.dX / 2, 0, this.dX, this.dX / 2);
            tmpR1 = this.boxFilterH(tmpR1, tmpR1, this.dX - 2, 0, this.dX, this.dX / 2);
            skipX = this.dX - 2 + this.dX / 2;
        }
        if (this.yinset == 0) {
            tmpR2 = tmpR1;
        } else if (this.convOp[1] != null) {
            if (tmpR2 == null) {
                tmpR2 = this.getColorModel().createCompatibleWritableRaster(r.width, r.height);
            }
            tmpR2 = this.convOp[1].filter(tmpR1, tmpR2);
        } else {
            if ((this.dY & 1) == 0) {
                tmpR1 = this.boxFilterV(tmpR1, tmpR1, skipX, 0, this.dY, this.dY / 2);
                tmpR1 = this.boxFilterV(tmpR1, tmpR1, skipX, this.dY / 2, this.dY, this.dY / 2 - 1);
                tmpR1 = this.boxFilterV(tmpR1, tmpR1, skipX, this.dY - 1, this.dY + 1, this.dY / 2);
            } else {
                tmpR1 = this.boxFilterV(tmpR1, tmpR1, skipX, 0, this.dY, this.dY / 2);
                tmpR1 = this.boxFilterV(tmpR1, tmpR1, skipX, this.dY / 2, this.dY, this.dY / 2);
                tmpR1 = this.boxFilterV(tmpR1, tmpR1, skipX, this.dY - 2, this.dY, this.dY / 2);
            }
            tmpR2 = tmpR1;
        }
        tmpR2 = tmpR2.createWritableTranslatedChild(r.x, r.y);
        GraphicsUtil.copyData(tmpR2, wr);
        return wr;
    }

    private WritableRaster boxFilterH(Raster src, WritableRaster dest, int skipX, int skipY, int boxSz, int loc) {
        int w = src.getWidth();
        int h = src.getHeight();
        if (w < 2 * skipX + boxSz) {
            return dest;
        }
        if (h < 2 * skipY) {
            return dest;
        }
        SinglePixelPackedSampleModel srcSPPSM = (SinglePixelPackedSampleModel)src.getSampleModel();
        SinglePixelPackedSampleModel dstSPPSM = (SinglePixelPackedSampleModel)dest.getSampleModel();
        int srcScanStride = srcSPPSM.getScanlineStride();
        int dstScanStride = dstSPPSM.getScanlineStride();
        DataBufferInt srcDB = (DataBufferInt)src.getDataBuffer();
        DataBufferInt dstDB = (DataBufferInt)dest.getDataBuffer();
        int srcOff = srcDB.getOffset() + srcSPPSM.getOffset(src.getMinX() - src.getSampleModelTranslateX(), src.getMinY() - src.getSampleModelTranslateY());
        int dstOff = dstDB.getOffset() + dstSPPSM.getOffset(dest.getMinX() - dest.getSampleModelTranslateX(), dest.getMinY() - dest.getSampleModelTranslateY());
        int[] srcPixels = srcDB.getBankData()[0];
        int[] destPixels = dstDB.getBankData()[0];
        int[] buffer = new int[boxSz];
        int scale = 0x1000000 / boxSz;
        for (int y = skipY; y < h - skipY; ++y) {
            int curr;
            int sp = srcOff + y * srcScanStride;
            int dp = dstOff + y * dstScanStride;
            int rowEnd = sp + (w - skipX);
            int k = 0;
            int sumA = 0;
            int sumR = 0;
            int sumG = 0;
            int sumB = 0;
            int end = (sp += skipX) + boxSz;
            while (sp < end) {
                curr = buffer[k] = srcPixels[sp];
                sumA += curr >>> 24;
                sumR += curr >> 16 & 0xFF;
                sumG += curr >> 8 & 0xFF;
                sumB += curr & 0xFF;
                ++k;
                ++sp;
            }
            int prev = destPixels[dp += skipX + loc] = sumA * scale & 0xFF000000 | (sumR * scale & 0xFF000000) >>> 8 | (sumG * scale & 0xFF000000) >>> 16 | (sumB * scale & 0xFF000000) >>> 24;
            ++dp;
            k = 0;
            while (sp < rowEnd) {
                curr = buffer[k];
                if (curr == srcPixels[sp]) {
                    destPixels[dp] = prev;
                } else {
                    sumA -= curr >>> 24;
                    sumR -= curr >> 16 & 0xFF;
                    sumG -= curr >> 8 & 0xFF;
                    sumB -= curr & 0xFF;
                    curr = buffer[k] = srcPixels[sp];
                    prev = destPixels[dp] = (sumA += curr >>> 24) * scale & 0xFF000000 | ((sumR += curr >> 16 & 0xFF) * scale & 0xFF000000) >>> 8 | ((sumG += curr >> 8 & 0xFF) * scale & 0xFF000000) >>> 16 | ((sumB += curr & 0xFF) * scale & 0xFF000000) >>> 24;
                }
                k = (k + 1) % boxSz;
                ++sp;
                ++dp;
            }
        }
        return dest;
    }

    private WritableRaster boxFilterV(Raster src, WritableRaster dest, int skipX, int skipY, int boxSz, int loc) {
        int w = src.getWidth();
        int h = src.getHeight();
        if (w < 2 * skipX) {
            return dest;
        }
        if (h < 2 * skipY + boxSz) {
            return dest;
        }
        SinglePixelPackedSampleModel srcSPPSM = (SinglePixelPackedSampleModel)src.getSampleModel();
        SinglePixelPackedSampleModel dstSPPSM = (SinglePixelPackedSampleModel)dest.getSampleModel();
        int srcScanStride = srcSPPSM.getScanlineStride();
        int dstScanStride = dstSPPSM.getScanlineStride();
        DataBufferInt srcDB = (DataBufferInt)src.getDataBuffer();
        DataBufferInt dstDB = (DataBufferInt)dest.getDataBuffer();
        int srcOff = srcDB.getOffset() + srcSPPSM.getOffset(src.getMinX() - src.getSampleModelTranslateX(), src.getMinY() - src.getSampleModelTranslateY());
        int dstOff = dstDB.getOffset() + dstSPPSM.getOffset(dest.getMinX() - dest.getSampleModelTranslateX(), dest.getMinY() - dest.getSampleModelTranslateY());
        int[] srcPixels = srcDB.getBankData()[0];
        int[] destPixels = dstDB.getBankData()[0];
        int[] buffer = new int[boxSz];
        int scale = 0x1000000 / boxSz;
        for (int x = skipX; x < w - skipX; ++x) {
            int curr;
            int sp = srcOff + x;
            int dp = dstOff + x;
            int colEnd = sp + (h - skipY) * srcScanStride;
            int k = 0;
            int sumA = 0;
            int sumR = 0;
            int sumG = 0;
            int sumB = 0;
            int end = (sp += skipY * srcScanStride) + boxSz * srcScanStride;
            while (sp < end) {
                curr = buffer[k] = srcPixels[sp];
                sumA += curr >>> 24;
                sumR += curr >> 16 & 0xFF;
                sumG += curr >> 8 & 0xFF;
                sumB += curr & 0xFF;
                ++k;
                sp += srcScanStride;
            }
            int prev = destPixels[dp += (skipY + loc) * dstScanStride] = sumA * scale & 0xFF000000 | (sumR * scale & 0xFF000000) >>> 8 | (sumG * scale & 0xFF000000) >>> 16 | (sumB * scale & 0xFF000000) >>> 24;
            dp += dstScanStride;
            k = 0;
            while (sp < colEnd) {
                curr = buffer[k];
                if (curr == srcPixels[sp]) {
                    destPixels[dp] = prev;
                } else {
                    sumA -= curr >>> 24;
                    sumR -= curr >> 16 & 0xFF;
                    sumG -= curr >> 8 & 0xFF;
                    sumB -= curr & 0xFF;
                    curr = buffer[k] = srcPixels[sp];
                    prev = destPixels[dp] = (sumA += curr >>> 24) * scale & 0xFF000000 | ((sumR += curr >> 16 & 0xFF) * scale & 0xFF000000) >>> 8 | ((sumG += curr >> 8 & 0xFF) * scale & 0xFF000000) >>> 16 | ((sumB += curr & 0xFF) * scale & 0xFF000000) >>> 24;
                }
                k = (k + 1) % boxSz;
                sp += srcScanStride;
                dp += dstScanStride;
            }
        }
        return dest;
    }

    protected static ColorModel fixColorModel(CachableRed src) {
        ColorModel cm = src.getColorModel();
        int b = src.getSampleModel().getNumBands();
        int[] masks = new int[4];
        switch (b) {
            case 1: {
                masks[0] = 255;
                break;
            }
            case 2: {
                masks[0] = 255;
                masks[3] = 65280;
                break;
            }
            case 3: {
                masks[0] = 0xFF0000;
                masks[1] = 65280;
                masks[2] = 255;
                break;
            }
            case 4: {
                masks[0] = 0xFF0000;
                masks[1] = 65280;
                masks[2] = 255;
                masks[3] = -16777216;
                break;
            }
            default: {
                throw new IllegalArgumentException("GaussianBlurRed8Bit only supports one to four band images");
            }
        }
        ColorSpace cs = cm.getColorSpace();
        return new DirectColorModel(cs, 8 * b, masks[0], masks[1], masks[2], masks[3], true, 3);
    }
}

