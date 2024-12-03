/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image;

import org.apache.batik.ext.awt.image.TransferFunction;

public class TableTransfer
implements TransferFunction {
    public byte[] lutData;
    public int[] tableValues;
    private int n;

    public TableTransfer(int[] tableValues) {
        this.tableValues = tableValues;
        this.n = tableValues.length;
    }

    private void buildLutData() {
        this.lutData = new byte[256];
        for (int j = 0; j <= 255; ++j) {
            float fi = (float)(j * (this.n - 1)) / 255.0f;
            int ffi = (int)Math.floor(fi);
            int cfi = ffi + 1 > this.n - 1 ? this.n - 1 : ffi + 1;
            float r = fi - (float)ffi;
            this.lutData[j] = (byte)((int)((float)this.tableValues[ffi] + r * (float)(this.tableValues[cfi] - this.tableValues[ffi])) & 0xFF);
        }
    }

    @Override
    public byte[] getLookupTable() {
        this.buildLutData();
        return this.lutData;
    }
}

