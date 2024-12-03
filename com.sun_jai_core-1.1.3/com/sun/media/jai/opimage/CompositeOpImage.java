/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.util.ImageUtil;
import com.sun.media.jai.util.JDKWorkarounds;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.ImageLayout;
import javax.media.jai.PointOpImage;
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFactory;
import javax.media.jai.RasterFormatTag;

final class CompositeOpImage
extends PointOpImage {
    protected RenderedImage source1Alpha;
    protected RenderedImage source2Alpha;
    protected boolean alphaPremultiplied;
    private int aOffset;
    private int cOffset;
    private byte maxValueByte;
    private short maxValueShort;
    private int maxValue;
    private float invMaxValue;

    public CompositeOpImage(RenderedImage source1, RenderedImage source2, Map config, ImageLayout layout, RenderedImage source1Alpha, RenderedImage source2Alpha, boolean alphaPremultiplied, boolean alphaFirst) {
        super(source1, source2, layout, config, true);
        this.source1Alpha = source1Alpha;
        this.source2Alpha = source2Alpha;
        this.alphaPremultiplied = alphaPremultiplied;
        SampleModel sm = source1.getSampleModel();
        ColorModel cm = source1.getColorModel();
        int dtype = sm.getTransferType();
        int bands = cm instanceof IndexColorModel ? cm.getNumComponents() : sm.getNumBands();
        if (this.sampleModel.getTransferType() != dtype || this.sampleModel.getNumBands() != ++bands) {
            this.sampleModel = RasterFactory.createComponentSampleModel(this.sampleModel, dtype, this.tileWidth, this.tileHeight, bands);
            if (this.colorModel != null && !JDKWorkarounds.areCompatibleDataModels(this.sampleModel, this.colorModel)) {
                this.colorModel = ImageUtil.getCompatibleColorModel(this.sampleModel, config);
            }
        }
        this.aOffset = alphaFirst ? 0 : bands - 1;
        this.cOffset = alphaFirst ? 1 : 0;
        switch (dtype) {
            case 0: {
                this.maxValue = 255;
                this.maxValueByte = (byte)-1;
                break;
            }
            case 1: {
                this.maxValue = 65535;
                this.maxValueShort = (short)-1;
                break;
            }
            case 2: {
                this.maxValue = Short.MAX_VALUE;
                this.maxValueShort = Short.MAX_VALUE;
                break;
            }
            case 3: {
                this.maxValue = Integer.MAX_VALUE;
                break;
            }
        }
        this.invMaxValue = 1.0f / (float)this.maxValue;
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        RenderedImage[] renderedSources = this.source2Alpha == null ? new RenderedImage[3] : new RenderedImage[4];
        renderedSources[0] = this.getSourceImage(0);
        renderedSources[1] = this.getSourceImage(1);
        renderedSources[2] = this.source1Alpha;
        Raster source1AlphaRaster = this.source1Alpha.getData(destRect);
        Raster source2AlphaRaster = null;
        if (this.source2Alpha != null) {
            renderedSources[3] = this.source2Alpha;
            source2AlphaRaster = this.source2Alpha.getData(destRect);
        }
        RasterFormatTag[] tags = RasterAccessor.findCompatibleTags(renderedSources, this);
        RasterAccessor s1 = new RasterAccessor(sources[0], destRect, tags[0], this.getSourceImage(0).getColorModel());
        RasterAccessor s2 = new RasterAccessor(sources[1], destRect, tags[1], this.getSourceImage(1).getColorModel());
        RasterAccessor a1 = new RasterAccessor(source1AlphaRaster, destRect, tags[2], this.source1Alpha.getColorModel());
        RasterAccessor a2 = null;
        RasterAccessor d = null;
        if (this.source2Alpha != null) {
            a2 = new RasterAccessor(source2AlphaRaster, destRect, tags[3], this.source2Alpha.getColorModel());
            d = new RasterAccessor(dest, destRect, tags[4], this.getColorModel());
        } else {
            a2 = null;
            d = new RasterAccessor(dest, destRect, tags[3], this.getColorModel());
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
        d.copyDataToRaster();
    }

    private void byteLoop(RasterAccessor src1, RasterAccessor src2, RasterAccessor afa1, RasterAccessor afa2, RasterAccessor dst) {
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int numBands = src1.getNumBands();
        byte[][] s1 = src1.getByteDataArrays();
        int s1ss = src1.getScanlineStride();
        int s1ps = src1.getPixelStride();
        int[] s1bo = src1.getBandOffsets();
        byte[][] s2 = src2.getByteDataArrays();
        int s2ss = src2.getScanlineStride();
        int s2ps = src2.getPixelStride();
        int[] s2bo = src2.getBandOffsets();
        byte[] a1 = afa1.getByteDataArray(0);
        int a1ss = afa1.getScanlineStride();
        int a1ps = afa1.getPixelStride();
        int a1bo = afa1.getBandOffset(0);
        byte[] a2 = null;
        int a2ss = 0;
        int a2ps = 0;
        int a2bo = 0;
        if (afa2 != null) {
            a2 = afa2.getByteDataArray(0);
            a2ss = afa2.getScanlineStride();
            a2ps = afa2.getPixelStride();
            a2bo = afa2.getBandOffset(0);
        }
        byte[][] d = dst.getByteDataArrays();
        int dss = dst.getScanlineStride();
        int dps = dst.getPixelStride();
        int[] dbo = dst.getBandOffsets();
        int s1so = 0;
        int s2so = 0;
        int a1so = 0;
        int a2so = 0;
        int dso = 0;
        if (this.alphaPremultiplied) {
            if (afa2 == null) {
                for (int h = 0; h < dheight; ++h) {
                    int s1po = s1so;
                    int s2po = s2so;
                    int a1po = a1so;
                    int dpo = dso;
                    for (int w = 0; w < dwidth; ++w) {
                        float t = 1.0f - (float)(a1[a1po + a1bo] & 0xFF) * this.invMaxValue;
                        d[this.aOffset][dpo + dbo[this.aOffset]] = this.maxValueByte;
                        for (int b = 0; b < numBands; ++b) {
                            int i = b + this.cOffset;
                            d[i][dpo + dbo[i]] = (byte)((float)(s1[b][s1po + s1bo[b]] & 0xFF) + (float)(s2[b][s2po + s2bo[b]] & 0xFF) * t);
                        }
                        s1po += s1ps;
                        s2po += s2ps;
                        a1po += a1ps;
                        dpo += dps;
                    }
                    s1so += s1ss;
                    s2so += s2ss;
                    a1so += a1ss;
                    dso += dss;
                }
            } else {
                for (int h = 0; h < dheight; ++h) {
                    int s1po = s1so;
                    int s2po = s2so;
                    int a1po = a1so;
                    int a2po = a2so;
                    int dpo = dso;
                    for (int w = 0; w < dwidth; ++w) {
                        int t1 = a1[a1po + a1bo] & 0xFF;
                        float t2 = 1.0f - (float)t1 * this.invMaxValue;
                        d[this.aOffset][dpo + dbo[this.aOffset]] = (byte)((float)t1 + (float)(a2[a2po + a2bo] & 0xFF) * t2);
                        for (int b = 0; b < numBands; ++b) {
                            int i = b + this.cOffset;
                            d[i][dpo + dbo[i]] = (byte)((float)(s1[b][s1po + s1bo[b]] & 0xFF) + (float)(s2[b][s2po + s2bo[b]] & 0xFF) * t2);
                        }
                        s1po += s1ps;
                        s2po += s2ps;
                        a1po += a1ps;
                        a2po += a2ps;
                        dpo += dps;
                    }
                    s1so += s1ss;
                    s2so += s2ss;
                    a1so += a1ss;
                    a2so += a2ss;
                    dso += dss;
                }
            }
        } else if (afa2 == null) {
            for (int h = 0; h < dheight; ++h) {
                int s1po = s1so;
                int s2po = s2so;
                int a1po = a1so;
                int dpo = dso;
                for (int w = 0; w < dwidth; ++w) {
                    float t1 = (float)(a1[a1po + a1bo] & 0xFF) * this.invMaxValue;
                    float t2 = 1.0f - t1;
                    d[this.aOffset][dpo + dbo[this.aOffset]] = this.maxValueByte;
                    for (int b = 0; b < numBands; ++b) {
                        int i = b + this.cOffset;
                        d[i][dpo + dbo[i]] = (byte)((float)(s1[b][s1po + s1bo[b]] & 0xFF) * t1 + (float)(s2[b][s2po + s2bo[b]] & 0xFF) * t2);
                    }
                    s1po += s1ps;
                    s2po += s2ps;
                    a1po += a1ps;
                    dpo += dps;
                }
                s1so += s1ss;
                s2so += s2ss;
                a1so += a1ss;
                dso += dss;
            }
        } else {
            for (int h = 0; h < dheight; ++h) {
                int s1po = s1so;
                int s2po = s2so;
                int a1po = a1so;
                int a2po = a2so;
                int dpo = dso;
                for (int w = 0; w < dwidth; ++w) {
                    float t5;
                    float t4;
                    int t1 = a1[a1po + a1bo] & 0xFF;
                    float t2 = (1.0f - (float)t1 * this.invMaxValue) * (float)(a2[a2po + a2bo] & 0xFF);
                    float t3 = (float)t1 + t2;
                    if (t3 == 0.0f) {
                        t4 = 0.0f;
                        t5 = 0.0f;
                    } else {
                        t4 = (float)t1 / t3;
                        t5 = t2 / t3;
                    }
                    d[this.aOffset][dpo + dbo[this.aOffset]] = (byte)t3;
                    for (int b = 0; b < numBands; ++b) {
                        int i = b + this.cOffset;
                        d[i][dpo + dbo[i]] = (byte)((float)(s1[b][s1po + s1bo[b]] & 0xFF) * t4 + (float)(s2[b][s2po + s2bo[b]] & 0xFF) * t5);
                    }
                    s1po += s1ps;
                    s2po += s2ps;
                    a1po += a1ps;
                    a2po += a2ps;
                    dpo += dps;
                }
                s1so += s1ss;
                s2so += s2ss;
                a1so += a1ss;
                a2so += a2ss;
                dso += dss;
            }
        }
    }

    private void ushortLoop(RasterAccessor src1, RasterAccessor src2, RasterAccessor afa1, RasterAccessor afa2, RasterAccessor dst) {
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int numBands = src1.getNumBands();
        short[][] s1 = src1.getShortDataArrays();
        int s1ss = src1.getScanlineStride();
        int s1ps = src1.getPixelStride();
        int[] s1bo = src1.getBandOffsets();
        short[][] s2 = src2.getShortDataArrays();
        int s2ss = src2.getScanlineStride();
        int s2ps = src2.getPixelStride();
        int[] s2bo = src2.getBandOffsets();
        short[] a1 = afa1.getShortDataArray(0);
        int a1ss = afa1.getScanlineStride();
        int a1ps = afa1.getPixelStride();
        int a1bo = afa1.getBandOffset(0);
        short[] a2 = null;
        int a2ss = 0;
        int a2ps = 0;
        int a2bo = 0;
        if (afa2 != null) {
            a2 = afa2.getShortDataArray(0);
            a2ss = afa2.getScanlineStride();
            a2ps = afa2.getPixelStride();
            a2bo = afa2.getBandOffset(0);
        }
        short[][] d = dst.getShortDataArrays();
        int dss = dst.getScanlineStride();
        int dps = dst.getPixelStride();
        int[] dbo = dst.getBandOffsets();
        int s1so = 0;
        int s2so = 0;
        int a1so = 0;
        int a2so = 0;
        int dso = 0;
        if (this.alphaPremultiplied) {
            if (afa2 == null) {
                for (int h = 0; h < dheight; ++h) {
                    int s1po = s1so;
                    int s2po = s2so;
                    int a1po = a1so;
                    int dpo = dso;
                    for (int w = 0; w < dwidth; ++w) {
                        float t = 1.0f - (float)(a1[a1po + a1bo] & 0xFFFF) * this.invMaxValue;
                        d[this.aOffset][dpo + dbo[this.aOffset]] = this.maxValueShort;
                        for (int b = 0; b < numBands; ++b) {
                            int i = b + this.cOffset;
                            d[i][dpo + dbo[i]] = (short)((float)(s1[b][s1po + s1bo[b]] & 0xFFFF) + (float)(s2[b][s2po + s2bo[b]] & 0xFFFF) * t);
                        }
                        s1po += s1ps;
                        s2po += s2ps;
                        a1po += a1ps;
                        dpo += dps;
                    }
                    s1so += s1ss;
                    s2so += s2ss;
                    a1so += a1ss;
                    dso += dss;
                }
            } else {
                for (int h = 0; h < dheight; ++h) {
                    int s1po = s1so;
                    int s2po = s2so;
                    int a1po = a1so;
                    int a2po = a2so;
                    int dpo = dso;
                    for (int w = 0; w < dwidth; ++w) {
                        int t1 = a1[a1po + a1bo] & 0xFFFF;
                        float t2 = 1.0f - (float)t1 * this.invMaxValue;
                        d[this.aOffset][dpo + dbo[this.aOffset]] = (short)((float)t1 + (float)(a2[a2po + a2bo] & 0xFFFF) * t2);
                        for (int b = 0; b < numBands; ++b) {
                            int i = b + this.cOffset;
                            d[i][dpo + dbo[i]] = (short)((float)(s1[b][s1po + s1bo[b]] & 0xFFFF) + (float)(s2[b][s2po + s2bo[b]] & 0xFFFF) * t2);
                        }
                        s1po += s1ps;
                        s2po += s2ps;
                        a1po += a1ps;
                        a2po += a2ps;
                        dpo += dps;
                    }
                    s1so += s1ss;
                    s2so += s2ss;
                    a1so += a1ss;
                    a2so += a2ss;
                    dso += dss;
                }
            }
        } else if (afa2 == null) {
            for (int h = 0; h < dheight; ++h) {
                int s1po = s1so;
                int s2po = s2so;
                int a1po = a1so;
                int dpo = dso;
                for (int w = 0; w < dwidth; ++w) {
                    float t1 = (float)(a1[a1po + a1bo] & 0xFFFF) * this.invMaxValue;
                    float t2 = 1.0f - t1;
                    d[this.aOffset][dpo + dbo[this.aOffset]] = this.maxValueShort;
                    for (int b = 0; b < numBands; ++b) {
                        int i = b + this.cOffset;
                        d[i][dpo + dbo[i]] = (short)((float)(s1[b][s1po + s1bo[b]] & 0xFFFF) * t1 + (float)(s2[b][s2po + s2bo[b]] & 0xFFFF) * t2);
                    }
                    s1po += s1ps;
                    s2po += s2ps;
                    a1po += a1ps;
                    dpo += dps;
                }
                s1so += s1ss;
                s2so += s2ss;
                a1so += a1ss;
                dso += dss;
            }
        } else {
            for (int h = 0; h < dheight; ++h) {
                int s1po = s1so;
                int s2po = s2so;
                int a1po = a1so;
                int a2po = a2so;
                int dpo = dso;
                for (int w = 0; w < dwidth; ++w) {
                    float t5;
                    float t4;
                    int t1 = a1[a1po + a1bo] & 0xFFFF;
                    float t2 = (1.0f - (float)t1 * this.invMaxValue) * (float)(a2[a2po + a2bo] & 0xFFFF);
                    float t3 = (float)t1 + t2;
                    if (t3 == 0.0f) {
                        t4 = 0.0f;
                        t5 = 0.0f;
                    } else {
                        t4 = (float)t1 / t3;
                        t5 = t2 / t3;
                    }
                    d[this.aOffset][dpo + dbo[this.aOffset]] = (short)t3;
                    for (int b = 0; b < numBands; ++b) {
                        int i = b + this.cOffset;
                        d[i][dpo + dbo[i]] = (short)((float)(s1[b][s1po + s1bo[b]] & 0xFFFF) * t4 + (float)(s2[b][s2po + s2bo[b]] & 0xFFFF) * t5);
                    }
                    s1po += s1ps;
                    s2po += s2ps;
                    a1po += a1ps;
                    a2po += a2ps;
                    dpo += dps;
                }
                s1so += s1ss;
                s2so += s2ss;
                a1so += a1ss;
                a2so += a2ss;
                dso += dss;
            }
        }
    }

    private void shortLoop(RasterAccessor src1, RasterAccessor src2, RasterAccessor afa1, RasterAccessor afa2, RasterAccessor dst) {
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int numBands = src1.getNumBands();
        short[][] s1 = src1.getShortDataArrays();
        int s1ss = src1.getScanlineStride();
        int s1ps = src1.getPixelStride();
        int[] s1bo = src1.getBandOffsets();
        short[][] s2 = src2.getShortDataArrays();
        int s2ss = src2.getScanlineStride();
        int s2ps = src2.getPixelStride();
        int[] s2bo = src2.getBandOffsets();
        short[] a1 = afa1.getShortDataArray(0);
        int a1ss = afa1.getScanlineStride();
        int a1ps = afa1.getPixelStride();
        int a1bo = afa1.getBandOffset(0);
        short[] a2 = null;
        int a2ss = 0;
        int a2ps = 0;
        int a2bo = 0;
        if (afa2 != null) {
            a2 = afa2.getShortDataArray(0);
            a2ss = afa2.getScanlineStride();
            a2ps = afa2.getPixelStride();
            a2bo = afa2.getBandOffset(0);
        }
        short[][] d = dst.getShortDataArrays();
        int dss = dst.getScanlineStride();
        int dps = dst.getPixelStride();
        int[] dbo = dst.getBandOffsets();
        int s1so = 0;
        int s2so = 0;
        int a1so = 0;
        int a2so = 0;
        int dso = 0;
        if (this.alphaPremultiplied) {
            if (afa2 == null) {
                for (int h = 0; h < dheight; ++h) {
                    int s1po = s1so;
                    int s2po = s2so;
                    int a1po = a1so;
                    int dpo = dso;
                    for (int w = 0; w < dwidth; ++w) {
                        float t = 1.0f - (float)a1[a1po + a1bo] * this.invMaxValue;
                        d[this.aOffset][dpo + dbo[this.aOffset]] = this.maxValueShort;
                        for (int b = 0; b < numBands; ++b) {
                            int i = b + this.cOffset;
                            d[i][dpo + dbo[i]] = (short)((float)s1[b][s1po + s1bo[b]] + (float)s2[b][s2po + s2bo[b]] * t);
                        }
                        s1po += s1ps;
                        s2po += s2ps;
                        a1po += a1ps;
                        dpo += dps;
                    }
                    s1so += s1ss;
                    s2so += s2ss;
                    a1so += a1ss;
                    dso += dss;
                }
            } else {
                for (int h = 0; h < dheight; ++h) {
                    int s1po = s1so;
                    int s2po = s2so;
                    int a1po = a1so;
                    int a2po = a2so;
                    int dpo = dso;
                    for (int w = 0; w < dwidth; ++w) {
                        short t1 = a1[a1po + a1bo];
                        float t2 = 1.0f - (float)t1 * this.invMaxValue;
                        d[this.aOffset][dpo + dbo[this.aOffset]] = (short)((float)t1 + (float)a2[a2po + a2bo] * t2);
                        for (int b = 0; b < numBands; ++b) {
                            int i = b + this.cOffset;
                            d[i][dpo + dbo[i]] = (short)((float)s1[b][s1po + s1bo[b]] + (float)s2[b][s2po + s2bo[b]] * t2);
                        }
                        s1po += s1ps;
                        s2po += s2ps;
                        a1po += a1ps;
                        a2po += a2ps;
                        dpo += dps;
                    }
                    s1so += s1ss;
                    s2so += s2ss;
                    a1so += a1ss;
                    a2so += a2ss;
                    dso += dss;
                }
            }
        } else if (afa2 == null) {
            for (int h = 0; h < dheight; ++h) {
                int s1po = s1so;
                int s2po = s2so;
                int a1po = a1so;
                int dpo = dso;
                for (int w = 0; w < dwidth; ++w) {
                    float t1 = (float)a1[a1po + a1bo] * this.invMaxValue;
                    float t2 = 1.0f - t1;
                    d[this.aOffset][dpo + dbo[this.aOffset]] = this.maxValueShort;
                    for (int b = 0; b < numBands; ++b) {
                        int i = b + this.cOffset;
                        d[i][dpo + dbo[i]] = (short)((float)s1[b][s1po + s1bo[b]] * t1 + (float)s2[b][s2po + s2bo[b]] * t2);
                    }
                    s1po += s1ps;
                    s2po += s2ps;
                    a1po += a1ps;
                    dpo += dps;
                }
                s1so += s1ss;
                s2so += s2ss;
                a1so += a1ss;
                dso += dss;
            }
        } else {
            for (int h = 0; h < dheight; ++h) {
                int s1po = s1so;
                int s2po = s2so;
                int a1po = a1so;
                int a2po = a2so;
                int dpo = dso;
                for (int w = 0; w < dwidth; ++w) {
                    float t5;
                    float t4;
                    short t1 = a1[a1po + a1bo];
                    float t2 = (1.0f - (float)t1 * this.invMaxValue) * (float)a2[a2po + a2bo];
                    float t3 = (float)t1 + t2;
                    if (t3 == 0.0f) {
                        t4 = 0.0f;
                        t5 = 0.0f;
                    } else {
                        t4 = (float)t1 / t3;
                        t5 = t2 / t3;
                    }
                    d[this.aOffset][dpo + dbo[this.aOffset]] = (short)t3;
                    for (int b = 0; b < numBands; ++b) {
                        int i = b + this.cOffset;
                        d[i][dpo + dbo[i]] = (short)((float)s1[b][s1po + s1bo[b]] * t4 + (float)s2[b][s2po + s2bo[b]] * t5);
                    }
                    s1po += s1ps;
                    s2po += s2ps;
                    a1po += a1ps;
                    a2po += a2ps;
                    dpo += dps;
                }
                s1so += s1ss;
                s2so += s2ss;
                a1so += a1ss;
                a2so += a2ss;
                dso += dss;
            }
        }
    }

    private void intLoop(RasterAccessor src1, RasterAccessor src2, RasterAccessor afa1, RasterAccessor afa2, RasterAccessor dst) {
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int numBands = src1.getNumBands();
        int[][] s1 = src1.getIntDataArrays();
        int s1ss = src1.getScanlineStride();
        int s1ps = src1.getPixelStride();
        int[] s1bo = src1.getBandOffsets();
        int[][] s2 = src2.getIntDataArrays();
        int s2ss = src2.getScanlineStride();
        int s2ps = src2.getPixelStride();
        int[] s2bo = src2.getBandOffsets();
        int[] a1 = afa1.getIntDataArray(0);
        int a1ss = afa1.getScanlineStride();
        int a1ps = afa1.getPixelStride();
        int a1bo = afa1.getBandOffset(0);
        int[] a2 = null;
        int a2ss = 0;
        int a2ps = 0;
        int a2bo = 0;
        if (afa2 != null) {
            a2 = afa2.getIntDataArray(0);
            a2ss = afa2.getScanlineStride();
            a2ps = afa2.getPixelStride();
            a2bo = afa2.getBandOffset(0);
        }
        int[][] d = dst.getIntDataArrays();
        int dss = dst.getScanlineStride();
        int dps = dst.getPixelStride();
        int[] dbo = dst.getBandOffsets();
        int s1so = 0;
        int s2so = 0;
        int a1so = 0;
        int a2so = 0;
        int dso = 0;
        if (this.alphaPremultiplied) {
            if (afa2 == null) {
                for (int h = 0; h < dheight; ++h) {
                    int s1po = s1so;
                    int s2po = s2so;
                    int a1po = a1so;
                    int dpo = dso;
                    for (int w = 0; w < dwidth; ++w) {
                        float t = 1.0f - (float)a1[a1po + a1bo] * this.invMaxValue;
                        d[this.aOffset][dpo + dbo[this.aOffset]] = this.maxValue;
                        for (int b = 0; b < numBands; ++b) {
                            int i = b + this.cOffset;
                            d[i][dpo + dbo[i]] = (int)((float)s1[b][s1po + s1bo[b]] + (float)s2[b][s2po + s2bo[b]] * t);
                        }
                        s1po += s1ps;
                        s2po += s2ps;
                        a1po += a1ps;
                        dpo += dps;
                    }
                    s1so += s1ss;
                    s2so += s2ss;
                    a1so += a1ss;
                    dso += dss;
                }
            } else {
                for (int h = 0; h < dheight; ++h) {
                    int s1po = s1so;
                    int s2po = s2so;
                    int a1po = a1so;
                    int a2po = a2so;
                    int dpo = dso;
                    for (int w = 0; w < dwidth; ++w) {
                        int t1 = a1[a1po + a1bo];
                        float t2 = 1.0f - (float)t1 * this.invMaxValue;
                        d[this.aOffset][dpo + dbo[this.aOffset]] = (int)((float)t1 + (float)a2[a2po + a2bo] * t2);
                        for (int b = 0; b < numBands; ++b) {
                            int i = b + this.cOffset;
                            d[i][dpo + dbo[i]] = (int)((float)s1[b][s1po + s1bo[b]] + (float)s2[b][s2po + s2bo[b]] * t2);
                        }
                        s1po += s1ps;
                        s2po += s2ps;
                        a1po += a1ps;
                        a2po += a2ps;
                        dpo += dps;
                    }
                    s1so += s1ss;
                    s2so += s2ss;
                    a1so += a1ss;
                    a2so += a2ss;
                    dso += dss;
                }
            }
        } else if (afa2 == null) {
            for (int h = 0; h < dheight; ++h) {
                int s1po = s1so;
                int s2po = s2so;
                int a1po = a1so;
                int dpo = dso;
                for (int w = 0; w < dwidth; ++w) {
                    float t1 = (float)a1[a1po + a1bo] * this.invMaxValue;
                    float t2 = 1.0f - t1;
                    d[this.aOffset][dpo + dbo[this.aOffset]] = this.maxValue;
                    for (int b = 0; b < numBands; ++b) {
                        int i = b + this.cOffset;
                        d[i][dpo + dbo[i]] = (int)((float)s1[b][s1po + s1bo[b]] * t1 + (float)s2[b][s2po + s2bo[b]] * t2);
                    }
                    s1po += s1ps;
                    s2po += s2ps;
                    a1po += a1ps;
                    dpo += dps;
                }
                s1so += s1ss;
                s2so += s2ss;
                a1so += a1ss;
                dso += dss;
            }
        } else {
            for (int h = 0; h < dheight; ++h) {
                int s1po = s1so;
                int s2po = s2so;
                int a1po = a1so;
                int a2po = a2so;
                int dpo = dso;
                for (int w = 0; w < dwidth; ++w) {
                    float t5;
                    float t4;
                    int t1 = a1[a1po + a1bo];
                    float t2 = (1.0f - (float)t1 * this.invMaxValue) * (float)a2[a2po + a2bo];
                    float t3 = (float)t1 + t2;
                    if (t3 == 0.0f) {
                        t4 = 0.0f;
                        t5 = 0.0f;
                    } else {
                        t4 = (float)t1 / t3;
                        t5 = t2 / t3;
                    }
                    d[this.aOffset][dpo + dbo[this.aOffset]] = (int)t3;
                    for (int b = 0; b < numBands; ++b) {
                        int i = b + this.cOffset;
                        d[i][dpo + dbo[i]] = (int)((float)s1[b][s1po + s1bo[b]] * t4 + (float)s2[b][s2po + s2bo[b]] * t5);
                    }
                    s1po += s1ps;
                    s2po += s2ps;
                    a1po += a1ps;
                    a2po += a2ps;
                    dpo += dps;
                }
                s1so += s1ss;
                s2so += s2ss;
                a1so += a1ss;
                a2so += a2ss;
                dso += dss;
            }
        }
    }

    private void floatLoop(RasterAccessor src1, RasterAccessor src2, RasterAccessor afa1, RasterAccessor afa2, RasterAccessor dst) {
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int numBands = src1.getNumBands();
        float[][] s1 = src1.getFloatDataArrays();
        int s1ss = src1.getScanlineStride();
        int s1ps = src1.getPixelStride();
        int[] s1bo = src1.getBandOffsets();
        float[][] s2 = src2.getFloatDataArrays();
        int s2ss = src2.getScanlineStride();
        int s2ps = src2.getPixelStride();
        int[] s2bo = src2.getBandOffsets();
        float[] a1 = afa1.getFloatDataArray(0);
        int a1ss = afa1.getScanlineStride();
        int a1ps = afa1.getPixelStride();
        int a1bo = afa1.getBandOffset(0);
        float[] a2 = null;
        int a2ss = 0;
        int a2ps = 0;
        int a2bo = 0;
        if (afa2 != null) {
            a2 = afa2.getFloatDataArray(0);
            a2ss = afa2.getScanlineStride();
            a2ps = afa2.getPixelStride();
            a2bo = afa2.getBandOffset(0);
        }
        float[][] d = dst.getFloatDataArrays();
        int dss = dst.getScanlineStride();
        int dps = dst.getPixelStride();
        int[] dbo = dst.getBandOffsets();
        int s1so = 0;
        int s2so = 0;
        int a1so = 0;
        int a2so = 0;
        int dso = 0;
        float invMaxValue = 2.938736E-39f;
        if (this.alphaPremultiplied) {
            if (afa2 == null) {
                for (int h = 0; h < dheight; ++h) {
                    int s1po = s1so;
                    int s2po = s2so;
                    int a1po = a1so;
                    int dpo = dso;
                    for (int w = 0; w < dwidth; ++w) {
                        float t = 1.0f - a1[a1po + a1bo] * invMaxValue;
                        d[this.aOffset][dpo + dbo[this.aOffset]] = Float.MAX_VALUE;
                        for (int b = 0; b < numBands; ++b) {
                            int i = b + this.cOffset;
                            d[i][dpo + dbo[i]] = s1[b][s1po + s1bo[b]] + s2[b][s2po + s2bo[b]] * t;
                        }
                        s1po += s1ps;
                        s2po += s2ps;
                        a1po += a1ps;
                        dpo += dps;
                    }
                    s1so += s1ss;
                    s2so += s2ss;
                    a1so += a1ss;
                    dso += dss;
                }
            } else {
                for (int h = 0; h < dheight; ++h) {
                    int s1po = s1so;
                    int s2po = s2so;
                    int a1po = a1so;
                    int a2po = a2so;
                    int dpo = dso;
                    for (int w = 0; w < dwidth; ++w) {
                        float t1 = a1[a1po + a1bo];
                        float t2 = 1.0f - t1 * invMaxValue;
                        d[this.aOffset][dpo + dbo[this.aOffset]] = t1 + a2[a2po + a2bo] * t2;
                        for (int b = 0; b < numBands; ++b) {
                            int i = b + this.cOffset;
                            d[i][dpo + dbo[i]] = s1[b][s1po + s1bo[b]] + s2[b][s2po + s2bo[b]] * t2;
                        }
                        s1po += s1ps;
                        s2po += s2ps;
                        a1po += a1ps;
                        a2po += a2ps;
                        dpo += dps;
                    }
                    s1so += s1ss;
                    s2so += s2ss;
                    a1so += a1ss;
                    a2so += a2ss;
                    dso += dss;
                }
            }
        } else if (afa2 == null) {
            for (int h = 0; h < dheight; ++h) {
                int s1po = s1so;
                int s2po = s2so;
                int a1po = a1so;
                int dpo = dso;
                for (int w = 0; w < dwidth; ++w) {
                    float t1 = a1[a1po + a1bo] * invMaxValue;
                    float t2 = 1.0f - t1;
                    d[this.aOffset][dpo + dbo[this.aOffset]] = Float.MAX_VALUE;
                    for (int b = 0; b < numBands; ++b) {
                        int i = b + this.cOffset;
                        d[i][dpo + dbo[i]] = s1[b][s1po + s1bo[b]] * t1 + s2[b][s2po + s2bo[b]] * t2;
                    }
                    s1po += s1ps;
                    s2po += s2ps;
                    a1po += a1ps;
                    dpo += dps;
                }
                s1so += s1ss;
                s2so += s2ss;
                a1so += a1ss;
                dso += dss;
            }
        } else {
            for (int h = 0; h < dheight; ++h) {
                int s1po = s1so;
                int s2po = s2so;
                int a1po = a1so;
                int a2po = a2so;
                int dpo = dso;
                for (int w = 0; w < dwidth; ++w) {
                    float t5;
                    float t4;
                    float t1 = a1[a1po + a1bo];
                    float t2 = (1.0f - t1 * invMaxValue) * a2[a2po + a2bo];
                    float t3 = t1 + t2;
                    if (t3 == 0.0f) {
                        t4 = 0.0f;
                        t5 = 0.0f;
                    } else {
                        t4 = t1 / t3;
                        t5 = t2 / t3;
                    }
                    d[this.aOffset][dpo + dbo[this.aOffset]] = t3;
                    for (int b = 0; b < numBands; ++b) {
                        int i = b + this.cOffset;
                        d[i][dpo + dbo[i]] = s1[b][s1po + s1bo[b]] * t4 + s2[b][s2po + s2bo[b]] * t5;
                    }
                    s1po += s1ps;
                    s2po += s2ps;
                    a1po += a1ps;
                    a2po += a2ps;
                    dpo += dps;
                }
                s1so += s1ss;
                s2so += s2ss;
                a1so += a1ss;
                a2so += a2ss;
                dso += dss;
            }
        }
    }

    private void doubleLoop(RasterAccessor src1, RasterAccessor src2, RasterAccessor afa1, RasterAccessor afa2, RasterAccessor dst) {
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int numBands = src1.getNumBands();
        double[][] s1 = src1.getDoubleDataArrays();
        int s1ss = src1.getScanlineStride();
        int s1ps = src1.getPixelStride();
        int[] s1bo = src1.getBandOffsets();
        double[][] s2 = src2.getDoubleDataArrays();
        int s2ss = src2.getScanlineStride();
        int s2ps = src2.getPixelStride();
        int[] s2bo = src2.getBandOffsets();
        double[] a1 = afa1.getDoubleDataArray(0);
        int a1ss = afa1.getScanlineStride();
        int a1ps = afa1.getPixelStride();
        int a1bo = afa1.getBandOffset(0);
        double[] a2 = null;
        int a2ss = 0;
        int a2ps = 0;
        int a2bo = 0;
        if (afa2 != null) {
            a2 = afa2.getDoubleDataArray(0);
            a2ss = afa2.getScanlineStride();
            a2ps = afa2.getPixelStride();
            a2bo = afa2.getBandOffset(0);
        }
        double[][] d = dst.getDoubleDataArrays();
        int dss = dst.getScanlineStride();
        int dps = dst.getPixelStride();
        int[] dbo = dst.getBandOffsets();
        int s1so = 0;
        int s2so = 0;
        int a1so = 0;
        int a2so = 0;
        int dso = 0;
        double invMaxValue = 5.562684646268003E-309;
        if (this.alphaPremultiplied) {
            if (afa2 == null) {
                for (int h = 0; h < dheight; ++h) {
                    int s1po = s1so;
                    int s2po = s2so;
                    int a1po = a1so;
                    int dpo = dso;
                    for (int w = 0; w < dwidth; ++w) {
                        double t = 1.0 - a1[a1po + a1bo] * invMaxValue;
                        d[this.aOffset][dpo + dbo[this.aOffset]] = Double.MAX_VALUE;
                        for (int b = 0; b < numBands; ++b) {
                            int i = b + this.cOffset;
                            d[i][dpo + dbo[i]] = s1[b][s1po + s1bo[b]] + s2[b][s2po + s2bo[b]] * t;
                        }
                        s1po += s1ps;
                        s2po += s2ps;
                        a1po += a1ps;
                        dpo += dps;
                    }
                    s1so += s1ss;
                    s2so += s2ss;
                    a1so += a1ss;
                    dso += dss;
                }
            } else {
                for (int h = 0; h < dheight; ++h) {
                    int s1po = s1so;
                    int s2po = s2so;
                    int a1po = a1so;
                    int a2po = a2so;
                    int dpo = dso;
                    for (int w = 0; w < dwidth; ++w) {
                        double t1 = a1[a1po + a1bo];
                        double t2 = 1.0 - t1 * invMaxValue;
                        d[this.aOffset][dpo + dbo[this.aOffset]] = t1 + a2[a2po + a2bo] * t2;
                        for (int b = 0; b < numBands; ++b) {
                            int i = b + this.cOffset;
                            d[i][dpo + dbo[i]] = s1[b][s1po + s1bo[b]] + s2[b][s2po + s2bo[b]] * t2;
                        }
                        s1po += s1ps;
                        s2po += s2ps;
                        a1po += a1ps;
                        a2po += a2ps;
                        dpo += dps;
                    }
                    s1so += s1ss;
                    s2so += s2ss;
                    a1so += a1ss;
                    a2so += a2ss;
                    dso += dss;
                }
            }
        } else if (afa2 == null) {
            for (int h = 0; h < dheight; ++h) {
                int s1po = s1so;
                int s2po = s2so;
                int a1po = a1so;
                int dpo = dso;
                for (int w = 0; w < dwidth; ++w) {
                    double t1 = a1[a1po + a1bo] * invMaxValue;
                    double t2 = 1.0 - t1;
                    d[this.aOffset][dpo + dbo[this.aOffset]] = Double.MAX_VALUE;
                    for (int b = 0; b < numBands; ++b) {
                        int i = b + this.cOffset;
                        d[i][dpo + dbo[i]] = s1[b][s1po + s1bo[b]] * t1 + s2[b][s2po + s2bo[b]] * t2;
                    }
                    s1po += s1ps;
                    s2po += s2ps;
                    a1po += a1ps;
                    dpo += dps;
                }
                s1so += s1ss;
                s2so += s2ss;
                a1so += a1ss;
                dso += dss;
            }
        } else {
            for (int h = 0; h < dheight; ++h) {
                int s1po = s1so;
                int s2po = s2so;
                int a1po = a1so;
                int a2po = a2so;
                int dpo = dso;
                for (int w = 0; w < dwidth; ++w) {
                    double t5;
                    double t4;
                    double t1 = a1[a1po + a1bo];
                    double t2 = (1.0 - t1 * invMaxValue) * a2[a2po + a2bo];
                    double t3 = t1 + t2;
                    if (t3 == 0.0) {
                        t4 = 0.0;
                        t5 = 0.0;
                    } else {
                        t4 = t1 / t3;
                        t5 = t2 / t3;
                    }
                    d[this.aOffset][dpo + dbo[this.aOffset]] = t3;
                    for (int b = 0; b < numBands; ++b) {
                        int i = b + this.cOffset;
                        d[i][dpo + dbo[i]] = s1[b][s1po + s1bo[b]] * t4 + s2[b][s2po + s2bo[b]] * t5;
                    }
                    s1po += s1ps;
                    s2po += s2ps;
                    a1po += a1ps;
                    a2po += a2ps;
                    dpo += dps;
                }
                s1so += s1ss;
                s2so += s2ss;
                a1so += a1ss;
                a2so += a2ss;
                dso += dss;
            }
        }
    }
}

