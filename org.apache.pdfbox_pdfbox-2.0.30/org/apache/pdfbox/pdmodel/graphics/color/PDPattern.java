/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.graphics.color;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.color.PDSpecialColorSpace;
import org.apache.pdfbox.pdmodel.graphics.pattern.PDAbstractPattern;

public final class PDPattern
extends PDSpecialColorSpace {
    private static PDColor EMPTY_PATTERN = new PDColor(new float[0], null);
    private final PDResources resources;
    private PDColorSpace underlyingColorSpace;

    public PDPattern(PDResources resources) {
        this.resources = resources;
        this.array = new COSArray();
        this.array.add(COSName.PATTERN);
    }

    public PDPattern(PDResources resources, PDColorSpace colorSpace) {
        this.resources = resources;
        this.underlyingColorSpace = colorSpace;
        this.array = new COSArray();
        this.array.add(COSName.PATTERN);
        this.array.add(colorSpace);
    }

    @Override
    public String getName() {
        return COSName.PATTERN.getName();
    }

    @Override
    public int getNumberOfComponents() {
        throw new UnsupportedOperationException();
    }

    @Override
    public float[] getDefaultDecode(int bitsPerComponent) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PDColor getInitialColor() {
        return EMPTY_PATTERN;
    }

    @Override
    public float[] toRGB(float[] value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BufferedImage toRGBImage(WritableRaster raster) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public BufferedImage toRawImage(WritableRaster raster) throws IOException {
        throw new UnsupportedOperationException();
    }

    public PDAbstractPattern getPattern(PDColor color) throws IOException {
        PDAbstractPattern pattern = this.resources.getPattern(color.getPatternName());
        if (pattern == null) {
            throw new IOException("pattern " + color.getPatternName() + " was not found");
        }
        return pattern;
    }

    public PDColorSpace getUnderlyingColorSpace() {
        return this.underlyingColorSpace;
    }

    public String toString() {
        return "Pattern";
    }
}

