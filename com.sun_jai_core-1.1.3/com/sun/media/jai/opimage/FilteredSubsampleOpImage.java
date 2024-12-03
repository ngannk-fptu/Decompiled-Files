/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.JaiI18N;
import com.sun.media.jai.util.ImageUtil;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.BorderExtender;
import javax.media.jai.GeometricOpImage;
import javax.media.jai.ImageLayout;
import javax.media.jai.Interpolation;
import javax.media.jai.InterpolationBicubic;
import javax.media.jai.InterpolationBicubic2;
import javax.media.jai.InterpolationBilinear;
import javax.media.jai.InterpolationNearest;
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFormatTag;

public class FilteredSubsampleOpImage
extends GeometricOpImage {
    protected int scaleX;
    protected int scaleY;
    protected int hParity;
    protected int vParity;
    protected float[] hKernel;
    protected float[] vKernel;

    private static float[] convolveFullKernels(float[] a, float[] b) {
        int lenA = a.length;
        int lenB = b.length;
        float[] c = new float[lenA + lenB - 1];
        for (int k = 0; k < c.length; ++k) {
            for (int j = Math.max(0, k - lenB + 1); j <= Math.min(k, lenA - 1); ++j) {
                int n = k;
                c[n] = c[n] + a[j] * b[k - j];
            }
        }
        return c;
    }

    private static float[] convolveSymmetricKernels(int aParity, int bParity, float[] a, float[] b) {
        int k;
        int lenA = a.length;
        int lenB = b.length;
        int lenTmpA = 2 * lenA - aParity;
        int lenTmpB = 2 * lenB - bParity;
        int lenTmpC = lenTmpA + lenTmpB - 1;
        float[] tmpA = new float[lenTmpA];
        float[] tmpB = new float[lenTmpB];
        float[] c = new float[(lenTmpC + 1) / 2];
        for (k = 0; k < lenTmpA; ++k) {
            tmpA[k] = a[Math.abs(k - lenA + (aParity - 1) * (k / lenA) + 1)];
        }
        for (k = 0; k < lenTmpB; ++k) {
            tmpB[k] = b[Math.abs(k - lenB + (bParity - 1) * (k / lenB) + 1)];
        }
        float[] tmpC = FilteredSubsampleOpImage.convolveFullKernels(tmpA, tmpB);
        int cParity = tmpC.length % 2;
        for (int k2 = 0; k2 < c.length; ++k2) {
            c[k2] = tmpC[lenTmpC - c.length - k2 - 1 + cParity];
        }
        return c;
    }

    private static float[] combineFilters(int scaleFactor, int resampleType, float[] qsFilter) {
        if (scaleFactor % 2 == 1) {
            return (float[])qsFilter.clone();
        }
        int qsParity = 1;
        int resampParity = 0;
        switch (resampleType) {
            case 0: {
                return (float[])qsFilter.clone();
            }
            case 1: {
                float[] bilinearKernel = new float[]{0.5f};
                return FilteredSubsampleOpImage.convolveSymmetricKernels(qsParity, resampParity, qsFilter, bilinearKernel);
            }
            case 2: {
                float[] bicubicKernel = new float[]{0.5625f, -0.0625f};
                return FilteredSubsampleOpImage.convolveSymmetricKernels(qsParity, resampParity, qsFilter, bicubicKernel);
            }
            case 3: {
                float[] bicubic2Kernel = new float[]{0.625f, -0.125f};
                return FilteredSubsampleOpImage.convolveSymmetricKernels(qsParity, resampParity, qsFilter, bicubic2Kernel);
            }
        }
        throw new IllegalArgumentException(JaiI18N.getString("FilteredSubsample0"));
    }

    private static int filterParity(int scaleFactor, int resampleType) {
        if (scaleFactor % 2 == 1 || resampleType == 0) {
            return 1;
        }
        return 0;
    }

    private static final ImageLayout layoutHelper(RenderedImage source, Interpolation interp, int scaleX, int scaleY, int filterSize, ImageLayout il) {
        ImageLayout layout;
        if (scaleX < 1 || scaleY < 1) {
            throw new IllegalArgumentException(JaiI18N.getString("FilteredSubsample1"));
        }
        if (filterSize < 1) {
            throw new IllegalArgumentException(JaiI18N.getString("FilteredSubsample2"));
        }
        Rectangle bounds = FilteredSubsampleOpImage.forwardMapRect(source.getMinX(), source.getMinY(), source.getWidth(), source.getHeight(), scaleX, scaleY);
        ImageLayout imageLayout = layout = il == null ? new ImageLayout(bounds.x, bounds.y, bounds.width, bounds.height) : (ImageLayout)il.clone();
        if (il != null) {
            layout.setWidth(bounds.width);
            layout.setHeight(bounds.height);
            layout.setMinX(bounds.x);
            layout.setMinY(bounds.y);
        }
        return layout;
    }

    /*
     * WARNING - void declaration
     */
    public FilteredSubsampleOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, int scaleX, int scaleY, float[] qsFilter, Interpolation interp) {
        super(FilteredSubsampleOpImage.vectorize(source), FilteredSubsampleOpImage.layoutHelper(source, interp, scaleX, scaleY, qsFilter.length, layout), config, true, extender, interp, null);
        void var9_9;
        int resampleType;
        if (interp instanceof InterpolationNearest) {
            resampleType = 0;
        } else if (interp instanceof InterpolationBilinear) {
            resampleType = 1;
        } else if (interp instanceof InterpolationBicubic) {
            resampleType = 2;
        } else if (interp instanceof InterpolationBicubic2) {
            resampleType = 3;
        } else {
            throw new IllegalArgumentException(JaiI18N.getString("FilteredSubsample3"));
        }
        this.hParity = FilteredSubsampleOpImage.filterParity(scaleX, (int)var9_9);
        this.vParity = FilteredSubsampleOpImage.filterParity(scaleY, (int)var9_9);
        this.hKernel = FilteredSubsampleOpImage.combineFilters(scaleX, (int)var9_9, qsFilter);
        this.vKernel = FilteredSubsampleOpImage.combineFilters(scaleY, (int)var9_9, qsFilter);
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    public Point2D mapDestPoint(Point2D destPt) {
        if (destPt == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        Point2D pt = (Point2D)destPt.clone();
        pt.setLocation(destPt.getX() * (double)this.scaleX, destPt.getY() * (double)this.scaleY);
        return pt;
    }

    public Point2D mapSourcePoint(Point2D sourcePt) {
        if (sourcePt == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        Point2D pt = (Point2D)sourcePt.clone();
        pt.setLocation(sourcePt.getX() / (double)this.scaleX, sourcePt.getY() / (double)this.scaleY);
        return pt;
    }

    public Rectangle mapSourceRect(Rectangle sourceRect, int sourceIndex) {
        if (sourceIndex != 0) {
            throw new IllegalArgumentException(JaiI18N.getString("FilteredSubsample4"));
        }
        int xOffset = sourceRect.x + this.hKernel.length - this.hParity - this.scaleX / 2;
        int yOffset = sourceRect.y + this.vKernel.length - this.vParity - this.scaleY / 2;
        int rectWidth = sourceRect.width - 2 * this.hKernel.length + this.hParity + 1;
        int rectHeight = sourceRect.height - 2 * this.vKernel.length + this.vParity + 1;
        return FilteredSubsampleOpImage.forwardMapRect(xOffset, yOffset, rectWidth, rectHeight, this.scaleX, this.scaleY);
    }

    private static final Rectangle forwardMapRect(int x, int y, int w, int h, int scaleX, int scaleY) {
        float sx = 1.0f / (float)scaleX;
        float sy = 1.0f / (float)scaleY;
        x = Math.round((float)x * sx);
        y = Math.round((float)y * sy);
        return new Rectangle(x, y, Math.round((float)(x + w) * sx) - x, Math.round((float)(y + h) * sy) - y);
    }

    protected final Rectangle forwardMapRect(Rectangle srcRect, int srcIndex) {
        int x = srcRect.x;
        int y = srcRect.y;
        int w = srcRect.width;
        int h = srcRect.height;
        float sx = 1.0f / (float)this.scaleX;
        float sy = 1.0f / (float)this.scaleY;
        x = Math.round((float)x * sx);
        y = Math.round((float)y * sy);
        return new Rectangle(x, y, Math.round((float)(x + w) * sx) - x, Math.round((float)(y + h) * sy) - y);
    }

    protected final Rectangle backwardMapRect(Rectangle destRect, int srcIncex) {
        int x = destRect.x;
        int y = destRect.y;
        int w = destRect.width;
        int h = destRect.height;
        return new Rectangle(x * this.scaleX, y * this.scaleY, (x + w) * this.scaleX - x, (y + h) * this.scaleY - y);
    }

    public Rectangle mapDestRect(Rectangle destRect, int sourceIndex) {
        if (sourceIndex != 0) {
            throw new IllegalArgumentException(JaiI18N.getString("FilteredSubsample4"));
        }
        int xOffset = destRect.x * this.scaleX - this.hKernel.length + this.hParity + this.scaleX / 2;
        int yOffset = destRect.y * this.scaleY - this.vKernel.length + this.vParity + this.scaleY / 2;
        int rectWidth = destRect.width * this.scaleX + 2 * this.hKernel.length - this.hParity - 1;
        int rectHeight = destRect.height * this.scaleY + 2 * this.vKernel.length - this.vParity - 1;
        return new Rectangle(xOffset, yOffset, rectWidth, rectHeight);
    }

    public void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        RasterFormatTag[] formatTags = this.getFormatTags();
        RasterAccessor dst = new RasterAccessor(dest, destRect, formatTags[1], this.getColorModel());
        RasterAccessor src = new RasterAccessor(sources[0], this.mapDestRect(destRect, 0), formatTags[0], this.getSourceImage(0).getColorModel());
        switch (dst.getDataType()) {
            case 0: {
                this.computeRectByte(src, dst);
                break;
            }
            case 1: {
                this.computeRectUShort(src, dst);
                break;
            }
            case 2: {
                this.computeRectShort(src, dst);
                break;
            }
            case 3: {
                this.computeRectInt(src, dst);
                break;
            }
            case 4: {
                this.computeRectFloat(src, dst);
                break;
            }
            case 5: {
                this.computeRectDouble(src, dst);
                break;
            }
            default: {
                throw new IllegalArgumentException(JaiI18N.getString("FilteredSubsample5"));
            }
        }
        if (dst.isDataCopy()) {
            dst.clampDataArrays();
            dst.copyDataToRaster();
        }
    }

    protected void computeRectByte(RasterAccessor src, RasterAccessor dst) {
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int dnumBands = dst.getNumBands();
        byte[][] dstDataArrays = dst.getByteDataArrays();
        int[] dstBandOffsets = dst.getBandOffsets();
        int dstPixelStride = dst.getPixelStride();
        int dstScanlineStride = dst.getScanlineStride();
        byte[][] srcDataArrays = src.getByteDataArrays();
        int[] srcBandOffsets = src.getBandOffsets();
        int srcPixelStride = src.getPixelStride();
        int srcScanlineStride = src.getScanlineStride();
        int kernelNx = 2 * this.hKernel.length - this.hParity;
        int kernelNy = 2 * this.vKernel.length - this.vParity;
        int stepDown = (kernelNy - 1) * srcScanlineStride;
        int stepRight = (kernelNx - 1) * srcPixelStride;
        float vCtr = this.vKernel[0];
        float hCtr = this.hKernel[0];
        for (int band = 0; band < dnumBands; ++band) {
            byte[] dstData = dstDataArrays[band];
            byte[] srcData = srcDataArrays[band];
            int srcScanlineOffset = srcBandOffsets[band];
            int dstScanlineOffset = dstBandOffsets[band];
            for (int ySrc = 0; ySrc < this.scaleY * dheight; ySrc += this.scaleY) {
                int dInd = dstScanlineOffset;
                for (int xSrc = 0; xSrc < this.scaleX * dwidth; xSrc += this.scaleX) {
                    int kInd;
                    float kk;
                    int upLeft0 = xSrc * srcPixelStride + ySrc * srcScanlineStride + srcScanlineOffset;
                    int upRight0 = upLeft0 + stepRight;
                    int dnLeft0 = upLeft0 + stepDown;
                    int dnRight0 = upRight0 + stepDown;
                    float sum = 0.0f;
                    for (int iy = this.vKernel.length - 1; iy > this.vParity - 1; --iy) {
                        int upLeft = upLeft0;
                        int upRight = upRight0;
                        int dnLeft = dnLeft0;
                        int dnRight = dnRight0;
                        for (int ix = this.hKernel.length - 1; ix > this.hParity - 1; --ix) {
                            kk = this.hKernel[ix] * this.vKernel[iy];
                            sum += kk * (float)((srcData[upLeft] & 0xFF) + (srcData[upRight] & 0xFF) + (srcData[dnLeft] & 0xFF) + (srcData[dnRight] & 0xFF));
                            upLeft += srcPixelStride;
                            upRight -= srcPixelStride;
                            dnLeft += srcPixelStride;
                            dnRight -= srcPixelStride;
                        }
                        upLeft0 += srcScanlineStride;
                        upRight0 += srcScanlineStride;
                        dnLeft0 -= srcScanlineStride;
                        dnRight0 -= srcScanlineStride;
                    }
                    if (this.hParity == 1) {
                        int xUp = (xSrc + this.hKernel.length - 1) * srcPixelStride + ySrc * srcScanlineStride + srcScanlineOffset;
                        kInd = this.vKernel.length - 1;
                        for (int xDown = xUp + stepDown; xUp < xDown; xUp += srcScanlineStride, xDown -= srcScanlineStride) {
                            kk = hCtr * this.vKernel[kInd--];
                            sum += kk * (float)((srcData[xUp] & 0xFF) + (srcData[xDown] & 0xFF));
                        }
                    }
                    if (this.vParity == 1) {
                        int xLeft = xSrc * srcPixelStride + (ySrc + this.vKernel.length - 1) * srcScanlineStride + srcScanlineOffset;
                        kInd = this.hKernel.length - 1;
                        for (int xRight = xLeft + stepRight; xLeft < xRight; xLeft += srcPixelStride, xRight -= srcPixelStride) {
                            kk = vCtr * this.hKernel[kInd--];
                            sum += kk * (float)((srcData[xLeft] & 0xFF) + (srcData[xRight] & 0xFF));
                        }
                        if (this.hParity == 1) {
                            sum += vCtr * hCtr * (float)(srcData[xLeft] & 0xFF);
                        }
                    }
                    if ((double)sum < 0.0) {
                        sum = 0.0f;
                    }
                    if ((double)sum > 255.0) {
                        sum = 255.0f;
                    }
                    dstData[dInd] = (byte)((double)sum + 0.5);
                    dInd += dstPixelStride;
                }
                dstScanlineOffset += dstScanlineStride;
            }
        }
    }

    protected void computeRectUShort(RasterAccessor src, RasterAccessor dst) {
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int dnumBands = dst.getNumBands();
        short[][] dstDataArrays = dst.getShortDataArrays();
        int[] dstBandOffsets = dst.getBandOffsets();
        int dstPixelStride = dst.getPixelStride();
        int dstScanlineStride = dst.getScanlineStride();
        short[][] srcDataArrays = src.getShortDataArrays();
        int[] srcBandOffsets = src.getBandOffsets();
        int srcPixelStride = src.getPixelStride();
        int srcScanlineStride = src.getScanlineStride();
        int kernelNx = 2 * this.hKernel.length - this.hParity;
        int kernelNy = 2 * this.vKernel.length - this.vParity;
        int stepDown = (kernelNy - 1) * srcScanlineStride;
        int stepRight = (kernelNx - 1) * srcPixelStride;
        float vCtr = this.vKernel[0];
        float hCtr = this.hKernel[0];
        for (int band = 0; band < dnumBands; ++band) {
            short[] dstData = dstDataArrays[band];
            short[] srcData = srcDataArrays[band];
            int srcScanlineOffset = srcBandOffsets[band];
            int dstScanlineOffset = dstBandOffsets[band];
            for (int ySrc = 0; ySrc < this.scaleY * dheight; ySrc += this.scaleY) {
                int dInd = dstScanlineOffset;
                for (int xSrc = 0; xSrc < this.scaleX * dwidth; xSrc += this.scaleX) {
                    int val;
                    int kInd;
                    float kk;
                    int upLeft0 = xSrc * srcPixelStride + ySrc * srcScanlineStride + srcScanlineOffset;
                    int upRight0 = upLeft0 + stepRight;
                    int dnLeft0 = upLeft0 + stepDown;
                    int dnRight0 = upRight0 + stepDown;
                    float sum = 0.0f;
                    for (int iy = this.vKernel.length - 1; iy > this.vParity - 1; --iy) {
                        int upLeft = upLeft0;
                        int upRight = upRight0;
                        int dnLeft = dnLeft0;
                        int dnRight = dnRight0;
                        for (int ix = this.hKernel.length - 1; ix > this.hParity - 1; --ix) {
                            kk = this.hKernel[ix] * this.vKernel[iy];
                            sum += kk * (float)((srcData[upLeft] & 0xFFFF) + (srcData[upRight] & 0xFFFF) + (srcData[dnLeft] & 0xFFFF) + (srcData[dnRight] & 0xFFFF));
                            upLeft += srcPixelStride;
                            upRight -= srcPixelStride;
                            dnLeft += srcPixelStride;
                            dnRight -= srcPixelStride;
                        }
                        upLeft0 += srcScanlineStride;
                        upRight0 += srcScanlineStride;
                        dnLeft0 -= srcScanlineStride;
                        dnRight0 -= srcScanlineStride;
                    }
                    if (this.hParity == 1) {
                        int xUp = (xSrc + this.hKernel.length - 1) * srcPixelStride + ySrc * srcScanlineStride + srcScanlineOffset;
                        kInd = this.vKernel.length - 1;
                        for (int xDown = xUp + stepDown; xUp < xDown; xUp += srcScanlineStride, xDown -= srcScanlineStride) {
                            kk = hCtr * this.vKernel[kInd--];
                            sum += kk * (float)((srcData[xUp] & 0xFFFF) + (srcData[xDown] & 0xFFFF));
                        }
                    }
                    if (this.vParity == 1) {
                        int xLeft = xSrc * srcPixelStride + (ySrc + this.vKernel.length - 1) * srcScanlineStride + srcScanlineOffset;
                        kInd = this.hKernel.length - 1;
                        for (int xRight = xLeft + stepRight; xLeft < xRight; xLeft += srcPixelStride, xRight -= srcPixelStride) {
                            kk = vCtr * this.hKernel[kInd--];
                            sum += kk * (float)((srcData[xLeft] & 0xFFFF) + (srcData[xRight] & 0xFFFF));
                        }
                        if (this.hParity == 1) {
                            sum += vCtr * hCtr * (float)(srcData[xLeft] & 0xFFFF);
                        }
                    }
                    dstData[dInd] = (short)((val = (int)((double)sum + 0.5)) > 65535 ? 65535 : (val < 0 ? 0 : val));
                    dInd += dstPixelStride;
                }
                dstScanlineOffset += dstScanlineStride;
            }
        }
    }

    protected void computeRectShort(RasterAccessor src, RasterAccessor dst) {
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int dnumBands = dst.getNumBands();
        short[][] dstDataArrays = dst.getShortDataArrays();
        int[] dstBandOffsets = dst.getBandOffsets();
        int dstPixelStride = dst.getPixelStride();
        int dstScanlineStride = dst.getScanlineStride();
        short[][] srcDataArrays = src.getShortDataArrays();
        int[] srcBandOffsets = src.getBandOffsets();
        int srcPixelStride = src.getPixelStride();
        int srcScanlineStride = src.getScanlineStride();
        int kernelNx = 2 * this.hKernel.length - this.hParity;
        int kernelNy = 2 * this.vKernel.length - this.vParity;
        int stepDown = (kernelNy - 1) * srcScanlineStride;
        int stepRight = (kernelNx - 1) * srcPixelStride;
        float vCtr = this.vKernel[0];
        float hCtr = this.hKernel[0];
        for (int band = 0; band < dnumBands; ++band) {
            short[] dstData = dstDataArrays[band];
            short[] srcData = srcDataArrays[band];
            int srcScanlineOffset = srcBandOffsets[band];
            int dstScanlineOffset = dstBandOffsets[band];
            for (int ySrc = 0; ySrc < this.scaleY * dheight; ySrc += this.scaleY) {
                int dInd = dstScanlineOffset;
                for (int xSrc = 0; xSrc < this.scaleX * dwidth; xSrc += this.scaleX) {
                    int kInd;
                    float kk;
                    int upLeft0 = xSrc * srcPixelStride + ySrc * srcScanlineStride + srcScanlineOffset;
                    int upRight0 = upLeft0 + stepRight;
                    int dnLeft0 = upLeft0 + stepDown;
                    int dnRight0 = upRight0 + stepDown;
                    float sum = 0.0f;
                    for (int iy = this.vKernel.length - 1; iy > this.vParity - 1; --iy) {
                        int upLeft = upLeft0;
                        int upRight = upRight0;
                        int dnLeft = dnLeft0;
                        int dnRight = dnRight0;
                        for (int ix = this.hKernel.length - 1; ix > this.hParity - 1; --ix) {
                            kk = this.hKernel[ix] * this.vKernel[iy];
                            sum += kk * (float)(srcData[upLeft] + srcData[upRight] + srcData[dnLeft] + srcData[dnRight]);
                            upLeft += srcPixelStride;
                            upRight -= srcPixelStride;
                            dnLeft += srcPixelStride;
                            dnRight -= srcPixelStride;
                        }
                        upLeft0 += srcScanlineStride;
                        upRight0 += srcScanlineStride;
                        dnLeft0 -= srcScanlineStride;
                        dnRight0 -= srcScanlineStride;
                    }
                    if (this.hParity == 1) {
                        int xUp = (xSrc + this.hKernel.length - 1) * srcPixelStride + ySrc * srcScanlineStride + srcScanlineOffset;
                        kInd = this.vKernel.length - 1;
                        for (int xDown = xUp + stepDown; xUp < xDown; xUp += srcScanlineStride, xDown -= srcScanlineStride) {
                            kk = hCtr * this.vKernel[kInd--];
                            sum += kk * (float)(srcData[xUp] + srcData[xDown]);
                        }
                    }
                    if (this.vParity == 1) {
                        int xLeft = xSrc * srcPixelStride + (ySrc + this.vKernel.length - 1) * srcScanlineStride + srcScanlineOffset;
                        kInd = this.hKernel.length - 1;
                        for (int xRight = xLeft + stepRight; xLeft < xRight; xLeft += srcPixelStride, xRight -= srcPixelStride) {
                            kk = vCtr * this.hKernel[kInd--];
                            sum += kk * (float)(srcData[xLeft] + srcData[xRight]);
                        }
                        if (this.hParity == 1) {
                            sum += vCtr * hCtr * (float)srcData[xLeft];
                        }
                    }
                    dstData[dInd] = ImageUtil.clampShort((int)((double)sum + 0.5));
                    dInd += dstPixelStride;
                }
                dstScanlineOffset += dstScanlineStride;
            }
        }
    }

    protected void computeRectInt(RasterAccessor src, RasterAccessor dst) {
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int dnumBands = dst.getNumBands();
        int[][] dstDataArrays = dst.getIntDataArrays();
        int[] dstBandOffsets = dst.getBandOffsets();
        int dstPixelStride = dst.getPixelStride();
        int dstScanlineStride = dst.getScanlineStride();
        int[][] srcDataArrays = src.getIntDataArrays();
        int[] srcBandOffsets = src.getBandOffsets();
        int srcPixelStride = src.getPixelStride();
        int srcScanlineStride = src.getScanlineStride();
        int kernelNx = 2 * this.hKernel.length - this.hParity;
        int kernelNy = 2 * this.vKernel.length - this.vParity;
        int stepDown = (kernelNy - 1) * srcScanlineStride;
        int stepRight = (kernelNx - 1) * srcPixelStride;
        double vCtr = this.vKernel[0];
        double hCtr = this.hKernel[0];
        for (int band = 0; band < dnumBands; ++band) {
            int[] dstData = dstDataArrays[band];
            int[] srcData = srcDataArrays[band];
            int srcScanlineOffset = srcBandOffsets[band];
            int dstScanlineOffset = dstBandOffsets[band];
            for (int ySrc = 0; ySrc < this.scaleY * dheight; ySrc += this.scaleY) {
                int dInd = dstScanlineOffset;
                for (int xSrc = 0; xSrc < this.scaleX * dwidth; xSrc += this.scaleX) {
                    int kInd;
                    double kk;
                    int upLeft0 = xSrc * srcPixelStride + ySrc * srcScanlineStride + srcScanlineOffset;
                    int upRight0 = upLeft0 + stepRight;
                    int dnLeft0 = upLeft0 + stepDown;
                    int dnRight0 = upRight0 + stepDown;
                    double sum = 0.0;
                    for (int iy = this.vKernel.length - 1; iy > this.vParity - 1; --iy) {
                        int upLeft = upLeft0;
                        int upRight = upRight0;
                        int dnLeft = dnLeft0;
                        int dnRight = dnRight0;
                        for (int ix = this.hKernel.length - 1; ix > this.hParity - 1; --ix) {
                            kk = this.hKernel[ix] * this.vKernel[iy];
                            sum += kk * (double)((long)srcData[upLeft] + (long)srcData[upRight] + (long)srcData[dnLeft] + (long)srcData[dnRight]);
                            upLeft += srcPixelStride;
                            upRight -= srcPixelStride;
                            dnLeft += srcPixelStride;
                            dnRight -= srcPixelStride;
                        }
                        upLeft0 += srcScanlineStride;
                        upRight0 += srcScanlineStride;
                        dnLeft0 -= srcScanlineStride;
                        dnRight0 -= srcScanlineStride;
                    }
                    if (this.hParity == 1) {
                        int xUp = (xSrc + this.hKernel.length - 1) * srcPixelStride + ySrc * srcScanlineStride + srcScanlineOffset;
                        kInd = this.vKernel.length - 1;
                        for (int xDown = xUp + stepDown; xUp < xDown; xUp += srcScanlineStride, xDown -= srcScanlineStride) {
                            kk = hCtr * (double)this.vKernel[kInd--];
                            sum += kk * (double)((long)srcData[xUp] + (long)srcData[xDown]);
                        }
                    }
                    if (this.vParity == 1) {
                        int xLeft = xSrc * srcPixelStride + (ySrc + this.vKernel.length - 1) * srcScanlineStride + srcScanlineOffset;
                        kInd = this.hKernel.length - 1;
                        for (int xRight = xLeft + stepRight; xLeft < xRight; xLeft += srcPixelStride, xRight -= srcPixelStride) {
                            kk = vCtr * (double)this.hKernel[kInd--];
                            sum += kk * (double)((long)srcData[xLeft] + (long)srcData[xRight]);
                        }
                        if (this.hParity == 1) {
                            sum += vCtr * hCtr * (double)srcData[xLeft];
                        }
                    }
                    dstData[dInd] = ImageUtil.clampInt((int)(sum + 0.5));
                    dInd += dstPixelStride;
                }
                dstScanlineOffset += dstScanlineStride;
            }
        }
    }

    protected void computeRectFloat(RasterAccessor src, RasterAccessor dst) {
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int dnumBands = dst.getNumBands();
        float[][] dstDataArrays = dst.getFloatDataArrays();
        int[] dstBandOffsets = dst.getBandOffsets();
        int dstPixelStride = dst.getPixelStride();
        int dstScanlineStride = dst.getScanlineStride();
        float[][] srcDataArrays = src.getFloatDataArrays();
        int[] srcBandOffsets = src.getBandOffsets();
        int srcPixelStride = src.getPixelStride();
        int srcScanlineStride = src.getScanlineStride();
        int kernelNx = 2 * this.hKernel.length - this.hParity;
        int kernelNy = 2 * this.vKernel.length - this.vParity;
        int stepDown = (kernelNy - 1) * srcScanlineStride;
        int stepRight = (kernelNx - 1) * srcPixelStride;
        double vCtr = this.vKernel[0];
        double hCtr = this.hKernel[0];
        for (int band = 0; band < dnumBands; ++band) {
            float[] dstData = dstDataArrays[band];
            float[] srcData = srcDataArrays[band];
            int srcScanlineOffset = srcBandOffsets[band];
            int dstScanlineOffset = dstBandOffsets[band];
            for (int ySrc = 0; ySrc < this.scaleY * dheight; ySrc += this.scaleY) {
                int dInd = dstScanlineOffset;
                for (int xSrc = 0; xSrc < this.scaleX * dwidth; xSrc += this.scaleX) {
                    int kInd;
                    double kk;
                    int upLeft0 = xSrc * srcPixelStride + ySrc * srcScanlineStride + srcScanlineOffset;
                    int upRight0 = upLeft0 + stepRight;
                    int dnLeft0 = upLeft0 + stepDown;
                    int dnRight0 = upRight0 + stepDown;
                    double sum = 0.0;
                    for (int iy = this.vKernel.length - 1; iy > this.vParity - 1; --iy) {
                        int upLeft = upLeft0;
                        int upRight = upRight0;
                        int dnLeft = dnLeft0;
                        int dnRight = dnRight0;
                        for (int ix = this.hKernel.length - 1; ix > this.hParity - 1; --ix) {
                            kk = this.hKernel[ix] * this.vKernel[iy];
                            sum += kk * ((double)srcData[upLeft] + (double)srcData[upRight] + (double)srcData[dnLeft] + (double)srcData[dnRight]);
                            upLeft += srcPixelStride;
                            upRight -= srcPixelStride;
                            dnLeft += srcPixelStride;
                            dnRight -= srcPixelStride;
                        }
                        upLeft0 += srcScanlineStride;
                        upRight0 += srcScanlineStride;
                        dnLeft0 -= srcScanlineStride;
                        dnRight0 -= srcScanlineStride;
                    }
                    if (this.hParity == 1) {
                        int xUp = (xSrc + this.hKernel.length - 1) * srcPixelStride + ySrc * srcScanlineStride + srcScanlineOffset;
                        kInd = this.vKernel.length - 1;
                        for (int xDown = xUp + stepDown; xUp < xDown; xUp += srcScanlineStride, xDown -= srcScanlineStride) {
                            kk = hCtr * (double)this.vKernel[kInd--];
                            sum += kk * ((double)srcData[xUp] + (double)srcData[xDown]);
                        }
                    }
                    if (this.vParity == 1) {
                        int xLeft = xSrc * srcPixelStride + (ySrc + this.vKernel.length - 1) * srcScanlineStride + srcScanlineOffset;
                        kInd = this.hKernel.length - 1;
                        for (int xRight = xLeft + stepRight; xLeft < xRight; xLeft += srcPixelStride, xRight -= srcPixelStride) {
                            kk = vCtr * (double)this.hKernel[kInd--];
                            sum += kk * ((double)srcData[xLeft] + (double)srcData[xRight]);
                        }
                        if (this.hParity == 1) {
                            sum += vCtr * hCtr * (double)srcData[xLeft];
                        }
                    }
                    dstData[dInd] = ImageUtil.clampFloat(sum);
                    dInd += dstPixelStride;
                }
                dstScanlineOffset += dstScanlineStride;
            }
        }
    }

    protected void computeRectDouble(RasterAccessor src, RasterAccessor dst) {
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int dnumBands = dst.getNumBands();
        double[][] dstDataArrays = dst.getDoubleDataArrays();
        int[] dstBandOffsets = dst.getBandOffsets();
        int dstPixelStride = dst.getPixelStride();
        int dstScanlineStride = dst.getScanlineStride();
        double[][] srcDataArrays = src.getDoubleDataArrays();
        int[] srcBandOffsets = src.getBandOffsets();
        int srcPixelStride = src.getPixelStride();
        int srcScanlineStride = src.getScanlineStride();
        int kernelNx = 2 * this.hKernel.length - this.hParity;
        int kernelNy = 2 * this.vKernel.length - this.vParity;
        int stepDown = (kernelNy - 1) * srcScanlineStride;
        int stepRight = (kernelNx - 1) * srcPixelStride;
        double vCtr = this.vKernel[0];
        double hCtr = this.hKernel[0];
        for (int band = 0; band < dnumBands; ++band) {
            double[] dstData = dstDataArrays[band];
            double[] srcData = srcDataArrays[band];
            int srcScanlineOffset = srcBandOffsets[band];
            int dstScanlineOffset = dstBandOffsets[band];
            for (int ySrc = 0; ySrc < this.scaleY * dheight; ySrc += this.scaleY) {
                int dInd = dstScanlineOffset;
                for (int xSrc = 0; xSrc < this.scaleX * dwidth; xSrc += this.scaleX) {
                    int kInd;
                    double kk;
                    int upLeft0 = xSrc * srcPixelStride + ySrc * srcScanlineStride + srcScanlineOffset;
                    int upRight0 = upLeft0 + stepRight;
                    int dnLeft0 = upLeft0 + stepDown;
                    int dnRight0 = upRight0 + stepDown;
                    double sum = 0.0;
                    for (int iy = this.vKernel.length - 1; iy > this.vParity - 1; --iy) {
                        int upLeft = upLeft0;
                        int upRight = upRight0;
                        int dnLeft = dnLeft0;
                        int dnRight = dnRight0;
                        for (int ix = this.hKernel.length - 1; ix > this.hParity - 1; --ix) {
                            kk = this.hKernel[ix] * this.vKernel[iy];
                            sum += kk * (srcData[upLeft] + srcData[upRight] + srcData[dnLeft] + srcData[dnRight]);
                            upLeft += srcPixelStride;
                            upRight -= srcPixelStride;
                            dnLeft += srcPixelStride;
                            dnRight -= srcPixelStride;
                        }
                        upLeft0 += srcScanlineStride;
                        upRight0 += srcScanlineStride;
                        dnLeft0 -= srcScanlineStride;
                        dnRight0 -= srcScanlineStride;
                    }
                    if (this.hParity == 1) {
                        int xUp = (xSrc + this.hKernel.length - 1) * srcPixelStride + ySrc * srcScanlineStride + srcScanlineOffset;
                        kInd = this.vKernel.length - 1;
                        for (int xDown = xUp + stepDown; xUp < xDown; xUp += srcScanlineStride, xDown -= srcScanlineStride) {
                            kk = hCtr * (double)this.vKernel[kInd--];
                            sum += kk * (srcData[xUp] + srcData[xDown]);
                        }
                    }
                    if (this.vParity == 1) {
                        int xLeft = xSrc * srcPixelStride + (ySrc + this.vKernel.length - 1) * srcScanlineStride + srcScanlineOffset;
                        kInd = this.hKernel.length - 1;
                        for (int xRight = xLeft + stepRight; xLeft < xRight; xLeft += srcPixelStride, xRight -= srcPixelStride) {
                            kk = vCtr * (double)this.hKernel[kInd--];
                            sum += kk * (srcData[xLeft] + srcData[xRight]);
                        }
                        if (this.hParity == 1) {
                            sum += vCtr * hCtr * srcData[xLeft];
                        }
                    }
                    dstData[dInd] = sum;
                    dInd += dstPixelStride;
                }
                dstScanlineOffset += dstScanlineStride;
            }
        }
    }
}

