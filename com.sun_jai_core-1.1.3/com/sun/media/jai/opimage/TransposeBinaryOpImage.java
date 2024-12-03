/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.TransposeOpImage;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferUShort;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;

final class TransposeBinaryOpImage
extends TransposeOpImage {
    private static ImageLayout layoutHelper(ImageLayout layout, SampleModel sm, ColorModel cm) {
        ImageLayout newLayout = layout != null ? (ImageLayout)layout.clone() : new ImageLayout();
        newLayout.setSampleModel(sm);
        newLayout.setColorModel(cm);
        return newLayout;
    }

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

    public TransposeBinaryOpImage(RenderedImage source, Map config, ImageLayout layout, int type) {
        super(source, TransposeBinaryOpImage.configHelper(config), TransposeBinaryOpImage.layoutHelper(layout, source.getSampleModel(), source.getColorModel()), type);
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        Raster source = sources[0];
        MultiPixelPackedSampleModel mppsm = (MultiPixelPackedSampleModel)source.getSampleModel();
        int srcScanlineStride = mppsm.getScanlineStride();
        int incr1 = 0;
        int incr2 = 0;
        int s_x = 0;
        int s_y = 0;
        int bits = 8;
        int dataType = source.getSampleModel().getDataType();
        if (dataType == 1) {
            bits = 16;
        } else if (dataType == 3) {
            bits = 32;
        }
        PlanarImage src = this.getSource(0);
        int sMinX = src.getMinX();
        int sMinY = src.getMinY();
        int sWidth = src.getWidth();
        int sHeight = src.getHeight();
        int sMaxX = sMinX + sWidth - 1;
        int sMaxY = sMinY + sHeight - 1;
        int[] pt = new int[]{destRect.x, destRect.y};
        TransposeBinaryOpImage.mapPoint(pt, sMinX, sMinY, sMaxX, sMaxY, this.type, false);
        s_x = pt[0];
        s_y = pt[1];
        switch (this.type) {
            case 0: {
                incr1 = 1;
                incr2 = -bits * srcScanlineStride;
                break;
            }
            case 1: {
                incr1 = -1;
                incr2 = bits * srcScanlineStride;
                break;
            }
            case 2: {
                incr1 = bits * srcScanlineStride;
                incr2 = 1;
                break;
            }
            case 3: {
                incr1 = -bits * srcScanlineStride;
                incr2 = -1;
                break;
            }
            case 4: {
                incr1 = -bits * srcScanlineStride;
                incr2 = 1;
                break;
            }
            case 5: {
                incr1 = -1;
                incr2 = -bits * srcScanlineStride;
                break;
            }
            case 6: {
                incr1 = bits * srcScanlineStride;
                incr2 = -1;
            }
        }
        switch (source.getSampleModel().getDataType()) {
            case 0: {
                this.byteLoop(source, dest, destRect, incr1, incr2, s_x, s_y);
                break;
            }
            case 1: 
            case 2: {
                this.shortLoop(source, dest, destRect, incr1, incr2, s_x, s_y);
                break;
            }
            case 3: {
                this.intLoop(source, dest, destRect, incr1, incr2, s_x, s_y);
            }
        }
    }

    private void byteLoop(Raster source, WritableRaster dest, Rectangle destRect, int incr1, int incr2, int s_x, int s_y) {
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
        int dx = destRect.x;
        int dy = destRect.y;
        int dwidth = destRect.width;
        int dheight = destRect.height;
        int sourceOffset = 8 * (s_y - sourceTransY) * sourceScanlineStride + 8 * sourceDBOffset + (s_x - sourceTransX) + sourceDataBitOffset;
        int destOffset = 8 * (dy - destTransY) * destScanlineStride + 8 * destDBOffset + (dx - destTransX) + destDataBitOffset;
        for (int j = 0; j < dheight; ++j) {
            int delement;
            int dshift;
            int dindex;
            int val;
            byte selement;
            int i;
            int sOffset = sourceOffset;
            int dOffset = destOffset;
            for (i = 0; i < dwidth && (dOffset & 7) != 0; ++i) {
                selement = sourceData[sOffset >> 3];
                val = selement >> 7 - (sOffset & 7) & 1;
                dindex = dOffset >> 3;
                dshift = 7 - (dOffset & 7);
                delement = destData[dindex];
                destData[dindex] = (byte)(delement |= val << dshift);
                sOffset += incr1;
                ++dOffset;
            }
            dindex = dOffset >> 3;
            if ((incr1 & 7) == 0) {
                int shift = 7 - (sOffset & 7);
                int offset = sOffset >> 3;
                int incr = incr1 >> 3;
                while (i < dwidth - 7) {
                    selement = sourceData[offset];
                    val = selement >> shift & 1;
                    delement = val << 7;
                    selement = sourceData[offset += incr];
                    val = selement >> shift & 1;
                    delement |= val << 6;
                    selement = sourceData[offset += incr];
                    val = selement >> shift & 1;
                    delement |= val << 5;
                    selement = sourceData[offset += incr];
                    val = selement >> shift & 1;
                    delement |= val << 4;
                    selement = sourceData[offset += incr];
                    val = selement >> shift & 1;
                    delement |= val << 3;
                    selement = sourceData[offset += incr];
                    val = selement >> shift & 1;
                    delement |= val << 2;
                    selement = sourceData[offset += incr];
                    val = selement >> shift & 1;
                    delement |= val << 1;
                    selement = sourceData[offset += incr];
                    val = selement >> shift & 1;
                    offset += incr;
                    destData[dindex] = (byte)(delement |= val);
                    sOffset += 8 * incr1;
                    dOffset += 8;
                    i += 8;
                    ++dindex;
                }
            } else {
                while (i < dwidth - 7) {
                    selement = sourceData[sOffset >> 3];
                    val = selement >> 7 - (sOffset & 7) & 1;
                    delement = val << 7;
                    selement = sourceData[(sOffset += incr1) >> 3];
                    val = selement >> 7 - (sOffset & 7) & 1;
                    delement |= val << 6;
                    selement = sourceData[(sOffset += incr1) >> 3];
                    val = selement >> 7 - (sOffset & 7) & 1;
                    delement |= val << 5;
                    selement = sourceData[(sOffset += incr1) >> 3];
                    val = selement >> 7 - (sOffset & 7) & 1;
                    delement |= val << 4;
                    selement = sourceData[(sOffset += incr1) >> 3];
                    val = selement >> 7 - (sOffset & 7) & 1;
                    delement |= val << 3;
                    selement = sourceData[(sOffset += incr1) >> 3];
                    val = selement >> 7 - (sOffset & 7) & 1;
                    delement |= val << 2;
                    selement = sourceData[(sOffset += incr1) >> 3];
                    val = selement >> 7 - (sOffset & 7) & 1;
                    delement |= val << 1;
                    selement = sourceData[(sOffset += incr1) >> 3];
                    val = selement >> 7 - (sOffset & 7) & 1;
                    sOffset += incr1;
                    destData[dindex] = (byte)(delement |= val);
                    dOffset += 8;
                    i += 8;
                    ++dindex;
                }
            }
            while (i < dwidth) {
                selement = sourceData[sOffset >> 3];
                val = selement >> 7 - (sOffset & 7) & 1;
                dindex = dOffset >> 3;
                dshift = 7 - (dOffset & 7);
                delement = destData[dindex];
                destData[dindex] = (byte)(delement |= val << dshift);
                sOffset += incr1;
                ++dOffset;
                ++i;
            }
            sourceOffset += incr2;
            destOffset += 8 * destScanlineStride;
        }
    }

    private void shortLoop(Raster source, Raster dest, Rectangle destRect, int incr1, int incr2, int s_x, int s_y) {
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
        int dx = destRect.x;
        int dy = destRect.y;
        int dwidth = destRect.width;
        int dheight = destRect.height;
        int sourceOffset = 16 * (s_y - sourceTransY) * sourceScanlineStride + 16 * sourceDBOffset + (s_x - sourceTransX) + sourceDataBitOffset;
        int destOffset = 16 * (dy - destTransY) * destScanlineStride + 16 * destDBOffset + (dx - destTransX) + destDataBitOffset;
        for (int j = 0; j < dheight; ++j) {
            int delement;
            int dshift;
            int dindex;
            int val;
            short selement;
            int i;
            int sOffset = sourceOffset;
            int dOffset = destOffset;
            for (i = 0; i < dwidth && (dOffset & 0xF) != 0; ++i) {
                selement = sourceData[sOffset >> 4];
                val = selement >> 15 - (sOffset & 0xF) & 1;
                dindex = dOffset >> 4;
                dshift = 15 - (dOffset & 0xF);
                delement = destData[dindex];
                destData[dindex] = (short)(delement |= val << dshift);
                sOffset += incr1;
                ++dOffset;
            }
            dindex = dOffset >> 4;
            if ((incr1 & 0xF) == 0) {
                int shift = 15 - (sOffset & 5);
                int offset = sOffset >> 4;
                int incr = incr1 >> 4;
                while (i < dwidth - 15) {
                    delement = 0;
                    for (int b = 15; b >= 0; --b) {
                        selement = sourceData[offset];
                        val = selement >> shift & 1;
                        delement |= val << b;
                        offset += incr;
                    }
                    destData[dindex] = (short)delement;
                    sOffset += 16 * incr1;
                    dOffset += 16;
                    i += 16;
                    ++dindex;
                }
            } else {
                while (i < dwidth - 15) {
                    delement = 0;
                    for (int b = 15; b >= 0; --b) {
                        selement = sourceData[sOffset >> 4];
                        val = selement >> 15 - (sOffset & 0xF) & 1;
                        delement |= val << b;
                        sOffset += incr1;
                    }
                    destData[dindex] = (short)delement;
                    dOffset += 15;
                    i += 16;
                    ++dindex;
                }
            }
            while (i < dwidth) {
                selement = sourceData[sOffset >> 4];
                val = selement >> 15 - (sOffset & 0xF) & 1;
                dindex = dOffset >> 4;
                dshift = 15 - (dOffset & 0xF);
                delement = destData[dindex];
                destData[dindex] = (short)(delement |= val << dshift);
                sOffset += incr1;
                ++dOffset;
                ++i;
            }
            sourceOffset += incr2;
            destOffset += 16 * destScanlineStride;
        }
    }

    private void intLoop(Raster source, Raster dest, Rectangle destRect, int incr1, int incr2, int s_x, int s_y) {
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
        int dx = destRect.x;
        int dy = destRect.y;
        int dwidth = destRect.width;
        int dheight = destRect.height;
        int sourceOffset = 32 * (s_y - sourceTransY) * sourceScanlineStride + 32 * sourceDBOffset + (s_x - sourceTransX) + sourceDataBitOffset;
        int destOffset = 32 * (dy - destTransY) * destScanlineStride + 32 * destDBOffset + (dx - destTransX) + destDataBitOffset;
        for (int j = 0; j < dheight; ++j) {
            int delement;
            int dshift;
            int dindex;
            int val;
            int selement;
            int i;
            int sOffset = sourceOffset;
            int dOffset = destOffset;
            for (i = 0; i < dwidth && (dOffset & 0x1F) != 0; ++i) {
                selement = sourceData[sOffset >> 5];
                val = selement >> 31 - (sOffset & 0x1F) & 1;
                dindex = dOffset >> 5;
                dshift = 31 - (dOffset & 0x1F);
                delement = destData[dindex];
                destData[dindex] = delement |= val << dshift;
                sOffset += incr1;
                ++dOffset;
            }
            dindex = dOffset >> 5;
            if ((incr1 & 0x1F) == 0) {
                int shift = 31 - (sOffset & 5);
                int offset = sOffset >> 5;
                int incr = incr1 >> 5;
                while (i < dwidth - 31) {
                    delement = 0;
                    for (int b = 31; b >= 0; --b) {
                        selement = sourceData[offset];
                        val = selement >> shift & 1;
                        delement |= val << b;
                        offset += incr;
                    }
                    destData[dindex] = delement;
                    sOffset += 32 * incr1;
                    dOffset += 32;
                    i += 32;
                    ++dindex;
                }
            } else {
                while (i < dwidth - 31) {
                    delement = 0;
                    for (int b = 31; b >= 0; --b) {
                        selement = sourceData[sOffset >> 5];
                        val = selement >> 31 - (sOffset & 0x1F) & 1;
                        delement |= val << b;
                        sOffset += incr1;
                    }
                    destData[dindex] = delement;
                    dOffset += 31;
                    i += 32;
                    ++dindex;
                }
            }
            while (i < dwidth) {
                selement = sourceData[sOffset >> 5];
                val = selement >> 31 - (sOffset & 0x1F) & 1;
                dindex = dOffset >> 5;
                dshift = 31 - (dOffset & 0x1F);
                delement = destData[dindex];
                destData[dindex] = delement |= val << dshift;
                sOffset += incr1;
                ++dOffset;
                ++i;
            }
            sourceOffset += incr2;
            destOffset += 32 * destScanlineStride;
        }
    }
}

