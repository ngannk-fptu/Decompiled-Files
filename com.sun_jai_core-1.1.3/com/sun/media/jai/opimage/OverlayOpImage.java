/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.util.ImageUtil;
import com.sun.media.jai.util.JDKWorkarounds;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import java.util.Vector;
import javax.media.jai.ImageLayout;
import javax.media.jai.PlanarImage;
import javax.media.jai.PointOpImage;
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFormatTag;

final class OverlayOpImage
extends PointOpImage {
    private static ImageLayout layoutHelper(ImageLayout layout, Vector sources, Map config) {
        if (layout != null) {
            layout = (ImageLayout)layout.clone();
            layout.unsetImageBounds();
        }
        return layout;
    }

    public OverlayOpImage(RenderedImage sourceUnder, RenderedImage sourceOver, Map config, ImageLayout layout) {
        super(sourceUnder, sourceOver, OverlayOpImage.layoutHelper(layout, OverlayOpImage.vectorize(sourceUnder, sourceOver), config), config, true);
        SampleModel srcSM = sourceUnder.getSampleModel();
        if (this.sampleModel.getTransferType() != srcSM.getTransferType() || this.sampleModel.getNumBands() != srcSM.getNumBands()) {
            this.sampleModel = srcSM.createCompatibleSampleModel(this.tileWidth, this.tileHeight);
            if (this.colorModel != null && !JDKWorkarounds.areCompatibleDataModels(this.sampleModel, this.colorModel)) {
                this.colorModel = ImageUtil.getCompatibleColorModel(this.sampleModel, config);
            }
        }
        this.minX = sourceUnder.getMinX();
        this.minY = sourceUnder.getMinY();
        this.width = sourceUnder.getWidth();
        this.height = sourceUnder.getHeight();
    }

    public Raster computeTile(int tileX, int tileY) {
        WritableRaster dest = this.createTile(tileX, tileY);
        Rectangle destRect = dest.getBounds().intersection(this.getBounds());
        PlanarImage srcUnder = this.getSource(0);
        PlanarImage srcOver = this.getSource(1);
        Rectangle srcUnderBounds = srcUnder.getBounds();
        Rectangle srcOverBounds = srcOver.getBounds();
        Raster[] sources = new Raster[1];
        if (srcOverBounds.contains(destRect)) {
            sources[0] = srcOver.getData(destRect);
            this.computeRect(sources, dest, destRect);
            if (srcOver.overlapsMultipleTiles(destRect)) {
                this.recycleTile(sources[0]);
            }
            return dest;
        }
        if (srcUnderBounds.contains(destRect) && !srcOverBounds.intersects(destRect)) {
            sources[0] = srcUnder.getData(destRect);
            this.computeRect(sources, dest, destRect);
            if (srcUnder.overlapsMultipleTiles(destRect)) {
                this.recycleTile(sources[0]);
            }
            return dest;
        }
        Rectangle isectUnder = destRect.intersection(srcUnderBounds);
        sources[0] = srcUnder.getData(isectUnder);
        this.computeRect(sources, dest, isectUnder);
        if (srcUnder.overlapsMultipleTiles(isectUnder)) {
            this.recycleTile(sources[0]);
        }
        if (srcOverBounds.intersects(destRect)) {
            Rectangle isectOver = destRect.intersection(srcOverBounds);
            sources[0] = srcOver.getData(isectOver);
            this.computeRect(sources, dest, isectOver);
            if (srcOver.overlapsMultipleTiles(isectOver)) {
                this.recycleTile(sources[0]);
            }
        }
        return dest;
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        RasterFormatTag[] formatTags = this.getFormatTags();
        RasterAccessor src = new RasterAccessor(sources[0], destRect, formatTags[0], this.getSource(0).getColorModel());
        RasterAccessor dst = new RasterAccessor(dest, destRect, formatTags[1], this.getColorModel());
        switch (dst.getDataType()) {
            case 0: {
                this.computeRectByte(src, dst);
                break;
            }
            case 1: 
            case 2: {
                this.computeRectShort(src, dst);
                break;
            }
            case 3: {
                this.computeRectInt(src, dst);
                break;
            }
            case 4: {
                this.computeRectFloat(src, dst);
                break;
            }
            case 5: {
                this.computeRectDouble(src, dst);
            }
        }
        dst.copyDataToRaster();
    }

    private void computeRectByte(RasterAccessor src, RasterAccessor dst) {
        int dstWidth = dst.getWidth();
        int dstHeight = dst.getHeight();
        int dstBands = dst.getNumBands();
        int dstLineStride = dst.getScanlineStride();
        int dstPixelStride = dst.getPixelStride();
        int[] dstBandOffsets = dst.getBandOffsets();
        byte[][] dstData = dst.getByteDataArrays();
        int srcLineStride = src.getScanlineStride();
        int srcPixelStride = src.getPixelStride();
        int[] srcBandOffsets = src.getBandOffsets();
        byte[][] srcData = src.getByteDataArrays();
        for (int b = 0; b < dstBands; ++b) {
            byte[] d = dstData[b];
            byte[] s = srcData[b];
            int dstLineOffset = dstBandOffsets[b];
            int srcLineOffset = srcBandOffsets[b];
            for (int h = 0; h < dstHeight; ++h) {
                int dstPixelOffset = dstLineOffset;
                int srcPixelOffset = srcLineOffset;
                dstLineOffset += dstLineStride;
                srcLineOffset += srcLineStride;
                for (int w = 0; w < dstWidth; ++w) {
                    d[dstPixelOffset] = s[srcPixelOffset];
                    dstPixelOffset += dstPixelStride;
                    srcPixelOffset += srcPixelStride;
                }
            }
        }
    }

    private void computeRectShort(RasterAccessor src, RasterAccessor dst) {
        int dstWidth = dst.getWidth();
        int dstHeight = dst.getHeight();
        int dstBands = dst.getNumBands();
        int dstLineStride = dst.getScanlineStride();
        int dstPixelStride = dst.getPixelStride();
        int[] dstBandOffsets = dst.getBandOffsets();
        short[][] dstData = dst.getShortDataArrays();
        int srcLineStride = src.getScanlineStride();
        int srcPixelStride = src.getPixelStride();
        int[] srcBandOffsets = src.getBandOffsets();
        short[][] srcData = src.getShortDataArrays();
        for (int b = 0; b < dstBands; ++b) {
            short[] d = dstData[b];
            short[] s = srcData[b];
            int dstLineOffset = dstBandOffsets[b];
            int srcLineOffset = srcBandOffsets[b];
            for (int h = 0; h < dstHeight; ++h) {
                int dstPixelOffset = dstLineOffset;
                int srcPixelOffset = srcLineOffset;
                dstLineOffset += dstLineStride;
                srcLineOffset += srcLineStride;
                for (int w = 0; w < dstWidth; ++w) {
                    d[dstPixelOffset] = s[srcPixelOffset];
                    dstPixelOffset += dstPixelStride;
                    srcPixelOffset += srcPixelStride;
                }
            }
        }
    }

    private void computeRectInt(RasterAccessor src, RasterAccessor dst) {
        int dstWidth = dst.getWidth();
        int dstHeight = dst.getHeight();
        int dstBands = dst.getNumBands();
        int dstLineStride = dst.getScanlineStride();
        int dstPixelStride = dst.getPixelStride();
        int[] dstBandOffsets = dst.getBandOffsets();
        int[][] dstData = dst.getIntDataArrays();
        int srcLineStride = src.getScanlineStride();
        int srcPixelStride = src.getPixelStride();
        int[] srcBandOffsets = src.getBandOffsets();
        int[][] srcData = src.getIntDataArrays();
        for (int b = 0; b < dstBands; ++b) {
            int[] d = dstData[b];
            int[] s = srcData[b];
            int dstLineOffset = dstBandOffsets[b];
            int srcLineOffset = srcBandOffsets[b];
            for (int h = 0; h < dstHeight; ++h) {
                int dstPixelOffset = dstLineOffset;
                int srcPixelOffset = srcLineOffset;
                dstLineOffset += dstLineStride;
                srcLineOffset += srcLineStride;
                for (int w = 0; w < dstWidth; ++w) {
                    d[dstPixelOffset] = s[srcPixelOffset];
                    dstPixelOffset += dstPixelStride;
                    srcPixelOffset += srcPixelStride;
                }
            }
        }
    }

    private void computeRectFloat(RasterAccessor src, RasterAccessor dst) {
        int dstWidth = dst.getWidth();
        int dstHeight = dst.getHeight();
        int dstBands = dst.getNumBands();
        int dstLineStride = dst.getScanlineStride();
        int dstPixelStride = dst.getPixelStride();
        int[] dstBandOffsets = dst.getBandOffsets();
        float[][] dstData = dst.getFloatDataArrays();
        int srcLineStride = src.getScanlineStride();
        int srcPixelStride = src.getPixelStride();
        int[] srcBandOffsets = src.getBandOffsets();
        float[][] srcData = src.getFloatDataArrays();
        for (int b = 0; b < dstBands; ++b) {
            float[] d = dstData[b];
            float[] s = srcData[b];
            int dstLineOffset = dstBandOffsets[b];
            int srcLineOffset = srcBandOffsets[b];
            for (int h = 0; h < dstHeight; ++h) {
                int dstPixelOffset = dstLineOffset;
                int srcPixelOffset = srcLineOffset;
                dstLineOffset += dstLineStride;
                srcLineOffset += srcLineStride;
                for (int w = 0; w < dstWidth; ++w) {
                    d[dstPixelOffset] = s[srcPixelOffset];
                    dstPixelOffset += dstPixelStride;
                    srcPixelOffset += srcPixelStride;
                }
            }
        }
    }

    private void computeRectDouble(RasterAccessor src, RasterAccessor dst) {
        int dstWidth = dst.getWidth();
        int dstHeight = dst.getHeight();
        int dstBands = dst.getNumBands();
        int dstLineStride = dst.getScanlineStride();
        int dstPixelStride = dst.getPixelStride();
        int[] dstBandOffsets = dst.getBandOffsets();
        double[][] dstData = dst.getDoubleDataArrays();
        int srcLineStride = src.getScanlineStride();
        int srcPixelStride = src.getPixelStride();
        int[] srcBandOffsets = src.getBandOffsets();
        double[][] srcData = src.getDoubleDataArrays();
        for (int b = 0; b < dstBands; ++b) {
            double[] d = dstData[b];
            double[] s = srcData[b];
            int dstLineOffset = dstBandOffsets[b];
            int srcLineOffset = srcBandOffsets[b];
            for (int h = 0; h < dstHeight; ++h) {
                int dstPixelOffset = dstLineOffset;
                int srcPixelOffset = srcLineOffset;
                dstLineOffset += dstLineStride;
                srcLineOffset += srcLineStride;
                for (int w = 0; w < dstWidth; ++w) {
                    d[dstPixelOffset] = s[srcPixelOffset];
                    dstPixelOffset += dstPixelStride;
                    srcPixelOffset += srcPixelStride;
                }
            }
        }
    }
}

