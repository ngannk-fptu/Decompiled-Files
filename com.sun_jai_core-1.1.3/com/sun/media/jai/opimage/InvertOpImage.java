/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.JaiI18N;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.ColormapOpImage;
import javax.media.jai.ImageLayout;
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFormatTag;

final class InvertOpImage
extends ColormapOpImage {
    public InvertOpImage(RenderedImage source, Map config, ImageLayout layout) {
        super(source, layout, config, true);
        this.permitInPlaceOperation();
        this.initializeColormapOperation();
    }

    protected void transformColormap(byte[][] colormap) {
        for (int b = 0; b < 3; ++b) {
            byte[] map = colormap[b];
            int mapSize = map.length;
            for (int i = 0; i < mapSize; ++i) {
                map[i] = (byte)(255 - (map[i] & 0xFF));
            }
        }
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        RasterFormatTag[] formatTags = this.getFormatTags();
        RasterAccessor s = new RasterAccessor(sources[0], destRect, formatTags[0], this.getSourceImage(0).getColorModel());
        RasterAccessor d = new RasterAccessor(dest, destRect, formatTags[1], this.getColorModel());
        if (d.isBinary()) {
            byte[] srcBits = s.getBinaryDataArray();
            byte[] dstBits = d.getBinaryDataArray();
            int length = dstBits.length;
            for (int i = 0; i < length; ++i) {
                dstBits[i] = ~srcBits[i];
            }
            d.copyBinaryDataToRaster();
        } else {
            switch (d.getDataType()) {
                case 0: {
                    this.computeRectByte(s, d);
                    break;
                }
                case 1: {
                    this.computeRectUShort(s, d);
                    break;
                }
                case 2: {
                    this.computeRectShort(s, d);
                    break;
                }
                case 3: {
                    this.computeRectInt(s, d);
                    break;
                }
                case 4: 
                case 5: {
                    throw new RuntimeException(JaiI18N.getString("InvertOpImage0"));
                }
            }
            d.copyDataToRaster();
        }
    }

    private void computeRectByte(RasterAccessor src, RasterAccessor dst) {
        int sLineStride = src.getScanlineStride();
        int sPixelStride = src.getPixelStride();
        int[] sBandOffsets = src.getBandOffsets();
        byte[][] sData = src.getByteDataArrays();
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int bands = dst.getNumBands();
        int dLineStride = dst.getScanlineStride();
        int dPixelStride = dst.getPixelStride();
        int[] dBandOffsets = dst.getBandOffsets();
        byte[][] dData = dst.getByteDataArrays();
        for (int b = 0; b < bands; ++b) {
            byte[] s = sData[b];
            byte[] d = dData[b];
            int sLineOffset = sBandOffsets[b];
            int dLineOffset = dBandOffsets[b];
            for (int h = 0; h < dheight; ++h) {
                int sPixelOffset = sLineOffset;
                int dPixelOffset = dLineOffset;
                sLineOffset += sLineStride;
                dLineOffset += dLineStride;
                int dstEnd = dPixelOffset + dwidth * dPixelStride;
                while (dPixelOffset < dstEnd) {
                    d[dPixelOffset] = (byte)(255 - (s[sPixelOffset] & 0xFF));
                    sPixelOffset += sPixelStride;
                    dPixelOffset += dPixelStride;
                }
            }
        }
    }

    private void computeRectUShort(RasterAccessor src, RasterAccessor dst) {
        int sLineStride = src.getScanlineStride();
        int sPixelStride = src.getPixelStride();
        int[] sBandOffsets = src.getBandOffsets();
        short[][] sData = src.getShortDataArrays();
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int bands = dst.getNumBands();
        int dLineStride = dst.getScanlineStride();
        int dPixelStride = dst.getPixelStride();
        int[] dBandOffsets = dst.getBandOffsets();
        short[][] dData = dst.getShortDataArrays();
        for (int b = 0; b < bands; ++b) {
            short[] s = sData[b];
            short[] d = dData[b];
            int sLineOffset = sBandOffsets[b];
            int dLineOffset = dBandOffsets[b];
            for (int h = 0; h < dheight; ++h) {
                int sPixelOffset = sLineOffset;
                int dPixelOffset = dLineOffset;
                sLineOffset += sLineStride;
                dLineOffset += dLineStride;
                int dstEnd = dPixelOffset + dwidth * dPixelStride;
                while (dPixelOffset < dstEnd) {
                    d[dPixelOffset] = (short)(65535 - (s[sPixelOffset] & 0xFFFF));
                    sPixelOffset += sPixelStride;
                    dPixelOffset += dPixelStride;
                }
            }
        }
    }

    private void computeRectShort(RasterAccessor src, RasterAccessor dst) {
        int sLineStride = src.getScanlineStride();
        int sPixelStride = src.getPixelStride();
        int[] sBandOffsets = src.getBandOffsets();
        short[][] sData = src.getShortDataArrays();
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int bands = dst.getNumBands();
        int dLineStride = dst.getScanlineStride();
        int dPixelStride = dst.getPixelStride();
        int[] dBandOffsets = dst.getBandOffsets();
        short[][] dData = dst.getShortDataArrays();
        for (int b = 0; b < bands; ++b) {
            short[] s = sData[b];
            short[] d = dData[b];
            int sLineOffset = sBandOffsets[b];
            int dLineOffset = dBandOffsets[b];
            for (int h = 0; h < dheight; ++h) {
                int sPixelOffset = sLineOffset;
                int dPixelOffset = dLineOffset;
                sLineOffset += sLineStride;
                dLineOffset += dLineStride;
                int dstEnd = dPixelOffset + dwidth * dPixelStride;
                while (dPixelOffset < dstEnd) {
                    d[dPixelOffset] = (short)(Short.MAX_VALUE - s[sPixelOffset]);
                    sPixelOffset += sPixelStride;
                    dPixelOffset += dPixelStride;
                }
            }
        }
    }

    private void computeRectInt(RasterAccessor src, RasterAccessor dst) {
        int sLineStride = src.getScanlineStride();
        int sPixelStride = src.getPixelStride();
        int[] sBandOffsets = src.getBandOffsets();
        int[][] sData = src.getIntDataArrays();
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int bands = dst.getNumBands();
        int dLineStride = dst.getScanlineStride();
        int dPixelStride = dst.getPixelStride();
        int[] dBandOffsets = dst.getBandOffsets();
        int[][] dData = dst.getIntDataArrays();
        int[] s = sData[0];
        int[] d = dData[0];
        int pixels = d.length;
        switch (this.sampleModel.getTransferType()) {
            case 0: {
                for (int i = 0; i < pixels; ++i) {
                    d[i] = ~s[i] & 0xFF;
                }
                break;
            }
            case 1: {
                for (int i = 0; i < pixels; ++i) {
                    d[i] = ~s[i] & 0xFFFF;
                }
                break;
            }
            case 2: {
                for (int i = 0; i < pixels; ++i) {
                    d[i] = Short.MAX_VALUE - s[i];
                }
                break;
            }
            case 3: {
                for (int b = 0; b < bands; ++b) {
                    s = sData[b];
                    d = dData[b];
                    int sLineOffset = sBandOffsets[b];
                    int dLineOffset = dBandOffsets[b];
                    for (int h = 0; h < dheight; ++h) {
                        int sPixelOffset = sLineOffset;
                        int dPixelOffset = dLineOffset;
                        sLineOffset += sLineStride;
                        dLineOffset += dLineStride;
                        int dstEnd = dPixelOffset + dwidth * dPixelStride;
                        while (dPixelOffset < dstEnd) {
                            d[dPixelOffset] = Integer.MAX_VALUE - s[sPixelOffset];
                            sPixelOffset += sPixelStride;
                            dPixelOffset += dPixelStride;
                        }
                    }
                }
                break;
            }
        }
    }
}

