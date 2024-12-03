/*
 * Decompiled with CFR 0.152.
 */
package com.jhlabs.image;

import com.jhlabs.image.OctTreeQuantizer;
import com.jhlabs.image.PixelUtils;
import com.jhlabs.image.WholeImageFilter;
import java.awt.Rectangle;
import java.io.Serializable;

public class QuantizeFilter
extends WholeImageFilter
implements Serializable {
    protected static final int[] matrix = new int[]{0, 0, 0, 0, 0, 7, 3, 5, 1};
    private int sum = 16;
    private boolean dither;
    private int numColors = 256;
    private boolean serpentine = true;

    public void setNumColors(int numColors) {
        this.numColors = Math.min(Math.max(numColors, 8), 256);
    }

    public int getNumColors() {
        return this.numColors;
    }

    public void setDither(boolean dither) {
        this.dither = dither;
    }

    public boolean getDither() {
        return this.dither;
    }

    public void setSerpentine(boolean serpentine) {
        this.serpentine = serpentine;
    }

    public boolean getSerpentine() {
        return this.serpentine;
    }

    public void quantize(int[] inPixels, int[] outPixels, int width, int height, int numColors, boolean dither, boolean serpentine) {
        int count = width * height;
        OctTreeQuantizer quantizer = new OctTreeQuantizer();
        quantizer.setup(numColors);
        quantizer.addPixels(inPixels, 0, count);
        int[] table = quantizer.buildColorTable();
        if (!dither) {
            for (int i = 0; i < count; ++i) {
                outPixels[i] = table[quantizer.getIndexForColor(inPixels[i])];
            }
        } else {
            int index = 0;
            for (int y = 0; y < height; ++y) {
                int direction;
                boolean reverse;
                boolean bl = reverse = serpentine && (y & 1) == 1;
                if (reverse) {
                    index = y * width + width - 1;
                    direction = -1;
                } else {
                    index = y * width;
                    direction = 1;
                }
                for (int x = 0; x < width; ++x) {
                    int rgb2;
                    int rgb1 = inPixels[index];
                    outPixels[index] = rgb2 = table[quantizer.getIndexForColor(rgb1)];
                    int r1 = rgb1 >> 16 & 0xFF;
                    int g1 = rgb1 >> 8 & 0xFF;
                    int b1 = rgb1 & 0xFF;
                    int r2 = rgb2 >> 16 & 0xFF;
                    int g2 = rgb2 >> 8 & 0xFF;
                    int b2 = rgb2 & 0xFF;
                    int er = r1 - r2;
                    int eg = g1 - g2;
                    int eb = b1 - b2;
                    for (int i = -1; i <= 1; ++i) {
                        int iy = i + y;
                        if (0 > iy || iy >= height) continue;
                        for (int j = -1; j <= 1; ++j) {
                            int w;
                            int jx = j + x;
                            if (0 > jx || jx >= width || (w = reverse ? matrix[(i + 1) * 3 - j + 1] : matrix[(i + 1) * 3 + j + 1]) == 0) continue;
                            int k = reverse ? index - j : index + j;
                            rgb1 = inPixels[k];
                            r1 = rgb1 >> 16 & 0xFF;
                            g1 = rgb1 >> 8 & 0xFF;
                            b1 = rgb1 & 0xFF;
                            inPixels[k] = PixelUtils.clamp(r1 += er * w / this.sum) << 16 | PixelUtils.clamp(g1 += eg * w / this.sum) << 8 | PixelUtils.clamp(b1 += eb * w / this.sum);
                        }
                    }
                    index += direction;
                }
            }
        }
    }

    protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
        int[] outPixels = new int[width * height];
        this.quantize(inPixels, outPixels, width, height, this.numColors, this.dither, this.serpentine);
        return outPixels;
    }

    public String toString() {
        return "Colors/Quantize...";
    }
}

