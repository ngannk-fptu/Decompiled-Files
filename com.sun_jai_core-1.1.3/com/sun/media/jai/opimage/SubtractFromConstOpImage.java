/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.util.ImageUtil;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.ColormapOpImage;
import javax.media.jai.ImageLayout;
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFormatTag;

final class SubtractFromConstOpImage
extends ColormapOpImage {
    protected double[] constants;
    private byte[][] byteTable = null;

    private synchronized void initByteTable() {
        if (this.byteTable != null) {
            return;
        }
        int nbands = this.constants.length;
        this.byteTable = new byte[nbands][256];
        for (int band = 0; band < nbands; ++band) {
            int k = ImageUtil.clampRoundInt(this.constants[band]);
            byte[] t = this.byteTable[band];
            for (int i = 0; i < 256; ++i) {
                int diff = k - i;
                t[i] = diff < 0 ? 0 : (diff > 255 ? -1 : (byte)diff);
            }
        }
    }

    public SubtractFromConstOpImage(RenderedImage source, Map config, ImageLayout layout, double[] constants) {
        super(source, layout, config, true);
        int numBands = this.getSampleModel().getNumBands();
        if (constants.length < numBands) {
            this.constants = new double[numBands];
            for (int i = 0; i < numBands; ++i) {
                this.constants[i] = constants[0];
            }
        } else {
            this.constants = (double[])constants.clone();
        }
        this.permitInPlaceOperation();
        this.initializeColormapOperation();
    }

    protected void transformColormap(byte[][] colormap) {
        this.initByteTable();
        for (int b = 0; b < 3; ++b) {
            byte[] map = colormap[b];
            byte[] luTable = this.byteTable[b >= this.byteTable.length ? 0 : b];
            int mapSize = map.length;
            for (int i = 0; i < mapSize; ++i) {
                map[i] = luTable[map[i] & 0xFF];
            }
        }
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        RasterFormatTag[] formatTags = this.getFormatTags();
        Rectangle srcRect = this.mapDestRect(destRect, 0);
        RasterAccessor dst = new RasterAccessor(dest, destRect, formatTags[1], this.getColorModel());
        RasterAccessor src = new RasterAccessor(sources[0], srcRect, formatTags[0], this.getSource(0).getColorModel());
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
            }
        }
        if (dst.needsClamping()) {
            dst.clampDataArrays();
        }
        dst.copyDataToRaster();
    }

    private void computeRectByte(RasterAccessor src, RasterAccessor dst) {
        this.initByteTable();
        int dstWidth = dst.getWidth();
        int dstHeight = dst.getHeight();
        int dstBands = dst.getNumBands();
        int dstLineStride = dst.getScanlineStride();
        int dstPixelStride = dst.getPixelStride();
        int[] dstBandOffsets = dst.getBandOffsets();
        byte[][] dstData = dst.getByteDataArrays();
        int srcLineStride = src.getScanlineStride();
        int srcPixelStride = src.getPixelStride();
        int[] srcBandOffsets = src.getBandOffsets();
        byte[][] srcData = src.getByteDataArrays();
        for (int b = 0; b < dstBands; ++b) {
            int c = ImageUtil.clampRoundInt(this.constants[b]);
            byte[] d = dstData[b];
            byte[] s = srcData[b];
            byte[] clamp = this.byteTable[b];
            int dstLineOffset = dstBandOffsets[b];
            int srcLineOffset = srcBandOffsets[b];
            for (int h = 0; h < dstHeight; ++h) {
                int dstPixelOffset = dstLineOffset;
                int srcPixelOffset = srcLineOffset;
                dstLineOffset += dstLineStride;
                srcLineOffset += srcLineStride;
                int dstEnd = dstPixelOffset + dstWidth * dstPixelStride;
                while (dstPixelOffset < dstEnd) {
                    d[dstPixelOffset] = clamp[s[srcPixelOffset] & 0xFF];
                    dstPixelOffset += dstPixelStride;
                    srcPixelOffset += srcPixelStride;
                }
            }
        }
    }

    private void computeRectUShort(RasterAccessor src, RasterAccessor dst) {
        int dstWidth = dst.getWidth();
        int dstHeight = dst.getHeight();
        int dstBands = dst.getNumBands();
        int dstLineStride = dst.getScanlineStride();
        int dstPixelStride = dst.getPixelStride();
        int[] dstBandOffsets = dst.getBandOffsets();
        short[][] dstData = dst.getShortDataArrays();
        int srcLineStride = src.getScanlineStride();
        int srcPixelStride = src.getPixelStride();
        int[] srcBandOffsets = src.getBandOffsets();
        short[][] srcData = src.getShortDataArrays();
        for (int b = 0; b < dstBands; ++b) {
            int c = ImageUtil.clampRoundInt(this.constants[b]);
            short[] d = dstData[b];
            short[] s = srcData[b];
            int dstLineOffset = dstBandOffsets[b];
            int srcLineOffset = srcBandOffsets[b];
            for (int h = 0; h < dstHeight; ++h) {
                int dstPixelOffset = dstLineOffset;
                int srcPixelOffset = srcLineOffset;
                dstLineOffset += dstLineStride;
                srcLineOffset += srcLineStride;
                int dstEnd = dstPixelOffset + dstWidth * dstPixelStride;
                while (dstPixelOffset < dstEnd) {
                    d[dstPixelOffset] = ImageUtil.clampUShort(c - (s[srcPixelOffset] & 0xFFFF));
                    dstPixelOffset += dstPixelStride;
                    srcPixelOffset += srcPixelStride;
                }
            }
        }
    }

    private void computeRectShort(RasterAccessor src, RasterAccessor dst) {
        int dstWidth = dst.getWidth();
        int dstHeight = dst.getHeight();
        int dstBands = dst.getNumBands();
        int dstLineStride = dst.getScanlineStride();
        int dstPixelStride = dst.getPixelStride();
        int[] dstBandOffsets = dst.getBandOffsets();
        short[][] dstData = dst.getShortDataArrays();
        int srcLineStride = src.getScanlineStride();
        int srcPixelStride = src.getPixelStride();
        int[] srcBandOffsets = src.getBandOffsets();
        short[][] srcData = src.getShortDataArrays();
        for (int b = 0; b < dstBands; ++b) {
            int c = ImageUtil.clampRoundInt(this.constants[b]);
            short[] d = dstData[b];
            short[] s = srcData[b];
            int dstLineOffset = dstBandOffsets[b];
            int srcLineOffset = srcBandOffsets[b];
            for (int h = 0; h < dstHeight; ++h) {
                int dstPixelOffset = dstLineOffset;
                int srcPixelOffset = srcLineOffset;
                dstLineOffset += dstLineStride;
                srcLineOffset += srcLineStride;
                int dstEnd = dstPixelOffset + dstWidth * dstPixelStride;
                while (dstPixelOffset < dstEnd) {
                    d[dstPixelOffset] = ImageUtil.clampShort(c - s[srcPixelOffset]);
                    dstPixelOffset += dstPixelStride;
                    srcPixelOffset += srcPixelStride;
                }
            }
        }
    }

    private void computeRectInt(RasterAccessor src, RasterAccessor dst) {
        int dstWidth = dst.getWidth();
        int dstHeight = dst.getHeight();
        int dstBands = dst.getNumBands();
        int dstLineStride = dst.getScanlineStride();
        int dstPixelStride = dst.getPixelStride();
        int[] dstBandOffsets = dst.getBandOffsets();
        int[][] dstData = dst.getIntDataArrays();
        int srcLineStride = src.getScanlineStride();
        int srcPixelStride = src.getPixelStride();
        int[] srcBandOffsets = src.getBandOffsets();
        int[][] srcData = src.getIntDataArrays();
        for (int b = 0; b < dstBands; ++b) {
            long c = ImageUtil.clampRoundInt(this.constants[b]);
            int[] d = dstData[b];
            int[] s = srcData[b];
            int dstLineOffset = dstBandOffsets[b];
            int srcLineOffset = srcBandOffsets[b];
            for (int h = 0; h < dstHeight; ++h) {
                int dstPixelOffset = dstLineOffset;
                int srcPixelOffset = srcLineOffset;
                dstLineOffset += dstLineStride;
                srcLineOffset += srcLineStride;
                int dstEnd = dstPixelOffset + dstWidth * dstPixelStride;
                while (dstPixelOffset < dstEnd) {
                    d[dstPixelOffset] = ImageUtil.clampInt(c - (long)s[srcPixelOffset]);
                    dstPixelOffset += dstPixelStride;
                    srcPixelOffset += srcPixelStride;
                }
            }
        }
    }

    private void computeRectFloat(RasterAccessor src, RasterAccessor dst) {
        int dstWidth = dst.getWidth();
        int dstHeight = dst.getHeight();
        int dstBands = dst.getNumBands();
        int dstLineStride = dst.getScanlineStride();
        int dstPixelStride = dst.getPixelStride();
        int[] dstBandOffsets = dst.getBandOffsets();
        float[][] dstData = dst.getFloatDataArrays();
        int srcLineStride = src.getScanlineStride();
        int srcPixelStride = src.getPixelStride();
        int[] srcBandOffsets = src.getBandOffsets();
        float[][] srcData = src.getFloatDataArrays();
        for (int b = 0; b < dstBands; ++b) {
            double c = this.constants[b];
            float[] d = dstData[b];
            float[] s = srcData[b];
            int dstLineOffset = dstBandOffsets[b];
            int srcLineOffset = srcBandOffsets[b];
            for (int h = 0; h < dstHeight; ++h) {
                int dstPixelOffset = dstLineOffset;
                int srcPixelOffset = srcLineOffset;
                dstLineOffset += dstLineStride;
                srcLineOffset += srcLineStride;
                int dstEnd = dstPixelOffset + dstWidth * dstPixelStride;
                while (dstPixelOffset < dstEnd) {
                    d[dstPixelOffset] = ImageUtil.clampFloat(c - (double)s[srcPixelOffset]);
                    dstPixelOffset += dstPixelStride;
                    srcPixelOffset += srcPixelStride;
                }
            }
        }
    }

    private void computeRectDouble(RasterAccessor src, RasterAccessor dst) {
        int dstWidth = dst.getWidth();
        int dstHeight = dst.getHeight();
        int dstBands = dst.getNumBands();
        int dstLineStride = dst.getScanlineStride();
        int dstPixelStride = dst.getPixelStride();
        int[] dstBandOffsets = dst.getBandOffsets();
        double[][] dstData = dst.getDoubleDataArrays();
        int srcLineStride = src.getScanlineStride();
        int srcPixelStride = src.getPixelStride();
        int[] srcBandOffsets = src.getBandOffsets();
        double[][] srcData = src.getDoubleDataArrays();
        for (int b = 0; b < dstBands; ++b) {
            double c = this.constants[b];
            double[] d = dstData[b];
            double[] s = srcData[b];
            int dstLineOffset = dstBandOffsets[b];
            int srcLineOffset = srcBandOffsets[b];
            for (int h = 0; h < dstHeight; ++h) {
                int dstPixelOffset = dstLineOffset;
                int srcPixelOffset = srcLineOffset;
                dstLineOffset += dstLineStride;
                srcLineOffset += srcLineStride;
                int dstEnd = dstPixelOffset + dstWidth * dstPixelStride;
                while (dstPixelOffset < dstEnd) {
                    d[dstPixelOffset] = c - s[srcPixelOffset];
                    dstPixelOffset += dstPixelStride;
                    srcPixelOffset += srcPixelStride;
                }
            }
        }
    }
}

