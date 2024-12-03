/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.graphics.color;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceColorSpace;

public final class PDDeviceRGB
extends PDDeviceColorSpace {
    public static final PDDeviceRGB INSTANCE = new PDDeviceRGB();
    private final PDColor initialColor = new PDColor(new float[]{0.0f, 0.0f, 0.0f}, (PDColorSpace)this);

    private PDDeviceRGB() {
    }

    @Override
    public String getName() {
        return COSName.DEVICERGB.getName();
    }

    @Override
    public int getNumberOfComponents() {
        return 3;
    }

    @Override
    public float[] getDefaultDecode(int bitsPerComponent) {
        return new float[]{0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f};
    }

    @Override
    public PDColor getInitialColor() {
        return this.initialColor;
    }

    @Override
    public float[] toRGB(float[] value) {
        return value;
    }

    @Override
    public BufferedImage toRGBImage(WritableRaster raster) throws IOException {
        BufferedImage image = new BufferedImage(raster.getWidth(), raster.getHeight(), 1);
        image.setData(raster);
        return image;
    }

    @Override
    public BufferedImage toRawImage(WritableRaster raster) throws IOException {
        return null;
    }
}

