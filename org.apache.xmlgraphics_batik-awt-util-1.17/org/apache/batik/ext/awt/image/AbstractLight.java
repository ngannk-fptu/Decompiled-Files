/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image;

import java.awt.Color;
import org.apache.batik.ext.awt.image.Light;

public abstract class AbstractLight
implements Light {
    private double[] color;

    public static final double sRGBToLsRGB(double value) {
        if (value <= 0.003928) {
            return value / 12.92;
        }
        return Math.pow((value + 0.055) / 1.055, 2.4);
    }

    @Override
    public double[] getColor(boolean linear) {
        double[] ret = new double[3];
        if (linear) {
            ret[0] = AbstractLight.sRGBToLsRGB(this.color[0]);
            ret[1] = AbstractLight.sRGBToLsRGB(this.color[1]);
            ret[2] = AbstractLight.sRGBToLsRGB(this.color[2]);
        } else {
            ret[0] = this.color[0];
            ret[1] = this.color[1];
            ret[2] = this.color[2];
        }
        return ret;
    }

    public AbstractLight(Color color) {
        this.setColor(color);
    }

    @Override
    public void setColor(Color newColor) {
        this.color = new double[3];
        this.color[0] = (double)newColor.getRed() / 255.0;
        this.color[1] = (double)newColor.getGreen() / 255.0;
        this.color[2] = (double)newColor.getBlue() / 255.0;
    }

    @Override
    public boolean isConstant() {
        return true;
    }

    @Override
    public double[][][] getLightMap(double x, double y, double dx, double dy, int width, int height, double[][][] z) {
        double[][][] L = new double[height][][];
        for (int i = 0; i < height; ++i) {
            L[i] = this.getLightRow(x, y, dx, width, z[i], null);
            y += dy;
        }
        return L;
    }

    @Override
    public double[][] getLightRow(double x, double y, double dx, int width, double[][] z, double[][] lightRow) {
        double[][] ret = lightRow;
        if (ret == null) {
            ret = new double[width][3];
        }
        for (int i = 0; i < width; ++i) {
            this.getLight(x, y, z[i][3], ret[i]);
            x += dx;
        }
        return ret;
    }
}

