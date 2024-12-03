/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.graphics.color;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;

public final class PDJPXColorSpace
extends PDColorSpace {
    private final ColorSpace awtColorSpace;

    public PDJPXColorSpace(ColorSpace colorSpace) {
        this.awtColorSpace = colorSpace;
    }

    @Override
    public String getName() {
        return "JPX";
    }

    @Override
    public int getNumberOfComponents() {
        return this.awtColorSpace.getNumComponents();
    }

    @Override
    public float[] getDefaultDecode(int bitsPerComponent) {
        int n = this.getNumberOfComponents();
        float[] decode = new float[n * 2];
        for (int i = 0; i < n; ++i) {
            decode[i * 2] = this.awtColorSpace.getMinValue(i);
            decode[i * 2 + 1] = this.awtColorSpace.getMaxValue(i);
        }
        return decode;
    }

    @Override
    public PDColor getInitialColor() {
        throw new UnsupportedOperationException("JPX color spaces don't support drawing");
    }

    @Override
    public float[] toRGB(float[] value) {
        throw new UnsupportedOperationException("JPX color spaces don't support drawing");
    }

    @Override
    public BufferedImage toRGBImage(WritableRaster raster) throws IOException {
        return this.toRGBImageAWT(raster, this.awtColorSpace);
    }

    @Override
    public BufferedImage toRawImage(WritableRaster raster) {
        return this.toRawImage(raster, this.awtColorSpace);
    }

    @Override
    public COSBase getCOSObject() {
        throw new UnsupportedOperationException("JPX color spaces don't have COS objects");
    }
}

