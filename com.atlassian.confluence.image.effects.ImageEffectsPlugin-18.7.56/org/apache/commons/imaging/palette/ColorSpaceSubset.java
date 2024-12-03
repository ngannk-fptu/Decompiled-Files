/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.palette;

import java.io.Serializable;
import java.util.Comparator;
import java.util.logging.Logger;

class ColorSpaceSubset {
    private static final Logger LOGGER = Logger.getLogger(ColorSpaceSubset.class.getName());
    final int[] mins;
    final int[] maxs;
    final int precision;
    final int precisionMask;
    final int total;
    int rgb;
    private int index;
    public static final RgbComparator RGB_COMPARATOR = new RgbComparator();

    ColorSpaceSubset(int total, int precision) {
        this.total = total;
        this.precision = precision;
        this.precisionMask = (1 << precision) - 1;
        this.mins = new int[3];
        this.maxs = new int[3];
        for (int i = 0; i < 3; ++i) {
            this.mins[i] = 0;
            this.maxs[i] = this.precisionMask;
        }
        this.rgb = -1;
    }

    ColorSpaceSubset(int total, int precision, int[] mins, int[] maxs) {
        this.total = total;
        this.precision = precision;
        this.mins = mins;
        this.maxs = maxs;
        this.precisionMask = (1 << precision) - 1;
        this.rgb = -1;
    }

    public final boolean contains(int red, int green, int blue) {
        if (this.mins[0] > (red >>= 8 - this.precision)) {
            return false;
        }
        if (this.maxs[0] < red) {
            return false;
        }
        if (this.mins[1] > (green >>= 8 - this.precision)) {
            return false;
        }
        if (this.maxs[1] < green) {
            return false;
        }
        if (this.mins[2] > (blue >>= 8 - this.precision)) {
            return false;
        }
        return this.maxs[2] >= blue;
    }

    public void dump(String prefix) {
        int rdiff = this.maxs[0] - this.mins[0] + 1;
        int gdiff = this.maxs[1] - this.mins[1] + 1;
        int bdiff = this.maxs[2] - this.mins[2] + 1;
        int colorArea = rdiff * gdiff * bdiff;
        LOGGER.fine(prefix + ": [" + Integer.toHexString(this.rgb) + "] total : " + this.total);
        LOGGER.fine("\trgb: " + Integer.toHexString(this.rgb) + ", red: " + Integer.toHexString(this.mins[0] << 8 - this.precision) + ", " + Integer.toHexString(this.maxs[0] << 8 - this.precision) + ", green: " + Integer.toHexString(this.mins[1] << 8 - this.precision) + ", " + Integer.toHexString(this.maxs[1] << 8 - this.precision) + ", blue: " + Integer.toHexString(this.mins[2] << 8 - this.precision) + ", " + Integer.toHexString(this.maxs[2] << 8 - this.precision));
        LOGGER.fine("\tred: " + this.mins[0] + ", " + this.maxs[0] + ", green: " + this.mins[1] + ", " + this.maxs[1] + ", blue: " + this.mins[2] + ", " + this.maxs[2]);
        LOGGER.fine("\trdiff: " + rdiff + ", gdiff: " + gdiff + ", bdiff: " + bdiff + ", colorArea: " + colorArea);
    }

    public void dumpJustRGB(String prefix) {
        LOGGER.fine("\trgb: " + Integer.toHexString(this.rgb) + ", red: " + Integer.toHexString(this.mins[0] << 8 - this.precision) + ", " + Integer.toHexString(this.maxs[0] << 8 - this.precision) + ", green: " + Integer.toHexString(this.mins[1] << 8 - this.precision) + ", " + Integer.toHexString(this.maxs[1] << 8 - this.precision) + ", blue: " + Integer.toHexString(this.mins[2] << 8 - this.precision) + ", " + Integer.toHexString(this.maxs[2] << 8 - this.precision));
    }

    public int getArea() {
        int rdiff = this.maxs[0] - this.mins[0] + 1;
        int gdiff = this.maxs[1] - this.mins[1] + 1;
        int bdiff = this.maxs[2] - this.mins[2] + 1;
        int colorArea = rdiff * gdiff * bdiff;
        return colorArea;
    }

    public void setAverageRGB(int[] table) {
        long redsum = 0L;
        long greensum = 0L;
        long bluesum = 0L;
        for (int red = this.mins[0]; red <= this.maxs[0]; ++red) {
            for (int green = this.mins[1]; green <= this.maxs[1]; ++green) {
                for (int blue = this.mins[2]; blue <= this.maxs[2]; ++blue) {
                    int idx = blue << 2 * this.precision | green << 1 * this.precision | red << 0 * this.precision;
                    int count = table[idx];
                    redsum += (long)(count * (red << 8 - this.precision));
                    greensum += (long)(count * (green << 8 - this.precision));
                    bluesum += (long)(count * (blue << 8 - this.precision));
                }
            }
        }
        this.rgb = (int)(((redsum /= (long)this.total) & 0xFFL) << 16 | ((greensum /= (long)this.total) & 0xFFL) << 8 | ((bluesum /= (long)this.total) & 0xFFL) << 0);
    }

    public final int getIndex() {
        return this.index;
    }

    public final void setIndex(int i) {
        this.index = i;
    }

    public static class RgbComparator
    implements Comparator<ColorSpaceSubset>,
    Serializable {
        private static final long serialVersionUID = 509214838111679029L;

        @Override
        public int compare(ColorSpaceSubset c1, ColorSpaceSubset c2) {
            return c1.rgb - c2.rgb;
        }
    }
}

