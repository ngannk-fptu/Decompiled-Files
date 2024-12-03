/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff;

import org.apache.commons.imaging.formats.tiff.TiffRasterData;

public class TiffRasterStatistics {
    private final int nSample;
    private final int nNull;
    private final float minValue;
    private final float maxValue;
    private final float meanValue;
    private final float excludedValue;

    TiffRasterStatistics(TiffRasterData raster, float excludedValue) {
        this.excludedValue = excludedValue;
        float vMin = Float.POSITIVE_INFINITY;
        float vMax = Float.NEGATIVE_INFINITY;
        double vSum = 0.0;
        int nS = 0;
        int nN = 0;
        float[] data = raster.getData();
        for (int i = 0; i < data.length; ++i) {
            float test = data[i];
            if (Float.isNaN(test)) {
                ++nN;
                continue;
            }
            if (test == excludedValue) continue;
            ++nS;
            vSum += (double)test;
            if (test < vMin) {
                vMin = test;
            }
            if (!(test > vMax)) continue;
            vMax = test;
        }
        this.minValue = vMin;
        this.maxValue = vMax;
        this.nSample = nS;
        this.nNull = nN;
        this.meanValue = this.nSample == 0 ? 0.0f : (float)(vSum / (double)this.nSample);
    }

    public int getCountOfSamples() {
        return this.nSample;
    }

    public int getCountOfNulls() {
        return this.nNull;
    }

    public float getMinValue() {
        return this.minValue;
    }

    public float getMaxValue() {
        return this.maxValue;
    }

    public float getMeanValue() {
        return this.meanValue;
    }

    public boolean isAnExcludedValueSet() {
        return !Float.isNaN(this.excludedValue);
    }

    public float getExcludedValue() {
        return this.excludedValue;
    }
}

