/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.palette;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.palette.ColorCount;
import org.apache.commons.imaging.palette.ColorGroupCut;

class ColorGroup {
    ColorGroupCut cut;
    int paletteIndex = -1;
    private final List<ColorCount> colorCounts;
    final boolean ignoreAlpha;
    int minRed = Integer.MAX_VALUE;
    int maxRed = Integer.MIN_VALUE;
    int minGreen = Integer.MAX_VALUE;
    int maxGreen = Integer.MIN_VALUE;
    int minBlue = Integer.MAX_VALUE;
    int maxBlue = Integer.MIN_VALUE;
    int minAlpha = Integer.MAX_VALUE;
    int maxAlpha = Integer.MIN_VALUE;
    final int alphaDiff;
    final int redDiff;
    final int greenDiff;
    final int blueDiff;
    final int maxDiff;
    final int diffTotal;
    final int totalPoints;

    ColorGroup(List<ColorCount> colorCounts, boolean ignoreAlpha) throws ImageWriteException {
        this.colorCounts = colorCounts;
        this.ignoreAlpha = ignoreAlpha;
        if (colorCounts.isEmpty()) {
            throw new ImageWriteException("empty color_group");
        }
        int total = 0;
        for (ColorCount color : colorCounts) {
            total += color.count;
            this.minAlpha = Math.min(this.minAlpha, color.alpha);
            this.maxAlpha = Math.max(this.maxAlpha, color.alpha);
            this.minRed = Math.min(this.minRed, color.red);
            this.maxRed = Math.max(this.maxRed, color.red);
            this.minGreen = Math.min(this.minGreen, color.green);
            this.maxGreen = Math.max(this.maxGreen, color.green);
            this.minBlue = Math.min(this.minBlue, color.blue);
            this.maxBlue = Math.max(this.maxBlue, color.blue);
        }
        this.totalPoints = total;
        this.alphaDiff = this.maxAlpha - this.minAlpha;
        this.redDiff = this.maxRed - this.minRed;
        this.greenDiff = this.maxGreen - this.minGreen;
        this.blueDiff = this.maxBlue - this.minBlue;
        this.maxDiff = Math.max(ignoreAlpha ? this.redDiff : Math.max(this.alphaDiff, this.redDiff), Math.max(this.greenDiff, this.blueDiff));
        this.diffTotal = (ignoreAlpha ? 0 : this.alphaDiff) + this.redDiff + this.greenDiff + this.blueDiff;
    }

    boolean contains(int argb) {
        int alpha = 0xFF & argb >> 24;
        int red = 0xFF & argb >> 16;
        int green = 0xFF & argb >> 8;
        int blue = 0xFF & argb >> 0;
        if (!(this.ignoreAlpha || alpha >= this.minAlpha && alpha <= this.maxAlpha)) {
            return false;
        }
        if (red < this.minRed || red > this.maxRed) {
            return false;
        }
        if (green < this.minGreen || green > this.maxGreen) {
            return false;
        }
        return blue >= this.minBlue && blue <= this.maxBlue;
    }

    int getMedianValue() {
        long countTotal = 0L;
        long alphaTotal = 0L;
        long redTotal = 0L;
        long greenTotal = 0L;
        long blueTotal = 0L;
        for (ColorCount color : this.colorCounts) {
            countTotal += (long)color.count;
            alphaTotal += (long)(color.count * color.alpha);
            redTotal += (long)(color.count * color.red);
            greenTotal += (long)(color.count * color.green);
            blueTotal += (long)(color.count * color.blue);
        }
        int alpha = this.ignoreAlpha ? 255 : (int)Math.round((double)alphaTotal / (double)countTotal);
        int red = (int)Math.round((double)redTotal / (double)countTotal);
        int green = (int)Math.round((double)greenTotal / (double)countTotal);
        int blue = (int)Math.round((double)blueTotal / (double)countTotal);
        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    List<ColorCount> getColorCounts() {
        return new ArrayList<ColorCount>(this.colorCounts);
    }

    public String toString() {
        return "{ColorGroup. minRed: " + Integer.toHexString(this.minRed) + ", maxRed: " + Integer.toHexString(this.maxRed) + ", minGreen: " + Integer.toHexString(this.minGreen) + ", maxGreen: " + Integer.toHexString(this.maxGreen) + ", minBlue: " + Integer.toHexString(this.minBlue) + ", maxBlue: " + Integer.toHexString(this.maxBlue) + ", minAlpha: " + Integer.toHexString(this.minAlpha) + ", maxAlpha: " + Integer.toHexString(this.maxAlpha) + ", maxDiff: " + Integer.toHexString(this.maxDiff) + ", diffTotal: " + this.diffTotal + "}";
    }
}

