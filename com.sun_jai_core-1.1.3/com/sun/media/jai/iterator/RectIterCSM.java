/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.iterator;

import com.sun.media.jai.iterator.JaiI18N;
import com.sun.media.jai.iterator.RectIterFallback;
import java.awt.Rectangle;
import java.awt.image.ComponentSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import javax.media.jai.PlanarImage;

public abstract class RectIterCSM
extends RectIterFallback {
    protected int[] bankIndices;
    protected int scanlineStride;
    protected int pixelStride;
    protected int[] bandOffsets;
    protected int[] DBOffsets;
    protected int offset;
    protected int bandOffset;

    public RectIterCSM(RenderedImage im, Rectangle bounds) {
        super(im, bounds);
        ComponentSampleModel csm = (ComponentSampleModel)this.sampleModel;
        this.scanlineStride = csm.getScanlineStride();
        this.pixelStride = csm.getPixelStride();
        this.bankIndices = csm.getBankIndices();
        int[] bo = csm.getBandOffsets();
        this.bandOffsets = new int[this.numBands + 1];
        for (int i = 0; i < this.numBands; ++i) {
            this.bandOffsets[i] = bo[i];
        }
        this.bandOffsets[this.numBands] = 0;
        this.DBOffsets = new int[this.numBands];
        this.offset = (this.y - this.sampleModelTranslateY) * this.scanlineStride + (this.x - this.sampleModelTranslateX) * this.pixelStride;
        this.bandOffset = this.bandOffsets[0];
    }

    protected void dataBufferChanged() {
    }

    protected void adjustBandOffsets() {
        int[] newDBOffsets = this.dataBuffer.getOffsets();
        int i = 0;
        while (i < this.numBands) {
            int bankNum = this.bankIndices[i];
            int n = i++;
            this.bandOffsets[n] = this.bandOffsets[n] + (newDBOffsets[bankNum] - this.DBOffsets[bankNum]);
        }
        this.DBOffsets = newDBOffsets;
    }

    protected void setDataBuffer() {
        Raster tile = this.im.getTile(this.tileX, this.tileY);
        this.dataBuffer = tile.getDataBuffer();
        this.dataBufferChanged();
        int newSampleModelTranslateX = tile.getSampleModelTranslateX();
        int newSampleModelTranslateY = tile.getSampleModelTranslateY();
        int deltaX = this.sampleModelTranslateX - newSampleModelTranslateX;
        int deltaY = this.sampleModelTranslateY - newSampleModelTranslateY;
        this.offset += deltaY * this.scanlineStride + deltaX * this.pixelStride;
        this.sampleModelTranslateX = newSampleModelTranslateX;
        this.sampleModelTranslateY = newSampleModelTranslateY;
    }

    public void startLines() {
        this.offset += (this.bounds.y - this.y) * this.scanlineStride;
        this.y = this.bounds.y;
        this.tileY = this.startTileY;
        this.setTileYBounds();
        this.setDataBuffer();
    }

    public void nextLine() {
        ++this.y;
        this.offset += this.scanlineStride;
    }

    public void jumpLines(int num) {
        int jumpY = this.y + num;
        if (jumpY < this.bounds.y || jumpY > this.lastY) {
            throw new IndexOutOfBoundsException(JaiI18N.getString("RectIterFallback1"));
        }
        this.y = jumpY;
        this.offset += num * this.scanlineStride;
        if (this.y < this.prevYBoundary || this.y > this.nextYBoundary) {
            this.tileY = PlanarImage.YToTileY(this.y, this.tileGridYOffset, this.tileHeight);
            this.setTileYBounds();
            this.setDataBuffer();
        }
    }

    public void startPixels() {
        this.offset += (this.bounds.x - this.x) * this.pixelStride;
        this.x = this.bounds.x;
        this.tileX = this.startTileX;
        this.setTileXBounds();
        this.setDataBuffer();
    }

    public void nextPixel() {
        ++this.x;
        this.offset += this.pixelStride;
    }

    public void jumpPixels(int num) {
        int jumpX = this.x + num;
        if (jumpX < this.bounds.x || jumpX > this.lastX) {
            throw new IndexOutOfBoundsException(JaiI18N.getString("RectIterFallback0"));
        }
        this.x = jumpX;
        this.offset += num * this.pixelStride;
        if (this.x < this.prevXBoundary || this.x > this.nextXBoundary) {
            this.tileX = PlanarImage.XToTileX(this.x, this.tileGridXOffset, this.tileWidth);
            this.setTileXBounds();
            this.setDataBuffer();
        }
    }

    public void startBands() {
        this.b = 0;
        this.bandOffset = this.bandOffsets[0];
    }

    public void nextBand() {
        ++this.b;
        this.bandOffset = this.bandOffsets[this.b];
    }
}

