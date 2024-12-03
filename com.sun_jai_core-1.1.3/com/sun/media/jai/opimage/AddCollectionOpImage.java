/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.util.ImageUtil;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import javax.media.jai.ImageLayout;
import javax.media.jai.PointOpImage;
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFormatTag;

final class AddCollectionOpImage
extends PointOpImage {
    private byte[][] byteTable = null;

    private synchronized void initByteTable() {
        if (this.byteTable != null) {
            return;
        }
        this.byteTable = new byte[256][256];
        for (int j = 0; j < 256; ++j) {
            byte[] t = this.byteTable[j];
            for (int i = 0; i < 256; ++i) {
                t[i] = ImageUtil.clampBytePositive(j + i);
            }
        }
    }

    public AddCollectionOpImage(Collection sources, Map config, ImageLayout layout) {
        super(AddCollectionOpImage.vectorize(sources), layout, config, true);
    }

    private static Vector vectorize(Collection sources) {
        if (sources instanceof Vector) {
            return (Vector)sources;
        }
        Vector v = new Vector(sources.size());
        Iterator iter = sources.iterator();
        while (iter.hasNext()) {
            v.add(iter.next());
        }
        return v;
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        RasterFormatTag[] formatTags = this.getFormatTags();
        int numSrcs = this.getNumSources();
        RasterAccessor dst = new RasterAccessor(dest, destRect, formatTags[numSrcs], this.getColorModel());
        RasterAccessor[] srcs = new RasterAccessor[numSrcs];
        for (int i = 0; i < numSrcs; ++i) {
            Rectangle srcRect = this.mapDestRect(destRect, i);
            srcs[i] = new RasterAccessor(sources[i], srcRect, formatTags[i], this.getSourceImage(i).getColorModel());
        }
        switch (dst.getDataType()) {
            case 0: {
                this.computeRectByte(srcs, dst);
                break;
            }
            case 1: {
                this.computeRectUShort(srcs, dst);
                break;
            }
            case 2: {
                this.computeRectShort(srcs, dst);
                break;
            }
            case 3: {
                this.computeRectInt(srcs, dst);
                break;
            }
            case 4: {
                this.computeRectFloat(srcs, dst);
                break;
            }
            case 5: {
                this.computeRectDouble(srcs, dst);
            }
        }
        if (dst.needsClamping()) {
            dst.clampDataArrays();
        }
        dst.copyDataToRaster();
    }

    private void computeRectByte(RasterAccessor[] srcs, RasterAccessor dst) {
        this.initByteTable();
        int dstWidth = dst.getWidth();
        int dstHeight = dst.getHeight();
        int dstBands = dst.getNumBands();
        int dstLineStride = dst.getScanlineStride();
        int dstPixelStride = dst.getPixelStride();
        int[] dstBandOffsets = dst.getBandOffsets();
        byte[][] dstData = dst.getByteDataArrays();
        int numSrcs = this.getNumSources();
        for (int i = 0; i < numSrcs; ++i) {
            RasterAccessor src = srcs[i];
            int srcLineStride = src.getScanlineStride();
            int srcPixelStride = src.getPixelStride();
            int[] srcBandOffsets = src.getBandOffsets();
            byte[][] srcData = src.getByteDataArrays();
            for (int b = 0; b < dstBands; ++b) {
                int dstLineOffset = dstBandOffsets[b];
                int srcLineOffset = srcBandOffsets[b];
                byte[] d = dstData[b];
                byte[] s = srcData[b];
                for (int h = 0; h < dstHeight; ++h) {
                    int dstPixelOffset = dstLineOffset;
                    int srcPixelOffset = srcLineOffset;
                    dstLineOffset += dstLineStride;
                    srcLineOffset += srcLineStride;
                    for (int w = 0; w < dstWidth; ++w) {
                        d[dstPixelOffset] = this.byteTable[d[dstPixelOffset] & 0xFF][s[srcPixelOffset] & 0xFF];
                        dstPixelOffset += dstPixelStride;
                        srcPixelOffset += srcPixelStride;
                    }
                }
            }
        }
    }

    private void computeRectUShort(RasterAccessor[] srcs, RasterAccessor dst) {
        int dstWidth = dst.getWidth();
        int dstHeight = dst.getHeight();
        int dstBands = dst.getNumBands();
        int dstLineStride = dst.getScanlineStride();
        int dstPixelStride = dst.getPixelStride();
        int[] dstBandOffsets = dst.getBandOffsets();
        short[][] dstData = dst.getShortDataArrays();
        int numSrcs = this.getNumSources();
        for (int i = 0; i < numSrcs; ++i) {
            RasterAccessor src = srcs[i];
            int srcLineStride = src.getScanlineStride();
            int srcPixelStride = src.getPixelStride();
            int[] srcBandOffsets = src.getBandOffsets();
            short[][] srcData = src.getShortDataArrays();
            for (int b = 0; b < dstBands; ++b) {
                int dstLineOffset = dstBandOffsets[b];
                int srcLineOffset = srcBandOffsets[b];
                short[] d = dstData[b];
                short[] s = srcData[b];
                for (int h = 0; h < dstHeight; ++h) {
                    int dstPixelOffset = dstLineOffset;
                    int srcPixelOffset = srcLineOffset;
                    dstLineOffset += dstLineStride;
                    srcLineOffset += srcLineStride;
                    for (int w = 0; w < dstWidth; ++w) {
                        d[dstPixelOffset] = ImageUtil.clampUShortPositive((d[dstPixelOffset] & 0xFFFF) + (s[srcPixelOffset] & 0xFFFF));
                        dstPixelOffset += dstPixelStride;
                        srcPixelOffset += srcPixelStride;
                    }
                }
            }
        }
    }

    private void computeRectShort(RasterAccessor[] srcs, RasterAccessor dst) {
        int dstWidth = dst.getWidth();
        int dstHeight = dst.getHeight();
        int dstBands = dst.getNumBands();
        int dstLineStride = dst.getScanlineStride();
        int dstPixelStride = dst.getPixelStride();
        int[] dstBandOffsets = dst.getBandOffsets();
        short[][] dstData = dst.getShortDataArrays();
        int numSrcs = this.getNumSources();
        for (int i = 0; i < numSrcs; ++i) {
            RasterAccessor src = srcs[i];
            int srcLineStride = src.getScanlineStride();
            int srcPixelStride = src.getPixelStride();
            int[] srcBandOffsets = src.getBandOffsets();
            short[][] srcData = src.getShortDataArrays();
            for (int b = 0; b < dstBands; ++b) {
                int dstLineOffset = dstBandOffsets[b];
                int srcLineOffset = srcBandOffsets[b];
                short[] d = dstData[b];
                short[] s = srcData[b];
                for (int h = 0; h < dstHeight; ++h) {
                    int dstPixelOffset = dstLineOffset;
                    int srcPixelOffset = srcLineOffset;
                    dstLineOffset += dstLineStride;
                    srcLineOffset += srcLineStride;
                    for (int w = 0; w < dstWidth; ++w) {
                        d[dstPixelOffset] = ImageUtil.clampShort(d[dstPixelOffset] + s[srcPixelOffset]);
                        dstPixelOffset += dstPixelStride;
                        srcPixelOffset += srcPixelStride;
                    }
                }
            }
        }
    }

    private void computeRectInt(RasterAccessor[] srcs, RasterAccessor dst) {
        int dstWidth = dst.getWidth();
        int dstHeight = dst.getHeight();
        int dstBands = dst.getNumBands();
        int dstLineStride = dst.getScanlineStride();
        int dstPixelStride = dst.getPixelStride();
        int[] dstBandOffsets = dst.getBandOffsets();
        int[][] dstData = dst.getIntDataArrays();
        int numSrcs = this.getNumSources();
        for (int i = 0; i < numSrcs; ++i) {
            RasterAccessor src = srcs[i];
            int srcLineStride = src.getScanlineStride();
            int srcPixelStride = src.getPixelStride();
            int[] srcBandOffsets = src.getBandOffsets();
            int[][] srcData = src.getIntDataArrays();
            for (int b = 0; b < dstBands; ++b) {
                int dstLineOffset = dstBandOffsets[b];
                int srcLineOffset = srcBandOffsets[b];
                int[] d = dstData[b];
                int[] s = srcData[b];
                for (int h = 0; h < dstHeight; ++h) {
                    int dstPixelOffset = dstLineOffset;
                    int srcPixelOffset = srcLineOffset;
                    dstLineOffset += dstLineStride;
                    srcLineOffset += srcLineStride;
                    for (int w = 0; w < dstWidth; ++w) {
                        d[dstPixelOffset] = ImageUtil.clampInt((long)d[dstPixelOffset] + (long)s[srcPixelOffset]);
                        dstPixelOffset += dstPixelStride;
                        srcPixelOffset += srcPixelStride;
                    }
                }
            }
        }
    }

    private void computeRectFloat(RasterAccessor[] srcs, RasterAccessor dst) {
        int dstWidth = dst.getWidth();
        int dstHeight = dst.getHeight();
        int dstBands = dst.getNumBands();
        int dstLineStride = dst.getScanlineStride();
        int dstPixelStride = dst.getPixelStride();
        int[] dstBandOffsets = dst.getBandOffsets();
        float[][] dstData = dst.getFloatDataArrays();
        int numSrcs = this.getNumSources();
        for (int i = 0; i < numSrcs; ++i) {
            RasterAccessor src = srcs[i];
            int srcLineStride = src.getScanlineStride();
            int srcPixelStride = src.getPixelStride();
            int[] srcBandOffsets = src.getBandOffsets();
            float[][] srcData = src.getFloatDataArrays();
            for (int b = 0; b < dstBands; ++b) {
                int dstLineOffset = dstBandOffsets[b];
                int srcLineOffset = srcBandOffsets[b];
                float[] d = dstData[b];
                float[] s = srcData[b];
                for (int h = 0; h < dstHeight; ++h) {
                    int dstPixelOffset = dstLineOffset;
                    int srcPixelOffset = srcLineOffset;
                    dstLineOffset += dstLineStride;
                    srcLineOffset += srcLineStride;
                    for (int w = 0; w < dstWidth; ++w) {
                        d[dstPixelOffset] = ImageUtil.clampFloat((double)d[dstPixelOffset] + (double)s[srcPixelOffset]);
                        dstPixelOffset += dstPixelStride;
                        srcPixelOffset += srcPixelStride;
                    }
                }
            }
        }
    }

    private void computeRectDouble(RasterAccessor[] srcs, RasterAccessor dst) {
        int dstWidth = dst.getWidth();
        int dstHeight = dst.getHeight();
        int dstBands = dst.getNumBands();
        int dstLineStride = dst.getScanlineStride();
        int dstPixelStride = dst.getPixelStride();
        int[] dstBandOffsets = dst.getBandOffsets();
        double[][] dstData = dst.getDoubleDataArrays();
        int numSrcs = this.getNumSources();
        for (int i = 0; i < numSrcs; ++i) {
            RasterAccessor src = srcs[i];
            int srcLineStride = src.getScanlineStride();
            int srcPixelStride = src.getPixelStride();
            int[] srcBandOffsets = src.getBandOffsets();
            double[][] srcData = src.getDoubleDataArrays();
            for (int b = 0; b < dstBands; ++b) {
                int dstLineOffset = dstBandOffsets[b];
                int srcLineOffset = srcBandOffsets[b];
                double[] d = dstData[b];
                double[] s = srcData[b];
                for (int h = 0; h < dstHeight; ++h) {
                    int dstPixelOffset = dstLineOffset;
                    int srcPixelOffset = srcLineOffset;
                    dstLineOffset += dstLineStride;
                    srcLineOffset += srcLineStride;
                    for (int w = 0; w < dstWidth; ++w) {
                        d[dstPixelOffset] = d[dstPixelOffset] + s[srcPixelOffset];
                        dstPixelOffset += dstPixelStride;
                        srcPixelOffset += srcPixelStride;
                    }
                }
            }
        }
    }
}

