/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.JaiI18N;
import com.sun.media.jai.util.ImageUtil;
import com.sun.media.jai.util.JDKWorkarounds;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.GeometricOpImage;
import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.PackedImageData;
import javax.media.jai.PixelAccessor;

public class SubsampleBinaryToGrayOpImage
extends GeometricOpImage {
    protected float scaleX;
    protected float scaleY;
    protected float invScaleX;
    protected float invScaleY;
    private float floatTol;
    private int blockX;
    private int blockY;
    private int dWidth;
    private int dHeight;
    private int[] xValues;
    private int[] yValues;
    private int[] lut = new int[256];
    protected byte[] lutGray;

    static ImageLayout layoutHelper(RenderedImage source, float scaleX, float scaleY, ImageLayout il, Map config) {
        ImageLayout layout = il == null ? new ImageLayout() : (ImageLayout)il.clone();
        int srcWidth = source.getWidth();
        int srcHeight = source.getHeight();
        float f_dw = scaleX * (float)srcWidth;
        float f_dh = scaleY * (float)srcHeight;
        float fTol = 0.1f * Math.min(scaleX / (f_dw + 1.0f), scaleY / (f_dh + 1.0f));
        int dWi = (int)f_dw;
        int dHi = (int)f_dh;
        if (Math.abs((float)Math.round(f_dw) - f_dw) < fTol) {
            dWi = Math.round(f_dw);
        }
        if (Math.abs((float)Math.round(f_dh) - f_dh) < fTol) {
            dHi = Math.round(f_dh);
        }
        layout.setMinX((int)(scaleX * (float)source.getMinX()));
        layout.setMinY((int)(scaleY * (float)source.getMinY()));
        layout.setWidth(dWi);
        layout.setHeight(dHi);
        SampleModel sm = layout.getSampleModel(null);
        if (sm == null || sm.getDataType() != 0 || !(sm instanceof PixelInterleavedSampleModel) && (!(sm instanceof SinglePixelPackedSampleModel) || sm.getNumBands() != 1)) {
            sm = new PixelInterleavedSampleModel(0, 1, 1, 1, 1, new int[]{0});
        }
        layout.setSampleModel(sm);
        ColorModel cm = layout.getColorModel(null);
        if (cm == null || !JDKWorkarounds.areCompatibleDataModels(sm, cm)) {
            layout.setColorModel(ImageUtil.getCompatibleColorModel(sm, config));
        }
        return layout;
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

    public SubsampleBinaryToGrayOpImage(RenderedImage source, ImageLayout layout, Map config, float scaleX, float scaleY) {
        super(SubsampleBinaryToGrayOpImage.vectorize(source), SubsampleBinaryToGrayOpImage.layoutHelper(source, scaleX, scaleY, layout, config), SubsampleBinaryToGrayOpImage.configHelper(config), true, null, null, null);
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        int srcMinX = source.getMinX();
        int srcMinY = source.getMinY();
        int srcWidth = source.getWidth();
        int srcHeight = source.getHeight();
        this.computeDestInfo(srcWidth, srcHeight);
        this.computableBounds = this.extender == null ? new Rectangle(0, 0, this.dWidth, this.dHeight) : this.getBounds();
        this.buildLookupTables();
        this.computeXYValues(srcWidth, srcHeight, srcMinX, srcMinY);
    }

    public Point2D mapDestPoint(Point2D destPt) {
        if (destPt == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        Point2D pt = (Point2D)destPt.clone();
        pt.setLocation(destPt.getX() / (double)this.scaleX, destPt.getY() / (double)this.scaleY);
        return pt;
    }

    public Point2D mapSourcePoint(Point2D sourcePt) {
        if (sourcePt == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        Point2D pt = (Point2D)sourcePt.clone();
        pt.setLocation(sourcePt.getX() * (double)this.scaleX, sourcePt.getY() * (double)this.scaleY);
        return pt;
    }

    protected Rectangle forwardMapRect(Rectangle sourceRect, int sourceIndex) {
        int dx0;
        if (sourceRect == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (sourceIndex != 0) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic1"));
        }
        int x0 = sourceRect.x - this.blockX + 1;
        int y0 = sourceRect.y - this.blockY + 1;
        x0 = x0 < 0 ? 0 : x0;
        y0 = y0 < 0 ? 0 : y0;
        int dy0 = (int)((float)y0 * this.scaleY);
        for (dx0 = (int)((float)x0 * this.scaleX); this.xValues[dx0] > x0 && dx0 > 0; --dx0) {
        }
        while (this.yValues[dy0] > y0 && dy0 > 0) {
            --dy0;
        }
        int x1 = sourceRect.x + sourceRect.width - 1;
        int y1 = sourceRect.y + sourceRect.height - 1;
        int dx1 = Math.round((float)x1 * this.scaleX);
        int dy1 = Math.round((float)y1 * this.scaleY);
        int n = dy1 = dy1 >= this.dHeight ? this.dHeight - 1 : dy1;
        for (dx1 = dx1 >= this.dWidth ? this.dWidth - 1 : dx1; this.xValues[dx1] < x1 && dx1 < this.dWidth - 1; ++dx1) {
        }
        while (this.yValues[dy1] < y1 && dy1 < this.dHeight - 1) {
            ++dy1;
        }
        return new Rectangle(dx0 += this.minX, dy0 += this.minY, (dx1 += this.minX) - dx0 + 1, (dy1 += this.minY) - dy0 + 1);
    }

    protected Rectangle backwardMapRect(Rectangle destRect, int sourceIndex) {
        if (destRect == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (sourceIndex != 0) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic1"));
        }
        int sx0 = this.xValues[destRect.x - this.minX];
        int sy0 = this.yValues[destRect.y - this.minY];
        int sx1 = this.xValues[destRect.x - this.minX + destRect.width - 1];
        int sy1 = this.yValues[destRect.y - this.minY + destRect.height - 1];
        return new Rectangle(sx0, sy0, sx1 - sx0 + this.blockX, sy1 - sy0 + this.blockY);
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        Raster source = sources[0];
        switch (source.getSampleModel().getDataType()) {
            case 0: 
            case 1: 
            case 2: 
            case 3: {
                this.byteLoop(source, dest, destRect);
                break;
            }
            default: {
                throw new RuntimeException(JaiI18N.getString("SubsampleBinaryToGrayOpImage0"));
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
            int x = this.xValues[dx + i - this.minX];
            int sbitnum = pid.bitOffset + (x - sourceTransX);
            sbytenum[i] = sbitnum >> 3;
            sstartbit[i] = sbitnum % 8;
        }
        for (int j = 0; j < dhi; ++j) {
            for (int i = 0; i < dwi; ++i) {
                sAreaBitsOn[i] = 0;
            }
            for (int y = this.yValues[dy + j - this.minY]; y < this.yValues[dy + j - this.minY] + this.blockY; ++y) {
                int sourceYOffset = (y - sourceTransY) * pid.lineStride + sourceDBOffset;
                int delement = 0;
                int i = 0;
                while (i < dwi) {
                    delement = 0;
                    int sendbiti = sstartbit[i] + this.blockX - 1;
                    int sendbytenumi = sbytenum[i] + (sendbiti >> 3);
                    sendbiti %= 8;
                    int selement = 0xFF & sourceData[sourceYOffset + sbytenum[i]];
                    if (sbytenum[i] == sendbytenumi) {
                        selement <<= 24 + sstartbit[i];
                        delement += this.lut[selement >>>= 31 - sendbiti + sstartbit[i]];
                    } else {
                        selement <<= 24 + sstartbit[i];
                        delement += this.lut[selement >>>= 24];
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

    private void computeDestInfo(int srcWidth, int srcHeight) {
        this.invScaleX = 1.0f / this.scaleX;
        this.invScaleY = 1.0f / this.scaleY;
        this.blockX = (int)Math.ceil(this.invScaleX);
        this.blockY = (int)Math.ceil(this.invScaleY);
        float f_dw = this.scaleX * (float)srcWidth;
        float f_dh = this.scaleY * (float)srcHeight;
        this.floatTol = 0.1f * Math.min(this.scaleX / (f_dw + 1.0f), this.scaleY / (f_dh + 1.0f));
        this.dWidth = (int)f_dw;
        this.dHeight = (int)f_dh;
        if (Math.abs((float)Math.round(f_dw) - f_dw) < this.floatTol) {
            this.dWidth = Math.round(f_dw);
        }
        if (Math.abs((float)Math.round(f_dh) - f_dh) < this.floatTol) {
            this.dHeight = Math.round(f_dh);
        }
        if (Math.abs((float)Math.round(this.invScaleX) - this.invScaleX) < this.floatTol) {
            this.invScaleX = Math.round(this.invScaleX);
            this.blockX = (int)this.invScaleX;
        }
        if (Math.abs((float)Math.round(this.invScaleY) - this.invScaleY) < this.floatTol) {
            this.invScaleY = Math.round(this.invScaleY);
            this.blockY = (int)this.invScaleY;
        }
    }

    private final void buildLookupTables() {
        int i;
        this.lut[0] = 0;
        this.lut[1] = 1;
        this.lut[2] = 1;
        this.lut[3] = 2;
        this.lut[4] = 1;
        this.lut[5] = 2;
        this.lut[6] = 2;
        this.lut[7] = 3;
        this.lut[8] = 1;
        this.lut[9] = 2;
        this.lut[10] = 2;
        this.lut[11] = 3;
        this.lut[12] = 2;
        this.lut[13] = 3;
        this.lut[14] = 3;
        this.lut[15] = 4;
        for (i = 16; i < 256; ++i) {
            this.lut[i] = this.lut[i & 0xF] + this.lut[i >> 4 & 0xF];
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

    private void computeXYValues(int srcWidth, int srcHeight, int srcMinX, int srcMinY) {
        float tmp;
        int i;
        if (this.xValues == null || this.yValues == null) {
            this.xValues = new int[this.dWidth];
            this.yValues = new int[this.dHeight];
        }
        for (i = 0; i < this.dWidth; ++i) {
            tmp = this.invScaleX * (float)i;
            this.xValues[i] = Math.round(tmp);
        }
        if (this.xValues[this.dWidth - 1] + this.blockX > srcWidth) {
            int n = this.dWidth - 1;
            this.xValues[n] = this.xValues[n] - 1;
        }
        for (i = 0; i < this.dHeight; ++i) {
            tmp = this.invScaleY * (float)i;
            this.yValues[i] = Math.round(tmp);
        }
        if (this.yValues[this.dHeight - 1] + this.blockY > srcHeight) {
            int n = this.dHeight - 1;
            this.yValues[n] = this.yValues[n] - 1;
        }
        if (srcMinX != 0) {
            i = 0;
            while (i < this.dWidth) {
                int n = i++;
                this.xValues[n] = this.xValues[n] + srcMinX;
            }
        }
        if (srcMinY != 0) {
            i = 0;
            while (i < this.dHeight) {
                int n = i++;
                this.yValues[n] = this.yValues[n] + srcMinY;
            }
        }
    }

    static boolean isMinWhite(ColorModel cm) {
        if (cm == null || !(cm instanceof IndexColorModel)) {
            return false;
        }
        byte[] red = new byte[256];
        ((IndexColorModel)cm).getReds(red);
        return red[0] == -1;
    }
}

