/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.medialib.mlib.Image
 *  com.sun.medialib.mlib.mediaLibImage
 */
package com.sun.media.jai.mlib;

import com.sun.media.jai.mlib.JaiI18N;
import com.sun.media.jai.mlib.MediaLibAccessor;
import com.sun.media.jai.mlib.MlibUtils;
import com.sun.media.jai.util.ImageUtil;
import com.sun.medialib.mlib.Image;
import com.sun.medialib.mlib.mediaLibImage;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.Interpolation;
import javax.media.jai.WarpGrid;
import javax.media.jai.WarpOpImage;

final class MlibWarpGridOpImage
extends WarpOpImage {
    private int xStart;
    private int xStep;
    private int xNumCells;
    private int xEnd;
    private int yStart;
    private int yStep;
    private int yNumCells;
    private int yEnd;
    private float[] xWarpPos;
    private float[] yWarpPos;
    private int filter;

    protected Rectangle backwardMapRect(Rectangle destRect, int sourceIndex) {
        Rectangle wrect = super.backwardMapRect(destRect, sourceIndex);
        wrect.setBounds(wrect.x - 1, wrect.y - 1, wrect.width + 2, wrect.height + 2);
        return wrect;
    }

    public MlibWarpGridOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, WarpGrid warp, Interpolation interp, int filter, double[] backgroundValues) {
        super(source, layout, config, true, extender, interp, warp, backgroundValues);
        this.filter = filter;
        this.xStart = warp.getXStart();
        this.xStep = warp.getXStep();
        this.xNumCells = warp.getXNumCells();
        this.xEnd = this.xStart + this.xStep * this.xNumCells;
        this.yStart = warp.getYStart();
        this.yStep = warp.getYStep();
        this.yNumCells = warp.getYNumCells();
        this.yEnd = this.yStart + this.yStep * this.yNumCells;
        this.xWarpPos = warp.getXWarpPos();
        this.yWarpPos = warp.getYWarpPos();
    }

    public Raster computeTile(int tileX, int tileY) {
        Rectangle srcRect;
        Rectangle rect;
        Point org = new Point(this.tileXToX(tileX), this.tileYToY(tileY));
        WritableRaster dest = this.createWritableRaster(this.sampleModel, org);
        Rectangle rect0 = new Rectangle(org.x, org.y, this.tileWidth, this.tileHeight);
        Rectangle destRect = rect0.intersection(this.computableBounds);
        Rectangle destRect1 = rect0.intersection(this.getBounds());
        if (destRect.isEmpty()) {
            if (this.setBackground) {
                ImageUtil.fillBackground(dest, destRect1, this.backgroundValues);
            }
            return dest;
        }
        if (!destRect1.equals(destRect)) {
            ImageUtil.fillBordersWithBackgroundValues(destRect1, destRect, dest, this.backgroundValues);
        }
        Raster[] sources = new Raster[1];
        Rectangle srcBounds = this.getSourceImage(0).getBounds();
        int x0 = destRect.x;
        int x1 = x0 + destRect.width - 1;
        int y0 = destRect.y;
        int y1 = y0 + destRect.height - 1;
        if (x0 >= this.xEnd || x1 < this.xStart || y0 >= this.yEnd || y1 < this.yStart) {
            Rectangle rect2 = srcBounds.intersection(destRect);
            if (!rect2.isEmpty()) {
                sources[0] = this.getSourceImage(0).getData(rect2);
                this.copyRect(sources, dest, rect2);
                if (this.getSourceImage(0).overlapsMultipleTiles(rect2)) {
                    this.recycleTile(sources[0]);
                }
            }
            return dest;
        }
        if (x0 < this.xStart) {
            rect = srcBounds.intersection(new Rectangle(x0, y0, this.xStart - x0, y1 - y0 + 1));
            if (!rect.isEmpty()) {
                sources[0] = this.getSourceImage(0).getData(rect);
                this.copyRect(sources, dest, rect);
                if (this.getSourceImage(0).overlapsMultipleTiles(rect)) {
                    this.recycleTile(sources[0]);
                }
            }
            x0 = this.xStart;
        }
        if (x1 >= this.xEnd) {
            rect = srcBounds.intersection(new Rectangle(this.xEnd, y0, x1 - this.xEnd + 1, y1 - y0 + 1));
            if (!rect.isEmpty()) {
                sources[0] = this.getSourceImage(0).getData(rect);
                this.copyRect(sources, dest, rect);
                if (this.getSourceImage(0).overlapsMultipleTiles(rect)) {
                    this.recycleTile(sources[0]);
                }
            }
            x1 = this.xEnd - 1;
        }
        if (y0 < this.yStart) {
            rect = srcBounds.intersection(new Rectangle(x0, y0, x1 - x0 + 1, this.yStart - y0));
            if (!rect.isEmpty()) {
                sources[0] = this.getSourceImage(0).getData(rect);
                this.copyRect(sources, dest, rect);
                if (this.getSourceImage(0).overlapsMultipleTiles(rect)) {
                    this.recycleTile(sources[0]);
                }
            }
            y0 = this.yStart;
        }
        if (y1 >= this.yEnd) {
            rect = srcBounds.intersection(new Rectangle(x0, this.yEnd, x1 - x0 + 1, y1 - this.yEnd + 1));
            if (!rect.isEmpty()) {
                sources[0] = this.getSourceImage(0).getData(rect);
                this.copyRect(sources, dest, rect);
                if (this.getSourceImage(0).overlapsMultipleTiles(rect)) {
                    this.recycleTile(sources[0]);
                }
            }
            y1 = this.yEnd - 1;
        }
        if (!(srcRect = this.backwardMapRect(destRect = new Rectangle(x0, y0, x1 - x0 + 1, y1 - y0 + 1), 0).intersection(srcBounds)).isEmpty()) {
            int l = this.interp == null ? 0 : this.interp.getLeftPadding();
            int r = this.interp == null ? 0 : this.interp.getRightPadding();
            int t = this.interp == null ? 0 : this.interp.getTopPadding();
            int b = this.interp == null ? 0 : this.interp.getBottomPadding();
            srcRect = new Rectangle(srcRect.x - l, srcRect.y - t, srcRect.width + l + r, srcRect.height + t + b);
            sources[0] = this.getBorderExtender() != null ? this.getSourceImage(0).getExtendedData(srcRect, this.extender) : this.getSourceImage(0).getData(srcRect);
            this.computeRect(sources, dest, destRect);
            if (this.getSourceImage(0).overlapsMultipleTiles(srcRect)) {
                this.recycleTile(sources[0]);
            }
        }
        return dest;
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        int formatTag = MediaLibAccessor.findCompatibleTag(sources, dest);
        Raster source = sources[0];
        MediaLibAccessor srcMA = new MediaLibAccessor(source, source.getBounds(), formatTag);
        MediaLibAccessor dstMA = new MediaLibAccessor(dest, destRect, formatTag);
        mediaLibImage[] srcMLI = srcMA.getMediaLibImages();
        mediaLibImage[] dstMLI = dstMA.getMediaLibImages();
        switch (dstMA.getDataType()) {
            case 0: 
            case 1: 
            case 2: 
            case 3: {
                if (this.setBackground) {
                    for (int i = 0; i < dstMLI.length; ++i) {
                        Image.GridWarp2((mediaLibImage)dstMLI[i], (mediaLibImage)srcMLI[i], (float[])this.xWarpPos, (float[])this.yWarpPos, (double)source.getMinX(), (double)source.getMinY(), (int)(this.xStart - destRect.x), (int)this.xStep, (int)this.xNumCells, (int)(this.yStart - destRect.y), (int)this.yStep, (int)this.yNumCells, (int)this.filter, (int)0, (int[])this.intBackgroundValues);
                    }
                } else {
                    for (int i = 0; i < dstMLI.length; ++i) {
                        Image.GridWarp((mediaLibImage)dstMLI[i], (mediaLibImage)srcMLI[i], (float[])this.xWarpPos, (float[])this.yWarpPos, (double)source.getMinX(), (double)source.getMinY(), (int)(this.xStart - destRect.x), (int)this.xStep, (int)this.xNumCells, (int)(this.yStart - destRect.y), (int)this.yStep, (int)this.yNumCells, (int)this.filter, (int)0);
                        MlibUtils.clampImage(dstMLI[i], this.getColorModel());
                    }
                }
                break;
            }
            case 4: 
            case 5: {
                if (this.setBackground) {
                    for (int i = 0; i < dstMLI.length; ++i) {
                        Image.GridWarp2_Fp((mediaLibImage)dstMLI[i], (mediaLibImage)srcMLI[i], (float[])this.xWarpPos, (float[])this.yWarpPos, (double)source.getMinX(), (double)source.getMinY(), (int)(this.xStart - destRect.x), (int)this.xStep, (int)this.xNumCells, (int)(this.yStart - destRect.y), (int)this.yStep, (int)this.yNumCells, (int)this.filter, (int)0, (double[])this.backgroundValues);
                    }
                } else {
                    for (int i = 0; i < dstMLI.length; ++i) {
                        Image.GridWarp_Fp((mediaLibImage)dstMLI[i], (mediaLibImage)srcMLI[i], (float[])this.xWarpPos, (float[])this.yWarpPos, (double)source.getMinX(), (double)source.getMinY(), (int)(this.xStart - destRect.x), (int)this.xStep, (int)this.xNumCells, (int)(this.yStart - destRect.y), (int)this.yStep, (int)this.yNumCells, (int)this.filter, (int)0);
                    }
                }
                break;
            }
            default: {
                throw new RuntimeException(JaiI18N.getString("Generic2"));
            }
        }
        if (dstMA.isDataCopy()) {
            dstMA.clampDataArrays();
            dstMA.copyDataToRaster();
        }
    }

    private void copyRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        int formatTag = MediaLibAccessor.findCompatibleTag(sources, dest);
        MediaLibAccessor srcMA = new MediaLibAccessor(sources[0], destRect, formatTag);
        MediaLibAccessor dstMA = new MediaLibAccessor(dest, destRect, formatTag);
        mediaLibImage[] srcMLI = srcMA.getMediaLibImages();
        mediaLibImage[] dstMLI = dstMA.getMediaLibImages();
        for (int i = 0; i < dstMLI.length; ++i) {
            Image.Copy((mediaLibImage)dstMLI[i], (mediaLibImage)srcMLI[i]);
        }
        if (dstMA.isDataCopy()) {
            dstMA.copyDataToRaster();
        }
    }
}

