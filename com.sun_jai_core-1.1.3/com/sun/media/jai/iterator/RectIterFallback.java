/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.iterator;

import com.sun.media.jai.iterator.JaiI18N;
import java.awt.Rectangle;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import javax.media.jai.PlanarImage;
import javax.media.jai.iterator.RectIter;

public class RectIterFallback
implements RectIter {
    protected RenderedImage im;
    protected Rectangle bounds;
    protected SampleModel sampleModel;
    protected int numBands;
    protected int tileWidth;
    protected int tileHeight;
    protected int tileGridXOffset;
    protected int tileGridYOffset;
    protected int startTileX;
    protected int startTileY;
    protected int tileXStart;
    protected int tileXEnd;
    protected int tileYStart;
    protected int tileYEnd;
    protected int prevXBoundary;
    protected int nextXBoundary;
    protected int prevYBoundary;
    protected int nextYBoundary;
    protected int tileX;
    protected int tileY;
    protected int lastX;
    protected int lastY;
    protected int x;
    protected int y;
    protected int localX;
    protected int localY;
    protected int sampleModelTranslateX = 0;
    protected int sampleModelTranslateY = 0;
    protected int b;
    protected DataBuffer dataBuffer = null;

    public RectIterFallback(RenderedImage im, Rectangle bounds) {
        this.im = im;
        this.bounds = bounds;
        this.sampleModel = im.getSampleModel();
        this.numBands = this.sampleModel.getNumBands();
        this.tileGridXOffset = im.getTileGridXOffset();
        this.tileGridYOffset = im.getTileGridYOffset();
        this.tileWidth = im.getTileWidth();
        this.tileHeight = im.getTileHeight();
        this.startTileX = PlanarImage.XToTileX(bounds.x, this.tileGridXOffset, this.tileWidth);
        this.startTileY = PlanarImage.YToTileY(bounds.y, this.tileGridYOffset, this.tileHeight);
        this.tileX = this.startTileX;
        this.tileY = this.startTileY;
        this.lastX = bounds.x + bounds.width - 1;
        this.lastY = bounds.y + bounds.height - 1;
        this.localX = this.x = bounds.x;
        this.localY = this.y = bounds.y;
        this.b = 0;
        this.setTileXBounds();
        this.setTileYBounds();
        this.setDataBuffer();
    }

    protected final void setTileXBounds() {
        this.tileXStart = this.tileX * this.tileWidth + this.tileGridXOffset;
        this.tileXEnd = this.tileXStart + this.tileWidth - 1;
        this.prevXBoundary = Math.max(this.tileXStart, this.bounds.x);
        this.nextXBoundary = Math.min(this.tileXEnd, this.lastX);
    }

    protected final void setTileYBounds() {
        this.tileYStart = this.tileY * this.tileHeight + this.tileGridYOffset;
        this.tileYEnd = this.tileYStart + this.tileHeight - 1;
        this.prevYBoundary = Math.max(this.tileYStart, this.bounds.y);
        this.nextYBoundary = Math.min(this.tileYEnd, this.lastY);
    }

    protected void setDataBuffer() {
        Raster tile = this.im.getTile(this.tileX, this.tileY);
        this.dataBuffer = tile.getDataBuffer();
        int newSampleModelTranslateX = tile.getSampleModelTranslateX();
        int newSampleModelTranslateY = tile.getSampleModelTranslateY();
        this.localX += this.sampleModelTranslateX - newSampleModelTranslateX;
        this.localY += this.sampleModelTranslateY - newSampleModelTranslateY;
        this.sampleModelTranslateX = newSampleModelTranslateX;
        this.sampleModelTranslateY = newSampleModelTranslateY;
    }

    public void startLines() {
        this.y = this.bounds.y;
        this.localY = this.y - this.sampleModelTranslateY;
        this.tileY = this.startTileY;
        this.setTileYBounds();
        this.setDataBuffer();
    }

    public void nextLine() {
        ++this.y;
        ++this.localY;
    }

    public void jumpLines(int num) {
        int jumpY = this.y + num;
        if (jumpY < this.bounds.y || jumpY > this.lastY) {
            throw new IndexOutOfBoundsException(JaiI18N.getString("RectIterFallback1"));
        }
        this.y = jumpY;
        this.localY += num;
        if (this.y < this.prevYBoundary || this.y > this.nextYBoundary) {
            this.tileY = PlanarImage.YToTileY(this.y, this.tileGridYOffset, this.tileHeight);
            this.setTileYBounds();
            this.setDataBuffer();
        }
    }

    public boolean finishedLines() {
        if (this.y > this.nextYBoundary) {
            if (this.y > this.lastY) {
                return true;
            }
            ++this.tileY;
            this.tileYStart += this.tileHeight;
            this.tileYEnd += this.tileHeight;
            this.prevYBoundary = Math.max(this.tileYStart, this.bounds.y);
            this.nextYBoundary = Math.min(this.tileYEnd, this.lastY);
            this.setDataBuffer();
            return false;
        }
        return false;
    }

    public boolean nextLineDone() {
        this.nextLine();
        return this.finishedLines();
    }

    public void startPixels() {
        this.x = this.bounds.x;
        this.localX = this.x - this.sampleModelTranslateX;
        this.tileX = this.startTileX;
        this.setTileXBounds();
        this.setDataBuffer();
    }

    public void nextPixel() {
        ++this.x;
        ++this.localX;
    }

    public void jumpPixels(int num) {
        int jumpX = this.x + num;
        if (jumpX < this.bounds.x || jumpX > this.lastX) {
            throw new IndexOutOfBoundsException(JaiI18N.getString("RectIterFallback0"));
        }
        this.x = jumpX;
        this.localX += num;
        if (this.x < this.prevXBoundary || this.x > this.nextXBoundary) {
            this.tileX = PlanarImage.XToTileX(this.x, this.tileGridXOffset, this.tileWidth);
            this.setTileXBounds();
            this.setDataBuffer();
        }
    }

    public boolean finishedPixels() {
        if (this.x > this.nextXBoundary) {
            if (this.x > this.lastX) {
                return true;
            }
            ++this.tileX;
            this.tileXStart += this.tileWidth;
            this.tileXEnd += this.tileWidth;
            this.prevXBoundary = Math.max(this.tileXStart, this.bounds.x);
            this.nextXBoundary = Math.min(this.tileXEnd, this.lastX);
            this.setDataBuffer();
            return false;
        }
        return false;
    }

    public boolean nextPixelDone() {
        this.nextPixel();
        return this.finishedPixels();
    }

    public void startBands() {
        this.b = 0;
    }

    public void nextBand() {
        ++this.b;
    }

    public boolean nextBandDone() {
        this.nextBand();
        return this.finishedBands();
    }

    public boolean finishedBands() {
        return this.b >= this.numBands;
    }

    public int getSample() {
        return this.sampleModel.getSample(this.localX, this.localY, this.b, this.dataBuffer);
    }

    public int getSample(int b) {
        return this.sampleModel.getSample(this.localX, this.localY, b, this.dataBuffer);
    }

    public float getSampleFloat() {
        return this.sampleModel.getSampleFloat(this.localX, this.localY, this.b, this.dataBuffer);
    }

    public float getSampleFloat(int b) {
        return this.sampleModel.getSampleFloat(this.localX, this.localY, b, this.dataBuffer);
    }

    public double getSampleDouble() {
        return this.sampleModel.getSampleDouble(this.localX, this.localY, this.b, this.dataBuffer);
    }

    public double getSampleDouble(int b) {
        return this.sampleModel.getSampleDouble(this.localX, this.localY, b, this.dataBuffer);
    }

    public int[] getPixel(int[] iArray) {
        return this.sampleModel.getPixel(this.localX, this.localY, iArray, this.dataBuffer);
    }

    public float[] getPixel(float[] fArray) {
        return this.sampleModel.getPixel(this.localX, this.localY, fArray, this.dataBuffer);
    }

    public double[] getPixel(double[] dArray) {
        return this.sampleModel.getPixel(this.localX, this.localY, dArray, this.dataBuffer);
    }
}

