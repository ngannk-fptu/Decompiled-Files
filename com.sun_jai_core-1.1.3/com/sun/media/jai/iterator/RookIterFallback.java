/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.iterator;

import java.awt.Rectangle;
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import javax.media.jai.PlanarImage;
import javax.media.jai.iterator.RookIter;

public class RookIterFallback
implements RookIter {
    protected RenderedImage im;
    protected Rectangle bounds;
    protected SampleModel sampleModel;
    protected int numBands;
    protected int tileWidth;
    protected int tileHeight;
    protected int tileGridXOffset;
    protected int tileGridYOffset;
    protected int startTileX;
    protected int endTileX;
    protected int startTileY;
    protected int endTileY;
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
    protected int firstX;
    protected int firstY;
    protected int lastX;
    protected int lastY;
    protected int x;
    protected int y;
    protected int localX;
    protected int localY;
    protected int b;
    protected DataBuffer dataBuffer = null;

    public RookIterFallback(RenderedImage im, Rectangle bounds) {
        this.im = im;
        this.bounds = bounds;
        this.sampleModel = im.getSampleModel();
        this.numBands = this.sampleModel.getNumBands();
        this.tileGridXOffset = im.getTileGridXOffset();
        this.tileGridYOffset = im.getTileGridYOffset();
        this.tileWidth = im.getTileWidth();
        this.tileHeight = im.getTileHeight();
        this.startTileX = PlanarImage.XToTileX(bounds.x, this.tileGridXOffset, this.tileWidth);
        this.endTileX = PlanarImage.XToTileX(bounds.x + bounds.width - 1, this.tileGridXOffset, this.tileWidth);
        this.startTileY = PlanarImage.YToTileY(bounds.y, this.tileGridYOffset, this.tileHeight);
        this.endTileY = PlanarImage.YToTileY(bounds.y + bounds.height - 1, this.tileGridYOffset, this.tileHeight);
        this.tileX = this.startTileX;
        this.tileY = this.startTileY;
        this.firstX = bounds.x;
        this.firstY = bounds.y;
        this.lastX = bounds.x + bounds.width - 1;
        this.lastY = bounds.y + bounds.height - 1;
        this.x = bounds.x;
        this.y = bounds.y;
        this.b = 0;
        this.setTileXBounds();
        this.setTileYBounds();
        this.setDataBuffer();
    }

    private final void setTileXBounds() {
        this.tileXStart = this.tileX * this.tileWidth + this.tileGridXOffset;
        this.tileXEnd = this.tileXStart + this.tileWidth - 1;
        this.localX = this.x - this.tileXStart;
        this.prevXBoundary = Math.max(this.tileXStart, this.firstX);
        this.nextXBoundary = Math.min(this.tileXEnd, this.lastX);
    }

    private final void setTileYBounds() {
        this.tileYStart = this.tileY * this.tileHeight + this.tileGridYOffset;
        this.tileYEnd = this.tileYStart + this.tileHeight - 1;
        this.localY = this.y - this.tileYStart;
        this.prevYBoundary = Math.max(this.tileYStart, this.firstY);
        this.nextYBoundary = Math.min(this.tileYEnd, this.lastY);
    }

    private final void setDataBuffer() {
        this.dataBuffer = this.im.getTile(this.tileX, this.tileY).getDataBuffer();
    }

    public void startLines() {
        this.y = this.firstY;
        this.localY = this.y - this.tileYStart;
        this.tileY = this.startTileY;
        this.setTileYBounds();
        this.setDataBuffer();
    }

    public void endLines() {
        this.y = this.lastY;
        this.localY = this.y - this.tileYStart;
        this.tileY = this.endTileY;
        this.setTileYBounds();
        this.setDataBuffer();
    }

    public void nextLine() {
        ++this.y;
        ++this.localY;
    }

    public void prevLine() {
        --this.y;
        --this.localY;
    }

    public void jumpLines(int num) {
        this.y += num;
        this.localY += num;
        if (this.y < this.tileYStart || this.y > this.tileYEnd) {
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
            this.localY -= this.tileHeight;
            this.prevYBoundary = Math.max(this.tileYStart, this.firstY);
            this.nextYBoundary = Math.min(this.tileYEnd, this.lastY);
            this.setDataBuffer();
            return false;
        }
        return false;
    }

    public boolean finishedLinesTop() {
        if (this.y < this.prevYBoundary) {
            if (this.y < this.firstY) {
                return true;
            }
            --this.tileY;
            this.tileYStart -= this.tileHeight;
            this.tileYEnd -= this.tileHeight;
            this.localY += this.tileHeight;
            this.prevYBoundary = Math.max(this.tileYStart, this.firstY);
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

    public boolean prevLineDone() {
        this.prevLine();
        return this.finishedLinesTop();
    }

    public void startPixels() {
        this.x = this.firstX;
        this.localX = this.x - this.tileXStart;
        this.tileX = this.startTileX;
        this.setTileXBounds();
        this.setDataBuffer();
    }

    public void endPixels() {
        this.x = this.lastX;
        this.tileX = this.endTileX;
        this.setTileXBounds();
        this.setDataBuffer();
    }

    public void nextPixel() {
        ++this.x;
        ++this.localX;
    }

    public void prevPixel() {
        --this.x;
        --this.localX;
    }

    public void jumpPixels(int num) {
        this.x += num;
        this.localX += num;
        if (this.x < this.tileXStart || this.x > this.tileXEnd) {
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
            this.localX -= this.tileWidth;
            this.prevXBoundary = Math.max(this.tileXStart, this.firstX);
            this.nextXBoundary = Math.min(this.tileXEnd, this.lastX);
            this.setDataBuffer();
            return false;
        }
        return false;
    }

    public boolean finishedPixelsLeft() {
        if (this.x < this.prevXBoundary) {
            if (this.x < this.firstX) {
                return true;
            }
            --this.tileX;
            this.tileXStart -= this.tileWidth;
            this.tileXEnd -= this.tileWidth;
            this.localX += this.tileWidth;
            this.prevXBoundary = Math.max(this.tileXStart, this.firstX);
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

    public boolean prevPixelDone() {
        this.prevPixel();
        return this.finishedPixelsLeft();
    }

    public void startBands() {
        this.b = 0;
    }

    public void endBands() {
        this.b = this.numBands - 1;
    }

    public void prevBand() {
        --this.b;
    }

    public void nextBand() {
        ++this.b;
    }

    public boolean prevBandDone() {
        return --this.b < 0;
    }

    public boolean nextBandDone() {
        return ++this.b >= this.numBands;
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

