/*
 * Decompiled with CFR 0.152.
 */
package com.jhlabs.image;

import com.jhlabs.image.ConvolveFilter;
import com.jhlabs.image.GaussianFilter;
import com.jhlabs.image.PixelUtils;
import java.awt.image.BufferedImage;

public class GlowFilter
extends GaussianFilter {
    static final long serialVersionUID = 5377089073023183684L;
    private float amount = 0.5f;

    public GlowFilter() {
        this.radius = 2.0f;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public float getAmount() {
        return this.amount;
    }

    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        int width = src.getWidth();
        int height = src.getHeight();
        if (dst == null) {
            dst = this.createCompatibleDestImage(src, null);
        }
        int[] inPixels = new int[width * height];
        int[] outPixels = new int[width * height];
        src.getRGB(0, 0, width, height, inPixels, 0, width);
        if (this.radius > 0.0f) {
            GaussianFilter.convolveAndTranspose(this.kernel, inPixels, outPixels, width, height, this.alpha, ConvolveFilter.CLAMP_EDGES);
            GaussianFilter.convolveAndTranspose(this.kernel, outPixels, inPixels, height, width, this.alpha, ConvolveFilter.CLAMP_EDGES);
        }
        src.getRGB(0, 0, width, height, outPixels, 0, width);
        float a = 4.0f * this.amount;
        int index = 0;
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                int rgb1 = outPixels[index];
                int r1 = rgb1 >> 16 & 0xFF;
                int g1 = rgb1 >> 8 & 0xFF;
                int b1 = rgb1 & 0xFF;
                int rgb2 = inPixels[index];
                int r2 = rgb2 >> 16 & 0xFF;
                int g2 = rgb2 >> 8 & 0xFF;
                int b2 = rgb2 & 0xFF;
                r1 = PixelUtils.clamp((int)((float)r1 + a * (float)r2));
                g1 = PixelUtils.clamp((int)((float)g1 + a * (float)g2));
                b1 = PixelUtils.clamp((int)((float)b1 + a * (float)b2));
                inPixels[index] = rgb1 & 0xFF000000 | r1 << 16 | g1 << 8 | b1;
                ++index;
            }
        }
        dst.setRGB(0, 0, width, height, inPixels, 0, width);
        return dst;
    }

    public String toString() {
        return "Blur/Glow...";
    }
}

