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

final class RescaleOpImage
extends ColormapOpImage {
    protected double[] constants;
    protected double[] offsets;
    private byte[][] byteTable = null;

    private synchronized void initByteTable() {
        if (this.byteTable != null) {
            return;
        }
        int nbands = this.constants.length;
        this.byteTable = new byte[nbands][256];
        for (int band = 0; band < nbands; ++band) {
            byte[] t = this.byteTable[band];
            double c = this.constants[band];
            double o = this.offsets[band];
            for (int i = 0; i < 256; ++i) {
                t[i] = ImageUtil.clampRoundByte((double)i * c + o);
            }
        }
    }

    public RescaleOpImage(RenderedImage source, Map config, ImageLayout layout, double[] constants, double[] offsets) {
        super(source, layout, config, true);
        int i;
        int numBands = this.getSampleModel().getNumBands();
        if (constants.length < numBands) {
            this.constants = new double[numBands];
            for (i = 0; i < numBands; ++i) {
                this.constants[i] = constants[0];
            }
        } else {
            this.constants = constants;
        }
        if (offsets.length < numBands) {
            this.offsets = new double[numBands];
            for (i = 0; i < numBands; ++i) {
                this.offsets[i] = offsets[0];
            }
        } else {
            this.offsets = offsets;
        }
        this.permitInPlaceOperation();
        this.initializeColormapOperation();
    }

    protected void transformColormap(byte[][] colormap) {
        for (int b = 0; b < 3; ++b) {
            byte[] map = colormap[b];
            int mapSize = map.length;
            float c = (float)(b < this.constants.length ? this.constants[b] : this.constants[0]);
            float o = (float)(b < this.constants.length ? this.offsets[b] : this.offsets[0]);
            for (int i = 0; i < mapSize; ++i) {
                map[i] = ImageUtil.clampRoundByte((float)(map[i] & 0xFF) * c + o);
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
        this.initByteTable();
        for (int b = 0; b < dstBands; ++b) {
            byte[] d = dstData[b];
            byte[] s = srcData[b];
            int dstLineOffset = dstBandOffsets[b];
            int srcLineOffset = srcBandOffsets[b];
            byte[] clamp = this.byteTable[b];
            double c = this.constants[b];
            double o = this.offsets[b];
            for (int h = 0; h < dstHeight; ++h) {
                int dstPixelOffset = dstLineOffset;
                int srcPixelOffset = srcLineOffset;
                dstLineOffset += dstLineStride;
                srcLineOffset += srcLineStride;
                for (int w = 0; w < dstWidth; ++w) {
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
            float c = (float)this.constants[b];
            float o = (float)this.offsets[b];
            short[] d = dstData[b];
            short[] s = srcData[b];
            int dstLineOffset = dstBandOffsets[b];
            int srcLineOffset = srcBandOffsets[b];
            for (int h = 0; h < dstHeight; ++h) {
                int dstPixelOffset = dstLineOffset;
                int srcPixelOffset = srcLineOffset;
                dstLineOffset += dstLineStride;
                srcLineOffset += srcLineStride;
                for (int w = 0; w < dstWidth; ++w) {
                    d[dstPixelOffset] = ImageUtil.clampRoundUShort((float)(s[srcPixelOffset] & 0xFFFF) * c + o);
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
            float c = (float)this.constants[b];
            float o = (float)this.offsets[b];
            short[] d = dstData[b];
            short[] s = srcData[b];
            int dstLineOffset = dstBandOffsets[b];
            int srcLineOffset = srcBandOffsets[b];
            for (int h = 0; h < dstHeight; ++h) {
                int dstPixelOffset = dstLineOffset;
                int srcPixelOffset = srcLineOffset;
                dstLineOffset += dstLineStride;
                srcLineOffset += srcLineStride;
                for (int w = 0; w < dstWidth; ++w) {
                    d[dstPixelOffset] = ImageUtil.clampRoundShort((float)s[srcPixelOffset] * c + o);
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
            double c = this.constants[b];
            double o = this.offsets[b];
            int[] d = dstData[b];
            int[] s = srcData[b];
            int dstLineOffset = dstBandOffsets[b];
            int srcLineOffset = srcBandOffsets[b];
            for (int h = 0; h < dstHeight; ++h) {
                int dstPixelOffset = dstLineOffset;
                int srcPixelOffset = srcLineOffset;
                dstLineOffset += dstLineStride;
                srcLineOffset += srcLineStride;
                for (int w = 0; w < dstWidth; ++w) {
                    d[dstPixelOffset] = ImageUtil.clampRoundInt((double)s[srcPixelOffset] * c + o);
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
            double o = this.offsets[b];
            float[] d = dstData[b];
            float[] s = srcData[b];
            int dstLineOffset = dstBandOffsets[b];
            int srcLineOffset = srcBandOffsets[b];
            for (int h = 0; h < dstHeight; ++h) {
                int dstPixelOffset = dstLineOffset;
                int srcPixelOffset = srcLineOffset;
                dstLineOffset += dstLineStride;
                srcLineOffset += srcLineStride;
                for (int w = 0; w < dstWidth; ++w) {
                    d[dstPixelOffset] = ImageUtil.clampFloat((double)s[srcPixelOffset] * c + o);
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
            double o = this.offsets[b];
            double[] d = dstData[b];
            double[] s = srcData[b];
            int dstLineOffset = dstBandOffsets[b];
            int srcLineOffset = srcBandOffsets[b];
            for (int h = 0; h < dstHeight; ++h) {
                int dstPixelOffset = dstLineOffset;
                int srcPixelOffset = srcLineOffset;
                dstLineOffset += dstLineStride;
                srcLineOffset += srcLineStride;
                for (int w = 0; w < dstWidth; ++w) {
                    d[dstPixelOffset] = s[srcPixelOffset] * c + o;
                    dstPixelOffset += dstPixelStride;
                    srcPixelOffset += srcPixelStride;
                }
            }
        }
    }
}

