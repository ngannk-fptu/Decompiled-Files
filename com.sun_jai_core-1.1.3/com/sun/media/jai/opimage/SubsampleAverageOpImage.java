/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.JaiI18N;
import com.sun.media.jai.util.ImageUtil;
import com.sun.media.jai.util.InterpAverage;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.GeometricOpImage;
import javax.media.jai.ImageLayout;
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFormatTag;

public class SubsampleAverageOpImage
extends GeometricOpImage {
    protected double scaleX;
    protected double scaleY;
    protected int blockX;
    protected int blockY;
    protected int sourceMinX;
    protected int sourceMinY;

    private static ImageLayout layoutHelper(RenderedImage source, double scaleX, double scaleY, ImageLayout il) {
        if (scaleX <= 0.0 || scaleX > 1.0) {
            throw new IllegalArgumentException(JaiI18N.getString("SubsampleAverageOpImage0"));
        }
        if (scaleY <= 0.0 || scaleY > 1.0) {
            throw new IllegalArgumentException(JaiI18N.getString("SubsampleAverageOpImage1"));
        }
        ImageLayout layout = il == null ? new ImageLayout() : (ImageLayout)il.clone();
        layout.setMinX((int)Math.floor((double)source.getMinX() * scaleX));
        layout.setMinY((int)Math.floor((double)source.getMinY() * scaleY));
        layout.setWidth((int)((double)source.getWidth() * scaleX));
        layout.setHeight((int)((double)source.getHeight() * scaleY));
        return layout;
    }

    public SubsampleAverageOpImage(RenderedImage source, ImageLayout layout, Map config, double scaleX, double scaleY) {
        super(SubsampleAverageOpImage.vectorize(source), SubsampleAverageOpImage.layoutHelper(source, scaleX, scaleY, layout), config, true, null, new InterpAverage((int)Math.ceil(1.0 / scaleX), (int)Math.ceil(1.0 / scaleY)), null);
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.blockX = (int)Math.ceil(1.0 / scaleX);
        this.blockY = (int)Math.ceil(1.0 / scaleY);
        this.sourceMinX = source.getMinX();
        this.sourceMinY = source.getMinY();
    }

    public Point2D mapDestPoint(Point2D destPt) {
        if (destPt == null) {
            throw new IllegalArgumentException("destPt == null!");
        }
        Point2D pt = (Point2D)destPt.clone();
        pt.setLocation((double)this.sourceMinX + (destPt.getX() + 0.5 - (double)this.minX) / this.scaleX - 0.5, (double)this.sourceMinY + (destPt.getY() + 0.5 - (double)this.minY) / this.scaleY - 0.5);
        return pt;
    }

    public Point2D mapSourcePoint(Point2D sourcePt) {
        if (sourcePt == null) {
            throw new IllegalArgumentException("sourcePt == null!");
        }
        Point2D pt = (Point2D)sourcePt.clone();
        pt.setLocation((double)this.minX + (sourcePt.getX() + 0.5 - (double)this.sourceMinX) * this.scaleX - 0.5, (double)this.minY + (sourcePt.getY() + 0.5 - (double)this.sourceMinY) * this.scaleY - 0.5);
        return pt;
    }

    protected Rectangle backwardMapRect(Rectangle destRect, int sourceIndex) {
        if (destRect == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (sourceIndex != 0) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic1"));
        }
        Point2D p1 = this.mapDestPoint(new Point2D.Double(destRect.x, destRect.y));
        Point2D p2 = this.mapDestPoint(new Point2D.Double(destRect.x + destRect.width - 1, destRect.y + destRect.height - 1));
        int x1 = (int)Math.floor(p1.getX());
        int y1 = (int)Math.floor(p1.getY());
        int x2 = (int)Math.floor(p2.getX());
        int y2 = (int)Math.floor(p2.getY());
        return new Rectangle(x1, y1, x2 - x1 + 1, y2 - y1 + 1);
    }

    protected Rectangle forwardMapRect(Rectangle sourceRect, int sourceIndex) {
        if (sourceRect == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (sourceIndex != 0) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic1"));
        }
        Point2D p1 = this.mapSourcePoint(new Point2D.Double(sourceRect.x, sourceRect.y));
        Point2D p2 = this.mapSourcePoint(new Point2D.Double(sourceRect.x + sourceRect.width - 1, sourceRect.y + sourceRect.height - 1));
        int x1 = (int)Math.floor(p1.getX());
        int y1 = (int)Math.floor(p1.getY());
        int x2 = (int)Math.floor(p2.getX());
        int y2 = (int)Math.floor(p2.getY());
        return new Rectangle(x1, y1, x2 - x1 + 1, y2 - y1 + 1);
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        RasterFormatTag[] formatTags = this.getFormatTags();
        RasterAccessor dst = new RasterAccessor(dest, destRect, formatTags[1], this.getColorModel());
        Rectangle srcRect = this.mapDestRect(destRect, 0).intersection(sources[0].getBounds());
        RasterAccessor src = new RasterAccessor(sources[0], srcRect, formatTags[0], this.getSourceImage(0).getColorModel());
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
                throw new RuntimeException(JaiI18N.getString("Generic3"));
            }
        }
        if (dst.isDataCopy()) {
            dst.clampDataArrays();
            dst.copyDataToRaster();
        }
    }

    private void computeRectByte(RasterAccessor src, RasterAccessor dst) {
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
        int[] srcPixelStrideScaled = new int[dwidth];
        for (int i = 0; i < dwidth; ++i) {
            srcPixelStrideScaled[i] = (int)Math.floor((double)i / this.scaleX) * srcPixelStride;
        }
        int[] srcScanlineStrideScaled = new int[dheight];
        for (int i = 0; i < dheight; ++i) {
            srcScanlineStrideScaled[i] = (int)Math.floor((double)i / this.scaleY) * srcScanlineStride;
        }
        float denom = this.blockX * this.blockY;
        for (int k = 0; k < dnumBands; ++k) {
            byte[] dstData = dstDataArrays[k];
            byte[] srcData = srcDataArrays[k];
            int srcScanlineOffset0 = srcBandOffsets[k];
            int dstScanlineOffset = dstBandOffsets[k];
            int srcScanlineOffset = srcScanlineOffset0;
            for (int j = 0; j < dheight; ++j) {
                int srcPixelOffset0 = srcScanlineOffset;
                int dstPixelOffset = dstScanlineOffset;
                int srcPixelOffset = srcPixelOffset0;
                for (int i = 0; i < dwidth; ++i) {
                    int imageVerticalOffset = srcPixelOffset;
                    int sum = 0;
                    for (int u = 0; u < this.blockY; ++u) {
                        int imageOffset = imageVerticalOffset;
                        for (int v = 0; v < this.blockX; ++v) {
                            sum += srcData[imageOffset] & 0xFF;
                            imageOffset += srcPixelStride;
                        }
                        imageVerticalOffset += srcScanlineStride;
                    }
                    dstData[dstPixelOffset] = ImageUtil.clampRoundByte((float)sum / denom);
                    srcPixelOffset = srcPixelOffset0 + srcPixelStrideScaled[i];
                    dstPixelOffset += dstPixelStride;
                }
                srcScanlineOffset = srcScanlineOffset0 + srcScanlineStrideScaled[j];
                dstScanlineOffset += dstScanlineStride;
            }
        }
    }

    private void computeRectUShort(RasterAccessor src, RasterAccessor dst) {
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
        int[] srcPixelStrideScaled = new int[dwidth];
        for (int i = 0; i < dwidth; ++i) {
            srcPixelStrideScaled[i] = (int)Math.floor((double)i / this.scaleX) * srcPixelStride;
        }
        int[] srcScanlineStrideScaled = new int[dheight];
        for (int i = 0; i < dheight; ++i) {
            srcScanlineStrideScaled[i] = (int)Math.floor((double)i / this.scaleY) * srcScanlineStride;
        }
        float denom = this.blockX * this.blockY;
        for (int k = 0; k < dnumBands; ++k) {
            short[] dstData = dstDataArrays[k];
            short[] srcData = srcDataArrays[k];
            int srcScanlineOffset0 = srcBandOffsets[k];
            int dstScanlineOffset = dstBandOffsets[k];
            int srcScanlineOffset = srcScanlineOffset0;
            for (int j = 0; j < dheight; ++j) {
                int srcPixelOffset0 = srcScanlineOffset;
                int dstPixelOffset = dstScanlineOffset;
                int srcPixelOffset = srcPixelOffset0;
                for (int i = 0; i < dwidth; ++i) {
                    int imageVerticalOffset = srcPixelOffset;
                    long sum = 0L;
                    for (int u = 0; u < this.blockY; ++u) {
                        int imageOffset = imageVerticalOffset;
                        for (int v = 0; v < this.blockX; ++v) {
                            sum += (long)(srcData[imageOffset] & 0xFFFF);
                            imageOffset += srcPixelStride;
                        }
                        imageVerticalOffset += srcScanlineStride;
                    }
                    dstData[dstPixelOffset] = ImageUtil.clampRoundUShort((float)sum / denom);
                    srcPixelOffset = srcPixelOffset0 + srcPixelStrideScaled[i];
                    dstPixelOffset += dstPixelStride;
                }
                srcScanlineOffset = srcScanlineOffset0 + srcScanlineStrideScaled[j];
                dstScanlineOffset += dstScanlineStride;
            }
        }
    }

    private void computeRectShort(RasterAccessor src, RasterAccessor dst) {
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
        int[] srcPixelStrideScaled = new int[dwidth];
        for (int i = 0; i < dwidth; ++i) {
            srcPixelStrideScaled[i] = (int)Math.floor((double)i / this.scaleX) * srcPixelStride;
        }
        int[] srcScanlineStrideScaled = new int[dheight];
        for (int i = 0; i < dheight; ++i) {
            srcScanlineStrideScaled[i] = (int)Math.floor((double)i / this.scaleY) * srcScanlineStride;
        }
        float denom = this.blockX * this.blockY;
        for (int k = 0; k < dnumBands; ++k) {
            short[] dstData = dstDataArrays[k];
            short[] srcData = srcDataArrays[k];
            int srcScanlineOffset0 = srcBandOffsets[k];
            int dstScanlineOffset = dstBandOffsets[k];
            int srcScanlineOffset = srcScanlineOffset0;
            for (int j = 0; j < dheight; ++j) {
                int srcPixelOffset0 = srcScanlineOffset;
                int dstPixelOffset = dstScanlineOffset;
                int srcPixelOffset = srcPixelOffset0;
                for (int i = 0; i < dwidth; ++i) {
                    int imageVerticalOffset = srcPixelOffset;
                    long sum = 0L;
                    for (int u = 0; u < this.blockY; ++u) {
                        int imageOffset = imageVerticalOffset;
                        for (int v = 0; v < this.blockX; ++v) {
                            sum += (long)srcData[imageOffset];
                            imageOffset += srcPixelStride;
                        }
                        imageVerticalOffset += srcScanlineStride;
                    }
                    dstData[dstPixelOffset] = ImageUtil.clampRoundShort((float)sum / denom);
                    srcPixelOffset = srcPixelOffset0 + srcPixelStrideScaled[i];
                    dstPixelOffset += dstPixelStride;
                }
                srcScanlineOffset = srcScanlineOffset0 + srcScanlineStrideScaled[j];
                dstScanlineOffset += dstScanlineStride;
            }
        }
    }

    private void computeRectInt(RasterAccessor src, RasterAccessor dst) {
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
        int[] srcPixelStrideScaled = new int[dwidth];
        for (int i = 0; i < dwidth; ++i) {
            srcPixelStrideScaled[i] = (int)Math.floor((double)i / this.scaleX) * srcPixelStride;
        }
        int[] srcScanlineStrideScaled = new int[dheight];
        for (int i = 0; i < dheight; ++i) {
            srcScanlineStrideScaled[i] = (int)Math.floor((double)i / this.scaleY) * srcScanlineStride;
        }
        float denom = this.blockX * this.blockY;
        for (int k = 0; k < dnumBands; ++k) {
            int[] dstData = dstDataArrays[k];
            int[] srcData = srcDataArrays[k];
            int srcScanlineOffset0 = srcBandOffsets[k];
            int dstScanlineOffset = dstBandOffsets[k];
            int srcScanlineOffset = srcScanlineOffset0;
            for (int j = 0; j < dheight; ++j) {
                int srcPixelOffset0 = srcScanlineOffset;
                int dstPixelOffset = dstScanlineOffset;
                int srcPixelOffset = srcPixelOffset0;
                for (int i = 0; i < dwidth; ++i) {
                    int imageVerticalOffset = srcPixelOffset;
                    double sum = 0.0;
                    for (int u = 0; u < this.blockY; ++u) {
                        int imageOffset = imageVerticalOffset;
                        for (int v = 0; v < this.blockX; ++v) {
                            sum += (double)srcData[imageOffset];
                            imageOffset += srcPixelStride;
                        }
                        imageVerticalOffset += srcScanlineStride;
                    }
                    dstData[dstPixelOffset] = ImageUtil.clampRoundInt(sum / (double)denom);
                    srcPixelOffset = srcPixelOffset0 + srcPixelStrideScaled[i];
                    dstPixelOffset += dstPixelStride;
                }
                srcScanlineOffset = srcScanlineOffset0 + srcScanlineStrideScaled[j];
                dstScanlineOffset += dstScanlineStride;
            }
        }
    }

    private void computeRectFloat(RasterAccessor src, RasterAccessor dst) {
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
        int[] srcPixelStrideScaled = new int[dwidth];
        for (int i = 0; i < dwidth; ++i) {
            srcPixelStrideScaled[i] = (int)Math.floor((double)i / this.scaleX) * srcPixelStride;
        }
        int[] srcScanlineStrideScaled = new int[dheight];
        for (int i = 0; i < dheight; ++i) {
            srcScanlineStrideScaled[i] = (int)Math.floor((double)i / this.scaleY) * srcScanlineStride;
        }
        float denom = this.blockX * this.blockY;
        for (int k = 0; k < dnumBands; ++k) {
            float[] dstData = dstDataArrays[k];
            float[] srcData = srcDataArrays[k];
            int srcScanlineOffset0 = srcBandOffsets[k];
            int dstScanlineOffset = dstBandOffsets[k];
            int srcScanlineOffset = srcScanlineOffset0;
            for (int j = 0; j < dheight; ++j) {
                int srcPixelOffset0 = srcScanlineOffset;
                int dstPixelOffset = dstScanlineOffset;
                int srcPixelOffset = srcPixelOffset0;
                for (int i = 0; i < dwidth; ++i) {
                    int imageVerticalOffset = srcPixelOffset;
                    double sum = 0.0;
                    for (int u = 0; u < this.blockY; ++u) {
                        int imageOffset = imageVerticalOffset;
                        for (int v = 0; v < this.blockX; ++v) {
                            sum += (double)srcData[imageOffset];
                            imageOffset += srcPixelStride;
                        }
                        imageVerticalOffset += srcScanlineStride;
                    }
                    dstData[dstPixelOffset] = ImageUtil.clampFloat(sum / (double)denom);
                    srcPixelOffset = srcPixelOffset0 + srcPixelStrideScaled[i];
                    dstPixelOffset += dstPixelStride;
                }
                srcScanlineOffset = srcScanlineOffset0 + srcScanlineStrideScaled[j];
                dstScanlineOffset += dstScanlineStride;
            }
        }
    }

    private void computeRectDouble(RasterAccessor src, RasterAccessor dst) {
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
        int[] srcPixelStrideScaled = new int[dwidth];
        for (int i = 0; i < dwidth; ++i) {
            srcPixelStrideScaled[i] = (int)Math.floor((double)i / this.scaleX) * srcPixelStride;
        }
        int[] srcScanlineStrideScaled = new int[dheight];
        for (int i = 0; i < dheight; ++i) {
            srcScanlineStrideScaled[i] = (int)Math.floor((double)i / this.scaleY) * srcScanlineStride;
        }
        double denom = this.blockX * this.blockY;
        for (int k = 0; k < dnumBands; ++k) {
            double[] dstData = dstDataArrays[k];
            double[] srcData = srcDataArrays[k];
            int srcScanlineOffset0 = srcBandOffsets[k];
            int dstScanlineOffset = dstBandOffsets[k];
            int srcScanlineOffset = srcScanlineOffset0;
            for (int j = 0; j < dheight; ++j) {
                int srcPixelOffset0 = srcScanlineOffset;
                int dstPixelOffset = dstScanlineOffset;
                int srcPixelOffset = srcPixelOffset0;
                for (int i = 0; i < dwidth; ++i) {
                    int imageVerticalOffset = srcPixelOffset;
                    double sum = 0.0;
                    for (int u = 0; u < this.blockY; ++u) {
                        int imageOffset = imageVerticalOffset;
                        for (int v = 0; v < this.blockX; ++v) {
                            sum += srcData[imageOffset];
                            imageOffset += srcPixelStride;
                        }
                        imageVerticalOffset += srcScanlineStride;
                    }
                    dstData[dstPixelOffset] = sum / denom;
                    srcPixelOffset = srcPixelOffset0 + srcPixelStrideScaled[i];
                    dstPixelOffset += dstPixelStride;
                }
                srcScanlineOffset = srcScanlineOffset0 + srcScanlineStrideScaled[j];
                dstScanlineOffset += dstScanlineStride;
            }
        }
    }
}

