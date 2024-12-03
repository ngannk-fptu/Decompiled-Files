/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.common;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.RasterFormatException;
import java.awt.image.WritableRaster;
import java.util.Properties;

public class ImageBuilder {
    private final int[] data;
    private final int width;
    private final int height;
    private final boolean hasAlpha;

    public ImageBuilder(int width, int height, boolean hasAlpha) {
        if (width <= 0) {
            throw new RasterFormatException("zero or negative width value");
        }
        if (height <= 0) {
            throw new RasterFormatException("zero or negative height value");
        }
        this.data = new int[width * height];
        this.width = width;
        this.height = height;
        this.hasAlpha = hasAlpha;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getRGB(int x, int y) {
        int rowOffset = y * this.width;
        return this.data[rowOffset + x];
    }

    public void setRGB(int x, int y, int argb) {
        int rowOffset = y * this.width;
        this.data[rowOffset + x] = argb;
    }

    public BufferedImage getBufferedImage() {
        return this.makeBufferedImage(this.data, this.width, this.height, this.hasAlpha);
    }

    public BufferedImage getSubimage(int x, int y, int w, int h) {
        if (w <= 0) {
            throw new RasterFormatException("negative or zero subimage width");
        }
        if (h <= 0) {
            throw new RasterFormatException("negative or zero subimage height");
        }
        if (x < 0 || x >= this.width) {
            throw new RasterFormatException("subimage x is outside raster");
        }
        if (x + w > this.width) {
            throw new RasterFormatException("subimage (x+width) is outside raster");
        }
        if (y < 0 || y >= this.height) {
            throw new RasterFormatException("subimage y is outside raster");
        }
        if (y + h > this.height) {
            throw new RasterFormatException("subimage (y+height) is outside raster");
        }
        int[] argb = new int[w * h];
        int k = 0;
        for (int iRow = 0; iRow < h; ++iRow) {
            int dIndex = (iRow + y) * this.width + x;
            System.arraycopy(this.data, dIndex, argb, k, w);
            k += w;
        }
        return this.makeBufferedImage(argb, w, h, this.hasAlpha);
    }

    private BufferedImage makeBufferedImage(int[] argb, int w, int h, boolean useAlpha) {
        WritableRaster raster;
        DirectColorModel colorModel;
        DataBufferInt buffer = new DataBufferInt(argb, w * h);
        if (useAlpha) {
            colorModel = new DirectColorModel(32, 0xFF0000, 65280, 255, -16777216);
            raster = Raster.createPackedRaster(buffer, w, h, w, new int[]{0xFF0000, 65280, 255, -16777216}, null);
        } else {
            colorModel = new DirectColorModel(24, 0xFF0000, 65280, 255);
            raster = Raster.createPackedRaster(buffer, w, h, w, new int[]{0xFF0000, 65280, 255}, null);
        }
        return new BufferedImage(colorModel, raster, colorModel.isAlphaPremultiplied(), new Properties());
    }
}

