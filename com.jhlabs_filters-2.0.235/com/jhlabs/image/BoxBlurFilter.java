/*
 * Decompiled with CFR 0.152.
 */
package com.jhlabs.image;

import com.jhlabs.image.AbstractBufferedImageOp;
import com.jhlabs.image.ImageMath;
import java.awt.image.BufferedImage;

public class BoxBlurFilter
extends AbstractBufferedImageOp {
    private int hRadius;
    private int vRadius;
    private int iterations = 1;

    public BoxBlurFilter() {
    }

    public BoxBlurFilter(int hRadius, int vRadius, int iterations) {
        this.hRadius = hRadius;
        this.vRadius = vRadius;
        this.iterations = iterations;
    }

    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        int width = src.getWidth();
        int height = src.getHeight();
        if (dst == null) {
            dst = this.createCompatibleDestImage(src, null);
        }
        int[] inPixels = new int[width * height];
        int[] outPixels = new int[width * height];
        this.getRGB(src, 0, 0, width, height, inPixels);
        for (int i = 0; i < this.iterations; ++i) {
            BoxBlurFilter.blur(inPixels, outPixels, width, height, this.hRadius);
            BoxBlurFilter.blur(outPixels, inPixels, height, width, this.vRadius);
        }
        this.setRGB(dst, 0, 0, width, height, inPixels);
        return dst;
    }

    public static void blur(int[] in, int[] out, int width, int height, int radius) {
        int widthMinus1 = width - 1;
        int tableSize = 2 * radius + 1;
        int[] divide = new int[256 * tableSize];
        for (int i = 0; i < 256 * tableSize; ++i) {
            divide[i] = i / tableSize;
        }
        int inIndex = 0;
        for (int y = 0; y < height; ++y) {
            int outIndex = y;
            int ta = 0;
            int tr = 0;
            int tg = 0;
            int tb = 0;
            for (int i = -radius; i <= radius; ++i) {
                int rgb = in[inIndex + ImageMath.clamp(i, 0, width - 1)];
                ta += rgb >> 24 & 0xFF;
                tr += rgb >> 16 & 0xFF;
                tg += rgb >> 8 & 0xFF;
                tb += rgb & 0xFF;
            }
            for (int x = 0; x < width; ++x) {
                int i2;
                out[outIndex] = divide[ta] << 24 | divide[tr] << 16 | divide[tg] << 8 | divide[tb];
                int i1 = x + radius + 1;
                if (i1 > widthMinus1) {
                    i1 = widthMinus1;
                }
                if ((i2 = x - radius) < 0) {
                    i2 = 0;
                }
                int rgb1 = in[inIndex + i1];
                int rgb2 = in[inIndex + i2];
                ta += (rgb1 >> 24 & 0xFF) - (rgb2 >> 24 & 0xFF);
                tr += (rgb1 & 0xFF0000) - (rgb2 & 0xFF0000) >> 16;
                tg += (rgb1 & 0xFF00) - (rgb2 & 0xFF00) >> 8;
                tb += (rgb1 & 0xFF) - (rgb2 & 0xFF);
                outIndex += height;
            }
            inIndex += width;
        }
    }

    public void setHRadius(int hRadius) {
        this.hRadius = hRadius;
    }

    public int getHRadius() {
        return this.hRadius;
    }

    public void setVRadius(int vRadius) {
        this.vRadius = vRadius;
    }

    public int getVRadius() {
        return this.vRadius;
    }

    public void setRadius(int radius) {
        this.hRadius = this.vRadius = radius;
    }

    public int getRadius() {
        return this.hRadius;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    public int getIterations() {
        return this.iterations;
    }

    public String toString() {
        return "Blur/Box Blur...";
    }
}

