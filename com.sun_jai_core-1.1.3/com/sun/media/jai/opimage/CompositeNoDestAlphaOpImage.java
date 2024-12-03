/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.ImageLayout;
import javax.media.jai.PointOpImage;
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFormatTag;

final class CompositeNoDestAlphaOpImage
extends PointOpImage {
    private RenderedImage alpha1;
    private RenderedImage alpha2;
    private boolean premultiplied;
    private RasterFormatTag[] tags;

    public CompositeNoDestAlphaOpImage(RenderedImage source1, RenderedImage source2, Map config, ImageLayout layout, RenderedImage alpha1, RenderedImage alpha2, boolean premultiplied) {
        super(source1, source2, layout, config, true);
        this.alpha1 = alpha1;
        this.alpha2 = alpha2;
        this.premultiplied = premultiplied;
        this.tags = this.getFormatTags();
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        RasterAccessor d;
        RasterAccessor s1 = new RasterAccessor(sources[0], destRect, this.tags[0], this.getSourceImage(0).getColorModel());
        RasterAccessor s2 = new RasterAccessor(sources[1], destRect, this.tags[1], this.getSourceImage(1).getColorModel());
        RasterAccessor a1 = new RasterAccessor(this.alpha1.getData(destRect), destRect, this.tags[2], this.alpha1.getColorModel());
        RasterAccessor a2 = null;
        if (this.alpha2 == null) {
            d = new RasterAccessor(dest, destRect, this.tags[3], this.getColorModel());
        } else {
            a2 = new RasterAccessor(this.alpha2.getData(destRect), destRect, this.tags[3], this.alpha2.getColorModel());
            d = new RasterAccessor(dest, destRect, this.tags[4], this.getColorModel());
        }
        switch (d.getDataType()) {
            case 0: {
                this.byteLoop(s1, s2, a1, a2, d);
                break;
            }
            case 1: {
                this.ushortLoop(s1, s2, a1, a2, d);
                break;
            }
            case 2: {
                this.shortLoop(s1, s2, a1, a2, d);
                break;
            }
            case 3: {
                this.intLoop(s1, s2, a1, a2, d);
                break;
            }
            case 4: {
                this.floatLoop(s1, s2, a1, a2, d);
                break;
            }
            case 5: {
                this.doubleLoop(s1, s2, a1, a2, d);
            }
        }
        if (d.isDataCopy()) {
            d.clampDataArrays();
            d.copyDataToRaster();
        }
    }

    private void byteLoop(RasterAccessor s1, RasterAccessor s2, RasterAccessor a1, RasterAccessor a2, RasterAccessor d) {
        int s1LineStride = s1.getScanlineStride();
        int s1PixelStride = s1.getPixelStride();
        int[] s1BandOffsets = s1.getBandOffsets();
        byte[][] s1Data = s1.getByteDataArrays();
        int s2LineStride = s2.getScanlineStride();
        int s2PixelStride = s2.getPixelStride();
        int[] s2BandOffsets = s2.getBandOffsets();
        byte[][] s2Data = s2.getByteDataArrays();
        int a1LineStride = a1.getScanlineStride();
        int a1PixelStride = a1.getPixelStride();
        int a1BandOffset = a1.getBandOffset(0);
        byte[] a1Data = a1.getByteDataArray(0);
        int a2LineStride = 0;
        int a2PixelStride = 0;
        int a2BandOffset = 0;
        byte[] a2Data = null;
        if (this.alpha2 != null) {
            a2LineStride = a2.getScanlineStride();
            a2PixelStride = a2.getPixelStride();
            a2BandOffset = a2.getBandOffset(0);
            a2Data = a2.getByteDataArray(0);
        }
        int dLineStride = d.getScanlineStride();
        int dPixelStride = d.getPixelStride();
        int[] dBandOffsets = d.getBandOffsets();
        byte[][] dData = d.getByteDataArrays();
        int dwidth = d.getWidth();
        int dheight = d.getHeight();
        int dbands = d.getNumBands();
        float invMax = 0.003921569f;
        int s1LineOffset = 0;
        int s2LineOffset = 0;
        int a1LineOffset = 0;
        int a2LineOffset = 0;
        int dLineOffset = 0;
        if (this.premultiplied) {
            for (int h = 0; h < dheight; ++h) {
                int s1PixelOffset = s1LineOffset;
                int s2PixelOffset = s2LineOffset;
                int a1PixelOffset = a1LineOffset + a1BandOffset;
                int dPixelOffset = dLineOffset;
                s1LineOffset += s1LineStride;
                s2LineOffset += s2LineStride;
                a1LineOffset += a1LineStride;
                dLineOffset += dLineStride;
                for (int w = 0; w < dwidth; ++w) {
                    float t = 1.0f - (float)(a1Data[a1PixelOffset] & 0xFF) * invMax;
                    for (int b = 0; b < dbands; ++b) {
                        dData[b][dPixelOffset + dBandOffsets[b]] = (byte)((float)(s1Data[b][s1PixelOffset + s1BandOffsets[b]] & 0xFF) + (float)(s2Data[b][s2PixelOffset + s2BandOffsets[b]] & 0xFF) * t);
                    }
                    s1PixelOffset += s1PixelStride;
                    s2PixelOffset += s2PixelStride;
                    a1PixelOffset += a1PixelStride;
                    dPixelOffset += dPixelStride;
                }
            }
        } else if (this.alpha2 == null) {
            for (int h = 0; h < dheight; ++h) {
                int s1PixelOffset = s1LineOffset;
                int s2PixelOffset = s2LineOffset;
                int a1PixelOffset = a1LineOffset + a1BandOffset;
                int dPixelOffset = dLineOffset;
                s1LineOffset += s1LineStride;
                s2LineOffset += s2LineStride;
                a1LineOffset += a1LineStride;
                dLineOffset += dLineStride;
                for (int w = 0; w < dwidth; ++w) {
                    float t1 = (float)(a1Data[a1PixelOffset] & 0xFF) * invMax;
                    float t2 = 1.0f - t1;
                    for (int b = 0; b < dbands; ++b) {
                        dData[b][dPixelOffset + dBandOffsets[b]] = (byte)((float)(s1Data[b][s1PixelOffset + s1BandOffsets[b]] & 0xFF) * t1 + (float)(s2Data[b][s2PixelOffset + s2BandOffsets[b]] & 0xFF) * t2);
                    }
                    s1PixelOffset += s1PixelStride;
                    s2PixelOffset += s2PixelStride;
                    a1PixelOffset += a1PixelStride;
                    dPixelOffset += dPixelStride;
                }
            }
        } else {
            for (int h = 0; h < dheight; ++h) {
                int s1PixelOffset = s1LineOffset;
                int s2PixelOffset = s2LineOffset;
                int a1PixelOffset = a1LineOffset + a1BandOffset;
                int a2PixelOffset = a2LineOffset + a2BandOffset;
                int dPixelOffset = dLineOffset;
                s1LineOffset += s1LineStride;
                s2LineOffset += s2LineStride;
                a1LineOffset += a1LineStride;
                a2LineOffset += a2LineStride;
                dLineOffset += dLineStride;
                for (int w = 0; w < dwidth; ++w) {
                    float t5;
                    float t4;
                    int t1 = a1Data[a1PixelOffset] & 0xFF;
                    float t2 = (float)(a2Data[a2PixelOffset] & 0xFF) * (1.0f - (float)t1 * invMax);
                    float t3 = (float)t1 + t2;
                    if (t3 == 0.0f) {
                        t4 = 0.0f;
                        t5 = 0.0f;
                    } else {
                        t4 = (float)t1 / t3;
                        t5 = t2 / t3;
                    }
                    for (int b = 0; b < dbands; ++b) {
                        dData[b][dPixelOffset + dBandOffsets[b]] = (byte)((float)(s1Data[b][s1PixelOffset + s1BandOffsets[b]] & 0xFF) * t4 + (float)(s2Data[b][s2PixelOffset + s2BandOffsets[b]] & 0xFF) * t5);
                    }
                    s1PixelOffset += s1PixelStride;
                    s2PixelOffset += s2PixelStride;
                    a1PixelOffset += a1PixelStride;
                    a2PixelOffset += a2PixelStride;
                    dPixelOffset += dPixelStride;
                }
            }
        }
    }

    private void ushortLoop(RasterAccessor s1, RasterAccessor s2, RasterAccessor a1, RasterAccessor a2, RasterAccessor d) {
        int s1LineStride = s1.getScanlineStride();
        int s1PixelStride = s1.getPixelStride();
        int[] s1BandOffsets = s1.getBandOffsets();
        short[][] s1Data = s1.getShortDataArrays();
        int s2LineStride = s2.getScanlineStride();
        int s2PixelStride = s2.getPixelStride();
        int[] s2BandOffsets = s2.getBandOffsets();
        short[][] s2Data = s2.getShortDataArrays();
        int a1LineStride = a1.getScanlineStride();
        int a1PixelStride = a1.getPixelStride();
        int a1BandOffset = a1.getBandOffset(0);
        short[] a1Data = a1.getShortDataArray(0);
        int a2LineStride = 0;
        int a2PixelStride = 0;
        int a2BandOffset = 0;
        short[] a2Data = null;
        if (this.alpha2 != null) {
            a2LineStride = a2.getScanlineStride();
            a2PixelStride = a2.getPixelStride();
            a2BandOffset = a2.getBandOffset(0);
            a2Data = a2.getShortDataArray(0);
        }
        int dLineStride = d.getScanlineStride();
        int dPixelStride = d.getPixelStride();
        int[] dBandOffsets = d.getBandOffsets();
        short[][] dData = d.getShortDataArrays();
        int dwidth = d.getWidth();
        int dheight = d.getHeight();
        int dbands = d.getNumBands();
        float invMax = 1.5259022E-5f;
        int s1LineOffset = 0;
        int s2LineOffset = 0;
        int a1LineOffset = 0;
        int a2LineOffset = 0;
        int dLineOffset = 0;
        if (this.premultiplied) {
            for (int h = 0; h < dheight; ++h) {
                int s1PixelOffset = s1LineOffset;
                int s2PixelOffset = s2LineOffset;
                int a1PixelOffset = a1LineOffset + a1BandOffset;
                int dPixelOffset = dLineOffset;
                s1LineOffset += s1LineStride;
                s2LineOffset += s2LineStride;
                a1LineOffset += a1LineStride;
                dLineOffset += dLineStride;
                for (int w = 0; w < dwidth; ++w) {
                    float t = 1.0f - (float)(a1Data[a1PixelOffset] & 0xFFFF) * invMax;
                    for (int b = 0; b < dbands; ++b) {
                        dData[b][dPixelOffset + dBandOffsets[b]] = (short)((float)(s1Data[b][s1PixelOffset + s1BandOffsets[b]] & 0xFFFF) + (float)(s2Data[b][s2PixelOffset + s2BandOffsets[b]] & 0xFFFF) * t);
                    }
                    s1PixelOffset += s1PixelStride;
                    s2PixelOffset += s2PixelStride;
                    a1PixelOffset += a1PixelStride;
                    dPixelOffset += dPixelStride;
                }
            }
        } else if (this.alpha2 == null) {
            for (int h = 0; h < dheight; ++h) {
                int s1PixelOffset = s1LineOffset;
                int s2PixelOffset = s2LineOffset;
                int a1PixelOffset = a1LineOffset + a1BandOffset;
                int dPixelOffset = dLineOffset;
                s1LineOffset += s1LineStride;
                s2LineOffset += s2LineStride;
                a1LineOffset += a1LineStride;
                dLineOffset += dLineStride;
                for (int w = 0; w < dwidth; ++w) {
                    float t1 = (float)(a1Data[a1PixelOffset] & 0xFFFF) * invMax;
                    float t2 = 1.0f - t1;
                    for (int b = 0; b < dbands; ++b) {
                        dData[b][dPixelOffset + dBandOffsets[b]] = (short)((float)(s1Data[b][s1PixelOffset + s1BandOffsets[b]] & 0xFFFF) * t1 + (float)(s2Data[b][s2PixelOffset + s2BandOffsets[b]] & 0xFFFF) * t2);
                    }
                    s1PixelOffset += s1PixelStride;
                    s2PixelOffset += s2PixelStride;
                    a1PixelOffset += a1PixelStride;
                    dPixelOffset += dPixelStride;
                }
            }
        } else {
            for (int h = 0; h < dheight; ++h) {
                int s1PixelOffset = s1LineOffset;
                int s2PixelOffset = s2LineOffset;
                int a1PixelOffset = a1LineOffset + a1BandOffset;
                int a2PixelOffset = a2LineOffset + a2BandOffset;
                int dPixelOffset = dLineOffset;
                s1LineOffset += s1LineStride;
                s2LineOffset += s2LineStride;
                a1LineOffset += a1LineStride;
                a2LineOffset += a2LineStride;
                dLineOffset += dLineStride;
                for (int w = 0; w < dwidth; ++w) {
                    float t5;
                    float t4;
                    int t1 = a1Data[a1PixelOffset] & 0xFFFF;
                    float t2 = (float)(a2Data[a2PixelOffset] & 0xFFFF) * (1.0f - (float)t1 * invMax);
                    float t3 = (float)t1 + t2;
                    if (t3 == 0.0f) {
                        t4 = 0.0f;
                        t5 = 0.0f;
                    } else {
                        t4 = (float)t1 / t3;
                        t5 = t2 / t3;
                    }
                    for (int b = 0; b < dbands; ++b) {
                        dData[b][dPixelOffset + dBandOffsets[b]] = (short)((float)(s1Data[b][s1PixelOffset + s1BandOffsets[b]] & 0xFFFF) * t4 + (float)(s2Data[b][s2PixelOffset + s2BandOffsets[b]] & 0xFFFF) * t5);
                    }
                    s1PixelOffset += s1PixelStride;
                    s2PixelOffset += s2PixelStride;
                    a1PixelOffset += a1PixelStride;
                    a2PixelOffset += a2PixelStride;
                    dPixelOffset += dPixelStride;
                }
            }
        }
    }

    private void shortLoop(RasterAccessor s1, RasterAccessor s2, RasterAccessor a1, RasterAccessor a2, RasterAccessor d) {
        int s1LineStride = s1.getScanlineStride();
        int s1PixelStride = s1.getPixelStride();
        int[] s1BandOffsets = s1.getBandOffsets();
        short[][] s1Data = s1.getShortDataArrays();
        int s2LineStride = s2.getScanlineStride();
        int s2PixelStride = s2.getPixelStride();
        int[] s2BandOffsets = s2.getBandOffsets();
        short[][] s2Data = s2.getShortDataArrays();
        int a1LineStride = a1.getScanlineStride();
        int a1PixelStride = a1.getPixelStride();
        int a1BandOffset = a1.getBandOffset(0);
        short[] a1Data = a1.getShortDataArray(0);
        int a2LineStride = 0;
        int a2PixelStride = 0;
        int a2BandOffset = 0;
        short[] a2Data = null;
        if (this.alpha2 != null) {
            a2LineStride = a2.getScanlineStride();
            a2PixelStride = a2.getPixelStride();
            a2BandOffset = a2.getBandOffset(0);
            a2Data = a2.getShortDataArray(0);
        }
        int dLineStride = d.getScanlineStride();
        int dPixelStride = d.getPixelStride();
        int[] dBandOffsets = d.getBandOffsets();
        short[][] dData = d.getShortDataArrays();
        int dwidth = d.getWidth();
        int dheight = d.getHeight();
        int dbands = d.getNumBands();
        float invMax = 3.051851E-5f;
        int s1LineOffset = 0;
        int s2LineOffset = 0;
        int a1LineOffset = 0;
        int a2LineOffset = 0;
        int dLineOffset = 0;
        if (this.premultiplied) {
            for (int h = 0; h < dheight; ++h) {
                int s1PixelOffset = s1LineOffset;
                int s2PixelOffset = s2LineOffset;
                int a1PixelOffset = a1LineOffset + a1BandOffset;
                int dPixelOffset = dLineOffset;
                s1LineOffset += s1LineStride;
                s2LineOffset += s2LineStride;
                a1LineOffset += a1LineStride;
                dLineOffset += dLineStride;
                for (int w = 0; w < dwidth; ++w) {
                    float t = 1.0f - (float)a1Data[a1PixelOffset] * invMax;
                    for (int b = 0; b < dbands; ++b) {
                        dData[b][dPixelOffset + dBandOffsets[b]] = (short)((float)s1Data[b][s1PixelOffset + s1BandOffsets[b]] + (float)s2Data[b][s2PixelOffset + s2BandOffsets[b]] * t);
                    }
                    s1PixelOffset += s1PixelStride;
                    s2PixelOffset += s2PixelStride;
                    a1PixelOffset += a1PixelStride;
                    dPixelOffset += dPixelStride;
                }
            }
        } else if (this.alpha2 == null) {
            for (int h = 0; h < dheight; ++h) {
                int s1PixelOffset = s1LineOffset;
                int s2PixelOffset = s2LineOffset;
                int a1PixelOffset = a1LineOffset + a1BandOffset;
                int dPixelOffset = dLineOffset;
                s1LineOffset += s1LineStride;
                s2LineOffset += s2LineStride;
                a1LineOffset += a1LineStride;
                dLineOffset += dLineStride;
                for (int w = 0; w < dwidth; ++w) {
                    float t1 = (float)a1Data[a1PixelOffset] * invMax;
                    float t2 = 1.0f - t1;
                    for (int b = 0; b < dbands; ++b) {
                        dData[b][dPixelOffset + dBandOffsets[b]] = (short)((float)s1Data[b][s1PixelOffset + s1BandOffsets[b]] * t1 + (float)s2Data[b][s2PixelOffset + s2BandOffsets[b]] * t2);
                    }
                    s1PixelOffset += s1PixelStride;
                    s2PixelOffset += s2PixelStride;
                    a1PixelOffset += a1PixelStride;
                    dPixelOffset += dPixelStride;
                }
            }
        } else {
            for (int h = 0; h < dheight; ++h) {
                int s1PixelOffset = s1LineOffset;
                int s2PixelOffset = s2LineOffset;
                int a1PixelOffset = a1LineOffset + a1BandOffset;
                int a2PixelOffset = a2LineOffset + a2BandOffset;
                int dPixelOffset = dLineOffset;
                s1LineOffset += s1LineStride;
                s2LineOffset += s2LineStride;
                a1LineOffset += a1LineStride;
                a2LineOffset += a2LineStride;
                dLineOffset += dLineStride;
                for (int w = 0; w < dwidth; ++w) {
                    float t5;
                    float t4;
                    short t1 = a1Data[a1PixelOffset];
                    float t2 = (float)a2Data[a2PixelOffset] * (1.0f - (float)t1 * invMax);
                    float t3 = (float)t1 + t2;
                    if (t3 == 0.0f) {
                        t4 = 0.0f;
                        t5 = 0.0f;
                    } else {
                        t4 = (float)t1 / t3;
                        t5 = t2 / t3;
                    }
                    for (int b = 0; b < dbands; ++b) {
                        dData[b][dPixelOffset + dBandOffsets[b]] = (short)((float)s1Data[b][s1PixelOffset + s1BandOffsets[b]] * t4 + (float)s2Data[b][s2PixelOffset + s2BandOffsets[b]] * t5);
                    }
                    s1PixelOffset += s1PixelStride;
                    s2PixelOffset += s2PixelStride;
                    a1PixelOffset += a1PixelStride;
                    a2PixelOffset += a2PixelStride;
                    dPixelOffset += dPixelStride;
                }
            }
        }
    }

    private void intLoop(RasterAccessor s1, RasterAccessor s2, RasterAccessor a1, RasterAccessor a2, RasterAccessor d) {
        int s1LineStride = s1.getScanlineStride();
        int s1PixelStride = s1.getPixelStride();
        int[] s1BandOffsets = s1.getBandOffsets();
        int[][] s1Data = s1.getIntDataArrays();
        int s2LineStride = s2.getScanlineStride();
        int s2PixelStride = s2.getPixelStride();
        int[] s2BandOffsets = s2.getBandOffsets();
        int[][] s2Data = s2.getIntDataArrays();
        int a1LineStride = a1.getScanlineStride();
        int a1PixelStride = a1.getPixelStride();
        int a1BandOffset = a1.getBandOffset(0);
        int[] a1Data = a1.getIntDataArray(0);
        int a2LineStride = 0;
        int a2PixelStride = 0;
        int a2BandOffset = 0;
        int[] a2Data = null;
        if (this.alpha2 != null) {
            a2LineStride = a2.getScanlineStride();
            a2PixelStride = a2.getPixelStride();
            a2BandOffset = a2.getBandOffset(0);
            a2Data = a2.getIntDataArray(0);
        }
        int dLineStride = d.getScanlineStride();
        int dPixelStride = d.getPixelStride();
        int[] dBandOffsets = d.getBandOffsets();
        int[][] dData = d.getIntDataArrays();
        int dwidth = d.getWidth();
        int dheight = d.getHeight();
        int dbands = d.getNumBands();
        float invMax = 4.656613E-10f;
        int s1LineOffset = 0;
        int s2LineOffset = 0;
        int a1LineOffset = 0;
        int a2LineOffset = 0;
        int dLineOffset = 0;
        if (this.premultiplied) {
            for (int h = 0; h < dheight; ++h) {
                int s1PixelOffset = s1LineOffset;
                int s2PixelOffset = s2LineOffset;
                int a1PixelOffset = a1LineOffset + a1BandOffset;
                int dPixelOffset = dLineOffset;
                s1LineOffset += s1LineStride;
                s2LineOffset += s2LineStride;
                a1LineOffset += a1LineStride;
                dLineOffset += dLineStride;
                for (int w = 0; w < dwidth; ++w) {
                    float t = 1.0f - (float)a1Data[a1PixelOffset] * invMax;
                    for (int b = 0; b < dbands; ++b) {
                        dData[b][dPixelOffset + dBandOffsets[b]] = (int)((float)s1Data[b][s1PixelOffset + s1BandOffsets[b]] + (float)s2Data[b][s2PixelOffset + s2BandOffsets[b]] * t);
                    }
                    s1PixelOffset += s1PixelStride;
                    s2PixelOffset += s2PixelStride;
                    a1PixelOffset += a1PixelStride;
                    dPixelOffset += dPixelStride;
                }
            }
        } else if (this.alpha2 == null) {
            for (int h = 0; h < dheight; ++h) {
                int s1PixelOffset = s1LineOffset;
                int s2PixelOffset = s2LineOffset;
                int a1PixelOffset = a1LineOffset + a1BandOffset;
                int dPixelOffset = dLineOffset;
                s1LineOffset += s1LineStride;
                s2LineOffset += s2LineStride;
                a1LineOffset += a1LineStride;
                dLineOffset += dLineStride;
                for (int w = 0; w < dwidth; ++w) {
                    float t1 = (float)a1Data[a1PixelOffset] * invMax;
                    float t2 = 1.0f - t1;
                    for (int b = 0; b < dbands; ++b) {
                        dData[b][dPixelOffset + dBandOffsets[b]] = (int)((float)s1Data[b][s1PixelOffset + s1BandOffsets[b]] * t1 + (float)s2Data[b][s2PixelOffset + s2BandOffsets[b]] * t2);
                    }
                    s1PixelOffset += s1PixelStride;
                    s2PixelOffset += s2PixelStride;
                    a1PixelOffset += a1PixelStride;
                    dPixelOffset += dPixelStride;
                }
            }
        } else {
            for (int h = 0; h < dheight; ++h) {
                int s1PixelOffset = s1LineOffset;
                int s2PixelOffset = s2LineOffset;
                int a1PixelOffset = a1LineOffset + a1BandOffset;
                int a2PixelOffset = a2LineOffset + a2BandOffset;
                int dPixelOffset = dLineOffset;
                s1LineOffset += s1LineStride;
                s2LineOffset += s2LineStride;
                a1LineOffset += a1LineStride;
                a2LineOffset += a2LineStride;
                dLineOffset += dLineStride;
                for (int w = 0; w < dwidth; ++w) {
                    float t5;
                    float t4;
                    int t1 = a1Data[a1PixelOffset];
                    float t2 = (float)a2Data[a2PixelOffset] * (1.0f - (float)t1 * invMax);
                    float t3 = (float)t1 + t2;
                    if (t3 == 0.0f) {
                        t4 = 0.0f;
                        t5 = 0.0f;
                    } else {
                        t4 = (float)t1 / t3;
                        t5 = t2 / t3;
                    }
                    for (int b = 0; b < dbands; ++b) {
                        dData[b][dPixelOffset + dBandOffsets[b]] = (int)((float)s1Data[b][s1PixelOffset + s1BandOffsets[b]] * t4 + (float)s2Data[b][s2PixelOffset + s2BandOffsets[b]] * t5);
                    }
                    s1PixelOffset += s1PixelStride;
                    s2PixelOffset += s2PixelStride;
                    a1PixelOffset += a1PixelStride;
                    a2PixelOffset += a2PixelStride;
                    dPixelOffset += dPixelStride;
                }
            }
        }
    }

    private void floatLoop(RasterAccessor s1, RasterAccessor s2, RasterAccessor a1, RasterAccessor a2, RasterAccessor d) {
        int s1LineStride = s1.getScanlineStride();
        int s1PixelStride = s1.getPixelStride();
        int[] s1BandOffsets = s1.getBandOffsets();
        float[][] s1Data = s1.getFloatDataArrays();
        int s2LineStride = s2.getScanlineStride();
        int s2PixelStride = s2.getPixelStride();
        int[] s2BandOffsets = s2.getBandOffsets();
        float[][] s2Data = s2.getFloatDataArrays();
        int a1LineStride = a1.getScanlineStride();
        int a1PixelStride = a1.getPixelStride();
        int a1BandOffset = a1.getBandOffset(0);
        float[] a1Data = a1.getFloatDataArray(0);
        int a2LineStride = 0;
        int a2PixelStride = 0;
        int a2BandOffset = 0;
        float[] a2Data = null;
        if (this.alpha2 != null) {
            a2LineStride = a2.getScanlineStride();
            a2PixelStride = a2.getPixelStride();
            a2BandOffset = a2.getBandOffset(0);
            a2Data = a2.getFloatDataArray(0);
        }
        int dLineStride = d.getScanlineStride();
        int dPixelStride = d.getPixelStride();
        int[] dBandOffsets = d.getBandOffsets();
        float[][] dData = d.getFloatDataArrays();
        int dwidth = d.getWidth();
        int dheight = d.getHeight();
        int dbands = d.getNumBands();
        int s1LineOffset = 0;
        int s2LineOffset = 0;
        int a1LineOffset = 0;
        int a2LineOffset = 0;
        int dLineOffset = 0;
        if (this.premultiplied) {
            for (int h = 0; h < dheight; ++h) {
                int s1PixelOffset = s1LineOffset;
                int s2PixelOffset = s2LineOffset;
                int a1PixelOffset = a1LineOffset + a1BandOffset;
                int dPixelOffset = dLineOffset;
                s1LineOffset += s1LineStride;
                s2LineOffset += s2LineStride;
                a1LineOffset += a1LineStride;
                dLineOffset += dLineStride;
                for (int w = 0; w < dwidth; ++w) {
                    float t = 1.0f - a1Data[a1PixelOffset];
                    for (int b = 0; b < dbands; ++b) {
                        dData[b][dPixelOffset + dBandOffsets[b]] = s1Data[b][s1PixelOffset + s1BandOffsets[b]] + s2Data[b][s2PixelOffset + s2BandOffsets[b]] * t;
                    }
                    s1PixelOffset += s1PixelStride;
                    s2PixelOffset += s2PixelStride;
                    a1PixelOffset += a1PixelStride;
                    dPixelOffset += dPixelStride;
                }
            }
        } else if (this.alpha2 == null) {
            for (int h = 0; h < dheight; ++h) {
                int s1PixelOffset = s1LineOffset;
                int s2PixelOffset = s2LineOffset;
                int a1PixelOffset = a1LineOffset + a1BandOffset;
                int dPixelOffset = dLineOffset;
                s1LineOffset += s1LineStride;
                s2LineOffset += s2LineStride;
                a1LineOffset += a1LineStride;
                dLineOffset += dLineStride;
                for (int w = 0; w < dwidth; ++w) {
                    float t1 = a1Data[a1PixelOffset];
                    float t2 = 1.0f - t1;
                    for (int b = 0; b < dbands; ++b) {
                        dData[b][dPixelOffset + dBandOffsets[b]] = s1Data[b][s1PixelOffset + s1BandOffsets[b]] * t1 + s2Data[b][s2PixelOffset + s2BandOffsets[b]] * t2;
                    }
                    s1PixelOffset += s1PixelStride;
                    s2PixelOffset += s2PixelStride;
                    a1PixelOffset += a1PixelStride;
                    dPixelOffset += dPixelStride;
                }
            }
        } else {
            for (int h = 0; h < dheight; ++h) {
                int s1PixelOffset = s1LineOffset;
                int s2PixelOffset = s2LineOffset;
                int a1PixelOffset = a1LineOffset + a1BandOffset;
                int a2PixelOffset = a2LineOffset + a2BandOffset;
                int dPixelOffset = dLineOffset;
                s1LineOffset += s1LineStride;
                s2LineOffset += s2LineStride;
                a1LineOffset += a1LineStride;
                a2LineOffset += a2LineStride;
                dLineOffset += dLineStride;
                for (int w = 0; w < dwidth; ++w) {
                    float t5;
                    float t4;
                    float t1 = a1Data[a1PixelOffset];
                    float t2 = a2Data[a2PixelOffset] * (1.0f - t1);
                    float t3 = t1 + t2;
                    if (t3 == 0.0f) {
                        t4 = 0.0f;
                        t5 = 0.0f;
                    } else {
                        t4 = t1 / t3;
                        t5 = t2 / t3;
                    }
                    for (int b = 0; b < dbands; ++b) {
                        dData[b][dPixelOffset + dBandOffsets[b]] = s1Data[b][s1PixelOffset + s1BandOffsets[b]] * t4 + s2Data[b][s2PixelOffset + s2BandOffsets[b]] * t5;
                    }
                    s1PixelOffset += s1PixelStride;
                    s2PixelOffset += s2PixelStride;
                    a1PixelOffset += a1PixelStride;
                    a2PixelOffset += a2PixelStride;
                    dPixelOffset += dPixelStride;
                }
            }
        }
    }

    private void doubleLoop(RasterAccessor s1, RasterAccessor s2, RasterAccessor a1, RasterAccessor a2, RasterAccessor d) {
        int s1LineStride = s1.getScanlineStride();
        int s1PixelStride = s1.getPixelStride();
        int[] s1BandOffsets = s1.getBandOffsets();
        double[][] s1Data = s1.getDoubleDataArrays();
        int s2LineStride = s2.getScanlineStride();
        int s2PixelStride = s2.getPixelStride();
        int[] s2BandOffsets = s2.getBandOffsets();
        double[][] s2Data = s2.getDoubleDataArrays();
        int a1LineStride = a1.getScanlineStride();
        int a1PixelStride = a1.getPixelStride();
        int a1BandOffset = a1.getBandOffset(0);
        double[] a1Data = a1.getDoubleDataArray(0);
        int a2LineStride = 0;
        int a2PixelStride = 0;
        int a2BandOffset = 0;
        double[] a2Data = null;
        if (this.alpha2 != null) {
            a2LineStride = a2.getScanlineStride();
            a2PixelStride = a2.getPixelStride();
            a2BandOffset = a2.getBandOffset(0);
            a2Data = a2.getDoubleDataArray(0);
        }
        int dLineStride = d.getScanlineStride();
        int dPixelStride = d.getPixelStride();
        int[] dBandOffsets = d.getBandOffsets();
        double[][] dData = d.getDoubleDataArrays();
        int dwidth = d.getWidth();
        int dheight = d.getHeight();
        int dbands = d.getNumBands();
        int s1LineOffset = 0;
        int s2LineOffset = 0;
        int a1LineOffset = 0;
        int a2LineOffset = 0;
        int dLineOffset = 0;
        if (this.premultiplied) {
            for (int h = 0; h < dheight; ++h) {
                int s1PixelOffset = s1LineOffset;
                int s2PixelOffset = s2LineOffset;
                int a1PixelOffset = a1LineOffset + a1BandOffset;
                int dPixelOffset = dLineOffset;
                s1LineOffset += s1LineStride;
                s2LineOffset += s2LineStride;
                a1LineOffset += a1LineStride;
                dLineOffset += dLineStride;
                for (int w = 0; w < dwidth; ++w) {
                    double t = 1.0 - a1Data[a1PixelOffset];
                    for (int b = 0; b < dbands; ++b) {
                        dData[b][dPixelOffset + dBandOffsets[b]] = s1Data[b][s1PixelOffset + s1BandOffsets[b]] + s2Data[b][s2PixelOffset + s2BandOffsets[b]] * t;
                    }
                    s1PixelOffset += s1PixelStride;
                    s2PixelOffset += s2PixelStride;
                    a1PixelOffset += a1PixelStride;
                    dPixelOffset += dPixelStride;
                }
            }
        } else if (this.alpha2 == null) {
            for (int h = 0; h < dheight; ++h) {
                int s1PixelOffset = s1LineOffset;
                int s2PixelOffset = s2LineOffset;
                int a1PixelOffset = a1LineOffset + a1BandOffset;
                int dPixelOffset = dLineOffset;
                s1LineOffset += s1LineStride;
                s2LineOffset += s2LineStride;
                a1LineOffset += a1LineStride;
                dLineOffset += dLineStride;
                for (int w = 0; w < dwidth; ++w) {
                    double t1 = a1Data[a1PixelOffset];
                    double t2 = 1.0 - t1;
                    for (int b = 0; b < dbands; ++b) {
                        dData[b][dPixelOffset + dBandOffsets[b]] = s1Data[b][s1PixelOffset + s1BandOffsets[b]] * t1 + s2Data[b][s2PixelOffset + s2BandOffsets[b]] * t2;
                    }
                    s1PixelOffset += s1PixelStride;
                    s2PixelOffset += s2PixelStride;
                    a1PixelOffset += a1PixelStride;
                    dPixelOffset += dPixelStride;
                }
            }
        } else {
            for (int h = 0; h < dheight; ++h) {
                int s1PixelOffset = s1LineOffset;
                int s2PixelOffset = s2LineOffset;
                int a1PixelOffset = a1LineOffset + a1BandOffset;
                int a2PixelOffset = a2LineOffset + a2BandOffset;
                int dPixelOffset = dLineOffset;
                s1LineOffset += s1LineStride;
                s2LineOffset += s2LineStride;
                a1LineOffset += a1LineStride;
                a2LineOffset += a2LineStride;
                dLineOffset += dLineStride;
                for (int w = 0; w < dwidth; ++w) {
                    double t5;
                    double t4;
                    double t1 = a1Data[a1PixelOffset];
                    double t2 = a2Data[a2PixelOffset] * (1.0 - t1);
                    double t3 = t1 + t2;
                    if (t3 == 0.0) {
                        t4 = 0.0;
                        t5 = 0.0;
                    } else {
                        t4 = t1 / t3;
                        t5 = t2 / t3;
                    }
                    for (int b = 0; b < dbands; ++b) {
                        dData[b][dPixelOffset + dBandOffsets[b]] = s1Data[b][s1PixelOffset + s1BandOffsets[b]] * t4 + s2Data[b][s2PixelOffset + s2BandOffsets[b]] * t5;
                    }
                    s1PixelOffset += s1PixelStride;
                    s2PixelOffset += s2PixelStride;
                    a1PixelOffset += a1PixelStride;
                    a2PixelOffset += a2PixelStride;
                    dPixelOffset += dPixelStride;
                }
            }
        }
    }

    protected synchronized RasterFormatTag[] getFormatTags() {
        RenderedImage[] ri;
        if (this.alpha2 == null) {
            ri = new RenderedImage[3];
        } else {
            ri = new RenderedImage[4];
            ri[3] = this.alpha2;
        }
        ri[0] = this.getSourceImage(0);
        ri[1] = this.getSourceImage(1);
        ri[2] = this.alpha1;
        return RasterAccessor.findCompatibleTags(ri, this);
    }
}

