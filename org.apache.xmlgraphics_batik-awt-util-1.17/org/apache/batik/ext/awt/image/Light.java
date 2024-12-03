/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image;

import java.awt.Color;

public interface Light {
    public boolean isConstant();

    public void getLight(double var1, double var3, double var5, double[] var7);

    public double[][][] getLightMap(double var1, double var3, double var5, double var7, int var9, int var10, double[][][] var11);

    public double[][] getLightRow(double var1, double var3, double var5, int var7, double[][] var8, double[][] var9);

    public double[] getColor(boolean var1);

    public void setColor(Color var1);
}

