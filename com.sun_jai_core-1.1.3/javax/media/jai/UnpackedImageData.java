/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.awt.Rectangle;
import java.awt.image.Raster;

public final class UnpackedImageData {
    public final Raster raster;
    public final Rectangle rect;
    public final int type;
    public final Object data;
    public final int pixelStride;
    public final int lineStride;
    public final int[] bandOffsets;
    public final boolean convertToDest;

    public UnpackedImageData(Raster raster, Rectangle rect, int type, Object data, int pixelStride, int lineStride, int[] bandOffsets, boolean convertToDest) {
        this.raster = raster;
        this.rect = rect;
        this.type = type;
        this.data = data;
        this.pixelStride = pixelStride;
        this.lineStride = lineStride;
        this.bandOffsets = bandOffsets;
        this.convertToDest = convertToDest;
    }

    public byte[][] getByteData() {
        return this.type == 0 ? (byte[][])this.data : (byte[][])null;
    }

    public byte[] getByteData(int b) {
        byte[][] d = this.getByteData();
        return d == null ? null : d[b];
    }

    public short[][] getShortData() {
        return this.type == 1 || this.type == 2 ? (short[][])this.data : (short[][])null;
    }

    public short[] getShortData(int b) {
        short[][] d = this.getShortData();
        return d == null ? null : d[b];
    }

    public int[][] getIntData() {
        return this.type == 3 ? (int[][])this.data : (int[][])null;
    }

    public int[] getIntData(int b) {
        int[][] d = this.getIntData();
        return d == null ? null : d[b];
    }

    public float[][] getFloatData() {
        return this.type == 4 ? (float[][])this.data : (float[][])null;
    }

    public float[] getFloatData(int b) {
        float[][] d = this.getFloatData();
        return d == null ? null : d[b];
    }

    public double[][] getDoubleData() {
        return this.type == 5 ? (double[][])this.data : (double[][])null;
    }

    public double[] getDoubleData(int b) {
        double[][] d = this.getDoubleData();
        return d == null ? null : d[b];
    }

    public int getOffset(int b) {
        return this.bandOffsets[b];
    }

    public int getMinOffset() {
        int min = this.bandOffsets[0];
        for (int i = 1; i < this.bandOffsets.length; ++i) {
            min = Math.min(min, this.bandOffsets[i]);
        }
        return min;
    }

    public int getMaxOffset() {
        int max = this.bandOffsets[0];
        for (int i = 1; i < this.bandOffsets.length; ++i) {
            max = Math.max(max, this.bandOffsets[i]);
        }
        return max;
    }
}

