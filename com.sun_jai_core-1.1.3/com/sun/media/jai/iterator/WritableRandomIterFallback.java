/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.iterator;

import com.sun.media.jai.iterator.RandomIterFallback;
import java.awt.Rectangle;
import java.awt.image.WritableRaster;
import java.awt.image.WritableRenderedImage;
import javax.media.jai.iterator.WritableRandomIter;

public final class WritableRandomIterFallback
extends RandomIterFallback
implements WritableRandomIter {
    WritableRenderedImage wim;

    public WritableRandomIterFallback(WritableRenderedImage im, Rectangle bounds) {
        super(im, bounds);
        this.wim = im;
    }

    private void makeCurrentWritable(int xLocal, int yLocal) {
        int xIDNew = this.xTiles[xLocal];
        int yIDNew = this.yTiles[yLocal];
        if (xIDNew != this.xID || yIDNew != this.yID || this.dataBuffer == null) {
            if (this.dataBuffer != null) {
                this.wim.releaseWritableTile(this.xID, this.yID);
            }
            this.xID = xIDNew;
            this.yID = yIDNew;
            WritableRaster tile = this.wim.getWritableTile(this.xID, this.yID);
            this.dataBuffer = tile.getDataBuffer();
            this.sampleModelTranslateX = tile.getSampleModelTranslateX();
            this.sampleModelTranslateY = tile.getSampleModelTranslateY();
        }
    }

    public void setSample(int x, int y, int b, int s) {
        this.makeCurrentWritable(x - this.boundsX, y - this.boundsY);
        this.sampleModel.setSample(x - this.sampleModelTranslateX, y - this.sampleModelTranslateY, b, s, this.dataBuffer);
    }

    public void setSample(int x, int y, int b, float s) {
        this.makeCurrentWritable(x - this.boundsX, y - this.boundsY);
        this.sampleModel.setSample(x - this.sampleModelTranslateX, y - this.sampleModelTranslateY, b, s, this.dataBuffer);
    }

    public void setSample(int x, int y, int b, double s) {
        this.makeCurrentWritable(x - this.boundsX, y - this.boundsY);
        this.sampleModel.setSample(x - this.sampleModelTranslateX, y - this.sampleModelTranslateY, b, s, this.dataBuffer);
    }

    public void setPixel(int x, int y, int[] iArray) {
        this.makeCurrentWritable(x - this.boundsX, y - this.boundsY);
        this.sampleModel.setPixel(x - this.sampleModelTranslateX, y - this.sampleModelTranslateY, iArray, this.dataBuffer);
    }

    public void setPixel(int x, int y, float[] fArray) {
        this.makeCurrentWritable(x - this.boundsX, y - this.boundsY);
        this.sampleModel.setPixel(x - this.sampleModelTranslateX, y - this.sampleModelTranslateY, fArray, this.dataBuffer);
    }

    public void setPixel(int x, int y, double[] dArray) {
        this.makeCurrentWritable(x - this.boundsX, y - this.boundsY);
        this.sampleModel.setPixel(x - this.sampleModelTranslateX, y - this.sampleModelTranslateY, dArray, this.dataBuffer);
    }

    public void done() {
        if (this.dataBuffer != null) {
            this.wim.releaseWritableTile(this.xID, this.yID);
        }
        this.dataBuffer = null;
    }
}

