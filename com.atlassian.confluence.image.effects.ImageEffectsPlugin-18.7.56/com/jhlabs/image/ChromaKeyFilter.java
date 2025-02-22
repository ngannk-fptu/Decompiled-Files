/*
 * Decompiled with CFR 0.152.
 */
package com.jhlabs.image;

import com.jhlabs.image.AbstractBufferedImageOp;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class ChromaKeyFilter
extends AbstractBufferedImageOp {
    private float hTolerance = 0.0f;
    private float sTolerance = 0.0f;
    private float bTolerance = 0.0f;
    private int color;

    public void setHTolerance(float hTolerance) {
        this.hTolerance = hTolerance;
    }

    public float getHTolerance() {
        return this.hTolerance;
    }

    public void setSTolerance(float sTolerance) {
        this.sTolerance = sTolerance;
    }

    public float getSTolerance() {
        return this.sTolerance;
    }

    public void setBTolerance(float bTolerance) {
        this.bTolerance = bTolerance;
    }

    public float getBTolerance() {
        return this.bTolerance;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return this.color;
    }

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        int width = src.getWidth();
        int height = src.getHeight();
        int type = src.getType();
        WritableRaster srcRaster = src.getRaster();
        if (dst == null) {
            dst = this.createCompatibleDestImage(src, null);
        }
        WritableRaster dstRaster = dst.getRaster();
        float[] hsb1 = null;
        float[] hsb2 = null;
        int rgb2 = this.color;
        int r2 = rgb2 >> 16 & 0xFF;
        int g2 = rgb2 >> 8 & 0xFF;
        int b2 = rgb2 & 0xFF;
        hsb2 = Color.RGBtoHSB(r2, b2, g2, hsb2);
        int[] inPixels = null;
        for (int y = 0; y < height; ++y) {
            inPixels = this.getRGB(src, 0, y, width, 1, inPixels);
            for (int x = 0; x < width; ++x) {
                int rgb1 = inPixels[x];
                int r1 = rgb1 >> 16 & 0xFF;
                int b1 = rgb1 & 0xFF;
                int g1 = rgb1 >> 8 & 0xFF;
                inPixels[x] = Math.abs((hsb1 = Color.RGBtoHSB(r1, b1, g1, hsb1))[0] - hsb2[0]) < this.hTolerance && Math.abs(hsb1[1] - hsb2[1]) < this.sTolerance && Math.abs(hsb1[2] - hsb2[2]) < this.bTolerance ? rgb1 & 0xFFFFFF : rgb1;
            }
            this.setRGB(dst, 0, y, width, 1, inPixels);
        }
        return dst;
    }

    public String toString() {
        return "Keying/Chroma Key...";
    }
}

