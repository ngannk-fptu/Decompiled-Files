/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff;

import org.apache.commons.imaging.formats.tiff.TiffRasterStatistics;

public class TiffRasterData {
    private final int width;
    private final int height;
    private final float[] data;

    public TiffRasterData(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Raster dimensions less than or equal to zero are not supported");
        }
        int nCells = width * height;
        this.data = new float[nCells];
        this.width = width;
        this.height = height;
    }

    public TiffRasterData(int width, int height, float[] data) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Raster dimensions less than or equal to zero are not supported");
        }
        if (data == null || data.length < width * height) {
            throw new IllegalArgumentException("Specified data does not contain sufficient elements");
        }
        this.width = width;
        this.height = height;
        this.data = data;
    }

    public void setValue(int x, int y, float value) {
        if (x < 0 || x >= this.width || y < 0 || y >= this.height) {
            throw new IllegalArgumentException("Coordinates out of range (" + x + ", " + y + ")");
        }
        this.data[y * this.width + x] = value;
    }

    public float getValue(int x, int y) {
        if (x < 0 || x >= this.width || y < 0 || y >= this.height) {
            throw new IllegalArgumentException("Coordinates out of range (" + x + ", " + y + ")");
        }
        return this.data[y * this.width + x];
    }

    public TiffRasterStatistics getSimpleStatistics() {
        return new TiffRasterStatistics(this, Float.NaN);
    }

    public TiffRasterStatistics getSimpleStatistics(float valueToExclude) {
        return new TiffRasterStatistics(this, valueToExclude);
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public float[] getData() {
        return this.data;
    }
}

