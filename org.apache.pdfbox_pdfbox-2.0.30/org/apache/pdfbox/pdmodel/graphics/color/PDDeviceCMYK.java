/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.graphics.color;

import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceColorSpace;

public class PDDeviceCMYK
extends PDDeviceColorSpace {
    public static PDDeviceCMYK INSTANCE = new PDDeviceCMYK();
    private final PDColor initialColor = new PDColor(new float[]{0.0f, 0.0f, 0.0f, 1.0f}, (PDColorSpace)this);
    private ICC_ColorSpace awtColorSpace;
    private volatile boolean initDone = false;
    private boolean usePureJavaCMYKConversion = false;

    protected PDDeviceCMYK() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void init() throws IOException {
        if (this.initDone) {
            return;
        }
        PDDeviceCMYK pDDeviceCMYK = this;
        synchronized (pDDeviceCMYK) {
            if (this.initDone) {
                return;
            }
            ICC_Profile iccProfile = this.getICCProfile();
            if (iccProfile == null) {
                throw new IOException("Default CMYK color profile could not be loaded");
            }
            this.awtColorSpace = new ICC_ColorSpace(iccProfile);
            this.awtColorSpace.toRGB(new float[]{0.0f, 0.0f, 0.0f, 0.0f});
            this.usePureJavaCMYKConversion = System.getProperty("org.apache.pdfbox.rendering.UsePureJavaCMYKConversion") != null;
            this.initDone = true;
        }
    }

    protected ICC_Profile getICCProfile() throws IOException {
        String name = "/org/apache/pdfbox/resources/icc/ISOcoated_v2_300_bas.icc";
        BufferedInputStream is = new BufferedInputStream(PDDeviceCMYK.class.getResourceAsStream(name));
        ICC_Profile iccProfile = ICC_Profile.getInstance(is);
        ((InputStream)is).close();
        return iccProfile;
    }

    @Override
    public String getName() {
        return COSName.DEVICECMYK.getName();
    }

    @Override
    public int getNumberOfComponents() {
        return 4;
    }

    @Override
    public float[] getDefaultDecode(int bitsPerComponent) {
        return new float[]{0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f};
    }

    @Override
    public PDColor getInitialColor() {
        return this.initialColor;
    }

    @Override
    public float[] toRGB(float[] value) throws IOException {
        this.init();
        return this.awtColorSpace.toRGB(value);
    }

    @Override
    public BufferedImage toRawImage(WritableRaster raster) throws IOException {
        return null;
    }

    @Override
    public BufferedImage toRGBImage(WritableRaster raster) throws IOException {
        this.init();
        return this.toRGBImageAWT(raster, this.awtColorSpace);
    }

    @Override
    protected BufferedImage toRGBImageAWT(WritableRaster raster, ColorSpace colorSpace) {
        if (this.usePureJavaCMYKConversion) {
            BufferedImage dest = new BufferedImage(raster.getWidth(), raster.getHeight(), 1);
            ColorSpace destCS = dest.getColorModel().getColorSpace();
            WritableRaster destRaster = dest.getRaster();
            float[] srcValues = new float[4];
            float[] lastValues = new float[]{-1.0f, -1.0f, -1.0f, -1.0f};
            float[] destValues = new float[3];
            int startX = raster.getMinX();
            int startY = raster.getMinY();
            int endX = raster.getWidth() + startX;
            int endY = raster.getHeight() + startY;
            for (int x = startX; x < endX; ++x) {
                for (int y = startY; y < endY; ++y) {
                    raster.getPixel(x, y, srcValues);
                    if (!Arrays.equals(lastValues, srcValues)) {
                        lastValues[0] = srcValues[0];
                        srcValues[0] = srcValues[0] / 255.0f;
                        lastValues[1] = srcValues[1];
                        srcValues[1] = srcValues[1] / 255.0f;
                        lastValues[2] = srcValues[2];
                        srcValues[2] = srcValues[2] / 255.0f;
                        lastValues[3] = srcValues[3];
                        srcValues[3] = srcValues[3] / 255.0f;
                        destValues = destCS.fromCIEXYZ(colorSpace.toCIEXYZ(srcValues));
                        for (int k = 0; k < destValues.length; ++k) {
                            destValues[k] = destValues[k] * 255.0f;
                        }
                    }
                    destRaster.setPixel(x, y, destValues);
                }
            }
            return dest;
        }
        return super.toRGBImageAWT(raster, colorSpace);
    }
}

