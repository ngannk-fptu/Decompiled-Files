/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.ImageLayout;
import javax.media.jai.LookupTableJAI;
import javax.media.jai.PixelAccessor;
import javax.media.jai.PointOpImage;
import javax.media.jai.ROI;
import javax.media.jai.RasterFactory;
import javax.media.jai.UnpackedImageData;

abstract class ColorQuantizerOpImage
extends PointOpImage {
    private static final int NBANDS = 3;
    private static final int NGRAYS = 256;
    protected PixelAccessor srcPA;
    protected int srcSampleType;
    protected boolean isInitialized = false;
    protected PixelAccessor destPA;
    protected LookupTableJAI colorMap;
    protected int maxColorNum;
    protected int xPeriod;
    protected int yPeriod;
    protected ROI roi;
    private int numBandsSource;
    protected boolean checkForSkippedTiles = false;

    static final int startPosition(int pos, int start, int period) {
        int t = (pos - start) % period;
        return t == 0 ? pos : pos + (period - t);
    }

    private static ImageLayout layoutHelper(ImageLayout layout, RenderedImage source) {
        ImageLayout il = layout == null ? new ImageLayout() : (ImageLayout)layout.clone();
        il.setMinX(source.getMinX());
        il.setMinY(source.getMinY());
        il.setWidth(source.getWidth());
        il.setHeight(source.getHeight());
        SampleModel sm = il.getSampleModel(source);
        if (sm.getNumBands() != 1) {
            sm = RasterFactory.createComponentSampleModel(sm, sm.getTransferType(), sm.getWidth(), sm.getHeight(), 1);
            il.setSampleModel(sm);
        }
        il.setColorModel(null);
        return il;
    }

    public ColorQuantizerOpImage(RenderedImage source, Map config, ImageLayout layout, int maxColorNum, ROI roi, int xPeriod, int yPeriod) {
        super(source, ColorQuantizerOpImage.layoutHelper(layout, source), config, true);
        SampleModel srcSampleModel = source.getSampleModel();
        this.numBandsSource = srcSampleModel.getNumBands();
        this.maxColorNum = maxColorNum;
        this.xPeriod = xPeriod;
        this.yPeriod = yPeriod;
        this.roi = roi;
        this.checkForSkippedTiles = xPeriod > this.tileWidth || yPeriod > this.tileHeight;
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        if (this.colorMap == null) {
            this.train();
        }
        if (!this.isInitialized) {
            this.srcPA = new PixelAccessor(this.getSourceImage(0));
            this.srcSampleType = this.srcPA.sampleType == -1 ? 0 : this.srcPA.sampleType;
            this.isInitialized = true;
        }
        UnpackedImageData uid = this.srcPA.getPixels(sources[0], destRect, this.srcSampleType, false);
        Rectangle rect = uid.rect;
        byte[][] data = uid.getByteData();
        int srcLineStride = uid.lineStride;
        int srcPixelStride = uid.pixelStride;
        byte[] rBand = data[0];
        byte[] gBand = data[1];
        byte[] bBand = data[2];
        int lastLine = rect.height * srcLineStride + uid.bandOffsets[0];
        if (this.destPA == null) {
            this.destPA = new PixelAccessor(this);
        }
        UnpackedImageData destUid = this.destPA.getPixels(dest, destRect, this.sampleModel.getDataType(), false);
        int destLineOffset = destUid.bandOffsets[0];
        int destLineStride = destUid.lineStride;
        byte[] d = destUid.getByteData(0);
        int[] currentPixel = new int[3];
        for (int lo = uid.bandOffsets[0]; lo < lastLine; lo += srcLineStride) {
            int lastPixel = lo + rect.width * srcPixelStride - uid.bandOffsets[0];
            int dstPixelOffset = destLineOffset;
            for (int po = lo - uid.bandOffsets[0]; po < lastPixel; po += srcPixelStride) {
                d[dstPixelOffset] = this.findNearestEntry(rBand[po + uid.bandOffsets[0]] & 0xFF, gBand[po + uid.bandOffsets[1]] & 0xFF, bBand[po + uid.bandOffsets[2]] & 0xFF);
                dstPixelOffset += destUid.pixelStride;
            }
            destLineOffset += destLineStride;
        }
    }

    public Object getProperty(String name) {
        int numBands = this.sampleModel.getNumBands();
        if (name.equals("JAI.LookupTable") || name.equals("LUT")) {
            if (this.colorMap == null) {
                this.train();
            }
            return this.colorMap;
        }
        return super.getProperty(name);
    }

    protected abstract void train();

    public ColorModel getColorModel() {
        if (this.colorMap == null) {
            this.train();
        }
        if (this.colorModel == null) {
            this.colorModel = new IndexColorModel(8, this.colorMap.getByteData(0).length, this.colorMap.getByteData(0), this.colorMap.getByteData(1), this.colorMap.getByteData(2));
        }
        return this.colorModel;
    }

    protected byte findNearestEntry(int r, int g, int b) {
        byte[] red = this.colorMap.getByteData(0);
        byte[] green = this.colorMap.getByteData(1);
        byte[] blue = this.colorMap.getByteData(2);
        int index = 0;
        int dr = r - (red[0] & 0xFF);
        int dg = g - (green[0] & 0xFF);
        int db = b - (blue[0] & 0xFF);
        int minDistance = dr * dr + dg * dg + db * db;
        for (int i = 1; i < red.length; ++i) {
            dr = r - (red[i] & 0xFF);
            int distance = dr * dr;
            if (distance > minDistance || (distance += (dg = g - (green[i] & 0xFF)) * dg) > minDistance || (distance += (db = b - (blue[i] & 0xFF)) * db) >= minDistance) continue;
            minDistance = distance;
            index = i;
        }
        return (byte)index;
    }
}

