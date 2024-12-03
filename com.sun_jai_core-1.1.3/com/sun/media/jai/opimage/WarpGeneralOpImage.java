/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.util.ImageUtil;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.Interpolation;
import javax.media.jai.PlanarImage;
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFormatTag;
import javax.media.jai.Warp;
import javax.media.jai.WarpOpImage;
import javax.media.jai.iterator.RandomIter;
import javax.media.jai.iterator.RandomIterFactory;

final class WarpGeneralOpImage
extends WarpOpImage {
    private byte[][] ctable = null;

    public WarpGeneralOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, Warp warp, Interpolation interp, double[] backgroundValues) {
        super(source, layout, config, false, extender, interp, warp, backgroundValues);
        ColorModel srcColorModel = source.getColorModel();
        if (srcColorModel instanceof IndexColorModel) {
            IndexColorModel icm = (IndexColorModel)srcColorModel;
            this.ctable = new byte[3][icm.getMapSize()];
            icm.getReds(this.ctable[0]);
            icm.getGreens(this.ctable[1]);
            icm.getBlues(this.ctable[2]);
        }
    }

    protected void computeRect(PlanarImage[] sources, WritableRaster dest, Rectangle destRect) {
        RasterFormatTag[] formatTags = this.getFormatTags();
        RasterAccessor d = new RasterAccessor(dest, destRect, formatTags[1], this.getColorModel());
        switch (d.getDataType()) {
            case 0: {
                this.computeRectByte(sources[0], d);
                break;
            }
            case 1: {
                this.computeRectUShort(sources[0], d);
                break;
            }
            case 2: {
                this.computeRectShort(sources[0], d);
                break;
            }
            case 3: {
                this.computeRectInt(sources[0], d);
                break;
            }
            case 4: {
                this.computeRectFloat(sources[0], d);
                break;
            }
            case 5: {
                this.computeRectDouble(sources[0], d);
            }
        }
        if (d.isDataCopy()) {
            d.clampDataArrays();
            d.copyDataToRaster();
        }
    }

    private void computeRectByte(PlanarImage src, RasterAccessor dst) {
        int h;
        RandomIter iter;
        int maxY;
        int minY;
        int maxX;
        int minX;
        int bpad;
        int tpad;
        int rpad;
        int lpad;
        if (this.interp != null) {
            lpad = this.interp.getLeftPadding();
            rpad = this.interp.getRightPadding();
            tpad = this.interp.getTopPadding();
            bpad = this.interp.getBottomPadding();
        } else {
            bpad = 0;
            tpad = 0;
            rpad = 0;
            lpad = 0;
        }
        if (this.extender != null) {
            minX = src.getMinX();
            maxX = src.getMaxX();
            minY = src.getMinY();
            maxY = src.getMaxY();
            Rectangle bounds = new Rectangle(src.getMinX() - lpad, src.getMinY() - tpad, src.getWidth() + lpad + rpad, src.getHeight() + tpad + bpad);
            iter = RandomIterFactory.create(src.getExtendedData(bounds, this.extender), bounds);
        } else {
            minX = src.getMinX() + lpad;
            maxX = src.getMaxX() - rpad;
            minY = src.getMinY() + tpad;
            maxY = src.getMaxY() - bpad;
            iter = RandomIterFactory.create(src, src.getBounds());
        }
        int kwidth = this.interp.getWidth();
        int kheight = this.interp.getHeight();
        int dstWidth = dst.getWidth();
        int dstHeight = dst.getHeight();
        int dstBands = dst.getNumBands();
        int lineStride = dst.getScanlineStride();
        int pixelStride = dst.getPixelStride();
        int[] bandOffsets = dst.getBandOffsets();
        byte[][] data = dst.getByteDataArrays();
        int precH = 1 << this.interp.getSubsampleBitsH();
        int precV = 1 << this.interp.getSubsampleBitsV();
        float[] warpData = new float[2 * dstWidth];
        int[][] samples = new int[kheight][kwidth];
        int lineOffset = 0;
        byte[] backgroundByte = new byte[dstBands];
        for (int i = 0; i < dstBands; ++i) {
            backgroundByte[i] = (byte)this.backgroundValues[i];
        }
        if (this.ctable == null) {
            for (h = 0; h < dstHeight; ++h) {
                int pixelOffset = lineOffset;
                lineOffset += lineStride;
                this.warp.warpRect(dst.getX(), dst.getY() + h, dstWidth, 1, warpData);
                int count = 0;
                for (int w = 0; w < dstWidth; ++w) {
                    int b;
                    float sx = warpData[count++];
                    float sy = warpData[count++];
                    int xint = WarpGeneralOpImage.floor(sx);
                    int yint = WarpGeneralOpImage.floor(sy);
                    int xfrac = (int)((sx - (float)xint) * (float)precH);
                    int yfrac = (int)((sy - (float)yint) * (float)precV);
                    if (xint < minX || xint >= maxX || yint < minY || yint >= maxY) {
                        if (this.setBackground) {
                            for (b = 0; b < dstBands; ++b) {
                                data[b][pixelOffset + bandOffsets[b]] = backgroundByte[b];
                            }
                        }
                    } else {
                        xint -= lpad;
                        yint -= tpad;
                        for (b = 0; b < dstBands; ++b) {
                            for (int j = 0; j < kheight; ++j) {
                                for (int i = 0; i < kwidth; ++i) {
                                    samples[j][i] = iter.getSample(xint + i, yint + j, b) & 0xFF;
                                }
                            }
                            data[b][pixelOffset + bandOffsets[b]] = ImageUtil.clampByte(this.interp.interpolate(samples, xfrac, yfrac));
                        }
                    }
                    pixelOffset += pixelStride;
                }
            }
        } else {
            for (h = 0; h < dstHeight; ++h) {
                int pixelOffset = lineOffset;
                lineOffset += lineStride;
                this.warp.warpRect(dst.getX(), dst.getY() + h, dstWidth, 1, warpData);
                int count = 0;
                for (int w = 0; w < dstWidth; ++w) {
                    int b;
                    float sx = warpData[count++];
                    float sy = warpData[count++];
                    int xint = WarpGeneralOpImage.floor(sx);
                    int yint = WarpGeneralOpImage.floor(sy);
                    int xfrac = (int)((sx - (float)xint) * (float)precH);
                    int yfrac = (int)((sy - (float)yint) * (float)precV);
                    if (xint < minX || xint >= maxX || yint < minY || yint >= maxY) {
                        if (this.setBackground) {
                            for (b = 0; b < dstBands; ++b) {
                                data[b][pixelOffset + bandOffsets[b]] = backgroundByte[b];
                            }
                        }
                    } else {
                        xint -= lpad;
                        yint -= tpad;
                        for (b = 0; b < dstBands; ++b) {
                            byte[] t = this.ctable[b];
                            for (int j = 0; j < kheight; ++j) {
                                for (int i = 0; i < kwidth; ++i) {
                                    samples[j][i] = t[iter.getSample(xint + i, yint + j, 0) & 0xFF] & 0xFF;
                                }
                            }
                            data[b][pixelOffset + bandOffsets[b]] = ImageUtil.clampByte(this.interp.interpolate(samples, xfrac, yfrac));
                        }
                    }
                    pixelOffset += pixelStride;
                }
            }
        }
    }

    private void computeRectUShort(PlanarImage src, RasterAccessor dst) {
        RandomIter iter;
        int maxY;
        int minY;
        int maxX;
        int minX;
        int bpad;
        int tpad;
        int rpad;
        int lpad;
        if (this.interp != null) {
            lpad = this.interp.getLeftPadding();
            rpad = this.interp.getRightPadding();
            tpad = this.interp.getTopPadding();
            bpad = this.interp.getBottomPadding();
        } else {
            bpad = 0;
            tpad = 0;
            rpad = 0;
            lpad = 0;
        }
        if (this.extender != null) {
            minX = src.getMinX();
            maxX = src.getMaxX();
            minY = src.getMinY();
            maxY = src.getMaxY();
            Rectangle bounds = new Rectangle(src.getMinX() - lpad, src.getMinY() - tpad, src.getWidth() + lpad + rpad, src.getHeight() + tpad + bpad);
            iter = RandomIterFactory.create(src.getExtendedData(bounds, this.extender), bounds);
        } else {
            minX = src.getMinX() + lpad;
            maxX = src.getMaxX() - rpad;
            minY = src.getMinY() + tpad;
            maxY = src.getMaxY() - bpad;
            iter = RandomIterFactory.create(src, src.getBounds());
        }
        int kwidth = this.interp.getWidth();
        int kheight = this.interp.getHeight();
        int dstWidth = dst.getWidth();
        int dstHeight = dst.getHeight();
        int dstBands = dst.getNumBands();
        int lineStride = dst.getScanlineStride();
        int pixelStride = dst.getPixelStride();
        int[] bandOffsets = dst.getBandOffsets();
        short[][] data = dst.getShortDataArrays();
        int precH = 1 << this.interp.getSubsampleBitsH();
        int precV = 1 << this.interp.getSubsampleBitsV();
        float[] warpData = new float[2 * dstWidth];
        int[][] samples = new int[kheight][kwidth];
        int lineOffset = 0;
        short[] backgroundUShort = new short[dstBands];
        for (int i = 0; i < dstBands; ++i) {
            backgroundUShort[i] = (short)this.backgroundValues[i];
        }
        for (int h = 0; h < dstHeight; ++h) {
            int pixelOffset = lineOffset;
            lineOffset += lineStride;
            this.warp.warpRect(dst.getX(), dst.getY() + h, dstWidth, 1, warpData);
            int count = 0;
            for (int w = 0; w < dstWidth; ++w) {
                int b;
                float sx = warpData[count++];
                float sy = warpData[count++];
                int xint = WarpGeneralOpImage.floor(sx);
                int yint = WarpGeneralOpImage.floor(sy);
                int xfrac = (int)((sx - (float)xint) * (float)precH);
                int yfrac = (int)((sy - (float)yint) * (float)precV);
                if (xint < minX || xint >= maxX || yint < minY || yint >= maxY) {
                    if (this.setBackground) {
                        for (b = 0; b < dstBands; ++b) {
                            data[b][pixelOffset + bandOffsets[b]] = backgroundUShort[b];
                        }
                    }
                } else {
                    xint -= lpad;
                    yint -= tpad;
                    for (b = 0; b < dstBands; ++b) {
                        for (int j = 0; j < kheight; ++j) {
                            for (int i = 0; i < kwidth; ++i) {
                                samples[j][i] = iter.getSample(xint + i, yint + j, b) & 0xFFFF;
                            }
                        }
                        data[b][pixelOffset + bandOffsets[b]] = ImageUtil.clampUShort(this.interp.interpolate(samples, xfrac, yfrac));
                    }
                }
                pixelOffset += pixelStride;
            }
        }
    }

    private void computeRectShort(PlanarImage src, RasterAccessor dst) {
        RandomIter iter;
        int maxY;
        int minY;
        int maxX;
        int minX;
        int bpad;
        int tpad;
        int rpad;
        int lpad;
        if (this.interp != null) {
            lpad = this.interp.getLeftPadding();
            rpad = this.interp.getRightPadding();
            tpad = this.interp.getTopPadding();
            bpad = this.interp.getBottomPadding();
        } else {
            bpad = 0;
            tpad = 0;
            rpad = 0;
            lpad = 0;
        }
        if (this.extender != null) {
            minX = src.getMinX();
            maxX = src.getMaxX();
            minY = src.getMinY();
            maxY = src.getMaxY();
            Rectangle bounds = new Rectangle(src.getMinX() - lpad, src.getMinY() - tpad, src.getWidth() + lpad + rpad, src.getHeight() + tpad + bpad);
            iter = RandomIterFactory.create(src.getExtendedData(bounds, this.extender), bounds);
        } else {
            minX = src.getMinX() + lpad;
            maxX = src.getMaxX() - rpad;
            minY = src.getMinY() + tpad;
            maxY = src.getMaxY() - bpad;
            iter = RandomIterFactory.create(src, src.getBounds());
        }
        int kwidth = this.interp.getWidth();
        int kheight = this.interp.getHeight();
        int dstWidth = dst.getWidth();
        int dstHeight = dst.getHeight();
        int dstBands = dst.getNumBands();
        int lineStride = dst.getScanlineStride();
        int pixelStride = dst.getPixelStride();
        int[] bandOffsets = dst.getBandOffsets();
        short[][] data = dst.getShortDataArrays();
        int precH = 1 << this.interp.getSubsampleBitsH();
        int precV = 1 << this.interp.getSubsampleBitsV();
        float[] warpData = new float[2 * dstWidth];
        int[][] samples = new int[kheight][kwidth];
        int lineOffset = 0;
        short[] backgroundShort = new short[dstBands];
        for (int i = 0; i < dstBands; ++i) {
            backgroundShort[i] = (short)this.backgroundValues[i];
        }
        for (int h = 0; h < dstHeight; ++h) {
            int pixelOffset = lineOffset;
            lineOffset += lineStride;
            this.warp.warpRect(dst.getX(), dst.getY() + h, dstWidth, 1, warpData);
            int count = 0;
            for (int w = 0; w < dstWidth; ++w) {
                int b;
                float sx = warpData[count++];
                float sy = warpData[count++];
                int xint = WarpGeneralOpImage.floor(sx);
                int yint = WarpGeneralOpImage.floor(sy);
                int xfrac = (int)((sx - (float)xint) * (float)precH);
                int yfrac = (int)((sy - (float)yint) * (float)precV);
                if (xint < minX || xint >= maxX || yint < minY || yint >= maxY) {
                    if (this.setBackground) {
                        for (b = 0; b < dstBands; ++b) {
                            data[b][pixelOffset + bandOffsets[b]] = backgroundShort[b];
                        }
                    }
                } else {
                    xint -= lpad;
                    yint -= tpad;
                    for (b = 0; b < dstBands; ++b) {
                        for (int j = 0; j < kheight; ++j) {
                            for (int i = 0; i < kwidth; ++i) {
                                samples[j][i] = iter.getSample(xint + i, yint + j, b);
                            }
                        }
                        data[b][pixelOffset + bandOffsets[b]] = ImageUtil.clampShort(this.interp.interpolate(samples, xfrac, yfrac));
                    }
                }
                pixelOffset += pixelStride;
            }
        }
    }

    private void computeRectInt(PlanarImage src, RasterAccessor dst) {
        RandomIter iter;
        int maxY;
        int minY;
        int maxX;
        int minX;
        int bpad;
        int tpad;
        int rpad;
        int lpad;
        if (this.interp != null) {
            lpad = this.interp.getLeftPadding();
            rpad = this.interp.getRightPadding();
            tpad = this.interp.getTopPadding();
            bpad = this.interp.getBottomPadding();
        } else {
            bpad = 0;
            tpad = 0;
            rpad = 0;
            lpad = 0;
        }
        if (this.extender != null) {
            minX = src.getMinX();
            maxX = src.getMaxX();
            minY = src.getMinY();
            maxY = src.getMaxY();
            Rectangle bounds = new Rectangle(src.getMinX() - lpad, src.getMinY() - tpad, src.getWidth() + lpad + rpad, src.getHeight() + tpad + bpad);
            iter = RandomIterFactory.create(src.getExtendedData(bounds, this.extender), bounds);
        } else {
            minX = src.getMinX() + lpad;
            maxX = src.getMaxX() - rpad;
            minY = src.getMinY() + tpad;
            maxY = src.getMaxY() - bpad;
            iter = RandomIterFactory.create(src, src.getBounds());
        }
        int kwidth = this.interp.getWidth();
        int kheight = this.interp.getHeight();
        int dstWidth = dst.getWidth();
        int dstHeight = dst.getHeight();
        int dstBands = dst.getNumBands();
        int lineStride = dst.getScanlineStride();
        int pixelStride = dst.getPixelStride();
        int[] bandOffsets = dst.getBandOffsets();
        int[][] data = dst.getIntDataArrays();
        int precH = 1 << this.interp.getSubsampleBitsH();
        int precV = 1 << this.interp.getSubsampleBitsV();
        float[] warpData = new float[2 * dstWidth];
        int[][] samples = new int[kheight][kwidth];
        int lineOffset = 0;
        int[] backgroundInt = new int[dstBands];
        for (int i = 0; i < dstBands; ++i) {
            backgroundInt[i] = (int)this.backgroundValues[i];
        }
        for (int h = 0; h < dstHeight; ++h) {
            int pixelOffset = lineOffset;
            lineOffset += lineStride;
            this.warp.warpRect(dst.getX(), dst.getY() + h, dstWidth, 1, warpData);
            int count = 0;
            for (int w = 0; w < dstWidth; ++w) {
                int b;
                float sx = warpData[count++];
                float sy = warpData[count++];
                int xint = WarpGeneralOpImage.floor(sx);
                int yint = WarpGeneralOpImage.floor(sy);
                int xfrac = (int)((sx - (float)xint) * (float)precH);
                int yfrac = (int)((sy - (float)yint) * (float)precV);
                if (xint < minX || xint >= maxX || yint < minY || yint >= maxY) {
                    if (this.setBackground) {
                        for (b = 0; b < dstBands; ++b) {
                            data[b][pixelOffset + bandOffsets[b]] = backgroundInt[b];
                        }
                    }
                } else {
                    xint -= lpad;
                    yint -= tpad;
                    for (b = 0; b < dstBands; ++b) {
                        for (int j = 0; j < kheight; ++j) {
                            for (int i = 0; i < kwidth; ++i) {
                                samples[j][i] = iter.getSample(xint + i, yint + j, b);
                            }
                        }
                        data[b][pixelOffset + bandOffsets[b]] = this.interp.interpolate(samples, xfrac, yfrac);
                    }
                }
                pixelOffset += pixelStride;
            }
        }
    }

    private void computeRectFloat(PlanarImage src, RasterAccessor dst) {
        RandomIter iter;
        int maxY;
        int minY;
        int maxX;
        int minX;
        int bpad;
        int tpad;
        int rpad;
        int lpad;
        if (this.interp != null) {
            lpad = this.interp.getLeftPadding();
            rpad = this.interp.getRightPadding();
            tpad = this.interp.getTopPadding();
            bpad = this.interp.getBottomPadding();
        } else {
            bpad = 0;
            tpad = 0;
            rpad = 0;
            lpad = 0;
        }
        if (this.extender != null) {
            minX = src.getMinX();
            maxX = src.getMaxX();
            minY = src.getMinY();
            maxY = src.getMaxY();
            Rectangle bounds = new Rectangle(src.getMinX() - lpad, src.getMinY() - tpad, src.getWidth() + lpad + rpad, src.getHeight() + tpad + bpad);
            iter = RandomIterFactory.create(src.getExtendedData(bounds, this.extender), bounds);
        } else {
            minX = src.getMinX() + lpad;
            maxX = src.getMaxX() - rpad;
            minY = src.getMinY() + tpad;
            maxY = src.getMaxY() - bpad;
            iter = RandomIterFactory.create(src, src.getBounds());
        }
        int kwidth = this.interp.getWidth();
        int kheight = this.interp.getHeight();
        int dstWidth = dst.getWidth();
        int dstHeight = dst.getHeight();
        int dstBands = dst.getNumBands();
        int lineStride = dst.getScanlineStride();
        int pixelStride = dst.getPixelStride();
        int[] bandOffsets = dst.getBandOffsets();
        float[][] data = dst.getFloatDataArrays();
        float[] warpData = new float[2 * dstWidth];
        float[][] samples = new float[kheight][kwidth];
        int lineOffset = 0;
        float[] backgroundFloat = new float[dstBands];
        for (int i = 0; i < dstBands; ++i) {
            backgroundFloat[i] = (float)this.backgroundValues[i];
        }
        for (int h = 0; h < dstHeight; ++h) {
            int pixelOffset = lineOffset;
            lineOffset += lineStride;
            this.warp.warpRect(dst.getX(), dst.getY() + h, dstWidth, 1, warpData);
            int count = 0;
            for (int w = 0; w < dstWidth; ++w) {
                int b;
                float sx = warpData[count++];
                float sy = warpData[count++];
                int xint = WarpGeneralOpImage.floor(sx);
                int yint = WarpGeneralOpImage.floor(sy);
                float xfrac = sx - (float)xint;
                float yfrac = sy - (float)yint;
                if (xint < minX || xint >= maxX || yint < minY || yint >= maxY) {
                    if (this.setBackground) {
                        for (b = 0; b < dstBands; ++b) {
                            data[b][pixelOffset + bandOffsets[b]] = backgroundFloat[b];
                        }
                    }
                } else {
                    xint -= lpad;
                    yint -= tpad;
                    for (b = 0; b < dstBands; ++b) {
                        for (int j = 0; j < kheight; ++j) {
                            for (int i = 0; i < kwidth; ++i) {
                                samples[j][i] = iter.getSampleFloat(xint + i, yint + j, b);
                            }
                        }
                        data[b][pixelOffset + bandOffsets[b]] = this.interp.interpolate(samples, xfrac, yfrac);
                    }
                }
                pixelOffset += pixelStride;
            }
        }
    }

    private void computeRectDouble(PlanarImage src, RasterAccessor dst) {
        RandomIter iter;
        int maxY;
        int minY;
        int maxX;
        int minX;
        int bpad;
        int tpad;
        int rpad;
        int lpad;
        if (this.interp != null) {
            lpad = this.interp.getLeftPadding();
            rpad = this.interp.getRightPadding();
            tpad = this.interp.getTopPadding();
            bpad = this.interp.getBottomPadding();
        } else {
            bpad = 0;
            tpad = 0;
            rpad = 0;
            lpad = 0;
        }
        if (this.extender != null) {
            minX = src.getMinX();
            maxX = src.getMaxX();
            minY = src.getMinY();
            maxY = src.getMaxY();
            Rectangle bounds = new Rectangle(src.getMinX() - lpad, src.getMinY() - tpad, src.getWidth() + lpad + rpad, src.getHeight() + tpad + bpad);
            iter = RandomIterFactory.create(src.getExtendedData(bounds, this.extender), bounds);
        } else {
            minX = src.getMinX() + lpad;
            maxX = src.getMaxX() - rpad;
            minY = src.getMinY() + tpad;
            maxY = src.getMaxY() - bpad;
            iter = RandomIterFactory.create(src, src.getBounds());
        }
        int kwidth = this.interp.getWidth();
        int kheight = this.interp.getHeight();
        int dstWidth = dst.getWidth();
        int dstHeight = dst.getHeight();
        int dstBands = dst.getNumBands();
        int lineStride = dst.getScanlineStride();
        int pixelStride = dst.getPixelStride();
        int[] bandOffsets = dst.getBandOffsets();
        double[][] data = dst.getDoubleDataArrays();
        float[] warpData = new float[2 * dstWidth];
        double[][] samples = new double[kheight][kwidth];
        int lineOffset = 0;
        for (int h = 0; h < dstHeight; ++h) {
            int pixelOffset = lineOffset;
            lineOffset += lineStride;
            this.warp.warpRect(dst.getX(), dst.getY() + h, dstWidth, 1, warpData);
            int count = 0;
            for (int w = 0; w < dstWidth; ++w) {
                int b;
                float sx = warpData[count++];
                float sy = warpData[count++];
                int xint = WarpGeneralOpImage.floor(sx);
                int yint = WarpGeneralOpImage.floor(sy);
                float xfrac = sx - (float)xint;
                float yfrac = sy - (float)yint;
                if (xint < minX || xint >= maxX || yint < minY || yint >= maxY) {
                    if (this.setBackground) {
                        for (b = 0; b < dstBands; ++b) {
                            data[b][pixelOffset + bandOffsets[b]] = this.backgroundValues[b];
                        }
                    }
                } else {
                    xint -= lpad;
                    yint -= tpad;
                    for (b = 0; b < dstBands; ++b) {
                        for (int j = 0; j < kheight; ++j) {
                            for (int i = 0; i < kwidth; ++i) {
                                samples[j][i] = iter.getSampleDouble(xint + i, yint + j, b);
                            }
                        }
                        data[b][pixelOffset + bandOffsets[b]] = this.interp.interpolate(samples, xfrac, yfrac);
                    }
                }
                pixelOffset += pixelStride;
            }
        }
    }

    private static final int floor(float f) {
        return f >= 0.0f ? (int)f : (int)f - 1;
    }
}

