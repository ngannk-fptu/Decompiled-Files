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

class SubsampleBinaryToGray2x2OpImage
extends GeometricOpImage {
    private int blockX = 2;
    private int blockY = 2;
    private int dWidth;
    private int dHeight;
    private int[] lut4_45;
    private int[] lut4_67;
    private byte[] lutGray;

    public SubsampleBinaryToGray2x2OpImage(RenderedImage source, ImageLayout layout, Map config) {
        super(SubsampleBinaryToGray2x2OpImage.vectorize(source), SubsampleBinaryToGrayOpImage.layoutHelper(source, 0.5f, 0.5f, layout, config), config, true, null, null, null);
        int srcWidth = source.getWidth();
        int srcHeight = source.getHeight();
        this.dWidth = srcWidth / this.blockX;
        this.dHeight = srcHeight / this.blockY;
        this.computableBounds = this.extender == null ? new Rectangle(0, 0, this.dWidth, this.dHeight) : this.getBounds();
        this.buildLookupTables();
    }

    public Point2D mapDestPoint(Point2D destPt) {
        if (destPt == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        Point2D pt = (Point2D)destPt.clone();
        pt.setLocation(destPt.getX() * 2.0, destPt.getY() * 2.0);
        return pt;
    }

    public Point2D mapSourcePoint(Point2D sourcePt) {
        if (sourcePt == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        Point2D pt = (Point2D)sourcePt.clone();
        pt.setLocation(sourcePt.getX() / 2.0, sourcePt.getY() / 2.0);
        return pt;
    }

    protected Rectangle forwardMapRect(Rectangle sourceRect, int sourceIndex) {
        if (sourceRect == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (sourceIndex != 0) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic1"));
        }
        int dx0 = sourceRect.x / this.blockX;
        int dy0 = sourceRect.y / this.blockY;
        int dx1 = (sourceRect.x + sourceRect.width - 1) / this.blockX;
        int dy1 = (sourceRect.y + sourceRect.height - 1) / this.blockY;
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
                this.byteLoop2x2(source, dest, destRect);
                break;
            }
            default: {
                throw new RuntimeException(JaiI18N.getString("SubsampleBinaryToGrayOpImage0"));
            }
        }
    }

    private void byteLoop2x2(Raster source, WritableRaster dest, Rectangle destRect) {
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
        int[] sAreaBitsOn = new int[4];
        if ((sourceDataBitOffset & 1) == 0) {
            for (int j = 0; j < dhi; ++j) {
                int y = dy + j << 1;
                int sourceYOffset = (y - sourceTransY) * sourceScanlineStride + sourceDBOffset;
                int sourceYOffset2 = sourceYOffset + sourceScanlineStride;
                int destYOffset = (j + dy - destTransY) * destScanlineStride + destDBOffset;
                destYOffset += dx - destTransX;
                int sbitnumi = (dx << 1) - sourceTransX + sourceDataBitOffset;
                int i = 0;
                while (i < dwi) {
                    int sbytenumi = sbitnumi >> 3;
                    int sstartbiti = sbitnumi % 8;
                    int selement = 0xFF & sourceData[sourceYOffset + sbytenumi];
                    sAreaBitsOn[2] = this.lut4_45[selement & 0xF];
                    sAreaBitsOn[3] = this.lut4_67[selement & 0xF];
                    sAreaBitsOn[0] = this.lut4_45[selement >>= 4];
                    sAreaBitsOn[1] = this.lut4_67[selement];
                    selement = 0xFF & sourceData[sourceYOffset2 + sbytenumi];
                    sAreaBitsOn[2] = sAreaBitsOn[2] + this.lut4_45[selement & 0xF];
                    sAreaBitsOn[3] = sAreaBitsOn[3] + this.lut4_67[selement & 0xF];
                    sAreaBitsOn[0] = sAreaBitsOn[0] + this.lut4_45[selement >>= 4];
                    sAreaBitsOn[1] = sAreaBitsOn[1] + this.lut4_67[selement];
                    sstartbiti >>= 1;
                    while (sstartbiti < 4 && i < dwi) {
                        destData[destYOffset + i] = this.lutGray[sAreaBitsOn[sstartbiti]];
                        ++sstartbiti;
                        ++i;
                        sbitnumi += this.blockX;
                    }
                }
            }
        } else {
            for (int j = 0; j < dhi; ++j) {
                int y = dy + j << 1;
                int sourceYOffset = (y - sourceTransY) * sourceScanlineStride + sourceDBOffset;
                int sourceYOffset2 = sourceYOffset + sourceScanlineStride;
                int destYOffset = (j + dy - destTransY) * destScanlineStride + destDBOffset;
                destYOffset += dx - destTransX;
                int sbitnumi = (dx << 1) - sourceTransX + sourceDataBitOffset;
                int i = 0;
                while (i < dwi) {
                    int sbytenumi = sbitnumi >> 3;
                    int sstartbiti = sbitnumi % 8;
                    int selement = 0xFF & sourceData[sourceYOffset + sbytenumi] << 1;
                    sAreaBitsOn[2] = this.lut4_45[selement & 0xF];
                    sAreaBitsOn[3] = this.lut4_67[selement & 0xF];
                    sAreaBitsOn[0] = this.lut4_45[selement >>= 4];
                    sAreaBitsOn[1] = this.lut4_67[selement];
                    selement = 0xFF & sourceData[sourceYOffset2 + sbytenumi] << 1;
                    sAreaBitsOn[2] = sAreaBitsOn[2] + this.lut4_45[selement & 0xF];
                    sAreaBitsOn[3] = sAreaBitsOn[3] + this.lut4_67[selement & 0xF];
                    sAreaBitsOn[0] = sAreaBitsOn[0] + this.lut4_45[selement >>= 4];
                    sAreaBitsOn[1] = sAreaBitsOn[1] + this.lut4_67[selement];
                    if (++sbytenumi < sourceData.length - sourceYOffset2) {
                        sAreaBitsOn[3] = sAreaBitsOn[3] + (sourceData[sourceYOffset + sbytenumi] < 0 ? 1 : 0);
                        sAreaBitsOn[3] = sAreaBitsOn[3] + (sourceData[sourceYOffset2 + sbytenumi] < 0 ? 1 : 0);
                    }
                    sstartbiti >>= 1;
                    while (sstartbiti < 4 && i < dwi) {
                        destData[destYOffset + i] = this.lutGray[sAreaBitsOn[sstartbiti]];
                        ++sstartbiti;
                        ++i;
                        sbitnumi += this.blockX;
                    }
                }
            }
        }
    }

    private final void buildLookupTables() {
        int i;
        this.lut4_45 = new int[16];
        this.lut4_67 = new int[16];
        this.lut4_67[0] = 0;
        this.lut4_67[1] = 1;
        this.lut4_67[2] = 1;
        this.lut4_67[3] = 2;
        for (i = 4; i < 16; ++i) {
            this.lut4_67[i] = this.lut4_67[i & 3];
        }
        for (i = 0; i < 16; ++i) {
            this.lut4_45[i] = this.lut4_67[i >> 2];
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
}

