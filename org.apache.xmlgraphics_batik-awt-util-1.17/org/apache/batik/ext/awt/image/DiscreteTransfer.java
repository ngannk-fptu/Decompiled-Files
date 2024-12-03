/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image;

import org.apache.batik.ext.awt.image.TransferFunction;

public class DiscreteTransfer
implements TransferFunction {
    public byte[] lutData;
    public int[] tableValues;
    private int n;

    public DiscreteTransfer(int[] tableValues) {
        this.tableValues = tableValues;
        this.n = tableValues.length;
    }

    private void buildLutData() {
        this.lutData = new byte[256];
        for (int j = 0; j <= 255; ++j) {
            int i = (int)Math.floor((float)(j * this.n) / 255.0f);
            if (i == this.n) {
                i = this.n - 1;
            }
            this.lutData[j] = (byte)(this.tableValues[i] & 0xFF);
        }
    }

    @Override
    public byte[] getLookupTable() {
        this.buildLutData();
        return this.lutData;
    }
}

