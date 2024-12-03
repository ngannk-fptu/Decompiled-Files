/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.graphics.color;

import java.awt.Point;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNull;
import org.apache.pdfbox.pdmodel.common.function.PDFunction;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.color.PDLab;
import org.apache.pdfbox.pdmodel.graphics.color.PDSpecialColorSpace;

public class PDSeparation
extends PDSpecialColorSpace {
    private final PDColor initialColor = new PDColor(new float[]{1.0f}, (PDColorSpace)this);
    private static final int COLORANT_NAMES = 1;
    private static final int ALTERNATE_CS = 2;
    private static final int TINT_TRANSFORM = 3;
    private PDColorSpace alternateColorSpace = null;
    private PDFunction tintTransform = null;
    private Map<Integer, float[]> toRGBMap = null;

    public PDSeparation() {
        this.array = new COSArray();
        this.array.add(COSName.SEPARATION);
        this.array.add(COSName.getPDFName(""));
        this.array.add(COSNull.NULL);
        this.array.add(COSNull.NULL);
    }

    public PDSeparation(COSArray separation) throws IOException {
        this.array = separation;
        this.alternateColorSpace = PDColorSpace.create(this.array.getObject(2));
        this.tintTransform = PDFunction.create(this.array.getObject(3));
        int numberOfOutputParameters = this.tintTransform.getNumberOfOutputParameters();
        if (numberOfOutputParameters > 0 && numberOfOutputParameters < this.alternateColorSpace.getNumberOfComponents()) {
            throw new IOException("The tint transform function has less output parameters (" + this.tintTransform.getNumberOfOutputParameters() + ") than the alternate colorspace " + this.alternateColorSpace + " (" + this.alternateColorSpace.getNumberOfComponents() + ")");
        }
    }

    @Override
    public String getName() {
        return COSName.SEPARATION.getName();
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
    public float[] toRGB(float[] value) throws IOException {
        int key;
        float[] retval;
        if (this.toRGBMap == null) {
            this.toRGBMap = new HashMap<Integer, float[]>();
        }
        if ((retval = this.toRGBMap.get(key = (int)(value[0] * 255.0f))) != null) {
            return retval;
        }
        float[] altColor = this.tintTransform.eval(value);
        retval = this.alternateColorSpace.toRGB(altColor);
        this.toRGBMap.put(key, retval);
        return retval;
    }

    @Override
    public BufferedImage toRGBImage(WritableRaster raster) throws IOException {
        if (this.alternateColorSpace instanceof PDLab) {
            return this.toRGBImage2(raster);
        }
        int numAltComponents = this.alternateColorSpace.getNumberOfComponents();
        WritableRaster altRaster = Raster.createBandedRaster(0, raster.getWidth(), raster.getHeight(), numAltComponents, new Point(0, 0));
        int width = raster.getWidth();
        int height = raster.getHeight();
        float[] samples = new float[1];
        HashMap<Integer, int[]> calculatedValues = new HashMap<Integer, int[]>();
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                raster.getPixel(x, y, samples);
                Integer hash = Float.floatToIntBits(samples[0]);
                int[] alt = (int[])calculatedValues.get(hash);
                if (alt == null) {
                    alt = new int[numAltComponents];
                    this.tintTransform(samples, alt);
                    calculatedValues.put(hash, alt);
                }
                altRaster.setPixel(x, y, alt);
            }
        }
        return this.alternateColorSpace.toRGBImage(altRaster);
    }

    private BufferedImage toRGBImage2(WritableRaster raster) throws IOException {
        int width = raster.getWidth();
        int height = raster.getHeight();
        BufferedImage rgbImage = new BufferedImage(width, height, 1);
        WritableRaster rgbRaster = rgbImage.getRaster();
        float[] samples = new float[1];
        HashMap<Integer, int[]> calculatedValues = new HashMap<Integer, int[]>();
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                raster.getPixel(x, y, samples);
                Integer hash = Float.floatToIntBits(samples[0]);
                int[] rgb = (int[])calculatedValues.get(hash);
                if (rgb == null) {
                    samples[0] = samples[0] / 255.0f;
                    float[] altColor = this.tintTransform.eval(samples);
                    float[] fltab = this.alternateColorSpace.toRGB(altColor);
                    rgb = new int[]{(int)(fltab[0] * 255.0f), (int)(fltab[1] * 255.0f), (int)(fltab[2] * 255.0f)};
                    calculatedValues.put(hash, rgb);
                }
                rgbRaster.setPixel(x, y, rgb);
            }
        }
        return rgbImage;
    }

    protected void tintTransform(float[] samples, int[] alt) throws IOException {
        samples[0] = samples[0] / 255.0f;
        float[] result = this.tintTransform.eval(samples);
        for (int s = 0; s < alt.length; ++s) {
            alt[s] = (int)(result[s] * 255.0f);
        }
    }

    @Override
    public BufferedImage toRawImage(WritableRaster raster) {
        return this.toRawImage(raster, ColorSpace.getInstance(1003));
    }

    public PDColorSpace getAlternateColorSpace() {
        return this.alternateColorSpace;
    }

    public String getColorantName() {
        COSName name = (COSName)this.array.getObject(1);
        return name.getName();
    }

    public void setColorantName(String name) {
        this.array.set(1, COSName.getPDFName(name));
    }

    public void setAlternateColorSpace(PDColorSpace colorSpace) {
        this.alternateColorSpace = colorSpace;
        COSBase space = null;
        if (colorSpace != null) {
            space = colorSpace.getCOSObject();
        }
        this.array.set(2, space);
    }

    public void setTintTransform(PDFunction tint) {
        this.tintTransform = tint;
        this.array.set(3, tint);
    }

    public String toString() {
        return this.getName() + "{\"" + this.getColorantName() + "\" " + this.alternateColorSpace.getName() + " " + this.tintTransform + "}";
    }
}

