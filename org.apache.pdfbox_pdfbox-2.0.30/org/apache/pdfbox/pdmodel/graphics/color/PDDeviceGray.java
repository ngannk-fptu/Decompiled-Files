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

public final class PDDeviceGray
extends PDDeviceColorSpace {
    public static final PDDeviceGray INSTANCE = new PDDeviceGray();
    private final PDColor initialColor = new PDColor(new float[]{0.0f}, (PDColorSpace)this);

    private PDDeviceGray() {
    }

    @Override
    public String getName() {
        return COSName.DEVICEGRAY.getName();
    }

    @Override
    public int getNumberOfComponents() {
        return 1;
    }

    @Override
    public float[] getDefaultDecode(int bitsPerComponent) {
        return new float[]{0.0f, 1.0f};
    }

    @Override
    public PDColor getInitialColor() {
        return this.initialColor;
    }

    @Override
    public float[] toRGB(float[] value) {
        return new float[]{value[0], value[0], value[0]};
    }

    @Override
    public BufferedImage toRawImage(WritableRaster raster) throws IOException {
        return null;
    }

    @Override
    public BufferedImage toRGBImage(WritableRaster raster) throws IOException {
        int width = raster.getWidth();
        int height = raster.getHeight();
        BufferedImage image = new BufferedImage(width, height, 1);
        int[] gray = new int[1];
        int[] rgb = new int[3];
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                raster.getPixel(x, y, gray);
                rgb[0] = gray[0];
                rgb[1] = gray[0];
                rgb[2] = gray[0];
                image.getRaster().setPixel(x, y, rgb);
            }
        }
        return image;
    }
}

