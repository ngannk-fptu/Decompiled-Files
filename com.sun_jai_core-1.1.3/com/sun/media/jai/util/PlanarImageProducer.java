/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.util;

import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageProducer;
import java.awt.image.Raster;
import java.util.Vector;
import javax.media.jai.PlanarImage;

public class PlanarImageProducer
implements ImageProducer {
    PlanarImage im;
    Vector consumers = new Vector();

    public PlanarImageProducer(PlanarImage im) {
        this.im = im.createSnapshot();
    }

    public void addConsumer(ImageConsumer ic) {
        if (!this.consumers.contains(ic)) {
            this.consumers.add(ic);
        }
        this.produceImage();
    }

    public boolean isConsumer(ImageConsumer ic) {
        return this.consumers.contains(ic);
    }

    public void removeConsumer(ImageConsumer ic) {
        this.consumers.remove(ic);
    }

    public void requestTopDownLeftRightResend(ImageConsumer ic) {
        this.startProduction(ic);
    }

    public void startProduction(ImageConsumer ic) {
        if (!this.consumers.contains(ic)) {
            this.consumers.add(ic);
        }
        this.produceImage();
    }

    private synchronized void produceImage() {
        ImageConsumer ic;
        int i;
        int numConsumers = this.consumers.size();
        int minX = this.im.getMinX();
        int minY = this.im.getMinY();
        int width = this.im.getWidth();
        int height = this.im.getHeight();
        int numBands = this.im.getSampleModel().getNumBands();
        int scansize = width * numBands;
        ColorModel colorModel = this.im.getColorModel();
        int[] pixels = new int[scansize];
        Rectangle rect = new Rectangle(minX, minY, width, 1);
        for (i = 0; i < numConsumers; ++i) {
            ic = (ImageConsumer)this.consumers.elementAt(i);
            ic.setHints(22);
        }
        for (int y = minY; y < minY + height; ++y) {
            rect.y = y;
            Raster row = this.im.getData(rect);
            row.getPixels(minX, y, width, 1, pixels);
            for (int i2 = 0; i2 < numConsumers; ++i2) {
                ImageConsumer ic2 = (ImageConsumer)this.consumers.elementAt(i2);
                ic2.setPixels(0, y - minY, width, 1, colorModel, pixels, 0, scansize);
            }
        }
        for (i = 0; i < numConsumers; ++i) {
            ic = (ImageConsumer)this.consumers.elementAt(i);
            ic.imageComplete(3);
        }
    }
}

