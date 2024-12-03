/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.GeometricOpImage;
import javax.media.jai.ImageLayout;
import javax.media.jai.IntegerSequence;
import javax.media.jai.PlanarImage;
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFormatTag;

public class TransposeOpImage
extends GeometricOpImage {
    protected int type;
    protected int src_width;
    protected int src_height;
    protected Rectangle sourceBounds;

    private static ImageLayout layoutHelper(ImageLayout layout, RenderedImage source, int type) {
        ImageLayout newLayout = layout != null ? (ImageLayout)layout.clone() : new ImageLayout();
        Rectangle sourceBounds = new Rectangle(source.getMinX(), source.getMinY(), source.getWidth(), source.getHeight());
        Rectangle rect = TransposeOpImage.mapRect(sourceBounds, sourceBounds, type, true);
        newLayout.setMinX(rect.x);
        newLayout.setMinY(rect.y);
        newLayout.setWidth(rect.width);
        newLayout.setHeight(rect.height);
        Rectangle tileRect = new Rectangle(source.getTileGridXOffset(), source.getTileGridYOffset(), source.getTileWidth(), source.getTileHeight());
        rect = TransposeOpImage.mapRect(tileRect, sourceBounds, type, true);
        if (newLayout.isValid(16)) {
            newLayout.setTileGridXOffset(rect.x);
        }
        if (newLayout.isValid(32)) {
            newLayout.setTileGridYOffset(rect.y);
        }
        if (newLayout.isValid(64)) {
            newLayout.setTileWidth(Math.abs(rect.width));
        }
        if (newLayout.isValid(128)) {
            newLayout.setTileHeight(Math.abs(rect.height));
        }
        return newLayout;
    }

    public TransposeOpImage(RenderedImage source, Map config, ImageLayout layout, int type) {
        super(TransposeOpImage.vectorize(source), TransposeOpImage.layoutHelper(layout, source, type), config, true, null, null, null);
        ColorModel srcColorModel = source.getColorModel();
        if (srcColorModel instanceof IndexColorModel) {
            this.sampleModel = source.getSampleModel().createCompatibleSampleModel(this.tileWidth, this.tileHeight);
            this.colorModel = srcColorModel;
        }
        this.type = type;
        this.src_width = source.getWidth();
        this.src_height = source.getHeight();
        this.sourceBounds = new Rectangle(source.getMinX(), source.getMinY(), source.getWidth(), source.getHeight());
    }

    protected Rectangle forwardMapRect(Rectangle sourceRect, int sourceIndex) {
        return TransposeOpImage.mapRect(sourceRect, this.sourceBounds, this.type, true);
    }

    protected Rectangle backwardMapRect(Rectangle destRect, int sourceIndex) {
        return TransposeOpImage.mapRect(destRect, this.sourceBounds, this.type, false);
    }

    protected static void mapPoint(int[] pt, int minX, int minY, int maxX, int maxY, int type, boolean mapForwards) {
        int sx = pt[0];
        int sy = pt[1];
        int dx = -1;
        int dy = -1;
        switch (type) {
            case 0: {
                dx = sx;
                dy = minY + maxY - sy;
                break;
            }
            case 1: {
                dx = minX + maxX - sx;
                dy = sy;
                break;
            }
            case 2: {
                dx = minX - minY + sy;
                dy = minY - minX + sx;
                break;
            }
            case 3: {
                if (mapForwards) {
                    dx = minX + maxY - sy;
                    dy = minY + maxX - sx;
                    break;
                }
                dx = minY + maxX - sy;
                dy = minX + maxY - sx;
                break;
            }
            case 4: {
                if (mapForwards) {
                    dx = minX + maxY - sy;
                    dy = minY - minX + sx;
                    break;
                }
                dx = minX - minY + sy;
                dy = minX + maxY - sx;
                break;
            }
            case 5: {
                dx = minX + maxX - sx;
                dy = minY + maxY - sy;
                break;
            }
            case 6: {
                if (mapForwards) {
                    dx = minX - minY + sy;
                    dy = maxX + minY - sx;
                    break;
                }
                dx = maxX + minY - sy;
                dy = minY - minX + sx;
            }
        }
        pt[0] = dx;
        pt[1] = dy;
    }

    private static Rectangle mapRect(Rectangle rect, Rectangle sourceBounds, int type, boolean mapForwards) {
        int dMaxY;
        int dMaxX;
        int sMinX = sourceBounds.x;
        int sMinY = sourceBounds.y;
        int sMaxX = sMinX + sourceBounds.width - 1;
        int sMaxY = sMinY + sourceBounds.height - 1;
        int[] pt = new int[]{rect.x, rect.y};
        TransposeOpImage.mapPoint(pt, sMinX, sMinY, sMaxX, sMaxY, type, mapForwards);
        int dMinX = dMaxX = pt[0];
        int dMinY = dMaxY = pt[1];
        pt[0] = rect.x + rect.width - 1;
        pt[1] = rect.y;
        TransposeOpImage.mapPoint(pt, sMinX, sMinY, sMaxX, sMaxY, type, mapForwards);
        dMinX = Math.min(dMinX, pt[0]);
        dMinY = Math.min(dMinY, pt[1]);
        dMaxX = Math.max(dMaxX, pt[0]);
        dMaxY = Math.max(dMaxY, pt[1]);
        pt[0] = rect.x;
        pt[1] = rect.y + rect.height - 1;
        TransposeOpImage.mapPoint(pt, sMinX, sMinY, sMaxX, sMaxY, type, mapForwards);
        dMinX = Math.min(dMinX, pt[0]);
        dMinY = Math.min(dMinY, pt[1]);
        dMaxX = Math.max(dMaxX, pt[0]);
        dMaxY = Math.max(dMaxY, pt[1]);
        pt[0] = rect.x + rect.width - 1;
        pt[1] = rect.y + rect.height - 1;
        TransposeOpImage.mapPoint(pt, sMinX, sMinY, sMaxX, sMaxY, type, mapForwards);
        dMinX = Math.min(dMinX, pt[0]);
        dMinY = Math.min(dMinY, pt[1]);
        dMaxX = Math.max(dMaxX, pt[0]);
        dMaxY = Math.max(dMaxY, pt[1]);
        return new Rectangle(dMinX, dMinY, dMaxX - dMinX + 1, dMaxY - dMinY + 1);
    }

    public Raster computeTile(int tileX, int tileY) {
        int boundsMaxY;
        int boundsMaxX;
        Point org = new Point(this.tileXToX(tileX), this.tileYToY(tileY));
        WritableRaster dest = this.createWritableRaster(this.sampleModel, org);
        int destMinX = dest.getMinX();
        int destMinY = dest.getMinY();
        int destMaxX = destMinX + dest.getWidth();
        int destMaxY = destMinY + dest.getHeight();
        Rectangle bounds = this.getBounds();
        if (destMinX < bounds.x) {
            destMinX = bounds.x;
        }
        if (destMaxX > (boundsMaxX = bounds.x + bounds.width)) {
            destMaxX = boundsMaxX;
        }
        if (destMinY < bounds.y) {
            destMinY = bounds.y;
        }
        if (destMaxY > (boundsMaxY = bounds.y + bounds.height)) {
            destMaxY = boundsMaxY;
        }
        if (destMinX >= destMaxX || destMinY >= destMaxY) {
            return dest;
        }
        Rectangle destRect = new Rectangle(destMinX, destMinY, destMaxX - destMinX, destMaxY - destMinY);
        IntegerSequence xSplits = new IntegerSequence(destMinX, destMaxX);
        xSplits.insert(destMinX);
        xSplits.insert(destMaxX);
        IntegerSequence ySplits = new IntegerSequence(destMinY, destMaxY);
        ySplits.insert(destMinY);
        ySplits.insert(destMaxY);
        PlanarImage src = this.getSource(0);
        int sMinX = src.getMinX();
        int sMinY = src.getMinY();
        int sWidth = src.getWidth();
        int sHeight = src.getHeight();
        int sMaxX = sMinX + sWidth - 1;
        int sMaxY = sMinY + sHeight - 1;
        int sTileWidth = src.getTileWidth();
        int sTileHeight = src.getTileHeight();
        int sTileGridXOffset = src.getTileGridXOffset();
        int sTileGridYOffset = src.getTileGridYOffset();
        int xStart = 0;
        int xGap = 0;
        int yStart = 0;
        int yGap = 0;
        int[] pt = new int[]{sTileGridXOffset, sTileGridYOffset};
        TransposeOpImage.mapPoint(pt, sMinX, sMinY, sMaxX, sMaxY, this.type, true);
        xStart = pt[0];
        yStart = pt[1];
        switch (this.type) {
            case 0: {
                ++yStart;
                xGap = sTileWidth;
                yGap = sTileHeight;
                break;
            }
            case 1: {
                ++xStart;
                xGap = sTileWidth;
                yGap = sTileHeight;
                break;
            }
            case 2: {
                xGap = sTileHeight;
                yGap = sTileWidth;
                break;
            }
            case 3: {
                ++xStart;
                ++yStart;
                xGap = sTileHeight;
                yGap = sTileWidth;
                break;
            }
            case 4: {
                ++xStart;
                xGap = sTileHeight;
                yGap = sTileWidth;
                break;
            }
            case 5: {
                ++xStart;
                ++yStart;
                xGap = sTileWidth;
                yGap = sTileHeight;
                break;
            }
            case 6: {
                ++yStart;
                xGap = sTileHeight;
                yGap = sTileWidth;
            }
        }
        int kx = (int)Math.floor((double)(destMinX - xStart) / (double)xGap);
        for (int xSplit = xStart + kx * xGap; xSplit < destMaxX; xSplit += xGap) {
            xSplits.insert(xSplit);
        }
        int ky = (int)Math.floor((double)(destMinY - yStart) / (double)yGap);
        for (int ySplit = yStart + ky * yGap; ySplit < destMaxY; ySplit += yGap) {
            ySplits.insert(ySplit);
        }
        Raster[] sources = new Raster[1];
        Rectangle subRect = new Rectangle();
        ySplits.startEnumeration();
        int y1 = ySplits.nextElement();
        while (ySplits.hasMoreElements()) {
            int y2 = ySplits.nextElement();
            int h = y2 - y1;
            xSplits.startEnumeration();
            int x1 = xSplits.nextElement();
            while (xSplits.hasMoreElements()) {
                int x2 = xSplits.nextElement();
                int w = x2 - x1;
                pt[0] = x1;
                pt[1] = y1;
                TransposeOpImage.mapPoint(pt, sMinX, sMinY, sMaxX, sMaxY, this.type, false);
                int tx = src.XToTileX(pt[0]);
                int ty = src.YToTileY(pt[1]);
                sources[0] = src.getTile(tx, ty);
                subRect.x = x1;
                subRect.y = y1;
                subRect.width = w;
                subRect.height = h;
                this.computeRect(sources, dest, subRect);
                x1 = x2;
            }
            y1 = y2;
        }
        return dest;
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        RasterFormatTag[] formatTags = this.getFormatTags();
        Raster src = sources[0];
        PlanarImage source = this.getSource(0);
        int sMinX = source.getMinX();
        int sMinY = source.getMinY();
        int sWidth = source.getWidth();
        int sHeight = source.getHeight();
        int sMaxX = sMinX + sWidth - 1;
        int sMaxY = sMinY + sHeight - 1;
        int translateX = src.getSampleModelTranslateX();
        int translateY = src.getSampleModelTranslateY();
        Rectangle srcRect = src.getBounds();
        RasterAccessor srcAccessor = new RasterAccessor(src, srcRect, formatTags[0], this.getSource(0).getColorModel());
        RasterAccessor dstAccessor = new RasterAccessor(dest, destRect, formatTags[1], this.getColorModel());
        int incr1 = 0;
        int incr2 = 0;
        int s_x = 0;
        int s_y = 0;
        int srcPixelStride = srcAccessor.getPixelStride();
        int srcScanlineStride = srcAccessor.getScanlineStride();
        int[] pt = new int[]{destRect.x, destRect.y};
        TransposeOpImage.mapPoint(pt, sMinX, sMinY, sMaxX, sMaxY, this.type, false);
        s_x = pt[0];
        s_y = pt[1];
        switch (this.type) {
            case 0: {
                incr1 = srcPixelStride;
                incr2 = -srcScanlineStride;
                break;
            }
            case 1: {
                incr1 = -srcPixelStride;
                incr2 = srcScanlineStride;
                break;
            }
            case 2: {
                incr1 = srcScanlineStride;
                incr2 = srcPixelStride;
                break;
            }
            case 3: {
                incr1 = -srcScanlineStride;
                incr2 = -srcPixelStride;
                break;
            }
            case 4: {
                incr1 = -srcScanlineStride;
                incr2 = srcPixelStride;
                break;
            }
            case 5: {
                incr1 = -srcPixelStride;
                incr2 = -srcScanlineStride;
                break;
            }
            case 6: {
                incr1 = srcScanlineStride;
                incr2 = -srcPixelStride;
            }
        }
        switch (dstAccessor.getDataType()) {
            case 0: {
                this.byteLoop(srcAccessor, destRect, translateX, translateY, dstAccessor, incr1, incr2, s_x, s_y);
                break;
            }
            case 3: {
                this.intLoop(srcAccessor, destRect, translateX, translateY, dstAccessor, incr1, incr2, s_x, s_y);
                break;
            }
            case 1: 
            case 2: {
                this.shortLoop(srcAccessor, destRect, translateX, translateY, dstAccessor, incr1, incr2, s_x, s_y);
                break;
            }
            case 4: {
                this.floatLoop(srcAccessor, destRect, translateX, translateY, dstAccessor, incr1, incr2, s_x, s_y);
                break;
            }
            case 5: {
                this.doubleLoop(srcAccessor, destRect, translateX, translateY, dstAccessor, incr1, incr2, s_x, s_y);
            }
        }
        if (dstAccessor.isDataCopy()) {
            dstAccessor.clampDataArrays();
            dstAccessor.copyDataToRaster();
        }
    }

    private void byteLoop(RasterAccessor src, Rectangle destRect, int srcTranslateX, int srcTranslateY, RasterAccessor dst, int incr1, int incr2, int s_x, int s_y) {
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int dnumBands = dst.getNumBands();
        byte[][] dstDataArrays = dst.getByteDataArrays();
        int[] dstBandOffsets = dst.getBandOffsets();
        int dstPixelStride = dst.getPixelStride();
        int dstScanlineStride = dst.getScanlineStride();
        byte[][] srcDataArrays = src.getByteDataArrays();
        int[] bandOffsets = src.getOffsetsForBands();
        int srcPixelStride = src.getPixelStride();
        int srcScanlineStride = src.getScanlineStride();
        int dst_num_bands = dst.getNumBands();
        int dst_min_x = destRect.x;
        int dst_min_y = destRect.y;
        int dst_max_x = destRect.x + destRect.width;
        int dst_max_y = destRect.y + destRect.height;
        int posy = (s_y - srcTranslateY) * srcScanlineStride;
        int posx = (s_x - srcTranslateX) * srcPixelStride;
        int srcScanlineOffset = posx + posy;
        int dstScanlineOffset = 0;
        for (int y = dst_min_y; y < dst_max_y; ++y) {
            for (int k2 = 0; k2 < dst_num_bands; ++k2) {
                byte[] srcDataArray = srcDataArrays[k2];
                byte[] dstDataArray = dstDataArrays[k2];
                int dstPixelOffset = dstScanlineOffset + dstBandOffsets[k2];
                int srcPixelOffset = srcScanlineOffset + bandOffsets[k2];
                for (int x = dst_min_x; x < dst_max_x; ++x) {
                    dstDataArray[dstPixelOffset] = srcDataArray[srcPixelOffset];
                    srcPixelOffset += incr1;
                    dstPixelOffset += dstPixelStride;
                }
            }
            srcScanlineOffset += incr2;
            dstScanlineOffset += dstScanlineStride;
        }
    }

    private void intLoop(RasterAccessor src, Rectangle destRect, int srcTranslateX, int srcTranslateY, RasterAccessor dst, int incr1, int incr2, int s_x, int s_y) {
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int dnumBands = dst.getNumBands();
        int[][] dstDataArrays = dst.getIntDataArrays();
        int[] dstBandOffsets = dst.getBandOffsets();
        int dstPixelStride = dst.getPixelStride();
        int dstScanlineStride = dst.getScanlineStride();
        int[][] srcDataArrays = src.getIntDataArrays();
        int[] bandOffsets = src.getOffsetsForBands();
        int srcPixelStride = src.getPixelStride();
        int srcScanlineStride = src.getScanlineStride();
        int dst_num_bands = dst.getNumBands();
        int dst_min_x = destRect.x;
        int dst_min_y = destRect.y;
        int dst_max_x = destRect.x + destRect.width;
        int dst_max_y = destRect.y + destRect.height;
        int posy = (s_y - srcTranslateY) * srcScanlineStride;
        int posx = (s_x - srcTranslateX) * srcPixelStride;
        int srcScanlineOffset = posx + posy;
        int dstScanlineOffset = 0;
        for (int y = dst_min_y; y < dst_max_y; ++y) {
            for (int k2 = 0; k2 < dst_num_bands; ++k2) {
                int[] srcDataArray = srcDataArrays[k2];
                int[] dstDataArray = dstDataArrays[k2];
                int dstPixelOffset = dstScanlineOffset + dstBandOffsets[k2];
                int srcPixelOffset = srcScanlineOffset + bandOffsets[k2];
                for (int x = dst_min_x; x < dst_max_x; ++x) {
                    dstDataArray[dstPixelOffset] = srcDataArray[srcPixelOffset];
                    srcPixelOffset += incr1;
                    dstPixelOffset += dstPixelStride;
                }
            }
            srcScanlineOffset += incr2;
            dstScanlineOffset += dstScanlineStride;
        }
    }

    private void shortLoop(RasterAccessor src, Rectangle destRect, int srcTranslateX, int srcTranslateY, RasterAccessor dst, int incr1, int incr2, int s_x, int s_y) {
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int dnumBands = dst.getNumBands();
        short[][] dstDataArrays = dst.getShortDataArrays();
        int[] dstBandOffsets = dst.getBandOffsets();
        int dstPixelStride = dst.getPixelStride();
        int dstScanlineStride = dst.getScanlineStride();
        short[][] srcDataArrays = src.getShortDataArrays();
        int[] bandOffsets = src.getOffsetsForBands();
        int srcPixelStride = src.getPixelStride();
        int srcScanlineStride = src.getScanlineStride();
        int dst_num_bands = dst.getNumBands();
        int dst_min_x = destRect.x;
        int dst_min_y = destRect.y;
        int dst_max_x = destRect.x + destRect.width;
        int dst_max_y = destRect.y + destRect.height;
        int posy = (s_y - srcTranslateY) * srcScanlineStride;
        int posx = (s_x - srcTranslateX) * srcPixelStride;
        int srcScanlineOffset = posx + posy;
        int dstScanlineOffset = 0;
        for (int y = dst_min_y; y < dst_max_y; ++y) {
            for (int k2 = 0; k2 < dst_num_bands; ++k2) {
                short[] srcDataArray = srcDataArrays[k2];
                short[] dstDataArray = dstDataArrays[k2];
                int dstPixelOffset = dstScanlineOffset + dstBandOffsets[k2];
                int srcPixelOffset = srcScanlineOffset + bandOffsets[k2];
                for (int x = dst_min_x; x < dst_max_x; ++x) {
                    dstDataArray[dstPixelOffset] = srcDataArray[srcPixelOffset];
                    srcPixelOffset += incr1;
                    dstPixelOffset += dstPixelStride;
                }
            }
            srcScanlineOffset += incr2;
            dstScanlineOffset += dstScanlineStride;
        }
    }

    private void floatLoop(RasterAccessor src, Rectangle destRect, int srcTranslateX, int srcTranslateY, RasterAccessor dst, int incr1, int incr2, int s_x, int s_y) {
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int dnumBands = dst.getNumBands();
        float[][] dstDataArrays = dst.getFloatDataArrays();
        int[] dstBandOffsets = dst.getBandOffsets();
        int dstPixelStride = dst.getPixelStride();
        int dstScanlineStride = dst.getScanlineStride();
        float[][] srcDataArrays = src.getFloatDataArrays();
        int[] bandOffsets = src.getOffsetsForBands();
        int srcPixelStride = src.getPixelStride();
        int srcScanlineStride = src.getScanlineStride();
        int dst_num_bands = dst.getNumBands();
        int dst_min_x = destRect.x;
        int dst_min_y = destRect.y;
        int dst_max_x = destRect.x + destRect.width;
        int dst_max_y = destRect.y + destRect.height;
        int posy = (s_y - srcTranslateY) * srcScanlineStride;
        int posx = (s_x - srcTranslateX) * srcPixelStride;
        int srcScanlineOffset = posx + posy;
        int dstScanlineOffset = 0;
        for (int y = dst_min_y; y < dst_max_y; ++y) {
            for (int k2 = 0; k2 < dst_num_bands; ++k2) {
                float[] srcDataArray = srcDataArrays[k2];
                float[] dstDataArray = dstDataArrays[k2];
                int dstPixelOffset = dstScanlineOffset + dstBandOffsets[k2];
                int srcPixelOffset = srcScanlineOffset + bandOffsets[k2];
                for (int x = dst_min_x; x < dst_max_x; ++x) {
                    dstDataArray[dstPixelOffset] = srcDataArray[srcPixelOffset];
                    srcPixelOffset += incr1;
                    dstPixelOffset += dstPixelStride;
                }
            }
            srcScanlineOffset += incr2;
            dstScanlineOffset += dstScanlineStride;
        }
    }

    private void doubleLoop(RasterAccessor src, Rectangle destRect, int srcTranslateX, int srcTranslateY, RasterAccessor dst, int incr1, int incr2, int s_x, int s_y) {
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int dnumBands = dst.getNumBands();
        double[][] dstDataArrays = dst.getDoubleDataArrays();
        int[] dstBandOffsets = dst.getBandOffsets();
        int dstPixelStride = dst.getPixelStride();
        int dstScanlineStride = dst.getScanlineStride();
        double[][] srcDataArrays = src.getDoubleDataArrays();
        int[] bandOffsets = src.getOffsetsForBands();
        int srcPixelStride = src.getPixelStride();
        int srcScanlineStride = src.getScanlineStride();
        int dst_num_bands = dst.getNumBands();
        int dst_min_x = destRect.x;
        int dst_min_y = destRect.y;
        int dst_max_x = destRect.x + destRect.width;
        int dst_max_y = destRect.y + destRect.height;
        int posy = (s_y - srcTranslateY) * srcScanlineStride;
        int posx = (s_x - srcTranslateX) * srcPixelStride;
        int srcScanlineOffset = posx + posy;
        int dstScanlineOffset = 0;
        for (int y = dst_min_y; y < dst_max_y; ++y) {
            for (int k2 = 0; k2 < dst_num_bands; ++k2) {
                double[] srcDataArray = srcDataArrays[k2];
                double[] dstDataArray = dstDataArrays[k2];
                int dstPixelOffset = dstScanlineOffset + dstBandOffsets[k2];
                int srcPixelOffset = srcScanlineOffset + bandOffsets[k2];
                for (int x = dst_min_x; x < dst_max_x; ++x) {
                    dstDataArray[dstPixelOffset] = srcDataArray[srcPixelOffset];
                    srcPixelOffset += incr1;
                    dstPixelOffset += dstPixelStride;
                }
            }
            srcScanlineOffset += incr2;
            dstScanlineOffset += dstScanlineStride;
        }
    }
}

