/*
 * Decompiled with CFR 0.152.
 */
package org.j3d.util;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.WritableRaster;
import java.util.Hashtable;

public class ImageGenerator
implements ImageConsumer {
    private final Object holder = new Object();
    private ColorModel colorModel;
    private WritableRaster raster;
    private int width = -1;
    private int height = -1;
    private BufferedImage image;
    private int[] intBuffer;
    private volatile boolean loadComplete = false;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void imageComplete(int status) {
        Object object = this.holder;
        synchronized (object) {
            this.loadComplete = true;
            this.holder.notify();
        }
    }

    @Override
    public void setColorModel(ColorModel model) {
        this.colorModel = model;
        this.createImage();
    }

    @Override
    public void setDimensions(int w, int h) {
        this.width = w;
        this.height = h;
        this.createImage();
    }

    @Override
    public void setHints(int flags) {
    }

    @Override
    public void setPixels(int x, int y, int w, int h, ColorModel model, byte[] pixels, int offset, int scansize) {
        if (this.intBuffer == null || pixels.length > this.intBuffer.length) {
            this.intBuffer = new int[pixels.length];
        }
        int i = pixels.length;
        while (--i >= 0) {
            this.intBuffer[i] = pixels[i] & 0xFF;
        }
        this.raster.setPixels(x, y, w, h, this.intBuffer);
    }

    @Override
    public void setPixels(int x, int y, int w, int h, ColorModel model, int[] pixels, int offset, int scansize) {
        this.image.setRGB(x, y, w, h, pixels, offset, scansize);
    }

    public void setProperties(Hashtable props) {
        this.createImage();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public BufferedImage getImage() {
        if (!this.loadComplete) {
            Object object = this.holder;
            synchronized (object) {
                try {
                    this.holder.wait();
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
            }
        }
        return this.image;
    }

    private void createImage() {
        if (this.image != null || this.width == -1 || this.colorModel == null) {
            return;
        }
        boolean hasAlpha = this.colorModel.hasAlpha() || this.colorModel.getTransparency() != 1;
        this.image = new BufferedImage(this.width, this.height, hasAlpha ? 2 : 1);
    }
}

