/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image;

import org.apache.batik.ext.awt.image.TransferFunction;

public class LinearTransfer
implements TransferFunction {
    public byte[] lutData;
    public float slope;
    public float intercept;

    public LinearTransfer(float slope, float intercept) {
        this.slope = slope;
        this.intercept = intercept;
    }

    private void buildLutData() {
        this.lutData = new byte[256];
        float scaledInt = this.intercept * 255.0f + 0.5f;
        for (int j = 0; j <= 255; ++j) {
            int value = (int)(this.slope * (float)j + scaledInt);
            if (value < 0) {
                value = 0;
            } else if (value > 255) {
                value = 255;
            }
            this.lutData[j] = (byte)(0xFF & value);
        }
    }

    @Override
    public byte[] getLookupTable() {
        this.buildLutData();
        return this.lutData;
    }
}

