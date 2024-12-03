/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.AffineNearestOpImage;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferUShort;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.Interpolation;
import javax.media.jai.JAI;
import javax.media.jai.util.Range;

final class AffineNearestBinaryOpImage
extends AffineNearestOpImage {
    private int black = 0;

    private static Map configHelper(Map configuration) {
        Map config;
        if (configuration == null) {
            config = new RenderingHints(JAI.KEY_REPLACE_INDEX_COLOR_MODEL, Boolean.FALSE);
        } else {
            config = configuration;
            if (!config.containsKey(JAI.KEY_REPLACE_INDEX_COLOR_MODEL)) {
                RenderingHints hints = (RenderingHints)configuration;
                config = (RenderingHints)hints.clone();
                config.put(JAI.KEY_REPLACE_INDEX_COLOR_MODEL, Boolean.FALSE);
            }
        }
        return config;
    }

    public AffineNearestBinaryOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, AffineTransform transform, Interpolation interp, double[] backgroundValues) {
        super(source, extender, AffineNearestBinaryOpImage.configHelper(config), layout, transform, interp, backgroundValues);
        this.colorModel = layout != null ? layout.getColorModel(source) : source.getColorModel();
        this.sampleModel = source.getSampleModel().createCompatibleSampleModel(this.tileWidth, this.tileHeight);
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        Raster source = sources[0];
        switch (source.getSampleModel().getDataType()) {
            case 0: {
                this.byteLoop(source, dest, destRect);
                break;
            }
            case 3: {
                this.intLoop(source, dest, destRect);
                break;
            }
            case 1: 
            case 2: {
                this.shortLoop(source, dest, destRect);
            }
        }
    }

    private void byteLoop(Raster source, WritableRaster dest, Rectangle destRect) {
        float src_rect_x1 = source.getMinX();
        float src_rect_y1 = source.getMinY();
        float src_rect_x2 = src_rect_x1 + (float)source.getWidth();
        float src_rect_y2 = src_rect_y1 + (float)source.getHeight();
        MultiPixelPackedSampleModel sourceSM = (MultiPixelPackedSampleModel)source.getSampleModel();
        DataBufferByte sourceDB = (DataBufferByte)source.getDataBuffer();
        int sourceTransX = source.getSampleModelTranslateX();
        int sourceTransY = source.getSampleModelTranslateY();
        int sourceDataBitOffset = sourceSM.getDataBitOffset();
        int sourceScanlineStride = sourceSM.getScanlineStride();
        MultiPixelPackedSampleModel destSM = (MultiPixelPackedSampleModel)dest.getSampleModel();
        DataBufferByte destDB = (DataBufferByte)dest.getDataBuffer();
        int destMinX = dest.getMinX();
        int destMinY = dest.getMinY();
        int destTransX = dest.getSampleModelTranslateX();
        int destTransY = dest.getSampleModelTranslateY();
        int destDataBitOffset = destSM.getDataBitOffset();
        int destScanlineStride = destSM.getScanlineStride();
        byte[] sourceData = sourceDB.getData();
        int sourceDBOffset = sourceDB.getOffset();
        byte[] destData = destDB.getData();
        int destDBOffset = destDB.getOffset();
        Point2D.Float dst_pt = new Point2D.Float();
        Point2D.Float src_pt = new Point2D.Float();
        int dst_min_x = destRect.x;
        int dst_min_y = destRect.y;
        int dst_max_x = destRect.x + destRect.width;
        int dst_max_y = destRect.y + destRect.height;
        int incyStride = this.incy * sourceScanlineStride;
        int incy1Stride = this.incy1 * sourceScanlineStride;
        this.black = (int)this.backgroundValues[0] & 1;
        for (int y = dst_min_y; y < dst_max_y; ++y) {
            int delement;
            int dshift;
            int dindex;
            int x;
            ((Point2D)dst_pt).setLocation((double)dst_min_x + 0.5, (double)y + 0.5);
            this.mapDestPoint((Point2D)dst_pt, src_pt);
            float s_x = (float)((Point2D)src_pt).getX();
            float s_y = (float)((Point2D)src_pt).getY();
            int s_ix = (int)Math.floor(s_x);
            int s_iy = (int)Math.floor(s_y);
            double fracx = (double)s_x - (double)s_ix;
            double fracy = (double)s_y - (double)s_iy;
            int ifracx = (int)Math.floor(fracx * 1048576.0);
            int ifracy = (int)Math.floor(fracy * 1048576.0);
            int start_s_ix = s_ix;
            int start_s_iy = s_iy;
            int start_ifracx = ifracx;
            int start_ifracy = ifracy;
            Range clipRange = this.performScanlineClipping(src_rect_x1, src_rect_y1, src_rect_x2 - 1.0f, src_rect_y2 - 1.0f, s_ix, s_iy, ifracx, ifracy, dst_min_x, dst_max_x, 0, 0, 0, 0);
            int clipMinX = (Integer)clipRange.getMinValue();
            int clipMaxX = (Integer)clipRange.getMaxValue();
            if (clipMinX > clipMaxX) continue;
            int destYOffset = (y - destTransY) * destScanlineStride + destDBOffset;
            int destXOffset = destDataBitOffset + (dst_min_x - destTransX);
            int sourceYOffset = (s_iy - sourceTransY) * sourceScanlineStride + sourceDBOffset;
            int sourceXOffset = s_ix - sourceTransX + sourceDataBitOffset;
            for (x = dst_min_x; x < clipMinX; ++x) {
                if (this.setBackground) {
                    dindex = destYOffset + (destXOffset >> 3);
                    dshift = 7 - (destXOffset & 7);
                    delement = destData[dindex];
                    destData[dindex] = (byte)(delement |= this.black << dshift);
                }
                if (ifracx < this.ifracdx1) {
                    ifracx += this.ifracdx;
                    sourceXOffset += this.incx;
                } else {
                    ifracx -= this.ifracdx1;
                    sourceXOffset += this.incx1;
                }
                if (ifracy < this.ifracdy1) {
                    ifracy += this.ifracdy;
                    sourceYOffset += incyStride;
                } else {
                    ifracy -= this.ifracdy1;
                    sourceYOffset += incy1Stride;
                }
                ++destXOffset;
            }
            for (x = clipMinX; x < clipMaxX; ++x) {
                int sindex = sourceYOffset + (sourceXOffset >> 3);
                byte selement = sourceData[sindex];
                int val = selement >> 7 - (sourceXOffset & 7) & 1;
                int dindex2 = destYOffset + (destXOffset >> 3);
                int dshift2 = 7 - (destXOffset & 7);
                int delement2 = destData[dindex2];
                destData[dindex2] = (byte)(delement2 |= val << dshift2);
                if (ifracx < this.ifracdx1) {
                    ifracx += this.ifracdx;
                    sourceXOffset += this.incx;
                } else {
                    ifracx -= this.ifracdx1;
                    sourceXOffset += this.incx1;
                }
                if (ifracy < this.ifracdy1) {
                    ifracy += this.ifracdy;
                    sourceYOffset += incyStride;
                } else {
                    ifracy -= this.ifracdy1;
                    sourceYOffset += incy1Stride;
                }
                ++destXOffset;
            }
            for (x = clipMaxX; x < dst_max_x; ++x) {
                if (this.setBackground) {
                    dindex = destYOffset + (destXOffset >> 3);
                    dshift = 7 - (destXOffset & 7);
                    delement = destData[dindex];
                    destData[dindex] = (byte)(delement |= this.black << dshift);
                }
                if (ifracx < this.ifracdx1) {
                    ifracx += this.ifracdx;
                    sourceXOffset += this.incx;
                } else {
                    ifracx -= this.ifracdx1;
                    sourceXOffset += this.incx1;
                }
                if (ifracy < this.ifracdy1) {
                    ifracy += this.ifracdy;
                    sourceYOffset += incyStride;
                } else {
                    ifracy -= this.ifracdy1;
                    sourceYOffset += incy1Stride;
                }
                ++destXOffset;
            }
        }
    }

    private void shortLoop(Raster source, WritableRaster dest, Rectangle destRect) {
        float src_rect_x1 = source.getMinX();
        float src_rect_y1 = source.getMinY();
        float src_rect_x2 = src_rect_x1 + (float)source.getWidth();
        float src_rect_y2 = src_rect_y1 + (float)source.getHeight();
        MultiPixelPackedSampleModel sourceSM = (MultiPixelPackedSampleModel)source.getSampleModel();
        DataBufferUShort sourceDB = (DataBufferUShort)source.getDataBuffer();
        int sourceTransX = source.getSampleModelTranslateX();
        int sourceTransY = source.getSampleModelTranslateY();
        int sourceDataBitOffset = sourceSM.getDataBitOffset();
        int sourceScanlineStride = sourceSM.getScanlineStride();
        MultiPixelPackedSampleModel destSM = (MultiPixelPackedSampleModel)dest.getSampleModel();
        DataBufferUShort destDB = (DataBufferUShort)dest.getDataBuffer();
        int destMinX = dest.getMinX();
        int destMinY = dest.getMinY();
        int destTransX = dest.getSampleModelTranslateX();
        int destTransY = dest.getSampleModelTranslateY();
        int destDataBitOffset = destSM.getDataBitOffset();
        int destScanlineStride = destSM.getScanlineStride();
        short[] sourceData = sourceDB.getData();
        int sourceDBOffset = sourceDB.getOffset();
        short[] destData = destDB.getData();
        int destDBOffset = destDB.getOffset();
        Point2D.Float dst_pt = new Point2D.Float();
        Point2D.Float src_pt = new Point2D.Float();
        int dst_min_x = destRect.x;
        int dst_min_y = destRect.y;
        int dst_max_x = destRect.x + destRect.width;
        int dst_max_y = destRect.y + destRect.height;
        int incyStride = this.incy * sourceScanlineStride;
        int incy1Stride = this.incy1 * sourceScanlineStride;
        this.black = (int)this.backgroundValues[0] & 1;
        for (int y = dst_min_y; y < dst_max_y; ++y) {
            int delement;
            int dshift;
            int dindex;
            int x;
            ((Point2D)dst_pt).setLocation((double)dst_min_x + 0.5, (double)y + 0.5);
            this.mapDestPoint((Point2D)dst_pt, src_pt);
            float s_x = (float)((Point2D)src_pt).getX();
            float s_y = (float)((Point2D)src_pt).getY();
            int s_ix = (int)Math.floor(s_x);
            int s_iy = (int)Math.floor(s_y);
            double fracx = (double)s_x - (double)s_ix;
            double fracy = (double)s_y - (double)s_iy;
            int ifracx = (int)Math.floor(fracx * 1048576.0);
            int ifracy = (int)Math.floor(fracy * 1048576.0);
            int start_s_ix = s_ix;
            int start_s_iy = s_iy;
            int start_ifracx = ifracx;
            int start_ifracy = ifracy;
            Range clipRange = this.performScanlineClipping(src_rect_x1, src_rect_y1, src_rect_x2 - 1.0f, src_rect_y2 - 1.0f, s_ix, s_iy, ifracx, ifracy, dst_min_x, dst_max_x, 0, 0, 0, 0);
            int clipMinX = (Integer)clipRange.getMinValue();
            int clipMaxX = (Integer)clipRange.getMaxValue();
            if (clipMinX > clipMaxX) continue;
            int destYOffset = (y - destTransY) * destScanlineStride + destDBOffset;
            int destXOffset = destDataBitOffset + (dst_min_x - destTransX);
            int sourceYOffset = (s_iy - sourceTransY) * sourceScanlineStride + sourceDBOffset;
            int sourceXOffset = s_ix - sourceTransX + sourceDataBitOffset;
            for (x = dst_min_x; x < clipMinX; ++x) {
                if (this.setBackground) {
                    dindex = destYOffset + (destXOffset >> 4);
                    dshift = 15 - (destXOffset & 0xF);
                    delement = destData[dindex];
                    destData[dindex] = (short)(delement |= this.black << dshift);
                }
                if (ifracx < this.ifracdx1) {
                    ifracx += this.ifracdx;
                    sourceXOffset += this.incx;
                } else {
                    ifracx -= this.ifracdx1;
                    sourceXOffset += this.incx1;
                }
                if (ifracy < this.ifracdy1) {
                    ifracy += this.ifracdy;
                    sourceYOffset += incyStride;
                } else {
                    ifracy -= this.ifracdy1;
                    sourceYOffset += incy1Stride;
                }
                ++destXOffset;
            }
            for (x = clipMinX; x < clipMaxX; ++x) {
                int sindex = sourceYOffset + (sourceXOffset >> 4);
                short selement = sourceData[sindex];
                int val = selement >> 15 - (sourceXOffset & 0xF) & 1;
                int dindex2 = destYOffset + (destXOffset >> 4);
                int dshift2 = 15 - (destXOffset & 0xF);
                int delement2 = destData[dindex2];
                destData[dindex2] = (short)(delement2 |= val << dshift2);
                if (ifracx < this.ifracdx1) {
                    ifracx += this.ifracdx;
                    sourceXOffset += this.incx;
                } else {
                    ifracx -= this.ifracdx1;
                    sourceXOffset += this.incx1;
                }
                if (ifracy < this.ifracdy1) {
                    ifracy += this.ifracdy;
                    sourceYOffset += incyStride;
                } else {
                    ifracy -= this.ifracdy1;
                    sourceYOffset += incy1Stride;
                }
                ++destXOffset;
            }
            for (x = clipMaxX; x < dst_max_x; ++x) {
                if (this.setBackground) {
                    dindex = destYOffset + (destXOffset >> 4);
                    dshift = 15 - (destXOffset & 0xF);
                    delement = destData[dindex];
                    destData[dindex] = (short)(delement |= this.black << dshift);
                }
                if (ifracx < this.ifracdx1) {
                    ifracx += this.ifracdx;
                    sourceXOffset += this.incx;
                } else {
                    ifracx -= this.ifracdx1;
                    sourceXOffset += this.incx1;
                }
                if (ifracy < this.ifracdy1) {
                    ifracy += this.ifracdy;
                    sourceYOffset += incyStride;
                } else {
                    ifracy -= this.ifracdy1;
                    sourceYOffset += incy1Stride;
                }
                ++destXOffset;
            }
        }
    }

    private void intLoop(Raster source, WritableRaster dest, Rectangle destRect) {
        float src_rect_x1 = source.getMinX();
        float src_rect_y1 = source.getMinY();
        float src_rect_x2 = src_rect_x1 + (float)source.getWidth();
        float src_rect_y2 = src_rect_y1 + (float)source.getHeight();
        MultiPixelPackedSampleModel sourceSM = (MultiPixelPackedSampleModel)source.getSampleModel();
        DataBufferInt sourceDB = (DataBufferInt)source.getDataBuffer();
        int sourceTransX = source.getSampleModelTranslateX();
        int sourceTransY = source.getSampleModelTranslateY();
        int sourceDataBitOffset = sourceSM.getDataBitOffset();
        int sourceScanlineStride = sourceSM.getScanlineStride();
        MultiPixelPackedSampleModel destSM = (MultiPixelPackedSampleModel)dest.getSampleModel();
        DataBufferInt destDB = (DataBufferInt)dest.getDataBuffer();
        int destMinX = dest.getMinX();
        int destMinY = dest.getMinY();
        int destTransX = dest.getSampleModelTranslateX();
        int destTransY = dest.getSampleModelTranslateY();
        int destDataBitOffset = destSM.getDataBitOffset();
        int destScanlineStride = destSM.getScanlineStride();
        int[] sourceData = sourceDB.getData();
        int sourceDBOffset = sourceDB.getOffset();
        int[] destData = destDB.getData();
        int destDBOffset = destDB.getOffset();
        Point2D.Float dst_pt = new Point2D.Float();
        Point2D.Float src_pt = new Point2D.Float();
        int dst_min_x = destRect.x;
        int dst_min_y = destRect.y;
        int dst_max_x = destRect.x + destRect.width;
        int dst_max_y = destRect.y + destRect.height;
        int incyStride = this.incy * sourceScanlineStride;
        int incy1Stride = this.incy1 * sourceScanlineStride;
        this.black = (int)this.backgroundValues[0] & 1;
        for (int y = dst_min_y; y < dst_max_y; ++y) {
            int delement;
            int dshift;
            int dindex;
            int x;
            ((Point2D)dst_pt).setLocation((double)dst_min_x + 0.5, (double)y + 0.5);
            this.mapDestPoint((Point2D)dst_pt, src_pt);
            float s_x = (float)((Point2D)src_pt).getX();
            float s_y = (float)((Point2D)src_pt).getY();
            int s_ix = (int)Math.floor(s_x);
            int s_iy = (int)Math.floor(s_y);
            double fracx = (double)s_x - (double)s_ix;
            double fracy = (double)s_y - (double)s_iy;
            int ifracx = (int)Math.floor(fracx * 1048576.0);
            int ifracy = (int)Math.floor(fracy * 1048576.0);
            int start_s_ix = s_ix;
            int start_s_iy = s_iy;
            int start_ifracx = ifracx;
            int start_ifracy = ifracy;
            Range clipRange = this.performScanlineClipping(src_rect_x1, src_rect_y1, src_rect_x2 - 1.0f, src_rect_y2 - 1.0f, s_ix, s_iy, ifracx, ifracy, dst_min_x, dst_max_x, 0, 0, 0, 0);
            int clipMinX = (Integer)clipRange.getMinValue();
            int clipMaxX = (Integer)clipRange.getMaxValue();
            if (clipMinX > clipMaxX) continue;
            int destYOffset = (y - destTransY) * destScanlineStride + destDBOffset;
            int destXOffset = destDataBitOffset + (dst_min_x - destTransX);
            int sourceYOffset = (s_iy - sourceTransY) * sourceScanlineStride + sourceDBOffset;
            int sourceXOffset = s_ix - sourceTransX + sourceDataBitOffset;
            for (x = dst_min_x; x < clipMinX; ++x) {
                if (this.setBackground) {
                    dindex = destYOffset + (destXOffset >> 5);
                    dshift = 31 - (destXOffset & 0x1F);
                    delement = destData[dindex];
                    destData[dindex] = delement |= this.black << dshift;
                }
                if (ifracx < this.ifracdx1) {
                    ifracx += this.ifracdx;
                    sourceXOffset += this.incx;
                } else {
                    ifracx -= this.ifracdx1;
                    sourceXOffset += this.incx1;
                }
                if (ifracy < this.ifracdy1) {
                    ifracy += this.ifracdy;
                    sourceYOffset += incyStride;
                } else {
                    ifracy -= this.ifracdy1;
                    sourceYOffset += incy1Stride;
                }
                ++destXOffset;
            }
            for (x = clipMinX; x < clipMaxX; ++x) {
                int sindex = sourceYOffset + (sourceXOffset >> 5);
                int selement = sourceData[sindex];
                int val = selement >> 31 - (sourceXOffset & 0x1F) & 1;
                int dindex2 = destYOffset + (destXOffset >> 5);
                int dshift2 = 31 - (destXOffset & 0x1F);
                int delement2 = destData[dindex2];
                destData[dindex2] = delement2 |= val << dshift2;
                if (ifracx < this.ifracdx1) {
                    ifracx += this.ifracdx;
                    sourceXOffset += this.incx;
                } else {
                    ifracx -= this.ifracdx1;
                    sourceXOffset += this.incx1;
                }
                if (ifracy < this.ifracdy1) {
                    ifracy += this.ifracdy;
                    sourceYOffset += incyStride;
                } else {
                    ifracy -= this.ifracdy1;
                    sourceYOffset += incy1Stride;
                }
                ++destXOffset;
            }
            for (x = clipMaxX; x < dst_max_x; ++x) {
                if (this.setBackground) {
                    dindex = destYOffset + (destXOffset >> 5);
                    dshift = 31 - (destXOffset & 0x1F);
                    delement = destData[dindex];
                    destData[dindex] = delement |= this.black << dshift;
                }
                if (ifracx < this.ifracdx1) {
                    ifracx += this.ifracdx;
                    sourceXOffset += this.incx;
                } else {
                    ifracx -= this.ifracdx1;
                    sourceXOffset += this.incx1;
                }
                if (ifracy < this.ifracdy1) {
                    ifracy += this.ifracdy;
                    sourceYOffset += incyStride;
                } else {
                    ifracy -= this.ifracdy1;
                    sourceYOffset += incy1Stride;
                }
                ++destXOffset;
            }
        }
    }
}

