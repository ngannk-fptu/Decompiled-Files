/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.iterator;

import java.awt.Rectangle;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import javax.media.jai.PlanarImage;
import javax.media.jai.iterator.RandomIter;

public class RandomIterFallback
implements RandomIter {
    protected RenderedImage im;
    protected Rectangle boundsRect;
    protected SampleModel sampleModel;
    protected int xID;
    protected int yID;
    protected int sampleModelTranslateX;
    protected int sampleModelTranslateY;
    protected DataBuffer dataBuffer = null;
    protected int boundsX;
    protected int boundsY;
    protected int[] xTiles;
    protected int[] yTiles;

    public RandomIterFallback(RenderedImage im, Rectangle bounds) {
        this.im = im;
        Rectangle imBounds = new Rectangle(im.getMinX(), im.getMinY(), im.getWidth(), im.getHeight());
        this.boundsRect = imBounds.intersection(bounds);
        this.sampleModel = im.getSampleModel();
        int x = this.boundsRect.x;
        int y = this.boundsRect.y;
        int width = this.boundsRect.width;
        int height = this.boundsRect.height;
        this.boundsX = this.boundsRect.x;
        this.boundsY = this.boundsRect.y;
        this.xTiles = new int[width];
        this.yTiles = new int[height];
        int tileWidth = im.getTileWidth();
        int tileGridXOffset = im.getTileGridXOffset();
        int minTileX = PlanarImage.XToTileX(x, tileGridXOffset, tileWidth);
        int offsetX = x - PlanarImage.tileXToX(minTileX, tileGridXOffset, tileWidth);
        int tileX = minTileX;
        for (int i = 0; i < width; ++i) {
            this.xTiles[i] = tileX++;
            if (++offsetX != tileWidth) continue;
            offsetX = 0;
        }
        int tileHeight = im.getTileHeight();
        int tileGridYOffset = im.getTileGridYOffset();
        int minTileY = PlanarImage.YToTileY(y, tileGridYOffset, tileHeight);
        int offsetY = y - PlanarImage.tileYToY(minTileY, tileGridYOffset, tileHeight);
        int tileY = minTileY;
        for (int i = 0; i < height; ++i) {
            this.yTiles[i] = tileY++;
            if (++offsetY != tileHeight) continue;
            offsetY = 0;
        }
    }

    private void makeCurrent(int xLocal, int yLocal) {
        int xIDNew = this.xTiles[xLocal];
        int yIDNew = this.yTiles[yLocal];
        if (xIDNew != this.xID || yIDNew != this.yID || this.dataBuffer == null) {
            this.xID = xIDNew;
            this.yID = yIDNew;
            Raster tile = this.im.getTile(this.xID, this.yID);
            this.dataBuffer = tile.getDataBuffer();
            this.sampleModelTranslateX = tile.getSampleModelTranslateX();
            this.sampleModelTranslateY = tile.getSampleModelTranslateY();
        }
    }

    public int getSample(int x, int y, int b) {
        this.makeCurrent(x - this.boundsX, y - this.boundsY);
        return this.sampleModel.getSample(x - this.sampleModelTranslateX, y - this.sampleModelTranslateY, b, this.dataBuffer);
    }

    public float getSampleFloat(int x, int y, int b) {
        this.makeCurrent(x - this.boundsX, y - this.boundsY);
        return this.sampleModel.getSampleFloat(x - this.sampleModelTranslateX, y - this.sampleModelTranslateY, b, this.dataBuffer);
    }

    public double getSampleDouble(int x, int y, int b) {
        this.makeCurrent(x - this.boundsX, y - this.boundsY);
        return this.sampleModel.getSampleDouble(x - this.sampleModelTranslateX, y - this.sampleModelTranslateY, b, this.dataBuffer);
    }

    public int[] getPixel(int x, int y, int[] iArray) {
        this.makeCurrent(x - this.boundsX, y - this.boundsY);
        return this.sampleModel.getPixel(x - this.sampleModelTranslateX, y - this.sampleModelTranslateY, iArray, this.dataBuffer);
    }

    public float[] getPixel(int x, int y, float[] fArray) {
        this.makeCurrent(x - this.boundsX, y - this.boundsY);
        return this.sampleModel.getPixel(x - this.sampleModelTranslateX, y - this.sampleModelTranslateY, fArray, this.dataBuffer);
    }

    public double[] getPixel(int x, int y, double[] dArray) {
        this.makeCurrent(x - this.boundsX, y - this.boundsY);
        return this.sampleModel.getPixel(x - this.sampleModelTranslateX, y - this.sampleModelTranslateY, dArray, this.dataBuffer);
    }

    public void done() {
        this.xTiles = null;
        this.yTiles = null;
        this.dataBuffer = null;
    }
}

