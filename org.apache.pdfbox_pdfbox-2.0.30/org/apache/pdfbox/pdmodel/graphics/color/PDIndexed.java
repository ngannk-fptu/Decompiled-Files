/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.graphics.color;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNull;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.color.PDICCBased;
import org.apache.pdfbox.pdmodel.graphics.color.PDSpecialColorSpace;

public final class PDIndexed
extends PDSpecialColorSpace {
    private final PDColor initialColor = new PDColor(new float[]{0.0f}, (PDColorSpace)this);
    private PDColorSpace baseColorSpace = null;
    private byte[] lookupData;
    private float[][] colorTable;
    private int actualMaxIndex;
    private int[][] rgbColorTable;

    public PDIndexed() {
        this.array = new COSArray();
        this.array.add(COSName.INDEXED);
        this.array.add(COSName.DEVICERGB);
        this.array.add(COSInteger.get(255L));
        this.array.add(COSNull.NULL);
    }

    public PDIndexed(COSArray indexedArray) throws IOException {
        this(indexedArray, null);
    }

    public PDIndexed(COSArray indexedArray, PDResources resources) throws IOException {
        this.array = indexedArray;
        this.baseColorSpace = PDColorSpace.create(this.array.get(1), resources);
        this.readColorTable();
        this.initRgbColorTable();
    }

    @Override
    public String getName() {
        return COSName.INDEXED.getName();
    }

    @Override
    public int getNumberOfComponents() {
        return 1;
    }

    @Override
    public float[] getDefaultDecode(int bitsPerComponent) {
        return new float[]{0.0f, (float)Math.pow(2.0, bitsPerComponent) - 1.0f};
    }

    @Override
    public PDColor getInitialColor() {
        return this.initialColor;
    }

    private void initRgbColorTable() throws IOException {
        WritableRaster baseRaster;
        int numBaseComponents = this.baseColorSpace.getNumberOfComponents();
        try {
            baseRaster = Raster.createBandedRaster(0, this.actualMaxIndex + 1, 1, numBaseComponents, new Point(0, 0));
        }
        catch (IllegalArgumentException ex) {
            throw new IOException(ex);
        }
        int[] base = new int[numBaseComponents];
        int n = this.actualMaxIndex;
        for (int i = 0; i <= n; ++i) {
            for (int c = 0; c < numBaseComponents; ++c) {
                base[c] = (int)(this.colorTable[i][c] * 255.0f);
            }
            baseRaster.setPixel(i, 0, base);
        }
        BufferedImage rgbImage = this.baseColorSpace.toRGBImage(baseRaster);
        WritableRaster rgbRaster = rgbImage.getRaster();
        this.rgbColorTable = new int[this.actualMaxIndex + 1][3];
        int[] nil = null;
        int n2 = this.actualMaxIndex;
        for (int i = 0; i <= n2; ++i) {
            this.rgbColorTable[i] = rgbRaster.getPixel(i, 0, nil);
        }
    }

    @Override
    public float[] toRGB(float[] value) {
        if (value.length != 1) {
            throw new IllegalArgumentException("Indexed color spaces must have one color value");
        }
        int index = Math.round(value[0]);
        index = Math.max(index, 0);
        index = Math.min(index, this.actualMaxIndex);
        int[] rgb = this.rgbColorTable[index];
        return new float[]{(float)rgb[0] / 255.0f, (float)rgb[1] / 255.0f, (float)rgb[2] / 255.0f};
    }

    @Override
    public BufferedImage toRGBImage(WritableRaster raster) throws IOException {
        int width = raster.getWidth();
        int height = raster.getHeight();
        BufferedImage rgbImage = new BufferedImage(width, height, 1);
        WritableRaster rgbRaster = rgbImage.getRaster();
        int[] src = new int[1];
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                raster.getPixel(x, y, src);
                int index = Math.min(src[0], this.actualMaxIndex);
                rgbRaster.setPixel(x, y, this.rgbColorTable[index]);
            }
        }
        return rgbImage;
    }

    @Override
    public BufferedImage toRawImage(WritableRaster raster) {
        if (this.baseColorSpace instanceof PDICCBased && ((PDICCBased)this.baseColorSpace).isSRGB()) {
            byte[] r = new byte[this.colorTable.length];
            byte[] g = new byte[this.colorTable.length];
            byte[] b = new byte[this.colorTable.length];
            for (int i = 0; i < this.colorTable.length; ++i) {
                r[i] = (byte)((int)(this.colorTable[i][0] * 255.0f) & 0xFF);
                g[i] = (byte)((int)(this.colorTable[i][1] * 255.0f) & 0xFF);
                b[i] = (byte)((int)(this.colorTable[i][2] * 255.0f) & 0xFF);
            }
            IndexColorModel colorModel = new IndexColorModel(8, this.colorTable.length, r, g, b);
            return new BufferedImage(colorModel, raster, false, null);
        }
        return null;
    }

    public PDColorSpace getBaseColorSpace() {
        return this.baseColorSpace;
    }

    private int getHival() {
        return ((COSNumber)this.array.getObject(2)).intValue();
    }

    private void readLookupData() throws IOException {
        if (this.lookupData == null) {
            COSBase lookupTable = this.array.getObject(3);
            if (lookupTable instanceof COSString) {
                this.lookupData = ((COSString)lookupTable).getBytes();
            } else if (lookupTable instanceof COSStream) {
                this.lookupData = new PDStream((COSStream)lookupTable).toByteArray();
            } else if (lookupTable == null) {
                this.lookupData = new byte[0];
            } else {
                throw new IOException("Error: Unknown type for lookup table " + lookupTable);
            }
        }
    }

    private void readColorTable() throws IOException {
        this.readLookupData();
        int maxIndex = Math.min(this.getHival(), 255);
        int numComponents = this.baseColorSpace.getNumberOfComponents();
        if (this.lookupData.length / numComponents < maxIndex + 1) {
            maxIndex = this.lookupData.length / numComponents - 1;
        }
        this.actualMaxIndex = maxIndex;
        this.colorTable = new float[maxIndex + 1][numComponents];
        int offset = 0;
        for (int i = 0; i <= maxIndex; ++i) {
            for (int c = 0; c < numComponents; ++c) {
                this.colorTable[i][c] = (float)(this.lookupData[offset] & 0xFF) / 255.0f;
                ++offset;
            }
        }
    }

    public void setBaseColorSpace(PDColorSpace base) {
        this.array.set(1, base.getCOSObject());
        this.baseColorSpace = base;
    }

    public void setHighValue(int high) {
        this.array.set(2, high);
    }

    public String toString() {
        return "Indexed{base:" + this.baseColorSpace + " hival:" + this.getHival() + " lookup:(" + this.colorTable.length + " entries)}";
    }
}

