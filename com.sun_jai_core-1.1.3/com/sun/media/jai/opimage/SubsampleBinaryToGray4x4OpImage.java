/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.JaiI18N;
import com.sun.media.jai.opimage.SubsampleBinaryToGrayOpImage;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.DataBufferByte;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.GeometricOpImage;
import javax.media.jai.ImageLayout;
import javax.media.jai.PackedImageData;
import javax.media.jai.PixelAccessor;

class SubsampleBinaryToGray4x4OpImage
extends GeometricOpImage {
    private int blockX = 4;
    private int blockY = 4;
    private int dWidth;
    private int dHeight;
    private int[] xValues;
    private int[] yValues;
    private int[] lut;
    private byte[] lutGray;

    public SubsampleBinaryToGray4x4OpImage(RenderedImage source, ImageLayout layout, Map config) {
        super(SubsampleBinaryToGray4x4OpImage.vectorize(source), SubsampleBinaryToGrayOpImage.layoutHelper(source, 0.25f, 0.25f, layout, config), config, true, null, null, null);
        int srcWidth = source.getWidth();
        int srcHeight = source.getHeight();
        this.blockY = 4;
        this.blockX = 4;
        this.dWidth = srcWidth / this.blockX;
        this.dHeight = srcHeight / this.blockY;
        this.computableBounds = this.extender == null ? new Rectangle(0, 0, this.dWidth, this.dHeight) : this.getBounds();
        this.buildLookupTables();
        this.computeXYValues(this.dWidth, this.dHeight);
    }

    public Point2D mapDestPoint(Point2D destPt) {
        if (destPt == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        Point2D pt = (Point2D)destPt.clone();
        pt.setLocation(destPt.getX() * 4.0, destPt.getY() * 4.0);
        return pt;
    }

    public Point2D mapSourcePoint(Point2D sourcePt) {
        if (sourcePt == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        Point2D pt = (Point2D)sourcePt.clone();
        pt.setLocation(sourcePt.getX() / 4.0, sourcePt.getY() / 4.0);
        return pt;
    }

    protected Rectangle forwardMapRect(Rectangle sourceRect, int sourceIndex) {
        if (sourceRect == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (sourceIndex != 0) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic1"));
        }
        int x0 = sourceRect.x;
        int y0 = sourceRect.y;
        int dx0 = x0 / this.blockX;
        int dy0 = y0 / this.blockY;
        int x1 = sourceRect.x + sourceRect.width - 1;
        int y1 = sourceRect.y + sourceRect.height - 1;
        int dx1 = x1 / this.blockX;
        int dy1 = y1 / this.blockY;
        return new Rectangle(dx0, dy0, dx1 - dx0 + 1, dy1 - dy0 + 1);
    }

    protected Rectangle backwardMapRect(Rectangle destRect, int sourceIndex) {
        if (destRect == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (sourceIndex != 0) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic1"));
        }
        int sx0 = destRect.x * this.blockX;
        int sy0 = destRect.y * this.blockY;
        int sx1 = (destRect.x + destRect.width - 1) * this.blockX;
        int sy1 = (destRect.y + destRect.height - 1) * this.blockY;
        return new Rectangle(sx0, sy0, sx1 - sx0 + this.blockX, sy1 - sy0 + this.blockY);
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        Raster source = sources[0];
        switch (source.getSampleModel().getDataType()) {
            case 0: 
            case 1: 
            case 2: 
            case 3: {
                this.byteLoop4x4(source, dest, destRect);
                break;
            }
            default: {
                throw new RuntimeException(JaiI18N.getString("SubsampleBinaryToGrayOpImage0"));
            }
        }
    }

    private void byteLoop4x4(Raster source, WritableRaster dest, Rectangle destRect) {
        PixelAccessor pa = new PixelAccessor(source.getSampleModel(), null);
        PackedImageData pid = pa.getPackedPixels(source, source.getBounds(), false, false);
        if (pid.bitOffset % 4 != 0) {
            this.byteLoop(source, dest, destRect);
            return;
        }
        byte[] sourceData = pid.data;
        int sourceDBOffset = pid.offset;
        int dx = destRect.x;
        int dy = destRect.y;
        int dwi = destRect.width;
        int dhi = destRect.height;
        int sourceTransX = pid.rect.x;
        int sourceTransY = pid.rect.y;
        int sourceDataBitOffset = pid.bitOffset;
        int sourceScanlineStride = pid.lineStride;
        PixelInterleavedSampleModel destSM = (PixelInterleavedSampleModel)dest.getSampleModel();
        DataBufferByte destDB = (DataBufferByte)dest.getDataBuffer();
        int destTransX = dest.getSampleModelTranslateX();
        int destTransY = dest.getSampleModelTranslateY();
        int destScanlineStride = destSM.getScanlineStride();
        byte[] destData = destDB.getData();
        int destDBOffset = destDB.getOffset();
        int[] sAreaBitsOn = new int[2];
        for (int j = 0; j < dhi; ++j) {
            int y = dy + j << 2;
            int sourceYOffset = (y - sourceTransY) * sourceScanlineStride + sourceDBOffset;
            int destYOffset = (j + dy - destTransY) * destScanlineStride + destDBOffset;
            destYOffset += dx - destTransX;
            int sbitnumi = (dx << 2) - sourceTransX + sourceDataBitOffset;
            int i = 0;
            while (i < dwi) {
                int sbytenumi = sbitnumi >> 3;
                int sstartbiti = sbitnumi % 8;
                int byteindex = sourceYOffset + sbytenumi;
                sAreaBitsOn[1] = 0;
                sAreaBitsOn[0] = 0;
                int k = 0;
                while (k < 4) {
                    int selement = 0xFF & sourceData[byteindex];
                    sAreaBitsOn[1] = sAreaBitsOn[1] + this.lut[selement & 0xF];
                    sAreaBitsOn[0] = sAreaBitsOn[0] + this.lut[selement >> 4];
                    ++k;
                    byteindex += sourceScanlineStride;
                }
                sstartbiti >>= 2;
                while (sstartbiti < 2 && i < dwi) {
                    destData[destYOffset + i] = this.lutGray[sAreaBitsOn[sstartbiti]];
                    ++sstartbiti;
                    ++i;
                    sbitnumi += this.blockX;
                }
            }
        }
    }

    private void byteLoop(Raster source, WritableRaster dest, Rectangle destRect) {
        PixelAccessor pa = new PixelAccessor(source.getSampleModel(), null);
        PackedImageData pid = pa.getPackedPixels(source, source.getBounds(), false, false);
        byte[] sourceData = pid.data;
        int sourceDBOffset = pid.offset;
        int dx = destRect.x;
        int dy = destRect.y;
        int dwi = destRect.width;
        int dhi = destRect.height;
        int sourceTransX = pid.rect.x;
        int sourceTransY = pid.rect.y;
        int sourceDataBitOffset = pid.bitOffset;
        int sourceScanlineStride = pid.lineStride;
        PixelInterleavedSampleModel destSM = (PixelInterleavedSampleModel)dest.getSampleModel();
        DataBufferByte destDB = (DataBufferByte)dest.getDataBuffer();
        int destTransX = dest.getSampleModelTranslateX();
        int destTransY = dest.getSampleModelTranslateY();
        int destScanlineStride = destSM.getScanlineStride();
        byte[] destData = destDB.getData();
        int destDBOffset = destDB.getOffset();
        int[] sbytenum = new int[dwi];
        int[] sstartbit = new int[dwi];
        int[] sAreaBitsOn = new int[dwi];
        for (int i = 0; i < dwi; ++i) {
            int x = this.xValues[dx + i];
            int sbitnum = sourceDataBitOffset + (x - sourceTransX);
            sbytenum[i] = sbitnum >> 3;
            sstartbit[i] = sbitnum % 8;
        }
        for (int j = 0; j < dhi; ++j) {
            for (int i = 0; i < dwi; ++i) {
                sAreaBitsOn[i] = 0;
            }
            for (int y = this.yValues[dy + j]; y < this.yValues[dy + j] + this.blockY; ++y) {
                int sourceYOffset = (y - sourceTransY) * sourceScanlineStride + sourceDBOffset;
                int i = 0;
                while (i < dwi) {
                    int delement = 0;
                    int sendbiti = sstartbit[i] + this.blockX - 1;
                    int sendbytenumi = sbytenum[i] + (sendbiti >> 3);
                    sendbiti %= 8;
                    int selement = 0xFF & sourceData[sourceYOffset + sbytenum[i]];
                    int swingBits = 24 + sstartbit[i];
                    if (sbytenum[i] == sendbytenumi) {
                        selement <<= swingBits;
                        delement += this.lut[selement >>>= 31 - sendbiti + sstartbit[i]];
                    } else {
                        selement <<= swingBits;
                        delement += this.lut[selement >>>= swingBits];
                        for (int b = sbytenum[i] + 1; b < sendbytenumi; ++b) {
                            selement = 0xFF & sourceData[sourceYOffset + b];
                            delement += this.lut[selement];
                        }
                        selement = 0xFF & sourceData[sourceYOffset + sendbytenumi];
                        delement += this.lut[selement >>>= 7 - sendbiti];
                    }
                    int n = i++;
                    sAreaBitsOn[n] = sAreaBitsOn[n] + delement;
                }
            }
            int destYOffset = (j + dy - destTransY) * destScanlineStride + destDBOffset;
            destYOffset += dx - destTransX;
            for (int i = 0; i < dwi; ++i) {
                destData[destYOffset + i] = this.lutGray[sAreaBitsOn[i]];
            }
        }
    }

    private final void buildLookupTables() {
        int i;
        this.lut = new int[16];
        this.lut[0] = 0;
        this.lut[1] = 1;
        this.lut[2] = 1;
        this.lut[3] = 2;
        this.lut[4] = 1;
        this.lut[5] = 2;
        this.lut[6] = 2;
        this.lut[7] = 3;
        for (i = 8; i < 16; ++i) {
            this.lut[i] = 1 + this.lut[i - 8];
        }
        if (this.lutGray != null) {
            return;
        }
        this.lutGray = new byte[this.blockX * this.blockY + 1];
        for (i = 0; i < this.lutGray.length; ++i) {
            int tmp = Math.round(255.0f * (float)i / ((float)this.lutGray.length - 1.0f));
            this.lutGray[i] = (byte)(tmp > 255 ? -1 : (byte)tmp);
        }
        if (SubsampleBinaryToGrayOpImage.isMinWhite(this.getSourceImage(0).getColorModel())) {
            for (i = 0; i < this.lutGray.length; ++i) {
                this.lutGray[i] = (byte)(255 - (0xFF & this.lutGray[i]));
            }
        }
    }

    private void computeXYValues(int dstWidth, int dstHeight) {
        int i;
        if (this.xValues == null || this.yValues == null) {
            this.xValues = new int[dstWidth];
            this.yValues = new int[dstHeight];
        }
        for (i = 0; i < dstWidth; ++i) {
            this.xValues[i] = i << 2;
        }
        for (i = 0; i < dstHeight; ++i) {
            this.yValues[i] = i << 2;
        }
    }
}

