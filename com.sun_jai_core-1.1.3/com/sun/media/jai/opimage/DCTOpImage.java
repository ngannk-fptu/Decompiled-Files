/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.FCT;
import com.sun.media.jai.opimage.JaiI18N;
import com.sun.media.jai.util.JDKWorkarounds;
import com.sun.media.jai.util.MathJAI;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.ImageLayout;
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFactory;
import javax.media.jai.RasterFormatTag;
import javax.media.jai.UntiledOpImage;

public class DCTOpImage
extends UntiledOpImage {
    private FCT fct;

    private static ImageLayout layoutHelper(ImageLayout layout, RenderedImage source) {
        SampleModel sm;
        int dataType;
        int newHeight;
        int h;
        int newWidth;
        ImageLayout il = layout == null ? new ImageLayout() : (ImageLayout)layout.clone();
        il.setMinX(source.getMinX());
        il.setMinY(source.getMinY());
        boolean createNewSampleModel = false;
        int w = il.getWidth(source);
        if (w > 1 && (newWidth = MathJAI.nextPositivePowerOf2(w)) != w) {
            w = newWidth;
            il.setWidth(w);
            createNewSampleModel = true;
        }
        if ((h = il.getHeight(source)) > 1 && (newHeight = MathJAI.nextPositivePowerOf2(h)) != h) {
            h = newHeight;
            il.setHeight(h);
            createNewSampleModel = true;
        }
        if ((dataType = (sm = il.getSampleModel(source)).getTransferType()) != 4 && dataType != 5) {
            dataType = 4;
            createNewSampleModel = true;
        }
        if (createNewSampleModel) {
            sm = RasterFactory.createComponentSampleModel(sm, dataType, w, h, sm.getNumBands());
            il.setSampleModel(sm);
            ColorModel cm = il.getColorModel(null);
            if (cm != null && !JDKWorkarounds.areCompatibleDataModels(sm, cm)) {
                il.unsetValid(512);
            }
        }
        return il;
    }

    public DCTOpImage(RenderedImage source, Map config, ImageLayout layout, FCT fct) {
        super(source, config, DCTOpImage.layoutHelper(layout, source));
        this.fct = fct;
    }

    public Point2D mapDestPoint(Point2D destPt) {
        if (destPt == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        return null;
    }

    public Point2D mapSourcePoint(Point2D sourcePt) {
        if (sourcePt == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        return null;
    }

    protected void computeImage(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        Raster source = sources[0];
        if (destRect.width == 1 && destRect.height == 1) {
            double[] pixel = source.getPixel(destRect.x, destRect.y, (double[])null);
            dest.setPixel(destRect.x, destRect.y, pixel);
            return;
        }
        this.fct.setLength(destRect.width > 1 ? this.getWidth() : this.getHeight());
        int srcWidth = source.getWidth();
        int srcHeight = source.getHeight();
        int srcX = source.getMinX();
        int srcY = source.getMinY();
        RasterFormatTag[] formatTags = this.getFormatTags();
        RasterAccessor srcAccessor = new RasterAccessor(source, new Rectangle(srcX, srcY, srcWidth, srcHeight), formatTags[0], this.getSourceImage(0).getColorModel());
        RasterAccessor dstAccessor = new RasterAccessor(dest, destRect, formatTags[1], this.getColorModel());
        int srcDataType = srcAccessor.getDataType();
        int dstDataType = dstAccessor.getDataType();
        int srcPixelStride = srcAccessor.getPixelStride();
        int srcScanlineStride = srcAccessor.getScanlineStride();
        int dstPixelStride = dstAccessor.getPixelStride();
        int dstScanlineStride = dstAccessor.getScanlineStride();
        int numBands = this.sampleModel.getNumBands();
        for (int band = 0; band < numBands; ++band) {
            int dstOffset;
            int srcOffset;
            Object srcData = srcAccessor.getDataArray(band);
            Object dstData = dstAccessor.getDataArray(band);
            if (destRect.width > 1) {
                this.fct.setLength(this.getWidth());
                srcOffset = srcAccessor.getBandOffset(band);
                dstOffset = dstAccessor.getBandOffset(band);
                for (int row = 0; row < srcHeight; ++row) {
                    this.fct.setData(srcDataType, srcData, srcOffset, srcPixelStride, srcWidth);
                    this.fct.transform();
                    this.fct.getData(dstDataType, dstData, dstOffset, dstPixelStride);
                    srcOffset += srcScanlineStride;
                    dstOffset += dstScanlineStride;
                }
            }
            if (destRect.width == 1) {
                srcOffset = srcAccessor.getBandOffset(band);
                dstOffset = dstAccessor.getBandOffset(band);
                this.fct.setData(srcDataType, srcData, srcOffset, srcScanlineStride, srcHeight);
                this.fct.transform();
                this.fct.getData(dstDataType, dstData, dstOffset, dstScanlineStride);
                continue;
            }
            if (destRect.height <= 1) continue;
            this.fct.setLength(this.getHeight());
            int dstOffset2 = dstAccessor.getBandOffset(band);
            for (int col = 0; col < destRect.width; ++col) {
                this.fct.setData(dstDataType, dstData, dstOffset2, dstScanlineStride, destRect.height);
                this.fct.transform();
                this.fct.getData(dstDataType, dstData, dstOffset2, dstScanlineStride);
                dstOffset2 += dstPixelStride;
            }
        }
        if (dstAccessor.needsClamping()) {
            dstAccessor.clampDataArrays();
        }
        dstAccessor.copyDataToRaster();
    }
}

