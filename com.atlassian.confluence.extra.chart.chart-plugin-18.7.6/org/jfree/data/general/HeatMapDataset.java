/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data.general;

public interface HeatMapDataset {
    public int getXSampleCount();

    public int getYSampleCount();

    public double getMinimumXValue();

    public double getMaximumXValue();

    public double getMinimumYValue();

    public double getMaximumYValue();

    public double getXValue(int var1);

    public double getYValue(int var1);

    public double getZValue(int var1, int var2);

    public Number getZ(int var1, int var2);
}

