/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hemf.record.emfplus;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.hemf.record.emfplus.HemfPlusImage;
import org.apache.poi.sl.draw.BitmapImageRenderer;
import org.apache.poi.util.IOUtils;

public class HemfPlusGDIImageRenderer
extends BitmapImageRenderer {
    private int width;
    private int height;
    private int stride;
    private HemfPlusImage.EmfPlusPixelFormat pixelFormat;

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getStride() {
        return this.stride;
    }

    public void setStride(int stride) {
        this.stride = stride;
    }

    public HemfPlusImage.EmfPlusPixelFormat getPixelFormat() {
        return this.pixelFormat;
    }

    public void setPixelFormat(HemfPlusImage.EmfPlusPixelFormat pixelFormat) {
        this.pixelFormat = pixelFormat;
    }

    @Override
    public boolean canRender(String contentType) {
        return true;
    }

    @Override
    public void loadImage(InputStream data, String contentType) throws IOException {
        this.img = this.readGDIImage(IOUtils.toByteArray(data));
    }

    @Override
    public void loadImage(byte[] data, String contentType) {
        this.img = this.readGDIImage(data);
    }

    public BufferedImage readGDIImage(byte[] data) {
        int[] bOffs;
        int[] nBits;
        switch (this.pixelFormat) {
            case ARGB_32BPP: {
                nBits = new int[]{8, 8, 8, 8};
                bOffs = new int[]{2, 1, 0, 3};
                break;
            }
            case RGB_24BPP: {
                nBits = new int[]{8, 8, 8};
                bOffs = new int[]{2, 1, 0};
                break;
            }
            default: {
                throw new RuntimeException("not yet implemented");
            }
        }
        ColorSpace cs = ColorSpace.getInstance(1000);
        ComponentColorModel cm = new ComponentColorModel(cs, nBits, this.pixelFormat.isAlpha(), this.pixelFormat.isPreMultiplied(), 3, 0);
        PixelInterleavedSampleModel csm = new PixelInterleavedSampleModel(cm.getTransferType(), this.width, this.height, cm.getNumComponents(), this.stride, bOffs);
        DataBufferByte dbb = new DataBufferByte(data, data.length);
        WritableRaster raster = (WritableRaster)Raster.createRaster(csm, dbb, null);
        return new BufferedImage(cm, raster, cm.isAlphaPremultiplied(), null);
    }
}

